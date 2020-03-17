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

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Main {


    public static void main1(String[] args) throws Exception {
        FileInputStream fis = new FileInputStream(
                "/Users/rukshan/wso2/apim/3.1.0/testing/target/dashboard/wso2/dashboard/resources/dashboards/apimpublisher.json");
        String payload = IOUtils.toString(fis, "UTF-8");

        FileInputStream fis1 = new FileInputStream(
                "/Users/rukshan/wso2/apim/3.1.0/testing/target/dashboard/wso2/dashboard/resources/dashboards/apimpublisher.json.imported");
        String payload1 = IOUtils.toString(fis1, "UTF-8");
        System.out.println(payload.hashCode());
        System.out.println(payload1.hashCode());

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] encodedhash = digest.digest(payload.getBytes(StandardCharsets.UTF_8));
        String hash = bytesToHex(encodedhash);
        System.out.println(hash);

        byte[] encodedhash1 = digest.digest(payload1.getBytes(StandardCharsets.UTF_8));
        String hash1 = bytesToHex(encodedhash1);
        System.out.println(hash1);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static void main2(String[] args) {
        check("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/APIM_ANALYTICS_DB", "root", "pass");
        check("com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://localhost;database=analyticsdb", "analyticsdb", "analyticsdb");
        check("oracle.jdbc.OracleDriver", "jdbc:oracle:thin:@localhost:1521:XE", "analyticsDb", "analyticsDb");
        check("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/analyticsdb", "analyticsdb", "analyticsdb");

        check("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/APIM_ANALYTICS_DB_exist", "root", "pass");
        check("com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://localhost;database=analyticsdb_exist", "analyticsdb_exist", "analyticsdb_exist");
        check("oracle.jdbc.OracleDriver", "jdbc:oracle:thin:@localhost:1521:XE", "analyticsdb_exist", "analyticsdb_exist");
        check("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/analyticsdb_exist", "analyticsdb_exist", "analyticsdb_exist");

    }

    public static void main(String[] args) throws Exception {
        System.out.println(Level.WSO2_COM);
    }
    public static void check(String driver, String url, String user, String pass) {
        Connection con = null;
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, pass);
            System.out.println(isUsageTableExist(con));
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if(con!=null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean isUsageTableExist(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        try {
            stmt.execute(TABLE_EXISTENCE_SQL);
            return true;
        } catch (SQLException e) {
//            System.out.println(e.getMessage());
            return false;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public static final String TABLE_EXISTENCE_SQL = "SELECT 1 FROM AM_USAGE_UPLOADED_FILES";
}

enum Level {
    WSO2_COM("asas");

    private String name;

    Level(String name) {
        this.name = name;
    }

//    public String getName() {
//        return this.name;
//    }

    @Override
    public String toString() {
        List<String> asd = new ArrayList<>();
        return this.name;
    }

}