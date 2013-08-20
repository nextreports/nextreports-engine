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
package ro.nextreports.engine.util;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import ro.nextreports.engine.querybuilder.sql.dialect.Dialect;
import ro.nextreports.engine.querybuilder.sql.dialect.DialectException;
import ro.nextreports.engine.querybuilder.sql.dialect.DialectFactory;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Feb 3, 2009
 * Time: 12:17:51 PM
 */
public class DialectUtil {

    public static Dialect getDialect(Connection connection) throws SQLException, DialectException {
        DatabaseMetaData dbmd = connection.getMetaData();
        String dbName = dbmd.getDatabaseProductName();
        String dbVersion = dbmd.getDatabaseProductVersion();
        return DialectFactory.determineDialect(dbName, dbVersion);
    }
    
    public static boolean isSupportedResultSetType(Connection connection, int resultSetType) throws SQLException {
    	DatabaseMetaData dbmd = connection.getMetaData();
    	return dbmd.supportsResultSetType(resultSetType);
    }
    
    // CsvJdbc driver className is not a full java class name
 	public static String getFullColumnClassName(String className) {
 		if (className.startsWith("java.")) {
 			return className;
 		}
 		if ("String".equals(className)) {
 			return "java.lang.String";
 		} else if ("Int".equals(className)) {
 			return "java.lang.Integer";
 		} else if ("Double".equals(className)) {
 			return "java.lang.Double";
 		} else if ("Date".equals(className)) {
 			return "java.util.Date";
 		} else if ("Long".equals(className)) {
 			return "java.lang.Long";
 		} else if ("Float".equals(className)) {
 			return "java.lang.Float";
 		} else if ("Boolean".equals(className)) {
 			return "java.lang.Boolean";
 		}  else {
 			return "java.lang.Object";
 		}
 	}
}
