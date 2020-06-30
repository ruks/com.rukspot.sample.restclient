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
import java.util.Arrays;
import java.util.List;

public class TestCase {
    public TestCase() {
        apiName = "sample";
        apiType = "rest";
        initVersion = "1.0.0";
        version = new ArrayList<>();
        version.add("2.0.0");
        version.add("3.0.0");
        creator = "admin";
        publisher = "admin";

        subscriptions = new ArrayList<>();
        subscriptions.add(new Subscription());

        Operation getMenu = new Operation();
        getMenu.setMethod("GET");
        getMenu.setTemplate("/menu");
        supportOperations = Arrays.asList(getMenu);
        TestOperation testOperation = new TestOperation();
        testOperation.setMethod(getMenu.getMethod());
        testOperation.setTemplate(getMenu.getTemplate());
        testOperations = Arrays.asList(testOperation);
    }

    @Element(description = "apiName")
    private String apiName;
    @Element(description = "endpoint")
    private String endpoint;
    @Element(description = "api type")
    private String apiType;
    @Element(description = "initial Version")
    private String initVersion;
    @Element(description = "Additional Versions")
    private List<String> version;
    @Element(description = "API Creator")
    private String creator;
    @Element(description = "API Publisher")
    private String publisher;
    @Element(description = "API subscriber")
    private List<Subscription> subscriptions;
    @Element(description = "Support Operations")
    private List<Operation> supportOperations;
    @Element(description = "Test Operations")
    private List<TestOperation> testOperations;

    public String getApiName() {
        return apiName;
    }

    public String getInitVersion() {
        return initVersion;
    }

    public List<String> getVersion() {
        return version;
    }

    public String getCreator() {
        return creator;
    }

    public String getPublisher() {
        return publisher;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public String getApiType() {
        return apiType;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public void setApiType(String apiType) {
        this.apiType = apiType;
    }

    public void setInitVersion(String initVersion) {
        this.initVersion = initVersion;
    }

    public void setVersion(List<String> version) {
        this.version = version;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<Operation> getSupportOperations() {
        return supportOperations;
    }

    public void setSupportOperations(List<Operation> supportOperations) {
        this.supportOperations = supportOperations;
    }

    public List<TestOperation> getTestOperations() {
        return testOperations;
    }

    public void setTestOperations(List<TestOperation> testOperations) {
        this.testOperations = testOperations;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
