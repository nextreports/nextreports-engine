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
 * Date: Jul 3, 2006
 * Time: 4:56:38 PM
 */
public class MSSQLDialect extends AbstractDialect {

    public MSSQLDialect() {
    	super();
        registerColumnType("bit", Types.BIT);
        registerColumnType("bigint", Types.BIGINT);
        registerColumnType("bigint identity", Types.BIGINT);
        registerColumnType("smallint", Types.SMALLINT);
        registerColumnType("smallint identity", Types.SMALLINT);
        registerColumnType("tinyint", Types.TINYINT);
        registerColumnType("tinyint identity", Types.TINYINT);
        registerColumnType("int", Types.INTEGER);
        registerColumnType("int identity", Types.INTEGER);
        registerColumnType("float", Types.FLOAT);        
        registerColumnType("decimal", Types.DECIMAL);        
        registerColumnType("double", Types.DOUBLE);
        registerColumnType("real", Types.DOUBLE);
        registerColumnType("numeric", Types.NUMERIC);
        registerColumnType("numeric identity", Types.NUMERIC);
        registerColumnType("uniqueidentifier", Types.CHAR);
        registerColumnType("char", Types.CHAR);
        registerColumnType("nchar", Types.CHAR);        
        registerColumnType("varchar", Types.VARCHAR);
        registerColumnType("nvarchar", Types.VARCHAR);
        registerColumnType("text", Types.VARCHAR);        
        registerColumnType("ntext", Types.VARCHAR);
        registerColumnType("date", Types.DATE);
        registerColumnType("smalldatetime", Types.TIMESTAMP);
        registerColumnType("datetime", Types.TIMESTAMP);        
        registerColumnType("timestamp", Types.TIMESTAMP);
        registerColumnType("money", Types.DECIMAL);
        registerColumnType("smallmoney", Types.DECIMAL);        
        registerColumnType("blob", Types.BLOB);
        registerColumnType("varbinary", Types.BLOB);
        registerColumnType("image", Types.BLOB);
        registerColumnType("clob", Types.CLOB);
    }

    public String getCurrentDate() throws DialectException {
        return "getdate()";
    }

    public String getCurrentDateSelect() {
        return "select getdate()";
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

    @Override
    public boolean schemaBeforeCatalog() {
        return false;
    }

    public String getSqlChecker() {
        return "SELECT 1";
    }

    protected void setKeywords() {
        keywords = new String[] {
           "ADD", "ALTER", "AND", "ANY", "AS", "ASC", "AUTHORIZATION", "BACKUP", "BEGIN",
           "BETWEEN", "BREAK", "BROWSE", "BULK", "BY", "CASCADE", "CASE", "CHECK", "CHECKPOINT",
           "CLOSE", "CLUSTERED", "COALESCE", "COLLATE", "COLUMN", "COMMIT", "COMPUTE",
           "CONSTRAINT", "CONTAINS", "CONTAINSTABLE", "CONTINUE", "CONVERT", "CREATE",
           "CROSS", "CURRENT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP",
           "CURRENT_USER", "CURSOR", "DATABASE", "DBCC", "DEALLOCATE", "DECLARE",
           "DEFAULT", "DELETE", "DENY", "DESC",  "DISK", "DISTINCT", "DISTRIBUTED",
           "DOUBLE", "DROP", "DUMMY", "DUMP", "ELSE", "END", "ERRLVL", "ESCAPE",
           "EXCEPT", "EXECUTE", "EXISTS", "EXIT", "FETCH", "FILE", "FILLFACTOR", "FOR",
           "FOREIGN", "FREETEXT", "FREETEXTTABLE", "FROM", "FULL", "FUNCTION", "GOTO",
           "GRANT", "GROUP", "HAVING", "HOLDLOCK", "IDENTITY", "IDENTITY_INSERT", "IDENTITYCOL",
           "IF", "IN", "INDEX", "INNER", "INSERT", "INTERSECT", "INTO", "IS", "JOIN",
           "KEY", "KILL", "LEFT", "LIKE", "LINENO", "LOAD", "NATIONAL", "NOCHECK",
           "NONCLUSTERED", "NOT", "NULL", "NULLIF", "OF", "OFF", "OFFSETS", "ON", "OPEN",
           "OPENDATASOURCE", "OPENQUERY", "OPENROWSET", "OPENXML", "OPTION", "OR", "ORDER",
           "OUTER", "OVER", "PERCENT", "PLAN", "PRECISION", "PRIMARY", "PRINT", "PROC",
		   "PROCEDURE", "PUBLIC", "RAISERROR", "READ", "READTEXT", "RECONFIGURE", "REFERENCES",
           "REPLICATION", "RESTORE", "RESTRICT", "RETURN" , "REVOKE", "RIGHT", "ROLLBACK",
		   "ROWCOUNT", "ROWGUIDCOL", "RULE", "SAVE", "SCHEMA", "SELECT", "SESSION_USER", "SET",
		   "SETUSER", "SHUTDOWN", "SOME", "STATISTICS", "SYSTEM_USER", "TABLE", "TEXTSIZE",
           "THEN", "TO", "TOP", "TRAN", "TRANSACTION", "TRIGGER" ,"TRUNCATE", "TSEQUAL",
		   "UNION", "UNIQUE", "UPDATE", "UPDATETEXT", "USE", "USER", "VALUES", "VARYING",
		   "VIEW", "WAITFOR", "WHEN", "WHERE", "WHILE", "WITH", "WRITETEXT"
        };
    }

    public String getEscapedKeyWord(String keyword) {
        if (keyword == null) {
            throw new IllegalArgumentException("Keyword cannot be null!");
        }
        return "[" + keyword + "]";
    }

}
