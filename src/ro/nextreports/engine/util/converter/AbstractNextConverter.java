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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import ro.nextreports.engine.ReleaseInfoAdapter;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.util.ReportUtil;


public abstract class AbstractNextConverter implements NextConverter {
	
	private static Log LOG = LogFactory.getLog(AbstractNextConverter.class);
	private boolean dirty = false;
	
	public String convertFromPath(String reportPath, boolean changeVersion) throws ConverterException {
		try {
			String text = ReportUtil.readAsString(reportPath);     
			return convert(text, "'" + reportPath + "'", changeVersion);
		} catch (IOException ex) {
			throw new ConverterException("Converter " + getConverterVersion() + " : " + ex.getMessage()); 
		}	
	}
	
	public String convertFromInputStream(InputStream is, boolean changeVersion) throws ConverterException {
		try {
			String text = ReportUtil.readAsString(is);   
			return convert(text, "from input stream", changeVersion);
		} catch (IOException ex) {
			throw new ConverterException("Converter " + getConverterVersion() + " : " + ex.getMessage()); 
		}
	}
	
	public String convertFromXml(String xml, boolean changeVersion) throws ConverterException {
		return convert(xml, "from xml", changeVersion);	
	}
	
	private String convert(String text, String message, boolean changeVersion) throws ConverterException {
		try {		
			dirty = false;			           
			String version = ReportUtil.getVersionFromText(text);
			if (version == null) {
				throw new ConverterException("Converter " + getConverterVersion() + " : Could not read version for report " + message);				
			}
			if (ReportUtil.compareVersions(version, getConverterVersion()) >= 0) {				
				return text;
			}
			LOG.info("Converter " + getConverterVersion() + " : Convert report (ver=" + version + ") " + message);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true); // never forget this!
			DocumentBuilder builder = factory.newDocumentBuilder();			
			Document doc = builder.parse(new ByteArrayInputStream(text.getBytes("UTF-8")));
			Document convertedDoc = convert(doc);
			String result = transformToXml(convertedDoc);
			if (changeVersion) {
				result = changeVersion(result);
			}
			LOG.info("Converter " + getConverterVersion() + " : Report " + message + " converted OK");
			dirty = true;
			return result;
		} catch (Throwable t) {
			if (t instanceof ConverterException) {
				throw (ConverterException)t;
			}
			throw new ConverterException("Converter " + getConverterVersion() + " : Report " + message + " converted FAILED", t);
		}		

	}
	
	public String changeVersion(String xml) throws ConverterException {
		// use xstream to load and save report to make xml without spaces and
		// with correct indents also set the new version
		Report report = ReportUtil.loadConvertedReport(xml);
		if (report == null) {
			throw new ConverterException("Converter " + getConverterVersion() + " : Report could not be read after conversion");
		}
		report.setVersion(ReleaseInfoAdapter.getVersionNumber());
		return ReportUtil.reportToXml(report);
	}
	
	private String transformToXml(Document doc) throws Exception {
		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		xformer.setOutputProperty(OutputKeys.INDENT, "yes");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		xformer.transform(new DOMSource(doc), new StreamResult(out));
		return new String(out.toByteArray());
	}	
	
	protected abstract Document convert(Document doc) throws Exception;	
	
	public boolean conversionDone() {
		return dirty;
	}		

}
