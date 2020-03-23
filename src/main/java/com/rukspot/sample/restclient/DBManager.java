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

import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBManager {
    String amHome = "/Users/rukshan/wso2/apim/301/active-active/wso2am-3.1.0-SNAPSHOT";

    public static void main2(String[] args) {
        String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        String DB_URL = "jdbc:mysql://localhost:3306";
        String USER = "root";
        String PASS = "pass";
        String amHome = "/Users/rukshan/wso2/apim/301/active-active/wso2am-3.1.0-SNAPSHOT";
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            stmt.addBatch("drop database amdb_1");
            stmt.addBatch("create database amdb_1");
            stmt.addBatch("use amdb_1");
            stmt.addBatch("source " + amHome + "/dbscripts/apimgt/mysql.sql;");

            stmt.addBatch("drop database sharedDB_1");
            stmt.addBatch("create database sharedDB_1");
            stmt.addBatch("use sharedDB_1");
            stmt.addBatch("source " + amHome + "/dbscripts/apimgt/mysql.sql;");

            stmt.addBatch("drop database APIM_ANALYTICS_DB_1");
            stmt.addBatch("create database APIM_ANALYTICS_DB_1");
            stmt.addBatch("SET GLOBAL max_connections = 500;");

            int[] rs = stmt.executeBatch();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void mysql() throws Exception {
        DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        String DB_URL = "jdbc:mysql://localhost:3306/APIM_ANALYTICS_DB_1?useSSL=false";
        String USER = "root";
        String PASS = "pass";
        String amHome = "/Users/rukshan/wso2/apim/301/active-active/wso2am-3.1.0-SNAPSHOT";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("AM_USAGE_UPLOADED_FILES : " + tableExist(conn, "AM_USAGE_UPLOADED_FILES"));
            System.out.println("APIEXETIME_DAYS : " + tableExist(conn, "APIEXETIME_DAYS"));
        } finally {
            conn.close();
        }
    }

    public static void mssql1() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("error " + e.getMessage());
        }
        String DB_URL = "jdbc:sqlserver://localhost:1433;database=amdb";
        String USER = "amdb";
        String PASS = "amdb";

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("AM_USAGE_UPLOADED_FILES : " + tableExist(conn, "AM_USAGE_UPLOADED_FILES"));
            System.out.println("API_LAST_ACCESS_TIME_SUMMARY : " + tableExist(conn, "API_LAST_ACCESS_TIME_SUMMARY"));

            Statement stmt = conn.createStatement();

            String sql = "select * from ApiVersionPerAppAgg_YEARS";
            try(ResultSet rs = stmt.executeQuery(sql)) {

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }

    public static void oracle() throws Exception {
        String JDBC_DRIVER = "oracle.jdbc.OracleDriver";
        String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
        String USER = "analyticsDb";
        String PASS = "analyticsDb";
        Connection conn = null;
        Statement stmt = null;
        try {
            String tf = "YYYY-MM-DD HH24:MI:SS";
            System.setProperty("NLS_DATE_FORMAT", "YYYY-MM-DD HH24:MI:SS");
            System.setProperty("oracle.net.NLS_DATE_FORMAT", "YYYY-MM-DD HH24:MI:SS");
            Class.forName(JDBC_DRIVER);
            Properties info = new Properties();
            info.setProperty("user", USER);
            info.setProperty("password", PASS);
            info.setProperty("NLS_DATE_FORMAT", tf);
            conn = DriverManager.getConnection(DB_URL, info);

            System.out.println("AM_USAGE_UPLOADED_FILES : " + tableExist(conn, "AM_USAGE_UPLOADED_FILES"));
            System.out.println("APIEXETIME_DAYS : " + tableExist(conn, "APIEXETIME_DAYS"));

            if (true) {
                return;
            }

            System.out.println("Creating statement...");
            stmt = conn.createStatement();

            String sql = "select CREATED_TIME from AM_API";
            stmt.execute("alter session set NLS_DATE_FORMAT='YYYY-MM-DD HH24:MI:SS'");
            sql =
                    "SELECT * FROM (select count(SUBSCRIBER_ID) as count, SUBSCRIBER_ID from AM_SUBSCRIBER where TENANT_ID = -1234 "
                            + "and CREATED_TIME between '2019-11-21 15:41:13' and '2019-11-28 15:41:13' group by SUBSCRIBER_ID ORDER BY SUBSCRIBER_ID DESC) WHERE ROWNUM <= 2147483647";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                //Retrieve by column name
                int time = rs.getInt("count");
                System.out.println("time " + time);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void postgre() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("error " + e.getMessage());
        }
        String DB_URL = "jdbc:postgresql://localhost:5432/analyticsdb";
        String USER = "analyticsdb";
        String PASS = "analyticsdb";

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("AM_USAGE_UPLOADED_FILES : " + tableExist(conn, "AM_USAGE_UPLOADED_FILES"));
            System.out.println("APIEXETIME_DAYS : " + tableExist(conn, "APIEXETIME_DAYS"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }

    private static boolean tableExist(Connection conn, String tableName) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "select 1 from " + tableName;
        try {
            boolean b = stmt.execute(sql);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static int findAppIDFromUUID(String uuid) throws Exception {
        if(uuid == null || uuid == "0" || uuid.isEmpty()) {
            return 0;
        }
        DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        String DB_URL = "jdbc:mysql://db.apim.com:3306/amdb?useSSL=false";
        String USER = "amuser";
        String PASS = "Pass@123";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "select APPLICATION_ID from AM_APPLICATION where UUID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, uuid);
            rs=stmt.executeQuery();
            while (rs.next()) {
                return rs.getInt(1);
            }
        } finally {
            if(conn != null) {
                conn.close();
            }
            if(stmt != null) {
                stmt.close();
            }
            if(rs != null) {
                rs.close();
            }
        }
        System.out.println("couldn't find the app ID for " + uuid);
        return -1;
    }

    public static void main(String[] args) throws Exception {
//        System.out.println("mysql");
//        mysql();
//        System.out.println();

//        System.out.println("oracle");
//        oracle();
//        System.out.println();

//        System.out.println("postgre");
//        postgre();
//        System.out.println();
        int a = findAppIDFromUUID("71f9d860-3ea8-4ccc-b12b-3be0062a8781");
        System.out.println(a);
    }
}
