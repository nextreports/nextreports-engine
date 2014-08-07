package ro.nextreports.engine.querybuilder.sql.dialect;

import java.sql.Types;

import ro.nextreports.engine.util.ProcUtil;

public class PervasiveDialect extends AbstractDialect {

    public PervasiveDialect() {
    	super();
    	 registerColumnType("bfloat4", Types.DOUBLE);
    	 registerColumnType("bfloat8", Types.DOUBLE);
    	 registerColumnType("bigint", Types.BIGINT);
    	 registerColumnType("binary", Types.BLOB);
    	 registerColumnType("bit", Types.BIT);
    	 registerColumnType("char", Types.CHAR);
    	 registerColumnType("currency", Types.DECIMAL);
    	 registerColumnType("date", Types.DATE);
    	 registerColumnType("datetime", Types.TIMESTAMP);
    	 registerColumnType("double", Types.DOUBLE);        
         registerColumnType("decimal", Types.DECIMAL);
         registerColumnType("float", Types.DOUBLE);
         registerColumnType("identity", Types.INTEGER);
         registerColumnType("integer", Types.INTEGER);
         registerColumnType("longvarchar", Types.LONGVARCHAR);
         registerColumnType("longvarbinary", Types.BLOB);
         registerColumnType("money", Types.DECIMAL);
         registerColumnType("numeric", Types.DECIMAL);
         registerColumnType("numericsa", Types.DECIMAL);
         registerColumnType("numericsts", Types.DECIMAL);
         registerColumnType("real", Types.REAL);
         registerColumnType("smallidentity", Types.INTEGER);
         registerColumnType("smallint", Types.SMALLINT);
         registerColumnType("time", Types.TIME);
         registerColumnType("timestamp", Types.TIMESTAMP);   
         registerColumnType("tinyint", Types.TINYINT);
         registerColumnType("ubigint", Types.BIGINT);
         registerColumnType("unique_identifier", Types.VARCHAR);
         registerColumnType("uinteger", Types.INTEGER);
         registerColumnType("usmallint", Types.SMALLINT);
         registerColumnType("utinyint", Types.TINYINT);
         registerColumnType("varchar", Types.VARCHAR);             	     	             
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

}
