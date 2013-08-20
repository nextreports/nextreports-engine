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
package ro.nextreports.engine.querybuilder.sql.dialect;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 28-Sep-2009
// Time: 10:46:27

import ro.nextreports.engine.util.DialectUtil;

//
public class ConnectionUtil {

    private static final Log LOG = LogFactory.getLog(ConnectionUtil.class);

    public static boolean isValidConnection(Connection connection) {
        return isValidConnection(connection, null);
    }

    public static boolean isValidConnection(Connection connection, Dialect dialect) {

        if (connection == null) {
            return false;
        }

        if (dialect == null) {
            try {
                dialect = DialectUtil.getDialect(connection);
            } catch (Exception e) {
                e.printStackTrace();
                LOG.error(e.getMessage(), e);
                return false;
            }
        }
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(dialect.getSqlChecker());
            if (rs.next()) {
                return true; // connection is valid
            }
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.error(e.getMessage(), e);

        }
        finally {
            closeStatement(stmt);
            closeResultSet(rs);
        }
        return false;
    }

    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
