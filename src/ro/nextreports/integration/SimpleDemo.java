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

import java.io.OutputStream;
import java.sql.Connection;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportRunner;


/**
 * @author Decebal Suiu
 */
public class SimpleDemo extends AbstractDemo {
	
	public static void main(String[] args) {
		new SimpleDemo().runDemo();
	}

	protected void runReport(Connection connection, Report report, OutputStream output)
			throws Exception {
		System.out.println("Run report...");
		long time = System.currentTimeMillis();
		ReportRunner runner = new ReportRunner();
		runner.setConnection(connection);
		runner.setReport(report);
		runner.setQueryTimeout(60); // optional
		runner.setParameterValues(DemoUtil.createDemoParameterValues()); // optional (for no parameters)
		runner.setFormat(ReportRunner.HTML_FORMAT);
		runner.run(output);
		time = System.currentTimeMillis() - time;
		System.out.println("Done in " + time + " ms");
	}
	
}
