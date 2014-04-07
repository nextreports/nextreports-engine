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
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.Date;

import ro.nextreports.engine.ReleaseInfoAdapter;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.Hyperlink;
import ro.nextreports.engine.band.HyperlinkBandElement;
import ro.nextreports.engine.band.ImageBandElement;
import ro.nextreports.engine.band.ImageColumnBandElement;
import ro.nextreports.engine.band.ReportBandElement;
import ro.nextreports.engine.exporter.util.StyleFormatConstants;
import ro.nextreports.engine.queryexec.QueryException;
import ro.nextreports.engine.util.HtmlUtil;
import ro.nextreports.engine.util.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Nov 10, 2008
 * Time: 4:23:32 PM
 */
public class HtmlExporter extends ResultExporter {

    private PrintStream stream;       

    public HtmlExporter(ExporterBean bean) {
        super(bean);
    }

    protected void initExport() throws QueryException {
        stream = createPrintStream();
		if (!bean.isSubreport()) {
			String style = buildHtmlStyle(getReportLayout());
			stream.print("<html><head>\n");
			stream.print("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
			stream.print(getMetaData());
			stream.print("<title>");
			stream.print(getDocumentTitle());
			stream.print("</title>\n" + style + " \n</head> ");
			if (bean.getReportLayout().getBackgroundImage() != null) {
				stream.print("<body style=\"background-image:url(");
				stream.print(bean.getReportLayout().getBackgroundImage());
				stream.print(")\">\n");
			} else {
				stream.print("<body>\n");
			}
		}
        if (bean.getReportLayout().isUseSize()) {
            stream.print("<table>");
        } else {
            stream.print("<table style='width:100%'>");
        }
    }

    private String getMetaData() {
        StringBuilder sb = new StringBuilder();
        sb.append("<meta name=\"author\" content=\"").
                append(ReleaseInfoAdapter.getCompany()).append("\">\n");
        sb.append("<meta name=\"creator\" content=\"").
                append("NextReports ").append(ReleaseInfoAdapter.getVersionNumber()).append("\">\n");
        sb.append("<meta name=\"subject\" content=\"").
                append("Created by NextReports Designer ").append(ReleaseInfoAdapter.getVersionNumber()).append("\">\n");
        sb.append("<meta name=\"date\" content=\"").
                append(new Date()).append("\">\n");
        sb.append("<meta name=\"keywords\" content=\"").
                append(ReleaseInfoAdapter.getHome()).append("\">\n");
        return sb.toString();
    }

    protected void finishExport() {
        stream.print("</table>\n");
        if (!bean.isSubreport()) {
        	stream.print("</body></html>");                	
        }
        stream.flush();
        if (!bean.isSubreport()) {
        	stream.close();
        }
    }
    
    private String getSubreportTable() {
    	try {
			return subreportStream.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} finally {
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
        return getIgnoredCellElements(band);
    }

    protected void exportCell(String bandName, BandElement bandElement, Object value, int gridRow, int row, int column,
                              int cols, int rowSpan, int colSpan, boolean isImage) {
    	    	
		if (newRow) {			
			stream.print("<tr>");			
		}
		stream.print(getTd(bandName, gridRow, row, column, value, rowSpan, colSpan, bandElement, isImage));        
        if (column + colSpan == cols) {        	
            stream.print("</tr>\n");            
        }      
        if (newRow) {			
        	// special case when we have a rowSpan>1 and this cell is also extended on all columns:
        	// because there are no other cells on the span rows (they are null and ingnored)
        	// we must add a set of n-1 <tr></tr>
			if (column + colSpan == cols) {
				for (int i = 0; i < newRowCount - 1; i++) {					
					stream.print("<tr></tr>\n");
				}
			}						
		}
    }

    protected void afterRowExport() {
    }

    protected String getNullElement() {
        return "&nbsp;";
    }
   
    private String getTd(String bandName, int gridRow, int i, int j, Object value, int rowSpan, int colSpan,
                         BandElement bandElement, boolean image) {
        StringBuilder sb = new StringBuilder();
        String val;
        if (image) {
            val = (String) value;
        } else {
            val = getStringValue(value, getPattern(bandElement));
        }
        sb.append("<td ");
        // if render conditions we cannot use the class anymore, but an inline style
        if (hasRenderConditions(bandElement, value) || hasRowRenderConditions(bandElement, gridRow, value)) {
            sb.append("style=\"");
            sb.append(renderCssCode(bandElement, value, gridRow, j, colSpan, true));
            sb.append("\"");
        } else {
            sb.append("class='");
            if (bean.isSubreport()) {
            	sb.append(bean.getFileName()).append("_");
            }
            sb.append(bandName).append(i).append("_").append(j).append("'");
        }

        sb.append(" rowspan=").append(rowSpan).
                append(" colspan=").append(colSpan);

        if (bean.getReportLayout().isUseSize()) {
            sb.append(" width=").append("\"");
            sb.append(getColumnWidth(j, colSpan));
            sb.append("\"");
        }

        if (bandElement != null) {
            String headers = bandElement.getHtmlAccHeaders();
            if (headers != null) {
                sb.append(" headers=\"").append(headers).append("\"");
            }
            String id = bandElement.getHtmlAccId();
            if (id != null) {
                sb.append(" id=\"").append(id).append("\"");
            }
            String scope = bandElement.getHtmlAccScope();
            if (scope != null) {
                sb.append(" scope=\"").append(scope).append("\"");
            }
        }
        sb.append(">");

        if (image) {
            ImageBandElement ibe = (ImageBandElement) bandElement;
            sb.append("<img src=\"").append(ibe.getImage()).append("\"");
            if (ibe.isScaled()) {
                sb.append(" width=\"").append(ibe.getWidth()).append("\"");
                sb.append(" height=\"").append(ibe.getHeight()).append("\"");
            }
            sb.append(" alt=\"").append(IMAGE_NOT_LOADED).append("\"></img>");
        } else if (bandElement instanceof HyperlinkBandElement) {
            Hyperlink hyperlink = ((HyperlinkBandElement) bandElement).getHyperlink();
            sb.append("<a href=\"").append(hyperlink.getUrl()).append("\" target=\"_blank\">").
                    append(hyperlink.getText()).append("</a>");
        } else if (bandElement instanceof ReportBandElement)  {
            Report report = ((ReportBandElement)bandElement).getReport(); 
            ExporterBean eb = null;
            try {            	
            	eb = getSubreportExporterBean(report);
                HtmlExporter subExporter = new HtmlExporter(eb);
                subExporter.export();
                sb.append(subExporter.getSubreportTable());                          
			} catch (Exception e) {				
				e.printStackTrace();
			} finally {
				if ((eb != null) && (eb.getResult() != null)) {
					eb.getResult().close();
				}
			}
        } else if (bandElement instanceof ImageColumnBandElement){
        	   		
    		String v = StringUtil.getValueAsString(value, null);
    		if(StringUtil.BLOB.equals(v)) {
    			sb.append(StringUtil.BLOB);            			
    		} else {
    			ImageColumnBandElement icbe = (ImageColumnBandElement) bandElement;
        		byte[] imageBytes = StringUtil.decodeImage(v); 									
        		sb.append("<img src=\"data:image/jpg;base64,").append(v).append("\"");
                if (icbe.isScaled()) {
                    sb.append(" width=\"").append(icbe.getWidth()).append("\"");
                    sb.append(" height=\"").append(icbe.getHeight()).append("\"");
                }
                sb.append(" alt=\"").append(IMAGE_NOT_LOADED).append("\"></img>");
    		}        		
		
        } else {
            sb.append(val);
        }

        sb.append("</td>");
        return sb.toString();
    }

    private String renderCssCode(BandElement be, int gridRow, int gridColumn, int colSpan) {
    	// this method is run in init code and we cannot overwrite cell render conditions here
    	// the overwrite is done in getTD method
        return renderCssCode(be, null, gridRow, gridColumn, colSpan, false);
    }

    private String renderCssCode(BandElement be, Object value, int gridRow, int gridColumn, int colSpan, boolean overwriteCellRenderCond) {
        Map<String, Object> style = null;
        style = buildCellStyleMap(be, value, gridRow, gridColumn, colSpan, overwriteCellRenderCond);
        
        // to see a background image all cells must not have any background!
        if (bean.getReportLayout().getBackgroundImage() != null) {
        	style.remove(StyleFormatConstants.BACKGROUND_COLOR);
        }	
        
        return HtmlUtil.getCssCode(be, style);
    }


    private String buildHtmlStyle(ReportLayout reportLayout) {
        StringBuilder retval = new StringBuilder();
        retval.append("<style type=\"text/css\" > \n table{  border-collapse:collapse; } \n	 ");
        List<Band> bands = reportLayout.getDocumentBands();
        for (Band band : bands) {
            int rows = band.getRowCount();
            int cols = band.getColumnCount();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    BandElement be = band.getElementAt(i, j);
                    int colSpan = (be == null) ? 1 : be.getColSpan();
                    
                    retval.append(" .").append(band.getName()).append(i).append("_").append(j).append(" \n{").
                           append(renderCssCode(be, i, j, colSpan)).append(" } \n");
                    
                    if (be instanceof ReportBandElement) {
                    	retval.append(buildSubreportHtmlStyle((ReportBandElement)be));
                    }                                                             
                }
            }
        }
        retval.append("</style> \n");
        return retval.toString();
    }
    
