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
package ro.nextreports.engine.exporter;

import java.io.OutputStream;
import java.sql.Connection;
import java.util.List;

import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.exporter.util.ParametersBean;
import ro.nextreports.engine.queryexec.QueryResult;

public class ExporterBean {	

	private Connection con;
	private int queryTimeout;
	private QueryResult result;
	private OutputStream out; 
	private ReportLayout reportLayout; 
	private ParametersBean pBean;
	private boolean rawPrint;
	private String fileName;
	private boolean subreport;
	private List<Alert> alerts;
	private boolean isProcedure;
	private String language;
	// should we put raw data inside table (and use renderer to show formatted data)  or we should put formatted data
	private boolean reportTableExporterRawData;
	private String imageChartPath;
	
	public ExporterBean(Connection con, int queryTimeout, QueryResult result,
			OutputStream out, ReportLayout reportLayout, ParametersBean pBean,
			String fileName, boolean rawPrint, boolean isProcedure) {
		this(con, queryTimeout, result, out, reportLayout, pBean, fileName, rawPrint, null, isProcedure);
	}
	
	public ExporterBean(Connection con, int queryTimeout, QueryResult result,
			OutputStream out, ReportLayout reportLayout, ParametersBean pBean,
			String fileName, boolean rawPrint, List<Alert> alerts, boolean isProcedure) {
		super();
		this.con = con;
		this.queryTimeout = queryTimeout;
		this.result = result;
		this.out = out;
		this.reportLayout = reportLayout;
		this.pBean = pBean;
		this.fileName = fileName;
		this.rawPrint = rawPrint;
		this.alerts = alerts;
		this.isProcedure = isProcedure;
		this.reportTableExporterRawData = false;
	}

	public Connection getConnection() {
		return con;
	}
	
	public void setConnection(Connection con) {
		this.con = con;
	}

	public int getQueryTimeout() {
		return queryTimeout;
	}
	
	public void setQueryTimeout(int queryTimeout) {
		this.queryTimeout = queryTimeout;
	}	

	public QueryResult getResult() {
		return result;
	}
	
	public void setResult(QueryResult result) {
		this.result = result;
	}

	public OutputStream getOut() {
		return out;
	}
	
	public void setOut(OutputStream out) {
		this.out = out;
	}

	public ReportLayout getReportLayout() {
		return reportLayout;
	}
	
	public void setReportLayout(ReportLayout reportLayout) {
		this.reportLayout = reportLayout;
	}

	public ParametersBean getParametersBean() {
		return pBean;
	}
	
	public void setParametersBean(ParametersBean pBean) {
		this.pBean = pBean;
	}

	public boolean isRawPrint() {
		return rawPrint;
	}
	
	public void setRawPrint(boolean rawPrint) {
		this.rawPrint = rawPrint;
	}
		
	public boolean isProcedure() {
		return isProcedure;
	}

	public void setProcedure(boolean isProcedure) {
		this.isProcedure = isProcedure;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
		
	public boolean isSubreport() {
		return subreport;
	}

	public void setSubreport(boolean subreport) {
		this.subreport = subreport;
	}
		
	public List<Alert> getAlerts() {
		return alerts;
	}		
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
	public boolean isReportTableExporterRawData() {
		return reportTableExporterRawData;
	}

	public void setReportTableExporterRawData(boolean reportTableExporterRawData) {
		this.reportTableExporterRawData = reportTableExporterRawData;
	}	

	public String getImageChartPath() {
		return imageChartPath;
	}

	public void setImageChartPath(String imageChartPath) {
		this.imageChartPath = imageChartPath;
	}

	@Override
	public String toString() {
		return "ExporterBean [con=" + con + ", queryTimeout=" + queryTimeout
				+ ", result=" + result + ", out=" + out + ", reportLayout="
				+ reportLayout + ", pBean=" + pBean + ", rawPrint=" + rawPrint
				+ ", fileName=" + fileName + ", subreport=" + subreport + "]";
	}				
		
}
