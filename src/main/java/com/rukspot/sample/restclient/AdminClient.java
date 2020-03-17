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

import org.wso2.am.integration.clients.admin.api.ApiClient;
import org.wso2.am.integration.clients.admin.api.ApiException;
import org.wso2.am.integration.clients.admin.api.v16.ApplicationPolicyCollectionApi;
import org.wso2.am.integration.clients.admin.api.v16.dto.ApplicationThrottlePolicyDTO;
import org.wso2.am.integration.clients.admin.api.v16.dto.ApplicationThrottlePolicyListDTO;
import org.wso2.am.integration.clients.admin.api.v16.dto.RequestCountLimitDTO;
import org.wso2.am.integration.clients.admin.api.v16.dto.ThrottleLimitDTO;

import java.util.List;

public class AdminClient {
    ApplicationPolicyCollectionApi policyCollectionApi;

    public AdminClient(String user, String pass) throws Exception {
        System.setProperty("javax.net.ssl.keyStore", Settings.AM_HOME + "/repository/resources/security/wso2carbon.jks");
        System.setProperty("javax.net.ssl.trustStore",
                Settings.AM_HOME + "/repository/resources/security/client-truststore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "wso2carbon");

        DCRClient dcrClient = new DCRClient();
        dcrClient.createOauthApp(user, "admin");

        Token token = new Token();
        String accessToken =
                token.getNewToken(user, pass, dcrClient.getConsumerKey(), dcrClient.getConsumerSecret(), "apim:tier_manage apim:tier_view");
        ApiClient apiAdminClient = new ApiClient();
        apiAdminClient.addDefaultHeader("Authorization", "Bearer " + accessToken);
        apiAdminClient.setBasePath(Settings.BASE_URL + "/api/am/admin/v0.16");

        policyCollectionApi = new ApplicationPolicyCollectionApi(apiAdminClient);
    }

    public void createApplicationPolicy(String name) throws ApiException {
        if(isApplicationPolicyExist(name)) {
            return;
        }
        ApplicationThrottlePolicyDTO dto = new ApplicationThrottlePolicyDTO();
        dto.setPolicyName(name);
        dto.setDisplayName(name);
        RequestCountLimitDTO limitDTO = new RequestCountLimitDTO();
        limitDTO.setTimeUnit("min");
        limitDTO.setUnitTime(1);
        limitDTO.setRequestCount(1L);
        limitDTO.setType(ThrottleLimitDTO.TypeEnum.REQUESTCOUNTLIMIT);
        dto.setDefaultLimit(limitDTO);

        policyCollectionApi.throttlingPoliciesApplicationPost(dto,"application/json");
    }

    public List<ApplicationThrottlePolicyDTO> getApplicationPolicy() throws ApiException {
        ApplicationThrottlePolicyListDTO
                applicationThrottlePolicyListDTO = policyCollectionApi.throttlingPoliciesApplicationGet(null, null, null);
        return applicationThrottlePolicyListDTO.getList();
    }

    public boolean isApplicationPolicyExist(String name) throws ApiException {
        List<ApplicationThrottlePolicyDTO> list = getApplicationPolicy();
        for (ApplicationThrottlePolicyDTO dto : list) {
            if(dto.getPolicyName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    public static void main(String[] args) throws Exception {
        AdminClient adminClient = new AdminClient("admin" ,"admin");
//        adminClient.createApplicationPolicy();
        System.out.println(adminClient.isApplicationPolicyExist("1PerMin"));
    }

}
