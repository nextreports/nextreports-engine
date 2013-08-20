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

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DerbyDemoDefinition implements DemoDefinition {	
	
	@Override
	public String getDatabaseName() {
		return "Demo";
	}
	
	public String getReportName() {
		return "Timesheet.report";
	}
	
	public String getChartName() {
		return "ProjectHours.chart";
	}

	@Override
	public Connection createDemoConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        String url = "jdbc:derby:" + DemoUtil.NEXTREPORTS_HOME + File.separator + "demo" +
                File.separator + "data;create=false";
        System.out.println("Connect to '" + url + "'");
        return DriverManager.getConnection(url);
	}
	
	public Map<String, Object> createDemoParameterValues() {
        Map<String, Object> parameterValues = new HashMap<String, Object>();

        parameterValues.put("Project", new Integer[]{1, 2, 3});
        parameterValues.put("Name", new Integer[]{1, 2, 3, 4});
        Calendar calendar = Calendar.getInstance();
        calendar.set(2012, Calendar.AUGUST, 1);
        parameterValues.put("start_date", calendar.getTime());
        calendar.set(2012, Calendar.SEPTEMBER, 30);
        parameterValues.put("end_date", calendar.getTime());

        return parameterValues;
    }

}
