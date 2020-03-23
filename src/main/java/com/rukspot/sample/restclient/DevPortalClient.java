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
import com.rukspot.sample.configuration.ConfigurationService;
import com.rukspot.sample.configuration.models.Configurations;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
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

import java.io.File;

public class DevPortalClient {
    ApIsApi apIsApi;
    static Token token;
    ApplicationKeysApi keysApi;
    ApplicationsApi applicationsApi;
    SubscriptionsApi subscriptionsApi;
    static Gson gson = new Gson();
    String version = "v1.1";
    Configurations configs;

    public void invokeAPI(String url, String token, int delay, String prefix) throws Exception {
        int timeout = 3;
        RequestConfig config =
                RequestConfig.custom().setConnectTimeout(timeout * 1000).setConnectionRequestTimeout(timeout * 1000)
                        .setSocketTimeout(timeout * 1000).build();
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

        HttpGet httpPost = new HttpGet(url);
        if (token != null) {
            httpPost.addHeader("Authorization", "Bearer " + token);
        }
        httpPost.addHeader("X-Forwarded-For", "1.1.177.1");

        for (int i = 0; i < 5; i++) {
            HttpResponse response;
            try {
                response = httpClient.execute(httpPost);
                String body = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
                //            System.out.println(body);
                System.out.println(prefix + " : " + response.getStatusLine().getStatusCode());
                Thread.sleep(delay * 1000);
            } catch (Exception e) {
                System.out.println(prefix+ " : " + "API call failed.");
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

        String relativePath = "data" + File.separator + "newApp.json";
        String payload =
                IOUtils.toString(PublisherClient.class.getClassLoader().getResourceAsStream(relativePath), "UTF-8");
        ApplicationDTO dto = gson.fromJson(payload, ApplicationDTO.class);
        dto.setThrottlingPolicy(policy);
        dto.setName(appName);
        dto = applicationsApi.applicationsPost(dto);
        return dto;
    }

    public ApplicationKeyDTO createSubscribe(ApplicationDTO dto, APIDTO apidto)
            throws Exception {
        String relativePath = "data" + File.separator + "keyGen.json";
        String payload = IOUtils.toString(PublisherClient.class.getClassLoader().getResourceAsStream(relativePath), "UTF-8");
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

    public void cleanApplications() throws Exception {
        ApplicationListDTO listDTO = applicationsApi.applicationsGet(null, null, null, null, 1000, 0, null);
        for (ApplicationInfoDTO dto : listDTO.getList()) {
            applicationsApi.applicationsApplicationIdDelete(dto.getApplicationId(), null);
        }
    }
}
