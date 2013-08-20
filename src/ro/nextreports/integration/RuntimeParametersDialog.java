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
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.sql.Connection;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import ro.nextreports.engine.Report;


/**
 * @author Decebal Suiu
 */
public class RuntimeParametersDialog extends JDialog {
 
	private Connection connection;
    private Report report;
    private RuntimeParametersPanel parametersPanel;
	
	public RuntimeParametersDialog(Frame owner, Connection connection, Report report) {
		super(owner, "Run report", true);
		
		this.connection = connection;
        this.report = report;
		
		initComponents();
	}

	public RuntimeParametersPanel getParametersPanel() {
		return parametersPanel;
	}

	protected void onRun() {
		// override this method
	}

	@SuppressWarnings("serial")
	private void initComponents() {
		setLayout(new BorderLayout());

		parametersPanel = new RuntimeParametersPanel(connection, report);
		add(parametersPanel, BorderLayout.CENTER);
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(new JButton(new AbstractAction("Run") {

			public void actionPerformed(ActionEvent event) {
				onRun();
				dispose();
			}
			
		}));
		buttonsPanel.add(new JButton(new AbstractAction("Close") {

			public void actionPerformed(ActionEvent event) {
				dispose();
			}
			
		}));
		add(buttonsPanel, BorderLayout.SOUTH);
	}

}
