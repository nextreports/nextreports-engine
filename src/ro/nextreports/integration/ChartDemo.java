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

import winstone.Launcher;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.sql.Connection;
import java.awt.*;
import java.awt.image.BufferedImage;


import javax.imageio.ImageIO;
import javax.swing.*;

import ro.nextreports.engine.chart.Chart;
import ro.nextreports.engine.chart.ChartRunner;

/**
 * User: mihai.panaitescu Date: 17-Feb-2010 Time: 14:58:20
 */
public class ChartDemo {

	private final int serverPort = 9099;
	// place where html, swf and json files are
	private final String WEB_ROOT = "chart-webroot";
	private Launcher server;

	public static void main(String[] args) {
		new ChartDemo().previewChartAsFlash();
		//new ChartDemo().previewChartAsImage();
		//new ChartDemo().previewChartAsData();
	}
	
	private ChartRunner createChartRunner(Chart chart , Connection connection, String format)  {		
		ChartRunner runner = new ChartRunner();
		runner.setChart(chart);
		runner.setFormat(format);
		runner.setConnection(connection);
		runner.setQueryTimeout(60);
		return runner;
	}

	public void previewChartAsFlash() {

		startServer();

		Connection connection = null;
		OutputStream outputStream = null;

		try {
			Chart chart = DemoUtil.loadDemoChart();
			connection = DemoUtil.createDemoConnection();
			
			ChartRunner runner = createChartRunner(chart, connection, ChartRunner.GRAPHIC_FORMAT);

			new File(WEB_ROOT).mkdir(); 
			outputStream = new FileOutputStream(WEB_ROOT + File.separatorChar + "data.json");
			boolean result = runner.run(outputStream);
			outputStream.close();

			if (result) {
				String url = "http://localhost:" + serverPort + "/chart.html?ofc=data.json";
				show("\r\n  To preview the chart open in browser the following url:\r\n\r\n   "	+ url, 400, 200);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DemoUtil.closeConnection(connection);
			DemoUtil.closeStream(outputStream);			
		}
	}

	public void previewChartAsImage() {

		Connection connection = null;		

		try {
			Chart chart = DemoUtil.loadDemoChart();
			connection = DemoUtil.createDemoConnection();

			ChartRunner runner = createChartRunner(chart, connection, ChartRunner.IMAGE_FORMAT);
			runner.setImageWidth(350);
			runner.setImageHeight(200);
			runner.run();

			show(new ShowImagePanel(runner.getChartImageAbsolutePath()), 360,240);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DemoUtil.closeConnection(connection);			
		}

	}
	
	public void previewChartAsData() {

		Connection connection = null;		

		try {
			Chart chart = DemoUtil.loadDemoChart();
			connection = DemoUtil.createDemoConnection();

			ChartRunner runner = createChartRunner(chart, connection, ChartRunner.TABLE_FORMAT);
			runner.run();
						
			List<String> tableHeader = runner.getTableData().getHeader();
			for (String header : tableHeader) {
				System.out.print(String.format("%-" + 30 + "s", header));
			}
			System.out.println();
			System.out.println("----------------------------------------");			
			List<List<Object>> data = runner.getTableData().getData();
			for (List<Object> row : data) {
				for (Object o : row) {
					System.out.print(String.format("%-" + 30 + "s", o));
				}
				System.out.println();
			}						

		} catch (Exception e) {
			e.printStackTrace();			
		} finally {
			DemoUtil.closeConnection(connection);			
		}

	}

	private void startServer() {
		// set the winstone arguments
		Map<String, String> winstoneArguments = new HashMap<String, String>();
		winstoneArguments.put("webroot", WEB_ROOT);
		winstoneArguments.put("httpPort", String.valueOf(serverPort));
		winstoneArguments.put("ajp13Port", "-1");
		winstoneArguments.put("httpListenAddress", "127.0.0.1");

		try {
			server = new Launcher(winstoneArguments);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopServer() {
		server.shutdown();
	}
	
		
	private void show(String text, int width, int height) {
		JTextArea textArea = new JTextArea(text);
		textArea.setEditable(false);
		show(textArea, width, height);
	}

	private void show(JComponent component, int width, int height) {
		JFrame frame = new JFrame("ChartDemo");
		frame.setLayout(new BorderLayout());
		frame.add(component, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(width, height);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private class ShowImagePanel extends JPanel {

		private BufferedImage image;

		public ShowImagePanel(String imagePath) {
			try {
				image = ImageIO.read(new File(imagePath));
			} catch (IOException ie) {
				System.out.println("Error:" + ie.getMessage());
			}
		}

		public void paint(Graphics g) {
			g.drawImage(image, 0, 0, null);
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(image.getWidth(), image.getHeight());
		}

	}

}
