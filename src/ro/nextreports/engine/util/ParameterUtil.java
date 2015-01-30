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


import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.querybuilder.sql.dialect.ConnectionUtil;
import ro.nextreports.engine.querybuilder.sql.dialect.Dialect;
import ro.nextreports.engine.querybuilder.sql.dialect.DialectException;
import ro.nextreports.engine.queryexec.IdName;
import ro.nextreports.engine.queryexec.Query;
import ro.nextreports.engine.queryexec.QueryException;
import ro.nextreports.engine.queryexec.QueryExecutor;
import ro.nextreports.engine.queryexec.QueryParameter;
import ro.nextreports.engine.queryexec.QueryResult;
import ro.nextreports.engine.util.comparator.IdNameComparator;

//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: Feb 3, 2009
// Time: 11:08:24 AM

//
/**
 * Utilities class to work with report parameters
 */
public class ParameterUtil {

    /**
     * String to pass to an empty list -> new Object[NULL]
     */
    public static final String NULL = "__NULL__";

    /**
     * Test if all parameters used in the report are defined
     *
     * @param report report object
     * @throws ParameterNotFoundException if a parameter used in the report is not defined
     */
    public void parametersAreDefined(Report report) throws ParameterNotFoundException {
        String[] paramNames;
        String sql = report.getSql();
        if (sql == null) {
            sql = report.getQuery().toString();
        }
        Query query = new Query(sql);
        paramNames = query.getParameterNames();

        List<QueryParameter> parameters = report.getParameters();

        for (String paramName : paramNames) {
            QueryParameter param = null;
            for (QueryParameter p : parameters) {
                if (paramName.equals(p.getName())) {
                    param = p;
                }
            }
            if (param == null) {
                throw new ParameterNotFoundException(paramName);
            }
        }
    }


    /**
     * Get values for a parameter from a database
     * If a parameter is dependent of another parameter(s) his values will not be loaded
     *
     * @param con       connection to database
     * @param parameter parameter
     * @return a list of parameter values
     * @throws SQLException     if an error to sql execution appears
     * @throws DialectException if dialect not found
     */
    public static List<IdName> getParameterValues(Connection con, QueryParameter parameter)
            throws SQLException, DialectException {

        List<IdName> values = new ArrayList<IdName>();
        if (parameter == null) {
            return values;
        }        
        String source = parameter.getSource();
        if (parameter.isManualSource()) {
            if (!parameter.isDependent()) {
                values = getSelectValues(con, source, true, parameter.getOrderBy());
            }
        } else {
            int index = source.indexOf(".");
            int index2 = source.lastIndexOf(".");
            String tableName = source.substring(0, index);
            String columnName;
            String shownColumnName = null;
            if (index == index2) {
                columnName = source.substring(index + 1);
            } else {
                columnName = source.substring(index + 1, index2);
                shownColumnName = source.substring(index2 + 1);
            }
            values = getColumnValues(con, parameter.getSchema(), tableName, columnName, shownColumnName, parameter.getOrderBy());
        }
        return values;
    }

