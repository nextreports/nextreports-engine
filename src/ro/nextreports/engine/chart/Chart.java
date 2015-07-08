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
package ro.nextreports.engine.chart;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.awt.*;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.i18n.I18nLanguage;

/**
 * User: mihai.panaitescu
 * Date: 14-Dec-2009
 * Time: 15:49:26
 */
public class Chart implements Serializable {

    private static final long serialVersionUID = -3455484134680121149L;

    // label orientation
    public static transient final byte HORIZONTAL = 1;
    public static transient final byte VERTICAL = 2;
    public static transient final byte DIAGONAL = 3;
    public static transient final byte HALF_DIAGONAL = 4;

    // transparency
    public static transient final byte NONE_TRANSPARENCY = 1;
    public static transient final byte LOW_TRANSPARENCY = 2;
    public static transient final byte AVG_TRANSPARENCY = 3;
    public static transient final byte HIGH_TRANSPARENCY = 4;
    
    // grid line style
    public static transient final byte LINE_STYLE_LINE = 1;
    public static transient final byte LINE_STYLE_DOT = 2;
    public static transient final byte LINE_STYLE_DASH = 3;

    private String name;
    private String version;
    private ChartTitle title;
    private ChartType type;
    private Report report;
    private Color background;
    private List<Color> foregrounds;
    private String xColumn;
    private List<String> yColumns;
    private List<String> yColumnsLegends;
    private String yColumnQuery;
    private byte xorientation;
    private String xPattern;
    private Color xColor;
    private Color yColor;
    private Color xAxisColor;
    private Color yAxisColor;
    private byte transparency;
    private ChartTitle xLegend;
    private ChartTitle yLegend;
    private ChartTitle yDualLegend;
    private String yFunction;
    private Color xGridColor;
    private Color yGridColor;
    private Boolean xShowGrid;
    private Boolean yShowGrid;
    private Boolean xShowLabel;
    private Boolean yShowLabel;
    private Boolean startingFromZero;
    // showYValuesOnChart has meaning for image, not for flash
    private Boolean showYValuesOnChart;
    private Boolean showDualAxis;
    private Integer y2SeriesCount;
    private String yTooltipPattern;
    private String tooltipMessage;
    private Font font;
    private Font xLabelFont;
    private Font yLabelFont;
    private List<String> i18nkeys;
    private List<I18nLanguage> languages;
    private byte styleGridX;
    private byte styleGridY;

    public static transient Color[] COLORS = new Color[] {
            new Color(0, 0, 204),
            Color.RED,
            new Color(0, 204, 102),
            new Color(153, 0, 153),
            new Color(255, 200, 50),
            new Color(0, 200, 200),
            new Color(240, 240, 17),
            new Color(131, 76, 20),
            new Color(120, 128, 13),
            new Color(230, 80, 200)
    };
    
    public Chart() {
        // defaults
        this.title = new ChartTitle("");
        this.xLegend = new ChartTitle("");
        this.yLegend = new ChartTitle("");
        this.yDualLegend = new ChartTitle("");
        this.background = Color.WHITE;
        this.foregrounds = new ArrayList<Color>();
        this.xorientation = HORIZONTAL;
        this.transparency = AVG_TRANSPARENCY;
        this.xColor = Color.BLACK;
        this.yColor= Color.BLACK;
        this.xAxisColor = Color.BLACK;
        this.yAxisColor= Color.BLACK;
        this.xShowGrid = Boolean.TRUE;
        this.yShowGrid = Boolean.TRUE;
        this.xShowLabel = Boolean.TRUE;
        this.yShowLabel = Boolean.TRUE;
        this.showYValuesOnChart = Boolean.FALSE;     
        this.showDualAxis = Boolean.FALSE;
        this.startingFromZero = Boolean.FALSE;
        this.y2SeriesCount = 1;
        this.styleGridX = LINE_STYLE_LINE;
        this.styleGridY = LINE_STYLE_LINE;
        foregrounds.addAll(Arrays.asList(COLORS));        
        setType(new ChartType(ChartType.BAR, ChartType.STYLE_BAR_GLASS));
    }

    public Chart(ChartType type, Report report) {
        this();
        setType(type);
        setReport(report);
    }

    public ChartType getType() {
        return type;
    }

