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
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Base64;

public class Token {

    public static String getNewToken(String user, String pass, String ck, String cs, String scope) throws IOException {
        ConfigurationService service = ConfigurationService.getInstance();
        Configurations configs = service.getConfigurations();
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(configs.getTokenEndpoint());
        String payload = "grant_type=password&username="+user+"&password="+pass;
        if(scope != null) {
            payload += "&scope=" + scope;
        }
        HttpEntity entity = new StringEntity(payload);
        httpPost.setEntity(entity);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        String cred = ck + ":" + cs;
        httpPost.addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(cred.getBytes()));
        HttpResponse response = httpClient.execute(httpPost);
        String body = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
//        System.out.println(body);
        JSONObject jsonObject = new JSONObject(body);
        return jsonObject.getString("access_token");
    }
}
