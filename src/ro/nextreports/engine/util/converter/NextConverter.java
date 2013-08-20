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


public interface NextConverter extends XmlConverter {
	
	/**
	 * Get the first version that does not need this convert
	 * @return the first version that does not need this convert
	 */
	public String getConverterVersion();
	
	/**
	 * Retuns true if conversion was done, false if conversion was not needed
	 * @return true if conversion was done, false if conversion was not needed
	 */
	public boolean conversionDone();
	
	/**
	 * Change the version from xml
	 * @param xml xml file
	 * @return new xml file with changed version
	 */
	public String changeVersion(String xml) throws ConverterException;
		
}
