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
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Dec 3, 2008
 * Time: 5:17:37 PM
 */
public class TsvExporter extends ResultExporter {

    private PrintStream stream;

    public TsvExporter(ExporterBean bean) {
        super(bean);
    }

    protected void initExport() throws QueryException {
        stream = createPrintStream();
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
                TsvExporter subExporter = new TsvExporter(eb);
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
        put(stream, StringUtil.getValueAsString(value, getPattern(bandElement)));
        if (column == cols - 1) {
            nl(stream);
        }
    }

    protected void afterRowExport(){
    }

    protected String getNullElement() {
        return null;
    }

    private char separator = '\t';

    /**
     * quote character, usually '\"' '\'' for SOL used to enclose fields
     * containing a separator character.
     */
    private char quote = '\'';

    /**
     * line separator to use. We use Windows style for all platforms since csv
     * is a Windows format file.
     */
    private static final String lineSeparator = " \r\n";

    /**
     * true if there has was a field previously written to this line, meaning
     * there is a comma pending to be written.
     */
    private boolean wasPreviousField = false;

    /**
     * how much extra quoting you want
     */
    private int quoteLevel = 1;

    /**
     * true if write should trim lead/trail whitespace from fields before
     * writing them.
     */
    private final boolean trim = true;

    /**
     * Write one tsv field to the file, followed by a separator unless it is the
     * last field on the line. Lead and trailing blanks will be removed.
     *
     * @param p print stream
     * @param s The string to write. Any additional quotes or embedded quotes
     *          will be provided by put. Null means start a new line.
     */
    private void put(PrintStream p, String s) {
        if (s == null) {
            // nl();
            put(p, "" + separator);
            return;
        }

        if (wasPreviousField) {
            p.print(separator);
        }
        if (trim) {
            s = s.trim();
        }
        if (s.indexOf(quote) >= 0) {
            /* worst case, needs surrounding quotes and internal quotes doubled */
            p.print(quote);
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c == quote) {
                    p.print(quote);
                    p.print(quote);
                } else {
                    p.print(c);
                }
            }
            p.print(quote);
        } else if (quoteLevel == 2 || quoteLevel == 1 && s.indexOf(' ') >= 0
                || s.indexOf(separator) >= 0) {
            /* need surrounding quotes */
            p.print(quote);
            p.print(s);
            p.print(quote);
        } else {
            /* ordinary case, no surrounding quotes needed */
            p.print(s);
        }
        /* make a note to print trailing comma later */
        wasPreviousField = true;
    }

    /**
     * Write a new line in the TSV output file to demark the end of record.
     *
     * @param p print stream
     */
    public void nl(PrintStream p) {
        /* don't bother to write last pending comma on the line */
        p.print(lineSeparator);
        wasPreviousField = false;
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
