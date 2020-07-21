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
import com.rukspot.sample.configuration.models.APIResource;
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
import org.wso2.am.integration.clients.publisher.api.v1.dto.APIProductDTO;
import org.wso2.am.integration.clients.publisher.api.v1.dto.ProductAPIDTO;
import org.wso2.am.integration.clients.store.api.v1.dto.ApplicationDTO;
import org.wso2.am.integration.clients.store.api.v1.dto.ApplicationKeyDTO;

import java.util.ArrayList;
import java.util.List;

public class ProductTestCase {

    Configurations configs = ConfigurationService.getInstance().getConfigurations();
    String tenant;
    TestCase testCase;

    public void run(TestCase testCase, String tenant) throws Exception {
        this.tenant = tenant;
        this.testCase = testCase;
        String id = System.getProperty(Constants.UNIQUE_ID);

        Configurations configs = ConfigurationService.getInstance().getConfigurations();
        PublisherClient publisherClient =
                new PublisherClient(Utils.getTeantUsername(testCase.getPublisher(), tenant), configs.getDefaultPass());
        String originalPayload = Utils.readFile("product.json");
        APIProductDTO productDTO = new Gson().fromJson(originalPayload, APIProductDTO.class);

        List<ProductAPIDTO> apis = new ArrayList<>();
        for (APIResource apiResource : testCase.getApiResources()) {
            ProductAPIDTO productAPIDTO = new ProductAPIDTO();
            APIDTO apiDto = publisherClient.getAPI(apiResource.getApiName() + "_" + id, apiResource.getVersion());
            productAPIDTO.setName(apiDto.getName());
            productAPIDTO.setApiId(apiDto.getId());
            if (apiDto != null) {
                for (APIOperationsDTO operationsDTO : apiDto.getOperations()) {
                    for (Operation operation : apiResource.getOperations()) {
                        if (operation.getTemplate().equalsIgnoreCase(operationsDTO.getTarget()) && operation.getMethod()
                                .equalsIgnoreCase(operationsDTO.getVerb())) {
                            productAPIDTO.addOperationsItem(operationsDTO);
                        }
                    }
                }
            }
            apis.add(productAPIDTO);
        }
        productDTO.setApis(apis);

        String apiName = testCase.getApiName() + "_" + id;
        APIProductDTO productDtoResponse = publisherClient
                .createAndPublishProduct(productDTO, apiName, Utils.getTeantUsername(testCase.getPublisher(), tenant));
        apiTestCase(publisherClient, productDtoResponse, testCase, tenant);
    }

    public void apiTestCase(PublisherClient publisherClient, APIProductDTO productDtoResponse, TestCase aCase, String tenant)
            throws Exception {
        Configurations configs = ConfigurationService.getInstance().getConfigurations();
        String id = System.getProperty(Constants.UNIQUE_ID);
        for (Subscription subscription : aCase.getSubscriptions()) {
            String appName = subscription.getAppName() + "_" + id;
            DevPortalClient devPortalClient =
                    new DevPortalClient(Utils.getTeantUsername(subscription.getSubscriber(), tenant),
                            configs.getDefaultPass());
            ApplicationDTO appDto = devPortalClient.createApp(appName, subscription.getAppPolicy());
            ApplicationKeyDTO keyDTO = devPortalClient.createSubscribe(appDto, productDtoResponse);

            runUsers(subscription.getUsers(), keyDTO, productDtoResponse);
        }
    }

    public void runUsers(List<String> users, ApplicationKeyDTO keyDTO, APIProductDTO productDtoResponse) throws Exception {
        for (String user : users) {
            String accessToken = Token.getNewToken(Utils.getTeantUsername(user, tenant), configs.getDefaultPass(),
                    keyDTO.getConsumerKey(), keyDTO.getConsumerSecret(), null);
            for (TestOperation operation : this.testCase.getTestOperations()) {
                String url = configs.getGwEndpoint() + productDtoResponse.getContext();
                if(!operation.isSecurity()) {
                    accessToken = null;
                }
                invokeAPI(url, accessToken, operation);
            }
        }
    }

    public void invokeAPI(String url, String accessToken, TestOperation operation) throws Exception {
        DevPortalClient.invokeAPI(url, accessToken, operation);
    }
}
