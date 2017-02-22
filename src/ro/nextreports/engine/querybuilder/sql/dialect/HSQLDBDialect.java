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


public class HSQLDBDialect extends AbstractDialect {

    public HSQLDBDialect() {
    	super();
        registerColumnType("integer", Types.INTEGER);
        registerColumnType("text", Types.VARCHAR);
        registerColumnType("numeric", Types.NUMERIC);
        registerColumnType("real", Types.DOUBLE);  
        registerColumnType("blob", Types.BLOB);
        
        registerColumnType("bit", Types.BIT);
        registerColumnType("tinyint", Types.TINYINT);
        registerColumnType("smallint", Types.SMALLINT);        
        registerColumnType("bigint", Types.BIGINT);
        registerColumnType("float", Types.FLOAT);
        registerColumnType("real", Types.REAL);
        registerColumnType("double", Types.DOUBLE);        
        registerColumnType("decimal", Types.DECIMAL);
        registerColumnType("char", Types.CHAR);
        registerColumnType("character", Types.VARCHAR);
        registerColumnType("varchar", Types.VARCHAR);
        registerColumnType("longvarchar", Types.LONGVARCHAR);
        registerColumnType("date", Types.DATE);
        registerColumnType("time", Types.TIME);
        registerColumnType("timestamp", Types.TIMESTAMP);        
        registerColumnType("clob", Types.CLOB);
        registerColumnType("boolean", Types.BOOLEAN);
    }

    public String getCurrentDate() throws DialectException {
        return "CURRENT_DATE";
    }

    public String getCurrentDateSelect() {
    	return "SELECT CURRENT_DATE FROM (VALUES(0))";        
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
        return "SELECT 1";
    }

}
