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

package com.rukspot.sample.configuration;

import com.rukspot.sample.configuration.models.Configurations;
import org.wso2.carbon.config.ConfigProviderFactory;
import org.wso2.carbon.config.provider.ConfigProvider;
import org.wso2.carbon.utils.Constants;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigurationService {
    private static ConfigurationService service = new ConfigurationService();
    private Configurations configurations;
    String configPathKey = "config";
    private ConfigurationService() {
        Path deploymentConfigPath;
        if(System.getProperties().containsKey(configPathKey)) {
            deploymentConfigPath = Paths.get(System.getProperty(configPathKey));
        } else {
            deploymentConfigPath = Paths.get("src/main/resources", Constants.DEPLOYMENT_CONFIG_YAML);
        }

        try {
            ConfigProvider configProvider = ConfigProviderFactory.getConfigProvider(deploymentConfigPath, null);
            configurations = configProvider.getConfigurationObject(Configurations.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ConfigurationService getInstance() {
        return service;
    }

    public Configurations getConfigurations() {
        return configurations;
    }

    public static void main(String[] args) {
        ConfigurationService service = new ConfigurationService();
    }
}
