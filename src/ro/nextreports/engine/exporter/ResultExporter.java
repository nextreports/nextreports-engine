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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.SwingConstants;
import javax.swing.event.EventListenerList;
import javax.imageio.ImageIO;

import com.itextpdf.text.pdf.Barcode;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.Barcode39;
import com.itextpdf.text.pdf.BarcodeCodabar;
import com.itextpdf.text.pdf.BarcodeDatamatrix;
import com.itextpdf.text.pdf.BarcodeEAN;
import com.itextpdf.text.pdf.BarcodeInter25;
import com.itextpdf.text.pdf.BarcodePDF417;
import com.itextpdf.text.pdf.BarcodeQRCode;

import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlException;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.engine.EngineProperties;
import ro.nextreports.engine.FunctionCache;
import ro.nextreports.engine.GroupCache;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportGroup;
import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.BarcodeBandElement;
import ro.nextreports.engine.band.Border;
import ro.nextreports.engine.band.ChartBandElement;
import ro.nextreports.engine.band.ColumnBandElement;
import ro.nextreports.engine.band.ExpressionBandElement;
import ro.nextreports.engine.band.ExpressionBean;
import ro.nextreports.engine.band.FieldBandElement;
import ro.nextreports.engine.band.FunctionBandElement;
import ro.nextreports.engine.band.HyperlinkBandElement;
import ro.nextreports.engine.band.ImageBandElement;
import ro.nextreports.engine.band.Padding;
import ro.nextreports.engine.band.ParameterBandElement;
import ro.nextreports.engine.band.RowElement;
import ro.nextreports.engine.band.VariableBandElement;
import ro.nextreports.engine.chart.Chart;
import ro.nextreports.engine.chart.ChartRunner;
import ro.nextreports.engine.condition.BandElementCondition;
import ro.nextreports.engine.condition.BandElementConditionProperty;
import ro.nextreports.engine.condition.FormattingConditions;
import ro.nextreports.engine.condition.RowFormattingConditions;
import ro.nextreports.engine.condition.exception.ConditionalException;
import ro.nextreports.engine.exporter.event.ExporterEvent;
import ro.nextreports.engine.exporter.event.ExporterEventListener;
import ro.nextreports.engine.exporter.event.ExporterObject;
import ro.nextreports.engine.exporter.exception.NoDataFoundException;
import ro.nextreports.engine.exporter.util.StyleFormatConstants;
import ro.nextreports.engine.exporter.util.function.FunctionFactory;
import ro.nextreports.engine.exporter.util.function.FunctionUtil;
import ro.nextreports.engine.exporter.util.function.GFunction;
import ro.nextreports.engine.exporter.util.variable.GroupRowVariable;
import ro.nextreports.engine.exporter.util.variable.PageNoVariable;
import ro.nextreports.engine.exporter.util.variable.RowVariable;
import ro.nextreports.engine.exporter.util.variable.TotalPageNoVariable;
import ro.nextreports.engine.exporter.util.variable.Variable;
import ro.nextreports.engine.exporter.util.variable.VariableFactory;
import ro.nextreports.engine.queryexec.IdName;
import ro.nextreports.engine.queryexec.Query;
import ro.nextreports.engine.queryexec.QueryException;
import ro.nextreports.engine.queryexec.QueryExecutor;
import ro.nextreports.engine.queryexec.QueryParameter;
import ro.nextreports.engine.queryexec.QueryResult;
import ro.nextreports.engine.util.PrefixSuffix;
import ro.nextreports.engine.util.QueryUtil;
import ro.nextreports.engine.util.ReportUtil;
import ro.nextreports.engine.util.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Dec 4, 2008
 * Time: 9:53:57 AM
 */
public abstract class ResultExporter {
	
	protected ExporterBean bean;

    protected boolean showNrCrt = false;

    public static final int PORTRAIT = 0;
    public static final int LANDSCAPE = 1;
    
    // special types of reports
    public static final int DEFAULT_TYPE = 0;
    public static final int ALARM_TYPE = 1;
    public static final int TABLE_TYPE = 2;
    public static final int INDICATOR_TYPE = 3;

    // A4 width in inches
    public static final float A4_WIDTH = 8.27f;
    // A4 height in inches
    public static final float A4_HEIGHT = 11.69f;

    public static final int A4_PORTRAIT_PIXELS = (int) (A4_WIDTH * getDPI());
    public static final int A4_LANDSCAPE_PIXELS = (int) (A4_HEIGHT * getDPI());
    public static final int DEFAULT_PADDING_PIXELS = (int) (0.5f * getDPI());

    public static final int FLUSH_ROWS = 7500;

    private Map<Long, Map<String, Object>> styleMap;   
    private String title = "";

    protected final int BORDER_THIN_VALUE = 1;
    protected final int BORDER_MEDIUM_VALUE = 2;
    protected final int BORDER_THICK_VALUE = 3;

    private boolean stopExport = false;    
    protected boolean isDetail;

    protected List<GroupCache> groupCache = new ArrayList<GroupCache>();
    // functions for entire column (without a group), shown only in footer
    protected List<FunctionCache> footerFunctionCache = new ArrayList<FunctionCache>();
    // informs if a new row started for current cell
    protected boolean newRow = false;
    // counts how many rows has the previous band row (max rowSpan)
    protected int newRowCount = 1;    
    protected int exporterRow = 0;
    protected int pageRow = 0;
    protected int pageNo = 0;
    protected int totalPageNo = 0;    
    protected VariableBandElement totalPageNoVbe;    
    protected int resultSetRow = 0;    
    private boolean start = false;

    // how many times the first group appears
    // the number of rows for a band is kept in the outter group :
    // Example : G1 G2 Detail
    //     number of rows for Detail band is kept in G2 group cache
    //     number of rows for G2 band is kept in G1 group cache
    //     number of rows for G1 band is kept in reportGroupRow
    protected int reportGroupRow = 1;
    
    // we can have more than one barcode inside a report, their images must have unique names for HTML exporter
    private int barcodeIndex = 1;
    
    private int NO_VALUES = 30;

    private Object[] previousRow;
    private List<ExpressionBean> expressions;

    protected EventListenerList listenerList = new EventListenerList();
    private ExporterObject exporterObject = new ExporterObject(0, 0);

    protected static String IMAGE_NOT_FOUND = "<image not found>";
    protected static String IMAGE_NOT_LOADED = "<image not loaded>";
    
    public static String SPACE_REPLACEMENT = "_";
    private String currentBandName;     
    
    private Map<Integer, RowElement> rowMap = null;

    // matrix with true values only if formatting conditions are met for a layout grid cell
    protected boolean[][] modifiedStyle;

    private static Log LOG = LogFactory.getLog(ResultExporter.class);
    
    private JexlEngine jexl = new JexlEngine();    
        
    private String imageChartPath;    
    
    protected ByteArrayOutputStream subreportStream;
    
    protected List<Alert> alerts;
    
    protected Map<String, Object> templatesValues = new LinkedHashMap<String, Object>();
    // for every group keep the current value (this map is used to compute a key for templatesValues)
    private Map<String, String> groupTemplateKeys = new LinkedHashMap<String, String>();
            
    // types of what we are printing
    public static final int PRINT_DOCUMENT = 0;
    public static final int PRINT_PAGE_HEADER = 1;
    public static final int PRINT_PAGE_FOOTER = 2;

    public ResultExporter(ExporterBean bean) {
    	this.bean = bean;       
        this.expressions = ReportUtil.getExpressions(bean.getReportLayout());
        createGroupCache();
        modifiedStyle = new boolean[bean.getReportLayout().getRowCount()][bean.getReportLayout().getColumnCount()];   
        totalPageNoVbe = getTotalPageNoVbe(bean.getReportLayout());    
        alerts = bean.getAlerts();    
        if (alerts == null) {
        	alerts = new ArrayList<Alert>();
        }
    }

    public String getDocumentTitle() {
        return title;
    }

    public void setDocumentTitle(String title) {
        this.title = title;
    }       

    public String getImageChartPath() {
		return imageChartPath;
	}

	public void setImageChartPath(String imageChartPath) {
		this.imageChartPath = imageChartPath;
	}

	// export the document
	// header page band and footer page band are written in PDF and RTF exporters
	public boolean export() throws QueryException, NoDataFoundException {

		start = true;
		
        testForData();        
        
		if (needsFirstCrossing() && !(this instanceof FirstCrossingExporter)) {			
			FirstCrossingExporter fe = new FirstCrossingExporter(bean);
			fe.export();
			// get template values from FirstCrossing
			templatesValues = fe.getTemplatesValues();
			groupTemplateKeys = fe.getGroupTemplateKeys();			
		} 

        initExport();

        printHeaderBand();
        if (!printContentBands()) {
            return false;
        }
        printFooterBand();

        finishExport();
        
        if ((bean.getResult() != null) && (!(this instanceof FirstCrossingExporter)) )  {
        	bean.getResult().close();
        }       
        
        if (this instanceof FirstCrossingExporter) {
        	// after FirstCrossing go to the beginning of the result set
        	try {
				bean.getResult().getResultSet().beforeFirst();
			} catch (SQLException ex) {
				LOG.error(ex.getMessage(), ex);
			}
        }               

        return true;
    }

