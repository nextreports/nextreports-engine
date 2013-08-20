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


import java.io.OutputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import ro.nextreports.engine.exporter.Alert;
import ro.nextreports.engine.exporter.event.ExporterEventListener;
import ro.nextreports.engine.exporter.exception.NoDataFoundException;

/** Utilities class to run a report using a fluent syntax
 * 
 * @author Decebal Suiu
 */
public class FluentReportRunner {

	private ReportRunner reportRunner;

    private FluentReportRunner(Report report) {
		reportRunner = new ReportRunner();
		reportRunner.setReport(report);
	}

    /** Create a FluentReportRunner object
     *
     * @param report next report object
     * @return the newly created FluentReportRunner object
     */
    public static FluentReportRunner report(Report report) {
		return new FluentReportRunner(report);
	}

    /** Set the connection to database
     *
     * @param connection database connection
     * @return FluentReportRunner object with database connection set
     */
    public FluentReportRunner connectTo(Connection connection) {
		reportRunner.setConnection(connection);
		return this;
	}
    
    /** Set the connection to csv file
    *
    * @param connection csv file connection
    * @return FluentReportRunner object with csv file connection set
    */
   public FluentReportRunner connectToCsv(Connection connection) {
		reportRunner.setConnection(connection, true);
		return this;
	}

    /** Set the query timeout
     *
     * @param queryTimeout database execution query timeout in seconds
     * @return FluentReportRunner object with query timeout set
     */
    public FluentReportRunner withQueryTimeout(int queryTimeout) {
		reportRunner.setQueryTimeout(queryTimeout);
		return this;
	}

    /** Set the parameters values
     *
     * @param parameterValues map of parameters values where the key is the parameter name
     * and the value is the parameter value(s)
     * Such parameter value can be a simple java object if the parameter has SINGLE SELECTION,
     * or in case of MULTIPLE SELECTION value is an array Object[] of java objects. For an empty
     * list of values , the value must be : new Object[]{ParameterUtil.NULL}
     *
     * If we programatically add value(s) in parameterValues for a hidden parameter, the default values
     * for that hidden parameter will be ignored and the engine will use those from the map.
     *
     * @return FluentReportRunner object with parameters values set
     */
    public FluentReportRunner withParameterValues(Map<String, Object> parameterValues) {
		reportRunner.setParameterValues(parameterValues);
		return this;
	}
    
    /** Set path to folder where chart generated images will be saved
     * 
     * @param path folder path
     * @return FluentReportRunner object with chart image path set
     */
    public FluentReportRunner withChartImagePath(String path) {
    	reportRunner.setChartImagePath(path);
    	return this;
    }

    /** Set the output format
     *
     * @param format output format : ReportRunner.PDF_FORMAT, ReportRunner.EXCEL_FROMAT
     * ReportRunner.HTML_FORMAT, ReportRunner.RTF_FORMAT, ReportRunner.CSV_FORMAT, ReportRunner.TSV_FROMAT
     * @return FluentReportRunner object with output format set
     */
    public FluentReportRunner formatAs(String format) {
		reportRunner.setFormat(format);
		return this;
	}   
    
    /** Set an alert
     * 
     * @param alerts alerts list
     * @return FluentReportRunner object with alert set
     */
    public FluentReportRunner withAlerts(List<Alert> alerts) {
    	reportRunner.setAlerts(alerts);
    	return this;
    }

    /** Set compute count property : default this property is false because this computation
     * takes time
     *
     * @param count true means the count will be computed
     * @return FluentReportRunner object with count property set
     */
    public FluentReportRunner computeCount(boolean count) {
        reportRunner.setCount(count);
        return this;
    }

    /** Register a listener
     *
     * @param listener exporter event listener
     * @return FluentReportRunner object with listener registered
     */
    public FluentReportRunner registerListener(ExporterEventListener listener) {
        reportRunner.addExporterEventListener(listener);
        return this;
    }

    /** Unregister a listener
     *
     * @param listener exporter event listener
     * @return FluentReportRunner object with listener unregistered
     */
    public FluentReportRunner unregisterListener(ExporterEventListener listener) {
        reportRunner.removeExporterEventListener(listener);
        return this;
    }

    /** Export the current report to the specified output format
     *
     * @param stream output stream to write the exported report
     * @throws ReportRunnerException if FluentReportRunner object is not correctly configured
     * @throws NoDataFoundException if no data is found
     */
    public void run(OutputStream stream) throws ReportRunnerException, NoDataFoundException {
		reportRunner.run(stream);
	}

    /** Stop the export process
     */
    public void stop() {
        if (reportRunner != null) {
            reportRunner.stop();
        }
    }

    /** See if runner is cancelled
     *
     * @return true if runner is cancelled
     */
    public boolean isCancelled() {
        return reportRunner.isCancelled();
    }
    
    /** Get parameters values
     * Values for dynamic parameters are set at runtime
     * 
     * @return parameters values
     */
    public Map<String, Object> getParameterValues() {
        return reportRunner.getParameterValues();
    }
	
}
