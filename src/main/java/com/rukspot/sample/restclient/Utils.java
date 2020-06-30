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

import com.rukspot.sample.configuration.ConfigurationService;
import com.rukspot.sample.configuration.models.Configurations;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Utils {
    public static String getTeantUsername(String user, String t) {
        if (t != null && !"carbon.super".equalsIgnoreCase(t)) {
            return user + "@" + t;
        }
        return user;
    }

    public static String readFile(String name) throws IOException {
        Configurations configs = ConfigurationService.getInstance().getConfigurations();
        String filePath = configs.getResourcePath() + File.separator + name;
        return FileUtils.readFileToString(new File(filePath), "UTF-8");
    }

    public static File getFile(String name) throws IOException {
        Configurations configs = ConfigurationService.getInstance().getConfigurations();
        String filePath = configs.getResourcePath() + File.separator + name;
        return new File(filePath);
    }
}
