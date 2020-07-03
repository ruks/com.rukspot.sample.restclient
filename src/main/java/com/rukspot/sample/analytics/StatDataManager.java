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
import com.rukspot.sample.configuration.models.TestConfig;
import com.rukspot.sample.configuration.models.ThrottlePolicy;
import com.rukspot.sample.configuration.models.User;
import com.rukspot.sample.restclient.AdminClient;
import com.rukspot.sample.restclient.TenantMgt;
import com.rukspot.sample.restclient.UserMgt;
import com.rukspot.sample.websocket.WebSocketServer;
import org.slf4j.LoggerFactory;
import org.wso2.apimgt.demo.backend.CustomerService;
import org.wso2.apimgt.demo.backend.CustomersService;
import org.wso2.msf4j.MicroservicesRunner;

public class StatDataManager {
    private Configurations configs;
    static org.slf4j.Logger log = null;

    public static void main(String[] args) throws Exception {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "ERROR");
        log = LoggerFactory.getLogger(StatDataManager.class);

        StatDataManager manager = new StatDataManager();
        MicroservicesRunner microservicesRunner = new MicroservicesRunner()
                .deploy(new CustomerService())
                .deploy(new CustomersService());
        WebSocketServer webSocketServer = new WebSocketServer(7474);
        try {
            microservicesRunner.start();
            webSocketServer.run();
            manager.init();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            microservicesRunner.stop();
            webSocketServer.stop();
            System.exit(0);
        }
    }

    public void init() throws Exception {
        ConfigurationService service = ConfigurationService.getInstance();
        configs = service.getConfigurations();
        if(configs == null) {
            log.error("Configurations not initialised");
            System.exit(-1);
        }
        System.setProperty("javax.net.ssl.keyStore",
                configs.getAmHome() + "/repository/resources/security/wso2carbon.jks");
        System.setProperty("javax.net.ssl.trustStore",
                configs.getAmHome() + "/repository/resources/security/client-truststore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "wso2carbon");

        String pass = configs.getDefaultPass();
        TenantMgt tenantMgt = new TenantMgt("admin", pass, configs.getAdminServiceBaseUrl());
        TestConfig testConfig = configs.getTestConfigs();
        for (String tenant: testConfig.getTenants()) {
            String admin = "admin";
            if (tenant != null && !"carbon.super".equalsIgnoreCase(tenant)) {
                admin = "admin@" + tenant;
            }
            if (tenant != null && !"carbon.super".equalsIgnoreCase(tenant)) {
                System.out.println("Adding tenant " + tenant);
                tenantMgt.addTenant(tenant, pass, "admin");
            }

            for (ThrottlePolicy policy : testConfig.getThrottlePolicy()) {
                AdminClient superClient = new AdminClient(admin, pass);
                superClient.createApplicationPolicy(policy);
            }

            UserMgt superUserMgt = new UserMgt(admin, pass, configs.getAdminServiceBaseUrl());
            for (User user : testConfig.getUsers()) {
                superUserMgt.addUser(user.getUsername(), pass, user.getRoles().toArray(new String[] {}));
            }
        }

        for (TestCase testCase: configs.getTestCases()) {
            for (String tenant : configs.getTestConfigs().getTenants()) {
                new StatTestCase(testCase, tenant).run();
            }
        }
    }
}