    public void setType(ChartType type) {
        if (type == null) {
            throw new IllegalArgumentException("Chart type cannot be null.");
        }
        this.type = type;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        if (report == null) {
            throw new IllegalArgumentException("Report cannot be null.");
        }
        this.report = report;
    }

    public ChartTitle getTitle() {
        return title;
    }

    public void setTitle(ChartTitle title) {
        this.title = title;
    }

    public Color getBackground() {
        return background;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public List<Color> getForegrounds() {
    	if (foregrounds.size() == 6) {
    		// from 4.1 to 4.2 added another 4 properties
    		foregrounds.add(new Color(240, 240, 17));
    		foregrounds.add(new Color(131, 76, 20));
    		foregrounds.add(new Color(120, 128, 13));
    		foregrounds.add(new Color(230, 80, 200));    	
    	}	
        return foregrounds;
    }

    public void setForegrounds(List<Color> foregrounds) {
        this.foregrounds = foregrounds;
    }

    public String getXColumn() {
        return xColumn;
    }

    public void setXColumn(String xColumn) {
        this.xColumn = xColumn;
    }

    public List<String> getYColumns() {
        if (yColumns == null) {
            yColumns = new ArrayList<String>();
        }
        return yColumns;
    }

    public void setYColumns(List<String> yColumns) {
        List<String> list = new ArrayList<String>();
        for (String s : yColumns) {
            if (s!=null) {
                list.add(s);
            }
        }
        this.yColumns = list;
    }
        
    public String getYColumnQuery() {
		return yColumnQuery;
	}

	public void setYColumnQuery(String yColumnQuery) {
		this.yColumnQuery = yColumnQuery;
	}

	public List<String> getYColumnsLegends() {
        if (yColumnsLegends == null) {
            yColumnsLegends = new ArrayList<String>();
        }
        return yColumnsLegends;
    }

    public void setYColumnsLegends(List<String> yColumnsLegends) {
        List<String> list = new ArrayList<String>();
        for (String s : yColumnsLegends) {
            if (s!=null) {
                list.add(s);
            }
        }
        this.yColumnsLegends = list;
    }

    public String getName() {
        return name;
    }

    public String getBaseName() {
        if (name.endsWith(".chart")) {
            return name.substring(0, name.length()-6);
        } else {
            return name;
        }
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public byte getXorientation() {
        return xorientation;
    }

    public void setXorientation(byte xorientation) {
        this.xorientation = xorientation;
    }

    public Color getXColor() {        
        return xColor;
    }

    public void setXColor(Color xColor) {
        this.xColor = xColor;
    }

    public Color getYColor() {
        return yColor;
    }

    public void setYColor(Color yColor) {
        this.yColor = yColor;
    }        

    public Color getxAxisColor() {
    	if (xAxisColor == null) {
    		xAxisColor = Color.BLACK;
    	}
		return xAxisColor;
	}

	public void setxAxisColor(Color xAxisColor) {
		this.xAxisColor = xAxisColor;
	}

	public Color getyAxisColor() {
		if (yAxisColor == null) {
			yAxisColor = Color.BLACK;
		}
		return yAxisColor;
	}

	public void setyAxisColor(Color yAxisColor) {
		this.yAxisColor = yAxisColor;
	}

	public byte getTransparency() {
        return transparency;
    }

    public void setTransparency(byte transparency) {
        this.transparency = transparency;
    }
        
    public String getTooltipMessage() {
		return tooltipMessage;
	}

	public void setTooltipMessage(String tooltipMessage) {
		this.tooltipMessage = tooltipMessage;
	}

	public ChartTitle getXLegend() {
        return xLegend;
    }

    public void setXLegend(ChartTitle xLegend) {
        this.xLegend = xLegend;
    }

    public ChartTitle getYLegend() {
        return yLegend;
    }

    public void setYLegend(ChartTitle yLegend) {
        this.yLegend = yLegend;
    }
        
    public ChartTitle getyDualLegend() {
		return yDualLegend;
	}

	public void setyDualLegend(ChartTitle yDualLegend) {
		this.yDualLegend = yDualLegend;
	}

	public String getYFunction() {
        return yFunction;
    }

    public void setYFunction(String yFunction) {
        this.yFunction = yFunction;
    }

    public String getXPattern() {
        return xPattern;
    }

    public void setXPattern(String xPattern) {
        this.xPattern = xPattern;
    }

    public Color getXGridColor() {
        return xGridColor;
    }

    public void setXGridColor(Color xGridColor) {
        this.xGridColor = xGridColor;
    }

    public Color getYGridColor() {
        return yGridColor;
    }

    public void setYGridColor(Color yGridColor) {
        this.yGridColor = yGridColor;
    }

    public Boolean getXShowGrid() {
        return xShowGrid;
    }

    public void setXShowGrid(Boolean xShowGrid) {
        this.xShowGrid = xShowGrid;
    }

    public Boolean getYShowGrid() {
        return yShowGrid;
    }

    public void setYShowGrid(Boolean yShowGrid) {
        this.yShowGrid = yShowGrid;
    }

    public Boolean getXShowLabel() {
        return xShowLabel;
    }

    public void setXShowLabel(Boolean xShowLabel) {
        this.xShowLabel = xShowLabel;
    }

    public Boolean getYShowLabel() {
        return yShowLabel;
    }

    public void setYShowLabel(Boolean yShowLabel) {
        this.yShowLabel = yShowLabel;
    }        

    public Boolean getShowYValuesOnChart() {
		return showYValuesOnChart;
	}

	public void setShowYValuesOnChart(Boolean showYValuesOnChart) {
		this.showYValuesOnChart = showYValuesOnChart;
	}
				
	public Boolean getShowDualAxis() {
		return showDualAxis;
	}

	public void setShowDualAxis(Boolean showDualAxis) {
		this.showDualAxis = showDualAxis;
	}
		
	public Integer getY2SeriesCount() {
		return y2SeriesCount;
	}

	public void setY2SeriesCount(Integer y2SeriesCount) {
		this.y2SeriesCount = y2SeriesCount;
	}
	
	public byte getStyleGridX() {
		return styleGridX;
	}
	
	public void setStyleGridX(byte styleGridX) {
		this.styleGridX = styleGridX;
	}
	
	public byte getStyleGridY() {
		return styleGridY;
	}
	
	public void setStyleGridY(byte styleGridY) {
		this.styleGridY = styleGridY;
	}
	
	public Boolean getStartingFromZero() {
		return startingFromZero;
	}

	public void setStartingFromZero(Boolean startingFromZero) {
		this.startingFromZero = startingFromZero;
	}

	public String getYTooltipPattern() {
		return yTooltipPattern;
	}

	public void setYTooltipPattern(String yTooltipPattern) {
		this.yTooltipPattern = yTooltipPattern;
	}
	
	public Font getFont() {
        if (font == null) {
            return getDefaultFont();
        }
        return font;
    }

    public void setFont(Font font) {    	
        this.font = font;
    }
    
    public Font getXLabelFont() {
        if (xLabelFont == null) {
            return getDefaultFont();
        }
        return xLabelFont;
    }

    public void setXLabelFont(Font xLabelFont) {    	
        this.xLabelFont = xLabelFont;
    }
    
    public Font getYLabelFont() {
        if (yLabelFont == null) {
            return getDefaultFont();
        }
        return yLabelFont;
    }

    public void setYLabelFont(Font yLabelFont) {    	
        this.yLabelFont = yLabelFont;
    }
	
	private Font getDefaultFont() {
		return new Font("SansSerif", Font.PLAIN, 12);
	}

	public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chart chart = (Chart) o;

        if (transparency != chart.transparency) return false;        
        if (xorientation != chart.xorientation) return false;
        if (background != null ? !background.equals(chart.background) : chart.background != null) return false;
        if (foregrounds != null ? !foregrounds.equals(chart.foregrounds) : chart.foregrounds != null) return false;
        if (name != null ? !name.equals(chart.name) : chart.name != null) return false;
        if (report != null ? !report.equals(chart.report) : chart.report != null) return false;
        if (title != null ? !title.equals(chart.title) : chart.title != null) return false;
        if (type != null ? !type.equals(chart.type) : chart.type != null) return false;
        if (version != null ? !version.equals(chart.version) : chart.version != null) return false;
        if (xColor != null ? !xColor.equals(chart.xColor) : chart.xColor != null) return false;
        if (xColumn != null ? !xColumn.equals(chart.xColumn) : chart.xColumn != null) return false;
        if (xGridColor != null ? !xGridColor.equals(chart.xGridColor) : chart.xGridColor != null) return false;
        if (xLegend != null ? !xLegend.equals(chart.xLegend) : chart.xLegend != null) return false;
        if (xPattern != null ? !xPattern.equals(chart.xPattern) : chart.xPattern != null) return false;
        if (xShowGrid != null ? !xShowGrid.equals(chart.xShowGrid) : chart.xShowGrid != null) return false;
        if (xShowLabel != null ? !xShowLabel.equals(chart.xShowLabel) : chart.xShowLabel != null) return false;
        if (startingFromZero != null ? !startingFromZero.equals(chart.startingFromZero) : chart.startingFromZero != null) return false;
        if (yColor != null ? !yColor.equals(chart.yColor) : chart.yColor != null) return false;
        if (yColumns != null ? !yColumns.equals(chart.yColumns) : chart.yColumns != null) return false;
        if (yColumnsLegends != null ? !yColumnsLegends.equals(chart.yColumnsLegends) : chart.yColumnsLegends != null)
            return false;
        if (yColumnQuery != null ? !yColumnQuery.equals(chart.yColumnQuery) : chart.yColumnQuery != null) return false;
        if (yFunction != null ? !yFunction.equals(chart.yFunction) : chart.yFunction != null) return false;
        if (yGridColor != null ? !yGridColor.equals(chart.yGridColor) : chart.yGridColor != null) return false;
        if (yLegend != null ? !yLegend.equals(chart.yLegend) : chart.yLegend != null) return false;
        if (yDualLegend != null ? !yDualLegend.equals(chart.yDualLegend) : chart.yDualLegend != null) return false;
        if (yShowGrid != null ? !yShowGrid.equals(chart.yShowGrid) : chart.yShowGrid != null) return false;
        if (yShowLabel != null ? !yShowLabel.equals(chart.yShowLabel) : chart.yShowLabel != null) return false;
        if (showYValuesOnChart != null ? !showYValuesOnChart.equals(chart.showYValuesOnChart) : chart.showYValuesOnChart != null) return false;
        if (showDualAxis != null ? !showDualAxis.equals(chart.showDualAxis) : chart.showDualAxis != null) return false;
        if (y2SeriesCount != null ? !y2SeriesCount.equals(chart.y2SeriesCount) : chart.y2SeriesCount != null) return false;
        if (yTooltipPattern != null ? !yTooltipPattern.equals(chart.yTooltipPattern) : chart.yTooltipPattern != null) return false;
        if (font != null ? !font.equals(chart.font) : chart.font != null) return false;
        if (xLabelFont != null ? !xLabelFont.equals(chart.xLabelFont) : chart.xLabelFont != null) return false;
        if (yLabelFont != null ? !yLabelFont.equals(chart.yLabelFont) : chart.yLabelFont != null) return false;
        if (xAxisColor != null ? !xAxisColor.equals(chart.xAxisColor) : chart.xAxisColor != null) return false;
        if (yAxisColor != null ? !yAxisColor.equals(chart.yAxisColor) : chart.yAxisColor != null) return false;
        if (tooltipMessage != null ? !tooltipMessage.equals(chart.tooltipMessage) : chart.tooltipMessage != null) return false;
        if (i18nkeys != null ? !i18nkeys.equals(chart.i18nkeys) : chart.i18nkeys != null) return false;
        if (languages != null ? !languages.equals(chart.languages) : chart.languages != null) return false;     
        if (styleGridX != chart.styleGridX) return false;
        if (styleGridY != chart.styleGridY) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (report != null ? report.hashCode() : 0);
        result = 31 * result + (background != null ? background.hashCode() : 0);
        result = 31 * result + (foregrounds != null ? foregrounds.hashCode() : 0);
        result = 31 * result + (xColumn != null ? xColumn.hashCode() : 0);
        result = 31 * result + (yColumns != null ? yColumns.hashCode() : 0);
        result = 31 * result + (yColumnsLegends != null ? yColumnsLegends.hashCode() : 0);
        result = 31 * result + (yColumnQuery != null ? yColumnQuery.hashCode() : 0);
        result = 31 * result + (int) xorientation;
        result = 31 * result + (xPattern != null ? xPattern.hashCode() : 0);
        result = 31 * result + (xColor != null ? xColor.hashCode() : 0);
        result = 31 * result + (yColor != null ? yColor.hashCode() : 0);
        result = 31 * result + (xAxisColor != null ? xAxisColor.hashCode() : 0);
        result = 31 * result + (yAxisColor != null ? yAxisColor.hashCode() : 0);
        result = 31 * result + (int) transparency;
        result = 31 * result + (xLegend != null ? xLegend.hashCode() : 0);
        result = 31 * result + (yLegend != null ? yLegend.hashCode() : 0);
        result = 31 * result + (yDualLegend != null ? yDualLegend.hashCode() : 0);
        result = 31 * result + (yFunction != null ? yFunction.hashCode() : 0);
        result = 31 * result + (xGridColor != null ? xGridColor.hashCode() : 0);
        result = 31 * result + (yGridColor != null ? yGridColor.hashCode() : 0);
        result = 31 * result + (xShowGrid != null ? xShowGrid.hashCode() : 0);
        result = 31 * result + (startingFromZero != null ? startingFromZero.hashCode() : 0);
        result = 31 * result + (yShowGrid != null ? yShowGrid.hashCode() : 0);
        result = 31 * result + (xShowLabel != null ? xShowLabel.hashCode() : 0);
        result = 31 * result + (yShowLabel != null ? yShowLabel.hashCode() : 0);
        result = 31 * result + (showYValuesOnChart != null ? showYValuesOnChart.hashCode() : 0);
        result = 31 * result + (showDualAxis != null ? showDualAxis.hashCode() : 0);
        result = 31 * result + (y2SeriesCount != null ? y2SeriesCount.hashCode() : 0);
        result = 31 * result + (yTooltipPattern != null ? yTooltipPattern.hashCode() : 0);
        result = 31 * result + (font != null ? font.hashCode() : 0);
        result = 31 * result + (xLabelFont != null ? xLabelFont.hashCode() : 0);
        result = 31 * result + (yLabelFont != null ? yLabelFont.hashCode() : 0);
        result = 31 * result + (tooltipMessage != null ? tooltipMessage.hashCode() : 0);
        result = 31 * result + (i18nkeys != null ? i18nkeys.hashCode() : 0);
        result = 31 * result + (languages != null ? languages.hashCode() : 0);
        result = 31 * result + styleGridX;
        result = 31 * result + styleGridY;
        return result;
    }

    public String toString() {
        return "Chart{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", title=" + title +
                ", type=" + type +
                ", report=" + report +
                ", background=" + background +
                ", foregrounds=" + foregrounds +
                ", xColumn='" + xColumn + '\'' +
                ", yColumns='" + yColumns + '\'' +
                ", yColumnsLegends='" + yColumnsLegends + '\'' +
                ", yColumnQuery='" + yColumnQuery + '\'' +
                ", xorientation=" + xorientation +
                ", xPattern='" + xPattern + '\'' +
                ", xColor=" + xColor +
                ", yColor=" + yColor +
                ", xAxisColor=" + xAxisColor +
                ", yAxisColor=" + yAxisColor +
                ", transparency=" + transparency +
                ", xLegend=" + xLegend +
                ", yLegend=" + yLegend +
                ", yDualLegend=" + yDualLegend +
                ", yFunction='" + yFunction + '\'' +
                ", xGridColor=" + xGridColor +
                ", yGridColor=" + yGridColor +
                ", xShowGrid=" + xShowGrid +
                ", yShowGrid=" + yShowGrid +
                ", xShowLabel=" + xShowLabel +
                ", yShowLabel=" + yShowLabel +
                ", styleGridX=" + styleGridX +
                ", styleGridY=" + styleGridY +
                ", showYValuesOnChart=" + showYValuesOnChart +
                ", showDualAxis=" + showDualAxis +
                ", y2SeriesCount=" + y2SeriesCount +
                ", tooltipMessage=" + tooltipMessage +
                ", yTooltipPattern=" + yTooltipPattern +
                ", startingFromZero=" + startingFromZero +
                ", font=" + font +
                ", xLabelFont=" + xLabelFont +
                ", yLabelFont=" + yLabelFont +
                ", i18nkeys='" + i18nkeys + '\'' +
                ", languages='" + languages + '\'' +
                '}';
    }
    
    /** Get keys for internationalized strings
     * 
     * @return list of keys for internationalized strings
     */
    public List<String> getI18nkeys() {
    	if (i18nkeys == null) {
    		return new ArrayList<String>();
    	}
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
