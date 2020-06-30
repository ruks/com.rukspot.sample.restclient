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

package com.rukspot.sample.analytics;

import com.google.gson.Gson;
import com.rukspot.sample.configuration.ConfigurationService;
import com.rukspot.sample.configuration.models.Configurations;
import com.rukspot.sample.configuration.models.Operation;
import com.rukspot.sample.configuration.models.Subscription;
import com.rukspot.sample.configuration.models.TestCase;
import com.rukspot.sample.configuration.models.TestOperation;
import com.rukspot.sample.restclient.DevPortalClient;
import com.rukspot.sample.restclient.PublisherClient;
import com.rukspot.sample.restclient.Token;
import com.rukspot.sample.restclient.Utils;
import org.wso2.am.integration.clients.publisher.api.v1.dto.APIDTO;
import org.wso2.am.integration.clients.publisher.api.v1.dto.APIOperationsDTO;
import org.wso2.am.integration.clients.store.api.v1.dto.ApplicationDTO;
import org.wso2.am.integration.clients.store.api.v1.dto.ApplicationKeyDTO;

import java.util.ArrayList;
import java.util.List;

public class RestAPITestCase {

    Configurations configs = ConfigurationService.getInstance().getConfigurations();
    String tenant;
    TestCase testCase;

    public void run(TestCase testCase, String tenant) throws Exception {
        this.tenant = tenant;
        this.testCase = testCase;
        Configurations configs = ConfigurationService.getInstance().getConfigurations();
        PublisherClient publisherClient =
                new PublisherClient(Utils.getTeantUsername(testCase.getPublisher(), tenant), configs.getDefaultPass());
        String originalPayload = Utils.readFile("api.json");
        String payload = originalPayload.replaceAll("\\$prod_endpoint", testCase.getEndpoint());
        APIDTO apidto = new Gson().fromJson(payload, APIDTO.class);
        apidto.setVersion(testCase.getInitVersion());
        List<APIOperationsDTO> operations = new ArrayList<>();
        for (Operation operation : testCase.getSupportOperations()) {
            APIOperationsDTO dto = new APIOperationsDTO();
            dto.setVerb(operation.getMethod());
            dto.setTarget(operation.getTemplate());
            if(!operation.isSecurity()) {
                dto.setAuthType("None");
            }
            operations.add(dto);
        }
        apidto.setOperations(operations);

        String id = System.currentTimeMillis() + "";
        String apiName = testCase.getApiName() + "_" + id;
        APIDTO apiDtoResponse = publisherClient
                .createAndPublishAPI(apidto, apiName, Utils.getTeantUsername(testCase.getPublisher(), tenant));
        apiTestCase(publisherClient, apiDtoResponse, testCase, tenant);
    }

    public void apiTestCase(PublisherClient publisherClient, APIDTO apiDtoResponse, TestCase aCase, String tenant)
            throws Exception {
        Configurations configs = ConfigurationService.getInstance().getConfigurations();
        String id = System.currentTimeMillis() + "";
        for (Subscription subscription : aCase.getSubscriptions()) {
            String appName = subscription.getAppName() + "_" + id;
            DevPortalClient devPortalClient =
                    new DevPortalClient(Utils.getTeantUsername(subscription.getSubscriber(), tenant),
                            configs.getDefaultPass());
            ApplicationDTO appDto = devPortalClient.createApp(appName, subscription.getAppPolicy());
            ApplicationKeyDTO keyDTO = devPortalClient.createSubscribe(appDto, apiDtoResponse);

            runUsers(subscription.getUsers(), keyDTO, apiDtoResponse);

            for (String version : aCase.getVersion()) {
                APIDTO versionApiDto = publisherClient.copyAndPublishAPI(apiDtoResponse, version);
                runUsers(subscription.getUsers(), keyDTO, versionApiDto);
            }
        }
    }

    public void runUsers(List<String> users, ApplicationKeyDTO keyDTO, APIDTO apiDtoResponse) throws Exception {
        for (String user : users) {
            String accessToken = Token.getNewToken(Utils.getTeantUsername(user, tenant), configs.getDefaultPass(),
                    keyDTO.getConsumerKey(), keyDTO.getConsumerSecret(), null);
            for (TestOperation operation : this.testCase.getTestOperations()) {
                String url = configs.getGwEndpoint() + apiDtoResponse.getContext() + "/" + apiDtoResponse.getVersion();
                if(!operation.isSecurity()) {
                    accessToken = null;
                }
                DevPortalClient.invokeAPI(url, accessToken, operation);
            }
        }
    }
}
