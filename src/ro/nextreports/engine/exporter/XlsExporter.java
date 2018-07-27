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
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.model.InternalSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.util.HSSFRegionUtil;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import ro.nextreports.engine.ReleaseInfoAdapter;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.Border;
import ro.nextreports.engine.band.ExpressionBandElement;
import ro.nextreports.engine.band.FieldBandElement;
import ro.nextreports.engine.band.Hyperlink;
import ro.nextreports.engine.band.HyperlinkBandElement;
import ro.nextreports.engine.band.ImageBandElement;
import ro.nextreports.engine.band.ImageColumnBandElement;
import ro.nextreports.engine.band.ReportBandElement;
import ro.nextreports.engine.band.VariableBandElement;
import ro.nextreports.engine.exporter.util.ExcelColorSupport;
import ro.nextreports.engine.exporter.util.StyleFormatConstants;
import ro.nextreports.engine.exporter.util.XlsUtil;
import ro.nextreports.engine.exporter.util.variable.PageNoVariable;
import ro.nextreports.engine.exporter.util.variable.Variable;
import ro.nextreports.engine.exporter.util.variable.VariableFactory;
import ro.nextreports.engine.queryexec.QueryException;
import ro.nextreports.engine.util.PrefixSuffix;
import ro.nextreports.engine.util.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Dec 4, 2008
 * Time: 9:53:57 AM
 */
public class XlsExporter extends ResultExporter {
	
	private static Log LOG = LogFactory.getLog(XlsExporter.class);

    //todo : character width?? (use 5)
    // method setColumnWidth(int column, int width) from poi
    // width is in units of 1/256 from character width (must be less or equal than 65536)
    // 72 is computer screen dpi
    // system dpi is Toolkit.getDefaultToolkit().getScreenResolution()        
    private static float POINTS_FOR_PIXEL = 72f * 256 / 5 / getDPI();
    private int prevSubreportFirstRow = -1;
    private int prevSubreportFirstColumn = 0;
    private int prevSubreportLastColumn = -1;    
    private int addedPageRows = 0;

    public XlsExporter(ExporterBean bean) {
        super(bean);
    }
    
    // constructor used by a subreport exporter    
    private XlsExporter(ExporterBean bean, HSSFCellStyle cellStyle) {
    	super(bean);
    	subreportCellStyle = cellStyle;
    }
      
    protected void initExport() throws QueryException {    	
    	if (hasTemplate()) {    		
    		try {
				wb = new HSSFWorkbook(getTemplateInputStream());
			} catch (IOException e) {
				e.printStackTrace();
				LOG.error(e.getMessage(), e);
				wb = new HSSFWorkbook();   
			}
    	} else {
    		wb = new HSSFWorkbook();
    	}
    }

