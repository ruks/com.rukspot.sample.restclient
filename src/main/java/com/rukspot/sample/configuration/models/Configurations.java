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

package com.rukspot.sample.configuration.models;

import org.wso2.carbon.config.annotation.Configuration;
import org.wso2.carbon.config.annotation.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration(namespace = "com.rukspot.restclient", description = "APIM Configuration Parameters")
public class Configurations {

    @Element(description = "hostname")
    private String dcrEndpoint = "https://localhost:9443/client-registration/v0.16/register";
    @Element(description = "hostname")
    private String publisherEndpoint = "https://localhost:9443/api/am/publisher/v1.1";
    @Element(description = "hostname")
    private String devPortalEndpoint = "https://localhost:9443/api/am/store/v1.1";
    @Element(description = "hostname")
    private String adminEndpoint = "https://localhost:9443/api/am/admin/v0.16";
    @Element(description = "hostname")
    private String tokenEndpoint = "https://localhost:9443/oauth2/token";
    @Element(description = "hostname")
    private String adminServiceBaseUrl = "https://localhost:9443";
    @Element(description = "hostname")
    private String gwEndpoint = "https://localhost:8243";

    @Element(description = "hostname")
    private String amHome = "/Users/rukshan/wso2/apim/3.1.0/testing/wso2am-3.1.0";
    @Element(description = "hostname")
    private String appThrottlePolicy = "1PerMin";
    @Element(description = "hostname")
    private String defaultThrottlePolicy = "50PerMin";

    @Element(description = "hostname")
    private String defaultPass = "admin";

    @Element(description = "API developer scopes")
    private String apiDeveloperScopes = "apim:api_view apim:api_create apim:api_publish apim:api_delete";

    @Element(description = "APP developer scopes")
    private String appDeveloperScopes = "apim:subscribe apim:app_manage apim:sub_manage";

    @Element(description = "APP developer scopes")
    private String adminScopes = "apim:tier_manage apim:tier_view";

    @Element(description = "APP developer scopes")
    private List<TestCase> testCases = new ArrayList<>();

    private TestConfig testConfigs;

    @Element(description = "Resource Path")
    private String resourcePath = "src/main/resources/data";

    public Configurations() {
//        Subscription subscription1 = new Subscription();
//        subscription1.setSubscriber("sub1");
//        subscription1.setUsers(Arrays.asList("user1", "user2", "user3"));
//        UserTestCase testCase = new UserTestCase();
//        testCase.setPublisher("pub1");
//        testCase.setSubscriptions(Arrays.asList(subscription1));
//        testCases.add(testCase);


        testConfigs = new TestConfig();
        testCases = Arrays.asList(new TestCase());

    }

    public String getDcrEndpoint() {
        return dcrEndpoint;
    }

    public String getPublisherEndpoint() {
        return publisherEndpoint;
    }

    public String getDevPortalEndpoint() {
        return devPortalEndpoint;
    }

    public String getAdminEndpoint() {
        return adminEndpoint;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public String getAdminServiceBaseUrl() {
        return adminServiceBaseUrl;
    }

    public String getGwEndpoint() {
        return gwEndpoint;
    }

    public String getAmHome() {
        return amHome;
    }

    public String getAppThrottlePolicy() {
        return appThrottlePolicy;
    }

    public String getDefaultThrottlePolicy() {
        return defaultThrottlePolicy;
    }

    public String getDefaultPass() {
        return defaultPass;
    }

    public String getApiDeveloperScopes() {
        return apiDeveloperScopes;
    }

    public String getAppDeveloperScopes() {
        return appDeveloperScopes;
    }

    public String getAdminScopes() {
        return adminScopes;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public TestConfig getTestConfigs() {
        return testConfigs;
    }
}
