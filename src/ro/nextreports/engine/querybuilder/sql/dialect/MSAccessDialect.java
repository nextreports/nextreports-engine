package ro.nextreports.engine.querybuilder.sql.dialect;

import java.sql.Types;

import ro.nextreports.engine.util.ProcUtil;

// At the foundations this driver uses hsqldb (see getSqlChecker)
// Info: http://ucanaccess.sourceforge.net/site.html
public class MSAccessDialect extends AbstractDialect {

    public MSAccessDialect() {
    	super();
    	registerColumnType("binary", Types.BLOB);
    	registerColumnType("bigbinary", Types.BLOB);
    	registerColumnType("bit", Types.BOOLEAN);
    	registerColumnType("boolean", Types.BOOLEAN);
    	registerColumnType("counter", Types.INTEGER);
    	registerColumnType("money", Types.DECIMAL);
    	registerColumnType("datetime", Types.TIMESTAMP);
    	registerColumnType("uniqueidentifier", Types.VARCHAR);
    	registerColumnType("long binary", Types.BLOB);
    	registerColumnType("long text", Types.LONGVARCHAR);
    	registerColumnType("memo", Types.LONGVARCHAR);
    	registerColumnType("numeric", Types.NUMERIC);
        registerColumnType("number", Types.NUMERIC);
        registerColumnType("ole", Types.BLOB);
        registerColumnType("text", Types.VARCHAR);
        registerColumnType("varbinary", Types.BLOB);    	    	
        registerColumnType("integer", Types.INTEGER);
        registerColumnType("tinyint", Types.INTEGER);
        registerColumnType("smallint", Types.INTEGER);
        registerColumnType("decimal", Types.DECIMAL);
        registerColumnType("real", Types.DOUBLE);
        registerColumnType("float", Types.FLOAT);
        registerColumnType("timestamp", Types.TIMESTAMP);
        registerColumnType("char", Types.CHAR);
        registerColumnType("nchar", Types.CHAR);        
        registerColumnType("varchar", Types.VARCHAR);
        registerColumnType("nvarchar", Types.VARCHAR);
        registerColumnType("varchar_ignorecase", Types.VARCHAR);
    }

    public String getCurrentDate() throws DialectException {
        return "Date()";
    }
    
    public String getCurrentTimestamp() throws DialectException {
    	return "Now()";
    }
    
    public String getCurrentTime() throws DialectException {
    	return "Now()";
    }

    public String getCurrentDateSelect() {
        return "select Date()";
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
        return "VALUES (CURRENT_TIMESTAMP)";
    }

}

