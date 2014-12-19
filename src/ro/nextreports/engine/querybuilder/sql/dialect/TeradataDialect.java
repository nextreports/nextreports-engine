package ro.nextreports.engine.querybuilder.sql.dialect;

import java.sql.Types;

public class TeradataDialect extends AbstractDialect {

	public TeradataDialect() {
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
		registerColumnType("byteint", Types.INTEGER);
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
		registerColumnType("time with time zone", Types.TIME);
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
		return "REF CURSOR";
	}

	public int getCursorSqlType() {
		return Types.OTHER;
	}

	public String getSqlChecker() {
		return "select 1";
	}
}
