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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

public class Test {
    public static void main(String[] args) throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(payload)));

        XPathFactory xpathfactory = XPathFactory.newInstance();
        XPath xpath = xpathfactory.newXPath();
        xpath.setNamespaceContext(new NamespaceResolver(doc));
        XPathExpression expr = xpath.compile("//ax2548:displayName");
        Object result = expr.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
        List<String> claimList = Arrays.asList("First Name","Telephone","Organization","Email");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i).getParentNode();
            if(claimList.contains(nodes.item(i).getTextContent())) {
                String claimName = nodes.item(i).getTextContent();
                NodeList nodeList = element.getElementsByTagName("ax2548:fieldValue");
                if(nodeList.getLength() > 0) {
                    System.out.println(nodeList.item(0).getTextContent());
                    String claimValue = nodes.item(i).getTextContent();
                }
            }
        }

    }

    static String p1 ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<Employee>\n" + "   <fieldValues>\n"
            + "      <checkedAttribute>false</checkedAttribute>\n"
            + "      <claimUri>http://wso2.org/claims/country</claimUri>\n"
            + "      <displayName>Country</displayName>\n" + "      <displayOrder>5</displayOrder>\n"
            + "      <fieldValue />\n" + "      <readOnly>false</readOnly>\n" + "      <regEx />\n"
            + "      <required>false</required>\n" + "   </fieldValues>\n" + "   <fieldValues>\n"
            + "      <checkedAttribute>false</checkedAttribute>\n"
            + "      <claimUri>http://wso2.org/claims/mobile</claimUri>\n" + "      <displayName>Mobile</displayName>\n"
            + "      <displayOrder>8</displayOrder>\n" + "      <fieldValue />\n" + "      <readOnly>false</readOnly>\n"
            + "      <regEx />\n" + "      <required>false</required>\n" + "   </fieldValues>\n" + "</Employee>";

    static String payload = "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">\n"
            + "   <soapenv:Body>\n"
            + "      <ns:getUserProfileResponse xmlns:ns=\"http://mgt.profile.user.identity.carbon.wso2.org\">\n"
            + "         <ns:return xsi:type=\"ax2548:UserProfileDTO\" xmlns:ax2548=\"http://mgt.profile.user.identity."
            + "carbon.wso2.org/xsd\" xmlns:ax2549=\"http://base.identity.carbon.wso2.org/xsd\" xmlns:ax2552="
            + "\"http://core.carbon.wso2.org/xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
            + "            <ax2548:fieldValues xsi:type=\"ax2548:UserFieldDTO\">\n"
            + "               <ax2548:checkedAttribute>false</ax2548:checkedAttribute>\n"
            + "               <ax2548:claimUri>http://wso2.org/claims/country</ax2548:claimUri>\n"
            + "               <ax2548:displayName>Country</ax2548:displayName>\n"
            + "               <ax2548:displayOrder>5</ax2548:displayOrder>\n"
            + "               <ax2548:fieldValue xsi:nil=\"true\"/>\n"
            + "               <ax2548:readOnly>false</ax2548:readOnly>\n"
            + "               <ax2548:regEx xsi:nil=\"true\"/>\n"
            + "               <ax2548:required>false</ax2548:required>\n" + "            </ax2548:fieldValues>\n"
            + "            <ax2548:fieldValues xsi:type=\"ax2548:UserFieldDTO\">\n"
            + "               <ax2548:checkedAttribute>false</ax2548:checkedAttribute>\n"
            + "               <ax2548:claimUri>http://wso2.org/claims/mobile</ax2548:claimUri>\n"
            + "               <ax2548:displayName>Mobile</ax2548:displayName>\n"
            + "               <ax2548:displayOrder>8</ax2548:displayOrder>\n"
            + "               <ax2548:fieldValue xsi:nil=\"true\"/>\n"
            + "               <ax2548:readOnly>false</ax2548:readOnly>\n"
            + "               <ax2548:regEx xsi:nil=\"true\"/>\n"
            + "               <ax2548:required>false</ax2548:required>\n" + "            </ax2548:fieldValues>\n"
            + "            <ax2548:fieldValues xsi:type=\"ax2548:UserFieldDTO\">\n"
            + "               <ax2548:checkedAttribute>false</ax2548:checkedAttribute>\n"
            + "               <ax2548:claimUri>http://wso2.org/claims/lastname</ax2548:claimUri>\n"
            + "               <ax2548:displayName>Last Name</ax2548:displayName>\n"
            + "               <ax2548:displayOrder>2</ax2548:displayOrder>\n"
            + "               <ax2548:fieldValue xsi:nil=\"true\"/>\n"
            + "               <ax2548:readOnly>false</ax2548:readOnly>\n"
            + "               <ax2548:regEx xsi:nil=\"true\"/>\n"
            + "               <ax2548:required>true</ax2548:required>\n" + "            </ax2548:fieldValues>\n"
            + "            <ax2548:fieldValues xsi:type=\"ax2548:UserFieldDTO\">\n"
            + "               <ax2548:checkedAttribute>false</ax2548:checkedAttribute>\n"
            + "               <ax2548:claimUri>http://wso2.org/claims/department</ax2548:claimUri>\n"
            + "               <ax2548:displayName>Department</ax2548:displayName>\n"
            + "               <ax2548:displayOrder>0</ax2548:displayOrder>\n"
            + "               <ax2548:fieldValue xsi:nil=\"true\"/>\n"
            + "               <ax2548:readOnly>true</ax2548:readOnly>\n"
            + "               <ax2548:regEx xsi:nil=\"true\"/>\n"
            + "               <ax2548:required>false</ax2548:required>\n" + "            </ax2548:fieldValues>\n"
            + "            <ax2548:fieldValues xsi:type=\"ax2548:UserFieldDTO\">\n"
            + "               <ax2548:checkedAttribute>false</ax2548:checkedAttribute>\n"
            + "               <ax2548:claimUri>http://wso2.org/claims/im</ax2548:claimUri>\n"
            + "               <ax2548:displayName>IM</ax2548:displayName>\n"
            + "               <ax2548:displayOrder>9</ax2548:displayOrder>\n"
            + "               <ax2548:fieldValue xsi:nil=\"true\"/>\n"
            + "               <ax2548:readOnly>false</ax2548:readOnly>\n"
            + "               <ax2548:regEx xsi:nil=\"true\"/>\n"
            + "               <ax2548:required>false</ax2548:required>\n" + "            </ax2548:fieldValues>\n"
            + "            <ax2548:fieldValues xsi:type=\"ax2548:UserFieldDTO\">\n"
            + "               <ax2548:checkedAttribute>false</ax2548:checkedAttribute>\n"
            + "               <ax2548:claimUri>http://wso2.org/claims/emailaddress</ax2548:claimUri>\n"
            + "               <ax2548:displayName>Email</ax2548:displayName>\n"
            + "               <ax2548:displayOrder>6</ax2548:displayOrder>\n"
            + "               <ax2548:fieldValue xsi:nil=\"true\"/>\n"
            + "               <ax2548:readOnly>false</ax2548:readOnly>\n"
            + "               <ax2548:regEx>^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})"
            + "+$</ax2548:regEx>\n"
            + "               <ax2548:required>true</ax2548:required>\n" + "            </ax2548:fieldValues>\n"
            + "            <ax2548:fieldValues xsi:type=\"ax2548:UserFieldDTO\">\n"
            + "               <ax2548:checkedAttribute>false</ax2548:checkedAttribute>\n"
            + "               <ax2548:claimUri>http://wso2.org/claims/organization</ax2548:claimUri>\n"
            + "               <ax2548:displayName>Organization</ax2548:displayName>\n"
            + "               <ax2548:displayOrder>3</ax2548:displayOrder>\n"
            + "               <ax2548:fieldValue xsi:nil=\"true\"/>\n"
            + "               <ax2548:readOnly>false</ax2548:readOnly>\n"
            + "               <ax2548:regEx xsi:nil=\"true\"/>\n"
            + "               <ax2548:required>false</ax2548:required>\n" + "            </ax2548:fieldValues>\n"
            + "            <ax2548:fieldValues xsi:type=\"ax2548:UserFieldDTO\">\n"
            + "               <ax2548:checkedAttribute>false</ax2548:checkedAttribute>\n"
            + "               <ax2548:claimUri>http://wso2.org/claims/url</ax2548:claimUri>\n"
            + "               <ax2548:displayName>URL</ax2548:displayName>\n"
            + "               <ax2548:displayOrder>10</ax2548:displayOrder>\n"
            + "               <ax2548:fieldValue xsi:nil=\"true\"/>\n"
            + "               <ax2548:readOnly>false</ax2548:readOnly>\n"
            + "               <ax2548:regEx xsi:nil=\"true\"/>\n"
            + "               <ax2548:required>false</ax2548:required>\n" + "            </ax2548:fieldValues>\n"
            + "            <ax2548:fieldValues xsi:type=\"ax2548:UserFieldDTO\">\n"
            + "               <ax2548:checkedAttribute>false</ax2548:checkedAttribute>\n"
            + "               <ax2548:claimUri>http://wso2.org/claims/telephone</ax2548:claimUri>\n"
            + "               <ax2548:displayName>Telephone</ax2548:displayName>\n"
            + "               <ax2548:displayOrder>7</ax2548:displayOrder>\n"
            + "               <ax2548:fieldValue xsi:nil=\"true\"/>\n"
            + "               <ax2548:readOnly>false</ax2548:readOnly>\n"
            + "               <ax2548:regEx xsi:nil=\"true\"/>\n"
            + "               <ax2548:required>false</ax2548:required>\n" + "            </ax2548:fieldValues>\n"
            + "            <ax2548:fieldValues xsi:type=\"ax2548:UserFieldDTO\">\n"
            + "               <ax2548:checkedAttribute>false</ax2548:checkedAttribute>\n"
            + "               <ax2548:claimUri>http://wso2.org/claims/givenname</ax2548:claimUri>\n"
            + "               <ax2548:displayName>First Name</ax2548:displayName>\n"
            + "               <ax2548:displayOrder>1</ax2548:displayOrder>\n"
            + "               <ax2548:fieldValue xsi:nil=\"true\"/>\n"
            + "               <ax2548:readOnly>false</ax2548:readOnly>\n"
            + "               <ax2548:regEx xsi:nil=\"true\"/>\n"
            + "               <ax2548:required>true</ax2548:required>\n" + "            </ax2548:fieldValues>\n"
            + "            <ax2548:fieldValues xsi:type=\"ax2548:UserFieldDTO\">\n"
            + "               <ax2548:checkedAttribute>false</ax2548:checkedAttribute>\n"
            + "               <ax2548:claimUri>http://wso2.org/claims/role</ax2548:claimUri>\n"
            + "               <ax2548:displayName>Role</ax2548:displayName>\n"
            + "               <ax2548:displayOrder>0</ax2548:displayOrder>\n"
            + "               <ax2548:fieldValue>Internal/everyone,admin,Application/apim_publisher,"
            + "Application/admin_pizzaApp_PRODUCTION,Application/rest_api_import_export,Application/scim_app,"
            + "Internal/subscriber,Application/admin_test2_PRODUCTION,Application/User Portal,Internal/creator,"
            + "Application/apim_devportal,Application/admin_scim_PRODUCTION,Application/new12,Internal/publisher"
            + ",Internal/analytics,Application/sp_analytics_dashboard</ax2548:fieldValue>\n"
            + "               <ax2548:readOnly>true</ax2548:readOnly>\n"
            + "               <ax2548:regEx xsi:nil=\"true\"/>\n"
            + "               <ax2548:required>false</ax2548:required>\n" + "            </ax2548:fieldValues>\n"
            + "            <ax2548:profileConifuration>default</ax2548:profileConifuration>\n"
            + "            <ax2548:profileName>default</ax2548:profileName>\n" + "         </ns:return>\n"
            + "      </ns:getUserProfileResponse>\n" + "   </soapenv:Body>\n" + "</soapenv:Envelope>";
}

class NamespaceResolver implements NamespaceContext {
    private Document sourceDocument;

    public NamespaceResolver(Document document) {
        sourceDocument = document;
    }

    public String getNamespaceURI(String prefix) {
        if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
            return sourceDocument.lookupNamespaceURI(null);
        } else {
            return "http://mgt.profile.user.identity.carbon.wso2.org/xsd";
        }
    }

    public String getPrefix(String namespaceURI) {
        return sourceDocument.lookupPrefix(namespaceURI);
    }

    @SuppressWarnings("rawtypes")
    public Iterator getPrefixes(String namespaceURI) {
        return null;
    }
}
