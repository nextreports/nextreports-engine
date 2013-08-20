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

import java.sql.Types;

import ro.nextreports.engine.util.ProcUtil;

/**
 * Vertica Dialect
 * 
 * @author Mihai Dinca-Panaitescu
 * @date 24.07.2013
 */
public class VerticaDialect extends AbstractDialect {

    public VerticaDialect() {
    	super();
    	registerColumnType("binary", Types.BLOB);
    	registerColumnType("varbinary", Types.BLOB);
    	registerColumnType("bytea", Types.BLOB);
    	registerColumnType("raw", Types.BLOB);
    	registerColumnType("boolean", Types.BOOLEAN);
    	registerColumnType("char", Types.CHAR);
        registerColumnType("varchar", Types.VARCHAR);
        registerColumnType("date", Types.DATE);
        registerColumnType("timestamp", Types.TIMESTAMP);
        registerColumnType("timestamp with timezone", Types.TIMESTAMP);
        registerColumnType("datetime", Types.TIMESTAMP);
        registerColumnType("smalldatetime", Types.TIMESTAMP);
        registerColumnType("double precision", Types.DOUBLE);
        registerColumnType("float", Types.FLOAT);
        registerColumnType("float8", Types.FLOAT);
        registerColumnType("real", Types.DOUBLE);        
        registerColumnType("bigint", Types.BIGINT);
        registerColumnType("smallint", Types.SMALLINT);
        registerColumnType("integer", Types.INTEGER);
        registerColumnType("int", Types.INTEGER);
        registerColumnType("tinyint", Types.INTEGER);
        registerColumnType("int8", Types.INTEGER);
        registerColumnType("decimal", Types.INTEGER);                
        registerColumnType("numeric", Types.NUMERIC);
        registerColumnType("number", Types.NUMERIC);
        registerColumnType("money", Types.NUMERIC);
        registerColumnType("time", Types.TIME);
        registerColumnType("time with timezone", Types.TIME);   
        registerColumnType("interval", Types.TIME);
    }

    public String getCurrentDate() throws DialectException {
        return "current_date";
    }
    
    public String getCurrentTimestamp() throws DialectException {
    	return "current_timestamp";
    }
    
    public String getCurrentTime() throws DialectException {
    	return "current_time";
    }

    public String getCurrentDateSelect() {
        return "select current_date";
    }

    public String getRecycleBinTablePrefix() {
        return null;
    }

    public String getCursorSqlTypeName() {
        return ProcUtil.REF_CURSOR;
    }

    public int getCursorSqlType() {
        return Types.OTHER;
    }

    public String getSqlChecker() {
        return "select 1";
    }

}

