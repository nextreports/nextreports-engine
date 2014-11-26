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

import ro.nextreports.engine.exporter.exception.NoDataFoundException;
import ro.nextreports.engine.exporter.util.AlarmData;
import ro.nextreports.engine.exporter.util.IndicatorData;
import ro.nextreports.engine.exporter.util.TableData;
import ro.nextreports.engine.queryexec.QueryResult;

/**
 * User: mihai.panaitescu
 * Date: 08-Apr-2010
 * Time: 14:05:53
 */
public interface Runner {
	
	/** Execute query
	 *  This method is useful in case you are not interested about report layout, 
	 *  but only query and you want to make your own business.
	 * 
	 * @return QueryResult object
	 * 
	 * @throws ReportRunnerException if Runner object is not correctly configured
     * @throws InterruptedException if process was interrupted
	 */
	public QueryResult executeQuery() throws ReportRunnerException, InterruptedException;
		

     /** Export to TABLE memory format
     *
     * @return true if export process finished, or false if export process was stopped
      *
     * @throws ReportRunnerException if Runner object is not correctly configured
     * @throws NoDataFoundException if no data
     * @throws InterruptedException if process was interrupted
     */
    public boolean run() throws ReportRunnerException, NoDataFoundException, InterruptedException;

    
    /** Export to the specified output format
     *
     * @param stream output stream to write
     *
     * @return true if export process finished, or false if export process was stopped
     *
     * @throws ReportRunnerException if Runner object is not correctly configured
     * @throws NoDataFoundException if no data
     * @throws InterruptedException if process was interrupted
     */
    public boolean run(OutputStream stream) throws ReportRunnerException, NoDataFoundException, InterruptedException;
    
   /** Get table data TABLE exporter
    *
    * @return table data for TABLE exporter
    */
    public TableData getTableData();
    
   /** Get alarm data ALARM exporter
    *
    * @return alarm data for ALARM exporter
    */
    public AlarmData getAlarmData();
    
   /** Get indicator data INDICATOR exporter
    *
    * @return indicator data for INDICATOR exporter
    */
    public IndicatorData getIndicatorData();

}
