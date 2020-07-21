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
import com.rukspot.sample.Utils.Constants;
import com.rukspot.sample.configuration.ConfigurationService;
import com.rukspot.sample.configuration.models.Configurations;
import com.rukspot.sample.configuration.models.TestCase;
import com.rukspot.sample.configuration.models.TestOperation;
import com.rukspot.sample.restclient.DevPortalClient;
import com.rukspot.sample.restclient.PublisherClient;
import com.rukspot.sample.restclient.Token;
import com.rukspot.sample.restclient.Utils;
import com.rukspot.sample.websocket.WebSocketTestClient;
import org.wso2.am.integration.clients.publisher.api.v1.dto.APIDTO;
import org.wso2.am.integration.clients.store.api.v1.dto.ApplicationKeyDTO;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class WebsocketAPITestCase extends RestAPITestCase {

    public void run(TestCase testCase, String tenant) throws Exception {
        this.tenant = tenant;
        this.testCase = testCase;
        Configurations configs = ConfigurationService.getInstance().getConfigurations();
        PublisherClient publisherClient =
                new PublisherClient(Utils.getTeantUsername(testCase.getPublisher(), tenant), configs.getDefaultPass());
        String originalPayload = Utils.readFile("websocket.json");
        String payload = originalPayload.replaceAll("\\$prod_endpoint", testCase.getEndpoint());
        APIDTO apidto = new Gson().fromJson(payload, APIDTO.class);
        apidto.setVersion(testCase.getInitVersion());
        String id = System.getProperty(Constants.UNIQUE_ID);
        String apiName = testCase.getApiName() + "_" + id;
        APIDTO apiDtoResponse = publisherClient
                .createAndPublishAPI(apidto, apiName, Utils.getTeantUsername(testCase.getPublisher(), tenant));
        apiTestCase(publisherClient, apiDtoResponse, testCase, tenant);
    }

    public void runUsers(List<String> users, ApplicationKeyDTO keyDTO, APIDTO apiDtoResponse) throws Exception {
        for (String user : users) {
            String accessToken = Token.getNewToken(Utils.getTeantUsername(user, tenant), configs.getDefaultPass(),
                    keyDTO.getConsumerKey(), keyDTO.getConsumerSecret(), null);
            for (TestOperation operation : this.testCase.getTestOperations()) {
                String url = configs.getWsEndpoint() + apiDtoResponse.getContext() + "/" + apiDtoResponse.getVersion();
                if(!operation.isSecurity()) {
                    accessToken = null;
                }
                invokeAPI(url, accessToken, operation);
            }
        }
    }

    public void invokeAPI(String url, String accessToken, TestOperation operation) throws Exception {
        int latchCountDownInSecs = 30;
        CountDownLatch latch = new CountDownLatch(1);
        WebSocketTestClient webSocketTestClient = new WebSocketTestClient(url, latch);
        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("Authorization","Bearer " + accessToken);
        webSocketTestClient.setCustomHeaders(customHeaders);
        if(webSocketTestClient.handhshake()) {
            System.out.println("Web Socket Handshake successful");
        } else {
            System.out.println("Web Socket Handshake failed");
        }
        webSocketTestClient.sendText(operation.getPayload());
        latch.await(latchCountDownInSecs, TimeUnit.SECONDS);
        System.out.println(webSocketTestClient.getTextReceived());
        webSocketTestClient.shutDown();
    }
}
