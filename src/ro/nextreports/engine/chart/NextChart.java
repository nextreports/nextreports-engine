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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/* 
 * @author Mihai Dinca-Panaitescu 
 */
public class NextChart {
	
	public static enum Type { bar, stackedbar, hbar, hstackedbar, line, area, pie, barcombo, stackedbarcombo };
	public static enum Style { normal, glass, cylinder, dome, parallelepiped, soliddot, hollowdot, anchordot, bowdot, stardot };
	public static enum Orientation { horizontal, vertical, diagonal, halfdiagonal };
	public static enum Alignment { left, center, right };
	
	private List<List<Number>> data = new ArrayList<List<Number>>();
	private List<List<Number>> lineData = new ArrayList<List<Number>>();
	private Type type;
	private Style style;
	private String background;	
	private List<String> labels = new ArrayList<String>();
	private Orientation labelOrientation;
	private List<String> color = new ArrayList<String>();
	private List<String> lineColor = new ArrayList<String>();
	private List<String> legend = new ArrayList<String>();
	private List<String> lineLegend = new ArrayList<String>();
	private Number alpha;
	private String colorXaxis;
	private String colorYaxis;
	private boolean showGridX;
	private boolean showGridY;
	private boolean showLabels;
	private String colorGridX;
	private String colorGridY;
	private String message;
	private int tickCount;
	private boolean showTicks = true;
	private NextChartTitle title;
	private NextChartAxis xData;
	private NextChartAxis yData;	
	private NextChartLegend xLegend;
	private NextChartLegend yLegend;
	private NextNumberFormat tooltipPattern;
		
	// function doClick(value){ console.log('Call from function : ' + value);}
	private String onClick;
	
	public NextChart() {	
	}
		
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Style getStyle() {
		return style;
	}

	public void setStyle(Style style) {
		this.style = style;
	}

	public List<List<Number>> getData() {
		return data;
	}

	public void setData(List<List<Number>> data) {
		this.data = data;
	}
	
	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public Orientation getLabelOrientation() {
		return labelOrientation;
	}

	public void setLabelOrientation(Orientation labelOrientation) {
		this.labelOrientation = labelOrientation;
	}

	public List<String> getColor() {
		return color;
	}

	public void setColor(List<String> color) {
		this.color = color;
	}

	public List<String> getLegend() {
		return legend;
	}

	public void setLegend(List<String> legend) {
		this.legend = legend;
	}

	public Number getAlpha() {
		return alpha;
	}

	public void setAlpha(Number alpha) {
		this.alpha = alpha;
	}

	public String getColorXaxis() {
		return colorXaxis;
	}

	public void setColorXaxis(String colorXaxis) {
		this.colorXaxis = colorXaxis;
	}

	public String getColorYaxis() {
		return colorYaxis;
	}

	public void setColorYaxis(String colorYaxis) {
		this.colorYaxis = colorYaxis;
	}

	public boolean isShowGridX() {
		return showGridX;
	}

	public void setShowGridX(boolean showGridX) {
		this.showGridX = showGridX;
	}

	public boolean isShowGridY() {
		return showGridY;
	}

	public void setShowGridY(boolean showGridY) {
		this.showGridY = showGridY;
	}
		
	public boolean isShowLabels() {
		return showLabels;
	}

	public void setShowLabels(boolean showLabels) {
		this.showLabels = showLabels;
	}

	public String getColorGridX() {
		return colorGridX;
	}

	public void setColorGridX(String colorGridX) {
		this.colorGridX = colorGridX;
	}

	public String getColorGridY() {
		return colorGridY;
	}

	public void setColorGridY(String colorGridY) {
		this.colorGridY = colorGridY;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getTickCount() {
		return tickCount;
	}

	public void setTickCount(int tickCount) {
		this.tickCount = tickCount;
	}

	public NextChartTitle getTitle() {
		return title;
	}

	public void setTitle(NextChartTitle title) {
		this.title = title;
	}
	
	public NextChartAxis getxData() {
		return xData;
	}

	public void setxData(NextChartAxis xData) {
		this.xData = xData;
	}

	public NextChartAxis getyData() {
		return yData;
	}

	public void setyData(NextChartAxis yData) {
		this.yData = yData;
	}

	public NextChartLegend getxLegend() {
		return xLegend;
	}

	public void setxLegend(NextChartLegend xLegend) {
		this.xLegend = xLegend;
	}

	public NextChartLegend getyLegend() {
		return yLegend;
	}

	public void setyLegend(NextChartLegend yLegend) {
		this.yLegend = yLegend;
	}		

	public boolean isShowTicks() {
		return showTicks;
	}

	public void setShowTicks(boolean showTicks) {
		this.showTicks = showTicks;
	}
	
	public NextNumberFormat getTooltipPattern() {
		return tooltipPattern;
	}

	public void setTooltipPattern(NextNumberFormat tooltipPattern) {
		this.tooltipPattern = tooltipPattern;
	}
		
	public List<List<Number>> getLineData() {
		return lineData;
	}

	public void setLineData(List<List<Number>> lineData) {
		this.lineData = lineData;
	}

	public List<String> getLineColor() {
		return lineColor;
	}

	public void setLineColor(List<String> lineColor) {
		this.lineColor = lineColor;
	}

	public List<String> getLineLegend() {
		return lineLegend;
	}

	public void setLineLegend(List<String> lineLegend) {
		this.lineLegend = lineLegend;
	}
		
	public String getOnClick() {
		return onClick;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();		
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		StringWriter writer = new StringWriter();
		try {
			mapper.writeValue(writer, this);
			return writer.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return "Error : " + ex.getMessage();
		}
	}		

}
