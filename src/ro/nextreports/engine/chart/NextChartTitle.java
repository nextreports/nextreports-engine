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

/* 
 * @author Mihai Dinca-Panaitescu 
 */
public class NextChartTitle {
	
	private String text;
	private NextChartFont font;
	private String color;
	private NextChart.Alignment alignment;
	
	public NextChartTitle(String text) {
		super();
		this.text = text;
	}

	public NextChartFont getFont() {
		return font;
	}

	public void setFont(NextChartFont font) {
		this.font = font;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public NextChart.Alignment getAlignment() {
		return alignment;
	}

	public void setAlignment(NextChart.Alignment alignment) {
		this.alignment = alignment;
	}

	public String getText() {
		return text;
	}		
	
	
}
