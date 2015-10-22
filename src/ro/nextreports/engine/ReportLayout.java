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
package ro.nextreports.engine;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.ChartBandElement;
import ro.nextreports.engine.band.FunctionBandElement;
import ro.nextreports.engine.band.Padding;
import ro.nextreports.engine.band.PaperSize;
import ro.nextreports.engine.band.ReportBandElement;
import ro.nextreports.engine.exporter.ResultExporter;
import ro.nextreports.engine.i18n.I18nLanguage;


/**
 * @author Decebal Suiu
 */
public class ReportLayout implements Serializable {

    public static final int HEADER_GROUP_MASK = 1;
	public static final int FOOTER_GROUP_MASK = 2;
	public static final int ALL_GROUP_MASK = HEADER_GROUP_MASK | FOOTER_GROUP_MASK;

    public static final String HEADER_BAND_NAME = "Header";
    public static final String PAGE_HEADER_BAND_NAME = "PageHeader";
    public static final String DETAIL_BAND_NAME = "Detail";
    public static final String FOOTER_BAND_NAME = "Footer";
    public static final String PAGE_FOOTER_BAND_NAME = "PageFooter";
    public static final String GROUP_HEADER_BAND_NAME_PREFIX = "Group_Header";
    public static final String GROUP_FOOTER_BAND_NAME_PREFIX = "Group_Footer";

    public static final String LETTER = "LETTER";
    public static final String A0 = "A0";
    public static final String A1 = "A1";
    public static final String A2 = "A2";
    public static final String A3 = "A3";
    public static final String A4 = "A4";
    public static final String LEGAL = "LEGAL";
    public static final String LEDGER = "LEDGER";
    public static final String TABLOID = "TABLOID";
    public static final String CUSTOM = "CUSTOM";
	
    private static final long serialVersionUID = -345768096451315236L;

    private List<ReportGroup> groups;
    private List<Integer> columnsWidth;
    private boolean useSize;
    
    private Band headerBand;    
    private Band pageHeaderBand;
    private List<Band> groupHeaderBands;
    private Band detailBand;
    private List<Band> groupFooterBands;
    private Band pageFooterBand;
    private Band footerBand;

    private int orientation;
    private int reportType;
    private String pageFormat;
    private PaperSize paperSize;
    private Padding pagePadding;
    private String backgroundImage;
    private String templateName;
    private int templateSheet;
    private String sheetNames;
    

    private boolean headerOnEveryPage;
    private boolean showEmptyData;
    
    private List<String> i18nkeys;
    private List<I18nLanguage> languages;

    public ReportLayout() {
        groups = new ArrayList<ReportGroup>();
        
        headerBand = new Band(HEADER_BAND_NAME);
        pageHeaderBand = new Band(PAGE_HEADER_BAND_NAME);
        detailBand = new Band(DETAIL_BAND_NAME);
        pageFooterBand = new Band(PAGE_FOOTER_BAND_NAME);
        footerBand = new Band(FOOTER_BAND_NAME);
        groupHeaderBands = new ArrayList<Band>();
        groupFooterBands = new ArrayList<Band>();

        columnsWidth = new ArrayList<Integer>();
    }

    public List<Integer> getColumnsWidth() {
        return columnsWidth;
    }

    public void setColumnsWidth(List<Integer> columnsWidth) {
        this.columnsWidth = columnsWidth;
    }

    public boolean isUseSize() {
        return useSize;
    }

