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

import java.util.ArrayList;
import java.util.List;

public class Subscription {
    public Subscription() {
        appName = "sample";
        appPolicy = "1PerMin";
        subscriber = "admin";
        subscriber = "admin";
        users.add("admin");
    }

    @Element(description = "APP Name")
    private String appName;
    @Element(description = "APP Policy")
    private String appPolicy;
    @Element(description = "APP developers")
    private String subscriber;
    @Element(description = "APP users")
    private List<String> users = new ArrayList<>();

    public String getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(String subscriber) {
        this.subscriber = subscriber;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppPolicy() {
        return appPolicy;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setAppPolicy(String appPolicy) {
        this.appPolicy = appPolicy;
    }
}
