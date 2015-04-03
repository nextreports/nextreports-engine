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
package ro.nextreports.engine.queryexec;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.sql.CallableStatement;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.engine.querybuilder.IdNameRenderer;
import ro.nextreports.engine.querybuilder.sql.dialect.CSVDialect;
import ro.nextreports.engine.querybuilder.sql.dialect.ConnectionUtil;
import ro.nextreports.engine.querybuilder.sql.dialect.Dialect;
import ro.nextreports.engine.querybuilder.sql.dialect.DialectException;
import ro.nextreports.engine.querybuilder.sql.dialect.OracleDialect;
import ro.nextreports.engine.querybuilder.sql.dialect.SQLiteDialect;
import ro.nextreports.engine.queryexec.util.StringUtil;
import ro.nextreports.engine.util.DialectUtil;
import ro.nextreports.engine.util.ParameterUtil;
import ro.nextreports.engine.util.QueryUtil;


/**
 * Interruptible Database Queries
 *
 * This class defines the <code>executeQuery()</code> method, with posibility
 * to interrup the database query, which is similar to <code>Statement.executeQuery()</code>
 * method. The difference is that this method can be interrupted by another thread.
 *
 * @author Decebal Suiu
 */
public class QueryExecutor implements Runnable {

    public static final int DEFAULT_TIMEOUT = 20;
	public static final int DEFAULT_MAX_ROWS = 100;

	public static final String EQUAL = "=";
	public static final String NOT_EQUAL = "<>";
	public static final String GREATER = ">";
	public static final String GREATER_EQUAL = ">=";
	public static final String LESS = "<";
	public static final String LESS_EQUAL = "<=";
	public static final String LIKE = "LIKE";
	public static final String NOT_LIKE = "NOT LIKE";
	public static final String IN = "IN";
	public static final String NOT_IN = "NOT IN";
	public static final String BETWEEN = "BETWEEN";
	public static final String AND = "AND";
	public static final String NOT = "NOT";

	private static Log LOG = LogFactory.getLog(QueryExecutor.class);
	
	private Query query;
	private Map<String, QueryParameter> parameters = new HashMap<String, QueryParameter>();
	private Map<String, Object> parameterValues;
	private List<String> parameterNames = new ArrayList<String>();
	private Connection conn;

	private int timeout = DEFAULT_TIMEOUT;
	private int maxRows = DEFAULT_MAX_ROWS;

	private Thread worker;
	private final InputWrapper inputWrapper;
	private final ResultWrapper resultWrapper;
	private volatile boolean cancelRequest;
	private volatile boolean closeRequest;
    private int outputParameterPosition = -1;
    private boolean computeCount = false;    
    private boolean check = true;
    private boolean isCsv = false;

    private Map<Integer, Object> statementParameters = new HashMap<Integer, Object>();
	
    // check = false when we want to run a parameter sql query at runtime
	public QueryExecutor(Query query, Map<String,QueryParameter> parameters,
			Map<String,Object> parameterValues, Connection conn, boolean computeCount, boolean check, boolean isCsv) throws QueryException {				
		
		processIgnoreParameters(query, parameters, parameterValues);

		// check inputs!!!
		if (check) {
			checkInputs(query, parameters, parameterValues, conn);
		}

		this.query = query;
		this.conn = conn;
		this.parameterNames = query.parameterNames;
		this.parameters = parameters;
		this.parameterValues = parameterValues;
        this.computeCount = computeCount;
        this.check = check;
        this.isCsv = isCsv;

        inputWrapper = new InputWrapper();
		resultWrapper = new ResultWrapper();

		worker = new Thread(this, getClass().getSimpleName());        
        worker.start();
	}
	
	public QueryExecutor(Query query, Map<String,QueryParameter> parameters,
			Map<String,Object> parameterValues, Connection conn, boolean computeCount) throws QueryException {
		this(query, parameters, parameterValues, conn, computeCount, true, false);
	}

    public QueryExecutor(Query query, Map<String,QueryParameter> parameters,
			Map<String,Object> parameterValues, Connection conn) throws QueryException {
        this(query, parameters, parameterValues, conn, false);
    }

