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
package ro.nextreports.engine;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import ro.nextreports.engine.exporter.AlarmExporter;
import ro.nextreports.engine.exporter.Alert;
import ro.nextreports.engine.exporter.CsvExporter;
import ro.nextreports.engine.exporter.DisplayExporter;
import ro.nextreports.engine.exporter.ExporterBean;
import ro.nextreports.engine.exporter.HtmlExporter;
import ro.nextreports.engine.exporter.IndicatorExporter;
import ro.nextreports.engine.exporter.PdfExporter;
import ro.nextreports.engine.exporter.ReportTableExporter;
import ro.nextreports.engine.exporter.ResultExporter;
import ro.nextreports.engine.exporter.RtfExporter;
import ro.nextreports.engine.exporter.TsvExporter;
import ro.nextreports.engine.exporter.TxtExporter;
import ro.nextreports.engine.exporter.XlsExporter;
import ro.nextreports.engine.exporter.XmlExporter;
import ro.nextreports.engine.exporter.event.ExporterEventListener;
import ro.nextreports.engine.exporter.exception.NoDataFoundException;
import ro.nextreports.engine.exporter.util.AlarmData;
import ro.nextreports.engine.exporter.util.DisplayData;
import ro.nextreports.engine.exporter.util.IndicatorData;
import ro.nextreports.engine.exporter.util.ParametersBean;
import ro.nextreports.engine.exporter.util.TableData;
import ro.nextreports.engine.querybuilder.sql.dialect.Dialect;
import ro.nextreports.engine.queryexec.Query;
import ro.nextreports.engine.queryexec.QueryExecutor;
import ro.nextreports.engine.queryexec.QueryParameter;
import ro.nextreports.engine.queryexec.QueryResult;
import ro.nextreports.engine.util.DialectUtil;
import ro.nextreports.engine.util.QueryUtil;
import ro.nextreports.engine.util.ReportUtil;

//
// Created by IntelliJ IDEA.
//  User: mihai.panaitescu
// Date: Feb 3, 2009
// Time: 2:42:51 PM
//

/** Utilities class to run a report
 */
public class ReportRunner implements Runner {

    /** PDF output format */
    public static final String PDF_FORMAT = "PDF";
    /** EXCEL output format */
    public static final String EXCEL_FORMAT = "EXCEL";
    /** HTML output format */
    public static final String HTML_FORMAT = "HTML";
    /** RTF output format */
    public static final String RTF_FORMAT = "RTF";
    /** CSV output format */
    public static final String CSV_FORMAT = "CSV";
    /** TSV  output format */
    public static final String TSV_FORMAT = "TSV";
    /** TXT  output format */
    public static final String TXT_FORMAT = "TXT";
    /** XML output format */
    public static final String XML_FORMAT = "XML";
    /** Array of all output persistent formats */
    public static final String[] FORMATS = { PDF_FORMAT, EXCEL_FORMAT, HTML_FORMAT, RTF_FORMAT,
            CSV_FORMAT, TSV_FORMAT, TXT_FORMAT, XML_FORMAT };

    /** Memory table output format */
    public static final String TABLE_FORMAT = "TABLE";
    /** Memory alarm output format */
    public static final String ALARM_FORMAT = "ALARM";
    /** Memory indicator output format */
    public static final String INDICATOR_FORMAT = "INDICATOR";
    /** Memory display output format */
    public static final String DISPLAY_FORMAT = "DISPLAY";
    
    private Connection connection;
    private Dialect dialect;
    private Report report;
    private Map<String, Object> parameterValues = new HashMap<String, Object>();
    private String format;
    private int queryTimeout = 600; // seconds
    private ResultExporter exporter;
    private boolean cancelled = false;
    private boolean count = false;
    private List<ExporterEventListener> listenerList = new ArrayList<ExporterEventListener>();
    private String chartImagePath;
    private List<Alert> alerts;
    private boolean csv = false;
    
    // to write excel in a existing template, in a specific sheet (other sheets may contain calculations on data sheet)
    private String templateName;
    private int sheetNumber;

    /** Get database connection
     *
     * @return database connection
     */
    public Connection getConnection() {
        return connection;
    }

    /** Set database connection
     *
     * @param connection database connection
     */
    public void setConnection(Connection connection) {
        setConnection(connection, false);
    }
    
    /** Set database connection
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
       if (report != null) {
           if (this.report.getQuery() != null) {
               report.getQuery().setDialect(dialect);
           }
       }
   }

    /** Get next report object
     *
     * @return next report object
     */
    public Report getReport() {
        return report;
    }

    /** Set next report object
     *
     * @param report next report object
     */
    public void setReport(Report report) {
        this.report = report;
        if (this.report.getQuery() != null) {
            this.report.getQuery().setDialect(dialect);
        }
    }

    /** Get parameters values
     *
     * @return parameters values
     */
    public Map<String, Object> getParameterValues() {
        return parameterValues;
    }

