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
 * @author Decebal Suiu
 */
public class FirebirdDialect extends AbstractDialect {

    public FirebirdDialect() {
    	super();
    	registerColumnType("smallint", Types.SMALLINT);
    	registerColumnType("numeric(18, 0)", Types.BIGINT);
    	registerColumnType("integer", Types.INTEGER);
    	registerColumnType("char(1, *)", Types.BIT);
    	registerColumnType("char(>1, *)", Types.VARCHAR);
    	registerColumnType("varchar", Types.VARCHAR);
    	registerColumnType("float", Types.FLOAT);
    	registerColumnType("double", Types.DOUBLE);
    	registerColumnType("double precision", Types.DOUBLE);
    	registerColumnType("date", Types.DATE);
        registerColumnType("decimal", Types.INTEGER);
    	registerColumnType("time", Types.TIME);
    	registerColumnType("timestamp", Types.TIMESTAMP);
    	registerColumnType("numeric", Types.NUMERIC);
    	registerColumnType("blob", Types.BLOB);
    	registerColumnType("blob sub_type 1", Types.CLOB);
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
        return "select current_date from rdb$database;";
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
        return "select 1 from rdb$database;";
    }
    
    public boolean needsHoldCursorsForPreparedStatement() {
    	return true;
    }

    protected void setKeywords() {
        keywords = new String[] {
              "ACTION", "ACTIVE", "ADD", "ADMIN", "AFTER", "ALL", "ALTER", "AND", "ANY", "ARE",
              "AS", "ASC", "ASCENDING", "AT", "AUTO", "AUTODDL", "AVG", "BASED", "BASENAME",
              "BASE_NAME", "BEFORE", "BEGIN", "BETWEEN", "BLOB", "BLOBEDIT", "BUFFER",
              "BY", "CACHE", "CASCADE", "CAST", "CHAR", "CHARACTER", "CHAR_LENGTH",
              "CHARACTER_LENGTH", "CHECK", "CHECK_POINT_LEN", "CHECK_POINT_LENGTH", "CLOSE",
              "COLLATE", "COLLATION", "COLUMN", "COMMIT", "COMMITTED", "COMPILETIME", "COMPUTED",
              "CONDITIONAL", "CONNECT", "CONSTRAINT",  "CONTAINING", "CONTINUE", "COUNT", "CREATE",
              "CSTRING", "CURRENT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP",  "DATABASE",
              "DATE", "DAY", "DB_KEY", "DEBUG", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELETE", "DESC",
              "DESCENDING", "DESCRIBE", "DESCRIPTOR", "DISCONNECT", "DISPLAY", "DISTINCT", "DO", "DOMAIN",
              "DOUBLE", "DROP", "ECHO", "EDIT", "ELSE", "END", "ENTRY_POINT", "ESCAPE", "EVENT", "EXCEPTION",
              "EXECUTE", "EXISTS", "EXIT", "EXTERN", "EXTERNAL", "EXTRACT", "FETCH", "FILE", "FILTER",
              "FLOAT", "FOR", "FOREIGN", "FOUND", "FREE_IT", "FROM", "FULL", "FUNCTION", "GDSCODE",
              "GENERATOR", "GEN_ID", "GLOBAL", "GOTO", "GRANT", "GROUP", "GROUP_COMMIT_WAIT",
              "GROUP_COMMIT_WAIT_TIME", "HAVING","HELP", "HOUR", "IF", "IMMEDIATE", "IN", "INACTIVE",
              "INDEX", "INDICATOR", "INIT", "INNER", "INPUT", "INPUT_TYPE", "INSERT", "INT", "INTEGER",
              "INTO", "IS", "ISOLATION", "ISQL", "JOIN", "KEY", "LC_MESSAGES", "LC_TYPE", "LEFT", "LENGTH",
              "LEV", "LEVEL", "LIKE", "LOGFILE", "LOG_BUFFER_SIZE", "LOG_BUF_SIZE", "LONG", "MANUAL",
              "MAX", "MAXIMUM", "MAXIMUM_SEGMENT", "MAX_SEGMENT", "MERGE", "MESSAGE", "MIN", "MINIMUM",
              "MINUTE", "MODULE_NAME", "MONTH", "NAMES", "NATIONAL", "NATURAL", "NCHAR", "NO", "NOAUTO",
              "NOT", "NULL", "NUM_LOG_BUFS", "NUM_LOG_BUFFERS", "NUMERIC", "OCTET_LENGTH", "OF", "ON", "ONLY",
              "OPEN", "OPTION", "OR", "ORDER", "OUTER", "OUTPUT", "OUTPUT_TYPE", "OVERFLOW", "PAGE",
              "PAGELENGTH", "PAGES", "PAGE_SIZE", "PARAMETER", "PASSWORD", "PLAN", "POSITION", "POST_EVENT",
              "PRECISION", "PREPARE", "PRIMARY", "PRIVILEGES", "PROCEDURE", "PUBLIC", "QUIT", "RAW_PARTITIONS",
              "RDB$DB_KEY", "READ", "REAL", "RECORD_VERSION", "REFERENCES", "RELEASE", "RESERV", "RESERVING",
              "RESTRICT", "RETAIN", "RETURN", "RETURNING_VALUES", "RETURNS", "REVOKE", "RIGHT", "ROLE", "ROLLBACK",
              "RUNTIME", "SCHEMA", "SECOND", "SELECT", "SET", "SHADOW", "SHARED", "SHELL", "SHOW", "SINGULAR",
              "SIZE", "SMALLINT", "SNAPSHOT", "SOME", "SORT", "SQL", "SQLCODE", "SQLERROR", "SQLWARNING",
              "STABILITY", "STARTING", "STARTS", "STATEMENT", "STATIC", "STATISTICS", "SUB_TYPE", "SUM",
              "SUSPEND", "TABLE", "TERMINATOR", "THEN", "TIME", "TIMESTAMP", "TO", "TRANSACTION", "TRANSLATE",
              "TRANSLATION", "TRIGGER", "TRIM", "TYPE", "UNCOMMITTED", "UNION", "UNIQUE", "UPDATE", "UPPER",
              "USER", "USING", "VALUE", "VALUES", "VARCHAR", "VARIABLE", "VARYING", "VERSION", "VIEW",
              "WAIT", "WEEKDAY", "WHEN", "WHENEVER", "WHILE", "WITH", "WORK", "WRITE", "YEAR", "YEARDAY"
        };
    }

    public String getEscapedKeyWord(String keyword) {
        if (keyword == null) {
            throw new IllegalArgumentException("Keyword cannot be null!");
        }
        return "\"" + keyword + "\"";
    }
}
