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
import java.net.URLEncoder;
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
import ro.nextreports.engine.queryexec.QueryException;
import ro.nextreports.engine.queryexec.QueryResult;
import ro.nextreports.engine.util.StringUtil;
import ro.nextreports.jofc2.model.Text;
import ro.nextreports.jofc2.model.axis.Axis;
import ro.nextreports.jofc2.model.axis.Label;
import ro.nextreports.jofc2.model.axis.Label.Rotation;
import ro.nextreports.jofc2.model.axis.XAxis;
import ro.nextreports.jofc2.model.axis.YAxis;
import ro.nextreports.jofc2.model.axis.YLabel;
import ro.nextreports.jofc2.model.elements.AnimatedElement;
import ro.nextreports.jofc2.model.elements.AreaLineChart;
import ro.nextreports.jofc2.model.elements.BarChart;
import ro.nextreports.jofc2.model.elements.Element;
import ro.nextreports.jofc2.model.elements.HorizontalBarChart;
import ro.nextreports.jofc2.model.elements.LineChart;
import ro.nextreports.jofc2.model.elements.PieChart;
import ro.nextreports.jofc2.model.elements.PieChart.AnimationPie;
import ro.nextreports.jofc2.model.elements.StackedBarChart;
import ro.nextreports.jofc2.model.elements.Tooltip;

/**
 * @author Decebal Suiu
 */
public class JsonExporter implements ChartExporter {

    private OutputStream out;
    private QueryResult result;
    private Chart chart;
    private PrintStream stream;
    // default background color for a flash chart (if none is set)
    private final Color DEFAULT_BACKGROUND = new Color(248, 248, 216);
    private Map<String, Object> parameterValues;
    private String drillFunction;

    private static final String X_KEY = "X_VALUE";
    public static final String X_VALUE = "${" + X_KEY +  "}";
    
    // animation strings
    // http://teethgrinder.co.uk/open-flash-chart-2/bar-chart-on-show.php
    private String POP = "pop";
    private String POP_UP = "pop-up";
    private String DROP = "drop";
    private String FADE_IN = "fade-in";
    private String GROW_UP = "grow-up";      // does not work
    private String GROW_DOWN = "grow-down";  // does not work  -> use grow
    
    // for line
    private String EXPLODE = "explode";
    private String MID_SLIDE= "mid-slide";
    private String SHRINK_IN = "shrink-in";
    
    public JsonExporter(Map<String, Object> parameterValues, QueryResult result, OutputStream out,
                        Chart chart, String drillFunction) {
        this.parameterValues = parameterValues;
        this.result = result;
        this.out = out;
        this.chart = chart;
        this.drillFunction = drillFunction;
    }

