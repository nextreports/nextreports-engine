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
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ro.nextreports.engine.ReleaseInfoAdapter;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.ColumnBandElement;
import ro.nextreports.engine.band.ExpressionBandElement;
import ro.nextreports.engine.band.Hyperlink;
import ro.nextreports.engine.band.HyperlinkBandElement;
import ro.nextreports.engine.band.ImageBandElement;
import ro.nextreports.engine.band.Padding;
import ro.nextreports.engine.band.PaperSize;
import ro.nextreports.engine.band.ReportBandElement;
import ro.nextreports.engine.band.VariableBandElement;
import ro.nextreports.engine.exporter.util.StyleFormatConstants;
import ro.nextreports.engine.exporter.util.variable.PageNoVariable;
import ro.nextreports.engine.exporter.util.variable.Variable;
import ro.nextreports.engine.exporter.util.variable.VariableFactory;
import ro.nextreports.engine.queryexec.QueryException;
import ro.nextreports.engine.util.PrefixSuffix;
import ro.nextreports.engine.util.StringUtil;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.FontFactoryImp;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.PageSize;
import com.lowagie.text.Image;
import com.lowagie.text.Anchor;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.field.RtfPageNumber;
import com.lowagie.text.rtf.headerfooter.RtfHeaderFooter;
import com.lowagie.text.rtf.headerfooter.RtfHeaderFooterGroup;
import com.lowagie.text.rtf.table.RtfBorder;
import com.lowagie.text.rtf.table.RtfBorderGroup;
import com.lowagie.text.rtf.table.RtfCell;

/**
 * Created by IntelliJ IDEA. User: mihai.panaitescu Date: 24-Feb-2009 Time:
 * 11:46:10
 */
public class RtfExporter extends ResultExporter {

	public RtfExporter(ExporterBean bean) {
		super(bean);
	}

	protected void initExport() throws QueryException {
		if (!bean.isSubreport()) {
			Padding margins = bean.getReportLayout().getPagePadding();
			
			Rectangle rectangle;
			if (ReportLayout.CUSTOM.equals(bean.getReportLayout().getPageFormat())) {
				PaperSize customSize = bean.getReportLayout().getPaperSize();
				rectangle = new Rectangle(customSize.getWidthPoints(), customSize.getHeightPoints());
			} else {
				rectangle = PageSize.getRectangle(getPageFormat());
			}
			
			document = new Document(rectangle, getPoints(margins.getLeft()),
					getPoints(margins.getRight()), getPoints(margins.getTop()), getPoints(margins.getBottom()));
			if (bean.getReportLayout().getOrientation() == LANDSCAPE) {
				document.setPageSize(rectangle.rotate());
			}
			RtfWriter2 writer2 = RtfWriter2.getInstance(document, getOut());
		}
		try {
			if (!bean.isSubreport()) {
				buildHeader();
				buildFooter();
				addMetaData();
				document.open();
			}
			table = buildRtfTable(PRINT_DOCUMENT);
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new QueryException(e);
		}
	}

	// Metadata methods must be called after establishing the document writer
	// and prior
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
				if (table != null) {
					document.add(table);
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
	}

	protected void flushNow() {
	}

	protected Set<CellElement> getIgnoredCells(Band band) {
		return getIgnoredCellElements(band);
	}

	protected void exportCell(String bandName, BandElement bandElement, Object value, int gridRow, int row, int column, int cols,
			int rowSpan, int colSpan, boolean isImage) {
		if (!bean.isSubreport()) {
			if (ReportLayout.PAGE_HEADER_BAND_NAME.equals(bandName)) {
				header.addCell(renderRtfCell(bandElement, value, gridRow, column, rowSpan, colSpan, isImage));
			} else if (ReportLayout.PAGE_FOOTER_BAND_NAME.equals(bandName)) {
				footer.addCell(renderRtfCell(bandElement, value, gridRow, column, rowSpan, colSpan, isImage));
			} else {
				table.addCell(renderRtfCell(bandElement, value, gridRow, column, rowSpan, colSpan, isImage));
			}
		} else {
			table.addCell(renderRtfCell(bandElement, value, gridRow, column, rowSpan, colSpan, isImage));
		}
	}

	protected void afterRowExport() {
	}

	protected String getNullElement() {
		return "";
	}

	private int fragmentsize = 15000;
	private Document document;
	private Table table;
	private Table header;
	private Table footer;

