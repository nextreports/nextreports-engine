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
package ro.nextreports.engine.util;

import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.engine.querybuilder.sql.dialect.ConnectionUtil;
import ro.nextreports.engine.querybuilder.sql.dialect.Dialect;
import ro.nextreports.engine.querybuilder.sql.dialect.OracleDialect;
import ro.nextreports.engine.queryexec.IdName;
import ro.nextreports.engine.queryexec.Query;
import ro.nextreports.engine.queryexec.QueryChunk;
import ro.nextreports.engine.queryexec.QueryException;
import ro.nextreports.engine.queryexec.QueryExecutor;
import ro.nextreports.engine.queryexec.QueryParameter;
import ro.nextreports.engine.queryexec.QueryResult;
import ro.nextreports.engine.queryexec.util.SqlFile;


/**
 * @author Decebal Suiu
 */
public class QueryUtil {

	private static Log LOG = LogFactory.getLog(QueryUtil.class);

	private Connection con;
	private Dialect dialect;

	public QueryUtil(Connection con, Dialect dialect) {
		this.con = con;
		this.dialect = dialect;
	}

	public String getSqlFromFile(String file) throws Exception {
		System.out.println("=== sql ===");
		SqlFile sqlFile = new SqlFile(file);
		String sql = sqlFile.getSqlList().get(0);
		System.out.println(sql);
		return sql;
	}
	
	public List<String> getColumnNames(String sql, Map<String, QueryParameter> params) throws Exception {
		return getColumnNames(sql, params, null);
	}

	public List<String> getColumnNames(String sql, Map<String, QueryParameter> params, List<NameType> cachedColumns) throws Exception {
		List<NameType> list = getColumns(sql, params, cachedColumns);
		List<String> columns = new ArrayList<String>();
		for (NameType nt : list) {
			columns.add(nt.getName());
		}
		return columns;
	}

	public List<String> getColumnTypes(String sql, Map<String, QueryParameter> params, List<NameType> cachedColumns) throws Exception {
		List<NameType> list = getColumns(sql, params, cachedColumns);
		return ReportUtil.getColumnNames(list);
	}
	
	public String getColumnType(String sql, Map<String, QueryParameter> params, String columnName, List<NameType> cachedColumns) throws Exception {
		List<NameType> list = getColumns(sql, params, cachedColumns);
		for (NameType nt : list) {
			if (nt.getName().equalsIgnoreCase(columnName)) {
				return nt.getType();
			}
		}
		return null;
	}
	
	public List<NameType> getColumns(String sql, Map<String, QueryParameter> params) throws Exception {
		return getColumns(sql, params, null);
	}
		

	public List<NameType> getColumns(String sql, Map<String, QueryParameter> params, List<NameType> cachedColumns) throws Exception {
		if (cachedColumns != null) {
			return cachedColumns;
		}
		
		// create the query object
		Query query = new Query(sql);

		// get parameter names
		String[] paramNames = query.getParameterNames();

		// execute query if no parameters
		if (paramNames.length == 0) {
			return executeQueryForColumnNames(sql);
		}

		// init the sql without parameters
		StringWriter sqlWithoutParameters = new StringWriter(100);

		// subtitute paramters with default value
		QueryChunk[] chunks = query.getChunks();
		for (QueryChunk chunk : chunks) {
			int chunckType = chunk.getType();
			if (QueryChunk.TEXT_TYPE == chunckType) {
				sqlWithoutParameters.append(chunk.getText());
			} else if (QueryChunk.PARAMETER_TYPE == chunckType) {
				String paramName = chunk.getText();
				QueryParameter param = params.get(paramName);
				if (param == null) {
					// do not internationalize strings in engine package!!!
					throw new Exception("Parameter '" + paramName + "' not defined.");
				}
				boolean afterIn = sqlWithoutParameters.getBuffer().toString().trim().toLowerCase()
						.endsWith(QueryExecutor.IN.toLowerCase());
				if (afterIn) {
					sqlWithoutParameters.append("(");
				}
				sqlWithoutParameters.append(getDummyValueForParameter(param));
				if (afterIn) {
					sqlWithoutParameters.append(")");
				}
			}
		}
		return executeQueryForColumnNames(sqlWithoutParameters.toString());
	}

