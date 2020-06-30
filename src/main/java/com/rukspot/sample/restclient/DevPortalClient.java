/*
 *   Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package com.rukspot.sample.restclient;

import com.google.gson.Gson;
import com.rukspot.sample.Utils.CurlGenerator;
import com.rukspot.sample.configuration.ConfigurationService;
import com.rukspot.sample.configuration.models.Configurations;
import com.rukspot.sample.configuration.models.TestOperation;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.wso2.am.integration.clients.publisher.api.v1.dto.APIDTO;
import org.wso2.am.integration.clients.store.api.ApiClient;
import org.wso2.am.integration.clients.store.api.v1.ApIsApi;
import org.wso2.am.integration.clients.store.api.v1.ApplicationKeysApi;
import org.wso2.am.integration.clients.store.api.v1.ApplicationsApi;
import org.wso2.am.integration.clients.store.api.v1.SubscriptionsApi;
import org.wso2.am.integration.clients.store.api.v1.dto.APIInfoDTO;
import org.wso2.am.integration.clients.store.api.v1.dto.APIListDTO;
import org.wso2.am.integration.clients.store.api.v1.dto.ApplicationDTO;
import org.wso2.am.integration.clients.store.api.v1.dto.ApplicationInfoDTO;
import org.wso2.am.integration.clients.store.api.v1.dto.ApplicationKeyDTO;
import org.wso2.am.integration.clients.store.api.v1.dto.ApplicationKeyGenerateRequestDTO;
import org.wso2.am.integration.clients.store.api.v1.dto.ApplicationListDTO;
import org.wso2.am.integration.clients.store.api.v1.dto.SubscriptionDTO;

public class DevPortalClient {
    ApIsApi apIsApi;
    static Token token;
    ApplicationKeysApi keysApi;
    ApplicationsApi applicationsApi;
    SubscriptionsApi subscriptionsApi;
    static Gson gson = new Gson();
    String version = "v1.1";
    Configurations configs;

    public static void invokeAPI(String url, String token, int delay, String prefix) throws Exception {
        invokeAPI(url, token, delay, prefix, 5);
    }

    public static void invokeAPI(String url, String token, int delay, String prefix, String payload) throws Exception {
        invokeAPI(url, token, delay, prefix, 5, payload);
    }

    public static void invokeAPI(String url, String token, int delay, String prefix, int times) throws Exception {
        invokeAPI(url, token, delay, prefix, times, null);
    }

    public static void invokeAPI(String url, String token, int delay, String prefix, int times, String payload) throws Exception {
        int timeout = 10;
        RequestConfig config =
                RequestConfig.custom().setConnectTimeout(timeout * 1000)
                        .setConnectionRequestTimeout(timeout * 1000)
                        .setSocketTimeout(timeout * 1000).build();
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

        HttpUriRequest request;
        if(payload == null) {
            request = new HttpGet(url);
        } else {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(payload));
            httpPost.addHeader("Content-Type","application/json");
            request=httpPost;
        }
        if (token != null) {
            request.addHeader("Authorization", "Bearer " + token);
        }
        request.addHeader("X-Forwarded-For", "1.1.177.1");

        for (int i = 0; i < times; i++) {
            HttpResponse response = null;
            try {
                response = httpClient.execute(request);
                String body = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
//                System.out.println(body);
                System.out.println(prefix + " : " + response.getStatusLine().getStatusCode());
                CurlGenerator.saveAsCurl(request, delay);
                Thread.sleep(delay * 1000);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(prefix+ " : " + "API call failed.");
            } finally {
                if(response != null) {
                    EntityUtils.consumeQuietly(response.getEntity());
                }
            }
        }

    }

    public DevPortalClient(String user, String pass) throws Exception {
        ConfigurationService service = ConfigurationService.getInstance();
        configs = service.getConfigurations();

        DCRClient dcrClient = new DCRClient();
        dcrClient.createOauthApp(user, "devportal");

        token = new Token();
        String accessToken =
                token.getNewToken(user, pass, dcrClient.getConsumerKey(), dcrClient.getConsumerSecret(), configs.getAppDeveloperScopes());

        ApiClient apiStoreClient = new ApiClient();
        apiStoreClient.addDefaultHeader("Authorization", "Bearer " + accessToken);
        apiStoreClient.setBasePath(configs.getDevPortalEndpoint());

        keysApi = new ApplicationKeysApi(apiStoreClient);
        applicationsApi = new ApplicationsApi(apiStoreClient);
        subscriptionsApi = new SubscriptionsApi(apiStoreClient);
        apIsApi = new ApIsApi(apiStoreClient);
    }

    public ApplicationDTO createApp(String appName, String policy)
            throws Exception {
        String payload = Utils.readFile("newApp.json");
        ApplicationDTO dto = gson.fromJson(payload, ApplicationDTO.class);
        dto.setThrottlingPolicy(policy);
        dto.setName(appName);
        dto = applicationsApi.applicationsPost(dto);
        return dto;
    }

    public ApplicationKeyDTO createSubscribe(ApplicationDTO dto, APIDTO apidto)
            throws Exception {
        String payload = Utils.readFile("keyGen.json");
        ApplicationKeyGenerateRequestDTO applicationKeyGenerateRequestDTO =
                gson.fromJson(payload, ApplicationKeyGenerateRequestDTO.class);

        int retry = 60;
        int limit = 10;
        while (retry > 0) {
            retry--;
            Thread.sleep(1000);
            //            System.out.println("Waiting to API ( " + apidto.getName() + ")available on dev portal " + retry);
            APIListDTO listDTO = apIsApi.apisGet(limit, 0, null, null, null);
            for (APIInfoDTO infoDTO : listDTO.getList()) {
                if (infoDTO.getId().equalsIgnoreCase(apidto.getId())) {
                    retry = -1;
                    break;
                }
            }
            limit = listDTO.getPagination().getTotal();
        }

        SubscriptionDTO subscriptionDTO = new SubscriptionDTO();
        subscriptionDTO.setApiId(apidto.getId());
        subscriptionDTO.setApplicationId(dto.getApplicationId());
        subscriptionDTO.setThrottlingPolicy("Unlimited");
        //        subscriptionDTO.setType(SubscriptionDTO.TypeEnum.API);
        subscriptionsApi.subscriptionsPost(subscriptionDTO, "");
        ApplicationKeyDTO keyDTO = keysApi.applicationsApplicationIdGenerateKeysPost(dto.getApplicationId(),
                applicationKeyGenerateRequestDTO);
        return keyDTO;

    }

    public static void invokeAPI(String url, String token, TestOperation operation) throws Exception {
        int timeout = 10;
        RequestConfig config =
                RequestConfig.custom().setConnectTimeout(timeout * 1000)
                        .setConnectionRequestTimeout(timeout * 1000)
                        .setSocketTimeout(timeout * 1000).build();
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

        HttpUriRequest request = null;
        String method = operation.getMethod();
        url += operation.getTemplate();
        if("get".equalsIgnoreCase(method)) {
            request = new HttpGet(url);
        } else if("post".equalsIgnoreCase(method)) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(operation.getPayload()));
            httpPost.addHeader("Content-Type","application/json");
            request=httpPost;
        }
        if (token != null) {
            request.addHeader("Authorization", "Bearer " + token);
        }
        request.addHeader("X-Forwarded-For", "1.1.177.1");
        request.addHeader("User-Agent", "Mozilla/5.0 (Android 4.4; Mobile; rv:41.0) Gecko/41.0 Firefox/41.0");

        for (int i = 0; i < operation.getTimes(); i++) {
            HttpResponse response = null;
            try {
                response = httpClient.execute(request);
                String body = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
                //                System.out.println(body);
                System.out.println(url + " : " + response.getStatusLine().getStatusCode());
                CurlGenerator.saveAsCurl(request, operation.getDelay());
                Thread.sleep(operation.getDelay() * 1000);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(url+ " : " + "API call failed.");
            } finally {
                if(response != null) {
                    EntityUtils.consumeQuietly(response.getEntity());
                }
            }
        }

    }

    public void cleanApplications() throws Exception {
        ApplicationListDTO listDTO = applicationsApi.applicationsGet(null, null, null, null, 1000, 0, null);
        for (ApplicationInfoDTO dto : listDTO.getList()) {
            applicationsApi.applicationsApplicationIdDelete(dto.getApplicationId(), null);
        }
    }
}