    /** Set parameters values
     * parameterValues is a map of parameters values where the key is the parameter name
     * and the value is the parameter value(s)
     * Such parameter value can be a simple java object if the parameter has SINGLE SELECTION,
     * or in case of MULTIPLE SELECTION value is an array Object[] of java objects. For an empty
     * list of values , the value must be : new Object[]{ParameterUtil.NULL}
     *
     * If we programatically add value(s) in parameterValues for a hidden parameter, the default values
     * for that hidden parameter will be ignored and the engine will use those from the map.
     *
     * @param parameterValues parameters values
     */
    public void setParameterValues(Map<String, Object> parameterValues) {
        this.parameterValues = parameterValues;
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

    /** Get query execution timeout
     *
     * @return query execution timeout in seconds
     */
    public int getQueryTimeout() {
        return queryTimeout;
    }

    /** Set query execution  timeout
     *
     * @param queryTimeout query execution timeout in seconds
     */
    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    /** See if we want to compute count
     *
     * @return true if count will be computed, false otrherwise
     */
    public boolean isCount() {
        return count;
    }

    /** Set compute count
     *
     * @param count true for computing count
     */
    public void setCount(boolean count) {
        this.count = count;
    }
    
    
    /** Set a list of alert object for report of type alarm
     * 
     * @param alerts list of alert object
     */
    public void setAlerts(List<Alert> alerts) {
    	if (format == null) {
    		throw new IllegalStateException("You have to use setFormat with a valid output format before using setAlert!");
    	}
    	if (!ALARM_FORMAT.equals(format) && !INDICATOR_FORMAT.equals(format) && !DISPLAY_FORMAT.equals(format)) {
    		throw new IllegalStateException("You can use setAlert only for ALARM, INDICATOR or DISPLAY output formats!");
    	}
		this.alerts = alerts;
	}

	/** Export the current report to the TABLE memory format
     *
     * @throws ReportRunnerException if FluentReportRunner object is not correctly configured
     * @return true if export process finished, or false if export process was stopped
     *
     * @throws NoDataFoundException if report has no data
     */
    public boolean run() throws ReportRunnerException,
            NoDataFoundException {
        return run(null);
    }       


    /** Export the current report to the specified output format
     *
     * @param stream output stream to write the exported report
     * @throws ReportRunnerException if FluentReportRunner object is not correctly configured
     * @return true if export process finished, or false if export process was stopped
     *
     * @throws NoDataFoundException if report has no data
     */
    public boolean run(OutputStream stream) throws ReportRunnerException, NoDataFoundException {

        if ((stream == null) && !TABLE_FORMAT.equals(format) && !ALARM_FORMAT.equals(format) && !INDICATOR_FORMAT.equals(format) && !DISPLAY_FORMAT.equals(format)) {
            throw new ReportRunnerException("OutputStream cannot be null!");
        }

        if ((stream != null) && TABLE_FORMAT.equals(format)) {
            throw new ReportRunnerException("TABLE FORMAT does not need an output stream. Use run() method instead.");
        }
        
        if ((stream != null) && ALARM_FORMAT.equals(format)) {
            throw new ReportRunnerException("ALARM FORMAT does not need an output stream. Use run() method instead.");
        }
        
        if ((stream != null) && INDICATOR_FORMAT.equals(format)) {
            throw new ReportRunnerException("INDICATOR FORMAT does not need an output stream. Use run() method instead.");
        }
        
        if ((stream != null) && DISPLAY_FORMAT.equals(format)) {
            throw new ReportRunnerException("DISPLAY FORMAT does not need an output stream. Use run() method instead.");
        }
        
        if (connection == null) {
            throw new ReportRunnerException("Connection is null!");
        }
        if (report == null) {
            throw new ReportRunnerException("Report is null!");
        }
        if (!formatAllowed(format)) {
            throw new ReportRunnerException("Unsupported format : " + format + " !");
        }       

        String sql = report.getSql();
        if (sql == null) {
            // get sql from SelectQuery object (report built with next reporter !)
            sql = report.getQuery().toString();
        }
        if (sql == null) {
            throw new ReportRunnerException("Report sql expression not found");
        }

        // retrieves the report parameters
        Map<String, QueryParameter> parameters = new LinkedHashMap<String, QueryParameter>();
        List<QueryParameter> parameterList = report.getParameters();
        if (parameterList != null) {
            for (QueryParameter param : parameterList) {
                parameters.put(param.getName(), param);
            }
        }

        if (QueryUtil.restrictQueryExecution(sql)) {
            throw new ReportRunnerException("You are not allowed to execute queries that modify the database!");
        }

        if (QueryUtil.isProcedureCall(sql)) {
            if (!QueryUtil.isValidProcedureCall(sql, dialect)) {
                throw new ReportRunnerException("Invalid procedure call! Must be of form 'call (${P1}, ?)'");
            }
        }
        
        QueryResult queryResult = null;
        try {        	        	        	
            Query query = new Query(sql);
            QueryExecutor executor = new QueryExecutor(query, parameters, parameterValues, connection, count, true, csv);
            executor.setMaxRows(0);
            executor.setTimeout(queryTimeout);

            queryResult = executor.execute();

            ParametersBean bean = new ParametersBean(query, parameters, parameterValues);
                        
            ReportLayout convertedLayout = ReportUtil.getDynamicReportLayout(connection, report.getLayout(), bean);
            
            boolean isProcedure = QueryUtil.isProcedureCall(sql);
            createExporter( new ExporterBean(connection, queryTimeout, queryResult, stream, convertedLayout, 
            								 bean, report.getBaseName(), false, alerts, isProcedure));

            return exporter.export();
        } catch (NoDataFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ReportRunnerException(e);
        } finally {
        	if (queryResult != null) {
        		queryResult.close();
        	}
        }
    }

    private boolean formatAllowed(String format) {
        if (TABLE_FORMAT.equals(format) || ALARM_FORMAT.equals(format) ||  INDICATOR_FORMAT.equals(format) ||  DISPLAY_FORMAT.equals(format)) {
            return true;
        }
        
        for (String f : FORMATS) {
            if (f.equals(format)) {
                return true;
            }
        }
        
        return false;
    }
    
    private void createExporter(ExporterBean bean) {        
        if (TABLE_FORMAT.equals(format)) {     
        	bean.setConnection(null);
        	bean.setQueryTimeout(0);
        	bean.setOut(new ByteArrayOutputStream());
            exporter = new ReportTableExporter(bean);
        } else if (ALARM_FORMAT.equals(format)) {        	
        	bean.setConnection(null);
        	bean.setQueryTimeout(0);
        	bean.setOut(new ByteArrayOutputStream());
            exporter = new AlarmExporter(bean);
        } else if (INDICATOR_FORMAT.equals(format)) {        	
        	bean.setConnection(null);
        	bean.setQueryTimeout(0);
        	bean.setOut(new ByteArrayOutputStream());
            exporter = new IndicatorExporter(bean);
        } else if (DISPLAY_FORMAT.equals(format)) {        	
        	bean.setConnection(null);
        	bean.setQueryTimeout(0);
        	bean.setOut(new ByteArrayOutputStream());
            exporter = new DisplayExporter(bean);    
        } else if (PDF_FORMAT.equals(format)) {
            exporter = new PdfExporter(bean);
        } else if (CSV_FORMAT.equals(format)) {        	
            exporter = new CsvExporter(bean);
        } else if (TSV_FORMAT.equals(format)) {        	
            exporter = new TsvExporter(bean);
        } else if (TXT_FORMAT.equals(format)) {        	
            exporter = new TxtExporter(bean);
        } else if (EXCEL_FORMAT.equals(format)) {        	
            exporter = new XlsExporter(bean);
        } else if (RTF_FORMAT.equals(format)) {
            exporter = new RtfExporter(bean);
        } else if (XML_FORMAT.equals(format)) {
            exporter = new XmlExporter(bean);
        } else {
            exporter = new HtmlExporter(bean);
        }
        exporter.setDocumentTitle(report.getBaseName());
        for (ExporterEventListener listener : listenerList)  {
             exporter.addExporterEventListener(listener);
        }
        exporter.setImageChartPath(getChartImagePath());
    }

    /** Stop the export process     
     */
    public void stop() {
        if (exporter != null) {
            cancelled = true;
            exporter.setStopExport(true);
        }
    }

    /** See if export process was cancelled
     *
     * @return true if current process was cancelled, false otherwise
     */
    public boolean isCancelled()  {
        return cancelled;
    }

    /** Add an exporter event listener
     *
     * @param listener exporter event listener
     */
    public void addExporterEventListener(ExporterEventListener listener) {
        listenerList.add(listener);
    }

    /** Remove an exporter event listener
     *
     * @param listener exporter event listener
     */
    public void removeExporterEventListener(ExporterEventListener listener) {
        listenerList.remove(listener);
        if (exporter != null) {
            exporter.removeExporterEventListener(listener);
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
            return new TableData();
        }
    }   
    
   /** Get alarm data ALARM exporter
    *
    * @return alarm data for ALARM exporter
    */
    public AlarmData getAlarmData() {
    	if (ALARM_FORMAT.equals(format)) {
            AlarmExporter alarmExporter = (AlarmExporter)exporter;
            return alarmExporter.getData();
        } else {
            return new AlarmData();
        }
    }      
    
   /** Get indicator data INDICATOR exporter
    *
    * @return indicator data for INDICATOR exporter
    */
    public IndicatorData getIndicatorData() {
    	if (INDICATOR_FORMAT.equals(format)) {
    		IndicatorExporter ie = (IndicatorExporter)exporter;
    		return ie.getData();            
        } else {
            return new IndicatorData();
        }
    }
    
    /** Get display data DISPLAY exporter
    *
    * @return display data for DISPLAY exporter
    */
    public DisplayData getDisplayData() {
    	if (DISPLAY_FORMAT.equals(format)) {
    		DisplayExporter de = (DisplayExporter)exporter;
    		return de.getData();            
        } else {
            return new DisplayData();
        }
    }

	public String getChartImagePath() {
		return chartImagePath;
	}

	public void setChartImagePath(String chartImagePath) {
		this.chartImagePath = chartImagePath;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public int getSheetNumber() {
		return sheetNumber;
	}

	public void setSheetNumber(int sheetNumber) {
		this.sheetNumber = sheetNumber;
	}		        
    
}
