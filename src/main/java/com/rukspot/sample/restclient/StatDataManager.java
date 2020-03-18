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

import com.google.gson.Gson;
import com.rukspot.sample.analytics.EventPublisher;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import org.wso2.am.integration.clients.publisher.api.v1.dto.APIDTO;
import org.wso2.am.integration.clients.publisher.api.v1.dto.APIOperationsDTO;
import org.wso2.am.integration.clients.store.api.v1.dto.ApplicationDTO;
import org.wso2.am.integration.clients.store.api.v1.dto.ApplicationKeyDTO;

import java.io.File;
import java.util.Arrays;

public class StatDataManager {
    static Gson gson = new Gson();

    static String PASS = "admin";

    public static void main(String[] args) throws Exception {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "ERROR");
        final org.slf4j.Logger log = LoggerFactory.getLogger(StatDataManager.class);

        System.setProperty("javax.net.ssl.keyStore",
                Settings.AM_HOME + "/repository/resources/security/wso2carbon.jks");
        System.setProperty("javax.net.ssl.trustStore",
                Settings.AM_HOME + "/repository/resources/security/client-truststore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "wso2carbon");

        TenantMgt tenantMgt = new TenantMgt("admin", PASS, Settings.BASE_ADMIN_URL);
        for (Tenant tenant : Arrays.asList(Tenant.values())) {
            String domain = tenant.name;
            System.out.println("Using tenant " + domain);
            String admin = "admin";
            if (domain != null && !"carbon.super".equalsIgnoreCase(domain)) {
                tenantMgt.addTenant(tenant.toString(), PASS, "admin");
                admin = "admin@" + domain;
            }
            AdminClient superClient = new AdminClient(admin, PASS);
            superClient.createApplicationPolicy(Settings.APP_POLICY_1MIN);
            generateTrafficForTenant(admin, domain);
        }

    }

    public static void generateTrafficForTenant(String user, String tenant) throws Exception {
        UserMgt superUserMgt = new UserMgt(user, PASS, Settings.BASE_ADMIN_URL);
        for (Publisher publisher : Arrays.asList(Publisher.values())) {
            superUserMgt.addUser(publisher.name, PASS, new String[] { "Internal/publisher", "Internal/creator" });
        }
        for (Subscriber subscriber : Arrays.asList(Subscriber.values())) {
            superUserMgt.addUser(subscriber.name, PASS, new String[] { "Internal/subscriber" });
        }
        for (Users users : Arrays.asList(Users.values())) {
            superUserMgt.addUser(users.name, PASS, new String[] { "Internal/everyone" });
        }

        //                clearAppsAndAPIs("admin","user2", null);
        //                clearAppsAndAPIs("admin","user2", Settings.TENANT_WSO2);
        UserSet set1 = new UserSet(Publisher.PUB1,
                new APPDev[] { new APPDev(Subscriber.SUB1, new Users[] { Users.USER1, Users.USER2, Users.USER3 }) });
        UserSet set2 = new UserSet(Publisher.PUB2,
                new APPDev[] { new APPDev(Subscriber.SUB1, new Users[] { Users.USER1, Users.USER2, Users.USER3 }) });
        UserSet set3 = new UserSet(Publisher.PUB3,
                new APPDev[] { new APPDev(Subscriber.SUB2, new Users[] { Users.USER1, Users.USER2, Users.USER3 }) });
        UserSet set4 = new UserSet(Publisher.PUB4,
                new APPDev[] { new APPDev(Subscriber.SUB2, new Users[] { Users.USER1, Users.USER2, Users.USER3 }) });
        UserSet set5 = new UserSet(Publisher.PUB5,
                new APPDev[] { new APPDev(Subscriber.SUB3, new Users[] { Users.USER1, Users.USER2, Users.USER3 }) });
        UserSet set6 = new UserSet(Publisher.PUB6,
                new APPDev[] { new APPDev(Subscriber.SUB3, new Users[] { Users.USER1, Users.USER2, Users.USER3 }) });
        UserSet set7 = new UserSet(Publisher.PUB7,
                new APPDev[] { new APPDev(Subscriber.SUB3, new Users[] { Users.USER1, Users.USER2, Users.USER3 }) });

        generateTrafficForUser(set1, tenant);
//        generateTrafficForUser(set2, tenant);
//        generateTrafficForUser(set3, tenant);
//        generateTrafficForUser(set4, tenant);
//        generateTrafficForUser(set5, tenant);
//        generateTrafficForUser(set6, tenant);
//        generateTrafficForUser(set7, tenant);
    }

