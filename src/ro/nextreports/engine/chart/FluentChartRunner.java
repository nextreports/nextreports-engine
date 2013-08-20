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
import java.util.Map;

import ro.nextreports.engine.ReportRunnerException;
import ro.nextreports.engine.exporter.exception.NoDataFoundException;
import ro.nextreports.engine.exporter.util.TableData;

public class FluentChartRunner {
	
	private ChartRunner chartRunner;
	
	private FluentChartRunner(Chart chart) {
		chartRunner = new ChartRunner();
		chartRunner.setChart(chart);
	}
	
   /** Create a FluentChartRunner object
    *
    * @param chart next chart object
    * @return the newly created FluentChartRunner object
    */
	public static FluentChartRunner chart(Chart chart) {
		return new FluentChartRunner(chart);
	}
	
	/** Set the connection to database
	 * 
	 * @param connection database connection
	 * @return FluentChartRunner object with database connection set
	 */
	public FluentChartRunner connectTo(Connection connection) {
		chartRunner.setConnection(connection);
		return this;
	}

   /** Set the query timeout
    *
    * @param queryTimeout database execution query timeout in seconds
    * @return FluentChartRunner object with query timeout set
    */
    public FluentChartRunner withQueryTimeout(int queryTimeout) {
	    chartRunner.setQueryTimeout(queryTimeout);
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
    * @return FluentChartRunner object with parameters values set
    */
    public FluentChartRunner withParameterValues(Map<String, Object> parameterValues) {
	    chartRunner.setParameterValues(parameterValues);
		return this;
	}
    
    /** Set the output format
    *
    * @param format output format : ChartRunner.GRAPHIC_FORMAT (flash), ChartRunner.TABLE_FORMAT, ChartRunner.IMAGE_FORMAT
    *
    * @return FluentChartRunner object with output format set
    */
    public FluentChartRunner formatAs(String format) {
	    chartRunner.setFormat(format);
		return this;
	}
    
    /** Set image path : has meaning just for IMAGE_FORMAT type
     * 
     * @param imagePath image path
     * @return FluentChartRunner object with image path set
     */
    public FluentChartRunner withImagePath(String imagePath) {    	
    	chartRunner.setImagePath(imagePath);
    	return this;
	}
    
    /** Set image name : has meaning just for IMAGE_FORMAT type
     * 
     * @param imageName image name
     * @return FluentChartRunner object with image name set
     */
    public FluentChartRunner withImageName(String imageName) {    	
    	chartRunner.setImageName(imageName);
    	return this;
	}
		
    /** Set image width : has meaning just for IMAGE_FORMAT type
     * if not set, default value is 500
     * 
     * @param imageWidth image width
     * @return FluentChartRunner object with image width set
     */
	public FluentChartRunner withImageWidth(int imageWidth) {
		chartRunner.setImageWidth(imageWidth);
		return this;
	}

	/** Set image height : has meaning just for IMAGE_FORMAT type
     * if not set, default value is 300
     * 
     * @param imageHeight image height
     * @return FluentChartRunner object with image height set
     */
	public FluentChartRunner withImageHeight(int imageHeight) {
		chartRunner.setImageHeight(imageHeight);
		return this;		
	}
	    
   /** Export the current chart to the specified output format : this method can be used by GRAPHIC_FORMAT
    * For IMAGE_FORMAT use withImagePath method.
    *
    * @param stream output stream to write the exported chart
    * @throws ReportRunnerException if ChartRunner object is not correctly configured
    * @throws NoDataFoundException if no data is found
    * @throws InterruptedException if process was interrupted
    */
   public void run(OutputStream stream) throws ReportRunnerException, NoDataFoundException, InterruptedException {
	   chartRunner.run(stream);
	}
   
   /** Export the current chart to the specified output format : this method can be used by IMAGE_FORMAT and TABLE_FORMAT
   *   
   * @throws ReportRunnerException if ChartRunner object is not correctly configured
   * @throws NoDataFoundException if no data is found
   * @throws InterruptedException if process was interrupted
   */
   public void run() throws ReportRunnerException, NoDataFoundException, InterruptedException {
	   chartRunner.run();
   }
   
   /** Get chart image name after export with IMAGE_FORMAT
    * 
    * @return chart image name after export
    */
   public String getChartImageName() {
		return chartRunner.getChartImageName();
	}
	
   /** Get chart image asbolute path after export with IMAGE_FORMAT
    * 
    * @return chart image absolute path after export
    */
	public String getChartImageAbsolutePath() {
		return chartRunner.getChartImageAbsolutePath();
	}
	
	/** Get table data after export with TABLE_FORMAT
	 * 
	 * @return table data after export with TABLE_FORMAT
	 */
	public TableData getTableData() {
		return chartRunner.getTableData();
	}		

}