	private RtfCell renderRtfCell(BandElement bandElement, Object value, int gridRow, int gridColumn, int rowSpan, int colSpan,
			boolean image) {
		Map<String, Object> style = buildCellStyleMap(bandElement, value, gridRow, gridColumn, colSpan);
		String stringValue;

		FontFactoryImp fact = new FontFactoryImp();
		Font fnt;
		if (bandElement != null) {
			String fontName = (String) style.get(StyleFormatConstants.FONT_NAME_KEY);
			int size = ((Float) style.get(StyleFormatConstants.FONT_SIZE)).intValue();
			fnt = getFont(fontName, size);
		} else {
			fnt = getFont(10);
		}

		RtfCell cell = null;
		boolean specialCell = false;
		if (image) {
			try {
				if (value == null) {
					cell = new RtfCell(new Phrase(IMAGE_NOT_FOUND));
				} else {
					ImageBandElement ibe = (ImageBandElement) bandElement;
					byte[] imageBytes = getImage((String) value, ibe.getWidth(), ibe.getHeight());
					cell = new RtfCell(Image.getInstance(imageBytes));
				}
			} catch (Exception e) {
				cell = new RtfCell(IMAGE_NOT_LOADED);
			}
			specialCell = true;
		} else if (bandElement instanceof HyperlinkBandElement) {
			Hyperlink hyperlink = ((HyperlinkBandElement) bandElement).getHyperlink();
			Anchor anchor = new Anchor(hyperlink.getText(), fnt);
			anchor.setReference(hyperlink.getUrl());
			Phrase ph = new Phrase();
			ph.add(anchor);
			try {
				cell = new RtfCell(ph);
			} catch (BadElementException e) {
				e.printStackTrace();
				cell = new RtfCell(hyperlink.getText());
			}
			specialCell = true;
		} else if (bandElement instanceof ReportBandElement)  {
            Report report = ((ReportBandElement)bandElement).getReport();    
            ExporterBean eb = null;
            try {            	
            	eb = getSubreportExporterBean(report);
                RtfExporter subExporter = new RtfExporter(eb);
                subExporter.export();
                Table innerTable = subExporter.getTable();
                cell = new RtfCell(innerTable);               
			} catch (Exception e) {
				cell = new RtfCell();
				e.printStackTrace();
			} finally {
				if ((eb != null) && (eb.getResult() != null)) {
					eb.getResult().close();
				}
			}
            specialCell = true;
		} else if (bandElement instanceof VariableBandElement) {
			VariableBandElement vbe = (VariableBandElement) bandElement;
			Variable var = VariableFactory.getVariable(vbe.getVariable());
			if (var instanceof PageNoVariable) {
				cell = new RtfCell();
				cell.add(new RtfPageNumber());
				cell.setBorderWidth(0);
				specialCell = true;
			}
		} else if (bandElement instanceof ExpressionBandElement) {
			// special case pageNo inside an expression
			// bandName is not important here (it is used for groupRow
			// computation)
			PrefixSuffix pf = interpretPageNo(bandElement);
			if (pf != null) {
				updateFont(fnt, style);
				cell = new RtfCell();
				if (!"".equals(pf.getPrefix())) {
					cell.add(new Phrase(pf.getPrefix(), fnt));
				}
				cell.add(new RtfPageNumber(fnt));
				if (!"".equals(pf.getSuffix())) {
					cell.add(new Phrase(pf.getSuffix(), fnt));
				}
				specialCell = true;
			}
		} else if ((bandElement instanceof ColumnBandElement) && (value instanceof Blob) ){
        	try {        		
        		String v = StringUtil.getValueAsString(value, null);
        		if(StringUtil.BLOB.equals(v)) {
        			cell = new RtfCell(new Phrase(StringUtil.BLOB));
        		} else {
	        		byte[] imageBytes = StringUtil.decodeImage(v); 									
					cell = new RtfCell(Image.getInstance(imageBytes));
        		}        		
			} catch (Exception e) {						
				cell = new RtfCell(IMAGE_NOT_LOADED);
			}
        	specialCell = true;
		}
		if (!specialCell) {
			if (style.containsKey(StyleFormatConstants.PATTERN)) {
				stringValue = StringUtil.getValueAsString(value, (String) style.get(StyleFormatConstants.PATTERN));
			} else {
				stringValue = StringUtil.getValueAsString(value, null);
			}
			if (stringValue == null) {
				stringValue = "";
			}

			Phrase ph = new Phrase(stringValue, fnt);

			try {
				cell = new RtfCell(ph);
			} catch (BadElementException e) {
				e.printStackTrace();
				cell = new RtfCell(stringValue);
			}
		}

		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

		
		if (colSpan > 1) {			
			cell.setColspan(colSpan);			
		}

		if (rowSpan > 1) {			
			cell.setRowspan(rowSpan);
		}

		setCellStyle(fnt, style, cell);

		return cell;
	}

