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

import com.thoughtworks.xstream.core.util.Base64Encoder;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.HashSet;
import java.util.Date;

import ro.nextreports.engine.ReleaseInfoAdapter;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.ColumnBandElement;
import ro.nextreports.engine.band.ExpressionBandElement;
import ro.nextreports.engine.band.FunctionBandElement;
import ro.nextreports.engine.band.Hyperlink;
import ro.nextreports.engine.band.HyperlinkBandElement;
import ro.nextreports.engine.band.ImageBandElement;
import ro.nextreports.engine.band.ParameterBandElement;
import ro.nextreports.engine.band.ReportBandElement;
import ro.nextreports.engine.band.VariableBandElement;
import ro.nextreports.engine.queryexec.QueryException;
import ro.nextreports.engine.queryexec.QueryResult;
import ro.nextreports.engine.queryexec.XResult;
import ro.nextreports.engine.util.StringUtil;

/**
 * User: mihai.panaitescu
 * Date: 05-Mar-2010
 * Time: 10:40:00
 */
public class XmlExporter extends ResultExporter {

    // useful to open xml file in excel (excel will see the space character)
    private String space = "_x0020_";
    private static final String DOCUMENT_TAG = "document";
    private static final String RECORD_TAG = "record";
    private static final String IMAGE_TAG = "image";
    private static final String TEXT_TAG = "text";

    private static final String lineSeparator = " \r\n";
    private PrintStream stream;
    private int currentRow = resultSetRow;

    public XmlExporter(ExporterBean bean) {
        super(bean);
    }

    protected void initExport() throws QueryException {
        stream = createPrintStream();
		if (!bean.isSubreport()) {
			stream.print("<?xml version=\"1.0\" standalone=\"yes\"?>");
			stream.print(lineSeparator);
			stream.print("<" + DOCUMENT_TAG + ">");
			stream.print(lineSeparator);
			stream.print(getMetaData());
		}
    }

