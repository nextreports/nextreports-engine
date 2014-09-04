package ro.nextreports.engine.exporter;

import java.awt.Color;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.docProps.core.CoreProperties;
import org.docx4j.docProps.core.dc.elements.SimpleLiteral;
import org.docx4j.jaxb.Context;
import org.docx4j.model.structure.PageDimensions;
import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.DocPropsCorePart;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.WordprocessingML.AltChunkType;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.CTBackground;
import org.docx4j.vml.CTFill;
import org.docx4j.wml.Body;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.Br;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.CTShd;
import org.docx4j.wml.CTSimpleField;
import org.docx4j.wml.CTVerticalJc;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.FooterReference;
import org.docx4j.wml.Ftr;
import org.docx4j.wml.Hdr;
import org.docx4j.wml.HdrFtrRef;
import org.docx4j.wml.HeaderReference;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase;
import org.docx4j.wml.RStyle;
import org.docx4j.wml.PPrBase.Spacing;
import org.docx4j.wml.R;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.STVerticalJc;
import org.docx4j.wml.SectPr;
import org.docx4j.wml.SectPr.PgMar;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.TblWidth;
import org.docx4j.wml.Tc;
import org.docx4j.wml.TcMar;
import org.docx4j.wml.TcPr;
import org.docx4j.wml.TcPrInner.GridSpan;
import org.docx4j.wml.TcPrInner.TcBorders;
import org.docx4j.wml.TcPrInner.VMerge;
import org.docx4j.wml.Text;
import org.docx4j.wml.TextDirection;
import org.docx4j.wml.Tr;
import org.docx4j.wml.TrPr;
import org.docx4j.wml.U;
import org.docx4j.wml.UnderlineEnumeration;

import ro.nextreports.engine.ReleaseInfoAdapter;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.ExpressionBandElement;
import ro.nextreports.engine.band.Hyperlink;
import ro.nextreports.engine.band.HyperlinkBandElement;
import ro.nextreports.engine.band.ImageBandElement;
import ro.nextreports.engine.band.ImageColumnBandElement;
import ro.nextreports.engine.band.Padding;
import ro.nextreports.engine.band.ReportBandElement;
import ro.nextreports.engine.band.VariableBandElement;
import ro.nextreports.engine.exporter.util.StyleFormatConstants;
import ro.nextreports.engine.exporter.util.variable.PageNoVariable;
import ro.nextreports.engine.exporter.util.variable.VariableFactory;
import ro.nextreports.engine.queryexec.QueryException;
import ro.nextreports.engine.util.ColorUtil;
import ro.nextreports.engine.util.StringUtil;

public class DocxExporter extends ResultExporter {
	
	private WordprocessingMLPackage wordMLPackage;
	private ObjectFactory factory;
	private Tbl table;
	private Tbl tableHeader;
	private Tbl tableFooter;
	private Tr tableRow;	
	private int fragmentsize = 15000;	
	private int[] headerwidths;
	private int A4_PORTRAIT_DXA = 11906;
	private int A4_LANDSCAPE_DXA = 16838;	
	private int rowNo = 0;
	// for a master report it is computed, for a subreport it is given
	private int currentWidth = -1;
	private boolean hasPageNoHeader = false;
	private boolean hasPageNoFooter = false;
	private Map<Integer, Integer> rowSpanForColumn = new HashMap<Integer, Integer>();
		
	public DocxExporter(ExporterBean bean) {
        super(bean);
    }
	
	public DocxExporter(ExporterBean bean, int currentWidth) {
        super(bean);
        this.currentWidth = currentWidth;
    }

	@Override
	protected void exportCell(String bandName, BandElement bandElement, Object value, int gridRow, int row, int column, int cols,
			int rowSpan, int colSpan, boolean isImage) {
		
		if (newRow) {
			// rowNo for a table (it is reset in createTable)
			rowNo++;						
						
			// for first row in page header and page footer we do not add the table row because it will be duplicated
			if ( ((!ReportLayout.PAGE_HEADER_BAND_NAME.equals(bandName) && !ReportLayout.PAGE_FOOTER_BAND_NAME.equals(bandName))) || (rowNo>1)) {	
				if (tableRow != null) {				
					if (ReportLayout.PAGE_HEADER_BAND_NAME.equals(bandName)) {			
						tableHeader.getContent().add(tableRow);
					} else if (ReportLayout.PAGE_FOOTER_BAND_NAME.equals(bandName)) {						
						tableFooter.getContent().add(tableRow);
					} else {						
						table.getContent().add(tableRow);
					}					
				}						
				tableRow = factory.createTr();
			}
			// create table header to be available on every page
			if (bean.getReportLayout().isHeaderOnEveryPage()) {
				if (ReportLayout.HEADER_BAND_NAME.equals(bandName)) {
					TrPr rowProperties = new TrPr();
					BooleanDefaultTrue bdt = Context.getWmlObjectFactory().createBooleanDefaultTrue();
					rowProperties.getCnfStyleOrDivIdOrGridBefore().add(Context.getWmlObjectFactory().createCTTrPrBaseTblHeader(bdt));						
					tableRow.setTrPr(rowProperties);
				}
			}
		}
		
		renderDocxCell(bandName,bandElement, value, gridRow, rowSpan, colSpan, isImage, column);		
	}
	
