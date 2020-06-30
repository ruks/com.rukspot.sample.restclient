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

import org.wso2.carbon.config.annotation.Element;

import java.util.Arrays;
import java.util.List;

public class TestConfig {
    public TestConfig() {
        User user = new User();
        user.username = "admin";
        user.roles = Arrays.asList("admin");
        users = Arrays.asList(user);

        tenants = Arrays.asList("carbon.super", "wso2.com");

        throttlePolicy = Arrays.asList(new ThrottlePolicy());
    }

    @Element(description = "user")
    private List<User> users;

    @Element(description = "tenants")
    private List<String> tenants;

    @Element(description = "throttlePolicy")
    private List<ThrottlePolicy> throttlePolicy;

    public List<User> getUsers() {
        return users;
    }

    public List<String> getTenants() {
        return tenants;
    }

    public List<ThrottlePolicy> getThrottlePolicy() {
        return throttlePolicy;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setTenants(List<String> tenants) {
        this.tenants = tenants;
    }

    public void setThrottlePolicy(List<ThrottlePolicy> throttlePolicy) {
        this.throttlePolicy = throttlePolicy;
    }
}
