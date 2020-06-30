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

import com.rukspot.sample.configuration.ConfigurationService;
import com.rukspot.sample.configuration.models.Configurations;
import com.rukspot.sample.configuration.models.ThrottlePolicy;
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
    Configurations configs;

    public AdminClient(String user, String pass) throws Exception {
        ConfigurationService service = ConfigurationService.getInstance();
        configs = service.getConfigurations();
        DCRClient dcrClient = new DCRClient();
        dcrClient.createOauthApp(user, "admin");

        Token token = new Token();
        String accessToken = token.getNewToken(user, pass, dcrClient.getConsumerKey(), dcrClient.getConsumerSecret(),
                configs.getAdminScopes());
        ApiClient apiAdminClient = new ApiClient();
        apiAdminClient.addDefaultHeader("Authorization", "Bearer " + accessToken);
        apiAdminClient.setBasePath(configs.getAdminEndpoint());
        policyCollectionApi = new ApplicationPolicyCollectionApi(apiAdminClient);
    }

    public void createApplicationPolicy(ThrottlePolicy policy) throws ApiException {
        if (isApplicationPolicyExist(policy.getName())) {
            return;
        }
        ApplicationThrottlePolicyDTO dto = new ApplicationThrottlePolicyDTO();
        dto.setPolicyName(policy.getName());
        dto.setDisplayName(policy.getName());
        RequestCountLimitDTO limitDTO = new RequestCountLimitDTO();
        limitDTO.setTimeUnit("min");
        limitDTO.setUnitTime(1);
        limitDTO.setRequestCount(Long.valueOf(policy.getCount()));
        limitDTO.setType(ThrottleLimitDTO.TypeEnum.REQUESTCOUNTLIMIT);
        dto.setDefaultLimit(limitDTO);

        policyCollectionApi.throttlingPoliciesApplicationPost(dto, "application/json");
    }

    public List<ApplicationThrottlePolicyDTO> getApplicationPolicy() throws ApiException {
        ApplicationThrottlePolicyListDTO applicationThrottlePolicyListDTO =
                policyCollectionApi.throttlingPoliciesApplicationGet(null, null, null);
        return applicationThrottlePolicyListDTO.getList();
    }

    public boolean isApplicationPolicyExist(String name) throws ApiException {
        List<ApplicationThrottlePolicyDTO> list = getApplicationPolicy();
        for (ApplicationThrottlePolicyDTO dto : list) {
            if (dto.getPolicyName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

}
