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
package ro.nextreports.engine.band;

import ro.nextreports.engine.Report;

/**
 * ForReportBandElement 
 * 
 * ForReportBandElement will not appear inside the layout used by exporters.
 * This band element will be replaced with a list of ReportBandElement. The number of reports is taken from the sql clause
 * which must return a list of values. Report must have a parameter with same name as column from sql. Every value from sql
 * will be set as default value for parameter from Report.
 * 
 * @author Mihai Dinca-Panaitescu
 * @date 19.03.2013
 */
public class ForReportBandElement extends ReportBandElement {
	
	private String sql;

	public ForReportBandElement(Report report) {
		super(report);
		this.text = "$FOR{R{" + report.getBaseName() + "}}";
	}

	public void setReport(Report report) {
        super.setReport(report);
        setText("$FOR{R{" + report.getBaseName() + "}}");
    }

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((sql == null) ? 0 : sql.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		ForReportBandElement other = (ForReportBandElement) obj;
		if (sql == null) {
			if (other.sql != null) {
				return false;
			}	
		} else if (!sql.equals(other.sql)) {
			return false;
		}	
		return true;
	}
}
