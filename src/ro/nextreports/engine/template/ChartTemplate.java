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
package ro.nextreports.engine.template;

import java.awt.Color;
import java.io.Serializable;
import java.util.List;

public class ChartTemplate implements Serializable {
		
	private static final long serialVersionUID = 8659341554114504567L;
	
	private String version;
	private Color background;
	private List<Color> foregrounds;
	private Color titleColor;
	private Color xAxisColor;
	private Color xLegendColor;
	private Color xLabelColor;
	private Color xGridColor;
	private Color yAxisColor;
	private Color yLegendColor;
	private Color yLabelColor;
	private Color yGridColor;
	
	public ChartTemplate() {		
	}

	public Color getBackground() {
		return background;
	}

	public void setBackground(Color background) {
		this.background = background;
	}

	public List<Color> getForegrounds() {
		return foregrounds;
	}

	public void setForegrounds(List<Color> foregrounds) {
		this.foregrounds = foregrounds;
	}

	public Color getTitleColor() {
		return titleColor;
	}

	public void setTitleColor(Color titleColor) {
		this.titleColor = titleColor;
	}

	public Color getxAxisColor() {
		return xAxisColor;
	}

	public void setxAxisColor(Color xAxisColor) {
		this.xAxisColor = xAxisColor;
	}

	public Color getxLegendColor() {
		return xLegendColor;
	}

	public void setxLegendColor(Color xLegendColor) {
		this.xLegendColor = xLegendColor;
	}

	public Color getxLabelColor() {
		return xLabelColor;
	}

	public void setxLabelColor(Color xLabelColor) {
		this.xLabelColor = xLabelColor;
	}

	public Color getxGridColor() {
		return xGridColor;
	}

	public void setxGridColor(Color xGridColor) {
		this.xGridColor = xGridColor;
	}

	public Color getyAxisColor() {
		return yAxisColor;
	}

	public void setyAxisColor(Color yAxisColor) {
		this.yAxisColor = yAxisColor;
	}

	public Color getyLegendColor() {
		return yLegendColor;
	}

	public void setyLegendColor(Color yLegendColor) {
		this.yLegendColor = yLegendColor;
	}

	public Color getyLabelColor() {
		return yLabelColor;
	}

	public void setyLabelColor(Color yLabelColor) {
		this.yLabelColor = yLabelColor;
	}

	public Color getyGridColor() {
		return yGridColor;
	}

	public void setyGridColor(Color yGridColor) {
		this.yGridColor = yGridColor;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}		
	
	

}