    public OutputStream getOut() {
        return bean.getOut();
    }

    public void setOut(FileOutputStream out) {
        bean.setOut(out);
    }

    public QueryResult getResult() {
        return bean.getResult();
    }

    public void setResult(QueryResult result) {
        bean.setResult(result);
    }

    public boolean isStopExport() {
        return stopExport;
    }

    public void setStopExport(boolean stopExport) {
        this.stopExport = stopExport;
    }

    public Map<Long, Map<String, Object>> getStyleMap() {
        if (styleMap == null) {
            this.styleMap = new HashMap<Long, Map<String, Object>>();
        }
        return styleMap;
    }

    public void setStyleMap(Map<Long, Map<String, Object>> styleMap) {
        this.styleMap = styleMap;
    }

    public ReportLayout getReportLayout() {
        return bean.getReportLayout();
    }

    public void setReportLayout(ReportLayout reportLayout) {
        bean.setReportLayout(reportLayout);
        totalPageNoVbe = getTotalPageNoVbe(bean.getReportLayout());        
    }
    
    
    protected Map<String, Object> buildCellStyleMap(BandElement bandElement) {
        Map<String, Object> format = new HashMap<String, Object>();                     
                
        if (bandElement == null) {
            return format;
        }

        buildCellFont(format, bandElement.getFont());
        format.put(StyleFormatConstants.FONT_COLOR, bandElement.getForeground());
        format.put(StyleFormatConstants.BACKGROUND_COLOR, bandElement.getBackground());

        buildCellHAllign(format, bandElement.getHorizontalAlign());
        buildCellVAllign(format, bandElement.getVerticalAlign());

        Padding padding = new Padding(0, 0, 0, 0);
        if (bandElement.getPadding() != null) {
            padding = bandElement.getPadding();
        }
        buildCellPadding(format, padding);
        //
        format.put(StyleFormatConstants.BORDER, new Float(1));
        //
        Border border = new Border(0, 0, 0, 0);
        if (bandElement.getBorder() != null) {
            border = bandElement.getBorder();
        }
        buildCellBorder(format, border);

        if (bandElement instanceof FieldBandElement) {
            FieldBandElement fbe = (FieldBandElement) bandElement;
            if (fbe.getPattern() != null) {
                format.put(StyleFormatConstants.PATTERN, fbe.getPattern());
            }
        }
        if (bandElement instanceof HyperlinkBandElement) {
            HyperlinkBandElement hbe = (HyperlinkBandElement) bandElement;
            if (hbe.getUrl() != null) {
                format.put(StyleFormatConstants.URL, hbe.getUrl());
            }
        }
        return format;
    }   
    
    protected Map<String, Object> buildCellStyleMap(BandElement bandElement, Object value, int gridRow, int gridColumn, int colSpan) {
    	return buildCellStyleMap(bandElement, value, gridRow, gridColumn, colSpan, true);
    }

    protected Map<String, Object> buildCellStyleMap(BandElement bandElement, Object value, int gridRow, int gridColumn, int colSpan, boolean overwriteCellRenderCond) {
        Map<String, Object> format = new HashMap<String, Object>();                     
                
        if (bandElement == null) {
            return format;
        }

        format = buildCellStyleMap(bandElement);
                
        // overwrite with row render conditions
        RowElement rowEl = getRowElement(getReportLayout(), gridRow);        
        if (rowEl != null) {        	
			RowFormattingConditions rfc = rowEl.getFormattingConditions();
			if ((rfc != null) && (rfc.getConditions().size() > 0)) {
				try {
					Serializable rowEval = (Serializable) evaluateExpression("", rfc.getExpressionText(), currentBandName, null);										
					RowFormattingConditions renderConditions = rowEl.getFormattingConditions();
					putFormattingConditions(format, renderConditions, gridRow, gridColumn, rowEval, colSpan, true);
				} catch (QueryException e) {
					e.printStackTrace();
					LOG.error(e.getMessage(), e);
				}
			}
        }

        // overwrite with cell render conditions
		if (overwriteCellRenderCond) {
			FormattingConditions renderConditions = bandElement.getFormattingConditions();
			if (renderConditions != null) {
				String cellExpressionText = renderConditions.getCellExpressionText();
				if (cellExpressionText == null) {
					putFormattingConditions(format, renderConditions, gridRow, gridColumn, (Serializable) value, colSpan, false);
				} else {
					try {
						Serializable expEval = (Serializable) evaluateExpression("", cellExpressionText,
								getBand(getReportLayout(), gridRow).getName(), null);
						putFormattingConditions(format, renderConditions, gridRow, gridColumn, expEval, colSpan, true);
					} catch (QueryException e) {
						e.printStackTrace();
						LOG.error(e.getMessage(), e);
					}
				}
			}
		}

        return format;
    }
    
