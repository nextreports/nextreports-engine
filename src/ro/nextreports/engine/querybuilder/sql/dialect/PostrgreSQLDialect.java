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
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 01-Sep-2009
// Time: 13:31:36

import ro.nextreports.engine.util.ProcUtil;

// PostgreSQL gotchas :
//
// Quoting an identifier also makes it case-sensitive, whereas unquoted names are always
// folded to lower case. For example, the identifiers FOO, foo, and "foo" are considered
// the same by PostgreSQL, but "Foo" and "FOO" are different from these three and each other

public class PostrgreSQLDialect extends AbstractDialect {

    public PostrgreSQLDialect() {
        super();
        registerColumnType("boolean", Types.BIT);
        registerColumnType("bool", Types.BIT);
        registerColumnType("bigint", Types.BIGINT);
        registerColumnType("int8", Types.BIGINT);
        registerColumnType("smallint", Types.SMALLINT);
        registerColumnType("int2", Types.SMALLINT);
        registerColumnType("integer", Types.INTEGER);
        registerColumnType("int", Types.INTEGER);
        registerColumnType("int4", Types.INTEGER);
        registerColumnType("character(1, *)", Types.CHAR);
        registerColumnType("char(1, *)", Types.CHAR);
        registerColumnType("character(>1, *)", Types.VARCHAR);
        registerColumnType("char(>1, *)", Types.VARCHAR);
        registerColumnType("varchar", Types.VARCHAR);
        registerColumnType("real", Types.FLOAT);
        registerColumnType("float4", Types.FLOAT);
        registerColumnType("double precision", Types.DOUBLE);
        registerColumnType("float8", Types.DOUBLE);
        registerColumnType("date", Types.DATE);
        registerColumnType("time", Types.TIME);
        registerColumnType("timestamp", Types.TIMESTAMP);
        registerColumnType("bytea", Types.VARBINARY);
        registerColumnType("text", Types.CLOB);
        registerColumnType("oid", Types.BLOB);
        registerColumnType("numeric", Types.NUMERIC);
    }

