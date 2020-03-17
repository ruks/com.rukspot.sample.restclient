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

public class Settings {
    public static String BASE_URL = "https://localhost:9443";
    public static String AM_HOME = "/Users/rukshan/wso2/apim/3.1.0/testing/wso2am-3.1.0";
    public static String APP_POLICY_1MIN = "1PerMin";
    public static String APP_POLICY_50MIN = "50PerMin";
    public static String ENDPOINT = "https://localhost:9443/am/sample/pizzashack/v1/api/";
    public static String ERROR_ENDPOINT = "https://localhost:9543/am/sample/pizzashack/v1/api/";
    public static String GW_URL = "https://localhost:8243/";
}

enum Publisher {
    PUB1("pub1"),
    PUB2("pub2"),
    PUB3("pub3"),
    PUB4("pub4"),
    PUB5("pub5"),
    PUB6("pub6"),
    PUB7("pub7"),
    ;

    public String name;
    Publisher(String name) {
        this.name = name;
    }

    public String getUsername(String t) {
        if (t != null && !"carbon.super".equalsIgnoreCase(t)) {
            return name + "@" + t;
        }
        return name;
    }
}

enum Subscriber {
    SUB1("sub1"),
    SUB2("sub2"),
    SUB3("sub3"),
    ;

    public String name;
    Subscriber(String name) {
        this.name = name;
    }
    public String getUsername(String t) {
        if (t != null && !"carbon.super".equalsIgnoreCase(t)) {
            return name + "@" + t;
        }
        return name;
    }
}

enum Users {
    USER1("user1"),
    USER2("user2"),
    USER3("user3"),
    ;

    public String name;
    Users(String name) {
        this.name = name;
    }
    public String getUsername(String t) {
        if (t != null && !"carbon.super".equalsIgnoreCase(t)) {
            return name + "@" + t;
        }
        return name;
    }
}

enum Tenant {
    CARBON_SUPER("carbon.super"),
    WSO2_COM("wso2.com"),
    APIM_COM("apim.com"),
    ;

    public String name;
    Tenant(String name) {
        this.name = name;
    }
    public String toString() {
        return this.name;
    }
}