    private void putFormattingConditions(Map<String, Object> format, FormattingConditions renderConditions, int gridRow, int gridColumn, 
    		Serializable leftOperand, int colSpan, boolean rowLevel) {
    	
    	if ((renderConditions != null) && (leftOperand != null)) {
            try {
                for (BandElementCondition bec : renderConditions.getConditions()) {
                    bec.getExpression().setLeftOperand(leftOperand);
                    if (bec.getExpression().evaluate()) {
                        modifiedStyle[gridRow][gridColumn] = true;
                        if (bec.getProperty() == BandElementConditionProperty.BACKGROUND_PROPERTY) {
                            format.put(StyleFormatConstants.BACKGROUND_COLOR, bec.getPropertyValue());                            
                        } else if (bec.getProperty() == BandElementConditionProperty.FOREGROUND_PROPERTY) {
                            format.put(StyleFormatConstants.FONT_COLOR, bec.getPropertyValue());
                        } else if (bec.getProperty() == BandElementConditionProperty.BORDER_PROPERTY) {
                            Border b = (Border) bec.getPropertyValue();
                            if (rowLevel) {
                            	Border rBorder = b.clone();
                            	// left border must be only for first column in row
                            	// right border must be only for last column in row                            	
                            	if (gridColumn == 0) {
                            		rBorder.setRight(0);
                            	} else if (gridColumn+colSpan-1 == bean.getReportLayout().getColumnCount()-1) {
                            		rBorder.setLeft(0);
                            	} else {
                            		rBorder.setLeft(0);
                            		rBorder.setRight(0);
                            	}
                            	buildCellBorder(format, rBorder);
                            } else {	
                            	buildCellBorder(format, b);
                            }	
                        } else if (bec.getProperty() == BandElementConditionProperty.FONT_PROPERTY) {
                            Font f = (Font) bec.getPropertyValue();
                            buildCellFont(format, f);
                        }
                    }
                }
            } catch (ConditionalException ex) {
                ex.printStackTrace();
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    private void buildCellBorder(Map<String, Object> format, Border border) {
        if (border.getLeft() > 0) {
            format.put(StyleFormatConstants.BORDER_LEFT, new Float(border.getLeft()));            
        }
        if (border.getRight() > 0) {
            format.put(StyleFormatConstants.BORDER_RIGHT, new Float(border.getRight()));
        }
        if (border.getTop() > 0) {
            format.put(StyleFormatConstants.BORDER_TOP, new Float(border.getTop()));
        }
        if (border.getBottom() > 0) {
            format.put(StyleFormatConstants.BORDER_BOTTOM, new Float(border.getBottom()));
        }        
        format.put(StyleFormatConstants.BORDER_LEFT_COLOR, border.getLeftColor());
        format.put(StyleFormatConstants.BORDER_RIGHT_COLOR, border.getRightColor());
        format.put(StyleFormatConstants.BORDER_TOP_COLOR, border.getTopColor());
        format.put(StyleFormatConstants.BORDER_BOTTOM_COLOR, border.getBottomColor());
    }

    private void buildCellFont(Map<String, Object> format, Font font) {
        format.put(StyleFormatConstants.FONT_FAMILY_KEY, font.getFamily());
        format.put(StyleFormatConstants.FONT_NAME_KEY, font.getName());
        format.put(StyleFormatConstants.FONT_SIZE, new Float(font.getSize()));
        if (Font.PLAIN == font.getStyle()) {
            format.put(StyleFormatConstants.FONT_STYLE_KEY, StyleFormatConstants.FONT_STYLE_NORMAL);
        }
        if (Font.BOLD == font.getStyle()) {
            format.put(StyleFormatConstants.FONT_STYLE_KEY, StyleFormatConstants.FONT_STYLE_BOLD);
        }
        if (Font.ITALIC == font.getStyle()) {
            format.put(StyleFormatConstants.FONT_STYLE_KEY, StyleFormatConstants.FONT_STYLE_ITALIC);
        }
        if ((Font.BOLD | Font.ITALIC) == font.getStyle()) {
            format.put(StyleFormatConstants.FONT_STYLE_KEY, StyleFormatConstants.FONT_STYLE_BOLDITALIC);
        }
    }

    private void buildCellPadding(Map<String, Object> format, Padding padding) {
        format.put(StyleFormatConstants.PADDING_LEFT, new Float(padding.getLeft()));
        format.put(StyleFormatConstants.PADDING_RIGHT, new Float(padding.getRight()));
        format.put(StyleFormatConstants.PADDING_TOP, new Float(padding.getTop()));
        format.put(StyleFormatConstants.PADDING_BOTTOM, new Float(padding.getBottom()));
    }

    private void buildCellHAllign(Map<String, Object> format, int hAllign) {
        if (SwingConstants.CENTER == hAllign) {
            format.put(StyleFormatConstants.HORIZONTAL_ALIGN_KEY, StyleFormatConstants.HORIZONTAL_ALIGN_CENTER);
        }
        if (SwingConstants.LEFT == hAllign) {
            format.put(StyleFormatConstants.HORIZONTAL_ALIGN_KEY, StyleFormatConstants.HORIZONTAL_ALIGN_LEFT);
        }
        if (SwingConstants.RIGHT == hAllign) {
            format.put(StyleFormatConstants.HORIZONTAL_ALIGN_KEY, StyleFormatConstants.HORIZONTAL_ALIGN_RIGHT);
        }
    }

    private void buildCellVAllign(Map<String, Object> format, int vAllign) {
        if (SwingConstants.CENTER == vAllign) {
            format.put(StyleFormatConstants.VERTICAL_ALIGN_KEY, StyleFormatConstants.VERTICAL_ALIGN_MIDDLE);
        }
        if (SwingConstants.TOP == vAllign) {
            format.put(StyleFormatConstants.VERTICAL_ALIGN_KEY, StyleFormatConstants.VERTICAL_ALIGN_TOP);
        }
        if (SwingConstants.BOTTOM == vAllign) {
            format.put(StyleFormatConstants.VERTICAL_ALIGN_KEY, StyleFormatConstants.VERTICAL_ALIGN_BOTTOM);
        }

    }

    protected boolean hasRenderConditions(BandElement be, Object value) {
        if (be == null) {
            return false;
        }
        FormattingConditions rc = be.getFormattingConditions();
        return ((value != null) && (rc != null) && (rc.getConditions().size() > 0));

    }
    
    protected boolean hasRowRenderConditions(BandElement be, int gridRow, Object value) {
        if (be == null) {
            return false;
        }
        RowElement row = getRowElement(getReportLayout(), gridRow);        
        if (row != null) {        	
			RowFormattingConditions rfc = row.getFormattingConditions();
			return ((value != null) && (rfc != null) && (rfc.getConditions().size() > 0));
        } else {
        	return false;
        }        
    }

    // Report Layout contains all cells
    // If two or more cells are merged, the information is kept inside one cell
    // and the other merged cells are null band elements.
    protected Set<CellElement> getIgnoredCellElements(Band band) {
        Set<CellElement> result = new HashSet<CellElement>();
        int rows = band.getRowCount();
        int cols = band.getColumnCount();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                BandElement element = band.getElementAt(i, j);
                if (element == null) {
                    continue;
                }
                int rowSpan = element.getRowSpan();
                int colSpan = element.getColSpan();

                if ((rowSpan > 1) && (colSpan > 1)) {
                    for (int k = 0; k < rowSpan; k++) {
                        for (int m = 0; m < colSpan; m++) {
                            if ((k != 0) || (m != 0)) {
                                result.add(new CellElement(i + k, j + m));
                            }
                        }
                    }
                } else if (rowSpan > 1) {
                    for (int k = 1; k < rowSpan; k++) {
                        result.add(new CellElement(i + k, j));
                    }
                } else if (colSpan > 1) {
                    for (int k = 1; k < colSpan; k++) {
                        result.add(new CellElement(i, j + k));
                    }
                }
            }
        }
        return result;
    }

    // @todo was used for pdf and rtf export (previous 2.1.7 iText version)
    // because there was no support for row span
    protected Set<CellElement> getIgnoredCellElementsForColSpan(Band band) {
        Set<CellElement> result = new HashSet<CellElement>();
        int rows = band.getRowCount();
        int cols = band.getColumnCount();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                BandElement element = band.getElementAt(i, j);
                if (element == null) {
                    continue;
                }
                //int rowSpan = element.getRowSpan();
                int colSpan = element.getColSpan();
                if (colSpan > 1) {
                    for (int k = 1; k < colSpan; k++) {
                        result.add(new CellElement(i, j + k));
                    }
                }
            }
        }
        return result;
    }

    protected int getColumnWidth(int column, int colSpan) {
        int width = bean.getReportLayout().getColumnsWidth().get(column);
        for (int i = column + 1; i < column + colSpan; i++) {
            width += bean.getReportLayout().getColumnsWidth().get(i);
        }
        return width;
    }

    private boolean findIgnoredCellElement(Set<CellElement> ignored, int i, int j) {
        return ignored.contains(new CellElement(i, j));
    }

    private void createGroupCache() {
        List<ReportGroup> groups = bean.getReportLayout().getGroups();
        if (groups == null) {
            groups = new ArrayList<ReportGroup>();
        }
        Collections.sort(groups, new Comparator<ReportGroup>() {
            public int compare(ReportGroup o1, ReportGroup o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (ReportGroup reportGroup : groups) {
            GroupCache gc = new GroupCache();
            gc.setGroup(reportGroup);
            gc.setStart(true);
            gc.setHgBand(bean.getReportLayout().getBand(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX + reportGroup.getName()));
            Band fgBand = bean.getReportLayout().getBand(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX + reportGroup.getName());
            gc.setFgBand(fgBand);
            List<FunctionCache> functionCache = new ArrayList<FunctionCache>();
            for (int i = 0; i < fgBand.getRowCount(); i++) {
                for (int j = 0; j < fgBand.getColumnCount(); j++) {
                    BandElement be = fgBand.getElementAt(i, j);
                    if (be instanceof FunctionBandElement) {
                        FunctionBandElement fbe = (FunctionBandElement) be;
                        FunctionCache fc = new FunctionCache();
                        GFunction gFunction = FunctionFactory.getFunction(fbe.getFunction());
                        fc.setFunction(gFunction);
                        fc.setFunctionColumn(fbe.getColumn());
                        fc.setExpression(fbe.isExpression());
                        functionCache.add(fc);
                    }
                }
            }
            gc.setFuncCache(functionCache);
            groupCache.add(gc);
        }

        // footer function cache
        Band fBand = bean.getReportLayout().getBand(ReportLayout.FOOTER_BAND_NAME);
        for (int i = 0; i < fBand.getRowCount(); i++) {
            for (int j = 0; j < fBand.getColumnCount(); j++) {
                BandElement be = fBand.getElementAt(i, j);
                if (be instanceof FunctionBandElement) {
                    FunctionBandElement fbe = (FunctionBandElement) be;
                    FunctionCache fc = new FunctionCache();
                    GFunction gFunction = FunctionFactory.getFunction(fbe.getFunction());
                    fc.setFunction(gFunction);
                    fc.setFunctionColumn(fbe.getColumn());
                    fc.setExpression(fbe.isExpression());
                    footerFunctionCache.add(fc);
                }
            }
        }
    }

    private void resetFunctions(GroupCache gc) {
        for (FunctionCache fc : gc.getFuncCache()) {
            GFunction gFunction = fc.getFunction();
            gFunction.reset();
        }
    }

    protected String getPattern(BandElement be) {
        if (be instanceof FieldBandElement) {
            FieldBandElement fbe = (FieldBandElement) be;
            if (fbe.getPattern() != null) {
                return fbe.getPattern();
            }
        }
        return null;
    }

    protected String getUrl(BandElement be) {
        if (be instanceof HyperlinkBandElement) {
            HyperlinkBandElement hbe = (HyperlinkBandElement) be;
            if (hbe.getUrl() != null) {
                return hbe.getUrl();
            }
        }
        return null;
    }

    // for ResultSet TYPE-FORWRD_ONLY (like Csv, SQLite) getResult().isEmpty does not work, so
    // we also test in printContentBands to throw NoDataFoundException
    private void testForData() throws QueryException, NoDataFoundException {
        // for procedure call we do not know the row count (is -1)    	    	
        if (this.getOut() == null || this.getResult() == null
                || getResult().getColumnCount() <= 0
                || getResult().getRowCount() == 0 
                || getResult().isEmpty() ) {                        	
            throw new NoDataFoundException();
        }
    }

    protected PrintStream createPrintStream() throws QueryException {
        PrintStream p;
        try {
        	if (bean.isSubreport()) {
        		subreportStream = new ByteArrayOutputStream();
        		p = new PrintStream(subreportStream, false, "UTF-8");
        	} else {
        		p = new PrintStream(getOut(), false, "UTF-8");
        	}
            return p;
        } catch (UnsupportedEncodingException e) {
            throw new QueryException(e);
        }
    }

    private String getStringValue(VariableBandElement bandElement, String bandName) {
        String pattern = getPattern(bandElement);
        Variable var = VariableFactory.getVariable(bandElement.getVariable());       
        Object value = getValue(var, bandName);
        return StringUtil.getValueAsString(value, pattern);
    }

    private Object getValue(Variable var, String bandName) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (Variable.ROW_VARIABLE.equals(var.getName())) {
            parameters.put(RowVariable.ROW_PARAM, resultSetRow + 1);
        } else if (Variable.GROUP_ROW_VARIABLE.equals(var.getName())) {
            // if no groups : GROUP_ROW_VARIABLE is the same with ROW_VARIABLE (groupCache.size() == 0)
            // if groups : we get the outter group for the band
            // if variable is in group footer , we must decrement groupRow by one
            // if variable is in footer band , we put 1
            int groupRow = reportGroupRow;            
            if (groupCache.size() == 0) {                	
                groupRow = resultSetRow + 1;                
            } else {
                GroupCache gc = getOutterGroupCache(bandName);
                if (gc != null) {
                    groupRow = gc.getGroupRow();                    
                }
                if (bandName.startsWith(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX)) {
                    groupRow--;
                } else if (bandName.equals(ReportLayout.FOOTER_BAND_NAME)) {
                    groupRow = 1;
                }
            }            
            parameters.put(GroupRowVariable.GROUP_ROW_PARAM, groupRow);
        }  else if (Variable.PAGE_NO_VARIABLE.equals(var.getName())) {
        	parameters.put(PageNoVariable.PAGE_NO_PARAM, getPageNo());
        }  else if (Variable.TOTAL_PAGE_NO_VARIABLE.equals(var.getName())) {
        	// must be interpreted inside PdfExporter!
        	parameters.put(TotalPageNoVariable.TOTAL_PAGE_NO_PARAM, getTotalPageNo());
        } else if (Variable.REPORT_NAME_VARIABLE.equals(var.getName())) {
        	parameters.put(Variable.REPORT_NAME_VARIABLE, bean.getFileName());
        }
        return var.getCurrentValue(parameters);
    }

    private String getStringValue(ParameterBandElement bandElement) {
        String name = bandElement.getParameter();
        QueryParameter qp = bean.getParametersBean().getParams().get(name);
        Object object = bean.getParametersBean().getParamValues().get(name);
        // a hidden parameter not used in query can be insert to layout, and after that it is modified in not-hidden
        // but it remains in the layout
        if (qp == null) {
            return "";
        }
        if (QueryParameter.MULTIPLE_SELECTION.equals(qp.getSelection())) {
            Object[] values = (Object[]) object;
            if ((values == null) || (values.length == 0)) {
                return getNullElement();
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0, size = values.length; i < size; i++) {
                    sb.append(StringUtil.getValueAsString(values[i], bandElement.getPattern()));
                    if (i < size - 1) {
                        sb.append(" ; ");
                    }
                    if (i == NO_VALUES - 1) {
                        if (i < size - 1) {
                            sb.append(" ...");
                        }
                        break;
                    }
                }
                return sb.toString();
            }
        } else {
            return StringUtil.getValueAsString(object, bandElement.getPattern());
        }
    }

    private boolean printContentBands() throws QueryException, NoDataFoundException {    	
        int cols = getResult().getColumnCount();
        List<String> expNames = ReportUtil.getExpressionsNames(bean.getReportLayout());
        int expNo = expNames.size();
        previousRow = new Object[cols + expNo];
        int k = 0;
        boolean isEmpty = true;
        while (getResult().hasNext()) {        	        	
            
        	isEmpty = false;
        	
        	if (Thread.currentThread().isInterrupted() || isStopExport()) {
                close();
                setStopExport(false);
                return false;
            }

            k++;
            if (k == EngineProperties.getRecordsYield()) {
                k = 0;
                try {
                    Thread.sleep(EngineProperties.getMillisYield());
                } catch (InterruptedException e) {
                    close();
                    setStopExport(false);
                    return false;
                }
            }

            flush();

            if (bean.isRawPrint()) {
                printRawRecord();
            } else {
                if (resultSetRow > 0) {
                    printFooterGroupBands();                    
                }
                printHeaderGroupBands();                
                printDetailBand();                
            }
            resultSetRow++;

            exporterObject.setRecord(resultSetRow);
            exporterObject.setRecordCount(bean.getResult().getRowCount());
            fireExporterEvent(new ExporterEvent(exporterObject));

            afterRowExport();            

            for (int i = 0; i < cols; i++) {
                previousRow[i] = getResult().nextValue(i);
            }
            for (int i = cols; i < cols+expNo; i++) {
                previousRow[i] = evaluateExpression(expressions.get(i-cols));
            }                               
        }
        
        if (isEmpty) {
        	throw new NoDataFoundException();
        }

        // footer for last groups
        for (int i = groupCache.size() - 1; i >= 0; i--) {
            GroupCache gc = groupCache.get(i);
            if (gc.footerHasRows()) {
                printFooterGroupBand(gc, true);
            }
        }
        return true;
    }

    private void printHeaderGroupBands() throws QueryException {
        for (GroupCache gc : groupCache) {
            if (gc.isStart()) {
                groupStarted(gc);
            } 
        }
    }

    private void groupStarted(GroupCache gc) throws QueryException {
        resetFunctions(gc);
        // init functions with first values
        for (FunctionCache fc : gc.getFuncCache()) {
            String column = fc.getFunctionColumn();
            Object value;
            if (fc.isExpression()) {
                value = evaluateExpression(getExpressionBandElement(fc.getFunctionColumn()), currentBandName);
            } else {
                value = getResult().nextValue(column);
            }            
            fc.getFunction().compute(value);
        }
        if (gc.headerHasRows()) {
            printHeaderGroupBand(gc);
        }
        gc.setStart(false);

        // if we start a group we increment group row count and also outter group row count
        GroupCache outter = getOutterGroupCache(gc);
        if (outter == null) {
            reportGroupRow++;
        } else {
            outter.incrementGroupRow();
        }
        gc.incrementGroupRow();
    }

    private boolean isGroupFinished(int groupIndex) throws QueryException {    	
        GroupCache gc = groupCache.get(groupIndex);
        String column = gc.getGroup().getColumn();
        Object value;
        if (resultSetRow == 0) {
            value = getResult().nextValue(column);
        } else {
            //value = getResult().getValueAt(currentRow - 1, column);
            value = previousRow[getResult().getColumnIndex(column)];
        }

        Object newValue = getResult().nextValue(column);
        boolean equals = FunctionUtil.parameterEquals(value, newValue);
        return !equals;
    }

    private boolean isPreviousGroupFinished(int groupIndex) throws QueryException {
        for (int i = groupIndex; i >= 0; i--) {
            if (isGroupFinished(i)) {
                return true;
            }
        }
        return false;
    }

    private void printFooterGroupBands() throws QueryException {
        for (int i = groupCache.size() - 1; i >= 0; i--) {        	 
            GroupCache gc = groupCache.get(i);            
            if (!isGroupFinished(i)) {

                // test to see if some previous group is finished
                // then this group is also finished
                if (i > 0) {
                    if (isPreviousGroupFinished(i - 1)) {
                        groupFinished(gc);
                        continue;
                    }
                }

                for (FunctionCache fc : gc.getFuncCache()) {
                    Object nv;
                    if (fc.isExpression()) {                    	
                        nv = evaluateExpression(getExpressionBandElement(fc.getFunctionColumn()), currentBandName);
                    } else {
                        nv = getResult().nextValue(fc.getFunctionColumn());
                    }
                    fc.getFunction().compute(nv);                    
                }

                // increment group row for the current group
                if (gc.getGroup().equals(getGroupCache(currentBandName).getGroup()))  {
                    gc.incrementGroupRow();
                }

            } else {                
                groupFinished(gc);               
            }
        }
    }

    private void groupFinished(GroupCache gc) throws QueryException {    	
        if (gc.footerHasRows()) {
            printFooterGroupBand(gc);
        }
        gc.setStart(true);
        if (gc.getGroup().isNewPageAfter()) {
            createNewPage();
        }

        // group finished : decrement group row        
        gc.resetGroupRow();
    }

    protected void printHeaderBand() throws QueryException {
        printBand(null, getReportLayout().getHeaderBand(), false);
    }

    private void printFooterBand() throws QueryException {
        printBand(null, getReportLayout().getFooterBand(), false);
    }
    
    protected void printPageHeaderBand() throws QueryException {
        printBand(null, getReportLayout().getPageHeaderBand(), false);
    }

    protected void printPageFooterBand() throws QueryException {
        printBand(null, getReportLayout().getPageFooterBand(), false);
    }

    private void printDetailBand() throws QueryException {
        printBand(null, null, false);
    }

    private void printHeaderGroupBand(GroupCache gc) throws QueryException {
        printBand(gc, null, false);
    }

    private void printFooterGroupBand(GroupCache gc) throws QueryException {
        printBand(gc, null, true, true);
    }

    private void printFooterGroupBand(GroupCache gc, boolean usePrevious) throws QueryException {
        printBand(gc, null, true, usePrevious);
    }

    private void printBand(GroupCache gc, Band staticBand, boolean hasFunction)
            throws QueryException {
        printBand(gc, staticBand, hasFunction, false);
    }

    // Static Band : header -> ColumnBandElement and FunctionBandElement are ignored
    //               footer -> ColumnBandElement is ignored
    // Detail Band & Header Group Band : FunctionBandElement is ignored
    // Footer Group Band : nothing is ignored
    //
    // print static band (staticBand != null) : header / footer
    // print detail band (gc=null, hasFunction=false)
    // print header group band (gc!= null & hasFunction=false) or
    // footer group band (gc!= null & hasFunction=true)
    private void printBand(GroupCache gc, Band staticBand, boolean hasFunction, boolean usePrevious)
            throws QueryException {

        Band band;
        List<FunctionCache> fCache = null;
        isDetail = false;
        boolean isPageHeaderFooter = false;
        if (gc == null) {
            if (staticBand != null) {
                band = staticBand;
                if (ReportLayout.PAGE_HEADER_BAND_NAME.equals(band.getName()) || ReportLayout.PAGE_FOOTER_BAND_NAME.equals(band.getName())) {                	
                	isPageHeaderFooter = true;
                }
            } else {
                isDetail = true;
                band = getReportLayout().getDetailBand();
            }
        } else {
            fCache = gc.getFuncCache();
            if (hasFunction) {
                band = gc.getFgBand();
            } else {
                band = gc.getHgBand();
            }
        }
        currentBandName = band.getName();        

        int rows = band.getRowCount();
        int cols = band.getColumnCount();
        Set<CellElement> ignored = getIgnoredCells(band);        
        int lastRow = -1;        
        for (int i = 0; i < rows; i++) {
        	
            // hide when expression
            // a hidden cell is considered to be rendered with null value (but value is taken
            // into account in functions)
            // if all cells from a row are hidden , we consider the entire row hidden (hideAll)
            // and no cell from that row is rendered
            boolean[] hide = new boolean[cols];
            boolean hideAll = false;
            int count = 0;
            boolean rowWithHideExpression = false;
            for (int j = 0; j < cols; j++) {
                BandElement bandElement = band.getElementAt(i, j);
                if ((bandElement != null) && (bandElement.getHideWhenExpression() != null)) {
                   rowWithHideExpression = true;
                }
            }
            if (rowWithHideExpression) {
                for (int j = 0; j < cols; j++) {
                    BandElement bandElement = band.getElementAt(i, j);
                    if ((bandElement != null) && (bandElement.getHideWhenExpression() != null)) {
                        String expression = bandElement.getHideWhenExpression();
                        Boolean result = (Boolean) evaluateExpression("", expression, currentBandName, null);
                        hide[j] = result;
                    } else {
                    	// bandElement can be null in older version of reports (previous to 4.1)
                        hide[j] = (bandElement == null) || "".equals(bandElement.getText());                                                
                    }
                    if (hide[j]) {
                        count++;
                    }
                }
                if (count == cols) {
                    hideAll = true;
                }
            }
            
            for (int j = 0; j < cols; j++) {
                
                if (findIgnoredCellElement(ignored, i, j)) {
                    //System.out.println("*** header ignored i="+i + "  j="+j);                	
                    continue;
                }                                
                
                // newRow is computed relative to cells that are renedered through exportCell
                // ignored cells are not taken into account
                if (i > lastRow) {                	
                    newRow = true;                                              
                } else {
                    newRow = false;                    
                }
                
                BandElement bandElement = band.getElementAt(i, j);      
                if (bandElement != null) {
                	newRowCount = Math.max(newRowCount, bandElement.getRowSpan());
                }
                if (newRow) {                       	
                	int gridRow = getReportLayout().getGridRow(band.getName(), i);                	
                	RowElement re = getRowElement(getReportLayout(), gridRow);
                	// if new page is put for the first row in the layout, we should not create a new page
                	if (re.isStartOnNewPage() && !start) {                		
                		createNewPage();
                	}
                }
                Object value = getBandElementValue(fCache, gc, staticBand, hasFunction, usePrevious, bandElement);

                // hide when expression
                if (!hideAll && hide[j]) {
                    value = null;
                }

                int rowSpan = 1, colSpan = 1;
                if (bandElement != null) {
                    rowSpan = bandElement.getRowSpan();
                    colSpan = bandElement.getColSpan();
                }

                int gridRow = getReportLayout().getGridRow(band.getName(), i);
                boolean isImage = bandElement instanceof ImageBandElement;
                
                // subreports with parameters can be used only inside detail band
                // we must update values for subreport parameters
                // parameter name used in subreport must be the column alias from parent report !
                if (!bean.isSubreport() && isDetail) {
                	Map<String, QueryParameter> params = bean.getParametersBean().getSubreportParams();
                	for (QueryParameter qp : params.values()) {                								
						Object pValue = getResult().nextValue(qp.getName());						
						bean.getParametersBean().setParameterValue(qp.getName(), pValue);
                	}
                }
                
                if (!hideAll) {
                    exportCell(band.getName(), bandElement, value, gridRow, i, j, cols, rowSpan, colSpan, isImage);                    
                }
                lastRow = i;
                
                // after exportCell where we may use newRow and newRowCount variables
                // we need to reset newRowCount
                if (newRow) {
                	newRowCount = 1;
                }
            }
            // page header and page footer do not count for row computation
            if (!isPageHeaderFooter) {
            	exporterRow++;
            	if (!hideAll) {
            		pageRow++;
            	}            	
                start = false;                	
            }
           
        }        
    }
    
    private Object getBandElementValue(List<FunctionCache> fCache, GroupCache gc, Band staticBand, 
    		boolean hasFunction, boolean usePrevious, BandElement bandElement) throws QueryException {
    	
    	Object value = null;
        String column = null;
        if (bandElement instanceof ColumnBandElement) {
            if (staticBand == null) {
                column = ((ColumnBandElement) bandElement).getColumn();
                if (usePrevious) {
                    value = previousRow[getResult().getColumnIndex(column)];
                } else {
                    value = getResult().nextValue(column);
                }

                // here compute the footer functions
                for (FunctionCache fc : footerFunctionCache) {
                    if (!fc.isExpression() && fc.getFunctionColumn().equals(column)) {
                        fc.getFunction().compute(value);
                    }
                }

                // overwrite repeated value
                if ((gc == null) && !hasFunction) {
                    if ((value != null) && (bandElement != null) &&
                            bandElement.isRepeatedValue() &&
                            value.equals(previousRow[getResult().getColumnIndex(column)])) {
                        value = null;
                    }
                }

            } else {
                value = getStringValue(bandElement, null);
            }                      

        } else if (bandElement instanceof ExpressionBandElement) {
            if (staticBand == null) {
                 if (usePrevious) {
                    value = previousRow[getExpressionBandElementIndex((ExpressionBandElement) bandElement)];
                 } else {                                    	 
                    value = evaluateExpression((ExpressionBandElement) bandElement, currentBandName);
                 }
                // here compute the footer functions
                for (FunctionCache fc : footerFunctionCache) {
                    if (fc.isExpression() && fc.getFunctionColumn().equals( ((ExpressionBandElement) bandElement).getExpressionName() )) {
                        fc.getFunction().compute(value);
                    }
                }
                // overwrite repeated value
                if ((gc == null) && !hasFunction) {
                    if ((value != null) && (bandElement != null) &&
                            bandElement.isRepeatedValue() &&
                            value.equals(previousRow[getExpressionBandElementIndex((ExpressionBandElement) bandElement)])) {
                        value = null;
                    }
                }
            } else {
                // expression does not contain columns , just variables, parameters and literals
                value = evaluateExpression((ExpressionBandElement) bandElement, currentBandName);
            }

        } else if (bandElement instanceof FunctionBandElement) {
        	FunctionBandElement fbe = (FunctionBandElement) bandElement;
        	//String functionTemplate = getFunctionTemplate(gc, fbe);
            // functions in footer
            if (staticBand != null) {                
                for (FunctionCache fc : footerFunctionCache) {
                    if (fc.getFunction().getName().equals(fbe.getFunction()) &&
                            fc.getFunctionColumn().equals(fbe.getColumn())) {
                        value = fc.getFunction().getComputedValue();
						if (needsFirstCrossing()) {
							if (this instanceof FirstCrossingExporter) {
								if (ReportUtil.foundFunctionInHeader(bean.getReportLayout())) {
									templatesValues.put(getFunctionTemplate(gc, fbe, true), value);
								}
							} else {
								value = getFunctionTemplate(gc, fbe, false);								
								if (templatesValues.containsKey(value)) {
									value = templatesValues.get(value);									
								}
							}
						}
                        break;
                    }
                }
                // functions in group
            } else if (hasFunction) {               
                for (FunctionCache fc : fCache) {
                    if (fc.getFunction().getName().equals(fbe.getFunction()) &&
                            fc.getFunctionColumn().equals(fbe.getColumn())) {
                        value = fc.getFunction().getComputedValue();  
						if (this instanceof FirstCrossingExporter) {
							if (ReportUtil.foundFunctionInGroupHeader(bean.getReportLayout(), gc.getGroup().getName())) {
								templatesValues.put(getFunctionTemplate(gc, fbe, true), value);
							}
						}
                        break;
                    }
                }
            } else {
            	// for FunctionBandElement in header
            	// at first crossing we just save the templateValues
            	// at second crossing we have the values in map and we print them
            	if (this instanceof FirstCrossingExporter) {
            		value = getFunctionTemplate(gc, fbe, true);
            	} else {            		
            		value = getFunctionTemplate(gc, fbe, false);            		
            		if (templatesValues.containsKey(value)) {            			
            			value = templatesValues.get(value);            			
            		}
            	} 
            }
        } else if (bandElement instanceof ImageBandElement) {
        	if (bandElement instanceof ChartBandElement) {
        		// generate chart image
        		generateChartImage((ChartBandElement)bandElement);
        	} else if (bandElement instanceof BarcodeBandElement) {
        		// generate barcode image
        		generateBarcodeImage((BarcodeBandElement)bandElement);
        	} 
        	value = ((ImageBandElement) bandElement).getImage();                	
        } else if (bandElement instanceof VariableBandElement) {
            value = getStringValue((VariableBandElement) bandElement, currentBandName);
        } else if (bandElement instanceof ParameterBandElement) {
            value = getStringValue((ParameterBandElement) bandElement);
        } else {
            value = getStringValue(bandElement, null);
        }
        return value;
    }
    
    protected String getBandElementValueAsString(BandElement bandElement) {
    	String pattern = getPattern(bandElement);
    	if (bandElement instanceof ExpressionBandElement) {    		
    		Object value = null;
			try {
				value = evaluateExpression((ExpressionBandElement)bandElement, currentBandName);
			} catch (QueryException e) {
				LOG.error(e.getMessage(), e);
			}    		
            return StringUtil.getValueAsString(value, pattern);
    	} else if (bandElement instanceof VariableBandElement) {
    		return getStringValue((VariableBandElement)bandElement, currentBandName);    	
    	} else {
    		return getStringValue(bandElement, pattern);
    	}
    }
    
    protected Object evaluateExpression(ExpressionBean bean) throws QueryException {
        return evaluateExpression(bean.getBandElement().getExpressionName(), bean.getBandElement().getExpression(), bean.getBandName(), bean.getBandElement().getPattern());
    }       

    protected Object evaluateExpression(ExpressionBandElement bandElement, String bandName) throws QueryException {
        return evaluateExpression(bandElement.getExpressionName(), bandElement.getExpression(), bandName, bandElement.getPattern());
    }

    private Object evaluateExpression(String expressionName, String expression, String bandName, String pattern) throws QueryException {    	    	
        Object value = null;        
        Expression e = jexl.createExpression(expression);
        // create context with all variables, parameters and columns
        // make sure to replace spaces in column names (as in designer expression evaluator)
        JexlContext checkContext = new MapContext();            
        for (Variable var : VariableFactory.getVariables()) {
        	if (((this instanceof RtfExporter) || (this instanceof XlsExporter)) && Variable.PAGE_NO_VARIABLE.equals(var.getName())) {
        		// RtfPageNumber must be added in RtfExporter -> let the variable as it is
        		checkContext.set("$V_" + var.getName(), "$V_" + var.getName());
        	} else if ( (this instanceof PdfExporter) && Variable.TOTAL_PAGE_NO_VARIABLE.equals(var.getName()) ) {
        		// compute total page no inside PdfExporter
        		checkContext.set("$V_" + var.getName(), "$V_" + var.getName());        		
        	} else {	
        		checkContext.set("$V_" + var.getName(), getValue(var, bandName));
        	}	
        }
        for (String paramName : bean.getParametersBean().getParamValues().keySet()) {
        	Object obj = bean.getParametersBean().getParamValues().get(paramName);
        	if (obj instanceof IdName) {
        		obj = ((IdName)obj).toString();
        	}
            checkContext.set("$P_" + paramName, obj);
        }
        // expresions outside detail or group bands do not contain columns              
        if (expression.contains("$C") ) {        	
            for (int k = 0, size = getResult().getColumnCount(); k < size; k++) {
                String columnName = getResult().getColumnName(k);
                String col = columnName.replaceAll("\\s", SPACE_REPLACEMENT);
                checkContext.set("$C_" + col, getResult().nextValue(columnName));
            }
        }
        
        // ony expressions in footers can contain functions
        if (expression.contains("$F")) {
        	for (String f : bean.getReportLayout().getFunctions()) {
        		FunctionCache fc = findFunctionCache(f, bandName);
        		Double fv = new Double(0);
        		if (fc != null) {        			        		
        			fv = (Double)fc.getFunction().getComputedValue();
        		}        		
        		checkContext.set("$F_" + f,fv);
        	}			
        }
        try {
            value = e.evaluate(checkContext);
        } catch (JexlException ex) {
            ex.printStackTrace();
            LOG.error(ex.getMessage(), ex);
        }                    
        
        return value;
    }
    
    private FunctionCache findFunctionCache(String fexp, String bandName) {
    	if (bandName.startsWith(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX)) {    		
    		for (GroupCache gc : groupCache) {
    			if (gc.getFgBand().getName().equals(bandName)) {    				
    				FunctionCache fc = findFunctionCache(gc.getFuncCache(), fexp, bandName);
    				if (fc != null) {
    					return fc;
    				}	
    			}
    		}
        // document footer	
    	} else {
    		FunctionCache fc = findFunctionCache(footerFunctionCache, fexp, bandName);
    		if (fc != null) {
				return fc;
			}
    	}
    	return null;
    }
    
    private FunctionCache findFunctionCache(List<FunctionCache> list, String fexp, String bandName) {
    	for (FunctionCache fc : list) {
			String exp = fc.getFunction().getName() + "_" + fc.getFunctionColumn();    			
			if (exp.equals(fexp)) {
				return fc;
			}
		}
    	return null;
    }

    private String getStringValue(BandElement bandElement, String pattern) {
        if ((bandElement == null) || (bandElement.getText().trim().length() == 0)) {
            return getNullElement();
        }
        return StringUtil.getValueAsString(bandElement.getText(), pattern);
    }

    protected abstract String getNullElement();

    protected abstract Set<CellElement> getIgnoredCells(Band band);

    protected abstract void exportCell(String bandName, BandElement bandElement, Object value,
                                       int gridRow, int row, int column, int cols,
                                       int rowSpan, int colSpan, boolean isImage);

    protected abstract void afterRowExport();

    protected abstract void close();

    protected abstract void flush();

    protected abstract void flushNow();

    protected abstract void initExport() throws QueryException;

    protected abstract void finishExport();


    // if java.awt.hedless=true => use 96
    protected static int getDPI() {
        return GraphicsEnvironment.isHeadless() ? 96 : Toolkit.getDefaultToolkit().getScreenResolution();
    }

    // This methods allows classes to register for ExporterEvents
    public void addExporterEventListener(ExporterEventListener listener) {
        listenerList.add(ExporterEventListener.class, listener);
    }

    // This methods allows classes to unregister for ExporterEvents
    public void removeExporterEventListener(ExporterEventListener listener) {
        listenerList.remove(ExporterEventListener.class, listener);
    }

    // This private class is used to fire ExporterEvents
    void fireExporterEvent(ExporterEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == ExporterEventListener.class) {
                ((ExporterEventListener) listeners[i + 1]).notify(evt);
            }
        }
    }

    protected byte[] getImage(String image) throws IOException {
        //System.out.println("%%% LOAD IMAGE="+image);
        InputStream is = getClass().getResourceAsStream("/" + image);
        if (is == null) {
        	LOG.error("Image '" + image + "' not found in classpath.");
            throw new IOException("Image '" + image + "' not found.");
        }
        //System.out.println("%%% Image loaded.");
        ByteArrayOutputStream img_bytes = new ByteArrayOutputStream();
        int b;
        try {
            while ((b = is.read()) != -1) {
                img_bytes.write(b);
            }
        } finally {
            is.close();
        }
        return img_bytes.toByteArray();
    }

    protected int[] getRealImageSize(String image) {
        InputStream is = getClass().getResourceAsStream("/" + image);
        int[] result = new int[2];
        try {
            BufferedImage img = ImageIO.read(is);
            result[0] = img.getWidth();
            result[1] = img.getHeight();
        } catch (IOException ex) {
            ex.printStackTrace();
            LOG.error(ex.getMessage(), ex);
        }
        return result;
    }
    
    protected int[] getRealImageSize(byte[] image) {    	    	
    	InputStream is = new ByteArrayInputStream(image);
    	int[] result = new int[2];
        try {
            BufferedImage img = ImageIO.read(is);
            result[0] = img.getWidth();
            result[1] = img.getHeight();
        } catch (IOException ex) {
            ex.printStackTrace();
            LOG.error(ex.getMessage(), ex);
        }
        return result;
    }

    protected byte[] getScaledImage(String image, int width, int height) throws IOException {
        InputStream is = getClass().getResourceAsStream("/" + image);
        if (is == null) {
            throw new IOException("Image '" + image + "' not found.");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            BufferedImage img = ImageIO.read(is);

            if ((img.getWidth() == width) && (img.getHeight() == height)) {
                // original width and height                
                ImageIO.write(img, "png", baos);
            } else {
                int type = img.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : img.getType();
                BufferedImage scaledImg = new BufferedImage(width, height, type);
                Graphics2D gScaledImg = scaledImg.createGraphics();

                gScaledImg.setComposite(AlphaComposite.Src);
                gScaledImg.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                gScaledImg.setRenderingHint(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY);

                gScaledImg.drawImage(img, 0, 0, width, height, null);

                ImageIO.write(scaledImg, "png", baos);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            LOG.error(ex.getMessage(), ex);
            throw new IOException("Image '" + image + "' could not be scaled.");
        } finally {
            is.close();
        }
        return baos.toByteArray();
    }

    protected byte[] getImage(String image, Integer width, Integer height) throws IOException {
        byte[] imageBytes;
        if ((width == null) || (width.intValue() == 0) || (height == null) || (height.intValue() == 0)) {
            imageBytes = getImage(image);
        } else {
            imageBytes = getScaledImage(image, width, height);
        }
        return imageBytes;
    }

    protected void printRawRecord() throws QueryException {
    }

    private void createNewPage() {
        pageRow = 0;
        newPage();
    }

    protected void newPage() {

    }
    
    // used only by PDF exporter
    // for RTF we must interpret PAGE_NO variable in header page band
    protected int getPageNo() {
    	return pageNo + 1;
    }
    
    protected int getTotalPageNo() {
    	return totalPageNo;
    }

    protected int getHeaderRows() {
        return bean.getReportLayout().getBand(ReportLayout.HEADER_BAND_NAME).getRowCount();
    }
    
    protected int getDetailRows() {
        return bean.getReportLayout().getBand(ReportLayout.DETAIL_BAND_NAME).getRowCount();
    }
    
    protected int getFooterRows() {
        return bean.getReportLayout().getBand(ReportLayout.FOOTER_BAND_NAME).getRowCount();
    }
    
    protected int getRowsCount() {
    	int recordRows = getResult().getRowCount();
    	if (recordRows == -1) {
    		throw new UnsupportedOperationException("Must use true value for computeCount inside QueryExecutor in order to get rows count!");
    	}
    	return getHeaderRows() + 
    		   getDetailRows() * recordRows +
    		   getFooterRows();	
    }

    private ExpressionBandElement getExpressionBandElement(String name) {
        for (ExpressionBean bean : expressions) {
        	ExpressionBandElement ebe = bean.getBandElement();
            if (ebe.getExpressionName().equals(name)) {
                return ebe;
            }
        }
        return null;
    }

    private int getExpressionBandElementIndex(ExpressionBandElement bandElement) {
       for (int i=0, size=expressions.size(); i<size; i++) {
    	   ExpressionBandElement ebe = expressions.get(i).getBandElement();
           if (bandElement.getExpressionName().equals(ebe.getExpressionName())) {
               return i + getResult().getColumnCount();
           }
       }
       return -1;
    }

    private int getGroupIndex(String bandName) {
        int groupIndex = -1;
        if (bandName.startsWith(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX)) {
            groupIndex = Integer.parseInt(bandName.substring(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX.length()));
        } else if (bandName.startsWith(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX)) {
            groupIndex = Integer.parseInt(bandName.substring(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX.length()));
        } else if (ReportLayout.DETAIL_BAND_NAME.equals(bandName)) {
            if (groupCache.size() > 0) {
                groupIndex = groupCache.size();
            }
        }
        return groupIndex;
    }

    private GroupCache getGroupCache(String bandName) {
        int groupIndex = getGroupIndex(bandName);
        GroupCache group = null;
        if (groupIndex != -1) {
            group = groupCache.get(groupIndex-1);
        }
        return group;
    }

    private GroupCache getOutterGroupCache(String bandName) {
        int groupIndex = getGroupIndex(bandName);       
        if (ReportUtil.isGroupBand(bandName)) {
            if (groupIndex > 1) {
                groupIndex--;
            } else if (groupIndex == 1) {
                groupIndex = -1;
            }
        }

        GroupCache group = null;
        if (groupIndex != -1) {
            group = groupCache.get(groupIndex-1);
        }
        return group;

    }

    private GroupCache getOutterGroupCache(GroupCache gc) {
       for (int i=0, size=groupCache.size(); i<size; i++) {
           GroupCache outerGc = groupCache.get(i);
           if (gc.getGroup().equals(outerGc.getGroup())) {
               if (i > 0) {
                   return groupCache.get(i-1);
               }
           }
       }
       return null;
    }
    
    private void generateChartImage(ChartBandElement bandElement) {
    	if (bean.getConnection() == null) {
    		return;
    	}
    	String image = null;
    	Chart chart = bandElement.getChart();
    	ChartRunner runner = new ChartRunner();
        runner.setFormat(ChartRunner.IMAGE_FORMAT);
        runner.setChart(chart);
        runner.setConnection(bean.getConnection());
        runner.setQueryTimeout(bean.getQueryTimeout());
        runner.setParameterValues(bean.getParametersBean().getParamValues());        
        runner.setImagePath(imageChartPath);  
        int width = (bandElement.getWidth() == null) ? 0 : bandElement.getWidth();
        int height = (bandElement.getHeight() == null) ? 0 : bandElement.getHeight();
        runner.setImageWidth(width);
        runner.setImageHeight(height);
        try {                        
            runner.run();
            image = runner.getChartImageName();
            bandElement.setImage(image);
        } catch (Exception e) {          
            e.printStackTrace();            
        }   
    }
    
    private void generateBarcodeImage(BarcodeBandElement bandElement) {    	
    	if (bean.getConnection() == null) {
    		return;
    	}    	    	
    	int width = (bandElement.getWidth() == null) ? 1 : bandElement.getWidth();
		int height = (bandElement.getHeight() == null) ? 1 : bandElement.getHeight();
        String value = bandElement.getValue();
        if (bandElement.isColumn()) {
        	try {
				value = String.valueOf(getResult().nextValue(value));
			} catch (QueryException e) {
				e.printStackTrace();
			}
        }
        Image image = null;       
		if (BarcodeBandElement.isEANFamily(bandElement.getBarcodeType())) {
			BarcodeEAN codeEAN = new BarcodeEAN();
			codeEAN.setCodeType(bandElement.getBarcodeType());
			codeEAN.setCode(value);			
			image = codeEAN.createAwtImage(Color.BLACK, Color.WHITE);
		} else {
			if (bandElement.getBarcodeType() == BarcodeBandElement.PDF417) {
				BarcodePDF417 barcode417 = new BarcodePDF417();
				barcode417.setText(value);				
				image = barcode417.createAwtImage(Color.BLACK, Color.WHITE);
			} else if (bandElement.getBarcodeType() == BarcodeBandElement.DATAMATRIX) {
				BarcodeDatamatrix datamatrix = new BarcodeDatamatrix();					
				try {
					datamatrix.generate(value);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}				
				image = datamatrix.createAwtImage(Color.BLACK, Color.WHITE);
			} else if (bandElement.getBarcodeType() == BarcodeBandElement.QRCODE) {				
				BarcodeQRCode qrcode = new BarcodeQRCode(value, width, height, null);				
				image = qrcode.createAwtImage(Color.BLACK, Color.WHITE);
			} else {
				Barcode barcode = null;
				if (bandElement.getBarcodeType() == BarcodeBandElement.CODE128) {
					barcode = new Barcode128();					
				} else if (bandElement.getBarcodeType() == BarcodeBandElement.CODE128_RAW) {
					barcode = new Barcode128();
					barcode.setCodeType(bandElement.getBarcodeType());
				} else if (bandElement.getBarcodeType() == BarcodeBandElement.INTER25) {
					barcode = new BarcodeInter25();
				} else if (bandElement.getBarcodeType() == BarcodeBandElement.CODE39) {
					barcode = new Barcode39();
				} else if (bandElement.getBarcodeType() == BarcodeBandElement.CODE39EXT) {
					barcode = new Barcode39();
					barcode.setStartStopText(false);
					barcode.setExtended(true);
				} else if (bandElement.getBarcodeType() == BarcodeBandElement.CODABAR) {
					barcode = new BarcodeCodabar();
				}
				barcode.setCode(value);				
				image = barcode.createAwtImage(Color.BLACK, Color.WHITE);
			}
		}
        String imageName = saveBarcode(bandElement, toBufferedImage(image), "png"); 
        bandElement.setImage(imageName);
    }
    
    private BufferedImage toBufferedImage(Image src) {
        int w = src.getWidth(null);
        int h = src.getHeight(null);
        int type = BufferedImage.TYPE_INT_RGB;  
        BufferedImage dest = new BufferedImage(w, h, type);
        Graphics2D g2 = dest.createGraphics();
        g2.drawImage(src, 0, 0, null);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.dispose();
        return dest;
    }
    
    private String saveBarcode(BarcodeBandElement bandElement, BufferedImage image, String ext) {    	
        String fileName = "barcode_" + bandElement.getBarcodeType() + "_" + barcodeIndex + "." + ext;
        barcodeIndex++;
        File file = new File(imageChartPath + File.separator + fileName);
        try {
            ImageIO.write(image, ext, file);  // ignore returned boolean
        } catch(IOException e) {
        	e.printStackTrace();            
        }
        return fileName;
    }
    
    
    public int getPoints(int pixels) {
    	// 1 inch = 72 points
    	return (int) ( (pixels * 72f) / getDPI() );
    }
    
    
    // for exporters who need to replace PAGE_NO variable (RTF, XLS)
    protected PrefixSuffix interpretPageNo(BandElement bandElement) {
    	String exp = "";
		try {
			String pattern = "";
			if (bandElement instanceof FieldBandElement) {
				pattern = ((FieldBandElement)bandElement).getPattern();
			}
			exp = StringUtil.getValueAsString( evaluateExpression(((ExpressionBandElement) bandElement), ""), pattern);
			String name = "$V_" + Variable.PAGE_NO_VARIABLE;
			return StringUtil.parse(exp, name);
		} catch (QueryException ex) {
			ex.printStackTrace();
		}
		return null;
    }       
    
    private RowElement getRowElement(ReportLayout layout, int gridRow) {
		if (rowMap == null) {
			rowMap = new HashMap<Integer, RowElement>();
			List<Band> bands = layout.getBands();
			int currentRow = 0;
			for (Band band : bands) {
				int rows = band.getRowCount();
				for (int i = 0; i < rows; i++) {
					int gr = layout.getGridRow(band.getName(), i);
					rowMap.put(gr, band.getElements().get(i));						
				}
			}			
		}
		return rowMap.get(gridRow);
       
    }  
    
    protected ExporterBean getSubreportExporterBean(Report subreport) throws Exception {		
		return getSubreportExporterBean(subreport, false);
	}
    
	protected ExporterBean getSubreportExporterBean(Report subreport, boolean rowCount) throws Exception {		
		String sql = ReportUtil.getSql(subreport);
		Query query = new Query(sql);
		// put subreport parameters
		bean.getParametersBean().addSubreportParameters(subreport.getParameters());
		// for reports inside ForReportBandElement we must overwrite parameter values
		// see ReportUtil.getForReportLayout where generated parameters are set
		bean.getParametersBean().overwriteSubreportParametersValues(subreport.getGeneratedParamValues());
		QueryExecutor executor = new QueryExecutor(query, bean.getParametersBean().getParams(), bean.getParametersBean()
				.getParamValues(), bean.getConnection(), rowCount);
		executor.setMaxRows(0);
		executor.setTimeout(bean.getQueryTimeout());
		QueryResult queryResult = executor.execute();
		boolean isProcedure = QueryUtil.isProcedureCall(sql);
		ExporterBean eb = new ExporterBean(bean.getConnection(), bean.getQueryTimeout(), queryResult, bean.getOut(),
				subreport.getLayout(), bean.getParametersBean(), subreport.getBaseName(), false, isProcedure);
		eb.setSubreport(true);
		return eb;
	}
	
	private VariableBandElement getTotalPageNoVbe(ReportLayout layout) {
		List<Band> bands = layout.getBands();
		for (Band band : bands) {
			for (int i = 0, rows = band.getRowCount(); i < rows; i++) {
				List<BandElement> elements = band.getRow(i);
				for (BandElement be : elements) {
					if ((be instanceof VariableBandElement)
							&& (VariableFactory.getVariable(((VariableBandElement) be).getVariable()) instanceof TotalPageNoVariable)) {
						return (VariableBandElement) be;
					}
				}
			}
		}
		return null;
	}		
	
	protected boolean isAlert(Alert alert, Object value) {		
		return (alert != null) && alert.isActive(value);
	}

	private void alert(Alert alert, Object value, String message) {
		if (alert != null) {
			alert.run(value, message);
		}
	}

	protected void executeAlert(final Alert alert, final Object value, final String message) {
		Runnable r = new Runnable() {
			@Override
			public void run() {				
				alert(alert, value, message);
			}
		};
		new Thread(r).start();
	}
	
	// string template used by functions in header and group header bands
	private String getFunctionTemplate(GroupCache gc, FunctionBandElement fbe, boolean previous) throws QueryException {
		
		StringBuilder templateKey = new StringBuilder();
		if (gc == null) {
			// function in Header
			templateKey.append("F_").
    		append(fbe.getFunction()).append("_").
    		append(fbe.getColumn());
			return templateKey.toString();
		}
		
		// function in group header
		String groupColumn = gc.getGroup().getColumn();
		Object groupValue;
		if (previous) {
			if (resultSetRow == 0) {
				groupValue = getResult().nextValue(groupColumn);
			} else {
				groupValue = previousRow[getResult().getColumnIndex(groupColumn)];
			}			
		} else {
			groupValue = getResult().nextValue(groupColumn);			
		}
		// keep the current value of the group
		groupTemplateKeys.put("G"+ gc.getGroup().getName(), "G"+ gc.getGroup().getName() +  "_" +  previousRow[getResult().getColumnIndex(groupColumn)]);		
		    	
    	templateKey.append("G").append(gc.getGroup().getName()).append("_F_").
    		append(fbe.getFunction()).append("_").
    		append(fbe.getColumn()).append("_").
    		append(groupValue);    	    
    	
    	int group = Integer.parseInt(gc.getGroup().getName());
    	StringBuilder result = new StringBuilder();
    	for (int i=1; i<group; i++) {    		
    		result.append(groupTemplateKeys.get("G"+ i)).append("_");    		
    	}
    	result.append(templateKey.toString());
    	
    	return result.toString();
	}

	public Map<String, Object> getTemplatesValues() {
		return templatesValues;
	}	
	
	public Map<String, String> getGroupTemplateKeys() {
		return groupTemplateKeys;
	}
	
	// if we need to compute something before we start the print process
	private boolean needsFirstCrossing() {
		return ReportUtil.foundFunctionInHeader(bean.getReportLayout()) ||
			   ReportUtil.foundFunctionInAnyGroupHeader(bean.getReportLayout());
	}	
	
	private Band getBand(ReportLayout layout, int gridRow) {
        List<Band> bands = layout.getBands();
        int currentRow = 0;
        for (Band band : bands) {
            int rows = band.getRowCount();
            currentRow += rows;
            if (gridRow < currentRow) {
                return band;
            }
        }
        return null;
    }
       
}
