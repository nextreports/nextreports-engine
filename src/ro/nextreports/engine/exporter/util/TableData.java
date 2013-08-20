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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

/**
 * User: mihai.panaitescu
 * Date: 08-Apr-2010
 * Time: 15:28:51
 */
public class TableData implements Serializable {

    private List<String> header;
    private List<List<Object>> data;
    private List<List<Map<String, Object>>> style;
    
    public TableData() {
    	 header = new ArrayList<String>();
    	 data = new ArrayList<List<Object>>();
    	 style = new ArrayList<List<Map<String, Object>>>();
    }

    public TableData(List<String> header, List<List<Object>> data, List<List<Map<String, Object>>> style) {
        this.header = header;
        this.data = data;
        this.style = style;        
    }

    public List<String> getHeader() {
        return header;
    }

    public List<List<Object>> getData() {
        return data;
    }
    
    public List<List<Map<String, Object>>> getStyle() {
    	return style;
    }

	public void setHeader(List<String> header) {
		this.header = header;
	}

	public void setData(List<List<Object>> data) {
		this.data = data;
	}

	public void setStyle(List<List<Map<String, Object>>> style) {
		this.style = style;
	}
        
}
