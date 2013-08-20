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
package ro.nextreports.engine.exporter.util;

import java.awt.Color;
import java.io.Serializable;

public class IndicatorData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String title;
	private String description;
	private String unit;
	private int min;
	private int max;
	private boolean showMinMax;
	private Color color;
	private Color background;
	private double value;
	
	public IndicatorData() {
		super();		
		title = "";
		description = "";
		unit = "";
		int min = 0;
		max = 100;
		showMinMax = true;
		color = Color.RED;
		background = Color.WHITE;
		value = 0;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public void setShowMinMax(boolean showMinMax) {
		this.showMinMax = showMinMax;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setBackground(Color background) {
		this.background = background;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getUnit() {
		return unit;
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	public boolean isShowMinMax() {
		return showMinMax;
	}

	public Color getColor() {
		return color;
	}

	public Color getBackground() {
		return background;
	}

	public double getValue() {
		return value;
	}
				
}
