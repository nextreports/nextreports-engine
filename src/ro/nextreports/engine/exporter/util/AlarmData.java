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
import java.io.StringWriter;

import ro.nextreports.engine.util.ColorUtil;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AlarmData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String color;
	private String text;
	
	public AlarmData() {
		color =  ColorUtil.getHexColor(Color.WHITE);
        text = "";
	}
	
	public AlarmData(String color, String text) {
		super();
		this.color = color;
		this.text = text;
	}

	public String getColor() {
		return color;
	}

	public String getText() {
		return text;
	}		

	public void setColor(String color) {
		this.color = color;
	}

	public void setText(String text) {
		this.text = text;
	}

	// Do not modify : used by wicket model on the server
	@Override
	public String toString() {
		return text;
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
