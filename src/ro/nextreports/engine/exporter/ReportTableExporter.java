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

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.TableExporter;
import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.Hyperlink;
import ro.nextreports.engine.band.HyperlinkBandElement;
import ro.nextreports.engine.exporter.util.TableData;
import ro.nextreports.engine.i18n.I18nUtil;
import ro.nextreports.engine.queryexec.QueryException;
import ro.nextreports.engine.util.StringUtil;

/**
 * User: mihai.panaitescu
 * Date: 07-Apr-2010
 * Time: 14:42:31
 */
public class ReportTableExporter extends ResultExporter implements TableExporter {

    private TableData data;
    private boolean tableRawData;
    
    public ReportTableExporter(ExporterBean bean) {
    	super(bean);  
    	data = new TableData();
    	data.setLanguage(I18nUtil.getLanguageByName(bean.getReportLayout(), bean.getLanguage()));
    	this.tableRawData = bean.isReportTableExporterRawData();
    }
    
    protected void exportCell(String bandName, BandElement bandElement, Object value, int gridRow,
                              int row, int column, int cols, int rowSpan, int colSpan, boolean isImage) {

        int hRows = getHeaderRows();

        if (bandElement instanceof HyperlinkBandElement) {
            Hyperlink hyperlink = ((HyperlinkBandElement) bandElement).getHyperlink();
            value = hyperlink.getText();
        }
        String s = StringUtil.getValueAsString(value, getPattern(bandElement), I18nUtil.getLanguageByName(bean.getReportLayout(), bean.getLanguage()));        
        
        // only last row from header is put in memory
        if (ReportLayout.HEADER_BAND_NAME.equals(bandName)) {
            if ((hRows == 1) || (hRows == gridRow + 1)) {
                data.getHeader().add(s);
                data.getPattern().add(null);
            }
            return;
        }
        
        data.getPattern().set(column, getPattern(bandElement));
        
        Map<String, Object> style = buildCellStyleMap(bandElement, value, gridRow, column, colSpan);
        
        List<Object> rowData;
        List<Map<String,Object>> st;
        if (data.getData().size()+hRows <= exporterRow) {
            rowData = new ArrayList<Object>();
            data.getData().add(rowData);
            st = new ArrayList<Map<String,Object>>();
            data.getStyle().add(st);
        } else {
            rowData = data.getData().get(exporterRow-hRows);
            st = data.getStyle().get(exporterRow-hRows);
        }      
        if (tableRawData) {
        	rowData.add(value); // formatted data is shown through renderer / dislayModel (see TableRendererPanel)
        } else {
        	rowData.add(s);
        }
        st.add(style);
    }

    protected void flush() {
        if (resultSetRow % FLUSH_ROWS == 0) {
            flushNow();
        }
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

    public TableData getTableData() {
    	return data;
    }
        
}
