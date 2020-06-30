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

package com.rukspot.sample;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class Tmg {

    static File file;
    public static void main(String[] args) throws Exception {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpUriRequest get = new HttpGet("http://get");
        get.addHeader("Authorization", "Bearer t");
        get.addHeader("X-Forwarded-For", "1.1.177.1");

        HttpPost post = new HttpPost("http://get");
        post.addHeader("Authorization", "Bearer t");
        post.addHeader("X-Forwarded-For", "1.1.177.1");
        post.setEntity(new StringEntity("String payload"));

        String fileName = "rundata";
        String path = "src/main/resources/analytics_data/";

        file = new File(path + fileName + ".sh");
        if(file.exists() && file.delete()) {
            System.out.println("existing file deleted");
        }

        saveFile(get);
        saveFile(post);

    }

    static String log(HttpUriRequest request) throws IOException {
        return null;
    }

    static void saveFile(HttpUriRequest request) throws IOException {
        String payload = log(request);
        FileUtils.writeStringToFile(file, payload, Charset.defaultCharset(), true);
        FileUtils.writeStringToFile(file, System.getProperty("line.separator"), Charset.defaultCharset(), true);
    }


}

//curl -X GET "https://localhost:8243/code_api_1592994531310/1.0.0/123" -H "accept: */*" -H "Authorization: Bearer asd"