    protected void finishExport() {    	
    	String sheetName = bean.getReportLayout().getSheetNames();		
		if (sheetNameContainsGroup(sheetName)) {
			String actualName = replaceSheetNameParam(sheetName);
	    	if (wb.getSheetName(page-3).equals(actualName)) {
	    		// after group we may have other pages!
	    		wb.setSheetName(page-2, String.valueOf(page-1));
	    	} else {
	    		wb.setSheetName(page-2, actualName);
	    	}
		}
		if (!bean.isSubreport()) {
			addRegions(xlsSheet, regions, wb);
			try {
				wb.write(getOut());
				getOut().flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					getOut().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		//System.out.println("*** generated condStyles = " + condStyles.size());
		//System.out.println("*** generated styles = " + styles.size());
    }

    // must be called after the file creation !!!
    // is called also on the server
    public static void createSummaryInformation(String filePath, String title) {

        if (filePath == null) {
            return;
        }
        try {
            File poiFilesystem = new File(filePath);
            InputStream is = new FileInputStream(poiFilesystem);
            POIFSFileSystem poifs = new POIFSFileSystem(is);
            is.close();
            DirectoryEntry dir = poifs.getRoot();

            SummaryInformation si = PropertySetFactory.newSummaryInformation();
            si.setTitle(title);
            si.setAuthor(ReleaseInfoAdapter.getCompany());
            si.setApplicationName("NextReports " + ReleaseInfoAdapter.getVersionNumber());
            si.setSubject("Created by NextReports Designer" + ReleaseInfoAdapter.getVersionNumber());
            si.setCreateDateTime(new Date());
            si.setKeywords(ReleaseInfoAdapter.getHome());

            si.write(dir, SummaryInformation.DEFAULT_STREAM_NAME);

            OutputStream out = new FileOutputStream(poiFilesystem);
            poifs.writeFilesystem(out);
            out.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

	protected void close() {
		if (!bean.isSubreport()) {
			try {
				getOut().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

    protected void flush() {
    }

    protected void flushNow() {
    }


    protected Set<CellElement> getIgnoredCells(Band band) {
        return new HashSet<CellElement>();
    }

    protected void exportCell(String bandName, BandElement bandElement, Object value, int gridRow, int row,
                              int column, int cols, int rowSpan, int colSpan, boolean isImage) {
    	    	
    	if (ReportLayout.PAGE_HEADER_BAND_NAME.equals(bandName)) {    	    		
    		renderCellToHeaderFooter(headerS, bandName, bandElement, value, gridRow, row, column, cols, rowSpan, colSpan, isImage);    		
    	} else if (ReportLayout.PAGE_FOOTER_BAND_NAME.equals(bandName)) { 
    		renderCellToHeaderFooter(footerS, bandName, bandElement, value, gridRow, row, column, cols, rowSpan, colSpan, isImage);
		} else {

			int sheetRow = pageRow % fragmentsize;
			if (column == 0) {
				if (sheetRow == 0) {
					if (page > 1) {
						// wb.write(out);
					}

					if ((page == 1) || (pageRow > 0)) {
						newPage();
						pageRow = 0;
					}
				}
				xlsRow = xlsSheet.createRow(sheetRow);
			}
			if (bean.getReportLayout().isUseSize()) {
				int width = (int) (bean.getReportLayout().getColumnsWidth().get(column) * POINTS_FOR_PIXEL);
				// System.out.println("row="+row+ "  col="+column +
				// "  width="+width);
				xlsSheet.setColumnWidth(column, width);
			}
						
			renderCell(bandElement, bandName, value, gridRow, sheetRow, column, rowSpan, colSpan, isImage);
		}
    }        

    protected void afterRowExport() {
        addRegions(xlsSheet, regions, wb);
    }

    protected String getNullElement() {
        return "";
    }

    ///// EXCEL stuff
    private int page = 1;
    private int fragmentsize = 65000;
    private HSSFWorkbook wb;
    private HSSFSheet xlsSheet = null;
    private HSSFRow xlsRow = null;
    private List<XlsRegion> regions = new ArrayList<XlsRegion>();
    private HSSFPatriarch patriarch;
    private StringBuilder headerS = new StringBuilder();
    private StringBuilder footerS = new StringBuilder();
    private HSSFCellStyle subreportCellStyle;
        
    // reuse fonts and styles
    // there is a maximum number of unique fonts in a workbook (512)
    // there is a maximum number of cell formats (4000)
    private Map<Integer, HSSFCellStyle> styles = new HashMap<Integer, HSSFCellStyle>();
    // styles used by formatting conditions
    private Map<Integer, HSSFCellStyle> condStyles = new HashMap<Integer, HSSFCellStyle>();
    
    private Map<Integer, HSSFFont> fonts = new HashMap<Integer, HSSFFont>();
    // fonts used by formatting conditions
    private Map<Integer, HSSFFont> condFonts = new HashMap<Integer, HSSFFont>();
    
    private Border border;

    private HSSFCellStyle buildBandElementStyle(BandElement bandElement, Object value, int gridRow, int gridColumn, int colSpan) {
        Map<String, Object> style = buildCellStyleMap(bandElement, value, gridRow, gridColumn, colSpan);
        HSSFCellStyle cellStyle = null;
        HSSFFont cellFont = null;
        int fontKey = -1;
        int styleKey = -1;
        // we have to create new fonts and styles if some formatting conditions are met  
        // also for subreports we may have a subreportCellStyle passed by ReportBandElement 
        boolean cacheFont = false;
        boolean cacheAllFont = false;
        boolean cacheStyle = false;
        boolean cacheAllStyle = false;
        if ((modifiedStyle[gridRow][gridColumn]) || bean.isSubreport()) {
        	fontKey = getFontKey(style);
        	if (fontKey != -1) {
        		cellFont = condFonts.get(fontKey);        		
        	}            
            if (cellFont == null) {
            	cellFont = wb.createFont();
            	cacheFont = true;
            }	
            
            styleKey = getStyleKey(style, bandElement);
            if (styleKey != -1) {
            	cellStyle = condStyles.get(styleKey);
            }
            if (cellStyle == null) {
            	cellStyle = wb.createCellStyle();
            	cacheStyle = true;
            }
            modifiedStyle[gridRow][gridColumn] = false;
        } else {            
            fontKey = getFontKey(style);
        	if (fontKey != -1) {        		
        		cellFont = fonts.get(fontKey);        		
        	}
        	if ((cellFont == null) && (bandElement != null)) {        		
            	cellFont = wb.createFont();            	
            	cacheAllFont = true;
            }	
        	
        	styleKey = getStyleKey(style, bandElement);
            if (styleKey != -1) {
            	cellStyle = styles.get(styleKey);
            }
            if (cellStyle == null) {
            	cellStyle = wb.createCellStyle();
            	cacheAllStyle = true;
            }        	        	
        }

        // HSSFPalette cellPal = wb.getCustomPalette();        
		if (style.containsKey(StyleFormatConstants.FONT_FAMILY_KEY)) {
			String val = (String) style.get(StyleFormatConstants.FONT_FAMILY_KEY);
			cellFont.setFontName(val);
		}
		if (style.containsKey(StyleFormatConstants.FONT_SIZE)) {
			Float val = (Float) style.get(StyleFormatConstants.FONT_SIZE);
			cellFont.setFontHeightInPoints(val.shortValue());
		}
		if (style.containsKey(StyleFormatConstants.FONT_COLOR)) {
			Color val = (Color) style.get(StyleFormatConstants.FONT_COLOR);
			cellFont.setColor(ExcelColorSupport.getNearestColor(val));
		}
		if (style.containsKey(StyleFormatConstants.FONT_STYLE_KEY)) {
			if (StyleFormatConstants.FONT_STYLE_NORMAL.equals(style.get(StyleFormatConstants.FONT_STYLE_KEY))) {
				cellFont.setBold(false);
			}
			if (StyleFormatConstants.FONT_STYLE_BOLD.equals(style.get(StyleFormatConstants.FONT_STYLE_KEY))) {
				cellFont.setBold(true);
                cellFont.setBold(true);

            }
			if (StyleFormatConstants.FONT_STYLE_ITALIC.equals(style.get(StyleFormatConstants.FONT_STYLE_KEY))) {
				cellFont.setItalic(true);
			}
			if (StyleFormatConstants.FONT_STYLE_BOLDITALIC.equals(style.get(StyleFormatConstants.FONT_STYLE_KEY))) {
				cellFont.setBold(true);
				cellFont.setItalic(true);
			}
		}
        
        if (cacheFont && (fontKey != -1)) {
        	condFonts.put(fontKey, cellFont);
        }
        if (cacheAllFont && (fontKey != -1)) {
        	fonts.put(fontKey, cellFont);        	
        }
        if (style.containsKey(StyleFormatConstants.BACKGROUND_COLOR)) {
            Color val = (Color) style.get(StyleFormatConstants.BACKGROUND_COLOR);
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle.setFillForegroundColor(ExcelColorSupport.getNearestColor(val));
        }
        if (style.containsKey(StyleFormatConstants.HORIZONTAL_ALIGN_KEY)) {
            if (StyleFormatConstants.HORIZONTAL_ALIGN_LEFT.equals(
                    style.get(StyleFormatConstants.HORIZONTAL_ALIGN_KEY))) {
                cellStyle.setAlignment(HorizontalAlignment.LEFT);
            }
            if (StyleFormatConstants.HORIZONTAL_ALIGN_RIGHT.equals(
                    style.get(StyleFormatConstants.HORIZONTAL_ALIGN_KEY))) {
                cellStyle.setAlignment(HorizontalAlignment.RIGHT);
            }
            if (StyleFormatConstants.HORIZONTAL_ALIGN_CENTER.equals(
                    style.get(StyleFormatConstants.HORIZONTAL_ALIGN_KEY))) {
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
            }
        }

        if (style.containsKey(StyleFormatConstants.VERTICAL_ALIGN_KEY)) {
            if (StyleFormatConstants.VERTICAL_ALIGN_TOP.equals(
                    style.get(StyleFormatConstants.VERTICAL_ALIGN_KEY))) {
                cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
            }
            if (StyleFormatConstants.VERTICAL_ALIGN_MIDDLE.equals(
                    style.get(StyleFormatConstants.VERTICAL_ALIGN_KEY))) {
                cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            }
            if (StyleFormatConstants.VERTICAL_ALIGN_BOTTOM.equals(
                    style.get(StyleFormatConstants.VERTICAL_ALIGN_KEY))) {
                cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
            }
        } else {
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        }

        short left = 0, right = 0, top = 0, bottom = 0; 
        Color leftColor = Color.BLACK, rightColor = Color.BLACK, topColor = Color.BLACK, bottomColor = Color.BLACK;
        if (style.containsKey(StyleFormatConstants.BORDER_LEFT)) {
            Float val = (Float) style.get(StyleFormatConstants.BORDER_LEFT);
            //
            left = val.shortValue();
            if (left == BORDER_THIN_VALUE) {
                cellStyle.setBorderLeft(BorderStyle.THIN);
            }
            if (left == BORDER_MEDIUM_VALUE) {
                cellStyle.setBorderLeft(BorderStyle.MEDIUM);
            }
            if (left == BORDER_THICK_VALUE) {
                cellStyle.setBorderLeft(BorderStyle.THICK);
            }
            
            Color color = (Color) style.get(StyleFormatConstants.BORDER_LEFT_COLOR);
            leftColor = color;
            cellStyle.setLeftBorderColor(ExcelColorSupport.getNearestColor(color));
        }
        if (style.containsKey(StyleFormatConstants.BORDER_RIGHT)) {        	
            Float val = (Float) style.get(StyleFormatConstants.BORDER_RIGHT);
            //
            right = val.shortValue();
            if (right == BORDER_THIN_VALUE) {
                cellStyle.setBorderRight(BorderStyle.THIN);
            }
            if (right == BORDER_MEDIUM_VALUE) {
                cellStyle.setBorderRight(BorderStyle.MEDIUM);
            }
            if (right == BORDER_THICK_VALUE) {
                cellStyle.setBorderRight(BorderStyle.THICK);
            }
            Color color = (Color) style.get(StyleFormatConstants.BORDER_RIGHT_COLOR);         
            rightColor = color;
            cellStyle.setRightBorderColor(ExcelColorSupport.getNearestColor(color));
        }
        if (style.containsKey(StyleFormatConstants.BORDER_TOP)) {
            Float val = (Float) style.get(StyleFormatConstants.BORDER_TOP);
            //
            top = val.shortValue();
            if (top == BORDER_THIN_VALUE) {
                cellStyle.setBorderTop(BorderStyle.THIN);
            }
            if (top == BORDER_MEDIUM_VALUE) {
                cellStyle.setBorderTop(BorderStyle.MEDIUM);
            }
            if (top == BORDER_THICK_VALUE) {
                cellStyle.setBorderTop(BorderStyle.THICK);
            }
            Color color = (Color) style.get(StyleFormatConstants.BORDER_TOP_COLOR);
            topColor = color;
            cellStyle.setTopBorderColor(ExcelColorSupport.getNearestColor(color));
        }
        if (style.containsKey(StyleFormatConstants.BORDER_BOTTOM)) {
            Float val = (Float) style.get(StyleFormatConstants.BORDER_BOTTOM);
            //
            bottom = val.shortValue();
            if (bottom == BORDER_THIN_VALUE) {
                cellStyle.setBorderBottom(BorderStyle.THIN);
            }
            if (bottom == BORDER_MEDIUM_VALUE) {
                cellStyle.setBorderBottom(BorderStyle.MEDIUM);
            }
            if (bottom == BORDER_THICK_VALUE) {
                cellStyle.setBorderBottom(BorderStyle.THICK);
            }
            Color color = (Color) style.get(StyleFormatConstants.BORDER_BOTTOM_COLOR);
            bottomColor = color;
            cellStyle.setBottomBorderColor(ExcelColorSupport.getNearestColor(color));
        }
        border = new Border(left, right, top, bottom);
        border.setLeftColor(leftColor);
        border.setRightColor(rightColor);
        border.setTopColor(topColor);
        border.setBottomColor(bottomColor);

        if (cellFont != null) {
        	cellStyle.setFont(cellFont);
        }

        if (style.containsKey(StyleFormatConstants.PATTERN)) {
            String pattern = (String) style.get(StyleFormatConstants.PATTERN);
            HSSFDataFormat format = wb.createDataFormat();
            cellStyle.setDataFormat(format.getFormat(pattern));
        } else {
        	cellStyle.setDataFormat((short)0);
        }

        if (bandElement != null) {
            cellStyle.setWrapText(bandElement.isWrapText());
        }
        
        cellStyle = updateSubreportBandElementStyle(cellStyle, bandElement, value, gridRow, gridColumn, colSpan);
        
        if (cacheStyle && (styleKey != -1)) {
        	condStyles.put(styleKey, cellStyle);
        }
        if (cacheAllStyle && (styleKey != -1)) {
        	styles.put(styleKey, cellStyle);
        }

        return cellStyle;
    }
    
    // If a border style is set on a ReportBandElement we must apply it to all subreport cells
    private HSSFCellStyle updateSubreportBandElementStyle(HSSFCellStyle cellStyle, BandElement bandElement, Object value, int gridRow, int gridColumn, int colSpan) {
    	if (subreportCellStyle == null) {
    		return cellStyle;
    	}
    	    	    	    	
    	if (gridColumn == 0) {    		
    		cellStyle.setBorderLeft(BorderStyle.THIN);
    		cellStyle.setLeftBorderColor(subreportCellStyle.getLeftBorderColor());    	
    	} else if (gridColumn+colSpan-1 == bean.getReportLayout().getColumnCount()-1) {    		
    		cellStyle.setBorderRight(BorderStyle.THIN);
    		cellStyle.setRightBorderColor(subreportCellStyle.getRightBorderColor());
    	}     	    	
    	
    	if (pageRow == 0) {    		    		
    		cellStyle.setBorderTop(BorderStyle.THIN);
    		cellStyle.setTopBorderColor(subreportCellStyle.getTopBorderColor());  
    	} else if ( (pageRow+1) == getRowsCount()) {    	    		
    		cellStyle.setBorderBottom(BorderStyle.THIN);
    		cellStyle.setBottomBorderColor(subreportCellStyle.getBottomBorderColor());
    	}    	
    	    
    	return cellStyle;
    }


    private void renderCell(BandElement bandElement, String bandName, Object value,
                            int gridRow, int sheetRow, int sheetColumn, int rowSpan,
                            int colSpan, boolean image) {    	
    	    	
    	if (bandElement instanceof ReportBandElement)  {
    		colSpan = 1;
    	}    	
        HSSFCellStyle cellStyle = buildBandElementStyle(bandElement, value, gridRow, sheetColumn, colSpan);
        
        // if we have a subreport on the current grid row we have to take care of the sheetColumn
        if (ReportLayout.HEADER_BAND_NAME.equals(bandName) && (gridRow == prevSubreportFirstRow) && (prevSubreportLastColumn != -1)) {        	
    		sheetColumn = prevSubreportLastColumn - prevSubreportFirstColumn - 1 + sheetColumn;    		
    	}
        HSSFCell c = xlsRow.createCell(sheetColumn);

        if (image) {        	        	
            if ((value == null) || "".equals(value)) {
                c.setCellType(HSSFCell.CELL_TYPE_STRING);
                c.setCellValue(new HSSFRichTextString(IMAGE_NOT_FOUND));
            } else {
                try {
                    ImageBandElement ibe = (ImageBandElement)bandElement;
                    byte[] imageBytes = getImage((String) value, ibe.getWidth(), ibe.getHeight());
                    HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 0, (short) sheetColumn, sheetRow,
                            (short) (sheetColumn + colSpan), (sheetRow + rowSpan));
                    int index = wb.addPicture(imageBytes, HSSFWorkbook.PICTURE_TYPE_JPEG);

                    // image is created over the cells, so if it's height is bigger we set the row height
                    short height = xlsRow.getHeight();
                    int realImageHeight = getRealImageSize((String) value)[1];
                    if (ibe.isScaled()) {
                        realImageHeight = ibe.getHeight();
                    }
                    short imageHeight = (short)(realImageHeight * POINTS_FOR_PIXEL/2.5);
                    if (imageHeight > height)  {
                        xlsRow.setHeight(imageHeight);
                    }

                    HSSFPicture picture = patriarch.createPicture(anchor, index);
                    picture.resize();
                    anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
                } catch (Exception ex) {
                    c.setCellType(HSSFCell.CELL_TYPE_STRING);
                    c.setCellValue(new HSSFRichTextString(IMAGE_NOT_LOADED));
                }
            }

            if (cellStyle != null) {
                c.setCellStyle(cellStyle);
            }

        } else {
            if (bandElement instanceof HyperlinkBandElement) {
                Hyperlink hyp = ((HyperlinkBandElement) bandElement).getHyperlink();
                HSSFHyperlink link = wb.getCreationHelper().createHyperlink(HyperlinkType.URL);
                link.setAddress(hyp.getUrl());
                c.setHyperlink(link);
                c.setCellValue(new HSSFRichTextString(hyp.getText()));
                c.setCellType(HSSFCell.CELL_TYPE_STRING);
            } else if (bandElement instanceof ReportBandElement)  {
                Report report = ((ReportBandElement)bandElement).getReport(); 
                ExporterBean eb = null;
                try {            	
                	eb = getSubreportExporterBean(report, true);                	
                    XlsExporter subExporter = new XlsExporter(eb, cellStyle);
                    subExporter.export();    
                    HSSFSheet subreportSheet = subExporter.getSubreportSheet();                    
                    
                    if (ReportLayout.HEADER_BAND_NAME.equals(bandName) && (gridRow == prevSubreportFirstRow)) {
                    	// other subreports on the same header line after the first
                    	sheetColumn = prevSubreportLastColumn;
                    	sheetRow -= addedPageRows;
                    	pageRow -= addedPageRows;
                    	addedPageRows = 0;
                    } else {
                    	addedPageRows = subreportSheet.getLastRowNum();
                    	pageRow += addedPageRows;
                    	// if subreport is not on the first column we merge all cells in the columns before, between the rows subreport occupies
                        if (sheetColumn > 0) {             
                        	for (int i=0; i <= sheetColumn-1; i++) {                        		
                        		CellRangeAddress cra = new CellRangeAddress(sheetRow, pageRow, i, i);		    						
                        		regions.add(new XlsRegion(cra, null));
                        	}
                        }  
                    }
                    int cols = XlsUtil.copyToSheet(xlsSheet, sheetRow, sheetColumn, subreportSheet);   
                    addRegions(xlsSheet, subExporter.getSubreportRegions(), wb);
                    if (ReportLayout.HEADER_BAND_NAME.equals(bandName)) {
                    	prevSubreportFirstRow = gridRow;
                    	prevSubreportFirstColumn = sheetColumn;
                    	prevSubreportLastColumn = sheetColumn + cols;                    	                    	
                    }
    			} catch (Exception e) {				
    				e.printStackTrace();
    			} finally {
    				if ((eb != null) && (eb.getResult() != null)) {
    					eb.getResult().close();
    				}
    			}    
        	} else if (bandElement instanceof ImageColumnBandElement){
            	try {        		
            		ImageColumnBandElement icbe = (ImageColumnBandElement)bandElement;
            		String v = StringUtil.getValueAsString(value, null);
            		if(StringUtil.BLOB.equals(v)) {
            			c.setCellType(HSSFCell.CELL_TYPE_STRING);
                        c.setCellValue(new HSSFRichTextString(StringUtil.BLOB));            			
            		} else {
    	        		byte[] imageD = StringUtil.decodeImage(v);
    	        		byte[] imageBytes = getImage(imageD, icbe.getWidth(), icbe.getHeight());
    	        		HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 0, (short) sheetColumn, sheetRow,
                                (short) (sheetColumn + colSpan), (sheetRow + rowSpan));
                        int index = wb.addPicture(imageBytes, HSSFWorkbook.PICTURE_TYPE_JPEG);

                        // image is created over the cells, so if it's height is bigger we set the row height
                        short height = xlsRow.getHeight();
                        int realImageHeight = getRealImageSize(imageBytes)[1];   
                        if (icbe.isScaled()) {
                            realImageHeight = icbe.getHeight();
                        }
                        short imageHeight = (short)(realImageHeight * POINTS_FOR_PIXEL/2.5);                        
                        if (imageHeight > height)  {
                            xlsRow.setHeight(imageHeight);
                        }

                        HSSFPicture picture = patriarch.createPicture(anchor, index);
                        picture.resize();
                        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
            		}        		
    			} catch (Exception e) {		
    				e.printStackTrace();
    				c.setCellType(HSSFCell.CELL_TYPE_STRING);
                    c.setCellValue(new HSSFRichTextString(IMAGE_NOT_LOADED));
    			}
            	
                
            } else {
            	            	            	
                if (value == null) {
                    c.setCellType(HSSFCell.CELL_TYPE_STRING);
                    c.setCellValue(new HSSFRichTextString(""));
                } else if (value instanceof Number) {
                    c.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                    c.setCellValue(((Number) value).doubleValue());
                } else {                	                	                    
                    String pattern = null;
                    if (bandElement instanceof FieldBandElement) {
                        FieldBandElement fbe = (FieldBandElement) bandElement;
                        pattern = fbe.getPattern();
                    }
                    if ((value instanceof java.sql.Date) || (value instanceof java.sql.Timestamp)) { 
                    	Date date;
                    	if (value instanceof java.sql.Date) {                    		
                    		date = new Date(((java.sql.Date)value).getTime());
                    	} else {
                    		date = (java.sql.Timestamp)value;
                    	}                    	                    	                      	                    	
                    	if (cellStyle != null) {
                    		if (pattern == null) {
                    			// use default pattern if none selected
                    			Locale locale = Locale.getDefault();
                    			pattern = ((SimpleDateFormat)DateFormat.getDateInstance(SimpleDateFormat.MEDIUM,locale)).toPattern();                    			
                    		} else {
                    			pattern = StringUtil.getI18nString(pattern, getReportLanguage());
                    		}
                    		cellStyle.setDataFormat(wb.createDataFormat().getFormat(pattern));
                    	}
                    	c.setCellValue(date);
                    } else {                    	
                    	c.setCellType(HSSFCell.CELL_TYPE_STRING);                    	
                    	String text = StringUtil.getValueAsString(value, pattern);                    	
						if ((bandElement != null) && bandElement.isWrapText()) {							
							// try to interpret new line characters
							// \\n is used here to be possible to add in designer grid cell with \n
							if (text.contains("\\n") || text.contains("\n") || text.contains("\r") || text.contains("\r\n")) {
								String crLf = Character.toString((char) 13) + Character.toString((char) 10);
								int lines = countLines(text);
								if (text.contains("\r\n")) {
									text = text.replaceAll("\r\n", crLf);
								} else {
									text = text.replaceAll("(\n)|(\r)|(\\\\n)", crLf);
								}
								c.setCellValue(text);
								cellStyle.setWrapText(true);
								xlsRow.setHeightInPoints(lines * (cellStyle.getFont(wb).getFontHeightInPoints() + 3));
							} else {
								c.setCellValue(new HSSFRichTextString(text));
							}
						} else {
							c.setCellValue(new HSSFRichTextString(text));
						}        	
                    	
                    }                    
                }
            }

            if (cellStyle != null) {
            	if (bandElement != null) {            		
            		cellStyle.setRotation(bandElement.getTextRotation());
            	}            	
            	if (!(bandElement instanceof ReportBandElement)) {
            		c.setCellStyle(cellStyle);
            	}
            }
			
			if ((rowSpan > 1) || (colSpan > 1)) {
				CellRangeAddress cra = new CellRangeAddress(sheetRow, sheetRow + rowSpan - 1, sheetColumn, sheetColumn + colSpan - 1);
				Border beBorder = bandElement.getBorder();
				if (hasRowRenderConditions(bandElement, gridRow, value)) {
					// for row render conditions we must keep the row border
					beBorder = border;
				}
				regions.add(new XlsRegion(cra, beBorder));
			}
			            
        }
    }
    
    private int countLines(String text) {
    	Matcher m = Pattern.compile("(\n)|(\r)|(\r\n)|(\\\\n)").matcher(text);
    	int lines = 1;
    	while (m.find()) {
    		lines ++;
    	}
    	return lines;
    }
    
    // http://poi.apache.org/apidocs/org/apache/poi/xssf/usermodel/extensions/XSSFHeaderFooter.html
    private void renderCellToHeaderFooter(StringBuilder result, String bandName, BandElement bandElement, Object value, int gridRow, int row,
                              int column, int cols, int rowSpan, int colSpan, boolean isImage) {
    	    	
    	if (newRow) {
    		result.append("\r\n ");
    	} else {
    		result.append(" ");
    	}
    	boolean specialCell = false;
    	if (bandElement instanceof VariableBandElement)  {
        	VariableBandElement vbe = (VariableBandElement)bandElement;
        	Variable var = VariableFactory.getVariable(vbe.getVariable());        	
        	if (var instanceof PageNoVariable) {           		
        		specialCell = true;
        		result.append("&P");
        	} 
    	} else if (bandElement instanceof ExpressionBandElement)  {
         	// special case pageNo inside an expression
         	// bandName is not important here (it is used for groupRow computation)
    		PrefixSuffix pf = interpretPageNo(bandElement);
			if (pf != null) {
				result.append(pf.getPrefix()).append(" &P ").append(pf.getSuffix());     
				specialCell = true;
			}	
    	}
		if (!specialCell) {
			result.append(value);
		}				
    }


    private BorderStyle getXlsBorderValue(int border) {
        if (border == BORDER_THIN_VALUE) {
            return BorderStyle.THIN;
        }
        if (border == BORDER_MEDIUM_VALUE) {
            return BorderStyle.MEDIUM;
        }
        if (border == BORDER_THICK_VALUE) {
            return BorderStyle.THICK;
        }
        return BorderStyle.NONE;
    }

    private void addRegions(HSSFSheet xlsSheet, List<XlsRegion> regions,  HSSFWorkbook wb ) {
        for (int r = 0, size = regions.size(); r < size; r++) {
            XlsRegion xlsRegion = regions.get(r);
            CellRangeAddress region = xlsRegion.getCellRangeAddress();
            Border border = xlsRegion.getBorder();
            xlsSheet.addMergedRegion(region);

            if (border != null) {
                BorderStyle xlsBottomBorder = getXlsBorderValue(border.getBottom());
                if (xlsBottomBorder != BorderStyle.NONE) {
                    HSSFRegionUtil.setBorderBottom(xlsBottomBorder.getCode(), region, xlsSheet, wb);
                    HSSFRegionUtil.setBottomBorderColor(ExcelColorSupport.getNearestColor(border.getBottomColor()), 
                    		region, xlsSheet, wb);
                }
                BorderStyle xlsTopBorder = getXlsBorderValue(border.getTop());
                if (xlsTopBorder != BorderStyle.NONE) {
                    HSSFRegionUtil.setBorderTop(xlsTopBorder.getCode(),region, xlsSheet, wb);
                    HSSFRegionUtil.setTopBorderColor(ExcelColorSupport.getNearestColor(border.getTopColor()), 
                    		region, xlsSheet, wb);
                }
                BorderStyle xlsLeftBorder = getXlsBorderValue(border.getLeft());
                if (xlsLeftBorder != BorderStyle.NONE) {
                    HSSFRegionUtil.setBorderLeft(xlsLeftBorder.getCode(), region, xlsSheet, wb);
                    HSSFRegionUtil.setLeftBorderColor(ExcelColorSupport.getNearestColor(border.getLeftColor()), 
                    		region, xlsSheet, wb);
                }
                BorderStyle xlsRightBorder = getXlsBorderValue(border.getRight());
                if (xlsRightBorder  != BorderStyle.NONE) {
                    HSSFRegionUtil.setBorderRight(xlsRightBorder.getCode(), region, xlsSheet, wb);
                    HSSFRegionUtil.setRightBorderColor(ExcelColorSupport.getNearestColor(border.getRightColor()), 
                    		region, xlsSheet, wb);
                }
            }

        }
        regions.clear();
    }

    private class XlsRegion {
        private CellRangeAddress cra;
        private Border border;

        private XlsRegion(CellRangeAddress cra, Border border) {
            this.cra = cra;
            this.border = border;
        }

        public CellRangeAddress getCellRangeAddress() {
            return cra;
        }

        public Border getBorder() {
            return border;
        }
    }

	protected void newPage() {
		addRegions(xlsSheet, regions, wb);
		if (hasTemplate()) {
			xlsSheet = wb.getSheetAt(bean.getReportLayout().getTemplateSheet()-1);  
		} else {
			// for a group name inside sheet name when we create a page we do not have the group name yet
			// so we will have to set the name of the previous sheet (see also finishExport where we set the name of the last sheet(s)
			String sheetName = replaceSheetNameParam(bean.getReportLayout().getSheetNames());
			if ((sheetName == null) || sheetName.isEmpty()) {
				sheetName = String.valueOf(page);
			}
			if (sheetNameContainsGroup(bean.getReportLayout().getSheetNames()) && (page>1)) {
				// current group is for previous sheet page			
				xlsSheet = wb.createSheet(String.valueOf(page));				
				wb.setSheetName(page-2, sheetName);				
			} else {
				xlsSheet = wb.createSheet(sheetName);
			}
		}
		xlsSheet.setMargin(InternalSheet.LeftMargin, getInches(bean.getReportLayout().getPagePadding().getLeft()));
		xlsSheet.setMargin(InternalSheet.RightMargin, getInches(bean.getReportLayout().getPagePadding().getRight()));
		xlsSheet.setMargin(InternalSheet.TopMargin, getInches(bean.getReportLayout().getPagePadding().getTop()));
		xlsSheet.setMargin(InternalSheet.BottomMargin, getInches(bean.getReportLayout().getPagePadding().getBottom()));		
		
		if (bean.getReportLayout().getOrientation() == LANDSCAPE) {
			xlsSheet.getPrintSetup().setLandscape(true);			
		}
		
		setPaperSize();
		
		patriarch = xlsSheet.createDrawingPatriarch();
		buildHeader();
		buildFooter();
		page++;
		// first page header is written by ResultExporter
		if (bean.getReportLayout().isHeaderOnEveryPage() && (page > 2)) {
			try {
				printHeaderBand();
			} catch (QueryException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void setPaperSize() {
		String pageFormat = bean.getReportLayout().getPageFormat();		
		short size = 0;		
		if (ReportLayout.LETTER.equals(pageFormat)) {			
			size = PrintSetup.LETTER_PAPERSIZE;
		} else if (ReportLayout.A3.equals(pageFormat)) {			
			size = PrintSetup.A3_PAPERSIZE;
		} else if (ReportLayout.A4.equals(pageFormat)) {			
			size = PrintSetup.A4_PAPERSIZE;
		} else if (ReportLayout.LEGAL.equals(pageFormat)) {			
			size = PrintSetup.LEGAL_PAPERSIZE;
		} else if (ReportLayout.LEDGER.equals(pageFormat)) {			
			size = PrintSetup.LEDGER_PAPERSIZE;
		} else if (ReportLayout.TABLOID.equals(pageFormat)) {			
			size = PrintSetup.TABLOID_PAPERSIZE;
		}
		if (size != 0) {
			xlsSheet.getPrintSetup().setPaperSize(size);
		}
	}
    
    private void buildHeader() {    	
    	if (bean.getReportLayout().getPageHeaderBand().getColumnCount() == 0) {
    		return;
    	}
    	try {    		
    		printPageHeaderBand();        
    		Header header = xlsSheet.getHeader();    		
    		header.setCenter(headerS.toString());
    	} catch (QueryException ex) {
    		ex.printStackTrace();
    	} finally {
    		headerS = new StringBuilder();
    	}
    }

    private void buildFooter() {
    	if (bean.getReportLayout().getPageFooterBand().getColumnCount() == 0) {
    		return;
    	}
    	try {
    		printPageFooterBand();  
    		Footer footer = xlsSheet.getFooter();
    		footer.setCenter(footerS.toString());
    	} catch (QueryException ex) {
    		ex.printStackTrace();
    	} finally {
    		footerS = new StringBuilder();
    	}
    }
    
    public float getInches(int pixels) {    	
    	return  (float)pixels  / getDPI() ;
    }
    
    private int getFontKey(Map<String, Object> style) {  
    	final int prime = 31;
		int hashCode = 1;
    	    	
    	if (style.containsKey(StyleFormatConstants.FONT_FAMILY_KEY)) {
    		
            String val = (String) style.get(StyleFormatConstants.FONT_FAMILY_KEY);
            hashCode = prime*hashCode + val.hashCode();
        }
        if (style.containsKey(StyleFormatConstants.FONT_SIZE)) {        	
            Float val = (Float) style.get(StyleFormatConstants.FONT_SIZE);
            hashCode = prime*hashCode + val.hashCode();
        }
        if (style.containsKey(StyleFormatConstants.FONT_COLOR)) {
            Color val = (Color) style.get(StyleFormatConstants.FONT_COLOR);
            hashCode = prime*hashCode + val.hashCode();
        }
        if (style.containsKey(StyleFormatConstants.FONT_STYLE_KEY)) {        
        	String val = (String) style.get(StyleFormatConstants.FONT_STYLE_KEY);
        	hashCode = prime*hashCode + val.hashCode();
        }
        return hashCode;
    }
    
    // all properties of same type like Color or Font must have different hashCodes!
    private int getStyleKey(Map<String, Object> style, BandElement bandElement) {  
    	final int prime = 31;
    	int hashCode = getFontKey(style);   	
    	if (style.containsKey(StyleFormatConstants.BACKGROUND_COLOR)) {    		
            Color val = (Color) style.get(StyleFormatConstants.BACKGROUND_COLOR);
            hashCode = prime*hashCode + val.hashCode() * 3;
        }
        if (style.containsKey(StyleFormatConstants.HORIZONTAL_ALIGN_KEY)) {        	
            String val = (String) style.get(StyleFormatConstants.HORIZONTAL_ALIGN_KEY);
            hashCode = prime*hashCode +  + val.hashCode();
        }
        if (style.containsKey(StyleFormatConstants.VERTICAL_ALIGN_KEY)) {        
        	String val = (String) style.get(StyleFormatConstants.VERTICAL_ALIGN_KEY);
            hashCode = prime*hashCode + val.hashCode();
        }
        if (style.containsKey(StyleFormatConstants.BORDER_LEFT)) {        	
        	Float val = (Float) style.get(StyleFormatConstants.BORDER_LEFT) * 23;
        	hashCode = prime*hashCode + val.hashCode();
        }
        if (style.containsKey(StyleFormatConstants.BORDER_RIGHT)) {        
        	Float val = (Float) style.get(StyleFormatConstants.BORDER_RIGHT) * 29;
        	hashCode = prime*hashCode + val.hashCode();
        }
        if (style.containsKey(StyleFormatConstants.BORDER_TOP)) {        	
        	Float val = (Float) style.get(StyleFormatConstants.BORDER_TOP) * 31;
        	hashCode = prime*hashCode + val.hashCode();
        }
        if (style.containsKey(StyleFormatConstants.BORDER_BOTTOM)) {        	
        	Float val = (Float) style.get(StyleFormatConstants.BORDER_BOTTOM) * 37;
        	hashCode = prime*hashCode + val.hashCode();
        }        
        if (style.containsKey(StyleFormatConstants.BORDER_LEFT_COLOR)) {        	
        	Color val = (Color) style.get(StyleFormatConstants.BORDER_LEFT_COLOR);
        	hashCode = prime*hashCode + val.hashCode() * 5;
        }
        if (style.containsKey(StyleFormatConstants.BORDER_RIGHT_COLOR)) {        	
        	Color val = (Color) style.get(StyleFormatConstants.BORDER_RIGHT_COLOR);
        	hashCode = prime*hashCode + val.hashCode() * 7;
        }
        if (style.containsKey(StyleFormatConstants.BORDER_TOP_COLOR)) {        
        	Color val = (Color) style.get(StyleFormatConstants.BORDER_TOP_COLOR);
        	hashCode = prime*hashCode +  val.hashCode() * 11;
        }
        if (style.containsKey(StyleFormatConstants.BORDER_BOTTOM_COLOR)) {        	
        	Color val = (Color) style.get(StyleFormatConstants.BORDER_BOTTOM_COLOR);
        	hashCode = prime*hashCode + val.hashCode() * 13;
        }
        if (style.containsKey(StyleFormatConstants.PATTERN)) {        	
        	String val = (String) style.get(StyleFormatConstants.PATTERN);        	
        	hashCode = prime*hashCode + val.hashCode() * 17;
        }
        
        if (bandElement != null) {
        	hashCode = prime*hashCode + bandElement.hashCode();
        }	
        return hashCode;
    }
    
    public HSSFSheet getSubreportSheet() {
    	return xlsSheet;
    }
    
    public List<XlsRegion> getSubreportRegions() {
    	return regions;
    }
    
	private InputStream getTemplateInputStream() throws IOException {
		LOG.info(">>>>>>>>> Look for : " + bean.getReportLayout().getTemplateName());
		InputStream is = getClass().getResourceAsStream("/" + bean.getReportLayout().getTemplateName());
		if (is == null) {
			LOG.error("Template '" + bean.getReportLayout().getTemplateName() + "' not found in classpath.");
			throw new IOException("Template '" + bean.getReportLayout().getTemplateName() + "' not found.");
		}
		LOG.info(">>>>>>>>> Found template: " + bean.getReportLayout().getTemplateName());
		
		return is;
	}
	
	private boolean hasTemplate() {
		return (bean.getReportLayout().getTemplateName() != null) && !"".equals(bean.getReportLayout().getTemplateName().trim());
	}
	
	private String replaceSheetNameParam(String sheetName) {	
		String actualName = sheetName;
		if (actualName == null) {
			actualName = String.valueOf(page);
		} else if (sheetName.contains("${NO}")) {			
			actualName = StringUtil.replace(sheetName, "\\$\\{NO\\}", String.valueOf(page));	     
		} else if (sheetName.contains("${G")) {
		    int startIndex = sheetName.indexOf("${");
		    int endIndex = sheetName.indexOf("}");
		    String group = sheetName.substring(startIndex+2, endIndex);		    
		    actualName = StringUtil.replace(sheetName, "\\$\\{" + group + "\\}", getCurrentValueForGroup(group));
		    if (actualName.isEmpty()) {
		    	actualName = String.valueOf(page);
		    }		    
		} else if (sheetName.contains(";")) {
			// static list of names
			String[] names = sheetName.split(";");
			if (names.length < page) {
				// too few sheet names
				actualName = String.valueOf(page);
			} else {
				actualName = names[page-1];
			}			
		} else {
			actualName = String.valueOf(page);
		}
		return actualName;				 	         
	}     
	
	private boolean sheetNameContainsGroup(String sheetName) {
		if (sheetName == null) {
			return false;
		}
		return sheetName.contains("${G");
	}

}
