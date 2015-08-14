
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

import java.awt.Color;
import java.awt.Font;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.nextreports.engine.exporter.exception.NoDataFoundException;
import ro.nextreports.engine.exporter.util.function.AbstractGFunction;
import ro.nextreports.engine.exporter.util.function.FunctionFactory;
import ro.nextreports.engine.exporter.util.function.FunctionUtil;
import ro.nextreports.engine.exporter.util.function.GFunction;
import ro.nextreports.engine.i18n.I18nUtil;
import ro.nextreports.engine.queryexec.QueryException;
import ro.nextreports.engine.queryexec.QueryResult;
import ro.nextreports.engine.util.ColorUtil;
import ro.nextreports.engine.util.ObjectCloner;
import ro.nextreports.engine.util.StringUtil;

/* 
 * @author Mihai Dinca-Panaitescu 
 */
public class JsonHTML5Exporter implements ChartExporter {

    private OutputStream out;
    private QueryResult result;
    private Chart chart;
    private PrintStream stream;
    // default background color for a flash chart (if none is set)
    private final Color DEFAULT_BACKGROUND = new Color(248, 248, 216);
    private Map<String, Object> parameterValues;
    private String drillFunction;
    private NextChart nc;
    private String language;

    // markup for clicked value inside onClick javascript function
    private static final String CLICKED_VALUE = "#val";            
    
    public JsonHTML5Exporter(Map<String, Object> parameterValues, QueryResult result, OutputStream out,
                        Chart chart, String drillFunction, String language) {
        this.parameterValues = parameterValues;
        this.result = result;
        this.out = out;
        this.chart = chart;
        this.drillFunction = drillFunction;
        this.language = language;
        this.nc = new NextChart();
    }

    public boolean export() throws QueryException, NoDataFoundException {
        testForData();

        initExport();

        String json = createHTML5Chart();   
        System.out.println(json);
        stream.print(json);

        return true;
    }

    private void testForData() throws QueryException, NoDataFoundException {
        // for procedure call we do not know the row count (is -1)
        if (this.out == null || result == null
                || result.getColumnCount() <= 0
                || result.getRowCount() == 0) {
            throw new NoDataFoundException();
        }
    }

    protected void initExport() throws QueryException {
        stream = createPrintStream();
    }