    public boolean export() throws QueryException, NoDataFoundException {
        testForData();

        initExport();

        String json = createFlashChart().toString();           
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

    private String getStyle(ChartTitle chartTitle) {
        StringBuffer titleStyle = new StringBuffer();
        Font font = chartTitle.getFont();
        titleStyle.append('{');
        titleStyle.append("font-size: ").append(font.getSize()).append("px;");
        titleStyle.append("color: ").append(getHexColor(chartTitle.getColor())).append(";");
        titleStyle.append("font-family: ").append(font.getFamily()).append(";");
        if (font.isBold()) {
            // it's omitted by ofc
            titleStyle.append("font-weight: bold;");
        }
        if (font.isItalic()) {
            // it's omitted by ofc
            titleStyle.append("font-style: italic;");
        }
        byte align = chartTitle.getAlignment();
        if (align == ChartTitle.LEFT_ALIGNMENT) {
            titleStyle.append("text-align: left;");
        } else if (align == ChartTitle.CENTRAL_ALIGNMENT) {
            titleStyle.append("text-align: center;");
        } else if (align == ChartTitle.RIGHT_ALIGNMENT) {
            titleStyle.append("text-align: right;");
        }
        titleStyle.append('}');
        return titleStyle.toString();
    }

    private ro.nextreports.jofc2.model.Chart createFlashChart() throws QueryException {

        ChartTitle chartTitle = chart.getTitle();
        String title = replaceParameters(chartTitle.getTitle());        

        String titleStyle = getStyle(chartTitle);
        ro.nextreports.jofc2.model.Chart flashChart = new ro.nextreports.jofc2.model.Chart(title, titleStyle);
        XAxis xAxis = new XAxis();
        xAxis.setColour(getHexColor(chart.getxAxisColor()));
        flashChart.setXAxis(xAxis);
        YAxis yAxis = new YAxis();
        yAxis.setColour(getHexColor(chart.getyAxisColor()));
        flashChart.setYAxis(yAxis);

        setBackground(flashChart);
        setGridAxisColors(flashChart, xAxis, yAxis);
        setLegends(flashChart);
        setTooltip(flashChart);

        boolean showXLabel = chart.getXShowLabel() == null ? false : chart.getXShowLabel();
        boolean showYLabel = chart.getYShowLabel() == null ? false : chart.getYShowLabel();

        setTicks(xAxis, yAxis, showXLabel, showYLabel);
        setElements(flashChart, xAxis, yAxis, showXLabel, showYLabel);
        
        String yTooltipPattern = chart.getYTooltipPattern();
        if (yTooltipPattern != null) {        	
        	DecimalFormat df = new DecimalFormat(yTooltipPattern);
        	int numDecimals = df.getMinimumFractionDigits();
        	char decimalSEparator = df.getDecimalFormatSymbols().getDecimalSeparator();        	
        	flashChart.setNumDecimals(numDecimals);
        	flashChart.setDecimalSeparatorIsComma(',' == decimalSEparator);
        	flashChart.setThousandSeparatorDisabled(false);
        }	
        
        return flashChart;
    }

    private Element[] createBarChart(XAxis xAxis, YAxis yAxis, boolean showXLabel, boolean showYLabel) throws QueryException {

        Element[] charts = new Element[chart.getYColumns().size()];

        for (int i = 0; i < charts.length; i++) {
            BarChart barChart;
            byte style = chart.getType().getStyle();
            switch (style) {
                case ChartType.STYLE_BAR_GLASS:
                    barChart = new BarChart(BarChart.Style.GLASS);
                    break;
                case ChartType.STYLE_BAR_CYLINDER:
                    barChart = new BarChart(BarChart.Style.CYLINDER);
                    break;
                case ChartType.STYLE_BAR_PARALLELIPIPED:
                    barChart = new BarChart(BarChart.Style.THREED);
                    break;
                case ChartType.STYLE_BAR_DOME:
                    barChart = new BarChart(BarChart.Style.DOME);
                    break;
                default:
                    barChart = new BarChart();
                    break;
            }
            barChart.setColour(getHexColor(chart.getForegrounds().get(i)));
            barChart.setAlpha(getAlpha(chart.getTransparency()));
            barChart.setTooltip("#val#");
            barChart.setFontSize(chart.getFont().getSize());
            barChart.setOnShow(new AnimatedElement.OnShow(DROP,0,1));
            charts[i] = barChart;
        }

        createChart(xAxis, yAxis, showXLabel, showYLabel, charts);

        return charts;
    }

    private Element[] createHorizontalBarChart(XAxis xAxis, YAxis yAxis, boolean showXLabel, boolean showYLabel) throws QueryException {
        Element[] charts = new Element[chart.getYColumns().size()];        
        for (int i = 0; i < charts.length; i++) {
            HorizontalBarChart barChart = new HorizontalBarChart();
            barChart.setColour(getHexColor(chart.getForegrounds().get(i)));
            barChart.setAlpha(getAlpha(chart.getTransparency()));
            barChart.setTooltip("#val#");            
            // setting font size on horizontal bar chart will make a json which cannot be rendered by JOFC api!            
            //barChart.setFontSize(chart.getFont().getSize());
            charts[i] = barChart;
        }
        createChart(xAxis, yAxis, showXLabel, showYLabel, charts);

        return charts;
    }


    private Element[] createPieChart(boolean showXLabel, boolean showYLabel) throws QueryException {
        Element[] charts = new Element[1];
        PieChart pieChart = new PieChart();
        pieChart.setAnimate(true);
        pieChart.setStartAngle(35);
        pieChart.setBorder(2);
        List<AnimationPie> animations = new ArrayList<AnimationPie>();
        animations.add(new AnimationPie.Fade());
        animations.add(new AnimationPie.Bounce(10));
        pieChart.addAnimations(animations);
        pieChart.setGradientFill(true);
        pieChart.setAlpha(getAlpha(chart.getTransparency()));
        List<String> colors = new ArrayList<String>();        
        for (Color color : chart.getForegrounds()) {
            colors.add(getHexColor(color));
        }        
        pieChart.setColours(colors.toArray(new String[10]));
        pieChart.setTooltip("#val# of #total#<br>#percent# of 100%"); 
        pieChart.setFontSize(chart.getFont().getSize());
        charts[0] = pieChart;
        createChart(null, null, showXLabel, showYLabel, new Element[]{pieChart});

        return charts;
    }

    private Element[] createStackedBarChart(XAxis xAxis, YAxis yAxis, boolean showXLabel, boolean showYLabel) throws QueryException {
        Element[] charts = new Element[chart.getYColumns().size()];
        StackedBarChart barChart = new StackedBarChart();
        barChart.setAlpha(getAlpha(chart.getTransparency()));        
        barChart.setTooltip("#val#");
        barChart.setFontSize(chart.getFont().getSize());            
        for (int i = 0; i < charts.length; i++) {
            charts[i] = barChart;
        }
        createChart(xAxis, yAxis, showXLabel, showYLabel, charts);
        return charts;
    }

    private Element[] createLineChart(XAxis xAxis, YAxis yAxis, boolean showXLabel, boolean showYLabel) throws QueryException {

        Element[] charts = new Element[chart.getYColumns().size()];
        for (int i = 0; i < charts.length; i++) {
            LineChart lineChart = new LineChart();
            lineChart.setWidth(4);
            lineChart.setColour(getHexColor(chart.getForegrounds().get(i)));
            lineChart.setAlpha(getAlpha(chart.getTransparency()));
            lineChart.setDotSize(5);
            LineChart.Style.Type type;
            byte style = chart.getType().getStyle();
            switch (style) {
                case ChartType.STYLE_LINE_DOT_SOLID:
                    type = LineChart.Style.Type.SOLID_DOT;
                    break;
                case ChartType.STYLE_LINE_DOT_HOLLOW:
                    type = LineChart.Style.Type.HALLOW_DOT;
                    break;
                case ChartType.STYLE_LINE_DOT_ANCHOR:
                    type = LineChart.Style.Type.ANCHOR;
                    break;
                case ChartType.STYLE_LINE_DOT_BOW:
                    type = LineChart.Style.Type.BOW;
                    break;
                case ChartType.STYLE_LINE_DOT_STAR:
                    type = LineChart.Style.Type.STAR;
                    break;
                default:
                    type = LineChart.Style.Type.DOT;
                    break;
            }
            lineChart.setDotStyle(new LineChart.Style(type, "#111111", 4, 4).setRotation(90));
            lineChart.setFontSize(chart.getFont().getSize());
            lineChart.setOnShow(new AnimatedElement.OnShow(EXPLODE));
            charts[i] = lineChart;
        }
        createChart(xAxis, yAxis, showXLabel, showYLabel, charts);

        return charts;
    }
       
    private Element[] createAreaChart(XAxis xAxis, YAxis yAxis, boolean showXLabel, boolean showYLabel) throws QueryException {
        Element[] charts = new Element[chart.getYColumns().size()];
        for (int i = 0; i < charts.length; i++) {
        	AreaLineChart areaChart = new AreaLineChart(); 
        	areaChart.setWidth(4);
        	areaChart.setColour(getHexColor(chart.getForegrounds().get(i)));
        	areaChart.setAlpha(getAlpha(chart.getTransparency()));
        	areaChart.setDotSize(5);          	
        	areaChart.setFontSize(chart.getFont().getSize());
        	areaChart.setOnShow(new AnimatedElement.OnShow(MID_SLIDE));       
            charts[i] = areaChart;        	
        }
        createChart(xAxis, yAxis, showXLabel, showYLabel, charts);
        return charts;
    }    

    private void createChart(XAxis xAxis, YAxis yAxis, boolean showXLabel, boolean showYLabel, Element[] elementChart) throws QueryException {
        Number min = Double.MAX_VALUE;
        Number max = Double.MIN_VALUE;
        int row = 0;
        Object previous = null;
        String xColumn = chart.getXColumn();
        String xPattern = chart.getXPattern();
        String lastXValue = "";

        boolean isPie = elementChart[0] instanceof PieChart;
        boolean isHorizontal = elementChart[0] instanceof HorizontalBarChart;
        boolean isStacked = elementChart[0] instanceof StackedBarChart;
        int chartsNo = elementChart.length;        

        GFunction[] functions = new GFunction[chartsNo];
        List<StackedBarChart.Key> keys = new ArrayList<StackedBarChart.Key>();
        for (int i = 0; i < chartsNo; i++) {
            functions[i] = FunctionFactory.getFunction(chart.getYFunction());
            if ((chart.getYColumnsLegends() != null) && (i < chart.getYColumnsLegends().size())) {
                if (chart.getYColumnsLegends().get(i) != null) {
                    if (isStacked) {
                        StackedBarChart.Key key = new StackedBarChart.Key(getHexColor(chart.getForegrounds().get(i)),
                                replaceParameters(chart.getYColumnsLegends().get(i)), 12);
                        keys.add(key);
                    } else {
                        elementChart[i].setText(replaceParameters(chart.getYColumnsLegends().get(i)));
                    }
                }
            }
        }
        // legends for stacked bar chart
        if (isStacked) {
            ((StackedBarChart)elementChart[0]).addKeys(keys);
        }

        int groups = 1;
        HashMap<Integer, String> infoLabels = new HashMap<Integer, String>();

        while (result.hasNext()) {        	
            Object[] objects = new Object[chartsNo];
            Number[] computedValues = new Number[chartsNo];
            for (int i = 0; i < chartsNo; i++) {
                if (chart.getYColumns().get(i) != null) {
                    objects[i] = result.nextValue(chart.getYColumns().get(i));
                    Number value = null;
                    if (objects[i] instanceof Number) {
                        value = (Number) objects[i];
                    } else if (objects[i] != null) {
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
                    addValue(computedValues[i], lastXValue, elementChart[i], i);                    
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
                addValue(value, lastXValue, elementChart[i], i);                
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
        if (!isPie) {
        	
            // for horizontal charts (horizontal bars)
            if (isHorizontal) {
                int factor = groups < 2 ? 0 : (groups - 2) / 10;
                YRange range = setAxisRange(yAxis, 1, groups - 0.5);                 
                for (int i = groups; i > 0; i--) {
                    //System.out.println("add label : " + infoLabels.get(i) + "  pos="+ (groups - i));
                    String text = infoLabels.get(i);                        
                    if (!showXLabel) {
                        text = "";
                    }
                    addYLabel(yAxis, text, groups - i + factor + 1);
                    for (int j=1; j<chartsNo; j++) {
                        addYLabel(yAxis, "", groups - i + factor + 1);
                    }
                }      
                
            // for vertical normal charts
            } else {
                for (int i = 1; i <= groups; i++) {
                    String text = infoLabels.get(i);
                    if (!showXLabel) {
                        text = "";
                    }
                    addXLabel(xAxis, text);
                }
            }
        }

        // value labels
        if (!isPie) {
            if (isHorizontal) {            	
                addXLabels(xAxis, min, max, showYLabel);                
            } else {
                addYLabels(yAxis, min, max, showYLabel);
            }
        }
    }       

    private void addXLabel(XAxis xAxis, String text) {
        Label label = new Label(text);
        label.setRotation(getRotation(chart.getXorientation()));
        label.setColour(getHexColor(chart.getXColor()));
        label.setSize(chart.getXLabelFont().getSize()); // 6 to n
        xAxis.addLabels(label);
    }

    private void addYLabels(YAxis yAxis, Number min, Number max, boolean showYLabels) {
    	YRange v = setAxisRange(yAxis, min, max);
        for (int n = v.getMin().intValue(); n <= v.getMax().intValue(); n += v.getStep()) {
            String text = String.valueOf(n);
            if (!showYLabels) {
                text = "";
            }
            YLabel yLabel = new YLabel(text, n);
            yLabel.setColour(getHexColor(chart.getYColor()));
            yAxis.addLabels(yLabel);
            yLabel.setSize(chart.getYLabelFont().getSize());
        }
    }

    // for horizontal chart
    private void addYLabel(YAxis yAxis, String text, int n) {
        YLabel label = new YLabel(text, n);
        label.setRotation(getRotation(chart.getXorientation()));
        label.setColour(getHexColor(chart.getXColor()));
        label.setSize(chart.getYLabelFont().getSize());
        yAxis.addLabels(label);
    }

    // for horizontal chart
    private void addXLabels(XAxis xAxis, Number min, Number max, boolean showYLabels) {
        xAxis.setOffset(false);
        YRange v = setAxisRange(xAxis, 0, max);
        if (!showYLabels) {
        	xAxis.setLabels("");
        } else {
        	xAxis.getLabels().setColour(getHexColor(chart.getYColor()));
        	// add orientation ? (we have only for x axis in layout)
        }
    }

    private void addValue(Number value, String text, Element chart, int position) {        	
        String function = null;
        if (drillFunction != null) {
            // put x value to java script function    
        	// because function is used inside URL (see OnClickChartAjaxBehavior) we must encode the parameter value
        	// so special characters (like %, space and so on) do not make the url fail
        	try {
    			text = URLEncoder.encode(text, "UTF-8");
    		} catch (UnsupportedEncodingException e) {
    			e.printStackTrace();
    		}        	
            function = StringUtil.replace(drillFunction, "\\$\\{" + X_KEY + "\\}", text);            
        }                

        if (chart instanceof BarChart) {            
            BarChart.Bar bar  = new BarChart.Bar(value);
            if  (function != null) {
                bar.setOnClick(function);
            }
            ((BarChart) chart).addBars(bar);
        } else if (chart instanceof HorizontalBarChart) {
            HorizontalBarChart.Bar bar = new HorizontalBarChart.Bar(0, value);
            bar.setTooltip(getStringValue(value));       
            ((HorizontalBarChart) chart).addBars(bar);
        } else if (chart instanceof AreaLineChart) {    //! before  "chart instanceof LineChart" because AreaLineChart is also a LineChart   	
        	AreaLineChart.Dot dot = new AreaLineChart.Dot(value);
            if  (function != null) {
                dot.setOnClick(function);
            }
            ((AreaLineChart) chart).addDots(dot);
        } else if (chart instanceof LineChart) {
            LineChart.Dot dot = new LineChart.Dot(value);
            if  (function != null) {
                dot.setOnClick(function);
            }
            ((LineChart) chart).addDots(dot);
        } else if (chart instanceof PieChart) {
            PieChart.Slice slice = new PieChart.Slice(value, text);
            if  (function != null) {
                slice.setOnClick(function);
            }
            ((PieChart) chart).addSlices(slice);
        } else if (chart instanceof StackedBarChart) {
            StackedBarChart sbChart =((StackedBarChart) chart);
            StackedBarChart.Stack stack;
            if (position  == 0) {
                stack = new StackedBarChart.Stack();
                sbChart.addStack(stack);
            } else {                
                stack = sbChart.lastStack();
            }            
            StackedBarChart.StackValue stackValue = new StackedBarChart.StackValue(value);
            stackValue.setColour(getHexColor(this.chart.getForegrounds().get(position)));
            stack.addStackValues(stackValue);
        }
    }

    private YRange setAxisRange(Axis axis, Number min, Number max) {
    	YRange yRange = new YRange(min, max);
    	yRange = yRange.update();      	
        axis.setRange(yRange.getMin(), yRange.getMax(), yRange.getStep());        
        return yRange;        
    }

    private void setBackground(ro.nextreports.jofc2.model.Chart flashChart) {
        if (chart.getBackground() != null) {
            flashChart.setBackgroundColour(getHexColor(chart.getBackground()));
        }
    }

    // to hide a grid we set its color to chart background color
    private void setGridAxisColors(ro.nextreports.jofc2.model.Chart flashChart, XAxis xAxis, YAxis yAxis) {
        boolean isHorizontal = chart.getType().isHorizontal();
        Color xGridColor = chart.getXGridColor();
        Color yGridColor = chart.getYGridColor();
        if (xGridColor != null) {
            getXAxis(xAxis, yAxis, isHorizontal).setGridColour(getHexColor(xGridColor));
        }
        if ((chart.getXShowGrid() != null) && !chart.getXShowGrid()) {
            if (flashChart.getBackgroundColour() == null) {
                getXAxis(xAxis, yAxis, isHorizontal).setGridColour(getHexColor(DEFAULT_BACKGROUND));
            } else {
                getXAxis(xAxis, yAxis, isHorizontal).setGridColour(flashChart.getBackgroundColour());
            }
        }
        if (yGridColor != null) {
            getYAxis(xAxis, yAxis, isHorizontal).setGridColour(getHexColor(yGridColor));
        }
        if ((chart.getYShowGrid() != null) && !chart.getYShowGrid()) {
            if (flashChart.getBackgroundColour() == null) {
                getYAxis(xAxis, yAxis, isHorizontal).setGridColour(getHexColor(DEFAULT_BACKGROUND));
            } else {
                getYAxis(xAxis, yAxis, isHorizontal).setGridColour(flashChart.getBackgroundColour());
            }
        }
    }

    private Axis getXAxis(XAxis xAxis, YAxis yAxis, boolean isHorizontal) {
        if (!isHorizontal) {
            return xAxis;
        } else {
            return yAxis;
        }
    }

    private Axis getYAxis(XAxis xAxis, YAxis yAxis, boolean isHorizontal) {
        if (!isHorizontal) {
            return yAxis;
        } else {
            return xAxis;
        }
    }

    // hide ticks if we do not show labels
    private void setTicks(XAxis xAxis, YAxis yAxis, boolean showXLabel, boolean showYLabel) {
        boolean isHorizontal = chart.getType().isHorizontal();

        if ((!showXLabel && !isHorizontal) || (!showYLabel && isHorizontal)) {
            xAxis.setTickHeight(0);
        }
        if ((!showYLabel && !isHorizontal) || (!showXLabel && isHorizontal)) {
            yAxis.setTickLength(0);
        }
    }

    private void setTooltip(ro.nextreports.jofc2.model.Chart flashChart) {
//        boolean isHorizontal = chart.getType().isHorizontal();
//        boolean isStacked = chart.getType().isStacked();
//        if (isHorizontal || isStacked) {
            // The location of a point or bar is the center of that element.
            // This works great on all charts, apart from horizontal bar charts.
            // So for these it is wise to change the default tooltip behaviour to hover. 
            Tooltip tooltip = new Tooltip();
            tooltip.setHover();
            flashChart.setTooltip(tooltip);
//        }
    }

    private void setLegends(ro.nextreports.jofc2.model.Chart flashChart) {
        boolean isHorizontal = chart.getType().isHorizontal();
        Text xText = new Text(replaceParameters(chart.getXLegend().getTitle()), getStyle(chart.getXLegend()));
        Text yText = new Text(replaceParameters(chart.getYLegend().getTitle()), getStyle(chart.getYLegend()));        
        if (!isHorizontal) {
            flashChart.setXLegend(xText);
            flashChart.setYLegend(yText);
        } else {
            flashChart.setXLegend(yText);
            flashChart.setYLegend(xText);
        }
    }

    private void setElements(ro.nextreports.jofc2.model.Chart flashChart, XAxis xAxis, YAxis yAxis,
                             boolean showXLabel, boolean showYLabel) throws QueryException {
        byte type = chart.getType().getType();
        Element[] charts = new Element[0];
        if (ChartType.BAR == type) {
            charts = createBarChart(xAxis, yAxis, showXLabel, showYLabel);
        } else if (ChartType.HORIZONTAL_BAR == type) {
            charts = createHorizontalBarChart(xAxis, yAxis, showXLabel, showYLabel);
        } else if (ChartType.STACKED_BAR == type) {
            charts = createStackedBarChart(xAxis, yAxis, showXLabel, showYLabel);
        } else if (ChartType.PIE == type) {
            charts = createPieChart(showXLabel, showYLabel);
        } else if (ChartType.LINE == type) {
            charts = createLineChart(xAxis, yAxis, showXLabel, showYLabel);
        } else if (ChartType.AREA == type) {        	
        	charts = createAreaChart(xAxis, yAxis, showXLabel, showYLabel);
        }        
        for (Element element : charts) {
            flashChart.addElements(element);
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

    private String getHexColor(Color color) {
        String rgb = Integer.toHexString(color.getRGB());
        rgb = rgb.substring(2, rgb.length());

        return rgb;
    }

    private Rotation getRotation(byte rotation) {
        if (Chart.VERTICAL == rotation) {
            return Rotation.VERTICAL;
        } else if (Chart.DIAGONAL == rotation) {
            return Rotation.DIAGONAL;
        } else if (Chart.HALF_DIAGONAL == rotation) {
            return Rotation.HALF_DIAGONAL;
        } else {
            return Rotation.HORIZONTAL;
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
             text = StringUtil.replace(text, "\\$P\\{" + param + "\\}", StringUtil.getValueAsString(parameterValues.get(param), null));
        }
        return text;
    }       

}
