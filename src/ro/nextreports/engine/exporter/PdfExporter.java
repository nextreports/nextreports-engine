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

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Image;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.ArabicLigaturizer;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.awt.*;
import java.io.IOException;
import java.io.StringReader;

import ro.nextreports.engine.ReleaseInfoAdapter;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.Hyperlink;
import ro.nextreports.engine.band.HyperlinkBandElement;
import ro.nextreports.engine.band.ImageBandElement;
import ro.nextreports.engine.band.ImageColumnBandElement;
import ro.nextreports.engine.band.Padding;
import ro.nextreports.engine.band.PaperSize;
import ro.nextreports.engine.band.ReportBandElement;
import ro.nextreports.engine.band.VariableBandElement;
import ro.nextreports.engine.exporter.util.StyleFormatConstants;
import ro.nextreports.engine.exporter.util.variable.TotalPageNoVariable;
import ro.nextreports.engine.exporter.util.variable.VariableFactory;
import ro.nextreports.engine.queryexec.QueryException;
import ro.nextreports.engine.util.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Dec 5, 2008
 * Time: 11:19:52 AM
 */
public class PdfExporter extends ResultExporter {      
	
	// total page no 
	// Templates are only written to the output when the document is closed permitting things like showing text in the first page
    // that is only defined in the last page
	private PdfTemplate total;	

    public PdfExporter(ExporterBean bean) {
        super(bean);
    }

    protected void initExport() throws QueryException {    	
    	Padding margins = bean.getReportLayout().getPagePadding();   
    	    	    	
		if (!bean.isSubreport()) {
			float footerHeight = computeFooterHeight();
			
			Rectangle rectangle;
			if (ReportLayout.CUSTOM.equals(bean.getReportLayout().getPageFormat())) {
				PaperSize customSize = bean.getReportLayout().getPaperSize();
				rectangle = new Rectangle(customSize.getWidthPoints(), customSize.getHeightPoints());
			} else {
				rectangle = PageSize.getRectangle(bean.getReportLayout().getPageFormat());
			}
			
			document = new Document(rectangle, getPoints(margins.getLeft()),
					getPoints(margins.getRight()), getPoints(margins.getTop()), getPoints(margins.getBottom()) + footerHeight);
			if (bean.getReportLayout().getOrientation() == LANDSCAPE) {
				document.setPageSize(rectangle.rotate());
			}
		}
        PdfWriter wr = null;
        try {
			if (!bean.isSubreport()) {
				wr = PdfWriter.getInstance(document, getOut());
				wr.setPageEvent(new PdfPageEvent());
				addMetaData();
				document.open();
			}
            table = buildPdfTable(PRINT_DOCUMENT);       
            
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new QueryException(e);
        }
    }

    // Metadata methods must be called after establishing the document writer and prior
    // to opening the document
    private void addMetaData() {
        document.addTitle(getDocumentTitle());
        document.addAuthor(ReleaseInfoAdapter.getCompany());
        document.addCreator("NextReports " + ReleaseInfoAdapter.getVersionNumber());
        document.addSubject("Created by NextReports Designer" + ReleaseInfoAdapter.getVersionNumber());
        document.addCreationDate();
        document.addKeywords(ReleaseInfoAdapter.getHome());
    }

	protected void finishExport() {
		if (!bean.isSubreport()) {
			try {
				// if nothing was added to report we must add an empty paragraph
				// otheriwse a "This document has no pages" error will be given
				// on document.close()
				if (table != null) {
					document.add(table);
				} else {
					document.add(new Paragraph(" "));
				}

			} catch (DocumentException e) {
				e.printStackTrace();
			} finally {
				table = null;
				document.close();
			}
		}
	}

    protected void close() {
    	if (!bean.isSubreport()) {
    		document.close();
    	}
    }

    protected void flush() {
		if (!bean.isSubreport()) {
			if (resultSetRow % fragmentsize == fragmentsize - 1) {
				flushNow();
			}
		}
    }

