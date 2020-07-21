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
import com.rukspot.sample.restclient.PublisherClient;
import com.rukspot.sample.restclient.Utils;
import org.wso2.am.integration.clients.publisher.api.v1.dto.APIDTO;

import java.io.File;

public class WSDLPassThroughTestCase extends RestAPITestCase {

    public void run(TestCase testCase, String tenant) throws Exception {
        this.tenant = tenant;
        this.testCase = testCase;
        Configurations configs = ConfigurationService.getInstance().getConfigurations();
        PublisherClient publisherClient =
                new PublisherClient(Utils.getTeantUsername(testCase.getPublisher(), tenant), configs.getDefaultPass());
        String originalPayload = Utils.readFile("wsdl.json");
        File wsdl = Utils.getFile("UserProfileMgtService.wsdl");
        String payload = originalPayload.replaceAll("\\$prod_endpoint", testCase.getEndpoint());
        APIDTO apidto = new Gson().fromJson(payload, APIDTO.class);
        apidto.setVersion(testCase.getInitVersion());
        String id = System.getProperty(Constants.UNIQUE_ID);
        String apiName = testCase.getApiName() + "_" + id;
        APIDTO apiDtoResponse = publisherClient
                .createAndPublishSOAPAPI(apidto, apiName, Utils.getTeantUsername(testCase.getPublisher(), tenant), wsdl);
        apiTestCase(publisherClient, apiDtoResponse, testCase, tenant);
    }
}
