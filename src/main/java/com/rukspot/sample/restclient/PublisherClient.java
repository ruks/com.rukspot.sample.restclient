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

import org.wso2.am.integration.clients.publisher.api.ApiClient;
import org.wso2.am.integration.clients.publisher.api.v1.ApIsApi;
import org.wso2.am.integration.clients.publisher.api.v1.ApiLifecycleApi;
import org.wso2.am.integration.clients.publisher.api.v1.dto.APIDTO;
import org.wso2.am.integration.clients.publisher.api.v1.dto.APIInfoDTO;
import org.wso2.am.integration.clients.publisher.api.v1.dto.APIListDTO;

public class PublisherClient {

    ApIsApi apIsApi;
    ApiLifecycleApi lifecycleApi;
    static Token token;
    String version = "v1.1";

    public PublisherClient(String user, String pass) throws Exception {
        DCRClient dcrClient = new DCRClient();
        dcrClient.createOauthApp(user, "publisher");

        token = new Token();
        String scopes = "apim:api_view apim:api_create apim:api_publish apim:api_delete";
        String accessToken = token.getNewToken(user, pass, dcrClient.getConsumerKey(), dcrClient.getConsumerSecret(), scopes);
        ApiClient apiPublisherClient = new ApiClient();
        apiPublisherClient.addDefaultHeader("Authorization", "Bearer " + accessToken);
        apiPublisherClient.setBasePath(Settings.BASE_URL + "/api/am/publisher/" + version);

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

    public void cleanAPis() throws Exception {
        APIListDTO listDTO = apIsApi.apisGet(1000, 0, null, null, null, null, null);
        for (APIInfoDTO infoDTO : listDTO.getList()) {
            apIsApi.apisApiIdDelete(infoDTO.getId(), null);
        }
    }
}
