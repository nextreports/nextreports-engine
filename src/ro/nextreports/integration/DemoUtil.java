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

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.List;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.chart.Chart;
import ro.nextreports.engine.util.LoadReportException;
import ro.nextreports.engine.util.NextChartUtil;
import ro.nextreports.engine.util.ReportUtil;


/**
 * @author Decebal Suiu
 */
public class DemoUtil {

	// from version 5.3 nextreports data is kept in USER_HOME folder
    public static final String NEXTREPORTS_HOME = "D:\\Programs\\NextReports 4.0";
    
    public static DemoDefinition def = DemoDefinitionFactory.get(DemoDefinitionFactory.DERBY_DB);
   
    public static Report loadDemoReport() throws FileNotFoundException, LoadReportException {
    	String location = DemoUtil.NEXTREPORTS_HOME + File.separator + "output" +
	            File.separator + def.getDatabaseName() + File.separator + "Reports";
		
		String file = location + File.separator + def.getReportName();    	        
        // use "Mike2.report" to test for Arabic characters
        
        Report report = ReportUtil.loadReport(new FileInputStream(file));
        // copy report images if any to directory where exported file is generated : works for HTML export
        // for PDF, RTF & EXCEL export the directory where we copy images must be in the CLASSPATH!
        copyImages(report, location, ".");
        return report;    	
    }

    public static Chart loadDemoChart() throws FileNotFoundException {
    	String location = NEXTREPORTS_HOME + File.separator + "output" +
                File.separator + def.getDatabaseName() + File.separator + "Charts";
    	
        String file = location + File.separator + def.getChartName();
        return NextChartUtil.loadChart(new FileInputStream(file));     
    }

    public static Connection createDemoConnection() throws ClassNotFoundException, SQLException {
    	return def.createDemoConnection();
    }

    public static Map<String, Object> createDemoParameterValues() {
    	return def.createDemoParameterValues();
    }

    public static void closeConnection(Connection connection) {
        if (connection == null) {
            return;
        }

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeStream(Closeable stream) {
        if (stream == null) {
            return;
        }

        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This method copies report images to a directory
    //          this must be the directory where the exported report is generated : HTML
    //          this must any folder taht is in the project classpath : PDF, EXCEL, RTF    
    public static void copyImages(Report report, String from, String to) {
        try {
            List<String> images = ReportUtil.getStaticImages(report);
            File destination = new File(to);
            for (String image : images) {
                File f = new File(from + File.separator + image);
                if (f.exists()) {
                    FileUtil.copyToDir(f, destination);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    

}
