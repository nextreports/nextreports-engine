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

import ro.nextreports.engine.chart.Chart;
import ro.nextreports.engine.chart.ChartRunner;
import ro.nextreports.engine.chart.FluentChartRunner;


public class FluentChartDemo {
	
	public static void main(String[] args) {
		new FluentChartDemo().run();
	}
	
	public void run() {
		Connection connection = null;
		OutputStream outputStream = null;

		try {
			Chart chart = DemoUtil.loadDemoChart();
			connection = DemoUtil.createDemoConnection();
			
			FluentChartRunner runner = FluentChartRunner.chart(chart);
			
			runner.connectTo(connection).
				withQueryTimeout(60).
				formatAs(ChartRunner.IMAGE_FORMAT).
				withImageWidth(600).
				withImageHeight(400).	
				withImageName("myimage.jpg").
				run();
			 
			System.out.println("Image absolute path= " + runner.getChartImageAbsolutePath());
			
							
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DemoUtil.closeConnection(connection);
			DemoUtil.closeStream(outputStream);			
		}
	}

}