    public String getCurrentDate() throws DialectException {
        return "current_date";
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

     protected void setKeywords() {
        keywords = new String[] {
             "ABORT", "ABSOLUTE", "ACCESS", "ACTION", "ADD", "ADMIN", "AFTER", "AGGREGATE",
             "ALSO", "ALTER", "ALWAYS", "ASSERTION", "ASSIGNMENT", "AT", "BACKWARD",
             "BEFORE", "BEGIN", "BETWEEN", "BIGINT", "BIT", "BOOLEAN", "BY", "CACHE",
             "CALLED", "CASCADE", "CASCADED", "CATALOG", "CHAIN", "CHAR", "CHARACTER",
             "CHARACTERISTICS", "CHECKPOINT", "CLASS", "CLOSE", "CLUSTER", "COALESCE",
             "COMMENT", "COMMENTS", "COMMIT", "COMMITED", "CONFIGURATION", "CONNECTION",
             "CONSTRAINTS", "CONTENT", "CONTINUE", "CONVERSION", "COPY", "COST", "CREATEDB",
             "CREATEROLE", "CREATEUSER", "CSV", "CURRENT", "CURSOR", "CYCLE", "DATA",
             "DATABASE", "DAY", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE", "DEFAULTS",
             "DEFERRED", "DEFINER", "DELETE", "DELIMITER", "DELIMITERS", "DICTIONARY",
             "DISABLE", "DISCARD", "DOCUMENT", "DOMAIN", "DOUBLE", "DROP", "EACH", "ENABLE",
             "ENCODING", "ENCRYPTED", "ENUM", "ESCAPE", "EXCLUDE", "EXCLUDING", "EXCLUSIVE",
             "EXECUTE", "EXISTS", "EXPLAIN", "EXTERNAL", "EXTRACT", "FAMILY", "FIRST",
             "FLOAT", "FOLLOWING", "FORCE", "FORWARD", "FUNCTION", "FUNCTIONS", "GLOBAL",
             "GRANTED", "GREATEST", "HANDLER", "HEADER", "HOLD", "HOUR", "IDENTITY", "IF",
             "IMMEDIATE", "IMMUTABLE", "IMPLICIT", "INCLUDING", "INCREMENT", "INDEX", "INDEXES",
             "INHERIT", "INHERITS", "INLINE", "INOUT", "INPUT", "INSENSITIVE", "INSERT",
             "INSTEAD", "INT", "INTEGER", "INTERVAL", "INVOKER", "ISOLATION", "KEY",
             "LANGUAGE", "LARGE", "LAST", "LC_COLLATE", "LC_CTYPE", "LEAST", "LEVEL", "LISTEN",
             "LOAD", "LOCAL", "LOCATION", "LOCK", "LOGIN", "MAPPING", "MATCH", "MAXVALUE",
             "MINUTE", "MINVALUE", "MODE", "MONTH", "MOVE", "NAME", "NAMES", "NATIONAL",
             "NCHAR", "NEXT", "NOCREATEDB", "NOCREATEROLE", "NOCREATEUSER", "NOINHERIT", "NOLOGIN",
             "NONE", "NOSUPERUSER", "NOTHING", "NOTIFY", "NOWAIT", "NULLIF", "NULLS", "NUMERIC",
             "OBJECT", "OF", "OIDS", "OPERATOR", "OPTION", "OPTIONS", "OUT", "OVER", "OVERLAY",
             "OWNED", "OWNER", "PARSER", "PARTIAL", "PARTITION", "PASSWORD", "PLANS", "POSITION",
             "PRECEDING", "PRECISION", "PREPARE", "PREPARED", "PRESERVE", "PRIOR", "PRIVILEGES",
             "PROCEDURAL", "PROCEDURE", "QUOTE", "RANGE", "READ", "REAL", "REASSIGN", "RECHECK",
             "RECURSIVE", "REINDEX", "RELATIVE", "RELEASE", "RENAME", "REPEATABLE", "REPLACE",
             "REPLICA", "RESET", "RESTART", "RESTRICT", "RETURNS", "REVOKE", "ROLE", "ROLLBACK",
             "ROW", "ROWS", "RULE", "SAVEPOINT", "SCHEMA", "SCROLL", "SEARCH", "SECOND", "SECURITY",
             "SEQUENCE", "SEQUENCES", "SERIALIZABLE", "SERVER", "SESSION", "SET", "SETOF", "SHARE",
             "SHOW", "SIMPLE", "SMALLINT", "STABLE", "STANDALONE", "START", "STATEMENT", "STATISTICS",
             "STDIN", "STDOUT", "STORAGE", "STRICT", "STRIP", "SUBSTRING", "SUPERUSER", "SYSID",
             "SYSTEM", "TABLES", "TABLESPACE", "TEMP", "TEMPLATE", "TEMPORARY", "TEXT", "TIME",
             "TIMESTAMP", "TRANSACTION", "TREAT", "TRIGGER", "TRIM", "TRUNCATE", "TRUSTED",
             "TYPE", "UNBOUNDED", "UNCOMMITED", "UNENCRYPTED", "UNKNOWN", "UNLISTEN", "UNTIL", "UPDATE",
             "VACUUM", "VALID", "VALIDATOR", "VALUE", "VALUES", "VARCHAR", "VARYING", "VERSION", "VIEW",
             "VOLATILE", "WHITESPACE", "WITHOUT", "WORK", "WRAPPER", "WRITE", "XML", "XMLATTRIBUTES",
             "XMLCONCAT", "XMLELEMENT", "XMLFOREST", "XMLPARSE", "XMLPI", "XMLROOT", "XMLSERIALIZE", "YEAR",
             "YES", "ZONE"
        };
     }

    public String getEscapedKeyWord(String keyword) {
        if (keyword == null) {
            throw new IllegalArgumentException("Keyword cannot be null!");
        }
        return "\"" + keyword + "\"";
    }

}
