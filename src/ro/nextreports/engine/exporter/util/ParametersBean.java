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
package ro.nextreports.engine.exporter.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.nextreports.engine.queryexec.Query;
import ro.nextreports.engine.queryexec.QueryParameter;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Sep 1, 2006
 * Time: 4:47:57 PM
 */
public class ParametersBean {

	private Query query;
	private Map<String, QueryParameter> params;
	private Map<String, Object> paramValues;

	public ParametersBean(Query query, Map<String, QueryParameter> params, Map<String, Object> paramValues) {
		this.query = query;
		this.params = params;
		this.paramValues = paramValues;
	}

	public Query getQuery() {
		return query;
	}

	public Map<String, QueryParameter> getParams() {
		return params;
	}
	
	public Map<String, QueryParameter> getSubreportParams() {
		Map<String, QueryParameter> subreportsParams = new HashMap<String, QueryParameter>();
		for (QueryParameter qp : params.values()) {
			if (qp.isSubreportParameter()) {
				subreportsParams.put(qp.getName(), qp);
			}
		}
		return subreportsParams;
	}

	public Map<String, Object> getParamValues() {
		return paramValues;
	}	
	
	public void addSubreportParameters(List<QueryParameter> parameters) {
		for (QueryParameter qp : parameters) {
			qp.setSubreportParameter(true);
			params.put(qp.getName(), qp);
		}	
	}
	
	public void addNotFoundSubreportParameters(List<QueryParameter> parameters) {
		for (QueryParameter qp : parameters) {
			qp.setSubreportParameter(true);
			if (!params.containsKey(qp.getName())) {
				params.put(qp.getName(), qp);
			}
		}	
	}
	
	public void overwriteSubreportParametersValues(Map<String, Object> values) {
		for (String paramName : values.keySet()) {
			paramValues.put(paramName, values.get(paramName));
		}	
	}
	
	public void setParameterValue(String name, Object value) {
		paramValues.put(name, value);
	}
}