    protected void flushNow() {
		if (!bean.isSubreport()) {
			try {
				document.add(table);
				table.deleteBodyRows();
				table.setSkipFirstHeader(true);
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}
    }

    protected Set<CellElement> getIgnoredCells(Band band) {        
        return getIgnoredCellElements(band);
    }

    protected void exportCell(String bandName, BandElement bandElement, Object value, int gridRow, int row, int column,
                              int cols, int rowSpan, int colSpan, boolean isImage) {
		if (!bean.isSubreport()) {
			if (ReportLayout.PAGE_HEADER_BAND_NAME.equals(bandName)) {
				header.addCell(renderPdfCell(bandElement, value, gridRow, rowSpan, colSpan, isImage, column));
			} else if (ReportLayout.PAGE_FOOTER_BAND_NAME.equals(bandName)) {
				footer.addCell(renderPdfCell(bandElement, value, gridRow, rowSpan, colSpan, isImage, column));
			} else {
				table.addCell(renderPdfCell(bandElement, value, gridRow, rowSpan, colSpan, isImage, column));
			}
		} else {
			table.addCell(renderPdfCell(bandElement, value, gridRow, rowSpan, colSpan, isImage, column));
		}
    }       

    protected void afterRowExport() {
    }

    protected String getNullElement() {
        return "";
    }

    public static final String PDF_ENCODING_PROPERTY = "nextreports.pdf.encoding";
    public static final String PDF_FONT_PROPERTY = "nextreports.pdf.font";
    public static final String PDF_DIRECTION = "nextreports.pdf.direction";
    public static final String PDF_ARABIC_OPTIONS = "nextreports.pdf.arabicOptions";        

    // ANSI Encoding of the pdf document
    // http://www.microsoft.com/globaldev/reference/WinCP.mspx
    private final String encoding = System.getProperty(PDF_ENCODING_PROPERTY);

    // Full path to an embedded tiff font (The outlines of the glyphs needed inside the text
    // will be embedded in the pdf document) . Without such a font the glyphs for some
    // alphabets (like greek, cyrillic, ..) will be overlapped one over the other in the document
    // creating a not-readable string.
    // This is used only if an encoding is present.
    private final String embeddedFont = System.getProperty(PDF_FONT_PROPERTY);
    
    // other writing properties 
    // PdfWriter.RUN_DIRECTION_DEFAULT, PdfWriter.RUN_DIRECTION_NO_BIDI, PdfWriter.RUN_DIRECTION_LTR, PdfWriter.RUN_DIRECTION_RTL
    private final int textDirection = Integer.getInteger(PDF_DIRECTION, PdfWriter.RUN_DIRECTION_DEFAULT);
    // ar_nothing, ar_novowel, ar_composedtashkeel, ar_lig
    private final int arabicOptions = Integer.getInteger(PDF_ARABIC_OPTIONS, ArabicLigaturizer.ar_nothing);

    // Pdf font name if no encoding or if no embedded font is used
    private String fontName = FontFactory.TIMES;

    private int fragmentsize = 15000;
    private Document document;
    private PdfPTable table;
    private PdfPTable header;
    private PdfPTable footer;    
    private float percentage = 100;
    private int[] headerwidths;    
    
    private static final int MINIMUM_HEIGHT = 12;

    private PdfPCell renderPdfCell(BandElement bandElement, Object value, int gridRow, int rowSpan, int colSpan, boolean image, int column) {
        Map<String, Object> style = buildCellStyleMap(bandElement, value, gridRow, column, colSpan);               

        FontFactoryImp fact = new FontFactoryImp();
        com.itextpdf.text.Font fnt;
        if (bandElement != null) {
            fontName = (String)style.get(StyleFormatConstants.FONT_NAME_KEY);
            int size = ((Float)style.get(StyleFormatConstants.FONT_SIZE )).intValue();            
            fnt = getFont(size);
        } else {
            fnt = getFont(10);
        }
        
        PdfPCell cell;
        if (image) {
            if (value == null) {
                cell = new PdfPCell(new Phrase(IMAGE_NOT_FOUND));
            } else {
                ImageBandElement ibe = (ImageBandElement)bandElement;               
                try {
                    byte[] imageBytes = getImage((String) value);
                    cell = getImageCell(ibe, imageBytes, column, colSpan);
                } catch (Exception e) {
                    cell = new PdfPCell(new Phrase(IMAGE_NOT_LOADED));
                }
            }
        } else if (bandElement instanceof HyperlinkBandElement)  {
            Hyperlink hyperlink = ((HyperlinkBandElement)bandElement).getHyperlink();
            Anchor anchor = new Anchor(hyperlink.getText(), fnt);
            anchor.setReference(hyperlink.getUrl());
            Phrase ph = new Phrase();
            ph.add(anchor);
            cell = new PdfPCell(ph);
        } else if (bandElement instanceof ReportBandElement)  {
            Report report = ((ReportBandElement)bandElement).getReport();  
            ExporterBean eb = null;
            try {            	
            	eb = getSubreportExporterBean(report);
                PdfExporter subExporter = new PdfExporter(eb);
                subExporter.export();
                PdfPTable innerTable = subExporter.getTable();               
                cell = new PdfPCell(innerTable);	                
			} catch (Exception e) {
				cell = new PdfPCell();
				e.printStackTrace();
			} finally {
				if ((eb != null) && (eb.getResult() != null)) {
					eb.getResult().close();
				}
			}
        } else if ( (bandElement instanceof VariableBandElement) && 
        		    (VariableFactory.getVariable(((VariableBandElement) bandElement).getVariable()) instanceof TotalPageNoVariable) )  {
        	try {
				cell = new PdfPCell(Image.getInstance(total));
			} catch (BadElementException e) {
				cell = new PdfPCell(new Phrase("NA"));
			}   
        	
        } else if (bandElement instanceof ImageColumnBandElement) {
        	try {        		
        		String v = StringUtil.getValueAsString(value, null);
        		if(StringUtil.BLOB.equals(v)) {
        			cell = new PdfPCell(new Phrase(StringUtil.BLOB));
        		} else {        			
	        		byte[] bytes = StringUtil.decodeImage(v);
	        		cell = getImageCell(bandElement, bytes, column, colSpan);
        		}        		
			} catch (Exception e) {		
				e.printStackTrace();
				cell = new PdfPCell(new Phrase(IMAGE_NOT_LOADED));
			}
        } else {
            String stringValue;
            if (style.containsKey(StyleFormatConstants.PATTERN)) {
                stringValue = StringUtil.getValueAsString(value, (String) style.get(StyleFormatConstants.PATTERN), getReportLanguage());
            } else {
                stringValue = StringUtil.getValueAsString(value, null, getReportLanguage());
            }
            if (stringValue == null) {
                stringValue = "";
            }
            if (stringValue.startsWith("<html>")) {
            	StringReader reader = new StringReader(stringValue);             	
            	List<Element> elems = new ArrayList<Element>();
				try {
					elems = HTMLWorker.parseToList(reader, new StyleSheet());
					Phrase ph = new Phrase();
	            	for (int i = 0; i < elems.size(); i++){
	            		Element elem = (Element)elems.get(i);
	            		ph.add(elem);
	            	}
	            	cell = new PdfPCell(ph); 
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Phrase ph = new Phrase(stringValue, fnt);
			        cell = new PdfPCell(ph);
				}
            	                     
            	
            } else {
	            Phrase ph = new Phrase(stringValue, fnt);
	            cell = new PdfPCell(ph);
            }
        }

        cell.setArabicOptions(arabicOptions);
        cell.setRunDirection(textDirection);
        
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setUseDescender(true); // needed for a cell without padding
        cell.setMinimumHeight(MINIMUM_HEIGHT);  // needed if there is a row in which all cells are empty

        if (bandElement != null) {
        	cell.setRotation(bandElement.getTextRotation());
        }
        
        if (colSpan > 1) {
            cell.setColspan(colSpan);
        }

        if (rowSpan > 1) {
            cell.setRowspan(rowSpan);
        }

        if (style != null) {
        	
        	updateFont(style, fnt);
        	
            if (style.containsKey(StyleFormatConstants.BACKGROUND_COLOR)) {
                Color val = (Color) style.get(StyleFormatConstants.BACKGROUND_COLOR);
                cell.setBackgroundColor(new BaseColor(val));
            }
            if (style.containsKey(StyleFormatConstants.HORIZONTAL_ALIGN_KEY)) {
                if (StyleFormatConstants.HORIZONTAL_ALIGN_LEFT.equals(style.get(StyleFormatConstants.HORIZONTAL_ALIGN_KEY))) {
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                }
                if (StyleFormatConstants.HORIZONTAL_ALIGN_RIGHT.equals(style.get(StyleFormatConstants.HORIZONTAL_ALIGN_KEY))) {
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                }
                if (StyleFormatConstants.HORIZONTAL_ALIGN_CENTER.equals(style.get(StyleFormatConstants.HORIZONTAL_ALIGN_KEY))) {
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                }
            }

            if (style.containsKey(StyleFormatConstants.VERTICAL_ALIGN_KEY)) {
                if (StyleFormatConstants.VERTICAL_ALIGN_TOP.equals(style.get(StyleFormatConstants.VERTICAL_ALIGN_KEY))) {
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                }
                if (StyleFormatConstants.VERTICAL_ALIGN_MIDDLE.equals(style.get(StyleFormatConstants.VERTICAL_ALIGN_KEY))) {
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                }
                if (StyleFormatConstants.VERTICAL_ALIGN_BOTTOM.equals(style.get(StyleFormatConstants.VERTICAL_ALIGN_KEY))) {
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                }
            }

            if (style.containsKey(StyleFormatConstants.PADDING_LEFT)) {
                Float val = (Float) style.get(StyleFormatConstants.PADDING_LEFT);
                cell.setPaddingLeft(val);
            }
            if (style.containsKey(StyleFormatConstants.PADDING_RIGHT)) {
                Float val = (Float) style.get(StyleFormatConstants.PADDING_RIGHT);
                cell.setPaddingRight(val);
            }
            if (style.containsKey(StyleFormatConstants.PADDING_TOP)) {
                Float val = (Float) style.get(StyleFormatConstants.PADDING_TOP);
                cell.setPaddingTop(val);
            }
            if (style.containsKey(StyleFormatConstants.PADDING_BOTTOM)) {
                Float val = (Float) style.get(StyleFormatConstants.PADDING_BOTTOM);
                cell.setPaddingBottom(val);
            }
            cell.setBorderWidth(0);

            if (style.containsKey(StyleFormatConstants.BORDER_LEFT)) {
                Float val = (Float) style.get(StyleFormatConstants.BORDER_LEFT);
                cell.setBorderWidthLeft(val / 2);
                Color color = (Color) style.get(StyleFormatConstants.BORDER_LEFT_COLOR);
                cell.setBorderColorLeft(new BaseColor(color));
            }
            if (style.containsKey(StyleFormatConstants.BORDER_RIGHT)) {
                Float val = (Float) style.get(StyleFormatConstants.BORDER_RIGHT);
                cell.setBorderWidthRight(val / 2);
                Color color = (Color) style.get(StyleFormatConstants.BORDER_RIGHT_COLOR);
                cell.setBorderColorRight(new BaseColor(color));
            }
            if (style.containsKey(StyleFormatConstants.BORDER_TOP)) {
                Float val = (Float) style.get(StyleFormatConstants.BORDER_TOP);
                cell.setBorderWidthTop(val / 2);
                Color color = (Color) style.get(StyleFormatConstants.BORDER_TOP_COLOR);
                cell.setBorderColorTop(new BaseColor(color));
            }
            if (style.containsKey(StyleFormatConstants.BORDER_BOTTOM)) {
                Float val = (Float) style.get(StyleFormatConstants.BORDER_BOTTOM);
                cell.setBorderWidthBottom(val / 2);
                Color color = (Color) style.get(StyleFormatConstants.BORDER_BOTTOM_COLOR);
                cell.setBorderColorBottom(new BaseColor(color));
            }

            // for subreports we use default no wrap
			if (cell.getTable() == null) {
				cell.setNoWrap(true);
				if (bandElement != null) {
					if (bandElement.isWrapText()) {
						cell.setNoWrap(false);
					}
				}
			}
			
			// to see a background image all cells must not have any background!
			if (bean.getReportLayout().getBackgroundImage() != null) {
				cell.setBackgroundColor(null);
			}
        }
        return cell;
    }
    
    private void updateFont(Map<String, Object> style, Font fnt) {
    	if (style.containsKey(StyleFormatConstants.FONT_FAMILY_KEY)) {
            String val = (String) style.get(StyleFormatConstants.FONT_FAMILY_KEY);
            fnt.setFamily(val);
        }
        if (style.containsKey(StyleFormatConstants.FONT_SIZE)) {
            Float val = (Float) style.get(StyleFormatConstants.FONT_SIZE);
            fnt.setSize(val);
        }
        if (style.containsKey(StyleFormatConstants.FONT_COLOR)) {
            Color val = (Color) style.get(StyleFormatConstants.FONT_COLOR);
            fnt.setColor(new BaseColor(val));
        }
        if (style.containsKey(StyleFormatConstants.FONT_STYLE_KEY)) {
            if (StyleFormatConstants.FONT_STYLE_NORMAL.equals(style.get(StyleFormatConstants.FONT_STYLE_KEY))) {
                fnt.setStyle(Font.NORMAL);
            }
            if (StyleFormatConstants.FONT_STYLE_BOLD.equals(style.get(StyleFormatConstants.FONT_STYLE_KEY))) {
                fnt.setStyle(Font.BOLD);
            }
            if (StyleFormatConstants.FONT_STYLE_ITALIC.equals(style.get(StyleFormatConstants.FONT_STYLE_KEY))) {
                fnt.setStyle(Font.ITALIC);
            }
            if (StyleFormatConstants.FONT_STYLE_BOLDITALIC.equals(style.get(StyleFormatConstants.FONT_STYLE_KEY))) {
                fnt.setStyle(Font.BOLDITALIC);
            }
        }
    }

    private Font getFont(int size) {        
        if (encoding == null) {
            return FontFactory.getFont(fontName, size);
        } else {
            if (embeddedFont == null) {
                return FontFactory.getFont(fontName, encoding, size);
            } else {
                return FontFactory.getFont(embeddedFont, encoding, true, size, Font.NORMAL);
            }
        }
    }

    private class PdfPageEvent extends PdfPageEventHelper {    	    	
                
        @Override
        public void onStartPage(PdfWriter writer, Document document) {
        	try {
        		header = buildPdfTable(PRINT_PAGE_HEADER);    
        		if (header != null) {
        			printPageHeaderBand();
        			document.add(header);
        		}	
        	} catch (Exception e) {
              throw new ExceptionConverter(e);
        	}	
        }      
        
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
            	footer = buildPdfTable(PRINT_PAGE_FOOTER);   
            	if (footer != null) {
            		printPageFooterBand();
            		Rectangle page = document.getPageSize();        		            		            		
            		footer.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());            		
            		footer.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin(), writer.getDirectContent());
            		
            		String image = bean.getReportLayout().getBackgroundImage();
					if (image != null) {						
						byte[] imageBytes = getImage(image);
						Image pdfImage = Image.getInstance(imageBytes);
						pdfImage.setAbsolutePosition(0, 0);
						writer.getDirectContentUnder().addImage(pdfImage);
					}
            	}
            } catch (Exception e) {
                throw new ExceptionConverter(e);
            } finally {
            	pageNo = writer.getPageNumber();
            }
        }
        
        /**
         * Creates the PdfTemplate that will hold the total number of pages.        
         */
        public void onOpenDocument(PdfWriter writer, Document document) {
        	int size = 10;
        	if (totalPageNoVbe != null) {
        		Map<String, Object> style = buildCellStyleMap(totalPageNoVbe);
        		size = ((Float) style.get(StyleFormatConstants.FONT_SIZE)).intValue();        		
        	}        	
            total = writer.getDirectContent().createTemplate(30, size);
        }
        
        /**
         * Fills out the total number of pages before the document is closed.        
         */
		public void onCloseDocument(PdfWriter writer, Document document) {
			totalPageNo = writer.getPageNumber() - 1;
			if (totalPageNoVbe != null) {
				Map<String, Object> style = buildCellStyleMap(totalPageNoVbe);
				
				fontName = (String) style.get(StyleFormatConstants.FONT_NAME_KEY);
				int size = ((Float) style.get(StyleFormatConstants.FONT_SIZE)).intValue();
				Font fnt = getFont(size);
				updateFont(style, fnt);
								
				ColumnText.showTextAligned(total, Element.ALIGN_LEFT, new Phrase(String.valueOf(totalPageNo), fnt), 0, 1, 0);
			} 			
		}
	}

    // type : PRINT_DOCUMENT, PRINT_PAGE_HEADER, PRINT_PAGE_FOOTER
    private PdfPTable buildPdfTable(int type) throws QueryException {

        List<Band> bands = new ArrayList<Band>();
        if (type == PRINT_PAGE_HEADER) {        	        	
        	bands.add(getReportLayout().getPageHeaderBand());
        } else if (type == PRINT_PAGE_FOOTER) {
        	bands.add(getReportLayout().getPageFooterBand());        
        } else {
        	bands = getReportLayout().getDocumentBands();
        }
        int totalRows = 0;
        int totalColumns = 0;
        for (Band band : bands) {
            totalRows += band.getRowCount();
            int cols = band.getColumnCount();
            if (cols > totalColumns) {
                totalColumns = cols;
            }
        }

        // no page header or no page footer
        if (totalColumns == 0) {
        	return null;
        }
        PdfPTable datatable = new PdfPTable(totalColumns);
                
        // !Important: when a cell is bigger than a page it won't show
        // we could use datatable.setSplitLate(false)and in this case we can see
        // some content but it is very ugly.
        
        headerwidths = new int[totalColumns]; // %

        int size = 100 / totalColumns;
        int totalWidth = 0;
        for (int i = 0; i < totalColumns; i++) {
            if (bean.getReportLayout().isUseSize()) {
                headerwidths[i] = bean.getReportLayout().getColumnsWidth().get(i);
            } else {
                headerwidths[i] = size;
            }
            totalWidth += headerwidths[i];
        }
        try {            
            if (bean.getReportLayout().isUseSize()) {
                float pixels = A4_PORTRAIT_PIXELS;
                if (bean.getReportLayout().getOrientation() == LANDSCAPE) {
                    pixels = A4_LANDSCAPE_PIXELS;
                }
                percentage = totalWidth * 100 / pixels;
                // do not allow to go outside an A4 frame
                if (percentage > 100) {
                    percentage = 100;
                }
                if (!ReportLayout.CUSTOM.equals(bean.getReportLayout().getPageFormat())) {
                	datatable.setWidthPercentage(percentage);
                }
                datatable.setWidths(headerwidths);
            } else {
                datatable.setWidthPercentage(100);
            }            
        } catch (DocumentException e) {
            throw new QueryException(e);
        }
        if (type == PRINT_DOCUMENT) {
        	writeHeader(datatable);
        }
        return datatable;
    }

    private void writeHeader(PdfPTable datatable) {
        if (bean.getReportLayout().isHeaderOnEveryPage()) {
            int headerNo = bean.getReportLayout().getHeaderBand().getRowCount();
            if (headerNo > 0) {
                datatable.setHeaderRows(headerNo);
            }
        }
    }

	protected void newPage() {
		if (!bean.isSubreport()) {
			try {
				document.add(table);
				document.newPage();
				table.deleteBodyRows();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
    
    private float computeFooterHeight() throws QueryException {
    	footer = buildPdfTable(PRINT_PAGE_FOOTER);
    	if (footer == null) {
    		return 0;
    	}
    	printPageFooterBand();
    	
    	float height = 0;
    	ArrayList<PdfPRow> rows = footer.getRows();
    	for (int k = 0; k < rows.size(); k++) {
    		PdfPRow row = rows.get(k);
    		PdfPCell[] cells = row.getCells();
    		float rowHeight = 0;
    		for (PdfPCell cell : cells) {
    			if (cell == null) {
    				rowHeight = Math.max(rowHeight, MINIMUM_HEIGHT);
    			} else {
    				rowHeight = Math.max(rowHeight, cell.getMaxHeight());
    			}	
    		}
        	height += rowHeight;
        }
    	return height;
    }
    
    public PdfPTable getTable() {
    	return table;
    }
    
	private PdfPCell getImageCell(BandElement bandElement, byte[] imageBytes, int column, int colSpan) {
		PdfPCell cell;
		try {			
			Image pdfImage = Image.getInstance(imageBytes);
			Integer width = getImageWidth(bandElement);
			Integer height = getImageHeight(bandElement);
			if (isScaled(bandElement)) {

				// scale image (is done in postscript points)
				// if use size
				// if width is less than the cell width (with span)
				// the image is added with its size :
				// new PdfPCell(pdfImage) -> no further scale
				// if width is greater than cell width (with span)
				// the image will fit the cell :
				// cell.setImage(pdfImage); -> does a scale to fit cell
				// if do not use size
				// the image will be added with its size

				float factor = 72f * percentage / (getDPI() * 100);
				pdfImage.scaleAbsolute(width * factor, height * factor);

				int cellWidth = 0;
				for (int i = column; i < column + colSpan; i++) {
					cellWidth += headerwidths[i];
				}
				if ((width.intValue() >= cellWidth) && bean.getReportLayout().isUseSize()) {
					cell = new PdfPCell();
					cell.setImage(pdfImage); // scale to fit cell
				} else {
					cell = new PdfPCell(pdfImage); // do not scale
				}
			} else {
				cell = new PdfPCell(pdfImage);
			}

		} catch (Exception e) {
			e.printStackTrace();
			cell = new PdfPCell(new Phrase(IMAGE_NOT_LOADED));
		}

		return cell;
	}
    
    private int getImageWidth(BandElement be) {    	
    	Integer width = null;
    	if (be instanceof ImageBandElement) {
    		width = ((ImageBandElement)be).getWidth();
    	} else {
    		width = ((ImageColumnBandElement)be).getWidth();
    	}
    	if (width == null) {
    		width = 0;
    	}
    	return width;
    }
    
    private int getImageHeight(BandElement be) {
    	Integer height = null;
    	if (be instanceof ImageBandElement) {
    		height =  ((ImageBandElement)be).getHeight();
    	} else {
    		height = ((ImageColumnBandElement)be).getHeight();
    	}
    	if (height == null) {
    		height = 0;
    	}
    	return height;
    }
    
    private boolean isScaled(BandElement be) {
    	if (be instanceof ImageBandElement) {
    		return ((ImageBandElement)be).isScaled();
    	} else {
    		return ((ImageColumnBandElement)be).isScaled();
    	}
    }

}
