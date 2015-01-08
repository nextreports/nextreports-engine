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
 * Date: Aug 24, 2006
 * Time: 4:45:35 PM
 */
public class MySQLDialect extends AbstractDialect {

    public MySQLDialect() {
        super();        
        registerColumnType("bit", Types.BIT);
        registerColumnType("bigint", Types.BIGINT);
        registerColumnType("bigint unsigned", Types.BIGINT);
        registerColumnType("smallint", Types.SMALLINT);
        registerColumnType("smallint unsigned", Types.SMALLINT);
        registerColumnType("tinyint", Types.TINYINT);
        registerColumnType("tinyint unsigned", Types.TINYINT);
        registerColumnType("integer", Types.INTEGER);
        registerColumnType("integer unsigned", Types.BIGINT);
        registerColumnType("int", Types.INTEGER);
        registerColumnType("int unsigned", Types.BIGINT);
        registerColumnType("float", Types.FLOAT);
        registerColumnType("decimal", Types.DECIMAL);
        registerColumnType("decimal unsigned", Types.DECIMAL);
        registerColumnType("double", Types.DOUBLE);
        registerColumnType("numeric", Types.NUMERIC);
        registerColumnType("char", Types.CHAR);
        registerColumnType("varchar", Types.VARCHAR);
        registerColumnType("text", Types.VARCHAR);
        registerColumnType("date", Types.DATE);
        registerColumnType("time", Types.TIMESTAMP);
        registerColumnType("datetime", Types.TIMESTAMP);
        registerColumnType("timestamp", Types.TIMESTAMP);
        registerColumnType("blob", Types.BLOB);
        registerColumnType("longblob", Types.BLOB);
        registerColumnType("mediumblob", Types.BLOB);
        registerColumnType("varbinary", Types.BLOB);        
        registerColumnType("clob", Types.CLOB);
    }

    public String getCurrentDate() throws DialectException {
        return "curdate()";
    }

    public String getCurrentDateSelect() {
        return "select curdate()";
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

    protected void setKeywords() {
        keywords = new String[]{
                "ACCESSIBLE", "ALTER", "AS", "BEFORE", "BINARY", "BY", "CASE", "CHARACTER",
                "COLUMN", "CONTINUE", "CROSS", "CURRENT_TIMESTAMP", "DATABASE", "DAY_MICROSECOND",
                "DEC", "DEFAULT", "DESC", "DISTINCT", "DOUBLE", "EACH", "ENCLOSED", "EXIT",
                "FETCH", "FLOAT8", "FOREIGN", "GRANT", "HIGH_PRIORITY", "HOUR_SECOND","IN", "INNER",
                "INSERT", "INT2", "INT8", "INTO", "JOIN", "KILL", "LEFT","LINEAR", "LOCALTIME",
                "LONG", "LOOP", "MATCH", "MEDIUMTEXT", "MINUTE_SECOND", "NATURAL", "NULL", "OPTIMIZE",
                "OR", "OUTER", "PRIMARY", "RANGE", "READ_WRITE", "REGEXP", "REPEAT", "RESTRICT",
                "RIGHT", "SCHEMAS", "SENSITIVE","SHOW", "SPECIFIC", "SQLSTATE", "SQL_CALC_FOUND_ROWS",
                "STARTING", "TERMINATED", "TINYINT", "TRAILING", "UNDO","UNLOCK", "USAGE", "UTC_DATE",
                "VALUES", "VARCHARACTER", "WHERE", "WRITE", "ZEROFILL", "ALL", "AND", "ASENSITIVE",
                "BIGINT", "BOTH", "CASCADE", "CHAR", "COLLATE", "CONSTRAINT", "CREATE", "CURRENT_TIME",
                "CURSOR", "DAY_HOUR", "DAY_SECOND", "DECLARE", "DELETE", "DETERMINISTIC", "DIV",
                "DUAL", "ELSEIF", "EXISTS", "FALSE", "FLOAT4","FORCE", "FULLTEXT", "HAVING", "HOUR_MINUTE",
                "IGNORE", "INFILE", "INSENSITIVE", "INT1", "INT4", "INTERVAL", "ITERATE", "KEYS",
                "LEAVE", "LIMIT", "LOAD", "LOCK", "LONGTEXT", "MASTER_SSL_VERIFY_SERVER_CERT", "MEDIUMINT",
                "MINUTE_MICROSECOND", "MODIFIES", "NO_WRITE_TO_BINLOG", "ON", "OPTIONALLY", "OUT",
                "PRECISION", "PURGE", "READS", "REFERENCES", "RENAME", "REQUIRE", "REVOKE", "SCHEMA",
                "SELECT", "SET", "SPATIAL", "SQLEXCEPTION", "SQL_BIG_RESULT", "SSL", "TABLE", "TINYBLOB",
                "TO", "TRUE", "UNIQUE", "UPDATE", "USING", "UTC_TIMESTAMP", "VARCHAR","WHEN", "WITH",
                "YEAR_MONTH", "ADD", "ANALYZE", "ASC", "BETWEEN", "BLOB", "CALL", "CHANGE", "CHECK",
                "CONDITION", "CONVERT", "CURRENT_DATE", "CURRENT_USER", "DATABASES", "DAY_MINUTE",
                "DECIMAL", "DELAYED", "DESCRIBE", "DISTINCTROW", "DROP", "ELSE", "ESCAPED", "EXPLAIN",
                "FLOAT", "FOR", "FROM", "GROUP", "HOUR_MICROSECOND", "IF", "INDEX", "INOUT", "INT",
                "INT3", "INTEGER", "IS", "KEY", "LEADING", "LIKE", "LINES", "LOCALTIMESTAMP", "LONGBLOB",
                "LOW_PRIORITY", "MEDIUMBLOB", "MIDDLEINT", "MOD", "NOT", "NUMERIC", "OPTION", "ORDER",
                "OUTFILE", "PROCEDURE", "READ", "REAL", "RELEASE", "REPLACE", "RETURN", "RLIKE",
                "SECOND_MICROSECOND", "SEPARATOR", "SMALLINT", "SQL", "SQLWARNING", "SQL_SMALL_RESULT",
                "STRAIGHT_JOIN", "THEN", "TINYTEXT", "TRIGGER", "UNION", "UNSIGNED", "USE", "UTC_TIME",
                "VARBINARY", "VARYING", "WHILE", "XOR"
        };
    }

    public String getEscapedKeyWord(String keyword) {
        if (keyword == null) {
            throw new IllegalArgumentException("Keyword cannot be null!");
        }
        return "`" + keyword + "`";
    }

}
