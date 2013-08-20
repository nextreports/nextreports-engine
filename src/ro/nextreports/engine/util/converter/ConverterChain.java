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
package ro.nextreports.engine.util.converter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ConverterChain {
	
	private static Log LOG = LogFactory.getLog(ConverterChain.class);
	
	private static List<NextConverter> converters = new ArrayList<NextConverter>();	
	
	static {
		converters.add(new Converter_5_2());		
	}
	
	// change version at the end of chain so all converters do their convert method
	public static String applyFromPath(String path) throws ConverterException {		
		String xml = converters.get(0).convertFromPath(path, false);						
		xml = applyFromSecond(xml);
		if (conversionDone()) {
			return converters.get(converters.size()-1).changeVersion(xml);
		} else {
			return xml;
		}
	}
	
	// change version at the end of chain so all converters do their convert method
	public static String applyFromInputStream(InputStream is) throws ConverterException {		
		String xml = converters.get(0).convertFromInputStream(is, false);						
		xml = applyFromSecond(xml);
		if (conversionDone()) {
			return converters.get(converters.size()-1).changeVersion(xml);
		} else {
			return xml;
		}
	}
	
	// change version at the end of chain so all converters do their convert method
	public static String applyFromXml(String text) throws ConverterException {		
		String xml = converters.get(0).convertFromXml(text, false);						
		xml = applyFromSecond(xml);
		if (conversionDone()) {
			return converters.get(converters.size()-1).changeVersion(xml);
		} else {
			return xml;
		}
	}
	
	private static String applyFromSecond(String xml) throws ConverterException {
									
		for (int i=1, size=converters.size(); i<size; i++) {
			xml = converters.get(i).convertFromXml(xml, false);
		}						
		return xml;
	}
	
	public static boolean conversionDone() {
		return converters.get(converters.size()-1).conversionDone();
	}
		
}
