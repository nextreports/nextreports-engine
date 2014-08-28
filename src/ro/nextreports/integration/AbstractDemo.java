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
package ro.nextreports.integration;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.util.LoadReportException;


/**
 * @author Decebal Suiu
 */
public abstract class AbstractDemo {
	
	protected OutputStream output = null;

	public void runDemo() {
		Connection connection = null;
		
		try {
			// load demo report
			Report report = getReport();
			
			// connect to demo database
			connection = getConnection();
			
			output = getOutputStream();

			// run the report
			runReport(connection, report);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DemoUtil.closeConnection(connection);
			DemoUtil.closeStream(output);
		}
	}
	
	public Report getReport() throws Exception, LoadReportException {
		return DemoUtil.loadDemoReport();
	}
	
	public Connection getConnection() throws ClassNotFoundException, SQLException  {
		return DemoUtil.createDemoConnection();
	}
	
	protected abstract OutputStream getOutputStream() throws IOException;

	protected abstract void runReport(Connection connection, Report report)
		throws Exception;
		
}