    /**
     * Get values from a column in the database
     *
     * @param con             database connection
     * @param schema          schema name
     * @param table           table
     * @param columnName      column name
     * @param shownColumnName shown column name
     * @param orderBy         order by
     * @return a list of values (columnName, shownColumnName)
     * @throws SQLException     if an error to sql execution appears
     * @throws DialectException if dialect is not found
     */
    @SuppressWarnings("unchecked")
    public static List<IdName> getColumnValues(Connection con, String schema, String table, String columnName, String shownColumnName, byte orderBy)
            throws SQLException, DialectException {

        List<IdName> values = new ArrayList<IdName>();
        Dialect dialect = DialectUtil.getDialect(con);
        ResultSet rs = null;
        String fromTable = table;
        if ( (schema != null) && (!"%".equals(schema)) ) {
        	fromTable = schema + "." + table;
        }
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            String sql;
            if (columnName.contains(" ")) {
            	columnName = dialect.getEscapedKeyWord(columnName);
            }
            if (shownColumnName == null) {
                sql = "SELECT DISTINCT " + columnName + " FROM " + fromTable + " WHERE " + columnName +
                        " IS NOT NULL ORDER BY " + columnName;
            } else {
                sql = "SELECT DISTINCT " + columnName + " , " + shownColumnName + " FROM " + fromTable + " WHERE " + columnName +
                        " IS NOT NULL ORDER BY " + shownColumnName;
            }

            rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            String type = rsmd.getColumnTypeName(1);
            int precision = rsmd.getPrecision(1);
            int scale = rsmd.getScale(1);
            int typeCode = dialect.getJdbcType(type, precision, scale);
            String type2;
            int precision2;
            int scale2;
            int typeCode2 = -1;
            if (shownColumnName != null) {
                type2 = rsmd.getColumnTypeName(2);
                precision2 = rsmd.getPrecision(2);
                scale2 = rsmd.getScale(2);
                typeCode2 = dialect.getJdbcType(type2, precision2, scale2);
            }

            while (rs.next()) {
                IdName in = new IdName();
                switch (typeCode) {
                    case Types.BIT:
                        in.setId(rs.getBoolean(1));
                        break;
                    case Types.SMALLINT:
                        in.setId(rs.getShort(1));
                        break;
                    case Types.INTEGER:
                    case Types.NUMERIC:
                        in.setId(rs.getInt(1));
                        break;
                    case Types.FLOAT:
                        in.setId(rs.getFloat(1));
                        break;
                    case Types.BIGINT:
                        in.setId(rs.getBigDecimal(1));
                        break;
                    case Types.DOUBLE:
                        in.setId(rs.getDouble(1));
                        break;
                    case Types.DATE:
                        in.setId(rs.getDate(1));
                        break;
                    case Types.TIME:
                        in.setId(rs.getTime(1));
                        break;
                    case Types.TIMESTAMP:
                        in.setId(rs.getTimestamp(1));
                        break;
                    case Types.VARCHAR:
                    case Types.CHAR:                    
                        in.setId(rs.getString(1));
                        break;
                    default:
                        //in.setId(rs.getObject(1));
                        throw new SQLException("NEXTREPORTS -> getColumnValues: type for value cannot be Serialized.");
                }
                setName(shownColumnName, in, rs, typeCode2);
                values.add(in);
                Collections.sort(values, new IdNameComparator(orderBy));
            }
        } finally {
            ConnectionUtil.closeResultSet(rs);
            ConnectionUtil.closeStatement(stmt);
        }
        return values;
    }


    /**
     * Get values returned by a select (with one or two fields)
     *
     * @param con     database connection
     * @param select  select
     * @param sort    if true sort after id
     * @param orderBy order by name or id
     * @return a list of values
     * @throws SQLException     if an error to sql execution appears
     * @throws DialectException if dialect is not found
     */
    @SuppressWarnings("unchecked")
    public static List<IdName> getSelectValues(Connection con, String select, boolean sort, byte orderBy)
            throws SQLException, DialectException {

        List<IdName> values = new ArrayList<IdName>();
        ResultSet rs = null;
        Statement stmt = null;
        Dialect dialect = DialectUtil.getDialect(con);
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(select);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            if (columnCount > 2) {
                throw new SQLException("Invalid sql.");
            }
            String type = rsmd.getColumnTypeName(1);
            int precision = rsmd.getPrecision(1);
            int scale = rsmd.getScale(1);
            int typeCode = dialect.getJdbcType(type, precision, scale);
            while (rs.next()) {
                boolean date = false;
                Serializable s;
                switch (typeCode) {
                    case Types.INTEGER:
                    case Types.NUMERIC:
                        s = rs.getInt(1);
                        break;
                    case Types.BIGINT:
                        s = rs.getBigDecimal(1);
                        break;
                    case Types.DOUBLE:
                        s = rs.getDouble(1);
                        break;
                    case Types.DATE:
                        date = true;
                        s = rs.getDate(1);
                        break;
                    case Types.TIME:
                        date = true;
                        s = rs.getTime(1);
                        break;
                    case Types.TIMESTAMP:
                        date = true;
                        s = rs.getTimestamp(1);
                        break;
                    case Types.VARCHAR:
                        s = rs.getString(1);
                        break;
                    default:
                        s = rs.getString(1);
                        break;
                }
                IdName in = new IdName();
                in.setId(s);
                if (columnCount == 1) {
                    if (date) {
                        in.setName(s);
                    } else {
                        in.setName(rs.getString(1));
                    }
                } else {
                    in.setName(rs.getString(2));
                }
                if (!values.contains(in)) {
                    values.add(in);
                }
            }
            if (sort && ((orderBy == QueryParameter.ORDER_BY_ID) || (orderBy == QueryParameter.ORDER_BY_NAME))) {
                Collections.sort(values, new IdNameComparator(orderBy));
            }
        } finally {
            ConnectionUtil.closeResultSet(rs);
            ConnectionUtil.closeStatement(stmt);
        }
        return values;
    }
    
    /**
     * Get values for a parameter sql at runtime
     * All parent parameters must have the values in the map.
     *
     * @param con  database connection
     * @param qp   parameter
     * @param map  report map of parameters
     * @param vals map of parameter values
     * @return values for parameter with sql source
     * @throws Exception if an exception occurs
     */
    public static List<IdName> getRuntimeParameterValues(Connection con, QueryParameter qp, Map<String, QueryParameter> map,
                                                  Map<String, Object> vals) throws Exception {    	    	
           	
    	List<IdName> values = new ArrayList<IdName>();    	
		if (qp.isManualSource()) {
			QueryResult qr = null;
			try {								
				Query query = new Query(qp.getSource());
				QueryExecutor executor = new QueryExecutor(query, map, vals, con, false, false, false);
				executor.setTimeout(10000);
				executor.setMaxRows(0);
				qr = executor.execute();
				// int count = qr.getRowCount();

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
				if (qr != null) {
					qr.close();
				}
			}
		} else {
			String source = qp.getSource();
            int index = source.indexOf(".");
            int index2 = source.lastIndexOf(".");
            String tableName = source.substring(0, index);
            String columnName;
            String shownColumnName = null;
            if (index == index2) {
                columnName = source.substring(index + 1);
            } else {
                columnName = source.substring(index + 1, index2);
                shownColumnName = source.substring(index2 + 1);
            }
            values = getColumnValues(con, qp.getSchema(), tableName, columnName, shownColumnName, qp.getOrderBy());
        }
		return values;
        
    }


    /**
     * Get values for a dependent parameter sql
     * All parent parameters must have the values in the map.
     *
     * @param con  database connection
     * @param qp   parameter
     * @param map  report map of parameters
     * @param vals map of parameter values
     * @return values for parameter with sql source
     * @throws Exception if an exception occurs
     */
    public static List<IdName> getParameterValues(Connection con, QueryParameter qp, Map<String, QueryParameter> map,
                                                  Map<String, Serializable> vals) throws Exception {    	    	

        Map<String, Object> objVals = new HashMap<String, Object>();
        for (String key : vals.keySet()) {
            Serializable s = vals.get(key);
            if (s instanceof Serializable[]) {
                Serializable[] array = (Serializable[]) s;
                Object[] objArray = new Object[array.length];
                for (int i = 0, size = array.length; i < size; i++) {
                    objArray[i] = array[i];
                }
                s = objArray;
            }
            objVals.put(key, s);            
        }

        QueryResult qr = null;
        try {
            List<IdName> values = new ArrayList<IdName>();
            
            Query query = new Query(qp.getSource());
            QueryExecutor executor = new QueryExecutor(query, map, objVals, con, false, false, false);
            executor.setTimeout(10000);
            executor.setMaxRows(0);
            qr = executor.execute();
            //int count = qr.getRowCount();

            // one or two columns in manual select source
            //for (int i = 0; i < count; i++) {
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
            return values;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception(ex);
        } finally {
        	if (qr != null)  {
        		qr.close();
        	}
        }
    }

    /**
     * Get values for a default source
     *
     * @param con connection
     * @param qp  parameter
     * @return a list of default values
     * @throws Exception if select failes
     */
    public static ArrayList<Serializable> getDefaultSourceValues(Connection con, QueryParameter qp) throws Exception {
        ArrayList<Serializable> result = new ArrayList<Serializable>();        
        List<IdName> list = getSelectValues(con, qp.getDefaultSource(), false, QueryParameter.NO_ORDER);
        if (QueryParameter.SINGLE_SELECTION.equals(qp.getSelection())) {
            for (IdName in : list) {
                result.add(in.getId());
            }
        } else {
            for (IdName in : list) {
                result.add(in);
            }
        }
        return result;
    }


    private static void setName(String shownColumnName, IdName in, ResultSet rs, int typeCode) throws SQLException {
        if (shownColumnName == null) {
            return;
        }
        switch (typeCode) {
            case Types.INTEGER:
                in.setName(rs.getInt(2));
                break;
            case Types.BIGINT:
                in.setName(rs.getBigDecimal(2));
                break;
            case Types.DOUBLE:
                in.setName(rs.getDouble(2));
                break;
            case Types.DATE:
                in.setName(rs.getDate(2));
                break;
            case Types.TIME:
                in.setName(rs.getTime(2));
                break;
            case Types.TIMESTAMP:
                in.setName(rs.getTimestamp(2));
                break;
            case Types.VARCHAR:
                in.setName(rs.getString(2));
                break;
            default:
                throw new SQLException("NEXTREPORTS -> getColumnValues/setName: type for value cannot be Serialized.");

        }
    }

    /**
     * Get child dependent parameters
     *
     * @param report next report object
     * @param p      current parameter
     * @return a map of all parameters that use the current parameter in theirs source definition
     */
    public static Map<String, QueryParameter> getChildDependentParameters(Report report, QueryParameter p) {
    	if (report == null) {
    		return new HashMap<String, QueryParameter>();
    	}
        return getChildDependentParameters(report.getParameters(), p);
    }

    /**
     * Get child dependent parameters
     *
     * @param params list of parameters
     * @param p      current parameter
     * @return a map of all parameters that use the current parameter in theirs source definition
     */
    public static Map<String, QueryParameter> getChildDependentParameters(List<QueryParameter> params, QueryParameter p) {
        Map<String, QueryParameter> result = new HashMap<String, QueryParameter>();
        for (QueryParameter param : params) {
            if (!param.equals(p)) {
                if (param.isDependent()) {
                    List<String> names = param.getDependentParameterNames();
                    if (names.contains(p.getName())) {
                        result.put(param.getName(), param);
                    }
                }
            }
        }
        return result;
    }


    /**
     * Get parent dependent parameters
     *
     * @param report next report object
     * @param p      current parameter
     * @return a map of all parameters that are used in the source definition of the current parameter
     */
    public static Map<String, QueryParameter> getParentDependentParameters(Report report, QueryParameter p) {
        return getParentDependentParameters(report.getParameters(), p);
    }

    /**
     * Get parent dependent parameters
     *
     * @param params list of parameters
     * @param p      current parameter
     * @return a map of all parameters that are used in the source definition of the current parameter
     */
    public static Map<String, QueryParameter> getParentDependentParameters(List<QueryParameter> params, QueryParameter p) {
        List<String> names = p.getDependentParameterNames();
        Map<String, QueryParameter> result = new HashMap<String, QueryParameter>();
        for (String name : names) {        	
            result.put(name, getParameterByName(params, name));
        }
        return result;
    }

    /**
     * Get parameter by name
     *
     * @param report        next report object
     * @param parameterName parameter name
     * @return return paramater with the specified name, null if parameter not found
     */
    public static QueryParameter getParameterByName(Report report, String parameterName) {
        return getParameterByName(report.getParameters(), parameterName);
    }

    /**
     * Get parameter by name
     *
     * @param params        list of parameters
     * @param parameterName parameter name
     * @return return paramater with the specified name, null if parameter not found
     */
    public static QueryParameter getParameterByName(List<QueryParameter> params, String parameterName) {
        for (QueryParameter parameter : params) {
            if (parameter.getName().equals(parameterName)) {
                return parameter;
            }
        }
        return null;
    }

    /**
     * Get used parameters map where the key is the parameter name and the value is the parameter
     * Not all the report parameters have to be used, some may only be defined for further usage.
     * The result will contain also the hidden parameters.
     *
     * @param report next report object
     * @return used parameters map
     */
    public static Map<String, QueryParameter> getUsedParametersMap(Report report) {
        return getUsedParametersMap(report, true, false);
    }

    /**
     * Get used parameters map where the key is the parameter name and the value is the parameter
     * Not all the report parameters have to be used, some may only be defined for further usage.
     * The result will not contain the hidden parameters.
     *
     * @param report next report object
     * @return used not-hidden parameters map
     */
    public static Map<String, QueryParameter> getUsedNotHiddenParametersMap(Report report) {
        return getUsedParametersMap(report, false, false);
    }
    
    /**
     * Get used hidden parameters map where the key is the parameter name and the value is the parameter
     * Not all the report parameters have to be used, some may only be defined for further usage.
     * The result will contain only the hidden parameters.
     *
     * @param report next report object
     * @return used hidden parameters map
     */
    public static Map<String, QueryParameter> getUsedHiddenParametersMap(Report report) {
        return getUsedParametersMap(report, false, true);
    }

    private static Map<String, QueryParameter> getUsedParametersMap(Report report, boolean withHidden, boolean onlyHidden) {  
    	if (report == null) {
    		return new HashMap<String, QueryParameter>();
    	}
        String sql = report.getSql();
        if (sql == null) {
            sql = report.getQuery().toString();
        }
        Query query = new Query(sql);

        String[] paramNames = query.getParameterNames();
        LinkedHashMap<String, QueryParameter> params = new LinkedHashMap<String, QueryParameter>();
        for (QueryParameter qp : report.getParameters()) {
            String name = qp.getName();
            boolean found = false;
            for (String pName : paramNames) {
                if (pName.equals(name)) {
                	if (onlyHidden) {
                		if (qp.isHidden()) {
                			found = true;
                			break;
                		}
                	} else if (!qp.isHidden() || (qp.isHidden() && withHidden)) {
                        found = true;
                        break;
                    }
                }
            }
            // parameter is not used inside query but it is used inside other parameter
            if (!found) {
            	if (getChildDependentParameters(report, qp).size() > 0) {
            		found = true;            	
            	} 
            }
            
            if (onlyHidden) {
            	if  (found) {
            		params.put(name, qp);
            	}
			} else {
				if (found || (qp.isHidden() && withHidden)) {
					params.put(name, qp);
				}
			}
        }
        return params;
    }


    /**
     * Get used parameters map where the key is the parameter name and the value is the parameter
     * Not all the report parameters have to be used, some may only be defined for further usage.
     * The result will contain also the hidden parameters and all parameters used just inside other parameters.
     *
     * @param query         query object
     * @param allParameters parameters map
     * @return used parameters map
     */
    public static Map<String, QueryParameter> getUsedParametersMap(Query query, Map<String, QueryParameter> allParameters) {    	
        Set<String> paramNames = new HashSet<String>(Arrays.asList(query.getParameterNames()));
        for (QueryParameter p : allParameters.values()) {        	
        	paramNames.addAll(p.getDependentParameterNames());
        }
        LinkedHashMap<String, QueryParameter> params = new LinkedHashMap<String, QueryParameter>();
        for (String name : allParameters.keySet()) {        	
            boolean found = false;
            for (String pName : paramNames) {
                if (pName.equals(name)) {
                    found = true;
                    break;
                }
            }            
            QueryParameter qp = allParameters.get(name);
            if (found || qp.isHidden()) {
                params.put(name, qp);
            }
        }        
        return params;
    }
    
    /**
     * Get ordered parameters map (dependent parameters are after their dependents)  where the key is the parameter name and 
     * the value is the parameter
     * The result will contain also the hidden parameters and all parameters used just inside other parameters.
     *
     * @param query         query object
     * @param allParameters parameters map
     * @return ordered parameters map
     */
    public static Map<String, QueryParameter> getOrderedParametersMap(Query query, Map<String, QueryParameter> allParameters) {    	
        Set<String> paramNames = new LinkedHashSet<String>(Arrays.asList(query.getParameterNames()));
        for (QueryParameter p : allParameters.values()) {   
        	if (p.getDependentParameterNames().size() > 0) {
        		paramNames.remove(p.getName());
        	}
        	paramNames.addAll(p.getDependentParameterNames());
        	if (p.getDependentParameterNames().size() > 0) {
        		paramNames.add(p.getName());
        	}
        }        
        LinkedHashMap<String, QueryParameter> params = new LinkedHashMap<String, QueryParameter>();
        for (String name :paramNames) {        	            
            QueryParameter qp = allParameters.get(name);            
            params.put(name, qp);            
        }        
        return params;
    }
    

    /**
     * Get used parameters map where the key is the parameter name and the value is the parameter
     * Not all the report parameters have to be used, some may only be defined for further usage.
     * The result will contain also the hidden parameters.
     *
     * @param sql           sql
     * @param allParameters parameters map
     * @return used parameters map
     */
    public static Map<String, QueryParameter> getUsedParametersMap(String sql, Map<String, QueryParameter> allParameters) {
        Query query = new Query(sql);
        return getUsedParametersMap(query, allParameters);
    }


    /**
     * See if all parameters are hidden
     *
     * @param map map of parameters
     * @return true if all parameters are hidden
     */
    public static boolean allParametersAreHidden(Map<String, QueryParameter> map) {
        for (QueryParameter qp : map.values()) {
            if (!qp.isHidden()) {
                return false;
            }
        }
        return true;
    }

    /**
     * See if all parameters have default values
     *
     * @param map map of parameters
     * @return true if all parameters have default values
     */
    public static boolean allParametersHaveDefaults(Map<String, QueryParameter> map) {
        for (QueryParameter qp : map.values()) {
            if ((qp.getDefaultValues() == null) || (qp.getDefaultValues().size() == 0)) {
                if ((qp.getDefaultSource() == null) || "".equals(qp.getDefaultSource().trim())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Init parameter values map with all the values
     *
     * @param param           parameter
     * @param values          all parameter values
     * @param parameterValues map of parameter values
     * @throws QueryException if could not get parameter values
     */
    public static void initAllRuntimeParameterValues(QueryParameter param,
                                                  List<IdName> values, Map<String, Object> parameterValues) throws QueryException {

        if (param.getSelection().equals(QueryParameter.SINGLE_SELECTION)) {
            parameterValues.put(param.getName(), values.get(0));
        } else {
            Object[] val = new Object[values.size()];
            for (int k = 0, size = values.size(); k < size; k++) {
                val[k] = values.get(k);
            }
            parameterValues.put(param.getName(), val);
        }
    }
    
    /**
     * Init parameter values map with the default values
     *
     * @param param           parameter
     * @param defValues       default values
     * @param parameterValues map of parameter values
     * @throws QueryException if could not get default parameter values
     */
    public static void initDefaultParameterValues(QueryParameter param,
                                                  List<Serializable> defValues, Map<String, Object> parameterValues) throws QueryException {

        if (param.getSelection().equals(QueryParameter.SINGLE_SELECTION)) {
            parameterValues.put(param.getName(), defValues.get(0));
        } else {
            Object[] val = new Object[defValues.size()];
            for (int k = 0, size = defValues.size(); k < size; k++) {
                val[k] = defValues.get(k);
            }
            parameterValues.put(param.getName(), val);
        }
    }
    
    /**
     * Init parameter values map with the default values
     *
     * @param param           parameter
     * @param defValues       default values
     * @param parameterValues map of parameter values
     * @throws QueryException if could not get default parameter values
     */
    public static void initDefaultSParameterValues(QueryParameter param,
                                                  List<Serializable> defValues, Map<String, Serializable> parameterValues) throws QueryException {

        if (param.getSelection().equals(QueryParameter.SINGLE_SELECTION)) {
            parameterValues.put(param.getName(), defValues.get(0));
        } else {
            Object[] val = new Object[defValues.size()];
            for (int k = 0, size = defValues.size(); k < size; k++) {
                val[k] = defValues.get(k);
            }
            parameterValues.put(param.getName(), val);
        }
    }
    
    /**
     * Init parameter values map with the static default values of a parameter
     *     
     * @param param           parameter
     * @param parameterValues map of parameter values
     * @throws QueryException if could not get default parameter values
     */
    public static void initStaticDefaultParameterValues(QueryParameter param,
                                                  Map<String, Object> parameterValues) throws QueryException {
        List<Serializable> defValues;
        if ((param.getDefaultValues() != null) && (param.getDefaultValues().size() > 0)) {
            defValues = param.getDefaultValues();
        } else {
			throw new QueryException(
					"Invalid use of method initStaticDefaultParameterValues : no static values for parameter "
							+ param.getName());
        }
        initDefaultParameterValues(param, defValues, parameterValues);
    }

    /**
     * Init parameter values map with the default values (static or dynamic) of a parameter 
     *
     * @param conn            database connection
     * @param param           parameter
     * @param parameterValues map of parameter values
     * @throws QueryException if could not get default parameter values
     */
    public static void initDefaultParameterValues(Connection conn, QueryParameter param,
                                                  Map<String, Object> parameterValues) throws QueryException {
        List<Serializable> defValues;
        if ((param.getDefaultValues() != null) && (param.getDefaultValues().size() > 0)) {
            defValues = param.getDefaultValues();
        } else {
            try {
                defValues = ParameterUtil.getDefaultSourceValues(conn, param);
            } catch (Exception e) {
                throw new QueryException(e);
            }
        }
        initDefaultParameterValues(param, defValues, parameterValues);
    }
    
    /**
     * Init parameter values map with all the values from select source of a parameter at runtime 
     *
     * @param conn            database connection
     * @param param           parameter
     * @param map			  report map of parameters
     * @param parameterValues map of parameter values
     * @throws QueryException if could not get parameter values
     */
    public static void initAllRuntimeParameterValues(Connection conn, QueryParameter param, Map<String,QueryParameter> map,
                                                  Map<String, Object> parameterValues) throws QueryException {
        
    	List<IdName> allValues = new ArrayList<IdName>();
        if ((param.getSource() != null) && (!param.getSource().trim().equals(""))) {           
            try {
                allValues = ParameterUtil.getRuntimeParameterValues(conn, param, map, parameterValues);
            } catch (Exception e) {
                throw new QueryException(e);
            }
        }
        initAllRuntimeParameterValues(param, allValues, parameterValues);
    }
    
    /**
     * Init parameter values map with the default values (static or dynamic) of a parameter 
     *
     * @param conn            database connection
     * @param param           parameter
     * @param parameterValues map of parameter values
     * @throws QueryException if could not get default parameter values
     */
    public static void initDefaultSParameterValues(Connection conn, QueryParameter param,
                                                  Map<String, Serializable> parameterValues) throws QueryException {    	
        List<Serializable> defValues;
        if ((param.getDefaultValues() != null) && (param.getDefaultValues().size() > 0)) {
            defValues = param.getDefaultValues();
        } else {
            try {
                defValues = ParameterUtil.getDefaultSourceValues(conn, param);
            } catch (Exception e) {            	
                throw new QueryException(e);
            }
        }
        initDefaultSParameterValues(param, defValues, parameterValues);
    }

    /**
     * Init parameter values map with the static default values for all not-hidden parameters of a report
     *     
     * @param report          report
     * @param parameterValues map of parameter values
     * @throws QueryException if could not get default parameter values
     */
    public static void initStaticNotHiddenDefaultParameterValues(Report report,
                                                           Map<String, Object> parameterValues) throws QueryException {
        Map<String, QueryParameter> params = getUsedParametersMap(report);
        for (QueryParameter qp : params.values()) {
            if (!qp.isHidden()) {
                initStaticDefaultParameterValues(qp, parameterValues);
            }
        }
    }
    
    /**
     * Init parameter values map with the default values (static or dynamic) for all not-hidden parameters of a report 
     *
     * @param conn            database connection
     * @param report          report
     * @param parameterValues map of parameter values
     * @throws QueryException if could not get default parameter values
     */
    public static void initNotHiddenDefaultParameterValues(Connection conn, Report report,
                                                           Map<String, Object> parameterValues) throws QueryException {
        Map<String, QueryParameter> params = getUsedParametersMap(report);
        for (QueryParameter qp : params.values()) {
            if (!qp.isHidden()) {
                initDefaultParameterValues(conn, qp, parameterValues);
            }
        }
    }
    
    /** Check if report has at least a parameter with a default source (will need a connection to get values)
     * 
     * @param report report
     * @return true if there is at least a parameter with a default source
     */
    public static boolean checkForParametersWithDefaultSource(Report report) {
    	Map<String, QueryParameter> params = getUsedParametersMap(report);
        for (QueryParameter qp : params.values()) {
        	if ((qp.getDefaultSource() != null) && !qp.getDefaultSource().equals("")) {
        		return true;
        	}
        }
        return false;
    }

    /**
     * Convert a list of QueryParameter object to a map  where the key is the parameter name
     * and the value is the parameter
     *
     * @param parameters list of parameters
     * @return map of parameters
     */

    public static Map<String, QueryParameter> toMap(List<QueryParameter> parameters) {
        Map<String, QueryParameter> map = new HashMap<String, QueryParameter>();
        for (QueryParameter qp : parameters) {
            map.put(qp.getName(), qp);
        }
        return map;
    }
    
    /** Get parameter value from a string represenation
    *
    * @param parameterClass parameter class
    * @param value string value representation
    * @return parameter value from string representation
    * @throws Exception if string value cannot be parse
    */
   public static Object getParameterValueFromString(String parameterClass, String value) throws Exception {
	   return getParameterValueFromString(parameterClass, value, null);
   }
   
   /** Get parameter value from a string represenation using a pattern
   *
   * @param parameterClass parameter class
   * @param value string value representation
   * @param pattern value pattern
   * @return parameter value from string representation using pattern
   * @throws Exception if string value cannot be parse
   */
  public static Object getParameterValueFromStringWithPattern(String parameterClass, String value, String pattern) throws Exception {
	  if (pattern == null) {
		  return getParameterValueFromString(parameterClass, value);
	  } else {
		  if (QueryParameter.DATE_VALUE.equals(parameterClass) ||
		      QueryParameter.TIME_VALUE.equals(parameterClass) ||
			  QueryParameter.TIMESTAMP_VALUE.equals(parameterClass)) {
			  
			  SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			  return getParameterValueFromString(parameterClass, value, sdf);
		  } else {
			  return getParameterValueFromString(parameterClass, value);
		  }
	  }
  }

    /** Get parameter value from a string represenation
     *
     * @param parameterClass parameter class
     * @param value string value representation
     * @param sdf SimpleDateFormat used to parse Date from String
     * @return parameter value from string representation
     * @throws Exception if string value cannot be parse
     */
    public static Object getParameterValueFromString(String parameterClass, String value, SimpleDateFormat sdf) throws Exception {
        Object result = value;
        if (result == null) {
        	return result;
        }
        try {
            if (QueryParameter.INTEGER_VALUE.equals(parameterClass)) {
                result = Integer.parseInt(value);
            } else if (QueryParameter.BYTE_VALUE.equals(parameterClass)) {
                result = Byte.parseByte(value);
            } else if (QueryParameter.SHORT_VALUE.equals(parameterClass)) {
                result = Short.parseShort(value);
            } else if (QueryParameter.LONG_VALUE.equals(parameterClass)) {
                result = Long.parseLong(value);
            } else if (QueryParameter.FLOAT_VALUE.equals(parameterClass)) {
                result = Float.parseFloat(value);
            } else if (QueryParameter.DOUBLE_VALUE.equals(parameterClass)) {
                result = Double.parseDouble(value);
            } else if (QueryParameter.BOOLEAN_VALUE.equals(parameterClass)) {
                result = Boolean.parseBoolean(value);
            } else if (QueryParameter.BIGDECIMAL_VALUE.equals(parameterClass)) {
                result = new BigDecimal(value);
            } else if (QueryParameter.BIGINTEGER_VALUE.equals(parameterClass)) {
                result = new BigInteger(value);    
            } else if (QueryParameter.DATE_VALUE.equals(parameterClass)) {  
				if (sdf == null) {
					sdf = new SimpleDateFormat();
					try {
						// see StringUtil.getValueAsString
						// help us to have date drill down parameters (if no pattern is present) !
						// day and time
						result = sdf.parse(value);						
					} catch (ParseException ex) {
						// day without time
						result = DateFormat.getDateInstance().parse(value);
					}
            	} else {        
            		// server request for url query parameters (SimpleDateFormat is hardcoded)
            		result = sdf.parse(value);                		            	
            	}
            } else if (QueryParameter.TIME_VALUE.equals(parameterClass)) {
            	if (sdf != null) {
            		result = sdf.parse(value);         
            	} else {
            		result = Time.valueOf(value);
            	}
            } else if (QueryParameter.TIMESTAMP_VALUE.equals(parameterClass)) {
            	if (sdf != null) {
            		result = sdf.parse(value);         
            	} else {
            		result = Timestamp.valueOf(value);
            	}
            }
            return result;
        } catch (NumberFormatException ex) {
            throw new Exception("Cannot parse " + parameterClass + " value from text " + value);
        } catch (ParseException ex) {
            throw new Exception("Cannot parse " + parameterClass + " value from text " + value);
        }
    }
    
    /** Get a map with all the identical parameters for a list of reports
     * 
     * @param reports list of reports
     * @return a map with all the identical parameters for a list of reports
     *         see QueryParameter.compare(Object o)
     */
    public static Map<String, QueryParameter> intersectParametersMap(List<Report> reports) {
    	Map<String, QueryParameter> map = new LinkedHashMap<String, QueryParameter>();
    	if ((reports == null) || (reports.size() == 0)) {
    		return map;
    	}
    	if (reports.size() == 1) {
    		return getUsedParametersMap(reports.get(0), false, false);
    	}    	
    	Map<String, QueryParameter> firstParamMap = getUsedParametersMap(reports.get(0), false, false);  
    	Map<String, QueryParameter> secondParamMap = getUsedParametersMap(reports.get(1), false, false);
        map = intersectParametersMap(firstParamMap, secondParamMap);
        
        for (int i=2, n=reports.size(); i<n; i++) {
        	Map<String, QueryParameter> paramMap = getUsedParametersMap(reports.get(i), false, false);
        	map = intersectParametersMap(map, paramMap);
        	if (map.size() == 0) {
        		break;
        	}
        }
        return map;
    }    

	private static Map<String, QueryParameter> intersectParametersMap(
			Map<String, QueryParameter> firstParamMap,
			Map<String, QueryParameter> secondParamMap) {

		Map<String, QueryParameter> map = new LinkedHashMap<String, QueryParameter>();
		if ((firstParamMap == null) || (firstParamMap.size() == 0) || 
			(secondParamMap == null) || (secondParamMap.size() == 0)) {
			return map;
		}

		for (QueryParameter qp : secondParamMap.values()) {
			for (QueryParameter qp2 : firstParamMap.values()) {
				if (qp.compare(qp2)) {
					if (!map.containsKey(qp.getName())) {
						map.put(qp.getName(), qp);
					}
				}
			}
		}
		return map;
	}
	
	/**
	 * Get a string representation of all parameters values
	 * @param parametersValues map of parameters
	 * 
	 * @return a string representation of all parameters values
	 */
	public static String getDebugParameters(Map<String, ? extends Object> parametersValues) {
    	SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    	SimpleDateFormat dayFormat = new SimpleDateFormat("dd/MM/yyyy");
    	if (parametersValues == null) {
    		return "";
    	}
        StringBuilder sb = new StringBuilder();
        for (String key : parametersValues.keySet()) {
            Object value = parametersValues.get(key);
            sb.append(" ").append(key).append(" = ");
            if (value == null) {
                sb.append("null\r\n");
                continue;
            }
            if (value instanceof Object[]) {
                Object[] values = (Object[]) value;
                sb.append("[");
                for (int i = 0, size = values.length; i < size; i++) {
                    Object obj = values[i];
                    if (obj instanceof IdName) {
                        sb.append(((IdName) obj).getId());
                    } else if (obj instanceof Date) {
                      	sb.append(dayFormat.format((Date)obj));
                    } else if (obj instanceof Timestamp) {
                        Date date = new Date(((Timestamp)obj).getTime());
                        sb.append(timeFormat.format(date));      
                    } else {
                        sb.append(obj);
                    }
                    if (i < size - 1) {
                        sb.append(";");
                    }
                }
                sb.append("]");
            } else if (value instanceof IdName) {
                sb.append(((IdName) value).getId());
            } else if (value instanceof Date) {
            	sb.append(dayFormat.format((Date)value));
            } else if (value instanceof Timestamp) {
            	Date date = new Date(((Timestamp)value).getTime());
            	sb.append(timeFormat.format(date));  
            } else {
                sb.append(value);
            }
            sb.append("\r\n");
        }
        return sb.toString();
    }   
	
	/**
	 * Test if a parameter has a date or time class name: Date, Time, Timestamp
	 * @param qp parameter
	 * @return true if a parameter has a date or time class name
	 */
	public static boolean isDateTime(QueryParameter qp) {
		String className = qp.getValueClassName();
		return className.equals(QueryParameter.DATE_VALUE) ||
			   className.equals(QueryParameter.TIME_VALUE) ||
			   className.equals(QueryParameter.TIMESTAMP_VALUE);
	}
    

}
