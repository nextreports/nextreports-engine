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

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.exporter.util.AlarmData;
import ro.nextreports.engine.exporter.util.StyleFormatConstants;
import ro.nextreports.engine.queryexec.QueryException;
import ro.nextreports.engine.util.ColorUtil;

public class AlarmExporter extends ResultExporter {
	        
    private AlarmData data;
    private List<Object> alertValues = new ArrayList<Object>();   

    public AlarmExporter(ExporterBean bean) {
    	super(bean);    		
        data = new AlarmData();
    }
    
    protected void exportCell(String bandName, BandElement bandElement, Object value, int gridRow,
                              int row, int column, int cols, int rowSpan, int colSpan, boolean isImage) {

        int rows = getDetailRows();  
        Map<String, Object> style = buildCellStyleMap(bandElement, value, gridRow, column, colSpan); 

        // only first row from detail is important
        if (ReportLayout.DETAIL_BAND_NAME.equals(bandName)) {
            if (rows == 1) {            	
                if (column == 0) {
                	data.setColor(ColorUtil.getHexColor(bandElement.getBackground()));
                	if (style.containsKey(StyleFormatConstants.BACKGROUND_COLOR)) {
                        data.setColor(ColorUtil.getHexColor((Color) style.get(StyleFormatConstants.BACKGROUND_COLOR)));                        
                    }
					for (Alert alert : alerts) {
						if (isAlert(alert, value)) {
							alertValues.add(value);
						} else {
							alertValues.add(null);
						}
					}
                } else if (column == 1) {
                	if (value == null) {
                		data.setText("");
                	} else {
                		data.setText(value.toString());
                	}
                	for (int i=0, size=alertValues.size(); i<size; i++) {
                		Object alertValue = alertValues.get(i);
                		if (alertValue != null) {                		
                			executeAlert(alerts.get(i), alertValue, value.toString());
                		}
                	}
                } else if (column == 2) {
                	data.setShadow(Boolean.parseBoolean(bandElement.getText()));
                }
            }
            return;
        }
               
    }

    protected void flush() {        
    }

    protected void flushNow() {
    }

    protected void initExport() throws QueryException {
    }

    protected void finishExport() {
    }

    protected Set<CellElement> getIgnoredCells(Band band) {
        return new HashSet<CellElement>();
    }

    protected void afterRowExport() {
    }

    protected void close() {
    }

    protected String getNullElement() {
        return "";
    }

    public AlarmData getData() {
		return data;
	}	               
 
}
