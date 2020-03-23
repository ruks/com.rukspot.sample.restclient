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
import com.rukspot.sample.configuration.ConfigurationService;
import com.rukspot.sample.configuration.models.Configurations;
import com.rukspot.sample.configuration.models.Subscriptions;
import com.rukspot.sample.configuration.models.UserTestCase;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import org.wso2.am.integration.clients.publisher.api.v1.dto.APIDTO;
import org.wso2.am.integration.clients.publisher.api.v1.dto.APIOperationsDTO;
import org.wso2.am.integration.clients.store.api.v1.dto.ApplicationDTO;
import org.wso2.am.integration.clients.store.api.v1.dto.ApplicationKeyDTO;

import java.io.File;

public class StatDataManager {
    static Gson gson = new Gson();

    public static String PASS = "admin";
    Configurations configs;

    public static void main(String[] args) throws Exception {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "ERROR");
        final org.slf4j.Logger log = LoggerFactory.getLogger(StatDataManager.class);

        StatDataManager manager = new StatDataManager();
        manager.init();
    }

    public void init() throws Exception {
        ConfigurationService service = ConfigurationService.getInstance();
        configs = service.getConfigurations();

        System.setProperty("javax.net.ssl.keyStore",
                configs.getAmHome() + "/repository/resources/security/wso2carbon.jks");
        System.setProperty("javax.net.ssl.trustStore",
                configs.getAmHome() + "/repository/resources/security/client-truststore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "wso2carbon");

        TenantMgt tenantMgt = new TenantMgt("admin", PASS, configs.getAdminServiceBaseUrl());
        for (String tenant : configs.getTenants()) {
            System.out.println("Using tenant " + tenant);
            String admin = "admin";
            if (tenant != null && !"carbon.super".equalsIgnoreCase(tenant)) {
                tenantMgt.addTenant(tenant, PASS, "admin");
                admin = "admin@" + tenant;
            }
            AdminClient superClient = new AdminClient(admin, PASS);
            superClient.createApplicationPolicy(configs.getAppThrottlePolicy());
            generateTrafficForTenant(admin, tenant);
        }
    }

    public void generateTrafficForTenant(String user, String tenant) throws Exception {
        UserMgt superUserMgt = new UserMgt(user, PASS, configs.getAdminServiceBaseUrl());
        for (String publisher : configs.getApiDevelopers()) {
            superUserMgt.addUser(publisher, PASS, configs.getApiDeveloperRoles().toArray(new String[] {}));
        }
        for (String subscriber : configs.getAppDevelopers()) {
            superUserMgt.addUser(subscriber, PASS, configs.getAppDeveloperRoles().toArray(new String[] {}));
        }
        for (String users : configs.getApiUsers()) {
            superUserMgt.addUser(users, PASS, configs.getApiUserRoles().toArray(new String[] {}));
        }

        //                clearAppsAndAPIs("admin","user2", null);
        //                clearAppsAndAPIs("admin","user2", Settings.TENANT_WSO2);
        for (UserTestCase aCase : configs.getUserTestCases()) {
            generateTrafficForUser(aCase, tenant);
        }

    }

    public void generateTrafficForUser(UserTestCase aCase, String tenant) throws Exception {

        PublisherClient publisherClient =
                new PublisherClient(Utils.getTeantUsername(aCase.getPublisher(), tenant), PASS);
        String relativePath = "data" + File.separator + "api.json";
        String originalPayload =
                IOUtils.toString(PublisherClient.class.getClassLoader().getResourceAsStream(relativePath), "UTF-8");
        String payload;
        APIDTO apidto;

        payload = originalPayload.replaceAll("\\$prod_endpoint", configs.getApiEndpoint());
        apidto = gson.fromJson(payload, APIDTO.class);
        apiTestCase(publisherClient, aCase, apidto, tenant, "default", configs.getDefaultThrottlePolicy());

        payload = originalPayload.replaceAll("\\$prod_endpoint", configs.getApiEndpoint());
        apidto = gson.fromJson(payload, APIDTO.class);
        apiTestCase(publisherClient, aCase, apidto, tenant, "throttle", configs.getAppThrottlePolicy());

        payload = originalPayload.replaceAll("\\$prod_endpoint", configs.getFaultyApiEndpoint());
        apidto = gson.fromJson(payload, APIDTO.class);
        apiTestCase(publisherClient, aCase, apidto, tenant, "faulty", configs.getDefaultThrottlePolicy());

        payload = originalPayload.replaceAll("\\$prod_endpoint", configs.getApiEndpoint());
        apidto = gson.fromJson(payload, APIDTO.class);
        apiTestCase(publisherClient, aCase, apidto, tenant, "no_auth", configs.getDefaultThrottlePolicy());

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

    public void apiTestCase(PublisherClient publisherClient, UserTestCase aCase, APIDTO apidto, String tenant,
            String prefix, String appPolicy) throws Exception {
        System.out.println("Starting " + prefix);
        int delay = 0;
        String id = System.currentTimeMillis() + "";
        String appName = prefix + "_app_" + id;
        String apiName = prefix + "_api_" + id;
        if ("throttle".equalsIgnoreCase(prefix)) {
            delay = 2;
        } else if ("no_auth".equalsIgnoreCase(prefix)) {
            for (APIOperationsDTO dto : apidto.getOperations()) {
                dto.setAuthType("None");
            }
            appName = prefix;
        }

        APIDTO apidtoResponse = publisherClient
                .createAndPublishAPI(apidto, apiName, Utils.getTeantUsername(aCase.getPublisher(), tenant));
        for (Subscriptions subscription : aCase.getSubscriptions()) {
            DevPortalClient devPortalClient =
                    new DevPortalClient(Utils.getTeantUsername(subscription.getSubscriber(), tenant), PASS);
            ApplicationDTO appDto = devPortalClient.createApp(appName, appPolicy);
            ApplicationKeyDTO keyDTO = devPortalClient.createSubscribe(appDto, apidtoResponse);
            for (String user : subscription.getUsers()) {
                String accessToken =
                        Token.getNewToken(Utils.getTeantUsername(user, tenant), PASS, keyDTO.getConsumerKey(),
                                keyDTO.getConsumerSecret(), null);
                String logPrefix =
                        apidto.getProvider() + "-" + apidto.getName() + "-" + Utils.getTeantUsername(user, tenant) + "-"
                                + appName + "-" + user;
                devPortalClient
                        .invokeAPI(configs.getGwEndpoint() + apidtoResponse.getContext() + "/1.0.0/menu", accessToken,
                                delay, logPrefix);
                //                    EventPublisher.saveResponse(apidto, appDto, keyDTO, tenant, user.getUsername(tenant));
            }
        }
    }
}