	private void renderDocxCell(String bandName, BandElement bandElement, Object value, int gridRow, int rowSpan, int colSpan, boolean image, int column) {
        Map<String, Object> style = buildCellStyleMap(bandElement, value, gridRow, column, colSpan);   
              
        String verticalMergedVal = null;
        if (rowSpan > 1) {        	
        	verticalMergedVal = "restart";        	
			rowSpanForColumn.put(column, rowSpan);			
        } else {
        	int span = rowSpanForColumn.get(column);        	
        	if (span > 1) {
        		rowSpanForColumn.put(column, span-1);
        		if (span == 2) {        			
        			// last cell to merge vertically
        			verticalMergedVal = "close";
        		} else {
        			verticalMergedVal = "";
        		}
        	}
        }

        int width = headerwidths[column];
        if (colSpan > 1) {
        	for (int i=1; i<colSpan; i++) {
        		width += headerwidths[column+i];
        	}
        }             

        if (image) {        	
        	if (value == null) {
        		addTableCell(tableRow, bandElement, "", width, style, colSpan, verticalMergedVal);
        	} else {
        		ImageBandElement ibe = (ImageBandElement)bandElement; 
	        	P pImage;
	    		try {
	    			byte[] imageD = getImage((String) value);	    			
	    			byte[] imageBytes = getImage(imageD, ibe.getWidth(), ibe.getHeight());
	    			int imageW;
	    			if (ibe.getWidth() == null) {
	    				imageW = getRealImageSize(imageBytes)[0];
	    			} else {
	    				imageW = ibe.getWidth();
	    			}	    			
	    			pImage = newImage(wordMLPackage, imageBytes, null, null, 0, 1, pixelsToDxa(imageW));	    			
	    			addTableCell(tableRow, bandElement, pImage, width, style, colSpan, verticalMergedVal, true);	    			
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}
        	}        
        } else if (bandElement instanceof HyperlinkBandElement)  {
            Hyperlink hyperlink = ((HyperlinkBandElement)bandElement).getHyperlink();            
            addHyperlinkTableCell(tableRow, bandElement, hyperlink, width, style, colSpan, verticalMergedVal);
            
        } else if (bandElement instanceof ReportBandElement)  {
            Report report = ((ReportBandElement)bandElement).getReport();  
            ExporterBean eb = null;
            try {            	
            	eb = getSubreportExporterBean(report);
                DocxExporter subExporter = new DocxExporter(eb, width);
                subExporter.export();
                Tbl innerTable = subExporter.getTable();   
                addSubreportTableCell(tableRow, bandElement, innerTable, width, style, colSpan, verticalMergedVal);                	                
			} catch (Exception e) {
				addTableCell(tableRow, bandElement, "", width, style, colSpan, verticalMergedVal);	
				e.printStackTrace();
			} finally {
				if ((eb != null) && (eb.getResult() != null)) {
					eb.getResult().close();
				}
			}
        } else if (  ((bandElement instanceof VariableBandElement) && 
        		     (VariableFactory.getVariable(((VariableBandElement) bandElement).getVariable()) instanceof PageNoVariable)) ||
        		     ((bandElement instanceof ExpressionBandElement) && 
        		      ((ExpressionBandElement)bandElement).getExpression().contains(PageNoVariable.PAGE_NO_PARAM)))  {

        	// limitation: if header or footer contains PAGE_NO varaible, only this will be shown, 
        	// all other cells from header/footer will be ignored
        	if (ReportLayout.PAGE_HEADER_BAND_NAME.equals(bandName)) {
        		hasPageNoHeader = true;
        	} else if (ReportLayout.PAGE_FOOTER_BAND_NAME.equals(bandName)) {
        		hasPageNoFooter = true;
        	}
        	// does not work (we should add pageNo to header or footer directly and not inside a table
        	// P numP = createPageNumParagraph();
        	// addTableCell(tableRow, bandElement, bandElement.getText(), numP, width, style, colSpan, verticalMergedVal);	       
        	
        } else if (bandElement instanceof ImageColumnBandElement) {
        	try {        		
        		String v = StringUtil.getValueAsString(value, null);
        		if(StringUtil.BLOB.equals(v)) {        			
        			addTableCell(tableRow, bandElement, StringUtil.BLOB, width, style, colSpan, verticalMergedVal);
        		} else {        				        			        		
	        		ImageColumnBandElement icbe = (ImageColumnBandElement) bandElement;
	        		byte[] imageD = StringUtil.decodeImage(v); 			
	        		byte[] imageBytes = getImage(imageD, icbe.getWidth(), icbe.getHeight());
	        		int imageW;
	    			if (icbe.getWidth() == null) {
	    				imageW = getRealImageSize(imageBytes)[0];
	    			} else {
	    				imageW = icbe.getWidth();
	    			}
	        		P pImage = newImage(wordMLPackage, imageBytes, null, null, 0, 1, pixelsToDxa(imageW));	    			
		    		addTableCell(tableRow, bandElement, pImage, width, style, colSpan, verticalMergedVal, true);	    					    		
        		}        		
			} catch (Exception e) {		
				e.printStackTrace();
				addTableCell(tableRow, bandElement, IMAGE_NOT_LOADED, width, style, colSpan, verticalMergedVal);				
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
            addTableCell(tableRow, bandElement, stringValue, width, style, colSpan, verticalMergedVal);	                   
        }
	}    

		
	@Override
	protected void initExport() throws QueryException {
		try {
			factory = Context.getWmlObjectFactory();	
			for (int i=0; i<bean.getReportLayout().getColumnCount(); i++) {
				rowSpanForColumn.put(i, 1);
			}
			if (!bean.isSubreport()) {
				boolean landscape = (bean.getReportLayout().getOrientation() == LANDSCAPE);
				wordMLPackage = WordprocessingMLPackage.createPackage(PageSizePaper.A4, landscape);							
				setPageMargins();				
				addMetadata();						
			}
			table = createTable(PRINT_DOCUMENT);	
		} catch (InvalidFormatException e) {
            e.printStackTrace();
            throw new QueryException(e);
        }
		
	}

	@Override
	protected void finishExport() {
		if (table != null) {			
			table.getContent().add(tableRow);
		}	
		
		if (!bean.isSubreport()) {
			
			if (table != null) {			
				wordMLPackage.getMainDocumentPart().addObject(table);
			} else {
				wordMLPackage.getMainDocumentPart().addParagraphOfText("");
			}
			
			table = null;		
			
			try {
				addPageHeaderFooter();				
				//addBackgroundImage();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
			try {
				wordMLPackage.save(getOut());
			} catch (Docx4JException e) {			
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void close() {				
	}

	@Override
	protected void flush() {
		if (!bean.isSubreport()) {
			if (resultSetRow % fragmentsize == fragmentsize - 1) {
				flushNow();
			}
		}		
	}

	@Override
	protected void flushNow() {		
		table.getContent().add(tableRow);
		if (!bean.isSubreport()) {					
			wordMLPackage.getMainDocumentPart().addObject(table);
			table = createTable(PRINT_DOCUMENT);	
			tableRow = null;
		}
    }
	
	@Override
	protected Set<CellElement> getIgnoredCells(Band band) {
		return getIgnoredCellElementsForColSpan(band);
	}
	
	@Override
	protected void afterRowExport() {	
	}

	@Override
	protected String getNullElement() {
		return "";
	}
	
	@Override
	protected void newPage() {		
		flushNow();
		Br objBr = new Br();
		objBr.setType(STBrType.PAGE);
		P para = createParagraph();
		para.getContent().add(objBr);
		wordMLPackage.getMainDocumentPart().getContent().add(para);
		if (bean.getReportLayout().isHeaderOnEveryPage()) {
			try {
				printHeaderBand();	
				newRow = true;		
			} catch (QueryException e) {				
				e.printStackTrace();
			}
		}
	}
	
	private void setPageMargins() {		
		try {
			Body body = wordMLPackage.getMainDocumentPart().getContents().getBody();
			Padding padding = bean.getReportLayout().getPagePadding();
			PageDimensions page = new PageDimensions();
			PgMar pgMar = page.getPgMar();      			
			pgMar.setBottom(BigInteger.valueOf(pixelsToDxa(padding.getBottom())));
			pgMar.setTop(BigInteger.valueOf(pixelsToDxa(padding.getTop())));
			pgMar.setLeft(BigInteger.valueOf(pixelsToDxa(padding.getLeft())));
			pgMar.setRight(BigInteger.valueOf(pixelsToDxa(padding.getRight())));			
			SectPr sectPr = factory.createSectPr();   
			body.setSectPr(sectPr);                           
			sectPr.setPgMar(pgMar);  
		} catch (Docx4JException e) {			
			e.printStackTrace();
		}		
	}
	
	private void addMetadata() {				
		try {
			DocPropsCorePart docPropsCorePart = wordMLPackage.getDocPropsCorePart();
			CoreProperties coreProps = (CoreProperties) docPropsCorePart.getContents();
			
			org.docx4j.docProps.core.ObjectFactory CorePropsfactory = new org.docx4j.docProps.core.ObjectFactory();
			org.docx4j.docProps.core.dc.elements.ObjectFactory dcElfactory = new org.docx4j.docProps.core.dc.elements.ObjectFactory();
			
			SimpleLiteral desc = dcElfactory.createSimpleLiteral();
			desc.getContent().add("Created by NextReports Designer" + ReleaseInfoAdapter.getVersionNumber());
			coreProps.setDescription(dcElfactory.createDescription(desc));
			
			SimpleLiteral title = dcElfactory.createSimpleLiteral();
			title.getContent().add(getDocumentTitle());
			coreProps.setTitle(dcElfactory.createTitle(title));
			
			SimpleLiteral author = dcElfactory.createSimpleLiteral();
			author.getContent().add(ReleaseInfoAdapter.getCompany());
			coreProps.setCreator(author);
			
			SimpleLiteral subject = dcElfactory.createSimpleLiteral();
			subject.getContent().add("Created by NextReports Designer" + ReleaseInfoAdapter.getVersionNumber());
			coreProps.setSubject(subject);
					
			coreProps.setKeywords(ReleaseInfoAdapter.getHome());
		} catch (Docx4JException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private Tbl createTable(int type) {		
		rowNo = 0;
				
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
        
        headerwidths = new int[totalColumns]; 
           
        boolean landscape = (bean.getReportLayout().getOrientation() == LANDSCAPE);
        Padding padding = bean.getReportLayout().getPagePadding();		
		int margs = pixelsToDxa(padding.getLeft()+padding.getRight());			
		if (currentWidth == -1) {
			currentWidth = landscape ? (A4_LANDSCAPE_DXA-margs) : (A4_PORTRAIT_DXA-margs);
		}
        int colWidth = currentWidth/totalColumns;
        for (int i = 0; i < totalColumns; i++) {
            if (bean.getReportLayout().isUseSize()) {            	
                headerwidths[i] = pixelsToDxa(bean.getReportLayout().getColumnsWidth().get(i));
            } else {
            	headerwidths[i] = colWidth;
            }            
        }    
        
        Tbl resultTable = factory.createTbl();				
        
        // set table width
        TblPr tblPr = factory.createTblPr();
        TblWidth tblWidth = factory.createTblWidth();        
        if (bean.getReportLayout().isUseSize()) {
        	int sum = 0;
        	for (int i = 0; i < totalColumns; i++) {
        		sum += headerwidths[i];
        	}
        	currentWidth = sum;
        }        
        tblWidth.setW(BigInteger.valueOf(currentWidth));
        tblWidth.setType(TblWidth.TYPE_DXA);
        tblPr.setTblW(tblWidth);
        resultTable.setTblPr(tblPr);
        
        return resultTable;
	}
	
	private void addHyperlinkTableCell(Tr tableRow, BandElement be, Hyperlink link, int width, Map<String, Object> style, int horizontalMergedCells, String verticalMergedVal) {
		org.docx4j.wml.P.Hyperlink hyp = newHyperlink(wordMLPackage.getMainDocumentPart(), link.getText(), link.getUrl());
		P paragraph = createParagraph();		
		paragraph.getContent().add(hyp);
		addTableCell(tableRow, be, paragraph, width, style, horizontalMergedCells, verticalMergedVal, false);
	}
	
	private void addSubreportTableCell(Tr tableRow, BandElement be, Tbl table, int width, Map<String, Object> style, int horizontalMergedCells, String verticalMergedVal) {		
		Tc tableCell = factory.createTc();		
		tableCell.getContent().add(table);
		tableCell.getContent().add(wordMLPackage.getMainDocumentPart().createParagraphOfText(""));
		setCellWidth(tableCell, width);				
		setCellVMerge(tableCell, verticalMergedVal);				
		setCellHMerge(tableCell, horizontalMergedCells);			
		tableRow.getContent().add(tableCell);
	}
	
	private void addTableCell(Tr tableRow, BandElement be, P paragraph, int width, Map<String, Object>  style, int horizontalMergedCells, String verticalMergedVal, boolean isImage) {
		Tc tableCell = factory.createTc();		
		if (isImage) {
			addImageCellStyle(tableCell, be, paragraph, style);
		} else {			
			addHyperlinkCellStyle(tableCell, be, paragraph, style);
		}
		setCellWidth(tableCell, width);				
		setCellVMerge(tableCell, verticalMergedVal);				
		setCellHMerge(tableCell, horizontalMergedCells);	
		if (!isImage) {
			if ((be != null) && !be.isWrapText()) {
				setCellNoWrap(tableCell);
			}
		}
		tableRow.getContent().add(tableCell);
	}
	
	private void addTableCell(Tr tableRow, BandElement be, String content, int width, Map<String, Object> style, int horizontalMergedCells, String verticalMergedVal) {		
		Tc tableCell = factory.createTc();				
		addCellStyle(tableCell, be, content, style);		 		
		setCellWidth(tableCell, width);				
		setCellVMerge(tableCell, verticalMergedVal);				
		setCellHMerge(tableCell, horizontalMergedCells);
		if ((be != null) && !be.isWrapText()) {
			setCellNoWrap(tableCell);
		}		
		tableRow.getContent().add(tableCell);
	}
	
	private void addTableCell(Tr tableRow, BandElement be, String content, P p, int width, Map<String, Object> style, int horizontalMergedCells, String verticalMergedVal) {		
		Tc tableCell = factory.createTc();				
		addCellStyle(tableCell, be, content, p, style);		 		
		setCellWidth(tableCell, width);				
		setCellVMerge(tableCell, verticalMergedVal);				
		setCellHMerge(tableCell, horizontalMergedCells);
		if ((be != null) && !be.isWrapText()) {
			setCellNoWrap(tableCell);
		}		
		tableRow.getContent().add(tableCell);
	}
	
	private void addImageCellStyle(Tc tableCell, BandElement be, P image,  Map<String, Object> style) {		
		setCellMargins(tableCell, style);
		setBackground(tableCell, style);
		setVerticalAlignment(tableCell, style);		
		setHorizontalAlignment(image, style);
		setCellBorders(tableCell, style);
		tableCell.getContent().add(image);
	}
	
	private void addHyperlinkCellStyle(Tc tableCell, BandElement be, P hyperlink, Map<String, Object> style) {
		setCellMargins(tableCell, style);
		setBackground(tableCell, style);
		setVerticalAlignment(tableCell, style);		
		setHorizontalAlignment(hyperlink, style);
		setCellBorders(tableCell, style);
					
		R run = (R)  ((org.docx4j.wml.P.Hyperlink)hyperlink.getContent().get(0)).getContent().get(0);
		RPr runProperties = run.getRPr();
		setFont(tableCell, style, runProperties);											
		if (be != null) {
			setTextDirection(tableCell, be.getTextRotation());
		}
		
		tableCell.getContent().add(hyperlink);		
	}
	
	private void addCellStyle(Tc tableCell, BandElement be, String content, Map<String, Object> style) {
		P paragraph = createParagraph();
		addCellStyle(tableCell, be, content, paragraph, style);
	}
	
	private void addCellStyle(Tc tableCell, BandElement be, String content, P paragraph, Map<String, Object> style) {
		if (style != null) {
			
			// inner html text
			if (content.startsWith("<html>")) {
				try {					
					wordMLPackage.getMainDocumentPart().addAltChunk(AltChunkType.Html, content.getBytes(), tableCell);					
					tableCell.getContent().add(paragraph);					
				} catch (Docx4JException e) {					
					e.printStackTrace();
				}
				return;
			}
						
			Text text = factory.createText();
			text.setValue(content);
	
			R run = factory.createR();
			run.getContent().add(text);
	
			paragraph.getContent().add(run);
			
			setHorizontalAlignment(paragraph, style);	
					
			RPr runProperties = factory.createRPr();
			
			setFont(tableCell, style, runProperties);									
			setCellMargins(tableCell,style);			
			setBackground(tableCell, style);			
			setVerticalAlignment(tableCell, style);					
			setCellBorders(tableCell, style);
			if (be != null) {
				setTextDirection(tableCell, be.getTextRotation());
			}
	
			run.setRPr(runProperties);
	
			tableCell.getContent().add(paragraph);
		}
	}
	
	private void setBackground(Tc tableCell, Map<String, Object> style) {
		// to see a background image all cells must not have any background!
		if (bean.getReportLayout().getBackgroundImage() == null) {
			if (style.containsKey(StyleFormatConstants.BACKGROUND_COLOR)) {
				Color val = (Color) style.get(StyleFormatConstants.BACKGROUND_COLOR);
				setCellColor(tableCell, ColorUtil.getHexColor(val).substring(1));
			}
		}
	}
	
	private void setFont(Tc tableCell, Map<String, Object> style, RPr runProperties ) {
		if (style.containsKey(StyleFormatConstants.FONT_FAMILY_KEY)) {
			String val = (String) style.get(StyleFormatConstants.FONT_FAMILY_KEY);
			setFontFamily(runProperties, val);
		}
		if (style.containsKey(StyleFormatConstants.FONT_SIZE)) {
			Float val = (Float) style.get(StyleFormatConstants.FONT_SIZE);
			setFontSize(runProperties, String.valueOf( (int)(2*val)));	
		}
		if (style.containsKey(StyleFormatConstants.FONT_COLOR)) {
			Color val = (Color) style.get(StyleFormatConstants.FONT_COLOR);
			setFontColor(runProperties, ColorUtil.getHexColor(val).substring(1));	
		}
		if (style.containsKey(StyleFormatConstants.FONT_STYLE_KEY)) {				
			if (StyleFormatConstants.FONT_STYLE_BOLD.equals(style.get(StyleFormatConstants.FONT_STYLE_KEY))) {
				addBoldStyle(runProperties);
			}
			if (StyleFormatConstants.FONT_STYLE_ITALIC.equals(style.get(StyleFormatConstants.FONT_STYLE_KEY))) {
				addItalicStyle(runProperties);
			}
			if (StyleFormatConstants.FONT_STYLE_BOLDITALIC.equals(style.get(StyleFormatConstants.FONT_STYLE_KEY))) {
				addBoldStyle(runProperties);
				addItalicStyle(runProperties);
			}
		}
	}
	
	private void setTextDirection(Tc tableCell, short textRotation) {
		String dir = null;
		if (textRotation == 90) {
			dir = "btLr";
		} else if (textRotation == -90) {
			dir = "tbRl";
		}
		
		if (dir != null) {
			TcPr tableCellProperties = tableCell.getTcPr();
			if (tableCellProperties == null) {
				tableCellProperties = new TcPr();
				tableCell.setTcPr(tableCellProperties);
			}
			TextDirection td = new TextDirection();
			td.setVal(dir);
			tableCellProperties.setTextDirection(td);
		}
	}
	
	private void setCellBorders(Tc tableCell, Map<String, Object> style) {
				
		TcPr tableCellProperties = tableCell.getTcPr();
		if (tableCellProperties == null) {
			tableCellProperties = new TcPr();
			tableCell.setTcPr(tableCellProperties);
		}
		
		CTBorder border = new CTBorder();
		// border.setColor("auto");			
		border.setSpace(new BigInteger("0"));
		border.setVal(STBorder.SINGLE);			
		
		TcBorders borders = new TcBorders();		
		
		if (style.containsKey(StyleFormatConstants.BORDER_LEFT)) {
            Float val = (Float) style.get(StyleFormatConstants.BORDER_LEFT);
            border.setSz(BigInteger.valueOf((long) (val / 2)));
            Color color = (Color) style.get(StyleFormatConstants.BORDER_LEFT_COLOR);
            border.setColor(ColorUtil.getHexColor(color).substring(1));	 
            borders.setLeft(border);
        }
        if (style.containsKey(StyleFormatConstants.BORDER_RIGHT)) {
            Float val = (Float) style.get(StyleFormatConstants.BORDER_RIGHT);
            border.setSz(BigInteger.valueOf((long) (val / 2)));
            Color color = (Color) style.get(StyleFormatConstants.BORDER_RIGHT_COLOR);
            border.setColor(ColorUtil.getHexColor(color).substring(1));	 
            borders.setRight(border);
        }
        if (style.containsKey(StyleFormatConstants.BORDER_TOP)) {
            Float val = (Float) style.get(StyleFormatConstants.BORDER_TOP);
            border.setSz(BigInteger.valueOf((long) (val / 2)));
            Color color = (Color) style.get(StyleFormatConstants.BORDER_TOP_COLOR);
            border.setColor(ColorUtil.getHexColor(color).substring(1));	 
            borders.setTop(border);
        }
        if (style.containsKey(StyleFormatConstants.BORDER_BOTTOM)) {
            Float val = (Float) style.get(StyleFormatConstants.BORDER_BOTTOM);
            border.setSz(BigInteger.valueOf((long) (val / 2)));
            Color color = (Color) style.get(StyleFormatConstants.BORDER_BOTTOM_COLOR);
            border.setColor(ColorUtil.getHexColor(color).substring(1));	
            borders.setBottom(border);
        }
		
		tableCellProperties.setTcBorders(borders);		
	}

	private void setCellWidth(Tc tableCell, int width) {		
		if (width > 0) {
			TcPr tableCellProperties = tableCell.getTcPr();
			if (tableCellProperties == null) {
				tableCellProperties = new TcPr();
				tableCell.setTcPr(tableCellProperties);
			}
			TblWidth tableWidth = new TblWidth();
			tableWidth.setType("dxa");
			tableWidth.setW(BigInteger.valueOf(width));
			tableCellProperties.setTcW(tableWidth);
		}
	}
	
	private void setCellNoWrap(Tc tableCell) {
		TcPr tableCellProperties = tableCell.getTcPr();
		if (tableCellProperties == null) {
			tableCellProperties = new TcPr();
			tableCell.setTcPr(tableCellProperties);
		}
		BooleanDefaultTrue b = new BooleanDefaultTrue();
		b.setVal(true);
		tableCellProperties.setNoWrap(b);		
	}

	private void setCellVMerge(Tc tableCell, String mergeVal) {
		if (mergeVal != null) {
			TcPr tableCellProperties = tableCell.getTcPr();
			if (tableCellProperties == null) {
				tableCellProperties = new TcPr();
				tableCell.setTcPr(tableCellProperties);
			}
			VMerge merge = new VMerge();
			if (!"close".equals(mergeVal)) {
				merge.setVal(mergeVal);
			}
			tableCellProperties.setVMerge(merge);
		}
	}
	
	private void setCellHMerge(Tc tableCell, int horizontalMergedCells) {		
		if (horizontalMergedCells > 1) {
			TcPr tableCellProperties = tableCell.getTcPr();
			if (tableCellProperties == null) {
				tableCellProperties = new TcPr();
				tableCell.setTcPr(tableCellProperties);
			}
	
			GridSpan gridSpan = new GridSpan();
			gridSpan.setVal(new BigInteger(String.valueOf(horizontalMergedCells)));
	
			tableCellProperties.setGridSpan(gridSpan);
			tableCell.setTcPr(tableCellProperties);
		}				
	}	
	
	private void setCellColor(Tc tableCell, String color) {
		if (color != null) {
			TcPr tableCellProperties = tableCell.getTcPr();
			if (tableCellProperties == null) {
				tableCellProperties = new TcPr();
				tableCell.setTcPr(tableCellProperties);
			}
			CTShd shd = new CTShd();
			shd.setFill(color);
			tableCellProperties.setShd(shd);
		}
	}

	private void setCellMargins(Tc tableCell, Map<String, Object> style) {
		int top = 0, left = 0, bottom = 0 ,right = 0;
		if (style.containsKey(StyleFormatConstants.PADDING_LEFT)) {
            Float val = (Float) style.get(StyleFormatConstants.PADDING_LEFT);
            left = val.intValue();
        }
        if (style.containsKey(StyleFormatConstants.PADDING_RIGHT)) {
            Float val = (Float) style.get(StyleFormatConstants.PADDING_RIGHT);
            right = val.intValue();
        }
        if (style.containsKey(StyleFormatConstants.PADDING_TOP)) {
            Float val = (Float) style.get(StyleFormatConstants.PADDING_TOP);
            top = val.intValue();
        }
        if (style.containsKey(StyleFormatConstants.PADDING_BOTTOM)) {
            Float val = (Float) style.get(StyleFormatConstants.PADDING_BOTTOM);
            bottom = val.intValue();
        }				
		
		TcPr tableCellProperties = tableCell.getTcPr();
		if (tableCellProperties == null) {
			tableCellProperties = new TcPr();
			tableCell.setTcPr(tableCellProperties);
		}
		TcMar margins = new TcMar();

		if (bottom > 0) {
			TblWidth bW = new TblWidth();
			bW.setType("dxa");
			bW.setW(BigInteger.valueOf(pixelsToDxa(bottom)));
			margins.setBottom(bW);
		}

		if (top  > 0) {
			TblWidth tW = new TblWidth();
			tW.setType("dxa");
			tW.setW(BigInteger.valueOf(pixelsToDxa(top)));
			margins.setTop(tW);
		}

		if (left > 0) {
			TblWidth lW = new TblWidth();
			lW.setType("dxa");
			lW.setW(BigInteger.valueOf(pixelsToDxa(left)));
			margins.setLeft(lW);
		}

		if (right > 0) {
			TblWidth rW = new TblWidth();
			rW.setType("dxa");
			rW.setW(BigInteger.valueOf(pixelsToDxa(right)));
			margins.setRight(rW);
		}

		tableCellProperties.setTcMar(margins);		
	}
	
	private void setVerticalAlignment(Tc tableCell, Map<String, Object> style) {
		if (style.containsKey(StyleFormatConstants.HORIZONTAL_ALIGN_KEY)) {
			if (StyleFormatConstants.VERTICAL_ALIGN_TOP.equals(style.get(StyleFormatConstants.VERTICAL_ALIGN_KEY))) {
            	setVerticalAlignment(tableCell, STVerticalJc.TOP);
            }
            if (StyleFormatConstants.VERTICAL_ALIGN_MIDDLE.equals(style.get(StyleFormatConstants.VERTICAL_ALIGN_KEY))) {
            	setVerticalAlignment(tableCell, STVerticalJc.CENTER);
            }
            if (StyleFormatConstants.VERTICAL_ALIGN_BOTTOM.equals(style.get(StyleFormatConstants.VERTICAL_ALIGN_KEY))) {
            	setVerticalAlignment(tableCell, STVerticalJc.BOTTOM);
            }
		}
	}

	private void setVerticalAlignment(Tc tableCell, STVerticalJc align) {
		if (align != null) {
			TcPr tableCellProperties = tableCell.getTcPr();
			if (tableCellProperties == null) {
				tableCellProperties = new TcPr();
				tableCell.setTcPr(tableCellProperties);
			}
	
			CTVerticalJc valign = new CTVerticalJc();
			valign.setVal(align);
	
			tableCellProperties.setVAlign(valign);
		}
	}
	
	private void setFontSize(RPr runProperties, String fontSize) {
		if (fontSize != null && !fontSize.isEmpty()) {
			HpsMeasure size = new HpsMeasure();
			size.setVal(new BigInteger(fontSize));
			runProperties.setSz(size);
			runProperties.setSzCs(size);
		}
	}

	private void setFontFamily(RPr runProperties, String fontFamily) {
		if (fontFamily != null) {
			RFonts rf = runProperties.getRFonts();
			if (rf == null) {
				rf = new RFonts();
				runProperties.setRFonts(rf);
			}
			rf.setAscii(fontFamily);
		}
	}

	private void setFontColor(RPr runProperties, String color) {
		if (color != null) {
			org.docx4j.wml.Color c = new org.docx4j.wml.Color();
			c.setVal(color);
			runProperties.setColor(c);
		}	
	}
	
	private void setHorizontalAlignment(P paragraph, Map<String, Object> style) {
		if (style.containsKey(StyleFormatConstants.HORIZONTAL_ALIGN_KEY)) {
			if (StyleFormatConstants.HORIZONTAL_ALIGN_LEFT.equals(style.get(StyleFormatConstants.HORIZONTAL_ALIGN_KEY))) {
				setHorizontalAlignment(paragraph, JcEnumeration.LEFT);
			}
			if (StyleFormatConstants.HORIZONTAL_ALIGN_RIGHT.equals(style.get(StyleFormatConstants.HORIZONTAL_ALIGN_KEY))) {
				setHorizontalAlignment(paragraph, JcEnumeration.RIGHT);
			}
			if (StyleFormatConstants.HORIZONTAL_ALIGN_CENTER.equals(style.get(StyleFormatConstants.HORIZONTAL_ALIGN_KEY))) {
				setHorizontalAlignment(paragraph, JcEnumeration.CENTER);
			}
		}
	}
	
	private void setHorizontalAlignment(P paragraph, JcEnumeration hAlign) {
		if (hAlign != null) {			
			PPr pprop = paragraph.getPPr();
			if (pprop == null) {
				pprop = new PPr();
				paragraph.setPPr(pprop);
			}						
			Jc align = new Jc();
			align.setVal(hAlign);
			pprop.setJc(align);
			paragraph.setPPr(pprop);
		}
	}
	
	private void addBoldStyle(RPr runProperties) {
		BooleanDefaultTrue b = new BooleanDefaultTrue();
		b.setVal(true);
		runProperties.setB(b);
	}

	private void addItalicStyle(RPr runProperties) {
		BooleanDefaultTrue b = new BooleanDefaultTrue();
		b.setVal(true);
		runProperties.setI(b);
	}

	private void addUnderlineStyle(RPr runProperties) {
		U val = new U();
		val.setVal(UnderlineEnumeration.SINGLE);
		runProperties.setU(val);
	}

	
	public P newImage(WordprocessingMLPackage wordMLPackage, byte[] bytes, String filenameHint, String altText, int id1, int id2, long cx) throws Exception {
		BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);
		Inline inline = imagePart.createImageInline(filenameHint, altText, id1, id2, cx, false);
		// Now add the inline in w:p/w:r/w:drawing
		ObjectFactory factory = Context.getWmlObjectFactory();
		P p = createParagraph();	
		R run = factory.createR();
		p.getContent().add(run);
		Drawing drawing = factory.createDrawing();
		run.getContent().add(drawing);
		drawing.getAnchorOrInline().add(inline);
		return p;
	}
	
	public org.docx4j.wml.P.Hyperlink newHyperlink(MainDocumentPart mdp, String text, String url) {
		try {
			// We need to add a relationship to word/_rels/document.xml.rels but since its external, we don't use 
			// the usual wordMLPackage.getMainDocumentPart().addTargetPart mechanism
			org.docx4j.relationships.ObjectFactory factory = new org.docx4j.relationships.ObjectFactory();
			org.docx4j.relationships.Relationship rel = factory.createRelationship();
			rel.setType(Namespaces.HYPERLINK);
			rel.setTarget(url);
			rel.setTargetMode("External");
			mdp.getRelationshipsPart().addRelationship(rel);
			// addRelationship sets the rel's @Id
			
			org.docx4j.wml.P.Hyperlink hyp = new org.docx4j.wml.P.Hyperlink();
			hyp.setId(rel.getId());
			R run = Context.getWmlObjectFactory().createR();
			hyp.getContent().add(run);
			RPr rpr = new RPr();
			RStyle rStyle = new RStyle();
			rStyle.setVal("Hyperlink");
			rpr.setRStyle(rStyle);			
			run.setRPr(rpr);
			
			Text t = new Text();
			t.setValue(text);
			run.getContent().add(t);
//			String hpl = "<w:hyperlink r:id=\"" + rel.getId()
//					+ "\" xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" "
//					+ "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" >" + "<w:r>" + "<w:rPr>"
//					+ "<w:rStyle w:val=\"Hyperlink\" />" + 
//					"</w:rPr>" + "<w:t>" + text + "</w:t>" + "</w:r>" + "</w:hyperlink>";
//			return (org.docx4j.wml.P.Hyperlink) XmlUtils.unmarshalString(hpl);
			return hyp;
		} catch (Exception e) {			
			e.printStackTrace();
			return null;
		}
	}
	
	private int pixelsToDxa(int pixels) {
		return  ( 1440 * pixels / getDPI() ); 		
	}		
	
	// create paraghraph with no space after
	private P createParagraph() {
		P paragraph = factory.createP();
		PPr pPr = factory.createPPr();
		Spacing spacing = new Spacing();
		spacing.setAfter(BigInteger.ZERO);
		pPr.setSpacing(spacing);
		paragraph.setPPr(pPr);
		return paragraph;
	}	
	
	private Hdr getHdr(WordprocessingMLPackage wordprocessingMLPackage, Part sourcePart, Tbl table) throws Exception {
		Hdr hdr = factory.createHdr();	
		if (hasPageNoHeader) {
			hdr.getContent().add(createPageNumParagraph());
		} else {
			hdr.getContent().add(table);
		}
		return hdr;
	}
	
	private void createHeaderReference(WordprocessingMLPackage wordprocessingMLPackage, Relationship relationship)
			throws InvalidFormatException {
		List<SectionWrapper> sections = wordprocessingMLPackage.getDocumentModel().getSections();
		SectPr sectPr = sections.get(sections.size() - 1).getSectPr();
		// There is always a section wrapper, but it might not contain a sectPr
		if (sectPr == null) {
			sectPr = factory.createSectPr();
			wordprocessingMLPackage.getMainDocumentPart().addObject(sectPr);
			sections.get(sections.size() - 1).setSectPr(sectPr);
		}
		HeaderReference headerReference = factory.createHeaderReference();
		headerReference.setId(relationship.getId());
		headerReference.setType(HdrFtrRef.DEFAULT);
		sectPr.getEGHdrFtrReferences().add(headerReference);
	}
	
	private Relationship createHeaderPart(WordprocessingMLPackage wordprocessingMLPackage, Tbl table) throws Exception {
		HeaderPart headerPart = new HeaderPart();
		Relationship rel = wordprocessingMLPackage.getMainDocumentPart().addTargetPart(headerPart);
		// After addTargetPart, so image can be added properly
		headerPart.setJaxbElement(getHdr(wordprocessingMLPackage, headerPart, table));
		return rel;
	}
	
	private Ftr getFtr(WordprocessingMLPackage wordprocessingMLPackage, Part sourcePart, Tbl table) throws Exception {
		Ftr ftr = factory.createFtr();		
		if (hasPageNoFooter) {
			ftr.getContent().add(createPageNumParagraph());
		} else {
			ftr.getContent().add(table);
		}
		return ftr;
	}
	
	private void createFooterReference(WordprocessingMLPackage wordprocessingMLPackage, Relationship relationship)
			throws InvalidFormatException {
		List<SectionWrapper> sections = wordprocessingMLPackage.getDocumentModel().getSections();
		SectPr sectPr = sections.get(sections.size() - 1).getSectPr();
		// There is always a section wrapper, but it might not contain a sectPr
		if (sectPr == null) {
			sectPr = factory.createSectPr();
			wordprocessingMLPackage.getMainDocumentPart().addObject(sectPr);
			sections.get(sections.size() - 1).setSectPr(sectPr);
		}
		FooterReference footerReference = factory.createFooterReference();
		footerReference.setId(relationship.getId());
		footerReference.setType(HdrFtrRef.DEFAULT);
		sectPr.getEGHdrFtrReferences().add(footerReference);
	}
	
	private Relationship createFooterPart(WordprocessingMLPackage wordprocessingMLPackage, Tbl table) throws Exception {
		FooterPart footerPart = new FooterPart();
		Relationship rel = wordprocessingMLPackage.getMainDocumentPart().addTargetPart(footerPart);
		// After addTargetPart, so image can be added properly
		footerPart.setJaxbElement(getFtr(wordprocessingMLPackage, footerPart, table));
		return rel;
	}
	
	private void addPageHeaderFooter() throws Exception {
		// Delete the Styles part, since it clutters up our output
//		MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
//		Relationship styleRel = mdp.getStyleDefinitionsPart().getSourceRelationships().get(0);
//		mdp.getRelationshipsPart().removeRelationship(styleRel);
		
		tableHeader = createTable(PRINT_PAGE_HEADER);						
		if (tableHeader != null) {			
			tableRow = factory.createTr();
			printPageHeaderBand();				
			tableHeader.getContent().add(tableRow);
			Relationship headerRel = createHeaderPart(wordMLPackage, tableHeader);		
			createHeaderReference(wordMLPackage, headerRel);
		}
		tableFooter = createTable(PRINT_PAGE_FOOTER);
		if (tableFooter != null) {			
			tableRow = factory.createTr();
			printPageFooterBand();			
			tableFooter.getContent().add(tableRow);
			Relationship footerRel = createFooterPart(wordMLPackage, tableFooter);		
			createFooterReference(wordMLPackage, footerRel);
		}
		
	}
	
	private void addBackgroundImage() throws Exception {
		String image = bean.getReportLayout().getBackgroundImage();
		if (image != null) {						
			byte[] imageBytes = getImage(image);			
			MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
			BinaryPartAbstractImage imagePartBG = BinaryPartAbstractImage.createImagePart(wordMLPackage, imageBytes);      		            
            mdp.getContents().setBackground(createBackground(imagePartBG.getRelLast().getId())); 
		}
	}
	
	private CTBackground createBackground(String rId) {

		org.docx4j.wml.ObjectFactory wmlObjectFactory = new org.docx4j.wml.ObjectFactory();

		CTBackground background = wmlObjectFactory.createCTBackground();
		background.setColor("FFFFFF");
		org.docx4j.vml.ObjectFactory vmlObjectFactory = new org.docx4j.vml.ObjectFactory();
		// Create object for background (wrapped in JAXBElement)
		org.docx4j.vml.CTBackground background2 = vmlObjectFactory.createCTBackground();
		JAXBElement<org.docx4j.vml.CTBackground> backgroundWrapped = vmlObjectFactory.createBackground(background2);
		background.getAnyAndAny().add(backgroundWrapped);
		background2.setTargetscreensize("1024,768");
		background2.setVmlId("_x0000_s1025");
		background2.setBwmode(org.docx4j.vml.officedrawing.STBWMode.WHITE);
		// Create object for fill
		CTFill fill = vmlObjectFactory.createCTFill();
		background2.setFill(fill);
		fill.setTitle("Alien 1");
		fill.setId(rId);
		fill.setType(org.docx4j.vml.STFillType.FRAME);
		fill.setRecolor(org.docx4j.vml.STTrueFalse.T);

		return background;
	}
		
		
	
	private P createPageNumParagraph() {
		 CTSimpleField pgnum = factory.createCTSimpleField();
         pgnum.setInstr(" PAGE \\* MERGEFORMAT ");
         RPr RPr = factory.createRPr();
         RPr.setNoProof(new BooleanDefaultTrue());
         PPr ppr = factory.createPPr();
         Jc jc = factory.createJc();
         jc.setVal(JcEnumeration.CENTER);
         ppr.setJc(jc);
         PPrBase.Spacing pprbase = factory.createPPrBaseSpacing();
         pprbase.setBefore(BigInteger.valueOf(240));
         pprbase.setAfter(BigInteger.valueOf(0));
         ppr.setSpacing(pprbase);
       
         R run = factory.createR();
         run.getContent().add(RPr);
         pgnum.getContent().add(run);

         JAXBElement<CTSimpleField> fldSimple = factory.createPFldSimple(pgnum);
         P para = createParagraph();
         para.getContent().add(fldSimple);
         para.setPPr(ppr);  
         return para;
	}
	
	public Tbl getTable() {		
    	return table;
    }

}
