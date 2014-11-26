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
package ro.nextreports.engine.chart;

import java.io.OutputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportRunnerException;
import ro.nextreports.engine.Runner;
import ro.nextreports.engine.TableExporter;
import ro.nextreports.engine.exporter.exception.NoDataFoundException;
import ro.nextreports.engine.exporter.util.AlarmData;
import ro.nextreports.engine.exporter.util.IndicatorData;
import ro.nextreports.engine.exporter.util.TableData;
import ro.nextreports.engine.querybuilder.sql.dialect.Dialect;
import ro.nextreports.engine.queryexec.Query;
import ro.nextreports.engine.queryexec.QueryExecutor;
import ro.nextreports.engine.queryexec.QueryParameter;
import ro.nextreports.engine.queryexec.QueryResult;
import ro.nextreports.engine.util.DialectUtil;
import ro.nextreports.engine.util.NameType;
import ro.nextreports.engine.util.QueryUtil;

/**
 * @author Decebal Suiu
 */
public class ChartRunner implements Runner {

    public static final String GRAPHIC_FORMAT = "GRAPHIC";    
    public static final String TABLE_FORMAT = "TABLE";
    public static final String IMAGE_FORMAT = "IMAGE";
    
    // type for GRAPHIC_FORMAT
    public static final byte NO_TYPE = 0;
    public static final byte FLASH_TYPE = 1;
    public static final byte HTML5_TYPE = 2;

    private String format = GRAPHIC_FORMAT;
    private byte graphicType = FLASH_TYPE;

    private Connection connection;
    private Dialect dialect;
    private Chart chart;
	private Map<String, Object> parameterValues = new HashMap<String, Object>();
	private int queryTimeout = 600; // seconds
	private ChartExporter exporter;
    private String drillFunction;
    private String imagePath;
    private String imageName;
    private int imageWidth;
    private int imageHeight;
    private boolean csv = false;
    private String language;
                
    /**
	 * Get database connection
	 * 
	 * @return database connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Set database connection
	 * 
	 * @param connection
	 *            database connection
	 */
	public void setConnection(Connection connection) {
		setConnection(connection, false);
    }
	