    public QueryExecutor(Query query, Connection conn) throws QueryException {
		this(query, new HashMap<String,QueryParameter>(), new HashMap<String,Object>(), conn);
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getMaxRows() {
		return maxRows;
	}

	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

	public List<String> getParameterNames() {
		return parameterNames;
	}

	/**
	 * Execute the query.
     * @return query result
     * @throws QueryException query exception
     * @throws InterruptedException interrupted exception
     */
	public synchronized QueryResult execute() throws QueryException, InterruptedException {
		// create query string
		String queryString = createQueryString();

        PreparedStatement countPstmt = null;
        if (computeCount) {
            try {
                // count statement
                String countQueryString = "SELECT COUNT(*) FROM (" + queryString + ") A";
                if (isCsv) {
                	// csv jdbc driver does not support sub-select
                	countQueryString = getCsvCountQuery(queryString);
                }
                countPstmt = createStatement(countQueryString);
                if (parameterNames.size() != 0) {
                    setParameterValues(countPstmt);
                }
            } catch (QueryException ex) {
                LOG.info("Cannot create count statement : " + ex.getMessage() + " .Will use rs.last()");
                countPstmt = null;
            }
        }

        // create statement
		PreparedStatement pstmt = createStatement(queryString);

        // set parameter values
		if (parameterNames.size() != 0) {
			setParameterValues(pstmt);
		}
        
        try {
            setOutParametersValues(pstmt);
        } catch (SQLException e) {
            throw new QueryException(e);
        } catch (DialectException e) {
            throw new QueryException("Error set out parameter values for executing query : could not get dialect", e);
        }

        // set query's input wrapper
		synchronized (inputWrapper) {
			inputWrapper.statement = pstmt;
            inputWrapper.countStatement = countPstmt;
            inputWrapper.query = queryString;
			inputWrapper.pending = true;
			inputWrapper.notify();
		}

		synchronized (resultWrapper) {
			try {
				// wait for the query to complete
				while (!resultWrapper.serviced) {
					resultWrapper.wait();
				}
				if (resultWrapper.exception != null) {
					throw resultWrapper.exception;
				}
			} catch (InterruptedException e) {
				cancel();
				throw e;
			} finally {
				resultWrapper.serviced = false;
			}

			if ((maxRows > 0) && (resultWrapper.count > maxRows)) {
				resultWrapper.count = maxRows;
			}

			//must finish run method thread normally
			closeRequest = true;
			synchronized (inputWrapper) {
				inputWrapper.pending = true;
				inputWrapper.notify();
			}

			return new QueryResult(resultWrapper.resultSet, resultWrapper.count, resultWrapper.executeTime);
		}
	}

	public void stop() {
		closeRequest = true;
		if ((inputWrapper.countStatement != null) || (inputWrapper.statement != null)) {
			cancel();
		}
		worker.interrupt();
		try {
			worker.join();
		} catch (InterruptedException e) {
            //nothing to do
        }
	}

	public void run() {
		ResultSet resultSet = null;
		SQLException sqlException = null;
		int count = 0;
		while (!closeRequest) {
			long executeTime = 0;
			synchronized(inputWrapper) {
				try {
					// wait for query parameters
					while(!inputWrapper.pending) {
						inputWrapper.wait();
					}
					inputWrapper.pending = false;
					if (closeRequest) {
						return;
					}
				} catch (InterruptedException e) {
					if (closeRequest) {
						return;
					}
				}
				// execute query
				try {
					executeTime = System.currentTimeMillis();
					
					Dialect dialect = null;
                    try {
                        dialect = DialectUtil.getDialect(conn);
                    } catch (DialectException e) {
                        e.printStackTrace();
                        LOG.error(e.getMessage(), e);
                    }
					
                    if (QueryUtil.isProcedureCall(query.getText()))  {
                        resultSet = inputWrapper.statement.executeQuery();                        
                        if (dialect instanceof OracleDialect) {
                            resultSet = (ResultSet)((CallableStatement)inputWrapper.statement).getObject(outputParameterPosition);
                        } 
                        // do not know how to get the number of rows
                        // last() and beforeFirst() do not work for an oracle stored procedure result set
                        // see also testForData() from ResultExporter
                        count = -1;
                    } else {                        

                        // try to get the count with a "select *"
                        // if that fails try to get the count with rs.last() (which is time & memory expensive)
                        // IMPORTANT : execute the count statement first (before the statement) ,otherwise there are
                        // drivers that will close the connection (Firebird), and an error of "result set is closed"
                        // will arise
                        count=-1;
                        boolean useLast = false;
                        if (inputWrapper.countStatement != null) {
                        	ResultSet countResultSet = null;
                            try {
                                countResultSet = inputWrapper.countStatement.executeQuery();
                                countResultSet.next();
                                count = countResultSet.getInt(1);                                
                            } catch (SQLException e) {
                                LOG.info("Cannot execute count statement : " + e.getMessage() + " .Will use rs.last()");
                                useLast = true;
                            } finally {                            	
                            	ConnectionUtil.closeStatement(inputWrapper.countStatement);                            		
                            	ConnectionUtil.closeResultSet(countResultSet);
                            	inputWrapper.countStatement = null;
                            }
                        } else {
                            if (!cancelRequest) {
                                useLast = true;
                            }
                        }
                        
                        if (!cancelRequest) {
                            resultSet = inputWrapper.statement.executeQuery();
                            
                            if (useLast && !cancelRequest && computeCount) {
                            	if ((dialect instanceof SQLiteDialect) ||(dialect instanceof CSVDialect)) {
                            		// resultSet is forward only
                            		count = -1;
                            	} else {
                            		resultSet.last();
                            		count = resultSet.getRow();
                            		resultSet.beforeFirst();
                            	}
                            }
                        }
                    }

                    executeTime = System.currentTimeMillis() - executeTime;
                    logSql(executeTime);
                    statementParameters.clear();
				} catch (SQLException e) {
					if (!cancelRequest) {
						sqlException = e;
					}
				} catch (Throwable t) {
                    // catch any driver exception and log it
                    LOG.error(t.getMessage(), t);
                    if (!cancelRequest) {
						sqlException = new SQLException("Execute query. See log for details") ;
					}
                }
			}

			// set query resultWrapper
			synchronized (resultWrapper) {
				resultWrapper.resultSet = resultSet;
				resultWrapper.count = count;
				resultWrapper.exception = (sqlException == null) ? null : new QueryException(sqlException);
				resultWrapper.serviced = true;
				resultWrapper.executeTime = executeTime;
				resultWrapper.notify();
			}		
		}
	}
		
	private void checkInputs(Query query, Map<String,QueryParameter> parameters,
			Map<String,Object> parameterValues, Connection conn) throws QueryException {
		if ((query == null) || (query.getText().trim().length() == 0)) {
			throw new QueryException("query cannot be null");
		}
		if (conn == null) {
			throw new QueryException("database connection cannot be null");
		}

        // may have hidden parameters which are not used in the query
		//System.out.println("**** keys = " + ParameterUtil.getUsedParametersMap(query, parameters).keySet());		
		//System.out.println("++++ map="+ParameterUtil.getDebugParameters(parameterValues));
		Map<String, QueryParameter> map =  ParameterUtil.getUsedParametersMap(query, parameters);
		for (String paramName : map.keySet()) {
			if ((parameters == null) || (!parameters.containsKey(paramName))) {
				throw new QueryException("cannot find parameter definition for " + paramName);
			}

            // if parameter value exists , we do not look for hidden (with default values) parameters
            //
            // if there is a value for that hidden parameter , it was set from another application
            // (like __USER__ ) and we dont overwrite it
            //
            // case when parameter is not hidden and has default values interpretted dynamically is possible
            // from NextReports Server from scheduler when parameter value has isDynamic = true
            // (and value is not put in parameters values map)
			// also if no default values are set, all parameter values will be considered (default values cannot 
			// contain parameters, so we need an extension to this)
            if (!parameterValues.containsKey(paramName)) {
            	
            	//System.out.println("**** map="+ParameterUtil.getDebugParameters(parameterValues));
            	//System.out.println("**** Parameter "+ paramName + "  get values at runtime...");
                QueryParameter param = parameters.get(paramName);
                
                boolean hasDefaultSource = (param.getDefaultSource() != null) && !"".equals(param.getDefaultSource().trim());
                boolean hasSource = (param.getSource() != null) && !"".equals(param.getSource().trim());
                
                // we must test for default values or all values for both hidden and not hidden parameters                               
				if (hasDefaultSource) {
					try {
						//System.out.println("  --> values from default source");
						ParameterUtil.initDefaultParameterValues(conn, param, parameterValues);
						//System.out.println("**** aftermap="+ParameterUtil.getDebugParameters(parameterValues));
					} catch (QueryException ex) {
						LOG.error(ex.getMessage(), ex);
						throw ex;
					}

				} else if (hasSource) {
					try {
						//System.out.println("  --> all values");
						ParameterUtil.initAllRuntimeParameterValues(conn, param, parameters, parameterValues);
						//System.out.println("**** aftermap="+ParameterUtil.getDebugParameters(parameterValues));
					} catch (QueryException ex) {
						LOG.error(ex.getMessage(), ex);
						throw ex;
					}
				} else if (param.isHidden()) {
					// hidden parameter without any source, must have some default values
					ParameterUtil.initDefaultParameterValues(conn, param, parameterValues);
				} else {
					throw new QueryException("cannot find parameter value for " + paramName);
				}
            } 

//            if (QueryUtil.isProcedureCall(query.getText())) {
//                QueryParameter qp = parameters.get(paramName);
//                if (QueryParameter.MULTIPLE_SELECTION.equals(qp.getSelection())) {
//                    throw new QueryException("Do not allow parameters with multiple selection for procedure call.");
//                }
////                if (!qp.isProcedureParameter()) {
////                    throw new QueryException("Parameter must be of stored procedure type.");
////                }
//            }
        }
	}

    private PreparedStatement createStatement(String queryString) throws QueryException {
		// create the prepared statement
		PreparedStatement pstmt;
		try {
			
			boolean hasScrollType = false;
			try {
				hasScrollType = DialectUtil.isSupportedResultSetType(conn, ResultSet.TYPE_SCROLL_INSENSITIVE);
			} catch (Exception ex) {
				ex.printStackTrace();
                LOG.error(ex.getMessage(), ex); 
			}
        	int resultSetType = hasScrollType ? ResultSet.TYPE_SCROLL_INSENSITIVE : ResultSet.TYPE_FORWARD_ONLY;
        	
            if (QueryUtil.isProcedureCall(queryString)) {
                pstmt = conn.prepareCall("{" + queryString + "}", resultSetType, ResultSet.CONCUR_READ_ONLY);
            } else {            	
            	if (isCsv) {            		
            		pstmt = conn.prepareStatement(queryString);
            	} else {
            		boolean keepCursorsOverCommit = false;
            		try {
						Dialect dialect = DialectUtil.getDialect(conn);
						keepCursorsOverCommit = dialect.needsHoldCursorsForPreparedStatement();
					} catch (DialectException e) {
						e.printStackTrace();
		                LOG.error(e.getMessage(), e); 
					}
            		if (keepCursorsOverCommit) {
            			pstmt = conn.prepareStatement(queryString, resultSetType, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
            		} else {
            			pstmt = conn.prepareStatement(queryString, resultSetType, ResultSet.CONCUR_READ_ONLY);
            		}
            	}
            }
            // ignore queryTimeout and maxRows (some drivers - derby - not implement
			// these feature yet)
			try {
				// set timeout
				pstmt.setQueryTimeout(timeout);

				// set max rows
				pstmt.setMaxRows(maxRows);
			} catch (SQLException e) {				
                LOG.warn(e); 
            }
		} catch (SQLException e) {
			throw new QueryException(e);
		}

		return pstmt;
	}

	private void setParameterValues(PreparedStatement pstmt) throws QueryException {
		try {
			QueryParameter parameter = null;
			String parameterName = null;
			Object parameterValue = null;
			int n = parameterNames.size();
			for (int i = 0, j = 0; (i < n) && (j < n); i++, j++) {
				parameterName = parameterNames.get(j);
				parameter = parameters.get(parameterName);
				parameterValue = parameterValues.get(parameterName);
				
				if (QueryParameter.MULTIPLE_SELECTION.equals(parameter.getSelection())) {
					Object[] multiParamValue = (Object[]) parameterValue;
					if (QueryUtil.isProcedureCall(query.getText())) {						
						// a multiple parameter in procedure call -> value is the list of values
						int index = i;
						if ((outputParameterPosition != -1) && (outputParameterPosition <= i + 1)) {
                            index = i + 1;
                        }
						StringBuilder sb= new StringBuilder();
						for (int k = 0; k < multiParamValue.length; k++) {
							Object v = multiParamValue[k];
							if (v instanceof IdName) {
								v = ((IdName)v).getId();
							} else if (v instanceof String) {
								sb.append("'");
							}
							sb.append(v);
							if (v instanceof String) {
								sb.append("'");
							}
							if (k < multiParamValue.length-1) {
								sb.append(",");
							}
						}											
						setParameterValue(pstmt, String.class, sb.toString(), index, true);
					} else {
						for (int k = 0; k < multiParamValue.length; k++) {
							setParameterValue(pstmt, parameter.getValueClass(), multiParamValue[k], i);
							i ++;
							n ++;
						}
						i--;
						n--;
					}
				} else {
                    int index = i;
                    // for procedure call if output parameter is before a query parameter
                    if (QueryUtil.isProcedureCall(query.getText())) {
                        if ((outputParameterPosition != -1) && (outputParameterPosition <= i + 1)) {
                            index = i + 1;
                        }
                    }                    
                    setParameterValue(pstmt, parameter.getValueClass(), parameterValue, index);
				}
			}

        } catch (SQLException e) {
			throw new QueryException("Error set parameter values for executing query", e);
		}
	}

    private void setOutParametersValues(PreparedStatement pstmt) throws SQLException, DialectException {
        Dialect dialect = DialectUtil.getDialect(conn);
        if (QueryUtil.isProcedureCall(query.getText())) {
            if (dialect.hasProcedureWithCursor()) {
                ((CallableStatement) pstmt).registerOutParameter(outputParameterPosition,dialect.getCursorSqlType());
            }            
        }
    }
    
    private void setParameterValue(PreparedStatement pstmt, Class paramValueClass,
			Object paramValue, int index) throws SQLException, QueryException {
    	setParameterValue(pstmt, paramValueClass, paramValue, index, false);
    }

    private void setParameterValue(PreparedStatement pstmt, Class paramValueClass,
			Object paramValue, int index, boolean isProcedureMultiple) throws SQLException, QueryException {

        // for "NOT IN (?)" setting null -> result is undeterminated
        // ParameterUtil.NULL was good only for list of strings (for NOT IN)!
        if (ParameterUtil.NULL.equals(paramValue)) {
            paramValue = null;
        }
        
        if (isProcedureMultiple) {
        	// a multiple selection parameter used inside a procedure is seen as a single parameter 
        	// with the value equals to the list of values
        	if (paramValue == null) {
				pstmt.setNull(index + 1, Types.VARCHAR);
			} else {								
				pstmt.setString(index + 1, (String)paramValue);
			}	
        } else if (paramValueClass.equals(Object.class)) {
			if (paramValue == null) {
				pstmt.setNull(index + 1, Types.JAVA_OBJECT);
			} else {
				if (paramValue instanceof IdName) {
					pstmt.setObject(index + 1, ((IdName)paramValue).getId());
				} else {
					pstmt.setObject(index + 1, paramValue);
				}
			}
		} else if (paramValueClass.equals(Boolean.class)) {
			if (paramValue == null) {
				pstmt.setNull(index + 1, Types.BIT);
			} else {
				if (paramValue instanceof IdName) {
					pstmt.setBoolean(index + 1, (Boolean)((IdName) paramValue).getId());
				} else {
					pstmt.setBoolean(index + 1, (Boolean) paramValue);
				}
			}
		} else if (paramValueClass.equals(Byte.class)) {
			if (paramValue == null) {
				pstmt.setNull(index + 1, Types.TINYINT);
			} else {
				if (paramValue instanceof IdName) {
					pstmt.setByte(index + 1, (Byte)((IdName) paramValue).getId());
				} else {
					pstmt.setByte(index + 1, (Byte) paramValue);
				}
			}
		} else if (paramValueClass.equals(Double.class)) {
			if (paramValue == null) {
				pstmt.setNull(index + 1, Types.DOUBLE);
			} else {
				if (paramValue instanceof IdName) {
					pstmt.setDouble(index + 1, (Double)((IdName) paramValue).getId());
				} else {
					pstmt.setDouble(index + 1, (Double) paramValue);
				}
			}
		} else if (paramValueClass.equals(Float.class)) {
			if (paramValue == null) {
				pstmt.setNull(index + 1, Types.FLOAT);
			} else {
				if (paramValue instanceof IdName) {
					pstmt.setFloat(index + 1, (Float)((IdName) paramValue).getId());
				} else {
					pstmt.setFloat(index + 1, (Float) paramValue);
				}
			}
		} else if (paramValueClass.equals(Integer.class)) {
			if (paramValue == null) {
				pstmt.setNull(index + 1, Types.INTEGER);
			} else {
				if (paramValue instanceof IdName) {
					pstmt.setObject(index + 1, ((IdName) paramValue).getId());
				} else {                    
                    pstmt.setInt(index + 1, (Integer) paramValue);
				}
			}
		} else if (paramValueClass.equals(Long.class)) {
			if (paramValue == null) {
				pstmt.setNull(index + 1, Types.BIGINT);
			} else {
				if (paramValue instanceof IdName) {
					pstmt.setLong(index + 1, (Long)((IdName) paramValue).getId());
				} else {
					pstmt.setLong(index + 1, (Long) paramValue);
				}
			}
		} else if (paramValueClass.equals(Short.class)) {
			if (paramValue == null) {
				pstmt.setNull(index + 1, Types.SMALLINT);
			} else {
				if (paramValue instanceof IdName) {
					pstmt.setShort(index + 1, (Short)((IdName) paramValue).getId());
				} else {
					pstmt.setShort(index + 1, (Short) paramValue);
				}
			}

        //@todo    
        // ParameterUtil -> values are taken from dialect (where there is no BigDecimal yet!)
        // or from meta  data
        } else if (paramValueClass.equals(BigDecimal.class)) {        	
			if (paramValue == null) {
				pstmt.setNull(index + 1, Types.DECIMAL);
			} else {
				if (paramValue instanceof IdName) {
                    Serializable ser = ((IdName) paramValue).getId();
                    if (ser instanceof BigDecimal) {
                        pstmt.setBigDecimal(index + 1, (BigDecimal)(ser));
                    } else {
                        pstmt.setInt(index + 1, (Integer)(ser));
                    }
                } else {
                	// a simple value cannot be cast to BigDecimal!                	
					pstmt.setObject(index + 1, paramValue);
				}
			}
        } else if (paramValueClass.equals(BigInteger.class)) {        	
			if (paramValue == null) {
				pstmt.setNull(index + 1, Types.BIGINT);
			} else {
				if (paramValue instanceof IdName) {
                    Serializable ser = ((IdName) paramValue).getId();
                    if (ser instanceof BigInteger) {
                        pstmt.setBigDecimal(index + 1, new BigDecimal((BigInteger)(ser)));
                    } else if (ser instanceof BigDecimal) {
                        pstmt.setBigDecimal(index + 1, (BigDecimal)(ser));                        
                    } else {
                        pstmt.setInt(index + 1, (Integer)(ser));
                    }
                } else {
                	// a simple value cannot be cast to BigDecimal!                	
					pstmt.setObject(index + 1, paramValue);
				}
			}	
		} else if (paramValueClass.equals(String.class)) {
			if (paramValue == null) {
				pstmt.setNull(index + 1, Types.VARCHAR);
			} else {
				if (paramValue instanceof IdName) {
					if (((IdName)paramValue).getId() == null) {
						pstmt.setNull(index + 1,Types.VARCHAR);
					} else {
						pstmt.setString(index + 1, ((IdName)paramValue).getId().toString());
					}
				} else {
					pstmt.setString(index + 1, paramValue.toString());
				}
			}
		} else if (paramValueClass.equals(Date.class)) {
			if (paramValue == null) {
				pstmt.setNull(index + 1, Types.DATE);
			} else {
				if (paramValue instanceof IdName) {
                    Serializable obj = ((IdName)paramValue).getId();
                    Date date;
                    if (obj instanceof String) {
                        try {
                            date = IdNameRenderer.sdf.parse((String)obj);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            LOG.error(e.getMessage(), e);
                            date = new Date();
                        }
                    } else {
                        date = (Date)obj;
                    }
                    pstmt.setDate(index + 1, new java.sql.Date(date.getTime()));
				} else {
					pstmt.setDate(index + 1, new java.sql.Date(((Date) paramValue).getTime()));
				}
			}
		} else if (paramValueClass.equals(Timestamp.class)) {
			if (paramValue == null) {
				pstmt.setNull(index + 1, Types.TIMESTAMP);
			} else {
				if (paramValue instanceof IdName) {
                    Serializable obj = ((IdName)paramValue).getId();
                    Date date;
                    if (obj instanceof String) {
                        try {
                            date = IdNameRenderer.sdf.parse((String)obj);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            LOG.error(e.getMessage(), e);
                            date = new Date();
                        }
                    } else {
                        date = (Date)obj;
                    }

                    pstmt.setTimestamp(index + 1, new Timestamp(date.getTime()));
				} else {                    
                    pstmt.setTimestamp(index + 1, new Timestamp(((Date) paramValue).getTime()));
				}
			}
		} else if (paramValueClass.equals(Time.class)) {
			if (paramValue == null) {
				pstmt.setNull(index + 1, Types.TIME);
			} else {
				if (paramValue instanceof IdName) {
                    Serializable obj = ((IdName)paramValue).getId();
                    Date date;
                    if (obj instanceof String) {
                        try {
                            date = IdNameRenderer.sdf.parse((String)obj);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            LOG.error(e.getMessage(), e);
                            date = new Date();
                        }
                    } else {
                        date = (Date)obj;
                    }
                    pstmt.setTime(index + 1, new Time( date.getTime()));
				} else {
					pstmt.setTime(index + 1, new Time (((Date) paramValue).getTime()));
				}
			}
		} else {
			throw new QueryException("Parameter type " + paramValueClass.getName() +
					" not supported in query");
		}
        
        // for logSql()
        statementParameters.put(index, paramValue);
	}

	/**
	 * Parse query and return a string query  that will be passed to prepared
	 * statement(substitute parameter with '?' char).
	 */
	private String createQueryString() {
        QueryChunk[] chunks = query.getChunks();
		if ((chunks == null) || (chunks.length == 0)) {
			// no query chunks
			return "";
		}

		StringBuffer sb = new StringBuffer();
        QueryChunk chunk = null;
        int position = 1;
        for (int i = 0; i < chunks.length; i++) {
			chunk = chunks[i];
			//System.out.println("chunk = " + chunk);
			switch (chunk.getType()) {
				case QueryChunk.PARAMETER_TYPE: {
                    position++;
                    String paramName = chunk.getText();
					QueryParameter param = parameters.get(paramName);					
					if (QueryParameter.MULTIPLE_SELECTION.equals(param.getSelection())) {
						if (QueryUtil.isProcedureCall(query.getText())) {		
							sb.append("?");
						} else {
							Object[] paramValue = (Object[]) parameterValues.get(paramName);						
							sb.append('(');
							for (int j = 0; j < paramValue.length; j++) {
								if (j > 0) {
									sb.append(',');
								}
								sb.append('?');
							}
							sb.append(')');
						}
					} else {
						sb.append("?");
					}
					break;
				}
				case QueryChunk.TEXT_TYPE:
                    if (chunk.getText().contains("?")) {
                        outputParameterPosition = position;                        
                    }
                default: {
					sb.append(chunk.getText());
					break;
				}
			}
		}        
        return sb.toString();
	}

	private void cancel() {
		cancelRequest = true;
		try {			
            if (inputWrapper.countStatement != null) {
                inputWrapper.countStatement.cancel();
            }

            inputWrapper.statement.cancel();
            
            synchronized(resultWrapper) {
				while(!resultWrapper.serviced) {
					resultWrapper.wait();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			cancelRequest = false;
		}
	}

	private void processIgnoreParameters(Query query, Map<String,QueryParameter> parameters,
			Map<String,Object> parameterValues) {

		List<String> ignoredList = new ArrayList<String>();
		if (parameters == null) {
			return;
		}
		for (Iterator it = parameters.keySet().iterator(); it.hasNext();) {
			String name = (String)it.next();
			QueryParameter param = parameters.get(name);
			if (param.isIgnore()) {
			   ignoredList.add("${" + name + "}");
			   it.remove();
			   parameterValues.remove(name);
			}
		}

		//System.out.println("----------- processIgnoreParameters");
		//System.out.println("parameters ignoredList = " + ignoredList);
		if (ignoredList.size() == 0) {
			return;
		}

		String sql = StringUtil.deleteExcededSpaces(query.getText());
		//System.out.println("sql = " + sql);
		// removed indexex
		Set<Integer> indexes = new HashSet<Integer>();
		Set<Integer> columns = new HashSet<Integer>();
		// split using any space characters
		String[] words = sql.split("\\s");
		for (int i=0, size=words.length; i<size; i++) {
			int pIndex = findIndex(words[i], ignoredList);
			//System.out.println("  --> i="+i + " word=" + words[i] + "   pIndex="+pIndex);
			if (pIndex != -1) {
				indexes.add(i);
				indexes.add(i-1);
				indexes.add(i-2);
				columns.add(i-2);
				//System.out.println("Find parameter " + words[i]);
				if (words[i-1].equalsIgnoreCase(IN) || words[i-1].equalsIgnoreCase(LIKE)) {
					// check for NOT
					if (words[i-2].equalsIgnoreCase(NOT)) {
					   indexes.add(i-3);
					}
				} else  if (words[i-1].equalsIgnoreCase(AND)) {
					// BETWEEN
					indexes.add(i-3);
					indexes.add(i-4);
					// only one 1=1 must be added for a between
					columns.remove(i-2);
				} else  if (words[i-1].equalsIgnoreCase(BETWEEN)) {
					// AND
					indexes.add(i+1);
					indexes.add(i+2);
				}
			}
		}

		StringBuilder newSql = new StringBuilder();
		for (int i=0, size=words.length; i<size; i++) {
			boolean removed = false;
			for (Integer index : indexes) {
				if (index.equals(i)) {
					removed = true;
				}
			}
			if (!removed) {
				newSql.append(words[i]);
				if (i < size-1) {
					newSql.append(" ");
				}
			} else {
				if (columns.contains(i)) {
					newSql.append(" 1 = 1 ");
				}
			}
		}

		//System.out.println("newSQL = " + newSql.toString());

		query.setText(newSql.toString());
		//System.out.println("----------- end processIgnoreParameters");
	}

	private int findIndex(String look, List<String> list) {
		for (int i=0, size=list.size(); i<size; i++) {
			String s = list.get(i);
			if (look.equalsIgnoreCase(s)) {
				return i;
			}
		}

		return -1;
	}
	
	private void logSql(long time) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(inputWrapper.query);
		}
		if (LOG.isInfoEnabled()) {
			String preparedSql = inputWrapper.query;
			StringBuffer displayableSql = new StringBuffer(preparedSql.length());

			if (statementParameters != null) {
				int i = 0;
				int limit = 0;
				int base = 0;
				Object value;
				while ((limit = preparedSql.indexOf('?', limit)) != -1) {
					displayableSql.append(preparedSql.substring(base, limit));
					value = statementParameters.get(i);
					if (value instanceof IdName) {
						Object idName = ((IdName) value).getId();
						if (idName instanceof String) {
							displayableSql.append("'");
							displayableSql.append(value);
							displayableSql.append("'");							
						} else {
							displayableSql.append(value);
						}
					} else if (value instanceof String) {
						displayableSql.append("'");
						displayableSql.append(value);
						displayableSql.append("'");
					} else if (value instanceof Date) {
						displayableSql.append(SimpleDateFormat.getDateTimeInstance().format(value));
					} else if (value == null) {
						displayableSql.append("NULL");
					} else {
						displayableSql.append(value);
					}
					i++;
					limit++;
					base = limit;
				}

				if (base < preparedSql.length()) {
					displayableSql.append(preparedSql.substring(base));
				}
			}

			LOG.info(displayableSql + " => (" + time + " ms)");
		}
	}

	class InputWrapper {

		public PreparedStatement statement;
        public PreparedStatement countStatement;
        public String query;
		public boolean pending;

	}

	class ResultWrapper {

		public ResultSet resultSet;
		public QueryException exception;
		public int count;
		public boolean serviced;
		public long executeTime;

	}
	
	private String getCsvCountQuery(String query) {
		if (query == null) {
			return null;
		}
		query = query.toLowerCase();
		int index = query.indexOf("from");
		if (index == -1) {
			return null;
		}
		
		return "select count(*) from " + query.substring(index + 4);  						
	}

}