    public static void generateTrafficForUser(UserSet set, String tenant) throws Exception {

        PublisherClient publisherClient = new PublisherClient(set.publisher.getUsername(tenant), PASS);
        DevPortalClient devPortalClient;
        String relativePath = "data" + File.separator + "api.json";
        String originalPayload =
                IOUtils.toString(PublisherClient.class.getClassLoader().getResourceAsStream(relativePath), "UTF-8");
        APIDTO apidtoResponse;
        String payload;
        APIDTO apidto;

        payload = originalPayload.replaceAll("\\$prod_endpoint", Settings.ENDPOINT);
        apidto = gson.fromJson(payload, APIDTO.class);
        System.out.println("Starting default");
        for (int i = 0; i < 1; i++) {
            String id = System.currentTimeMillis() + "";
            apidtoResponse =
                    publisherClient.createAndPublishAPI(apidto, "api_name_" + id, set.publisher.getUsername(tenant));
            for (APPDev subscriber : set.subscribers) {
                devPortalClient = new DevPortalClient(subscriber.getAppDev().getUsername(tenant), PASS);
                ApplicationDTO appDto = devPortalClient.createApp("app_name_" + id, Settings.APP_POLICY_50MIN);
                ApplicationKeyDTO keyDTO = devPortalClient.createSubscribe(appDto, apidtoResponse);
                for (Users user : subscriber.endusers) {
                    String accessToken = Token.getNewToken(user.getUsername(tenant), PASS, keyDTO.getConsumerKey(),
                            keyDTO.getConsumerSecret(), null);
                    String prefix=apidto.getProvider()+"-"+apidto.getName()+"-"+subscriber.getAppDev().getUsername(tenant)+"-"+"app_name_" + id+"-"+user;
                    devPortalClient
                            .invokeAPI(Settings.GW_URL + apidtoResponse.getContext() + "/1.0.0/menu", accessToken, 0, prefix);
                    EventPublisher.saveResponse(apidto, appDto, keyDTO, tenant, user.getUsername(tenant));
                }
            }
        }

        System.out.println("Starting throttling");
        payload = originalPayload.replaceAll("\\$prod_endpoint", Settings.ENDPOINT);
        apidto = gson.fromJson(payload, APIDTO.class);
        for (int i = 0; i < 1; i++) {
            String id = System.currentTimeMillis() + "";
            String apiName = "throttle_api_name_" + id;
            String appName = "throttle_app_name_" + id;
            apidtoResponse =
                    publisherClient.createAndPublishAPI(apidto, apiName + id, set.publisher.getUsername(tenant));
            for (APPDev subscriber : set.subscribers) {
                devPortalClient = new DevPortalClient(subscriber.getAppDev().getUsername(tenant), PASS);
                ApplicationDTO appDto = devPortalClient.createApp(appName + id, Settings.APP_POLICY_1MIN);
                ApplicationKeyDTO keyDTO = devPortalClient.createSubscribe(appDto, apidtoResponse);

                for (Users user : subscriber.endusers) {
                    String accessToken = Token.getNewToken(user.getUsername(tenant), PASS, keyDTO.getConsumerKey(),
                            keyDTO.getConsumerSecret(), null);
                    String prefix=apidto.getProvider()+"-"+apidto.getName()+"-"+subscriber.getAppDev().getUsername(tenant)+"-"+"app_name_" + id+"-"+user;
                    devPortalClient
                            .invokeAPI(Settings.GW_URL + apidtoResponse.getContext() + "/1.0.0/menu", accessToken, 2, prefix);
                    EventPublisher.saveThrottle(apidto, appDto, keyDTO, tenant, user.getUsername(tenant));
                }
            }
        }

        System.out.println("Starting faulty");
        payload = originalPayload.replaceAll("\\$prod_endpoint", Settings.ERROR_ENDPOINT);
        apidto = gson.fromJson(payload, APIDTO.class);
        for (int i = 0; i < 1; i++) {
            String id = System.currentTimeMillis() + "";
            apidtoResponse = publisherClient
                    .createAndPublishAPI(apidto, "faulty_api_name_" + id, set.publisher.getUsername(tenant));
            for (APPDev subscriber : set.subscribers) {
                devPortalClient = new DevPortalClient(subscriber.getAppDev().getUsername(tenant), PASS);
                ApplicationDTO appDto = devPortalClient.createApp("faulty_app_name_" + id, Settings.APP_POLICY_50MIN);
                ApplicationKeyDTO keyDTO = devPortalClient.createSubscribe(appDto, apidtoResponse);
                for (Users user : subscriber.endusers) {
                    String accessToken = Token.getNewToken(user.getUsername(tenant), PASS, keyDTO.getConsumerKey(),
                            keyDTO.getConsumerSecret(), null);
                    String prefix=apidto.getProvider()+"-"+apidto.getName()+"-"+subscriber.getAppDev().getUsername(tenant)+"-"+"app_name_" + id+"-"+user;
                    devPortalClient
                            .invokeAPI(Settings.GW_URL + apidtoResponse.getContext() + "/1.0.0/menu", accessToken, 0, prefix);
                    EventPublisher.saveFaulty(apidto, appDto, keyDTO, tenant, user.getUsername(tenant));
                }
            }
        }

        System.out.println("Starting non auth");
        payload = originalPayload.replaceAll("\\$prod_endpoint", Settings.ENDPOINT);
        apidto = gson.fromJson(payload, APIDTO.class);
        for (int i = 0; i < 1; i++) {
            String id = System.currentTimeMillis() + "";
            for (APIOperationsDTO dto : apidto.getOperations()) {
                dto.setAuthType("None");
            }
            apidtoResponse = publisherClient
                    .createAndPublishAPI(apidto, "noauth_api_name_" + id, set.publisher.getUsername(tenant));
            for (APPDev subscriber : set.subscribers) {
                devPortalClient = new DevPortalClient(subscriber.getAppDev().getUsername(tenant), PASS);
                String prefix=apidto.getProvider()+"-"+apidto.getName()+"-"+subscriber.getAppDev().getUsername(tenant)+"-anonymous";
                devPortalClient.invokeAPI(Settings.GW_URL + apidtoResponse.getContext() + "/1.0.0/menu", null, 0, prefix);
                EventPublisher.saveNonAuth(apidto, tenant);
            }
        }

    }

    public static void clearAppsAndAPIs(String publisher, String subscriber, String tenant) throws Exception {
        if (tenant != null) {
            publisher += "@" + tenant;
            subscriber += "@" + tenant;
        }
        PublisherClient publisherClient = new PublisherClient(publisher, "admin");
        DevPortalClient devPortalClient = new DevPortalClient(subscriber, "admin");
        devPortalClient.cleanApplications();
        publisherClient.cleanAPis();
    }

}