    public void setUseSize(boolean useSize) {
        this.useSize = useSize;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
        
    public int getReportType() {
		return reportType;
	}

	public void setReportType(int reportType) {
		this.reportType = reportType;
	}

	public String getPageFormat() {
        if (pageFormat == null) {
            return A4;
        }
        return pageFormat;
    }

    public void setPageFormat(String pageFormat) {
        this.pageFormat = pageFormat;
    }
    
    public PaperSize getPaperSize() {
        if (paperSize == null) {
            return PaperSize.A4;
        }
        return paperSize;
    }

    public void setPaperSize(PaperSize paperSize) {
        this.paperSize = paperSize;
    }
                        
    public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public int getTemplateSheet() {
		return templateSheet;
	}

	public void setTemplateSheet(int templateSheet) {
		this.templateSheet = templateSheet;
	}
		
	public String getSheetNames() {
		return sheetNames;
	}

	public void setSheetNames(String sheetNames) {
		this.sheetNames = sheetNames;
	}

	public String getBackgroundImage() {
		return backgroundImage;
	}

	public void setBackgroundImage(String backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	public Padding getPagePadding() {
    	if (pagePadding == null) {
    		int m = ResultExporter.DEFAULT_PADDING_PIXELS;
    		return new Padding(m, m, m, m);
    	}
		return pagePadding;
	}

	public void setPagePadding(Padding pagePadding) {
		this.pagePadding = pagePadding;
	}

	public boolean isHeaderOnEveryPage() {
        return headerOnEveryPage;
    }

    public void setHeaderOnEveryPage(boolean headerOnEveryPage) {
        this.headerOnEveryPage = headerOnEveryPage;
    }
    
    public boolean isShowEmptyData() {
		return showEmptyData;
	}

	public void setShowEmptyData(boolean showEmptyData) {
		this.showEmptyData = showEmptyData;
	}

	public Band getDetailBand() {
        return detailBand;
    }
    
    public Band getFooterBand() {
        return footerBand;
    }
    
    public Band getHeaderBand() {
        return headerBand;
    }
    
    public Band getPageFooterBand() {
    	if (pageFooterBand == null) {
    		pageFooterBand = new Band(PAGE_FOOTER_BAND_NAME);
    	}
        return pageFooterBand;
    }
    
    public Band getPageHeaderBand() {
    	if (pageHeaderBand == null) {
    		pageHeaderBand = new Band(PAGE_HEADER_BAND_NAME);
    	}
        return pageHeaderBand;
    }
    
    public List<Band> getGroupHeaderBands() {
		return groupHeaderBands;
	}

	public List<Band> getGroupFooterBands() {
		return groupFooterBands;
	}

	public List<ReportGroup> getGroups() {
		return groups;
	}

	public Band getBand(String name) {
		for (Band band : getBands()) {
			if (name.equals(band.getName())) {
				return band;
			}
		}
		
		return null;
	}
	
	public int getBandIndex(String bandName) {
		List<Band> bands = getBands();
		for (int i = 0; i < bands.size(); i++) {
			if (bandName.equals(bands.get(i).getName())) {
				return i;
			}
		}
		
		return -1;
	}

	public List<String> getBandNamesAfter(String bandName) {
		return getBandNamesAfter(getBandIndex(bandName));
	}
	
	public List<String> getBandNamesAfter(int index) {
		List<Band> bandAfters = getBandsAfter(index);
		List<String> bandNamesAfter = new ArrayList<String>();
		for (Band band : bandAfters) {
			bandNamesAfter.add(band.getName());
		}
		
		return bandNamesAfter;
	}

	public List<Band> getBandsAfter(String bandName) {
		return getBandsAfter(getBandIndex(bandName));
	}
	
	public List<Band> getBandsAfter(int index) {
		if (index == -1) {
			return new ArrayList<Band>();
		}
		
		List<Band> bands = getBands();
		List<Band> bandsAfter = new ArrayList<Band>();
		for (int i = index + 1; i < bands.size(); i++) {
			bandsAfter.add(bands.get(i));
		}
		
		return bandsAfter;
	}

    public int getGridRow(String bandName, int bandRow) {
        int gridRow = 0;
        for (Band band : getBands()) {
             if (!band.getName().equals(bandName)) {
                 gridRow += band.getRowCount();
             } else {
                 gridRow += bandRow;
                 break;
             }
        }
        return gridRow;
    }

    public void addGroup(ReportGroup group, int groupMask) {
        groupHeaderBands.add(new Band(GROUP_HEADER_BAND_NAME_PREFIX + group.getName()));
        groupFooterBands.add(0, new Band(GROUP_FOOTER_BAND_NAME_PREFIX + group.getName()));        
        if (groups == null) {
            groups = new ArrayList<ReportGroup>();
        }
        groups.add(group);
    }
	
	public void removeGroup(String groupName) {
        for (Iterator it = groups.iterator(); it.hasNext();) {
            ReportGroup group = (ReportGroup)it.next();
            if (group.getName().equals(groupName)) {
                it.remove();                
            }
        }
        for (Iterator it = groupHeaderBands.iterator(); it.hasNext(); ) {
            Band band =(Band)it.next();
            if  (band.getName().substring(GROUP_HEADER_BAND_NAME_PREFIX.length()).equals(groupName))  {
                it.remove();
                break;
            }
        }
        for (Iterator it = groupFooterBands.iterator(); it.hasNext(); ) {
            Band band =(Band)it.next();
            if  (band.getName().substring(GROUP_FOOTER_BAND_NAME_PREFIX.length()).equals(groupName))  {
                it.remove();
                break;
            }
        }
    }

    public void editGroup(String groupName, String newColumnName, boolean headerOnEveryPage, boolean newPageAfter) {
        for (ReportGroup group : groups) {
            if (group.getName().equals(groupName)) {
                group.setColumn(newColumnName);
                group.setHeaderOnEveryPage(headerOnEveryPage);
                group.setNewPageAfter(newPageAfter);
            }
        }        
    }

    public ReportGroup getGroup(String groupName) {
        for (ReportGroup group : groups) {
            if (group.getName().equals(groupName)) {
                return group;
            }
        }
        return null;
    }


    /**
	 * @return List with 'sorted' bands.
	 */
    public List<Band> getBands() {
        List<Band> bands = new ArrayList<Band>();
        bands.add(getPageHeaderBand());
        bands.add(headerBand);       
        bands.addAll(groupHeaderBands);
        bands.add(detailBand);
        bands.addAll(groupFooterBands);        
        bands.add(footerBand);
        bands.add(getPageFooterBand());
        
        return bands;
    }
    
    /**
     * Get bands that appear in document page (no header page and footer page)
     * @return list of bands that appear in document page 
     */
    public List<Band> getDocumentBands() {
        List<Band> bands = new ArrayList<Band>();
        bands.add(headerBand);        
        bands.addAll(groupHeaderBands);
        bands.add(detailBand);
        bands.addAll(groupFooterBands);        
        bands.add(footerBand);
        
        return bands;
    }

    public List<Band> getNotEmptyBands() {
        List<Band> bands = new ArrayList<Band>();
        List<Band> allBands = getBands();
        for (Band band : allBands) {
            if (band.getRowCount() > 0) {
                bands.add(band);
            }
        }

        return bands;
    }

    public int getRowCount() {
    	int rowCount = 0;
    	for (Band band : getBands()) {
    		rowCount += band.getRowCount();
    	}
    	
    	return rowCount;
    }
    
    public int getColumnCount() {
        for (Band band : getBands()) {
            int count = band.getColumnCount();
            if  (count > 0) {
                return count;
            }
        }
        return 0;
    }

    public void clear() {
        headerBand.clear();
        pageHeaderBand.clear();
        detailBand.clear();
        pageFooterBand.clear();
        footerBand.clear();
        
        for (Band band : groupHeaderBands) {
        	band.clear();
        }
        
        for (Band band : groupFooterBands) {
        	band.clear();
        }

        if (groups != null) {
            groups.clear();
        }
    }
    
    public void initBandsListenerList() {
        for (Band band : getBands()) {
            band.initListenerList();
        }
    }

    public static String[] getPageFormats() {
        return new String[] { A4, A3, A2, A1, A0, LETTER, LEGAL, LEDGER, TABLOID, CUSTOM };
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportLayout that = (ReportLayout) o;

        if (headerOnEveryPage != that.headerOnEveryPage) return false;
        if (showEmptyData != that.showEmptyData) return false;
        if (orientation != that.orientation) return false;
        if (reportType != that.reportType) return false;
        if (useSize != that.useSize) return false;
        if (columnsWidth != null ? !columnsWidth.equals(that.columnsWidth) : that.columnsWidth != null) return false;
        if (detailBand != null ? !detailBand.equals(that.detailBand) : that.detailBand != null) return false;
        if (footerBand != null ? !footerBand.equals(that.footerBand) : that.footerBand != null) return false;
        if (groupFooterBands != null ? !groupFooterBands.equals(that.groupFooterBands) : that.groupFooterBands != null)
            return false;
        if (groupHeaderBands != null ? !groupHeaderBands.equals(that.groupHeaderBands) : that.groupHeaderBands != null)
            return false;
        if (groups != null ? !groups.equals(that.groups) : that.groups != null) return false;
        if (headerBand != null ? !headerBand.equals(that.headerBand) : that.headerBand != null) return false;
        if (pageHeaderBand != null ? !pageHeaderBand.equals(that.pageHeaderBand) : that.pageHeaderBand != null) return false;
        if (pageFooterBand != null ? !pageFooterBand.equals(that.pageFooterBand) : that.pageFooterBand != null) return false;
        if (pageFormat != null ? !pageFormat.equals(that.pageFormat) : that.pageFormat != null) return false;
        if (templateName != null ? !templateName.equals(that.templateName) : that.templateName != null) return false;
        if (templateSheet != that.templateSheet) return false;
        if (sheetNames != null ? !sheetNames.equals(that.sheetNames) : that.sheetNames != null) return false;
        if (pagePadding != null ? !pagePadding.equals(that.pagePadding) : that.pagePadding != null) return false;
        if (backgroundImage != null ? !backgroundImage.equals(that.backgroundImage) : that.backgroundImage != null) return false;
        if (paperSize != null ? !paperSize.equals(that.paperSize) : that.paperSize != null) return false;
        if (i18nkeys != null && that.i18nkeys != null && (!i18nkeys.containsAll(that.i18nkeys) ||
                !that.i18nkeys.containsAll(i18nkeys))) return false;
        if (languages != null && that.languages != null && (!languages.containsAll(that.languages) ||
                !that.languages.containsAll(languages))) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (groups != null ? groups.hashCode() : 0);
        result = 31 * result + (columnsWidth != null ? columnsWidth.hashCode() : 0);
        result = 31 * result + (useSize ? 1 : 0);
        result = 31 * result + (headerBand != null ? headerBand.hashCode() : 0);
        result = 31 * result + (pageHeaderBand != null ? pageHeaderBand.hashCode() : 0);
        result = 31 * result + (pageFooterBand != null ? pageFooterBand.hashCode() : 0);
        result = 31 * result + (groupHeaderBands != null ? groupHeaderBands.hashCode() : 0);
        result = 31 * result + (detailBand != null ? detailBand.hashCode() : 0);
        result = 31 * result + (groupFooterBands != null ? groupFooterBands.hashCode() : 0);
        result = 31 * result + (footerBand != null ? footerBand.hashCode() : 0);
        result = 31 * result + orientation;
        result = 31 * result + reportType;
        result = 31 * result + (pageFormat != null ? pageFormat.hashCode() : 0);
        result = 31 * result + (templateName != null ? templateName.hashCode() : 0);
        result = 31 * result + templateSheet;
        result = 31 * result + (sheetNames != null ? sheetNames.hashCode() : 0);
        result = 31 * result + (headerOnEveryPage ? 1 : 0);
        result = 31 * result + (showEmptyData ? 1 : 0);
        result = 31 * result + (pagePadding != null ? pagePadding.hashCode() : 0);
        result = 31 * result + (backgroundImage != null ? backgroundImage.hashCode() : 0);
        result = 31 * result + (paperSize != null ? paperSize.hashCode() : 0);
        result = 31 * result + (i18nkeys != null ? i18nkeys.hashCode() : 0);
        result = 31 * result + (languages != null ? languages.hashCode() : 0);
        return result;
    }
    
    public Set<String> getFunctions() {
    	Set<String> functions = new HashSet<String>();
    	List<Band> bands = getBands();
    	for (Band band : bands) {
    		for (int i=0, rows = band.getRowCount(); i<rows; i++) {
    			for (int j=0, cols = band.getColumnCount(); j<cols;  j++) {
    				BandElement be = band.getElementAt(i, j);
    				if (be instanceof FunctionBandElement) {
    					FunctionBandElement fbe = (FunctionBandElement)be;
    					functions.add( fbe.getFunction() + "_" + fbe.getColumn());
    				}
    			}
    		}
    	}
    	return functions;
    }
    
    public Set<String> getFunctions(String bandName) {    	
    	Set<String> functions = new HashSet<String>();
    	List<Band> bands = getBands();
    	for (Band band : bands) {    		
			if (band.getName().equals(bandName)) {
				for (int i = 0, rows = band.getRowCount(); i < rows; i++) {
					for (int j = 0, cols = band.getColumnCount(); j < cols; j++) {
						BandElement be = band.getElementAt(i, j);
						if (be instanceof FunctionBandElement) {
							FunctionBandElement fbe = (FunctionBandElement) be;
							functions.add(fbe.getFunction() + "_" + fbe.getColumn());							
						}
					}
				}
			}
    	}
    	return functions;
    }
    
    public ReportBandElement getReportBandElement(String reportName) {
    	 List<Band> bands = getDocumentBands();
         for (Band band : bands) {
        	 for (int i=0; i<band.getRowCount(); i++) {
        		 for (int j=0; j<band.getColumnCount(); j++) {        			 
        			 BandElement be = band.getElementAt(i, j);
        			 String text = (be == null) ? "" : be.getText();        			 
        			 if (be instanceof ReportBandElement) {
        				 ReportBandElement rbe = (ReportBandElement)be;        				 
        				 if (rbe.getReport().getName().equals(reportName)) {
        					 return rbe; 
        				 }
        			 }
        		 }
        	 }
         }
         return null;
    }
    
    public ChartBandElement getChartBandElement(String chartName) {
   	    List<Band> bands = getDocumentBands();
        for (Band band : bands) {
       	 for (int i=0; i<band.getRowCount(); i++) {
       		 for (int j=0; j<band.getColumnCount(); j++) {
       			 BandElement be = band.getElementAt(i, j);
       			 if (be instanceof ChartBandElement) {
       				ChartBandElement cbe = (ChartBandElement)be;
       				 if (cbe.getChart().getName().equals(chartName)) {
       					 return cbe; 
       				 }
       			 }
       		 }
       	 }
        }
        return null;
   }
    
    /** Get keys for internationalized strings
     * 
     * @return list of keys for internationalized strings
     */
    public List<String> getI18nkeys() {
    	Collections.sort(i18nkeys, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {				
				return Collator.getInstance().compare(o1, o2);
			}
		});
		return i18nkeys;
	}

    /** Set keys for internationalized strings     
     * 
     * @param i18nkeys list of keys for internationalized strings
     */
	public void setI18nkeys(List<String> i18nkeys) {
		this.i18nkeys = i18nkeys;
	}

	 /** Get languages for internationalized strings
     * 
     * @return list of languages for internationalized strings
     */
	public List<I18nLanguage> getLanguages() {
		return languages;
	}

	 /** Set languages for internationalized strings     
     * 
     * @param languages list of languages for internationalized strings
     */
	public void setLanguages(List<I18nLanguage> languages) {
		this.languages = languages;
	}
	
	private Object readResolve() throws ObjectStreamException {
		if (i18nkeys == null) {
			i18nkeys = new ArrayList<String>();
		}
		if (languages == null) {
			languages = new ArrayList<I18nLanguage>();
		}
		return this;
	}
}
