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

import com.google.gson.Gson;
import com.rukspot.sample.restclient.PublisherClient;
import com.rukspot.sample.restclient.Settings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.wso2.am.integration.clients.publisher.api.v1.dto.APIDTO;
import org.wso2.am.integration.clients.store.api.v1.dto.ApplicationDTO;
import org.wso2.am.integration.clients.store.api.v1.dto.ApplicationKeyDTO;
import org.wso2.carbon.apimgt.gateway.APIMgtGatewayConstants;
import org.wso2.carbon.apimgt.usage.publisher.APIMgtUsageDataBridgeDataPublisher;
import org.wso2.carbon.apimgt.usage.publisher.APIMgtUsageDataPublisher;
import org.wso2.carbon.apimgt.usage.publisher.DataPublisherUtil;
import org.wso2.carbon.apimgt.usage.publisher.dto.DataBridgeFaultPublisherDTO;
import org.wso2.carbon.apimgt.usage.publisher.dto.DataBridgeRequestResponseStreamPublisherDTO;
import org.wso2.carbon.apimgt.usage.publisher.dto.DataBridgeThrottlePublisherDTO;
import org.wso2.carbon.apimgt.usage.publisher.dto.ExecutionTimeDTO;
import org.wso2.carbon.apimgt.usage.publisher.dto.FaultPublisherDTO;
import org.wso2.carbon.apimgt.usage.publisher.dto.RequestResponseStreamDTO;
import org.wso2.carbon.apimgt.usage.publisher.dto.ThrottlePublisherDTO;
import org.wso2.carbon.databridge.agent.DataPublisher;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.exception.TransportException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class EventPublisher extends APIMgtUsageDataBridgeDataPublisher {
    private static final Log log = LogFactory.getLog(APIMgtUsageDataBridgeDataPublisher.class);
    static Gson gson = new Gson();
    static List<RequestResponseStreamDTO> responseList = new ArrayList<>();
    static List<FaultPublisherDTO> faultList = new ArrayList<>();
    static List<ThrottlePublisherDTO> throttleList = new ArrayList<>();

    public static void main(String[] args) {
        APIMgtUsageDataPublisher publisher = new EventPublisher();
        publisher.init();
        Calendar calendar = Calendar.getInstance();

        int oldDays = 30;
        int hoursPerDay = 24;

        for (int i = 0; i < oldDays; i++) {
            for (int j = 0; j < hoursPerDay; j++) {
                calendar.add(Calendar.DATE, -i);
                calendar.add(Calendar.HOUR, -j);
                long requestTime = calendar.getTime().getTime();

                calendar.add(Calendar.SECOND, 60);
                long responseTime = calendar.getTime().getTime();

                for (RequestResponseStreamDTO dto: responseList) {
                    dto.setRequestTimestamp(requestTime);
                    dto.setResponseTime(responseTime);
                    publisher.publishEvent(dto);
                }

                for (FaultPublisherDTO dto: faultList) {
                    dto.setRequestTimestamp(requestTime);
                    publisher.publishEvent(dto);
                }

                for (ThrottlePublisherDTO dto: throttleList) {
                    dto.setThrottledTime(requestTime);
                    publisher.publishEvent(dto);
                }
            }
        }

    }

    public static void loadAndPublishResponseEvent() throws Exception {
        File dir = new File(
                "/Users/rukshan/wso2/apim/com.rukspot.sample.restclient/src/main/resources/analytics_data/response");
        List<File> files = (List<File>) FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            if(file.getName().contains(".DS_Store")) {
             continue;
            }
            String content = IOUtils.toString(new FileInputStream(file), "UTF-8");
            RequestResponseStreamDTO dto = gson.fromJson(content, RequestResponseStreamDTO.class);
            responseList.add(dto);
        }
    }

    public static void loadAndPublishFaultEvent() throws Exception {
        File dir = new File(
                "/Users/rukshan/wso2/apim/com.rukspot.sample.restclient/src/main/resources/analytics_data/fault");
        List<File> files = (List<File>) FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            if(file.getName().contains(".DS_Store")) {
                continue;
            }
            String content = IOUtils.toString(new FileInputStream(file), "UTF-8");
            FaultPublisherDTO dto = gson.fromJson(content, FaultPublisherDTO.class);
            faultList.add(dto);

        }
    }

    public static void loadAndPublishThrottleEvent()
            throws Exception {
        File dir = new File(
                "/Users/rukshan/wso2/apim/com.rukspot.sample.restclient/src/main/resources/analytics_data/throttle");
        List<File> files = (List<File>) FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            if(file.getName().contains(".DS_Store")) {
                continue;
            }
            String content = IOUtils.toString(new FileInputStream(file), "UTF-8");
            ThrottlePublisherDTO dto = gson.fromJson(content, ThrottlePublisherDTO.class);
            throttleList.add(dto);

        }
    }

    public static void saveResponse(APIDTO apidto, ApplicationDTO appDto, ApplicationKeyDTO keyDTO, String tenant,
            String enduser) {
        RequestResponseStreamDTO stream = new RequestResponseStreamDTO();
        stream.setApiContext(apidto.getContext());
        stream.setApiHostname("gateway");
        stream.setApiMethod("GET");
        stream.setApiName(apidto.getName());
        stream.setApiCreatorTenantDomain(tenant);
        stream.setApiCreator(apidto.getProvider());
        stream.setApiResourcePath("/menu");
        stream.setApiResourceTemplate("/menu");
        stream.setApiTier("unlimited");
        stream.setApiVersion("1.0.0");
        stream.setApplicationConsumerKey(keyDTO.getConsumerKey());
        stream.setApplicationId(appDto.getApplicationId());
        stream.setApplicationName(appDto.getName());
        stream.setApplicationOwner(appDto.getOwner());
        stream.setBackendTime(System.currentTimeMillis());

        stream.setDestination(Settings.ENDPOINT);
        stream.setExecutionTime(new ExecutionTimeDTO());
        stream.setMetaClientType("");
        stream.setProtocol("");
        stream.setRequestTimestamp(System.currentTimeMillis());
        stream.setResponseCacheHit(false);
        stream.setResponseCode(200);
        stream.setResponseSize(1);
        stream.setServiceTime(System.currentTimeMillis());
        stream.setThrottledOut(false);
        stream.setUserAgent("usageAgent");
        stream.setUserIp("127.0.0.1");
        stream.setUsername(enduser);
        stream.setUserTenantDomain(tenant);
        stream.setResponseTime(System.currentTimeMillis());
        stream.setCorrelationID(UUID.randomUUID().toString());
        stream.setGatewayType(APIMgtGatewayConstants.GATEWAY_TYPE);
        stream.setLabel(APIMgtGatewayConstants.SYNAPDE_GW_LABEL);
        String file = "response/" + tenant + "_" + stream.getApiCreator() + "_" + stream.getApiName() + "_" + stream
                .getApplicationOwner() + "_" + stream.getApplicationName() + "_" + stream.getUsername();
        String payload = gson.toJson(stream);
        save(file, payload);
    }

    public static void saveThrottle(APIDTO apidto, ApplicationDTO appDto, ApplicationKeyDTO keyDTO, String tenant,
            String enduser) {
        ThrottlePublisherDTO stream = new ThrottlePublisherDTO();
        stream.setContext(apidto.getContext());
        stream.setHostName("gateway");
        stream.setApiname(apidto.getName());
        stream.setApiCreatorTenantDomain(tenant);
        stream.setApiCreator(apidto.getProvider());
        stream.setVersion(apidto.getVersion());
        stream.setApplicationId(appDto.getApplicationId());
        stream.setApplicationName(appDto.getName());
        stream.setSubscriber(appDto.getOwner());
        stream.setThrottledTime(System.currentTimeMillis());
        stream.setUsername(enduser);
        stream.setTenantDomain(tenant);
        stream.setCorrelationID(UUID.randomUUID().toString());
        stream.setGatewayType(APIMgtGatewayConstants.GATEWAY_TYPE);
        stream.setThrottledOutReason("appThrottledOut");
        String file = "throttle/" + tenant + "_" + stream.getApiCreator() + "_" + stream.getApiname() + "_" + stream
                .getSubscriber() + "_" + stream.getApplicationName() + "_" + stream.getUsername();
        String payload = gson.toJson(stream);
        save(file, payload);
    }

    public static void saveFaulty(APIDTO apidto, ApplicationDTO appDto, ApplicationKeyDTO keyDTO, String tenant,
            String enduser) {
        FaultPublisherDTO stream = new FaultPublisherDTO();
        stream.setApiContext(apidto.getContext());
        stream.setHostname("gateway");
        stream.setApiMethod("GET");
        stream.setApiName(apidto.getName());
        stream.setApiCreatorTenantDomain(tenant);
        stream.setApiCreator(apidto.getProvider());
        stream.setApiResourcePath("/menu");
        stream.setApiVersion("1.0.0");
        stream.setApplicationConsumerKey(keyDTO.getConsumerKey());
        stream.setApplicationId(appDto.getApplicationId());
        stream.setApplicationName(appDto.getName());
        stream.setMetaClientType("");
        stream.setProtocol("");
        stream.setRequestTimestamp(System.currentTimeMillis());
        stream.setUsername(enduser);
        stream.setUserTenantDomain(tenant);
        stream.setRequestTimestamp(System.currentTimeMillis());
        stream.setGatewaType(APIMgtGatewayConstants.GATEWAY_TYPE);
        String file = "fault/" + tenant + "_" + stream.getApiCreator() + "_" + stream.getApiName() + "_" + stream
                .getApplicationName() + "_" + stream.getUsername();
        String payload = gson.toJson(stream);
        save(file, payload);
    }

    public static void saveNonAuth(APIDTO apidto, String tenant) {
        ApplicationDTO appDto = new ApplicationDTO();
        ApplicationKeyDTO keyDTO = new ApplicationKeyDTO();

        appDto.setApplicationId("0");
        appDto.setName("None");
        appDto.setOwner("None");
        keyDTO.setConsumerKey("None");
        saveResponse(apidto, appDto, keyDTO, tenant, "anonymous");
    }

    static void save(String fileName, String payload) {
        String path = "src/main/resources/analytics_data/";
        try {
            File file = new File(path + fileName + ".json");
            IOUtils.write(payload, new FileOutputStream(file), "UTF-8");
            System.out.println("saving " + file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static RequestResponseStreamDTO getRequestResponseStreamDTO(long requestTime, long responseTime) {
        RequestResponseStreamDTO stream = new RequestResponseStreamDTO();
        stream.setApiContext("/api_name_1584357768536/1.0.0");
        stream.setApiHostname("gateway");
        stream.setApiMethod("GET");
        stream.setApiName("api_name_1584357768536");
        stream.setApiCreatorTenantDomain("carbon.super");
        stream.setApiCreator("pub3");
        stream.setApiResourcePath("/menu");
        stream.setApiResourceTemplate("/menu");
        stream.setApiTier("unlimited");
        stream.setApiVersion("1.0.0");
        stream.setApplicationConsumerKey("NYmDS49Ot_bLV8_adPnMLeGjv44a");
        stream.setApplicationId("51");
        stream.setApplicationName("test1");
        stream.setApplicationOwner("admin");
        stream.setBackendTime(System.currentTimeMillis());
        stream.setDestination("https://localhost:9443/am/sample/pizzashack/v1/api/");
        stream.setExecutionTime(new ExecutionTimeDTO());
        stream.setMetaClientType("");
        stream.setProtocol("");
        stream.setRequestTimestamp(requestTime);
        stream.setResponseCacheHit(false);
        stream.setResponseCode(200);
        stream.setResponseSize(1);
        stream.setServiceTime(System.currentTimeMillis());
        stream.setThrottledOut(false);
        stream.setUserAgent("usageAgent");
        stream.setUserIp("127.0.0.1");
        stream.setUsername("admin@carbon.super");
        stream.setUserTenantDomain("carbon.super");
        stream.setResponseTime(responseTime);
        stream.setCorrelationID(UUID.randomUUID().toString());
        stream.setGatewayType(APIMgtGatewayConstants.GATEWAY_TYPE);
        stream.setLabel(APIMgtGatewayConstants.SYNAPDE_GW_LABEL);
        return stream;
    }

    @Override
    public void publishEvent(RequestResponseStreamDTO requestStream) {
        DataBridgeRequestResponseStreamPublisherDTO dataBridgeRequestStreamPublisherDTO =
                new DataBridgeRequestResponseStreamPublisherDTO(requestStream);
        List<String> missingMandatoryValues = dataBridgeRequestStreamPublisherDTO.getMissingMandatoryValues();
        if (missingMandatoryValues.isEmpty()) {
            try {
                String streamID = "org.wso2.apimgt.statistics.request:3.1.0";
                this.dataPublisher.publish(streamID, System.currentTimeMillis(),
                        (Object[]) dataBridgeRequestStreamPublisherDTO.createMetaData(), (Object[]) null,
                        (Object[]) ((Object[]) dataBridgeRequestStreamPublisherDTO.createPayload()));
            } catch (Exception var5) {
                log.error("Error while publishing Request event", var5);
            }
        } else {
            log.error("RequestResponse event dropped due to unavailability of mandatory data: " + missingMandatoryValues
                    .toString() + " in event: " + dataBridgeRequestStreamPublisherDTO.toString());
        }
    }

    @Override
    public void publishEvent(FaultPublisherDTO faultPublisherDTO) {
        DataBridgeFaultPublisherDTO dataBridgeFaultPublisherDTO = new DataBridgeFaultPublisherDTO(faultPublisherDTO);
        List<String> missingMandatoryValues = dataBridgeFaultPublisherDTO.getMissingMandatoryValues();
        if (missingMandatoryValues.isEmpty()) {
            try {
                String streamID = "org.wso2.apimgt.statistics.fault:3.1.0";
                this.dataPublisher.publish(streamID, System.currentTimeMillis(),
                        (Object[]) dataBridgeFaultPublisherDTO.createMetaData(), (Object[]) null,
                        (Object[]) ((Object[]) dataBridgeFaultPublisherDTO.createPayload()));
            } catch (Exception var5) {
                log.error("Error while publishing Fault event", var5);
            }
        } else {
            log.error("Faulty invocation event dropped due to missing mandatory data: " + missingMandatoryValues
                    .toString() + " in event: " + dataBridgeFaultPublisherDTO.toString());
        }
    }

    @Override
    public void publishEvent(ThrottlePublisherDTO throttPublisherDTO) {
        DataBridgeThrottlePublisherDTO dataBridgeThrottlePublisherDTO =
                new DataBridgeThrottlePublisherDTO(throttPublisherDTO);
        List<String> missingMandatoryValues = dataBridgeThrottlePublisherDTO.getMissingMandatoryValues();
        if (missingMandatoryValues.isEmpty()) {
            try {
                String streamID = "org.wso2.apimgt.statistics.throttle:3.1.0";
                this.dataPublisher.tryPublish(streamID, System.currentTimeMillis(),
                        (Object[]) ((Object[]) dataBridgeThrottlePublisherDTO.createMetaData()), (Object[]) null,
                        (Object[]) ((Object[]) dataBridgeThrottlePublisherDTO.createPayload()));
            } catch (Exception var5) {
                log.error("Error while publishing Throttle exceed event", var5);
            }
        } else {
            log.error("Throttling event dropped due to missing mandatory data: " + missingMandatoryValues.toString()
                    + " in event: " + dataBridgeThrottlePublisherDTO.toString());
        }
    }

    @Override
    public void init() {
        try {
            loadAndPublishResponseEvent();
            loadAndPublishThrottleEvent();
            loadAndPublishFaultEvent();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.setProperty("javax.net.ssl.keyStore",
                Settings.AM_HOME + "/repository/resources/security/wso2carbon.jks");
        System.setProperty("javax.net.ssl.trustStore",
                Settings.AM_HOME + "/repository/resources/security/client-truststore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "wso2carbon");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");

        System.setProperty("carbon.home", Settings.AM_HOME);
        String serverUser = "admin";
        String serverPassword = "admin";
        String serverURL = "tcp://localhost:7612";
        String serverAuthURL = "ssl://localhost:7712";

        try {
            this.dataPublisher = new DataPublisher(null, serverURL, serverAuthURL, serverUser, serverPassword);
        } catch (DataEndpointConfigurationException e) {
            log.error("Error while creating data publisher", e);
        } catch (DataEndpointException e) {
            log.error("Error while creating data publisher", e);
        } catch (DataEndpointAgentConfigurationException e) {
            log.error("Error while creating data publisher", e);
        } catch (TransportException e) {
            log.error("Error while creating data publisher", e);
        } catch (DataEndpointAuthenticationException e) {
            log.error("Error while creating data publisher", e);
        }
    }
}