    private String buildSubreportHtmlStyle(ReportBandElement rbe) {
    	StringBuilder retval = new StringBuilder();
    	ReportLayout layout = rbe.getReport().getLayout();
    	List<Band> bands = layout.getDocumentBands();
        for (Band band : bands) {
            int rows = band.getRowCount();
            int cols = band.getColumnCount();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    BandElement be = band.getElementAt(i, j);
                    int colSpan = (be == null) ? 1 : be.getColSpan();
                    retval.append(" .").append(rbe.getReport().getBaseName()).append("_");                                        
                    retval.append(band.getName()).append(i).append("_").append(j).append(" \n{").
                            append(renderCssCode(be, i, j, colSpan)).append(" } \n");
                    // add recursive the style for subreports in other subreports
                    if (be instanceof ReportBandElement) {
                    	retval.append(buildSubreportHtmlStyle((ReportBandElement)be));
                    }
                }
            }
        }
    	return retval.toString();    	    	
    }


    private String getStringValue(Object val, String pattern) {
        String v = StringUtil.getValueAsString(val, pattern);
        if (v == null) {
            return getNullElement();
        }
        return v;
    }    
    
//    private String getRotationStyle(short angle) {
//    	StringBuilder sb = new StringBuilder();
//    	if (angle == -90) {
//    		sb.append("-webkit-transform: rotate(90deg);\n"); // safari
//    		sb.append("-moz-transform: rotate(90deg);\n");    // firefox
//    		sb.append("-o-transform: rotate(90deg);\n");      // opera
//    		sb.append("-ms-transform: rotate(90deg);\n");     // IE 9+
//    		sb.append("filter: progid:DXImageTransform.Microsoft.BasicImage(rotation=1);\n");  // Internet Explorer till 9
//    	} else if (angle == 90) {
//    		sb.append("-webkit-transform: rotate(-90deg);\n"); // safari
//    		sb.append("-moz-transform: rotate(-90deg);\n");    // firefox
//    		sb.append("-o-transform: rotate(-90deg);\n");      // opera
//    		sb.append("-ms-transform: rotate(-90deg);\n");     // IE 9+
//    		sb.append("filter: progid:DXImageTransform.Microsoft.BasicImage(rotation=3);\n");  // Internet Explorer till 9                     
//    	}
//    	return sb.toString();
//    }

}
