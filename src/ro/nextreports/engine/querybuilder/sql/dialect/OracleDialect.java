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
 * An SQL dialect for Oracle.
 * 
 * @author Decebal Suiu
 */
public class OracleDialect extends AbstractDialect {

    // property used by oracle driver to allow for jdbc url of type :
    // jdbc:oracle:thin:@<database>
    // where <database> is the database name in the tnsnames.ora file
    public static String ORACLE_CLIENT_PROPERTY = "oracle.net.tns_admin";


    public OracleDialect() {
    	super();
        registerColumnType("bit", Types.BIT);
        registerColumnType("bigint", Types.INTEGER);
        registerColumnType("smallint", Types.SMALLINT);
        registerColumnType("tinyint", Types.TINYINT);
        registerColumnType("integer", Types.INTEGER);
        registerColumnType("int", Types.INTEGER);
        registerColumnType("float", Types.FLOAT);        
        registerColumnType("decimal", Types.INTEGER);        
        registerColumnType("double", Types.DOUBLE);
        registerColumnType("numeric", Types.NUMERIC);
        registerColumnType("number(*, <0)", Types.NUMERIC);
        registerColumnType("number(*, 0)", Types.INTEGER);
        registerColumnType("number(*, >0)", Types.DOUBLE);
        registerColumnType("char", Types.CHAR);
        registerColumnType("varchar", Types.VARCHAR);
        registerColumnType("varchar2", Types.VARCHAR);
        registerColumnType("date", Types.DATE);
        registerColumnType("datetime", Types.TIMESTAMP);        
        registerColumnType("timestamp", Types.TIMESTAMP);
        registerColumnType("blob", Types.BLOB);
        registerColumnType("varbinary", Types.BLOB);
        registerColumnType("clob", Types.CLOB);

//        registerColumnType("number(1, 0)", Types.BIT);
//        registerColumnType("number(19, 0)", Types.BIGINT);
//        registerColumnType("number(5, 0)", Types.SMALLINT);
//        registerColumnType("number(3, 0)", Types.TINYINT);
//        registerColumnType("number(10, 0)", Types.INTEGER);
//        registerColumnType("char(1, *)", Types.CHAR);
//        registerColumnType("number", Types.NUMERIC);
    }

    public String getCurrentDate() throws DialectException {
        return "sysdate";
    }

    public String getCurrentDateSelect() {
        return "select sysdate from dual";
    }

    public String getRecycleBinTablePrefix() {
        return "BIN$";
    }

    public String getCursorSqlTypeName() {
        return ProcUtil.REF_CURSOR;
    }

    public int getCursorSqlType() {
        return -10;  // OracleTypes.CURSOR
    }

    @Override
    public boolean hasProcedureWithCursor() {
        return true;
    }
    
    public String getSqlChecker() {
        return "SELECT 1 FROM DUAL";
    }

    protected void setKeywords() {
        keywords = new String[] {
            "ADMIN", "CURSOR", "FOUND", "MOUNT", "AFTER", "CYCLE", "FUNCTION", "NEXT",
            "ALLOCATE", "DATABASE", "GO", "NEW", "ANALYZE", "DATAFILE", "GOTO", "NOARCHIVELOG",
            "ARCHIVE", "DBA", "GROUPS", "NOCACHE", "ARCHIVELOG", "DEC", "INCLUDING", "NOCYCLE",
            "AUTHORIZATION", "DECLARE", "INDICATOR", "NOMAXVALUE", "AVG", "DISABLE", "INITRANS", "NOMINVALUE",
            "BACKUP",  "DISMOUNT", "INSTANCE", "NONE", "BEGIN", "DOUBLE", "INT", "NOORDER",
            "BECOME", "DUMP", "KEY", "NORESETLOGS", "BEFORE", "EACH", "LANGUAGE", "NORMAL",
            "BLOCK", "ENABLE", "LAYER", "NOSORT", "BODY", "END", "LINK", "NUMERIC",
            "CACHE", "ESCAPE", "LISTS", "OFF", "CANCEL", "EVENTS", "LOGFILE", "OLD",
            "CASCADE", "EXCEPT", "MANAGE", "ONLY", "CHANGE", "EXCEPTIONS", "MANUAL", "OPEN",
            "CHARACTER", "EXEC", "MAX", "OPTIMAL", "CHECKPOINT", "EXPLAIN", "MAXDATAFILES", "OWN",
            "CLOSE", "EXECUTE", "MAXINSTANCES", "PACKAGE", "COBOL", "EXTENT", "MAXLOGFILES", "PARALLEL",
            "COMMIT", "EXTERNALLY", "MAXLOGHISTORY", "PCTINCREASE", "COMPILE", "FETCH", "MAXLOGMEMBERS", "PCTUSED",
            "CONSTRAINT", "FLUSH", "MAXTRANS", "PLAN", "CONSTRAINTS", "FREELIST", "MAXVALUE", "PLI",
            "CONTENTS", "FREELISTS", "MIN", "PRECISION", "CONTINUE", "FORCE", "MINEXTENTS", "PRIMARY",
            "CONTROLFILE", "FOREIGN", "MINVALUE", "PRIVATE", "COUNT", "FORTRAN", "MODULE", "PROCEDURE",
            "PROFILE", "SAVEPOINT", "SQLSTATE", "TRACING", "QUOTA", "SCHEMA", "STATEMENT_ID", "TRANSACTION",
            "READ", "SCN", "STATISTICS", "TRIGGERS", "REAL", "SECTION", "STOP", "TRUNCATE",
            "RECOVER", "SEGMENT", "STORAGE", "UNDER", "REFERENCES", "SEQUENCE", "SUM", "UNLIMITED",
            "REFERENCING", "SHARED", "SWITCH", "UNTIL", "RESETLOGS", "SNAPSHOT", "SYSTEM", "USE",
            "RESTRICTED", "SOME", "TABLES", "USING", "REUSE", "SORT", "TABLESPACE", "WHEN",
            "ROLE", "SQL", "TEMPORARY", "WRITE", "ROLES", "SQLCODE", "THREAD", "WORK",
            "ROLLBACK", "SQLERROR", "TIME"
        };
    }

    public String getEscapedKeyWord(String keyword) {
        if (keyword == null) {
            throw new IllegalArgumentException("Keyword cannot be null!");
        }
        return "\"" + keyword.toUpperCase() + "\"";
    }

}
