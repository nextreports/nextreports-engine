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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import ro.nextreports.engine.FluentReportRunner;
import ro.nextreports.engine.Report;


/**
 * @author Decebal Suiu
 * For images to be loaded inside PDF, RTF, EXCEL you have to add 
 * the project folder (next-reports-integration) to classpath
 */
public class SwingDemo {

	private JFrame frame;
	
	public SwingDemo() {
		frame = new JFrame("SwingDemo");		
		frame.setLayout(new BorderLayout());
		
		JToolBar toolBar = new JToolBar(); 
		toolBar.add(new RunReportAction());
		frame.add(toolBar, BorderLayout.NORTH);
		
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(400, 300);
		frame.setLocationRelativeTo(null);		
	}
	
	public void showDemo() {
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		SwingDemo swingDemo = new SwingDemo();
		swingDemo.showDemo();
	}
	
	class RunReportAction extends AbstractAction {
		
		public RunReportAction() {
			super("Run report");
		}
		
		public void actionPerformed(ActionEvent event) {
			final Connection connection;
			final Report report;
			try {
				connection = DemoUtil.createDemoConnection();
				report = DemoUtil.loadDemoReport();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}


            JDialog dialog = new RuntimeParametersDialog(frame, connection, report) {

				@Override
				protected void onRun() {
					OutputStream output = null;
					try {
						Map<String, Object> parameterValues = getParametersPanel().getParametersValues();
						String format = getParametersPanel().getFormat();
						
						String file = report.getName().substring(0, report.getName().lastIndexOf('.'));
						file += "." + format.toLowerCase();
						output = new FileOutputStream(file);
						FluentReportRunner.report(report)
							.connectTo(connection)
							.withQueryTimeout(60) // optional
							.withParameterValues(parameterValues) // optional (for no parameters)
							.formatAs(format)
							.run(output);
						JOptionPane.showMessageDialog(frame, "Created file '" + file + "'.");
					} catch (Exception e) {
						JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					} finally {
						DemoUtil.closeStream(output);
					}
				}								
				
			};
			dialog.pack();
			dialog.setLocationRelativeTo(frame);
			dialog.setVisible(true);
		}

	}

}
