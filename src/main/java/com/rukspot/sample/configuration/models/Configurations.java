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
    public String dcrEndpoint = "https://localhost:9443/client-registration/v0.16/register";
    @Element(description = "hostname")
    public String publisherEndpoint = "https://localhost:9443/api/am/publisher/v1.1";
    @Element(description = "hostname")
    public String devPortalEndpoint = "https://localhost:9443/api/am/store/v1.1";
    @Element(description = "hostname")
    public String adminEndpoint = "https://localhost:9443/api/am/admin/v0.16";
    @Element(description = "hostname")
    public String tokenEndpoint = "https://localhost:9443/oauth2/token";
    @Element(description = "hostname")
    public String adminServiceBaseUrl = "https://localhost:9443";
    @Element(description = "hostname")
    public String gwEndpoint = "https://localhost:8243/";

    @Element(description = "hostname")
    public String amHome = "/Users/rukshan/wso2/apim/3.1.0/testing/wso2am-3.1.0";
    @Element(description = "hostname")
    public String appThrottlePolicy = "1PerMin";
    @Element(description = "hostname")
    public String defaultThrottlePolicy = "50PerMin";
    @Element(description = "hostname")
    public String apiEndpoint = "https://localhost:9443/am/sample/pizzashack/v1/api/";
    @Element(description = "hostname")
    public String faultyApiEndpoint = "https://localhost:9543/am/sample/pizzashack/v1/api/";

    @Element(description = "hostname")
    public String defaultPass = "admin";

    @Element(description = "tenants")
    public List<String> tenants = new ArrayList<>(Arrays.asList("carbon.super", "wso2.com", "apim.com"));

    @Element(description = "API developers")
    public List<String> apiDevelopers = new ArrayList<>(Arrays.asList("pub1", "pub2", "pub3"));

    @Element(description = "API developer roles")
    public List<String> apiDeveloperRoles = new ArrayList<>(Arrays.asList("Internal/publisher", "Internal/creator"));

    @Element(description = "APP developers")
    public List<String> appDevelopers = new ArrayList<>(Arrays.asList("sub1", "sub2", "sub3"));

    @Element(description = "APP developer Roles")
    public List<String> appDeveloperRoles = new ArrayList<>(Arrays.asList("Internal/subscriber"));

    @Element(description = "API users")
    public List<String> apiUsers = new ArrayList<>(Arrays.asList("user1", "user2", "user3"));

    @Element(description = "API user Roles")
    public List<String> apiUserRoles = new ArrayList<>(Arrays.asList("Internal/everyone"));

    @Element(description = "API developer scopes")
    public String apiDeveloperScopes = "apim:api_view apim:api_create apim:api_publish apim:api_delete";

    @Element(description = "APP developer scopes")
    public String appDeveloperScopes = "apim:subscribe apim:app_manage apim:sub_manage";

    @Element(description = "APP developer scopes")
    public String adminScopes = "apim:tier_manage apim:tier_view";

    @Element(description = "APP developer scopes")
    public List<UserTestCase> userTestCases = new ArrayList<>();

    public Configurations() {
        Subscriptions subscription1 = new Subscriptions();
        subscription1.setSubscriber("sub1");
        subscription1.setUsers(Arrays.asList("user1", "user2", "user3"));
        UserTestCase testCase = new UserTestCase();
        testCase.setPublisher("pub1");
        testCase.setSubscriptions(Arrays.asList(subscription1));
        userTestCases.add(testCase);
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

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public String getFaultyApiEndpoint() {
        return faultyApiEndpoint;
    }

    public String getDefaultPass() {
        return defaultPass;
    }

    public List<String> getTenants() {
        return tenants;
    }

    public List<String> getApiDevelopers() {
        return apiDevelopers;
    }

    public List<String> getApiDeveloperRoles() {
        return apiDeveloperRoles;
    }

    public List<String> getAppDevelopers() {
        return appDevelopers;
    }

    public List<String> getAppDeveloperRoles() {
        return appDeveloperRoles;
    }

    public List<String> getApiUsers() {
        return apiUsers;
    }

    public List<String> getApiUserRoles() {
        return apiUserRoles;
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

    public List<UserTestCase> getUserTestCases() {
        return userTestCases;
    }
}
