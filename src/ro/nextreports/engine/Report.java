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
package ro.nextreports.engine;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.nextreports.engine.persistence.TablePersistentObject;
import ro.nextreports.engine.querybuilder.MyRow;
import ro.nextreports.engine.querybuilder.sql.SelectQuery;
import ro.nextreports.engine.queryexec.QueryParameter;
import ro.nextreports.engine.util.EqualsUtil;


/** Report Object that is saved as xml
 * It contains a query (created through designer) or an sql (created through editor)
 * It contains the defined parameters (not all of them have to be used) 
 *
 * @author Decebal Suiu
 */
public class Report implements Serializable {

    private static final long serialVersionUID = -3868225927478009576L;

    private int id;
    private String name;
    private SelectQuery query;
    private List<QueryParameter> parameters;
    private String sql;
    private List<TablePersistentObject> tables;
    private List<MyRow> rows;
    private ReportLayout layout;
    private String version;        
    
    // for ForReportBandElement
 	private transient Map<String, Object> generatedParamValues;

    /** Creates a new next report object
     */
    public Report() {
    }

    /** Get report id
     *
     * @return report id
     */
    public int getId() {
        return id;
    }

    /** Set report id
     *
     * @param id report id
     */
    public void setId(int id) {
        this.id = id;
    }

    /** Get report name
     *
     * @return report name
     */
    public String getName() {
        return name;
    }

    /** Get report name without extension
     *
     * @return report name without extension
     */
    public String getBaseName() {
        if (name.endsWith(".report")) {
            return name.substring(0, name.length()-7);
        } else {
            return name;
        }
    }

    /** Set report name
     *
     * @param name report name
     */
    public void setName(String name) {
        this.name = name;
    }

    /** Get report query (this is created through designer)
     *
     * @return report query
     */
    public SelectQuery getQuery() {
        return query;
    }

    /** Set report query
     *
     * @param query report query
     */
    public void setQuery(SelectQuery query) {
        this.query = query;
    }

    /** Get report sql (this is created through sql editor)
     *
     * @return report sql
     */
    public String getSql() {
        return sql;
    }

    /** Set report sql
     *
     * @param sql report sql
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /** Get report parameters
     *
     * @return report parameters
     */
    public List<QueryParameter> getParameters() {
        return parameters;
    }

    /** Set report parameters
     *
     * @param parameters report paramaters
     */
    public void setParameters(List<QueryParameter> parameters) {
        this.parameters = parameters;
    }

    /** Get report tables (used in designer)
     *
     * @return report tables
     */
    public List<TablePersistentObject> getTables() {
        return tables;
    }

    /** Set report tables
     *
     * @param tables report tables
     */
    public void setTables(List<TablePersistentObject> tables) {
        this.tables = tables;
    }

    /** Get designer rows (obtained by selecting columns in designer tables)
     *
     * @return designer rows
     */
    public List<MyRow> getRows() {
        return rows;
    }

    /** Set designer rows
     *
     * @param rows designer rows
     */
    public void setRows(List<MyRow> rows) {
        this.rows = rows;
    }

    /** Get report layout
     *
     * @return report layout
     */
    public ReportLayout getLayout() {
        return layout;
    }

    /** Set report layout
     *
     * @param layout report  layout
     */
    public void setLayout(ReportLayout layout) {
        this.layout = layout;
    }

    /** Get report version
     *
     * @return report version
     */
    public String getVersion() {
        return version;
    }

    /** Set report version
     *
     * @param version report version
     */
    public void setVersion(String version) {
        this.version = version;
    }
    
    public Map<String, Object> getGeneratedParamValues() {
		if (generatedParamValues == null) {
			generatedParamValues = new HashMap<String, Object>();
		}
		return generatedParamValues;
	}	
        
	/** Tostring method
     *
     * @return report object as a string
     */
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", query=" + query +
                ", parameters=" + parameters +
                ", sql='" + sql + '\'' +
                ", tables=" + tables +            
                '}';
    }

    /** Equals
     *
     * @param o report object
     * @return true if current report object equals parameter report object, false otherwise
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Report report = (Report) o;

        if (layout != null ? !layout.equals(report.layout) : report.layout != null) return false;
        if (parameters != null ? !parameters.equals(report.parameters) : report.parameters != null) return false;
        if (query != null ? !query.equals(report.query) : report.query != null) return false;
        if (rows != null && report.rows != null && (!rows.containsAll(report.rows) ||
                !report.rows.containsAll(rows))) return false;
        if (sql != null ? !sql.equals(report.sql) : report.sql != null) return false;
        if (tables != null && report.tables != null && (!tables.containsAll(report.tables) ||
                !report.tables.containsAll(tables))) return false;                
        return true;
    }

    /** Hash code value for this report object
     *
     * @return a hash code value for this report object
     */
    public int hashCode() {
        int result;        
        result = (query != null ? query.hashCode() : 0);
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        result = 31 * result + (sql != null ? sql.hashCode() : 0);
        result = 31 * result + (tables != null ? tables.hashCode() : 0);
        result = 31 * result + (rows != null ? EqualsUtil.hashCode(rows) : 0);
        result = 31 * result + (layout != null ? layout.hashCode() : 0);       
        return result;
    }
       
}