    protected void finishExport() {
    	if (!bean.isSubreport()) {
    		stream.print("</" + DOCUMENT_TAG + ">");
    		stream.print(lineSeparator);
    	}
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

    protected void printRawRecord() throws QueryException {
        XResult qr = getResult();
        int columns = qr.getColumnCount();
        stream.print("\t<" + RECORD_TAG + ">");
        stream.print(lineSeparator);
        for (int i = 0; i < columns; i++) {

            String name = qr.getColumnName(i);
            Object value = qr.nextValue(name);

            // xml tags do not allow space characters
            name = name.replaceAll(" ", space);
            
            stream.print("\t\t<");
            stream.print(name);
            stream.print(">");
            stream.print(value);
            stream.print("</");
            stream.print(name);
            stream.print(">");
            stream.print(lineSeparator);
        }
        stream.print("\t</" + RECORD_TAG + ">");
        stream.print(lineSeparator);
    }

    private String getMetaData() {
        StringBuilder sb = new StringBuilder();
        sb.append("\t<meta name=\"author\" content=\"").
                append(ReleaseInfoAdapter.getCompany()).
                append("\"/>").append(lineSeparator);
        sb.append("\t<meta name=\"creator\" content=\"").
                append("NextReports ").append(ReleaseInfoAdapter.getVersionNumber()).
                append("\"/>").append(lineSeparator);
        sb.append("\t<meta name=\"subject\" content=\"").
                append("Created by NextReports Designer ").append(ReleaseInfoAdapter.getVersionNumber()).
                append("\"/>").append(lineSeparator);
        sb.append("\t<meta name=\"date\" content=\"").
                append(new Date()).
                append("\"/>").append(lineSeparator);
        sb.append("\t<meta name=\"keywords\" content=\"").
                append(ReleaseInfoAdapter.getHome()).
                append("\"/>").append(lineSeparator);
        return sb.toString();
    }

    protected void exportCell(String bandName, BandElement bandElement, Object value, int gridRow, int row,
                              int column, int cols, int rowSpan, int colSpan, boolean isImage) {

        if (bean.isRawPrint()) {
            return;
        }

        if (newRow) {
            stream.print("\t<" + RECORD_TAG + ">");
            stream.print(lineSeparator);
        }
        String val;
        if (isImage) {
            val = (String) value;
        } else {
            val = getStringValue(value, getPattern(bandElement));
            // special xml characters
            val = val.replaceAll("& ", "&amp; ");
            val = val.replaceAll("<", "&lt;");
            val = val.replaceAll(">", "&gt;");
            val = val.replaceAll("\"", "&quot;");
            val = val.replaceAll("'", "&apos;");
        }
        stream.print("\t\t");
        stream.print(getTag(bandName, row, column, val, rowSpan, colSpan, bandElement, isImage));
        stream.print(lineSeparator);
        if (column == cols-1) {
            stream.print("\t</" + RECORD_TAG + ">");
            stream.print(lineSeparator);
            if ((colSpan == cols) && (rowSpan > 1)) {
                for (int i = 0; i < rowSpan - 1; i++) {
                    stream.print(getEmptyRow(colSpan));
                }
            }
        }
    }

    private String getStringValue(Object val, String pattern) {
        String v = StringUtil.getValueAsString(val, pattern, getReportLanguage());
        if (v == null) {
            return getNullElement();
        }
        return v;
    }

    protected void afterRowExport() {
    }

    protected String getNullElement() {
        return "";
    }

     private String getEmptyRow(int colSpan) {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(RECORD_TAG).append(">").
           append(getNullElement()).
           append("</").append(RECORD_TAG).append(">").
           append(lineSeparator);
        return sb.toString();
    }

    private String getTag(String bandName, int i, int j, String value, int rowSpan, int colSpan,
                         BandElement bandElement, boolean image) {

        if (bandElement == null) {
            return getNullElement();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getTag(bandElement, true));

       String result = "";
       if (image) {
            if (value != null) {
                try {
                    byte[] imageBytes = getImage(value);
                    result = getBinaryDataForXml(imageBytes);
                } catch (Exception e) {
                    // nothing to do
                }                
            }
        } else if (bandElement instanceof HyperlinkBandElement)  {
            Hyperlink hyperlink = ((HyperlinkBandElement)bandElement).getHyperlink();
            result = hyperlink.getUrl();
        } else if (bandElement instanceof ReportBandElement)  {
            Report report = ((ReportBandElement)bandElement).getReport(); 
            ExporterBean eb = null;
            try {            	
            	eb = getSubreportExporterBean(report);
                XmlExporter subExporter = new XmlExporter(eb);
                subExporter.export();
                result = subExporter.getSubreportData();                          
			} catch (Exception e) {				
				e.printStackTrace();
			} finally {
				if ((eb != null) && (eb.getResult() != null)) {
					eb.getResult().close();
				}
			}    
        } else {
            result = StringUtil.getValueAsString(value, null, getReportLanguage());
            if (result == null) {
                result = getNullElement();
            }
        }
        sb.append(result);        

        sb.append(getTag(bandElement, false));
        return sb.toString();
    }

    private String getTag(BandElement be, boolean start)  {

        StringBuilder sb = new StringBuilder();
        
        if (start) {
            sb.append("<");
        } else {
            sb.append("</");
        }

        String tagName;
        if (be instanceof ColumnBandElement) {
            tagName = ((ColumnBandElement)be).getColumn();
        } else if (be instanceof FunctionBandElement) {
            tagName = ((FunctionBandElement)be).getFunction();
        } else if (be instanceof ExpressionBandElement) {
            tagName = ((ExpressionBandElement)be).getExpressionName();
        } else if (be instanceof HyperlinkBandElement) {
            tagName = ((HyperlinkBandElement)be).getName();
        } else if (be instanceof ImageBandElement) {
            tagName = IMAGE_TAG;
        } else if (be instanceof ParameterBandElement) {
            tagName = ((ParameterBandElement)be).getParameter();
        } else if (be instanceof VariableBandElement) {
            tagName = ((VariableBandElement)be).getVariable();
        } else {
            // static text
            tagName = TEXT_TAG;
        }        
        // special xml characters
        tagName = tagName.replaceAll("& ", "&amp; ");
        tagName = tagName.replaceAll("<", "&lt;");
        tagName = tagName.replaceAll(">", "&gt;");
        tagName = tagName.replaceAll("\"", "&quot;");
        tagName = tagName.replaceAll("'", "&apos;");
        // space in tag
        tagName = tagName.replaceAll(" ", space);        
        sb.append(tagName);
        sb.append(">");

        return sb.toString();            
    }

    // http://www.javaworld.com/javatips/jw-javatip117.html
    public String getBinaryDataForXml(byte[] buffer) {        
        StringBuffer hexData = new StringBuffer();
        Base64Encoder encoder = new Base64Encoder();
        return encoder.encode(buffer);
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
