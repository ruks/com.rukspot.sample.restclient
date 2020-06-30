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

package com.rukspot.sample.Utils;

import com.rukspot.sample.configuration.ConfigurationService;
import com.rukspot.sample.configuration.models.Configurations;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class CurlGenerator {
    private File file;
    private static CurlGenerator instance;

    private CurlGenerator() {
        String fileName = "rundata.sh";
        Configurations configs = ConfigurationService.getInstance().getConfigurations();
        String filePath = configs.getResourcePath() + File.separator + fileName;
        file = new File(filePath);
        file.setExecutable(true);
        if(file.exists() && file.delete()) {
            System.out.println("existing file deleted");
        }
    }

    public static void saveAsCurl(HttpUriRequest request, int delay) {
        if(instance == null){
            instance = new CurlGenerator();
        }
        try {
            instance.saveFile(request, delay);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String log(HttpUriRequest request) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("curl").append(' ')
                .append("-X").append(' ')
                .append(request.getMethod()).append(' ')
                .append("\"").append(request.getURI().toString()).append("\"").append(' ')
        ;

        for (Header header : request.getAllHeaders()) {
            builder
                    .append("-H").append(' ')
                    .append("\"").append(header.getName()).append(": ")
                    .append(header.getValue()).append("\"").append(" ")
            ;
        }
        if(request instanceof HttpPost) {
            if( ((HttpPost) request).getEntity() != null) {
                String content = IOUtils.toString(((HttpPost) request).getEntity().getContent(), "UTF-8");
                builder.append("-d").append(' ')
                        .append("'").append(content).append("'").append(' ');
            }
        }
        builder.append("-k");
        return builder.toString();
    }

    private void saveFile(HttpUriRequest request, int delay) throws IOException {
        String payload = log(request);
        FileUtils.writeStringToFile(file, payload, Charset.defaultCharset(), true);
        FileUtils.writeStringToFile(file, System.getProperty("line.separator"), Charset.defaultCharset(), true);
        if(delay > 0) {
            String delayPayload = "sleep " + delay;
            FileUtils.writeStringToFile(file, delayPayload, Charset.defaultCharset(), true);
            FileUtils.writeStringToFile(file, System.getProperty("line.separator"), Charset.defaultCharset(), true);
        }
    }
}