	public List<NameType> executeQueryForColumnNames(String sql) throws Exception {
		// long t = System.currentTimeMillis();
		StringWriter sw = new StringWriter(100);
		// sw.append("SELECT * FROM (");
		sw.append(sql);
		// sw.append(") A WHERE 1 = -1");

		String sqlForHeader = sw.toString();
		LOG.info("call for header columns = " + sqlForHeader);

		ResultSet rs = null;
		Statement stmt = null;
		try {
			if (isProcedureCall(sqlForHeader)) {
				Dialect dialect = DialectUtil.getDialect(con);
				CallableStatement cs = con.prepareCall("{" + sqlForHeader + "}");
				stmt = cs;
				if (dialect.hasProcedureWithCursor()) {
					cs.registerOutParameter(1, dialect.getCursorSqlType());
				}
				rs = cs.executeQuery();
				if (dialect.hasProcedureWithCursor()) {
					rs = (ResultSet) (cs.getObject(1));
				}
			} else {
				stmt = con.createStatement();
				stmt.setMaxRows(1);
				rs = stmt.executeQuery(sqlForHeader);
			}
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();

			List<NameType> columnNames = new ArrayList<NameType>();
			for (int i = 0; i < columnCount; i++) {
				columnNames.add(new NameType(rsmd.getColumnLabel(i + 1), dialect.getJavaType(rsmd.getColumnTypeName(i + 1),
						rsmd.getPrecision(i + 1), rsmd.getScale(i + 1))));
				// rsmd.getColumnClassName(i + 1)));
			}

			// t = System.currentTimeMillis() - t;
			// System.out.println("execute query for column names in " + t +
			// "ms");

			return columnNames;
		} finally {
			ConnectionUtil.closeResultSet(rs);
			ConnectionUtil.closeStatement(stmt);
		}

	}

	// return (column name, legend name) to set on chart
	public List<NameType> executeQueryForDynamicColumn(String sql) throws Exception {

		StringWriter sw = new StringWriter(100);
		sw.append(sql);

		String sqlForHeader = sw.toString();
		LOG.info("call for chart dynamic columns = " + sqlForHeader);

		ResultSet rs = null;
		Statement stmt = null;
		try {
			if (isProcedureCall(sqlForHeader)) {
				Dialect dialect = DialectUtil.getDialect(con);
				CallableStatement cs = con.prepareCall("{" + sqlForHeader + "}");
				stmt = cs;
				if (dialect.hasProcedureWithCursor()) {
					cs.registerOutParameter(1, dialect.getCursorSqlType());
				}
				rs = cs.executeQuery();
				if (dialect.hasProcedureWithCursor()) {
					rs = (ResultSet) (cs.getObject(1));
				}
			} else {
				stmt = con.createStatement();
				rs = stmt.executeQuery(sqlForHeader);
			}
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			if (columnCount != 2) {
				throw new QueryException("Column query must have two data columns : column name and column legend.");
			}

			List<NameType> columnNames = new ArrayList<NameType>();
			while (rs.next()) {
				columnNames.add(new NameType(rs.getString(1), rs.getString(2)));
			}
			return columnNames;
		} finally {
			ConnectionUtil.closeResultSet(rs);
			ConnectionUtil.closeStatement(stmt);
		}

	}

	private String getDummyValueForParameter(QueryParameter param) throws Exception {

		if (param.isProcedureParameter()) {
			String valueClassName = param.getValueClassName();
			if (QueryParameter.STRING_VALUE.equals(valueClassName)) {
				return "'" + param.getPreviewValue() + "'";
			} else {
				return param.getPreviewValue();
			}
		}

		String valueClassName = param.getValueClassName();
		if (QueryParameter.STRING_VALUE.equals(valueClassName)) {
			return "'dummy'";
		} else if (QueryParameter.BOOLEAN_VALUE.equals(valueClassName)) {
			// return "'true'";
			// ok for oracle (varchar(1) , number(1) -> can apply a boolean
			// parameter)
			// @todo verify for other databases with boolean parameter
			return "1";
		} else if (QueryParameter.BYTE_VALUE.equals(valueClassName)) {
			return "0";
		} else if (QueryParameter.DOUBLE_VALUE.equals(valueClassName)) {
			return "0";
		} else if (QueryParameter.LONG_VALUE.equals(valueClassName)) {
			return "0";
		} else if (QueryParameter.FLOAT_VALUE.equals(valueClassName)) {
			return "0";
		} else if (QueryParameter.INTEGER_VALUE.equals(valueClassName)) {
			return "0";
		} else if (QueryParameter.SHORT_VALUE.equals(valueClassName)) {
			return "0";
		} else if (QueryParameter.DATE_VALUE.equals(valueClassName)) {
			return dialect.getCurrentDate();
		} else if (QueryParameter.TIME_VALUE.equals(valueClassName)) {
			return dialect.getCurrentTime();
		} else if (QueryParameter.TIMESTAMP_VALUE.equals(valueClassName)) {
			return dialect.getCurrentTimestamp();
		} else if (QueryParameter.OBJECT_VALUE.equals(valueClassName)) {
			return "0";
		} else if (QueryParameter.BIGDECIMAL_VALUE.equals(valueClassName)) {
			return "0";
		} else if (QueryParameter.BIGINTEGER_VALUE.equals(valueClassName)) {
			return "0";
		}

		return "dummy";
	}

