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
import org.wso2.am.integration.clients.publisher.api.ApiClient;
import org.wso2.am.integration.clients.publisher.api.v1.ApIsApi;
import org.wso2.am.integration.clients.publisher.api.v1.ApiLifecycleApi;
import org.wso2.am.integration.clients.publisher.api.v1.dto.APIDTO;
import org.wso2.am.integration.clients.publisher.api.v1.dto.APIInfoDTO;
import org.wso2.am.integration.clients.publisher.api.v1.dto.APIListDTO;

import java.io.File;

public class PublisherClient {

    ApIsApi apIsApi;
    ApiLifecycleApi lifecycleApi;
    static Token token;
    Configurations configs;

    public PublisherClient(String user, String pass) throws Exception {
        ConfigurationService service = ConfigurationService.getInstance();
        configs = service.getConfigurations();
        DCRClient dcrClient = new DCRClient();
        dcrClient.createOauthApp(user, "publisher");

        token = new Token();
        String accessToken = token.getNewToken(user, pass, dcrClient.getConsumerKey(), dcrClient.getConsumerSecret(), configs.getApiDeveloperScopes());
        ApiClient apiPublisherClient = new ApiClient();
        apiPublisherClient.addDefaultHeader("Authorization", "Bearer " + accessToken);
        apiPublisherClient.setBasePath(configs.getPublisherEndpoint());

        apIsApi = new ApIsApi(apiPublisherClient);
        lifecycleApi = new ApiLifecycleApi(apiPublisherClient);
    }

    public APIDTO createAndPublishAPI(APIDTO apidto, String newName, String provider) throws Exception {
        apidto.setName(newName);
        apidto.setContext(newName);
        apidto.setProvider(provider);
        apidto = apIsApi.apisPost(apidto, "v3");

        String lifeCycleCheckList = "Deprecate old versions after publishing the API:false,Requires re-subscription when publishing the API:false";
        lifecycleApi.apisChangeLifecyclePost("Publish", apidto.getId(), lifeCycleCheckList, null);
        return apidto;
    }

    public APIDTO createAndPublishAPI(APIDTO apidto, String newName, String provider, File schema) throws Exception {
        apidto.setName(newName);
        apidto.setContext(newName);
        apidto.setProvider(provider);
        try {
            apidto = apIsApi.apisImportGraphqlSchemaPost("GraphQL", schema, new Gson().toJson(apidto), null);
        }catch (Exception e) {
            e.printStackTrace();
        }
        String lifeCycleCheckList = "Deprecate old versions after publishing the API:false,Requires re-subscription when publishing the API:false";
        lifecycleApi.apisChangeLifecyclePost("Publish", apidto.getId(), lifeCycleCheckList, null);
        return apidto;
    }

    public APIDTO createAndPublishSOAPAPI(APIDTO apidto, String newName, String provider, File wsdl) throws Exception {
        apidto.setName(newName);
        apidto.setContext(newName);
        apidto.setProvider(provider);
        try {
            apidto = apIsApi.importWSDLDefinition(wsdl, null, new Gson().toJson(apidto), "SOAP");
        }catch (Exception e) {
            e.printStackTrace();
        }
        String lifeCycleCheckList = "Deprecate old versions after publishing the API:false,Requires re-subscription when publishing the API:false";
        lifecycleApi.apisChangeLifecyclePost("Publish", apidto.getId(), lifeCycleCheckList, null);
        return apidto;
    }

    public void cleanAPis() throws Exception {
        APIListDTO listDTO = apIsApi.apisGet(1000, 0, null, null, null, null, null);
        for (APIInfoDTO infoDTO : listDTO.getList()) {
            apIsApi.apisApiIdDelete(infoDTO.getId(), null);
        }
    }

    public APIDTO copyAndPublishAPI(APIDTO apidto, String version) throws Exception {
        try {
            apidto = apIsApi.apisCopyApiPost(version, apidto.getId(), false);
        }catch (Exception e) {
            e.printStackTrace();
        }
        String lifeCycleCheckList = "Deprecate old versions after publishing the API:false,Requires re-subscription when publishing the API:false";
        lifecycleApi.apisChangeLifecyclePost("Publish", apidto.getId(), "", null);
        return apidto;
    }
}