	private void updateFont(Font fnt, Map<String, Object> style) {
		if (style != null) {
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
				fnt.setColor(val);
			}
			if (style.containsKey(StyleFormatConstants.FONT_STYLE_KEY)) {
				if (StyleFormatConstants.FONT_STYLE_NORMAL.equals(style.get(StyleFormatConstants.FONT_STYLE_KEY))) {
					fnt.setStyle(com.lowagie.text.Font.NORMAL);
				}
				if (StyleFormatConstants.FONT_STYLE_BOLD.equals(style.get(StyleFormatConstants.FONT_STYLE_KEY))) {
					fnt.setStyle(com.lowagie.text.Font.BOLD);
				}
				if (StyleFormatConstants.FONT_STYLE_ITALIC.equals(style.get(StyleFormatConstants.FONT_STYLE_KEY))) {
					fnt.setStyle(com.lowagie.text.Font.ITALIC);
				}
				if (StyleFormatConstants.FONT_STYLE_BOLDITALIC.equals(style.get(StyleFormatConstants.FONT_STYLE_KEY))) {
					fnt.setStyle(com.lowagie.text.Font.BOLDITALIC);
				}
			}
		}
	}

	private void setCellStyle(Font fnt, Map<String, Object> style, RtfCell cell) {
		if (style != null) {
			updateFont(fnt, style);
			if (style.containsKey(StyleFormatConstants.BACKGROUND_COLOR)) {
				Color val = (Color) style.get(StyleFormatConstants.BACKGROUND_COLOR);
				cell.setBackgroundColor(val);
			}
			if (style.containsKey(StyleFormatConstants.HORIZONTAL_ALIGN_KEY)) {
				if (StyleFormatConstants.HORIZONTAL_ALIGN_LEFT.equals(style.get(StyleFormatConstants.HORIZONTAL_ALIGN_KEY))) {
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				}
				if (StyleFormatConstants.HORIZONTAL_ALIGN_RIGHT.equals(style.get(StyleFormatConstants.HORIZONTAL_ALIGN_KEY))) {
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				}
				if (StyleFormatConstants.HORIZONTAL_ALIGN_CENTER.equals(style.get(StyleFormatConstants.HORIZONTAL_ALIGN_KEY))) {
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
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
			// if (style.containsKey(StyleFormatConstants.PADDING_LEFT)) {
			// Float val = (Float) style.get(StyleFormatConstants.PADDING_LEFT);
			// cell.setPaddingLeft(val);
			// }
			// if (style.containsKey(StyleFormatConstants.PADDING_RIGHT)) {
			// Float val = (Float)
			// style.get(StyleFormatConstants.PADDING_RIGHT);
			// cell.setPaddingRight(val);
			// }
			// if (style.containsKey(StyleFormatConstants.PADDING_TOP)) {
			// Float val = (Float) style.get(StyleFormatConstants.PADDING_TOP);
			// cell.setPaddingTop(val);
			// }
			// if (style.containsKey(StyleFormatConstants.PADDING_BOTTOM)) {
			// Float val = (Float)
			// style.get(StyleFormatConstants.PADDING_BOTTOM);
			// cell.setPaddingBottom(val);
			// }
			cell.setBorderWidth(0);

			Float val = Float.valueOf(1);
			RtfBorderGroup bg = new RtfBorderGroup();
			if (style.containsKey(StyleFormatConstants.BORDER_LEFT)) {
				val = (Float) style.get(StyleFormatConstants.BORDER_LEFT);
				Color color = (Color) style.get(StyleFormatConstants.BORDER_LEFT_COLOR);
				bg.addBorder(Rectangle.LEFT, RtfBorder.BORDER_SINGLE, val, color);
			}
			if (style.containsKey(StyleFormatConstants.BORDER_RIGHT)) {
				val = (Float) style.get(StyleFormatConstants.BORDER_RIGHT);
				Color color = (Color) style.get(StyleFormatConstants.BORDER_RIGHT_COLOR);
				bg.addBorder(Rectangle.RIGHT, RtfBorder.BORDER_SINGLE, val, color);
			}
			if (style.containsKey(StyleFormatConstants.BORDER_TOP)) {
				val = (Float) style.get(StyleFormatConstants.BORDER_TOP);
				Color color = (Color) style.get(StyleFormatConstants.BORDER_TOP_COLOR);
				bg.addBorder(Rectangle.TOP, RtfBorder.BORDER_SINGLE, val, color);
			}
			if (style.containsKey(StyleFormatConstants.BORDER_BOTTOM)) {
				val = (Float) style.get(StyleFormatConstants.BORDER_BOTTOM);
				Color color = (Color) style.get(StyleFormatConstants.BORDER_BOTTOM_COLOR);
				bg.addBorder(Rectangle.BOTTOM, RtfBorder.BORDER_SINGLE, val, color);
			}
			cell.setBorders(bg);

			// cell.setNoWrap(true);
			// if (bandElement != null) {
			// if (bandElement.isWrapText()) {
			// cell.setNoWrap(false);
			// }
			// }
		}
	}

	private Font getFont(String fontName, int size) {
		return FontFactory.getFont(fontName, size);
	}

	private Font getFont(int size) {
		return FontFactory.getFont(FontFactory.TIMES, size);
	}

	// type : PRINT_DOCUMENT, PRINT_PAGE_HEADER, PRINT_PAGE_FOOTER
	private Table buildRtfTable(int type) throws DocumentException {
		List<Band> bands = new ArrayList<Band>();
		if (type == 1) {
			bands.add(getReportLayout().getPageHeaderBand());
		} else if (type == 2) {
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
		Table datatable = new Table(totalColumns);
		int[] headerwidths = new int[totalColumns]; // %
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

		if (bean.getReportLayout().isUseSize()) {
			float pixels = A4_PORTRAIT_PIXELS;
			if (bean.getReportLayout().getOrientation() == LANDSCAPE) {
				pixels = A4_LANDSCAPE_PIXELS;
			}
			float percentage = totalWidth * 100 / pixels;
			// do not allow to go outside an A4 frame
			if (percentage > 100) {
				percentage = 100;
			}
			if (!ReportLayout.CUSTOM.equals(bean.getReportLayout().getPageFormat())) {
				datatable.setWidth(percentage);
			}
			datatable.setWidths(headerwidths);
		} else {
			datatable.setWidth(100);
		}

		datatable.setPadding(2);
		if (type == PRINT_DOCUMENT) {
			writeHeader(datatable);
		}
		return datatable;
	}

	private void writeHeader(Table datatable) {
		if (bean.getReportLayout().isHeaderOnEveryPage()) {
			int headerNo = bean.getReportLayout().getHeaderBand().getRowCount();
			if (headerNo > 0) {
				datatable.setLastHeaderRow(headerNo);
			}
		}
	}

	private void buildHeader() throws DocumentException, QueryException {
		header = buildRtfTable(PRINT_PAGE_HEADER);
		if (header == null) {
			return;
		}
		printPageHeaderBand();
		HeaderFooter hf = new RtfHeaderFooter(header);
		document.setHeader(hf);
	}

	private void buildFooter() throws DocumentException, QueryException {
		footer = buildRtfTable(PRINT_PAGE_FOOTER);
		if (footer == null) {
			return;
		}
		printPageFooterBand();
		RtfHeaderFooterGroup footerGroup = new RtfHeaderFooterGroup();
		RtfHeaderFooter hf = new RtfHeaderFooter(footer);
		footerGroup.setHeaderFooter(hf, RtfHeaderFooter.DISPLAY_ALL_PAGES);
		document.setFooter(footerGroup);
	}

	protected void newPage() {
		if (!bean.isSubreport()) {
			try {
				document.add(table);
				document.newPage();
				table.deleteAllRows();
				if (bean.getReportLayout().isHeaderOnEveryPage()) {
					try {
						printHeaderBand();
					} catch (QueryException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String getPageFormat() {
		String format = bean.getReportLayout().getPageFormat();
		// not supported by RTF
		if (ReportLayout.A0.equals(format) || ReportLayout.A1.equals(format)) {
			format = ReportLayout.A2;
		}
		return format;
	}
	
	public Table getTable() {
    	return table;
    }

}
