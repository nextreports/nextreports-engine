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

/**
 * Represents a dialect of SQL implemented by a particular RDBMS.
 * 
 * @author Decebal Suiu
 */
public interface Dialect {

    /**
     * Get <tt>java.sql.Types</tt> typecode of the column database associated 
     * with the given sql type, precision and scale.
     * 
     * @param type      sql type
     * @param precision the precision of the column
     * @param scale the scale of the column
     *
     * @return the column typecode
     * @throws DialectException
     */
    public int getJdbcType(String type, int precision, int scale) throws DialectException;
    
    /**
     * Get java class name of the column database associated 
     * with the given sql type, precision and scale.
     * 
     * @param type      sql type
     * @param precision the precision of the column
     * @param scale the scale of the column
     * 
     * @return the java class name
     * @throws DialectException
     */
    public String getJavaType(String type, int precision, int scale) throws DialectException;

    public String getCurrentDate() throws DialectException;
    
    public String getCurrentTimestamp() throws DialectException;
    
    public String getCurrentTime() throws DialectException;

    public String getCurrentDateSelect();

    public String getRecycleBinTablePrefix();

    public String getCursorSqlTypeName();

    public int getCursorSqlType();

    public boolean hasProcedureWithCursor();

    public boolean schemaBeforeCatalog();

    public boolean isKeyWord(String word);

    public String getEscapedKeyWord(String keyword);

    /**
     * Get a select used to check the connection is valid
     * @return
     */
    public String getSqlChecker();
    
    // Firebird needs it
    // when using several statements on the same connection object or several result sets on the same statement object 
    //     -> a "resultSet is closed" exception is raised
    // we should create a prepared statement with ResultSet.HOLD_CURSORS_OVER_COMMIT flag
    // http://tech.groups.yahoo.com/group/firebird-support/message/107922
    public boolean needsHoldCursorsForPreparedStatement();
    
}
