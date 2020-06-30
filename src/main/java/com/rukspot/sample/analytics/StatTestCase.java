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

import com.rukspot.sample.configuration.ConfigurationService;
import com.rukspot.sample.configuration.models.Configurations;
import com.rukspot.sample.configuration.models.TestCase;

public class StatTestCase {

    private TestCase testCase;
    private String tenant;
    Configurations configs;

    public StatTestCase(TestCase testCase, String tenant) {
        this.testCase = testCase;
        this.tenant = tenant;
        configs = ConfigurationService.getInstance().getConfigurations();
    }

    public void run() throws Exception {
        if("rest".equalsIgnoreCase(testCase.getApiType())) {
            new RestAPITestCase().run(testCase, tenant);
        }
        if("graphql".equalsIgnoreCase(testCase.getApiType())) {
            new GraphQLAPITestCase().run(testCase, tenant);
        }
    }
}
