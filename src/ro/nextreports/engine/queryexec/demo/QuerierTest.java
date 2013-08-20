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
import java.sql.SQLException;

import ro.nextreports.engine.queryexec.Query;
import ro.nextreports.engine.queryexec.QueryResult;


/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Sep 18, 2006
 * Time: 2:01:39 PM
 */
public class QuerierTest {
    //Provide your values for all static variables below


    private static final String SQL_QUERY =
            "SELECT S1.FIRST_NAME, S1.LAST_NAME,  S1.BORN_DATE," +
                    " C1.CITY_NAME,  S2.STREET,  S2.STREET_NO FROM  " +
                    "SUBSCRIBERS S1, CITIES C1, SUBSCRIBER_ADDRESSES S2 " +
                    "WHERE  S1.SUBSCRIBER_ID = S2.SUBSCRIBER_ID AND " +
                    "S2.CITY_ID = C1.CITY_ID ORDER BY  S1.FIRST_NAME";

    public QuerierTest() {
        Connection conn = null;
        try {

            conn = openOracleConnection();
            Query query = new Query(SQL_QUERY);
            final SimpleQueryExecutor executor = new SimpleQueryExecutor(query, conn);           
            Thread thread = new Thread() {
                public void run() {
                    String threadName = Thread.currentThread().getName();
                    QueryResult result  = null;
                    try {
                        result = executor.execute();

                        //System.out.println("rows=" + result.getRowCount());

                        System.out.println("Thread " + threadName + " completed");
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("\tInterruptedException in thread " +
                                threadName);
                    } finally {
                    	if (result != null) {
                    		result.close();
                    	}
                    }
                }
            };


            System.out.println("Thread created");
            thread.start();
            System.out.println("Threads started");


            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();  
            }

            System.out.println("Call interrupt ...");

            thread.interrupt();
            System.out.println("Thread interrupted");


            try {
                thread.join();
            } catch (InterruptedException e) {
            }

            System.out.println("Thread terminated");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public static Connection openOracleConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (java.lang.ClassNotFoundException e) {
            throw new SQLException("Cannot load database driver");
        }
        String url = "jdbc:oracle:thin:@hornet1001.intranet.asf.ro:1521:BANKING";
        return DriverManager.getConnection(url, "capone", "banking");
    }


    public static void main(String[] args) {
        new QuerierTest();
    }

}