    protected PrintStream createPrintStream() throws QueryException {
        try {
            return new PrintStream(out, false, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new QueryException(e);
        }
    }

    private NextChartTitle createTitle(ChartTitle chartTitle) {
        NextChartTitle nct = new NextChartTitle(StringUtil.getI18nString(chartTitle.getTitle(), I18nUtil.getLanguageByName(chart, language)));
        Font font = chartTitle.getFont();
        
        if (chartTitle.getColor() !=  null) {
        	nct.setColor(ColorUtil.getHexColor(chartTitle.getColor()));
        }
        nct.setFont(createFont(font));
        
        byte align = chartTitle.getAlignment();
        if (align == ChartTitle.LEFT_ALIGNMENT) {
            nct.setAlignment(NextChart.Alignment.left);
        } else if (align == ChartTitle.CENTRAL_ALIGNMENT) {
        	nct.setAlignment(NextChart.Alignment.center);
        } else if (align == ChartTitle.RIGHT_ALIGNMENT) {
        	nct.setAlignment(NextChart.Alignment.right);
        }
        return nct;        
    }
    
    private NextChartFont createFont(Font font) {
    	 String weight = "normal";
         if (font.isBold()) {
             weight = "bold";
         }
         if (font.isItalic()) {
             weight += " italic";
         }
         return new NextChartFont(weight, font.getSize(), font.getFamily());
    }

    private String createHTML5Chart() throws QueryException {
    	
    	setType();
    	setStyle();
    	setBackground();
    	setLabelOrientation(chart.getXorientation());
        setTitle();
                
        if (drillFunction != null) {               	
            nc.setOnClick(drillFunction);        	
        }             
        
        nc.setAlpha(getAlpha(chart.getTransparency()));
        
        setGridStyle();
				
        if (chart.getxAxisColor() != null) {
        	nc.setColorXaxis(ColorUtil.getHexColor(chart.getxAxisColor()));
        }
        if (chart.getyAxisColor() != null) {
        	nc.setColorYaxis(ColorUtil.getHexColor(chart.getyAxisColor()));
        }
		boolean showGridX = chart.getXShowGrid() == null ? false : chart.getXShowGrid();
	    boolean showGridY = chart.getYShowGrid() == null ? false :chart.getYShowGrid();
		nc.setShowGridX(showGridX);
		nc.setShowGridY(showGridY);
		if (chart.getXGridColor() != null) {
			nc.setColorGridX(ColorUtil.getHexColor(chart.getXGridColor()));
		}
		if (chart.getYGridColor() != null) {
			nc.setColorGridY(ColorUtil.getHexColor(chart.getYGridColor()));
		}
		if (chart.getShowDualAxis() != null) {
			nc.setDualYaxis(chart.getShowDualAxis());
			if (chart.getY2SeriesCount() != null) {
				nc.setY2Count(chart.getY2SeriesCount());
			}
		}
		
		// todo : customize tickCount?
		nc.setTickCount(5);
		
		if ((chart.getTooltipMessage() != null) && ! chart.getTooltipMessage().trim().isEmpty()) {
			String msg = StringUtil.getI18nStringMultiple(chart.getTooltipMessage(), I18nUtil.getLanguageByName(chart, language));
			nc.setMessage(msg);
		}
		
		boolean startingFromZero = chart.getStartingFromZero() == null ? false : chart.getStartingFromZero();
		nc.setStartingFromZero(startingFromZero);

        boolean showXLabel = chart.getXShowLabel() == null ? false : chart.getXShowLabel();
        boolean showYLabel = chart.getYShowLabel() == null ? false : chart.getYShowLabel();
        nc.setShowTicks(showYLabel);
        if (showXLabel) {
        	NextChartAxis xData = new NextChartAxis();
        	xData.setColor(ColorUtil.getHexColor(chart.getXColor()));		
        	xData.setFont(createFont(chart.getXLabelFont()));
        	nc.setxData(xData);
        }
        if (showYLabel) {
			NextChartAxis yData = new NextChartAxis();
			yData.setColor(ColorUtil.getHexColor(chart.getYColor()));
			yData.setFont(createFont(chart.getYLabelFont()));
			nc.setyData(yData);
        }
                       
        String yTooltipPattern = chart.getYTooltipPattern();
        if (yTooltipPattern != null) {        	
        	DecimalFormat df = new DecimalFormat(yTooltipPattern);
        	int decimals = df.getMinimumFractionDigits();
        	char decimalSeparator = df.getDecimalFormatSymbols().getDecimalSeparator(); 
        	char thousandSeparator = df.getDecimalFormatSymbols().getGroupingSeparator();
        	NextNumberFormat tooltipPattern = new NextNumberFormat();
        	tooltipPattern.setDecimals(decimals);
        	tooltipPattern.setDecimalSeparator(String.valueOf(decimalSeparator));
        	tooltipPattern.setThousandSeparator(String.valueOf(thousandSeparator));
        	nc.setTooltipPattern(tooltipPattern);
        }	
        
        
        boolean isCombo = ChartType.isCombo(chart.getType().getType());        
        
        setLegends(isCombo);
        setColors(isCombo);
        
        List<List<Number>> dataList = new ArrayList<List<Number>>();
        int size = chart.getYColumns().size();
        if (isCombo) {
        	// last column is line
        	size = chart.getYColumns().size()-1;
        	if (size > 0) {
        		List<List<Number>> lineDataList = new ArrayList<List<Number>>();        	
        		lineDataList.add(new ArrayList<Number>());    
        		nc.setLineData(lineDataList);
        	} else {
        		// is only one y column, put it to data (we cannot have a combo)        		
                dataList.add(new ArrayList<Number>());                
        	}
        }        
        for (int i=0; i<size; i++) {
        	dataList.add(new ArrayList<Number>());
        }
                
        nc.setData(dataList);        
        nc.setLabels(new ArrayList<String>());
        
        createChart(showXLabel, showYLabel);
        
        return nc.toJson();
    }
    
    private String convertGridStyleFromByte(byte style) {
    	String styleGrid = "line";
    	switch (style) {
    		case Chart.LINE_STYLE_DOT:
    			styleGrid = "dot";
    			break;
    		case Chart.LINE_STYLE_DASH:
    			styleGrid = "dash";
    			break;
    		case Chart.LINE_STYLE_LINE:
    		default:
    			styleGrid = "line";
    	}
    	return styleGrid;    	
    }
    
    private void setGridStyle() {    	
    	nc.setStyleGridX(convertGridStyleFromByte(chart.getStyleGridX()));
        nc.setStyleGridY(convertGridStyleFromByte(chart.getStyleGridY()));
    }

      
    private void createChart(boolean showXLabel, boolean showYLabel) throws QueryException {
        Number min = Double.MAX_VALUE;
        Number max = Double.MIN_VALUE;
        int row = 0;
        Object previous = null;
        String xColumn = chart.getXColumn();
        String xPattern = chart.getXPattern();
        String lastXValue = "";
       
        int chartsNo = chart.getYColumns().size();  
        boolean isStacked = nc.getType().equals(NextChart.Type.stackedbar) || nc.getType().equals(NextChart.Type.hstackedbar);         
        
        GFunction[] functions = new GFunction[chartsNo];     
        for (int i = 0; i < chartsNo; i++) {
            functions[i] = FunctionFactory.getFunction(chart.getYFunction());
        }    
        int groups = 1;
        HashMap<Integer, String> infoLabels = new HashMap<Integer, String>();
        List<String> categories = new ArrayList<String>();
        while (result.hasNext()) {        	
            Object[] objects = new Object[chartsNo];
            Number[] computedValues = new Number[chartsNo];            
            for (int i = 0; i < chartsNo; i++) {
                if (chart.getYColumns().get(i) != null) {
                    objects[i] = result.nextValue(chart.getYColumns().get(i));
                    Number value = null;
                    String sv = null;
                    if (objects[i] instanceof Number) {
                        value = (Number) objects[i];
                    } else if (objects[i] != null) {
                    	if (ChartType.BUBBLE == chart.getType().getType()) {
                    		sv = (String)objects[i];
                    	} 
                    	value = 1;                    	
                    }
                    if (value == null) {
                    	value = 0;
                    }
                    // open flash chart bug:
                    // if value is of type BigDecimal and chart is StackedBarChart : json has values between "" 
                    // and bars are drawn over the title (a good stackedbarchart has not the values between "")
                    // curiously all other chart types have values between ""
                    // so here we assure that values are not BigDecimals
                    computedValues[i] =  value.doubleValue();  
                    if (sv != null) {
                    	categories.add(sv);
                    }
                }
            }

            Object xValue;
            if (row == 0) {
                xValue = result.nextValue(xColumn);
                lastXValue = getStringValue(xColumn, xPattern);
            } else {
                xValue = previous;
            }
            Object newXValue = result.nextValue(xColumn);

            boolean add = false;
            // no function : add the value
            if (AbstractGFunction.NOOP.equals(functions[0].getName())) {
                lastXValue = getStringValue(xColumn, xPattern);
                add = true;

                // compute function
            } else {
                boolean equals = FunctionUtil.parameterEquals(xValue, newXValue);
                if (equals) {
                    for (int i = 0; i < chartsNo; i++) {
                        functions[i].compute(objects[i]);
                    }
                } else {
                    for (int i = 0; i < chartsNo; i++) {
                        add = true;
                        computedValues[i] = (Number) functions[i].getComputedValue();
                        functions[i].reset();
                        functions[i].compute(objects[i]);
                    }
                }
            }

            if (add) {
                //System.out.println("compValue="+computedValue);
                Number sum = 0;
                for (int i = 0; i < chartsNo; i++) {
                    addValue(computedValues[i], lastXValue, i);                    
                    if (!isStacked) {
                        min = Math.min(min.doubleValue(), computedValues[i].doubleValue());
                        max = Math.max(max.doubleValue(), computedValues[i].doubleValue());
                    } else {
                        sum = sum.doubleValue() + computedValues[i].doubleValue();                        
                    }
                }
                if (isStacked) {
                    min = 0;
                    max = Math.max(max.doubleValue(), sum.doubleValue());
                }

                infoLabels.put(groups, lastXValue);
                groups++;
                //System.out.println("add label : " + lastXValue + "  g="+ groups);
                lastXValue = getStringValue(xColumn, xPattern);

            }
            row++;
            previous = newXValue;
        }        

        // last group
        if (!AbstractGFunction.NOOP.equals(functions[0].getName())) {
            Number sum = 0;
            for (int i = 0; i < chartsNo; i++) {
                Number value = (Number) functions[i].getComputedValue();
                addValue(value, lastXValue, i);                
                if (!isStacked) {
                    min = Math.min(min.doubleValue(), value.doubleValue());
                    max = Math.max(max.doubleValue(), value.doubleValue());
                } else {
                    sum = sum.doubleValue() + value.doubleValue();
                }
            }
            if (isStacked) {
                min = 0;
                max = Math.max(max.doubleValue(), sum.doubleValue());
            }

            infoLabels.put(groups, lastXValue);
        } else {
            groups--;
        }               

		// info labels		
		for (int i = 1; i <= groups; i++) {
			String text = infoLabels.get(i);				
			nc.getLabels().add(text);			
			nc.setShowLabels(showXLabel);			
		}
		
		if (!categories.isEmpty()) {
			nc.setCategories(categories);
		}
		
    }       
   

    private void addValue(Number value, String text, int position) {        	          
        if (ChartType.isCombo(chart.getType().getType())) {
        	if ((position == nc.getData().size()) && (nc.getData().size() > 1)) {
        		nc.getLineData().get(0).add(value); 
        	} else {
        		nc.getData().get(position).add(value);  
        	}
        } else {
        	nc.getData().get(position).add(value);  
        	//System.out.println("add " + position + "  value=" + value);
        }
    }
    
    private void setTitle() {
    	ChartTitle chartTitle = ObjectCloner.silenceDeepCopy(chart.getTitle());    	
        String title = replaceParameters(chartTitle.getTitle());
        chartTitle.setTitle(title);
        if (!isEmpty(title)) {
        	nc.setTitle(createTitle(chartTitle));
        }
    }
   

    private void setBackground() {
        if (chart.getBackground() != null) {
        	nc.setBackground(ColorUtil.getHexColor(chart.getBackground()));
        }
    }  
    
    private void setColors(boolean isCombo) {
    	List<String> colors = new ArrayList<String>();
    	if (ChartType.PIE == chart.getType().getType()) {
    		for (Color color : chart.getForegrounds()) {
    			if (color != null) {
    				colors.add(ColorUtil.getHexColor(color));
    			}
            }    
    	} else {
    		int size = chart.getYColumns().size();
			if (isCombo) {
				if (size > 1) {
					size--;
					List<String> lineColorList = new ArrayList<String>();
					lineColorList.add(ColorUtil.getHexColor(chart.getForegrounds().get(size)));
					nc.setLineColor(lineColorList);
				}
			}
	    	for (int i=0; i < size; i++) {
	    		colors.add(ColorUtil.getHexColor(chart.getForegrounds().get(i)));	    		
	    	}     
    	}
		nc.setColor(colors);
    }
   
    private void setType() {
        byte type = chart.getType().getType();        
        if ((ChartType.BAR == type) || (ChartType.BAR_COMBO == type)) {
            nc.setType(NextChart.Type.bar);
        } else if (ChartType.NEGATIVE_BAR == type) {
        	nc.setType(NextChart.Type.nbar);  	
        } else if (ChartType.HORIZONTAL_BAR == type) {
        	nc.setType(NextChart.Type.hbar);
        } else if ((ChartType.STACKED_BAR == type) || (ChartType.STACKED_BAR_COMBO == type)) {
        	nc.setType(NextChart.Type.stackedbar);
        } else if (ChartType.HORIZONTAL_STACKED_BAR == type) {
        	nc.setType(NextChart.Type.hstackedbar);	
        } else if (ChartType.PIE == type) {
        	nc.setType(NextChart.Type.pie);
        } else if (ChartType.LINE == type) {
            nc.setType(NextChart.Type.line);
        } else if (ChartType.AREA == type) {        	
        	nc.setType(NextChart.Type.area);
        } else if (ChartType.BUBBLE == type) {        	
        	nc.setType(NextChart.Type.bubble);
        }                        
    }   
    
	private void setStyle() {
		byte style = chart.getType().getStyle();
		switch (style) {
		case ChartType.STYLE_BAR_GLASS:
			nc.setStyle(NextChart.Style.glass);
			break;
		case ChartType.STYLE_BAR_CYLINDER:
			nc.setStyle(NextChart.Style.cylinder);
			break;
		case ChartType.STYLE_BAR_PARALLELIPIPED:
			nc.setStyle(NextChart.Style.parallelepiped);
			break;
		case ChartType.STYLE_BAR_DOME:
			nc.setStyle(NextChart.Style.dome);
			break;
		case ChartType.STYLE_LINE_DOT_SOLID:
			nc.setStyle(NextChart.Style.soliddot);
			break;
		case ChartType.STYLE_LINE_DOT_HOLLOW:
			nc.setStyle(NextChart.Style.hollowdot);
			break;
		case ChartType.STYLE_LINE_DOT_ANCHOR:
			nc.setStyle(NextChart.Style.anchordot);
			break;
		case ChartType.STYLE_LINE_DOT_BOW:
			nc.setStyle(NextChart.Style.bowdot);
			break;	
		case ChartType.STYLE_LINE_DOT_STAR:
			nc.setStyle(NextChart.Style.stardot);
			break;
		default:
			nc.setStyle(NextChart.Style.normal);
			break;
		}
	}
	
	private void setLegends(boolean isCombo) {		
		if ((chart.getYColumnsLegends() != null) && (chart.getYColumnsLegends().size() > 0) && 
			!isEmpty(chart.getYColumnsLegends().get(0))) {			
			List<String> list = new ArrayList<String>();
			int size = chart.getYColumnsLegends().size();
			if (chart.getYColumnsLegends().size() > chart.getYColumns().size()) {
				size = chart.getYColumns().size();
			}
			if (isCombo) {				
				if (size > 1) {
					size--;
					List<String> lineList = new ArrayList<String>();
					lineList.add(StringUtil.getI18nString(replaceParameters(chart.getYColumnsLegends().get(size)),I18nUtil.getLanguageByName(chart, language)));					
					nc.setLineLegend(lineList);
				}
			}
			for (int i=0; i<size; i++) {
				String s = chart.getYColumnsLegends().get(i);
				list.add(StringUtil.getI18nString(replaceParameters(s),I18nUtil.getLanguageByName(chart, language)));
			}			
			nc.setLegend(list);
			
			
		}		
				
		if ((chart.getXLegend() != null) && !isEmpty(chart.getXLegend().getTitle())) {
			String xLeg = StringUtil.getI18nString(replaceParameters(chart.getXLegend().getTitle()), I18nUtil.getLanguageByName(chart, language));
			NextChartLegend xLegend = new NextChartLegend(xLeg);
			xLegend.setColor(ColorUtil.getHexColor(chart.getXLegend().getColor()));
			xLegend.setFont(createFont(chart.getXLegend().getFont()));
			nc.setxLegend(xLegend);
		}
		if ((chart.getYLegend() != null) && !isEmpty(chart.getYLegend().getTitle()))  {
			String yLeg = StringUtil.getI18nString(replaceParameters(chart.getYLegend().getTitle()), I18nUtil.getLanguageByName(chart, language));
			NextChartLegend yLegend = new NextChartLegend(yLeg);
			yLegend.setColor(ColorUtil.getHexColor(chart.getYLegend().getColor()));
			yLegend.setFont(createFont(chart.getYLegend().getFont()));
			nc.setyLegend(yLegend);
		}
		if ((chart.getyDualLegend() != null) && !isEmpty(chart.getyDualLegend().getTitle()))  {
			String yLeg = StringUtil.getI18nString(replaceParameters(chart.getyDualLegend().getTitle()), I18nUtil.getLanguageByName(chart, language));
			NextChartLegend yLegend = new NextChartLegend(yLeg);
			yLegend.setColor(ColorUtil.getHexColor(chart.getyDualLegend().getColor()));
			yLegend.setFont(createFont(chart.getyDualLegend().getFont()));
			nc.setY2Legend(yLegend);
		}
	}

    private String getStringValue(String column, String pattern) throws QueryException {
        Object xObject = result.nextValue(column);
        return StringUtil.getValueAsString(xObject, pattern);
    }

    private String getStringValue(Number number) {
        if (number == null) {
            return "";
        }
        if (number.intValue() == number.doubleValue()) {
            return String.valueOf(number.intValue());
        } else {
            return String.valueOf(number.doubleValue());
        }
    }    

    private void setLabelOrientation(byte rotation) {
        if (Chart.VERTICAL == rotation) {
            nc.setLabelOrientation(NextChart.Orientation.vertical);
        } else if (Chart.DIAGONAL == rotation) {
        	nc.setLabelOrientation(NextChart.Orientation.diagonal);
        } else if (Chart.HALF_DIAGONAL == rotation) {
        	nc.setLabelOrientation(NextChart.Orientation.halfdiagonal);
        } else {
        	nc.setLabelOrientation(NextChart.Orientation.horizontal);
        }
    }    

    private float getAlpha(byte transparency) {
        switch (transparency) {
            case Chart.LOW_TRANSPARENCY:
                return 0.75f;
            case Chart.AVG_TRANSPARENCY:
                return 0.5f;
            case Chart.HIGH_TRANSPARENCY:
                return 0.25f;
            default:
                return 1;
        }
    }

    // replace $P{...} parameters (used in title and x,y legends
    private String replaceParameters(String text) {
        for  (String param : parameterValues.keySet()) {
             text = StringUtil.replace(text, "\\$P\\{" + param + "\\}", StringUtil.getValueAsString(parameterValues.get(param), null,I18nUtil.getLanguageByName(chart, language)));
        }
        return text;
    }           
    
    private boolean isEmpty(String s) {
    	return (s == null) || "".equals(s.trim());
    }

}
