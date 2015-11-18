/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ro.nextreports.engine.queryexec.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import ro.nextreports.engine.queryexec.Query;
import ro.nextreports.engine.queryexec.QueryExecutor;
import ro.nextreports.engine.queryexec.QueryParameter;
import ro.nextreports.engine.queryexec.QueryResult;
import ro.nextreports.engine.queryexec.util.QueryResultPrinter;
import ro.nextreports.engine.queryexec.util.SqlFile;
import ro.nextreports.engine.queryexec.util.StringUtil;


/**
 * @author Decebal Suiu
 */
public class QueryExecDemo {

    private static Connection conn;
    
    public static void main(String[] args) {   
    	QueryExecutor executor = null;
        try {
            SqlFile sqlFile = new SqlFile("demo.sql");
            String sql = sqlFile.getSqlList().get(0);
            System.out.println("=== sql ===");
            System.out.println(sql);
            
            sql = format(sql);
            System.out.println();
            System.out.println("=== sql after format ===");
            System.out.println(sql);

            Query query = new Query(sql);
            String[] parameterNames = query.getParameterNames();
            printParameterNames(parameterNames);
            
            Connection conn = getConnection();
            Map<String,QueryParameter> parameters = new HashMap<String,QueryParameter>();
//            QueryParameter param = new QueryParameter("clientId", "", QueryParameter.INTEGER_VALUE);
            QueryParameter param = new QueryParameter("name", "", QueryParameter.STRING_VALUE);
            parameters.put(param.getName(), param);
            Map<String,Object> values = new HashMap<String,Object>();
//            values.put(param.getName(), new Integer(1000));
            values.put(param.getName(), "M%");
            executor = new QueryExecutor(query, parameters, values, conn);
            QueryResult result = executor.execute();
            System.out.println("columns = " + result.getColumnCount());
            //System.out.println("rows = " + result.getRowCount());
            QueryResultPrinter.printResult(result);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
        	if (executor !=  null) {
        		executor.close();
        	}
        }
    }

    private static void printParameterNames(String[] parametersNames) {
        System.out.println("Parmaeter names:");
        for (int i = 0; i < parametersNames.length; i++) {
            System.out.println(parametersNames[i]);
        }
    }
    
    private static String format(String sql) {
        sql = StringUtil.stripSingleLineComments(sql);
        sql = StringUtil.stripMultiLineComments(sql);
        sql = StringUtil.stripBlankLines(sql);
        sql = StringUtil.trimLines(sql);
        sql = StringUtil.replaceTabsWithSpaces(sql, 1);
        sql = StringUtil.stripNewlines(sql);
        return sql;
    }

    private static Connection getConnection() throws Exception {
        if (conn == null) {
            // load the JDBC driver
            String driverName = "oracle.jdbc.driver.OracleDriver";
            Class.forName(driverName);

            // Create a conn to the database
//            String serverName = "192.168.16.205";
            String serverName = "192.168.12.14";
            String portNumber = "1521";
//            String sid = "ksi";
            String sid = "dev1";
            String url = "jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + sid;
//            String username = "Kollecto";
            String username = "misdev1";
//            String password = "ksi";
            String password = "misdev1";
            conn = DriverManager.getConnection(url, username, password);
        }

        return conn;
    }
    
}