	public QueryResult executeQueryFromFile(String file) throws Exception {
		QueryExecutor executor = null;
		try {
			String sql = getSqlFromFile(file);
			Query query = new Query(sql);
			// String[] parameterNames = query.getParameterNames();
	
			Map<String, QueryParameter> parameters = new HashMap<String, QueryParameter>();
			// QueryParameter param = new QueryParameter("name", "",
			// QueryParameter.STRING_VALUE);
			// parameters.put(param.getName(), param);
			Map<String, Object> values = new HashMap<String, Object>();
			// values.put(param.getName(), new Integer(1000));
			// values.put(param.getName(), "M%");
			executor = new QueryExecutor(query, parameters, values, con);
			QueryResult result = executor.execute();
			// System.out.println("columns = " + result.getColumnCount());
			// System.out.println("rows = " + result.getRowCount());
			// QueryResultPrinter.printResult(result);
	
			return result;
		} finally {
			if (executor != null) {
				executor.closeCursors();
			}
		}
	}

	// public static void main(String[] args) {
	// try {
	// QueryUtil qu = new QueryUtil(Globals.getConnection(),
	// Globals.getDialect());
	// String sql = qu.getSqlFromFile("demo.sql");
	// List<String> columnNames = qu.getColumnNames(sql);
	// System.out.println("columnNames = " + columnNames);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	/**
	 * Restrict a query execution. Do not allow for database modifications.
	 * 
	 * @param sql
	 *            sql to execute
	 * @return true if query is restricted
	 */
	public static boolean restrictQueryExecution(String sql) {
		String[] restrictions = { "delete", "truncate", "update", "drop", "alter" };
		if (sql != null) {
			sql = sql.toLowerCase();
			for (String restriction : restrictions) {
				if (sql.startsWith(restriction)) {
					return true;
				}
				String regex = "\\s+" + restriction + "\\s+";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(sql);
				if (matcher.find()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * See if the sql is a stored procedure call
	 * 
	 * @param sql
	 *            sql to execute
	 * @return true if the sql is a stored procedure call, false otherwise
	 */
	public static boolean isProcedureCall(String sql) {
		if (sql == null) {
			return false;
		}
		return sql.toLowerCase().startsWith("call ");
	}

	/**
	 * See if the sql contains only one '?' character
	 * 
	 * @param sql
	 *            sql to execute
	 * @param dialect
	 *            dialect
	 * @return true if the sql contains only one '?' character, false otherwise
	 */
	public static boolean isValidProcedureCall(String sql, Dialect dialect) {
		if (sql == null) {
			return false;
		}
		if (dialect instanceof OracleDialect) {
			return sql.split("\\?").length == 2;
		} else {
			return true;
		}
	}

	public List<IdName> getValues(String sql, Map<String, QueryParameter> map, Map<String, Object> vals) throws Exception {

		List<IdName> values = new ArrayList<IdName>();
				
		QueryExecutor executor = null;
		try {
			Query query = new Query(sql);
			executor = new QueryExecutor(query, map, vals, con, false, false, false);
			executor.setTimeout(10000);
			executor.setMaxRows(0);
			QueryResult qr = executor.execute();

			// one or two columns in manual select source
			// for (int i = 0; i < count; i++) {
			while (qr.hasNext()) {
				IdName in = new IdName();
				in.setId((Serializable) qr.nextValue(0));
				if (qr.getColumnCount() == 1) {
					in.setName((Serializable) qr.nextValue(0));
				} else {
					in.setName((Serializable) qr.nextValue(1));
				}
				values.add(in);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex);
		} finally {
			if (executor != null) {
				executor.close();
			}
		}		
		return values;
	}

}
