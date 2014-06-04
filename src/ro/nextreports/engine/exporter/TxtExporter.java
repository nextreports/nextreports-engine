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

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.Hyperlink;
import ro.nextreports.engine.band.HyperlinkBandElement;
import ro.nextreports.engine.band.ReportBandElement;
import ro.nextreports.engine.queryexec.QueryException;
import ro.nextreports.engine.util.StringUtil;

/**
 * Fixed Size flat file exporter
 * @author mihai.panaitescu
 */
public class TxtExporter extends ResultExporter {

    private PrintStream stream;
    private int columnWidth[];

    public TxtExporter(ExporterBean bean) {
        super(bean);
    }

    protected void initExport() throws QueryException {
        stream = createPrintStream();
        columnWidth = getColumnCharacters();        
    }

    protected void finishExport() {
        stream.flush();
        if (!bean.isSubreport()) {
        	stream.close();
        }
    }

    protected void close() {
    	if (!bean.isSubreport()) {
    		stream.close();
    	}
    }

    protected void flush() {
        if (resultSetRow % FLUSH_ROWS == 0) {
            flushNow();
        }
    }

    protected void flushNow() {
        stream.flush();
    }

    protected Set<CellElement> getIgnoredCells(Band band) {
        return new HashSet<CellElement>();
    }

    protected void exportCell(String bandName, BandElement bandElement, Object value, int gridRow, int row,
                              int column, int cols, int rowSpan, int colSpan, boolean isImage) {
        if (bandElement instanceof HyperlinkBandElement) {
            Hyperlink hyperlink = ((HyperlinkBandElement)bandElement).getHyperlink();
            value = hyperlink.getText();
        } else if (bandElement instanceof ReportBandElement)  {
            Report report = ((ReportBandElement)bandElement).getReport(); 
            ExporterBean eb = null;
            try {            	
            	eb = getSubreportExporterBean(report);
                TxtExporter subExporter = new TxtExporter(eb);
                subExporter.export();
                value = subExporter.getSubreportData();                          
			} catch (Exception e) {				
				e.printStackTrace();
			} finally {
				if ((eb != null) && (eb.getResult() != null)) {
					eb.getResult().close();
				}
			}
        }    
        put(stream, StringUtil.getValueAsString(value, getPattern(bandElement), getReportLanguage()), column, colSpan, bandElement);
        if (column == cols - 1) {
            nl(stream);
        }
    }

    protected void afterRowExport(){
    }

    protected String getNullElement() {
        return null;
    }
    
    public static final float PIXELS_PER_CHAR = 6.55f;
    private int defaultChars = Math.round((A4_PORTRAIT_PIXELS / PIXELS_PER_CHAR));      

    /**
     * line separator to use. We use Windows style for all platforms since csv
     * is a Windows format file.
     */
    private static final String lineSeparator = " \r\n";
   
    
    private int[] getColumnCharacters() {
    	List<Band> bands = getReportLayout().getDocumentBands();        
        int totalColumns = 0;
        for (Band band : bands) {        
            int cols = band.getColumnCount();
            if (cols > totalColumns) {
                totalColumns = cols;
            }
        }
        
        int[] headerwidths = new int[totalColumns];
        if (totalColumns == 0) {
        	return headerwidths;
        }

        int size = defaultChars / totalColumns;
        int totalWidth = 0;
        for (int i = 0; i < totalColumns; i++) {
            if (bean.getReportLayout().isUseSize()) {
                headerwidths[i] = Math.round(bean.getReportLayout().getColumnsWidth().get(i) / PIXELS_PER_CHAR);
            } else {
                headerwidths[i] = size;
            }
            totalWidth += headerwidths[i];
        }
        return headerwidths;
    }

    /**
     * Write one tsv field to the file, followed by a separator unless it is the
     * last field on the line. Lead and trailing blanks will be removed.
     *
     * @param p print stream
     * @param s The string to write. Any additional quotes or embedded quotes
     *          will be provided by put. Null means start a new line.
     */
    private void put(PrintStream p, String s, int column, int colSpan, BandElement bandElement) {
        if (s == null) {
            // nl();
            put(p, "", column, colSpan, bandElement);
            return;
        }
               
        int size = 0;
        if (colSpan > 1) {
        	for (int i=column; i<column+colSpan; i++)  {
        		size += columnWidth[i];
        	}
        } else {
        	size = columnWidth[column];
        }                
        if ((bandElement != null) && bandElement.getHorizontalAlign() == BandElement.RIGHT)  {
        	p.print(String.format("%" + size + "s", s));
        } else { 
        	p.print(String.format("%-" + size + "s", s));
        }
        
    }

    /**
     * Write a new line in the TXT output file to demark the end of record.
     *
     * @param p print stream
     */
    public void nl(PrintStream p) {        
        p.print(lineSeparator);        
    }
    
    private String getSubreportData() {
    	try {
			return subreportStream.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		} finally {
			stream.close();
		}
    }

}
