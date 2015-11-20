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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

import ro.nextreports.engine.i18n.I18nLanguage;

/**
 * User: mihai.panaitescu
 * Date: 08-Apr-2010
 * Time: 15:28:51
 */
public class TableData implements Serializable {

    private List<String> header;
    private List<List<Object>> data;
    private List<List<Map<String, Object>>> style;   
    
    private I18nLanguage language;
    private List<String> pattern;
    
    private List<Integer> excludedColumns;
    
    public static int SEARCH_TO_TOP = 1;  // all elements are kept and search elements are moved to top of the list
    public static int SEARCH_ONLY = 2;    // only search elements are kept in the list
    
    public TableData() {
    	 header = new ArrayList<String>();
    	 data = new ArrayList<List<Object>>();
    	 style = new ArrayList<List<Map<String, Object>>>();    
    	 pattern = new ArrayList<String>();
    }

    public TableData(List<String> header, List<List<Object>> data, List<List<Map<String, Object>>> style) {
    	if (header ==  null) {
    		header = new ArrayList<String>();
    	}
        this.header = header;  
        for (int i=0, size=header.size(); i<size; i++) {
        	this.pattern.add(null);
        }
        this.data = data;
        this.style = style;        
    }

    public I18nLanguage getLanguage() {
		return language;
	}

	public void setLanguage(I18nLanguage language) {
		this.language = language;
	}

	public List<String> getPattern() {
		return pattern;
	}

	public void setPattern(List<String> pattern) {
		this.pattern = pattern;
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
		this.pattern.clear();
		for (int i=0, size=header.size(); i<size; i++) {
        	this.pattern.add(null);
        }
	}

	public void setData(List<List<Object>> data) {
		this.data = data;
	}

	public void setStyle(List<List<Map<String, Object>>> style) {
		this.style = style;
	}
		
	public List<Integer> getExcludedColumns() {
		return excludedColumns;
	}

	public void setExcludedColumns(List<Integer> excludedColumns) {
		this.excludedColumns = excludedColumns;
	}

	private void moveRowToPosition(int rowIndex, int position) {
		if (data != null) {
			if (data.size() > rowIndex) {				
				List<Object> rowToMove = data.get(rowIndex);
				data.remove(rowIndex);
				data.add(position, rowToMove);
				
				if (style != null) {
					List<Map<String, Object>> styleToMove = style.get(rowIndex);
					style.remove(rowIndex);
					style.add(position, styleToMove);
				}
			}
		}
	}
	
	public void search(List<Object> tableFilter) {
		search(tableFilter, SEARCH_ONLY);
	}
	
	public void search(List<Object> tableFilter, int algorithm) {
		int position = 0;
		if ((tableFilter != null) && !hasOnlyNullValues(tableFilter)) {
			if (algorithm == SEARCH_TO_TOP) {
				for (int i=0, size=data.size(); i<size; i++) {
					List<Object> row = data.get(i);
					boolean found = true;
					for (int j=0, no = tableFilter.size(); j<no; j++) {
						Object t = tableFilter.get(j);
						if (t != null) {
							Object o = row.get(j);						
							if (t != null) {	
								if ((o == null)|| !o.toString().toLowerCase().contains(t.toString().toLowerCase())) {
									found = false;
									break;
								}								
							}
						}
					}
					if (found) {
						moveRowToPosition(i, position);
						position++;
					}					
				}
			} else {
				for (Iterator<List<Object>> it = data.iterator(); it.hasNext();) {
					List<Object> row = it.next();
					boolean kept = true;
					for (int j=0, no = tableFilter.size(); j<no; j++) {
						Object t = tableFilter.get(j);
						if (t != null) {
							Object o = row.get(j);						
							if ((o == null) || !o.toString().toLowerCase().contains(t.toString().toLowerCase())) {							
								kept = false;
								break;
							}
						}
					}
					if (!kept) {
						it.remove();
					}
				}
			}
		}
	}
	
	private boolean hasOnlyNullValues(List<Object> list) {
		if (list == null) {
			return true;
		}
		boolean result = true;
		for (Object obj : list) {
			if (obj != null) {
				result = false;
				break;
			}
		}
		return result;
	}
        
}
