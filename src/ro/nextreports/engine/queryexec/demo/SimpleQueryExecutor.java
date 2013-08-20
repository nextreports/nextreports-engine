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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ro.nextreports.engine.queryexec.Query;
import ro.nextreports.engine.queryexec.QueryException;
import ro.nextreports.engine.queryexec.QueryResult;


/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Sep 18, 2006
 * Time: 2:36:44 PM 
 */
public class SimpleQueryExecutor implements Runnable {

    public static final int DEFAULT_TIMEOUT = 20;
    public static final int DEFAULT_MAX_ROWS = 100;

    private Query query;
    private Connection conn;

    private Thread worker;
    private final InputWrapper inputWrapper;
    private final ResultWrapper resultWrapper;
    private volatile boolean cancelRequest;
    private volatile boolean closeRequest;

    public SimpleQueryExecutor(Query query, Connection conn) {

        this.query = query;
        this.conn = conn;

        inputWrapper = new InputWrapper();
        resultWrapper = new ResultWrapper();

        worker = new Thread(this);
        worker.start();
    }

    /**
     * Execute the query.
     */
    public synchronized QueryResult execute() throws QueryException, InterruptedException {
        // create query string
        String queryString = query.getText();
        System.out.println("queryString = " + queryString);

        // count statement
        String countQueryString = "SELECT COUNT(*) FROM (" + queryString + ")";


        // set query's input wrapper
        synchronized (inputWrapper) {
            try {
                inputWrapper.statement = conn.createStatement();
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                return null;
            }
            inputWrapper.query = queryString;
            try {
                inputWrapper.countStatement = conn.createStatement();
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                return null;
            }
            inputWrapper.countQuery = countQueryString;
            inputWrapper.pending = true;
            inputWrapper.notify();
        }

        synchronized (resultWrapper) {
            try {
                // wait for the query to complete
                while (!resultWrapper.serviced) {
                    resultWrapper.wait();
                }
                if (resultWrapper.exception != null) {
                    throw resultWrapper.exception;
                }
            } catch (InterruptedException e) {
                cancel();
                throw e;
            } finally {
                resultWrapper.serviced = false;
            }

            return new QueryResult(resultWrapper.resultSet, resultWrapper.count, resultWrapper.executeTime);
        }
    }

    private void cancel() {
        cancelRequest = true;
        try {
            System.out.println("### Cancel start ...");
            inputWrapper.statement.cancel();
            System.out.println("### Cancel stop.");
            synchronized(resultWrapper) {
                while(!resultWrapper.serviced) {
                    resultWrapper.wait();
                }
            }
        } catch (SQLException e) {
            //nothing to do
        } catch (InterruptedException e) {
            //nothing to do
        } finally {
            cancelRequest = false;
        }
    }

    public void stop() {
        closeRequest = true;
        if (inputWrapper.statement != null) {
            cancel();
        }
        worker.interrupt();
        try {
            worker.join();
        } catch (InterruptedException e) {
        }
    }

    public void run() {
        ResultSet resultSet = null;
        SQLException sqlException = null;
        int count = 0;
        while(!closeRequest) {
            long executeTime = 0;
            synchronized(inputWrapper) {
                try {
                    // wait for query parameters
                    while(!inputWrapper.pending) {
                        inputWrapper.wait();
                    }
                    inputWrapper.pending = false;
                } catch (InterruptedException e) {
                    if (closeRequest) {
                        return;
                    }
                }
                // execute query
                try {
                    executeTime = System.currentTimeMillis();
                    System.out.println(">> query = " + inputWrapper.query);
                    resultSet = inputWrapper.statement.executeQuery(inputWrapper.query);
//                    while(resultSet.next()) {
//                        System.out.println(":::::::: " + resultSet.getRow());
//                    }
                    System.out.println(">> countQuery = " + inputWrapper.countQuery);
                    ResultSet countResultSet = inputWrapper.countStatement.executeQuery(inputWrapper.countQuery);
                    countResultSet.next();
                    count = countResultSet.getInt(1);
                    System.out.println(">>HERE count="+count);
                    executeTime = System.currentTimeMillis() - executeTime;
                } catch (SQLException e) {
                    if (!cancelRequest) {
                        sqlException = e;
                    }
                }
            }

            // set query resultWrapper
            synchronized (resultWrapper) {
                resultWrapper.resultSet = resultSet;
                resultWrapper.count = count;
                resultWrapper.exception = (sqlException == null) ? null : new QueryException(sqlException);
                resultWrapper.serviced = true;
                resultWrapper.executeTime = executeTime;
                resultWrapper.notify();
            }
        }
    }

    class InputWrapper {

        public Statement statement;
        public Statement countStatement;
        public String query;
        public String countQuery;
        public boolean pending;

    }

    class ResultWrapper {

        public ResultSet resultSet;
        public int count;
        public QueryException exception;
        public boolean serviced;
        public long executeTime;

    }

}
