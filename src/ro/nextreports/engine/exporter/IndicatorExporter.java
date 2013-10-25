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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.exporter.util.IndicatorData;
import ro.nextreports.engine.exporter.util.StyleFormatConstants;
import ro.nextreports.engine.queryexec.QueryException;

public class IndicatorExporter extends ResultExporter {
	       
    private IndicatorData data;    

    public IndicatorExporter(ExporterBean bean) {
    	super(bean);     	  
    	data = new IndicatorData();
    }
    
    protected void exportCell(String bandName, BandElement bandElement, Object value, int gridRow,
                              int row, int column, int cols, int rowSpan, int colSpan, boolean isImage) {

    	int headerRows = getHeaderRows(); 
        int detailRows = getDetailRows();  
        Map<String, Object> style = buildCellStyleMap(bandElement, value, gridRow, column, colSpan); 

        if (ReportLayout.HEADER_BAND_NAME.equals(bandName)) {
        	if (headerRows == 2) {
        		if (row == 0) {
        			switch (column) {
        				case 0: data.setTitle(getBandElementValueAsString(bandElement));
        						break;
        				case 1: data.setDescription(getBandElementValueAsString(bandElement));
        						break;
        				case 2: data.setUnit(bandElement.getText());
        						break;
        			}
        		} else if (row == 1) {
					switch (column) {
						case 0:	data.setMin(Integer.parseInt(bandElement.getText()));
								break;
						case 1: data.setMax(Integer.parseInt(bandElement.getText()));
								break;
						case 2: data.setShowMinMax(Boolean.parseBoolean(bandElement.getText()));						
								break;
					}
        		}
        	}
        } else if (ReportLayout.DETAIL_BAND_NAME.equals(bandName)) {
            if (detailRows == 1) {            	
                if (column == 0) {
                	data.setBackground(bandElement.getBackground());
                	if (style.containsKey(StyleFormatConstants.BACKGROUND_COLOR)) {
                		data.setBackground((Color) style.get(StyleFormatConstants.BACKGROUND_COLOR));                        
                    }  
                	data.setColor(bandElement.getForeground());
                	if (style.containsKey(StyleFormatConstants.FONT_COLOR)) {
                		data.setColor((Color) style.get(StyleFormatConstants.FONT_COLOR));                        
                    }  
                	if (value instanceof Integer) {
                		data.setValue((Integer)value);
                	} else if (value instanceof Long) {
                		data.setValue((Long)value);
                	} else {
                		data.setValue((Double)value);
                	}                	
                	for (Alert alert : alerts) {                	
						if (isAlert(alert, value)) {
							executeAlert(alert, value, "");
						} 
					}
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

	public IndicatorData getData() {
		return data;
	}		
 
}
