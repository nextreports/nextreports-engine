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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.engine.util.ReportUtil;


public class ConverterUtil {
	
	private static Log LOG = LogFactory.getLog(ConverterUtil.class);
		
	public static byte TYPE_NO_CONVERSION_NEEDED = 0;
	public static byte TYPE_CONVERTED = 1;
	public static byte TYPE_CONVERSION_EXCEPTION = 2;
	 
	public static byte convertIfNeeded(String path) {
		
		boolean dirty = false;
		try {
			String xml = ConverterChain.applyFromPath(path);
			dirty = ConverterChain.conversionDone();
			if (dirty) {				
				ReportUtil.saveReport(xml, path);
				return TYPE_CONVERTED;
			} else {
				return TYPE_NO_CONVERSION_NEEDED;
			}
		} catch (ConverterException ex) {
			ex.printStackTrace();
			LOG.error(ex.getMessage());
			return TYPE_CONVERSION_EXCEPTION;
		}
		
	}   

}
