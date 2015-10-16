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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;

import ro.nextreports.engine.FluentReportRunner;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportRunner;
import ro.nextreports.engine.context.JDBCConnectionContext;
import ro.nextreports.engine.exporter.event.ExporterEvent;
import ro.nextreports.engine.exporter.event.ExporterEventListener;
import ro.nextreports.engine.exporter.event.ExporterObject;


/**
 * @author Decebal Suiu
 */
public class FluentSimpleDemo extends AbstractDemo {

    protected int eventCounter = 0;
    // threshold after which we do something for the notified event
    protected int eventThreshold = 1;

    public static void main(String[] args) {
		new FluentSimpleDemo().runDemo();
	}

	public void runReport(Connection connection, Report report)
			throws Exception {
		System.out.println("Run report...");

        ExporterEventListener listener = new ExporterEventListener() {
            public void notify(final ExporterEvent event) {
                ExporterObject obj = event.getExporterObject();
                eventCounter++;
                if ((eventCounter % eventThreshold == 0) || (eventCounter == obj.getRecordCount())) {
                    // to compute record count you will have to use computeCount(true)
                    // otherwise will return -1
                    System.out.println(obj.getRecord() + " / " + obj.getRecordCount());                                        
                }
            }
        };
        
        JDBCConnectionContext connectionContext = new JDBCConnectionContext();
        connectionContext.setConnection(connection);
        connectionContext.setQueryTimeout(60);
        connectionContext.setCsv(false);

        long time = System.currentTimeMillis();
		FluentReportRunner.report(report)
    		//.connectTo(connection)
    		//.withQueryTimeout(60) // optional
			.connectTo(connectionContext)
    		.withParameterValues(DemoUtil.createDemoParameterValues()) // optional (for no parameters)
    		.formatAs(ReportRunner.HTML_FORMAT)
            .computeCount(true)
            .registerListener(listener)
            .run(output);
		time = System.currentTimeMillis() - time;
		System.out.println("Done in " + time + " ms");
	}

	@Override
	protected OutputStream getOutputStream() throws IOException {		
		return new FileOutputStream("test.html");
	}
	

}