	/**
	 * Set database connection
	 * 
	 * @param connection database connection
	 * @param csv true for a csv file connection
	 */
	public void setConnection(Connection connection, boolean csv) {
		this.connection = connection;
		this.csv = csv;
        try {
            dialect = DialectUtil.getDialect(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if  (chart != null) {
            if (chart.getReport().getQuery() != null) {
                chart.getReport().getQuery().setDialect(dialect);
            }           
        }
    }

	/**
	 * Get next chart object
	 * 
	 * @return next chart object
	 */
	public Chart getChart() {
		return chart;
	}

	/**
	 * Set next chart object
	 * 
	 * @param chart  next chart object
	 */
	public void setChart(Chart chart) {
		this.chart = chart;
        if (this.chart.getReport().getQuery() != null) {
            this.chart.getReport().getQuery().setDialect(dialect);
        }       
    }
	
	private void setDynamicColumns() throws Exception {
		if (chart.getYColumnQuery() != null) {
			QueryUtil qu = new QueryUtil(connection, DialectUtil.getDialect(connection));
			List<NameType> list = qu.executeQueryForDynamicColumn(chart.getYColumnQuery());
			List<String> columns = new LinkedList<String>();
			List<String> legends = new LinkedList<String>();
			for (NameType nt : list) {
				columns.add(nt.getName());
				legends.add(nt.getType());
			}			
			chart.setYColumns(columns);
			chart.setYColumnsLegends(legends);
		}
	}
	
	// after run we must delete static yColumns and yLegends (set with setDynamicColumns) from chart object
	private void resetStaticColumnsAfterRun() {
		if (chart.getYColumnQuery() != null) {				
			chart.setYColumns(new ArrayList<String>());
			chart.setYColumnsLegends(new ArrayList<String>());
		}
	}

    /** Get output format
     *
     * @return output format
     */
    public String getFormat() {
        return format;
    }

    /** Set output format
     *
     * @param format output format
     */
    public void setFormat(String format) {
        this.format = format;
    }
    
    
    /** Get graphic type
    *
    * @return graphic type
    */
    public byte getGraphicType() {
		return graphicType;
	}

    /** Set graphic type (one of FLASH_TYPE or HTML5_TYPE)
     * 
     * @param type graphic type
     */
	public void setGraphicType(byte graphicType) {
		this.graphicType = graphicType;
	}

	/**
	 * Get parameters values
	 * 
	 * @return parameters values
	 */
	public Map<String, Object> getParameterValues() {
		return parameterValues;
	}

	/**
	 * Set parameters values parameterValues is a map of parameters values where
	 * the key is the parameter name and the value is the parameter value(s)
	 * Such parameter value can be a simple java object if the parameter has
	 * SINGLE SELECTION, or in case of MULTIPLE SELECTION value is an array
	 * Object[] of java objects. For an empty list of values , the value must be
	 * : new Object[]{ParameterUtil.NULL}
	 * 
	 * If we programatically add value(s) in parameterValues for a hidden
	 * parameter, the default values for that hidden parameter will be ignored
	 * and the engine will use those from the map.
	 * 
	 * @param parameterValues
	 *            parameters values
	 */
	public void setParameterValues(Map<String, Object> parameterValues) {
		this.parameterValues = parameterValues;
	}

	/**
	 * Get query execution timeout
	 * 
	 * @return query execution timeout in seconds
	 */
	public int getQueryTimeout() {
		return queryTimeout;
	}

	/**
	 * Set query execution timeout
	 * 
	 * @param queryTimeout
	 *            query execution timeout in seconds
	 */
	public void setQueryTimeout(int queryTimeout) {
		this.queryTimeout = queryTimeout;
	}


    /** Set a drill function for onclick action
     *
     * @param drillFunction drill function text
     */
    public void setDrillFunction(String drillFunction) {
        this.drillFunction = drillFunction;
    }
    
    
    /** Get language for internationalized strings
     * 
     * @return language for internationalized strings
     */
    public String getLanguage() {
		return language;
	}

    /** Set language for internationalized strings
     * 
     * @param language language for internationalized strings
     */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Export the current chart to table
	 *
	 * @throws ReportRunnerException if ChartRunner object is not correctly configured
     * @throws NoDataFoundException if chart has no data
     * @throws InterruptedException if process was interrupted
	 * @return true if export process finished, or false if export process crashed
	 */
    public boolean run() throws ReportRunnerException,
            NoDataFoundException, InterruptedException {
        return run(null);
    }
    
    /** Execute query: not supported for chart   
   	 * 
   	 * @return QueryResult object
   	 * 
   	 * @throws ReportRunnerException if Runner object is not correctly configured
     * @throws InterruptedException if process was interrupted
   	 */
     public QueryResult executeQuery() throws ReportRunnerException, InterruptedException {
    	 throw new UnsupportedOperationException();
     }

    /**
	 * Export the current chart to the specified output format
	 * For IMAGE_FORMAT use withImagePath method.
	 * 
	 * @param stream output stream to write the exported chart
	 * @throws ReportRunnerException if ChartRunner object is not correctly configured
     * @throws NoDataFoundException if chart has no data
     * @throws InterruptedException if process was interrupted
	 * @return true if export process finished, or false if export process crashed
	 */
	public boolean run(OutputStream stream) throws ReportRunnerException,
			NoDataFoundException, InterruptedException {
        if ((stream == null) && GRAPHIC_FORMAT.equals(format)) {
            throw new ReportRunnerException("OutputStream cannot be null!");
        }

        if ((stream != null) && TABLE_FORMAT.equals(format)) {
            throw new ReportRunnerException("TABLE FORMAT does not need an output stream. Use run() method instead.");
        }

        if (connection == null) {
			throw new ReportRunnerException("Connection is null!");
		}
		if (chart == null) {
			throw new ReportRunnerException("Chart is null!");
		}

		Report report = chart.getReport();
		String sql = report.getSql();
		if (sql == null) {
			// get sql from SelectQuery object (report built with next reporter
			// !)
			sql = report.getQuery().toString();
		}
		if (sql == null) {
			throw new ReportRunnerException("Report sql expression not found");
		}
		
		try {
			setDynamicColumns();
		} catch (Exception e1) {
			throw new ReportRunnerException(e1);
		}

		// retrieves the report parameters
		Map<String, QueryParameter> parameters = new HashMap<String, QueryParameter>();
		List<QueryParameter> parameterList = report.getParameters();
		if (parameterList != null) {
			for (QueryParameter param : parameterList) {
				parameters.put(param.getName(), param);
			}
		}

		if (QueryUtil.restrictQueryExecution(sql)) {
			throw new ReportRunnerException(
					"You are not allowed to execute queries that modify the database!");
		}

		if (QueryUtil.isProcedureCall(sql)) {
			Dialect dialect = null;
			try {
				dialect = DialectUtil.getDialect(connection);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!QueryUtil.isValidProcedureCall(sql, dialect)) {
				throw new ReportRunnerException(
						"Invalid procedure call! Must be of form 'call (${P1}, ?)'");
			}
		}

		QueryResult queryResult =  null;
		try {
			Query query = new Query(sql);
			QueryExecutor executor = new QueryExecutor(query, parameters, parameterValues, connection, true, true, csv);
			executor.setMaxRows(0);
			executor.setTimeout(queryTimeout);

			queryResult = executor.execute();

			createExporter(query, parameters, parameterValues, queryResult, stream);
            return exporter.export();

		} catch (NoDataFoundException e) {
			throw e;
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ReportRunnerException(e);
		} finally {
			resetStaticColumnsAfterRun();
        	if (queryResult != null) {
        		queryResult.close();
        	}
        }
	}
	
    private void createExporter(Query query, Map<String, QueryParameter> parameters,
               Map<String, Object> parameterValues, QueryResult qr, OutputStream stream) {
        if (TABLE_FORMAT.equals(format)) {
            exporter = new ChartTableExporter(qr, chart, language);
        } else if (IMAGE_FORMAT.equals(format)) {
        	if (imagePath == null) {
        		imagePath = ".";
        	}
        	exporter = new JFreeChartExporter(parameterValues, qr, chart, imagePath, imageName, imageWidth, imageHeight, language);     
        	
        } else {
        	if (graphicType == HTML5_TYPE) {
        		exporter = new JsonHTML5Exporter(parameterValues, qr, stream, chart, drillFunction, language);
        	} else {
        		// FLASH_TYPE
        		exporter = new JsonExporter(parameterValues, qr, stream, chart, drillFunction, language);
        	}
        }
    }
    
    /** Get table data TABLE exporter
    *
    * @return table data for TABLE exporter
    */
    public TableData getTableData() {
    	if (TABLE_FORMAT.equals(format)) {
            TableExporter tableExporter = (TableExporter)exporter;
            return tableExporter.getTableData();
        } else {
        	TableData data = new TableData();
        	data.setStyle(null);
            return data;
        }
    }

   /** Get alarm data ALARM exporter
    *
    * @return alarm data for ALARM exporter
    */
    public AlarmData getAlarmData() {
    	return new AlarmData();
    }
    
   /** Get indicator data INDICATOR exporter
    *
    * @return indicator data for INDICATOR exporter
    */
    public IndicatorData getIndicatorData() {
    	return new IndicatorData();
    }
    
	/** Set image path : has meaning just for IMAGE_FORMAT type
     * 
     * @param imagePath image path
     */
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	/** Set image name : has meaning just for IMAGE_FORMAT type
     * 
     * @param imageName image name
     */
	public void setImageName(String imageName) {
		if ((imageName != null) && !imageName.endsWith(".jpg")) {
			throw new IllegalArgumentException("Image must be a jpeg file.");
		}
		this.imageName = imageName;
	}
			
	 /** Set image width : has meaning just for IMAGE_FORMAT type
     * if not set, default value is 500
     * 
     * @param imageWidth image width     
     */
	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	/** Set image height : has meaning just for IMAGE_FORMAT type
     * if not set, default value is 300
     * 
     * @param imageHeight image height     
     */
	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	/**
	 * Get chart image name after export with IMAGE_FORMAT
	 * 
	 * @return chart image name after export
	 */
	public String getChartImageName() {
		if (exporter instanceof JFreeChartExporter) {
			return ((JFreeChartExporter) exporter).getChartImageName();
		}
		return null;
	}
	
	/**
	 * Get chart image asbolute path after export with IMAGE_FORMAT
	 * 
	 * @return chart image absolute path after export
	 */
	public String getChartImageAbsolutePath() {
		if (exporter instanceof JFreeChartExporter) {
			return ((JFreeChartExporter) exporter).getChartImageAbsolutePath();
		}
		return null;
	}

}
