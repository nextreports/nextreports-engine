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
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Sep 24, 2008
 * Time: 3:59:13 PM
 */
public class DerbyDialect extends AbstractDialect {

    public DerbyDialect() {
    	super();
        registerColumnType("bigint", Types.BIGINT);
        registerColumnType("smallint", Types.SMALLINT);
        registerColumnType("integer", Types.INTEGER);
        registerColumnType("double", Types.DOUBLE);
        registerColumnType("char", Types.CHAR);
        registerColumnType("varchar", Types.VARCHAR);
        registerColumnType("date", Types.DATE);
        registerColumnType("timestamp", Types.TIMESTAMP);
        registerColumnType("blob", Types.BLOB);
        registerColumnType("clob", Types.CLOB);
        registerColumnType("boolean", Types.BOOLEAN);
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
        return "select current_date from sysibm.sysdummy1";
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
        return "values(1)";
    }

}
