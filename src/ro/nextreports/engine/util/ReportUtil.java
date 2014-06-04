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
package ro.nextreports.engine.util;

import com.thoughtworks.xstream.XStream;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.engine.ReleaseInfoAdapter;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportGroup;
import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.XStreamFactory;
import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.BarcodeBandElement;
import ro.nextreports.engine.band.ChartBandElement;
import ro.nextreports.engine.band.ExpressionBandElement;
import ro.nextreports.engine.band.ExpressionBean;
import ro.nextreports.engine.band.FieldBandElement;
import ro.nextreports.engine.band.ForReportBandElement;
import ro.nextreports.engine.band.FunctionBandElement;
import ro.nextreports.engine.band.ImageBandElement;
import ro.nextreports.engine.band.ReportBandElement;
import ro.nextreports.engine.exporter.util.ParametersBean;
import ro.nextreports.engine.i18n.I18nLanguage;
import ro.nextreports.engine.i18n.I18nString;
import ro.nextreports.engine.queryexec.IdName;
import ro.nextreports.engine.queryexec.QueryParameter;
import ro.nextreports.engine.util.converter.ConverterChain;
import ro.nextreports.engine.util.converter.ConverterException;

//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: Feb 3, 2009
// Time: 4:00:15 PM

//
/**
 * Utilities class for report
 */
public class ReportUtil {

	/**
	 * Report has the version of current engine
	 */
	public static byte REPORT_VALID = 1;
	/**
	 * Report has an older invalid version than current engine (less than 2.0)
	 */
	public static byte REPORT_INVALID_OLDER = 2;
	/**
	 * Report has a version newer than current engine
	 */
	public static byte REPORT_INVALID_NEWER = 3;

	private static Log LOG = LogFactory.getLog(ReportUtil.class);

	/**
	 * Create a report object from an input stream
	 * 
	 * Use this method if you know your report version does not need any
	 * conversion from older versions, otherwise see
	 * {@link #loadReport(InputStream)}
	 * 
	 * @param is
	 *            input stream
	 * @return the report object created from the input stream or null if cannot
	 *         be read
	 */
	public static Report loadConvertedReport(InputStream is) {
		XStream xstream = XStreamFactory.createXStream();
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(is, "UTF-8");
			return (Report) xstream.fromXML(reader);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Create a report object from xml
	 * 
	 * Use this method if you know your report version does not need any
	 * conversion from older versions, otherwise see {@link #loadReport(String)}
	 * 
	 * @param xml
	 *            xml text
	 * @return the report object created from xml or null if cannot be read
	 */
	public static Report loadConvertedReport(String xml) {
		XStream xstream = XStreamFactory.createXStream();
		try {
			return (Report) xstream.fromXML(xml);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Create a report object from xml Do a conversion if it is needed
	 * 
	 * @since 5.2
	 * 
	 * @param xml
	 *            xml text
	 * @return the report object created from xml or null if cannot be read
	 * @throws LoadReportException
	 *             if report object cannot be created
	 */
	public static Report loadReport(String xml) throws LoadReportException {
		try {
			String convertedXml = ConverterChain.applyFromXml(xml);
			XStream xstream = XStreamFactory.createXStream();
			return (Report) xstream.fromXML(convertedXml);
		} catch (ConverterException ex) {
			LOG.error(ex.getMessage(), ex);
			throw new LoadReportException(ex.getMessage(), ex);
		}
	}

	/**
	 * Create a report object from an input stream Do a conversion if it is
	 * needed
	 * 
	 * @since 5.2
	 * 
	 * @param is
	 *            input stream
	 * @return the report object created from the input stream or null if cannot
	 *         be read
	 * @throws LoadReportException
	 *             if report object cannot be created
	 */
	public static Report loadReport(InputStream is) throws LoadReportException {
		try {
			String xml = readAsString(is);
			String convertedXml = ConverterChain.applyFromXml(xml);
			XStream xstream = XStreamFactory.createXStream();
			return (Report) xstream.fromXML(convertedXml);
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			throw new LoadReportException(ex.getMessage(), ex);
		}
	}

	/**
	 * Write a report object to an output stream
	 * 
	 * @param report
	 *            report object
	 * @param out
	 *            output stream
	 */
	public static void saveReport(Report report, OutputStream out) {
		XStream xstream = XStreamFactory.createXStream();
		xstream.toXML(report, out);
	}

	/**
	 * Write a report object to a file at specified path
	 * 
	 * @param report
	 *            report object
	 * @param path
	 *            file path
	 */
	public static void saveReport(Report report, String path) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path);
			saveReport(report, fos);
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			ex.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Write a xml text to a file at specified path
	 * 
	 * @param xml
	 *            xml text
	 * @param path
	 *            file path
	 */
	public static void saveReport(String xml, String path) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path);
			fos.write(xml.getBytes("UTF-8"));
			fos.flush();
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			ex.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert a report object to xml text
	 * 
	 * @param report
	 *            report object
	 * @return xml text
	 */
	public static String reportToXml(Report report) {
		XStream xstream = XStreamFactory.createXStream();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		xstream.toXML(report, bos);
		return bos.toString();
	}

	/**
	 * Test if report content given as parameter has a valid version, meaning is
	 * over 2.0 and no greater than current engine version
	 * 
	 * @param reportContent
	 *            report byte content
	 * @return one of REPORT_VALID, REPORT_INVALID_OLDER, REPORT_INVALID_NEWER
	 */
	public static byte isValidReportVersion(byte[] reportContent) {
		String reportVersion = getVersion(reportContent);
		return isValid(reportVersion);
	}

	/**
	 * Test if report given as parameter has a valid version, meaning is over
	 * 2.0 and no greater than current engine version
	 * 
	 * @param report
	 *            report object
	 * @return one of REPORT_VALID, REPORT_INVALID_OLDER, REPORT_INVALID_NEWER
	 */
	public static byte isValidReportVersion(Report report) {
		String reportVersion = report.getVersion();
		return isValid(reportVersion);
	}

	/**
	 * Test if report file given as parameter has a valid version, meaning is
	 * over 2.0 and no greater than current engine version
	 * 
	 * @param reportFile
	 *            report file
	 * @return one of REPORT_VALID, REPORT_INVALID_OLDER, REPORT_INVALID_NEWER
	 */
	public static byte isValidReportVersion(String reportFile) {
		String reportVersion = getVersion(reportFile);
		return isValid(reportVersion);
	}

	/**
	 * Test if string version given as parameter is valid, meaning is over 2.0
	 * and no greater than current engine version
	 * 
	 * @param reportVersion
	 *            version
	 * @return one of REPORT_VALID, REPORT_INVALID_OLDER, REPORT_INVALID_NEWER
	 */
	public static byte isValid(String reportVersion) {
		if (isOlderUnsupportedVersion(reportVersion)) {
			return REPORT_INVALID_OLDER;
		} else if (isNewerUnsupportedVersion(reportVersion)) {
			return REPORT_INVALID_NEWER;
		} else {
			return REPORT_VALID;
		}
	}

	/**
	 * Return true if version string is less than 2.0
	 * 
	 * @param version
	 *            version string
	 * @return true if version string is less than 2.0
	 */
	public static boolean isOlderUnsupportedVersion(String version) {
		return ((version == null) || "".equals(version) || version.startsWith("0") || version.startsWith("1"));
	}

	/**
	 * Return true if version string is newer than version of the report engine
	 * 
	 * @param version
	 *            version string
	 * @return true if version string is newer than version of the report engine
	 */
	public static boolean isNewerUnsupportedVersion(String version) {
		if ((version == null) || "".equals(version)) {
			return true;
		}
		String engineVersion = ReleaseInfoAdapter.getVersionNumber();
		String[] ev = engineVersion.split("\\.");
		String[] rv = version.split("\\.");
		return ((Integer.parseInt(ev[0]) < Integer.parseInt(rv[0])) || ((Integer.parseInt(ev[0]) == Integer.parseInt(rv[0])) && (Integer
				.parseInt(ev[1]) < Integer.parseInt(rv[1]))));
	}

	/**
	 * Compare two report versions strings
	 * 
	 * @param v1
	 *            first version string
	 * @param v2
	 *            second version string
	 * @return -1 if v1 less than v2, 0 if v1 equals v2, 1 if v1 greater than v2
	 */
	public static int compareVersions(String v1, String v2) {
		String[] v1a = v1.split("\\.");
		String[] v2a = v2.split("\\.");
		Integer v1M = Integer.parseInt(v1a[0]);
		Integer v2M = Integer.parseInt(v2a[0]);
		if (v1M < v2M) {
			return -1;
		} else if (v1M > v2M) {
			return 1;
		} else {
			Integer v1min = Integer.parseInt(v1a[1]);
			Integer v2min = Integer.parseInt(v2a[1]);
			if (v1min < v2min) {
				return -1;
			} else if (v1min > v2min) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	/**
	 * Get report version from report file
	 * 
	 * @param reportFile
	 *            report file
	 * @return report version
	 */
	public static String getVersion(String reportFile) {
		try {
			String text = readAsString(reportFile);
			return getVersionFromText(text);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Get report version from input stream to read report file
	 * 
	 * @param is
	 *            input stream
	 * @return report version
	 */
	public static String getVersion(InputStream is) {
		try {
			String text = readAsString(is);
			return getVersionFromText(text);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Get report version from file byte content
	 * 
	 * @param reportContent
	 *            file byte content
	 * @return report version
	 */
	public static String getVersion(byte[] reportContent) {
		String text = new String(reportContent);
		return getVersionFromText(text);
	}

	/**
	 * Get report version from xml text
	 * 
	 * @param reportText
	 *            xml text
	 * @return report version
	 */
	public static String getVersionFromText(String reportText) {
		String start = "<report version=\"";
		int index = reportText.indexOf(start);
		if (index == -1) {
			return null;
		}
		index = index + start.length();
		int lastIndex = reportText.indexOf("\"", index);
		if (lastIndex == -1) {
			return null;
		} else {
			return reportText.substring(index, lastIndex);
		}
	}

	/**
	 * Read a report file as string
	 * 
	 * @param reportPath
	 *            file path
	 * @return string file content
	 * @throws IOException
	 *             if file cannot be read
	 */
	public static String readAsString(String reportPath) throws IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(reportPath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			fileData.append(buf, 0, numRead);
		}
		reader.close();
		return fileData.toString();
	}

	/**
	 * Read data from input stream
	 * 
	 * @param is
	 *            input stream
	 * @return string content read from input stream
	 * @throws IOException
	 *             if cannot read from input stream
	 */
	public static String readAsString(InputStream is) throws IOException {
		try {
			// Scanner iterates over tokens in the stream, and in this case
			// we separate tokens using "beginning of the input boundary" (\A)
			// thus giving us only one token for the entire contents of the
			// stream
			return new Scanner(is, "UTF-8").useDelimiter("\\A").next();
		} catch (java.util.NoSuchElementException e) {
			return "";
		}
	}

	/**
	 * Get file name from a file path
	 * 
	 * @param filePath
	 *            file path
	 * @return file name
	 */
	public static String getFileName(String filePath) {
		if (filePath == null) {
			return filePath;
		}
		int index = filePath.lastIndexOf(File.separator);
		if (index == -1) {
			return filePath;
		}
		return filePath.substring(index + 1);
	}

	/**
	 * Get static images used by report
	 * 
	 * @param report
	 *            report
	 * @return a list of static images used by report
	 */
	public static List<String> getStaticImages(Report report) {
		List<String> images = new ArrayList<String>();
		ReportLayout layout = report.getLayout();
		List<Band> bands = layout.getBands();
		for (Band band : bands) {
			for (int i = 0, rows = band.getRowCount(); i < rows; i++) {
				List<BandElement> list = band.getRow(i);
				for (BandElement be : list) {
					if ((be instanceof ImageBandElement) && !(be instanceof ChartBandElement)
							&& !(be instanceof BarcodeBandElement)) {
						images.add(((ImageBandElement) be).getImage());
					}
				}
			}
		}
		if (layout.getBackgroundImage() != null) {
			images.add(layout.getBackgroundImage());
		}
		return images;
	}

	/**
	 * Get sql string from report object
	 * 
	 * @param report
	 *            report
	 * @return sql string from report object
	 */
	public static String getSql(Report report) {
		String sql;
		if (report.getSql() != null) {
			sql = report.getSql();
		} else {
			sql = report.getQuery().toString();
		}
		return sql;
	}

	/**
	 * Get sql string from report object with parameters values
	 * 
	 * @param report
	 *            report
	 * @param parameterValues
	 *            parameter values map
	 * @return sql string from report object with parameters values
	 */
	public static String getSql(Report report, Map<String, Object> parameterValues) {
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat dayFormat = new SimpleDateFormat("dd/MM/yyyy");
		String sql = getSql(report);
		if (parameterValues != null) {

			for (String pName : parameterValues.keySet()) {
				Object value = parameterValues.get(pName);
				StringBuilder text = new StringBuilder();
				if (value == null) {
					text.append("null");
					continue;
				}
				if (value instanceof Object[]) {
					Object[] values = (Object[]) value;
					text.append("(");
					for (int i = 0, size = values.length; i < size; i++) {
						Object obj = values[i];
						if (obj instanceof IdName) {
							text.append(((IdName) obj).getId());
						} else if (obj instanceof Date) {
							text.append(dayFormat.format((Date) obj));
						} else if (obj instanceof Timestamp) {
							Date date = new Date(((Timestamp) obj).getTime());
							text.append(timeFormat.format(date));
						} else {
							text.append(obj);
						}
						if (i < size - 1) {
							text.append(",");
						}
					}
					text.append(")");
				} else if (value instanceof IdName) {
					Object idName = ((IdName) value).getId();
					if (idName instanceof String) {
						text.append("'");
						text.append(value);
						text.append("'");
					} else {
						text.append(value);
					}
				} else if (value instanceof Date) {
					text.append("'");
					text.append(dayFormat.format((Date) value));
					text.append("'");
				} else if (value instanceof Timestamp) {
					text.append("'");
					Date date = new Date(((Timestamp) value).getTime());
					text.append(timeFormat.format(date));
					text.append("'");
				} else if (value instanceof String) {
					text.append("'");
					text.append(value);
					text.append("'");
				} else {
					text.append(value);
				}

				String tag = "${" + pName + "}";
				while (sql.indexOf(tag) != -1) {
					int index = sql.indexOf(tag);
					sql = sql.substring(0, index) + text.toString() + sql.substring(index + tag.length());
				}
			}
		}
		return sql;
	}

	/**
	 * Get expression elements from report layout
	 * 
	 * @param layout
	 *            report layout
	 * @return list of expression elements from report layout
	 */
	public static List<ExpressionBean> getExpressions(ReportLayout layout) {
		List<ExpressionBean> expressions = new LinkedList<ExpressionBean>();
		for (Band band : layout.getBands()) {
			for (int i = 0, rows = band.getRowCount(); i < rows; i++) {
				List<BandElement> list = band.getRow(i);
				for (BandElement be : list) {
					if (be instanceof ExpressionBandElement) {
						if (!expressions.contains((ExpressionBandElement) be)) {
							expressions.add(new ExpressionBean((ExpressionBandElement) be, band.getName()));
						}
					}
				}
			}
		}
		return expressions;
	}

	/**
	 * Get expression elements for a band from report layout
	 * 
	 * @param layout
	 *            report layout
	 * @param bandName
	 *            band name
	 * @return list of expression elements for band from report layout
	 */
	public static List<ExpressionBean> getExpressions(ReportLayout layout, String bandName) {
		List<ExpressionBean> expressions = new LinkedList<ExpressionBean>();
		for (Band band : layout.getBands()) {
			if (band.getName().equals(bandName)) {
				for (int i = 0, rows = band.getRowCount(); i < rows; i++) {
					List<BandElement> list = band.getRow(i);
					for (BandElement be : list) {
						if (be instanceof ExpressionBandElement) {
							if (!expressions.contains((ExpressionBandElement) be)) {
								expressions.add(new ExpressionBean((ExpressionBandElement) be, band.getName()));
							}
						}
					}
				}
				break;
			}
		}
		return expressions;
	}

	/**
	 * Get expression names from report layout
	 * 
	 * @param layout
	 *            report layout
	 * @return list of expression names from report layout
	 */
	public static List<String> getExpressionsNames(ReportLayout layout) {
		List<String> expressions = new LinkedList<String>();
		for (Band band : layout.getBands()) {
			for (int i = 0, rows = band.getRowCount(); i < rows; i++) {
				List<BandElement> list = band.getRow(i);
				for (BandElement be : list) {
					if (be instanceof ExpressionBandElement) {
						String expName = ((ExpressionBandElement) be).getExpressionName();
						if (!expressions.contains(expName)) {
							expressions.add(expName);
						}
					}
				}
			}
		}
		return expressions;
	}

	/**
	 * Test if sql from a report object and sql from all parameter sources (if any) are valid
	 * 
	 * @param con database connection
	 * @param report report object
	 * @return true if sql is valid, false if it is not valid
	 */
	public static boolean isValidSql(Connection con, Report report) {
		return (isValidSqlWithMessage(con, report) == null);
	}
	
	/**
	 * Test if sql from a report object and sql from all parameter sources (if any) are valid
	 * 
	 * @param con database connection
	 * @param report report object
	 * @return return message error if sql is not valid, null otherwise
	 */
	public static String isValidSqlWithMessage(Connection con, Report report) {
		String sql = getSql(report);
		List<QueryParameter> parameters = report.getParameters();
		String message = isValidSqlWithMessage(con, sql, parameters);
		if (message == null) {
			for (QueryParameter qp : parameters) {
				if (qp.isManualSource()) {
					String parMessage = isValidSqlWithMessage(con, qp.getSource(), parameters);
					if (parMessage != null) {
						parMessage = "Parameter '" + qp.getName() + "'\n" + parMessage;
						return parMessage;
					}
				}
 			}
		} 
		return message;		
	}
	
	/**
	 * Test if sql with parameters is valid
	 * 
	 * @param con database connection
	 * @param sql sql
	 * @return return message error if sql is not valid, null otherwise
	 */
	public static String isValidSqlWithMessage(Connection con, String sql, List<QueryParameter> parameters) {		
		try {
			QueryUtil qu = new QueryUtil(con, DialectUtil.getDialect(con));
			Map<String, QueryParameter> params = new HashMap<String, QueryParameter>();			
			for (QueryParameter qp : parameters) {
				params.put(qp.getName(), qp);
			}
			qu.getColumnNames(sql, params);
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			return ex.getMessage();
		}
		return null;
	}
	
	/**
	 * Get subreports for a report
	 * @param report current report
	 * @return list of subreports
	 */
	public static List<Report> getSubreports(Report report) {
		List<Report> subreports = new ArrayList<Report>();
		
		List<Band> bands = report.getLayout().getDocumentBands();
		for (Band band : bands) {
			for (int i = 0, rows = band.getRowCount(); i < rows; i++) {
				List<BandElement> list = band.getRow(i);
				for (int j = 0, size = list.size(); j < size; j++) {
					BandElement be = list.get(j);
					if (be instanceof ReportBandElement) {
						subreports.add(((ReportBandElement)be).getReport());
					}
				}
			}
		}	
		return subreports;
	}

	public static boolean isGroupBand(String bandName) {
		if (bandName == null) {
			return false;
		}
		return bandName.startsWith(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX)
				|| bandName.startsWith(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX);
	}

	public static boolean isDetailBand(String bandName) {
		if (bandName == null) {
			return false;
		}
		return bandName.equals(ReportLayout.DETAIL_BAND_NAME);
	}

	public static boolean isPageHeaderBand(String bandName) {
		if (bandName == null) {
			return false;
		}
		return bandName.equals(ReportLayout.PAGE_HEADER_BAND_NAME);
	}

	/**
	 * If a report layout contains a ForReportBandElement we must replace this
	 * element with more ReportBandElements This means inserting n-1 new
	 * columns, where n is the number of values return by sql inside
	 * ForReportBandElement
	 * 
	 * A ForReportBandElement is interpreted only at first appearance Column
	 * name from sql inside ForReportBandElement must be the same with a
	 * parameter name from subreport. Every value from sql will be considered
	 * the value for that parameter from subreport.
	 * 
	 * @param con
	 *            connection
	 * @param layout
	 *            report layout
	 * @param pBean
	 *            parameters bean
	 * @return a new report layout with ReportBandElement elements instead a
	 *         ForReportBandElement
	 * @throws Exception
	 *             if query fails
	 */
	private static ReportLayout getForReportLayout(Connection con, ReportLayout layout, ParametersBean pBean) throws Exception {
		ReportLayout convertedLayout = ObjectCloner.silenceDeepCopy(layout);

		List<Band> bands = convertedLayout.getDocumentBands();
		for (Band band : bands) {
			for (int i = 0, rows = band.getRowCount(); i < rows; i++) {
				List<BandElement> list = band.getRow(i);
				for (int j = 0, size = list.size(); j < size; j++) {
					BandElement be = list.get(j);
					if (be instanceof ForReportBandElement) {
						String sql = ((ForReportBandElement) be).getSql();
						Report report = ((ForReportBandElement) be).getReport();
						if ((sql == null) || sql.isEmpty()) {
							return convertedLayout;
						} else {
							QueryUtil qu = new QueryUtil(con, DialectUtil.getDialect(con));
							// column name is the same with parameter name
							String columnName = qu.getColumnNames(sql, pBean.getParams()).get(0);
							List<IdName> values = qu.getValues(sql, pBean.getParams(), pBean.getParamValues());
							int pos = j;
							for (int k = 0; k < values.size(); k++) {
								IdName in = values.get(k);
								if (k > 0) {
									band.insertColumn(pos);
								}
								Report newReport = ObjectCloner.silenceDeepCopy(report);								
								ReportLayout subReportLayout = ReportUtil.getReportLayoutForHeaderFunctions(newReport.getLayout());
								newReport.setLayout(subReportLayout);
								newReport.setName(report.getBaseName() + "_" + (k + 1) + ".report");
								newReport.getGeneratedParamValues().put(columnName, in.getId());
								band.setElementAt(new ReportBandElement(newReport), i, pos);
								pos++;
							}
							List<Integer> oldColumnsWidth = layout.getColumnsWidth();
							List<Integer> newColumnWidth = new ArrayList<Integer>();
							for (int m = 0; m < j; m++) {
								newColumnWidth.add(oldColumnsWidth.get(m));
							}
							for (int m = 0; m < values.size(); m++) {
								newColumnWidth.add(oldColumnsWidth.get(j));
							}
							for (int m = j + 1; m < size; m++) {
								newColumnWidth.add(oldColumnsWidth.get(m));
							}
							convertedLayout.setColumnsWidth(newColumnWidth);
							// we look just for first appearance
							return convertedLayout;
						}
					}
				}
			}
		}
		return convertedLayout;

	}
	
	/**
	 * If a report layout contains a function in header or in group header, we must add it as hidden in footer or group footer
	 * to be computed (any function is added to a new row)
	 * 
	 * @param layout report layout
	 * 
	 * @return a new report layout with header functions also inserted in footers	
	 */
	private static ReportLayout getReportLayoutForHeaderFunctions(ReportLayout layout) {
		ReportLayout convertedLayout = ObjectCloner.silenceDeepCopy(layout);
		
		List<FunctionBandElement> headerFunc = getHeaderFunctions(convertedLayout);
		Band footerBand = convertedLayout.getFooterBand();
		insertFunctionsInFooterBands(headerFunc, footerBand, convertedLayout.getColumnCount());
		
		List<ReportGroup> groups = convertedLayout.getGroups();
		for (ReportGroup rg : groups) {
			List<FunctionBandElement> groupHeaderFunc = getGroupHeaderFunctions(convertedLayout, rg.getName());
			Band gBand = convertedLayout.getBand(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX + rg.getName());
			insertFunctionsInFooterBands(groupHeaderFunc, gBand, convertedLayout.getColumnCount());
		}
		
		return convertedLayout;
	}
	
	
	/**
	 * Get dynamic report layout
	 * Report layout is dynamically modified in some situations like:
	 *    1. FOR Report Band Element
	 *    2. Functions in Header or Group Header
	 * 
	 * @param con database connection
	 * @param layout report layout
	 * @param pBean parameters bean
	 * 
	 * @return dynamic report layout
	 * @throws Exception
	 */
	public static ReportLayout getDynamicReportLayout(Connection con, ReportLayout layout, ParametersBean pBean) throws Exception {		
        ReportLayout convertedLayout = ReportUtil.getReportLayoutForHeaderFunctions(layout);
        // IMPORTANT: must take for report layout at the end because we save some transient data 
        // (see getForReportLayout: newReport.getGeneratedParamValues().put(columnName, in.getId());
        ReportLayout forConvertedLayout = ReportUtil.getForReportLayout(con, convertedLayout, pBean);
        return forConvertedLayout;
	}
	
	/**
	 * Test to see if a function is found in header band
	 * 
	 * @param layout report layout
	 * @return true if a function is found in header band, false otherwise
	 */
	public static boolean foundFunctionInHeader(ReportLayout layout) {
		Band header = layout.getHeaderBand();
		return foundFunctionInBand(header);
	}
	
	/**
	 * Test to see if a function is found in group header band
	 * 
	 * @param layout report layout
	 * @param groupName group name
	 * @return true if a function is found in group header band, false otherwise
	 */
	public static boolean foundFunctionInGroupHeader(ReportLayout layout, String groupName) {
		List<Band> groupHeaderBands = layout.getGroupHeaderBands();
		for (Band band : groupHeaderBands) {
			if (band.getName().equals(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX + groupName)) {				
				return foundFunctionInBand(band);
			}
		}
		return false;
	}
	
	/**
	 * Test to see if a function is found in any group header band
	 * 
	 * @param layout report layout
	 * @return true if a function is found in any group header band, false otherwise
	 */
	public static boolean foundFunctionInAnyGroupHeader(ReportLayout layout) {
		List<Band> groupHeaderBands = layout.getGroupHeaderBands();
		for (Band band : groupHeaderBands) {
			boolean found = foundFunctionInBand(band);
			if (found) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean foundFunctionInBand(Band band) {		
		for (int i=0, size = band.getRowCount(); i<size; i++) {
			List<BandElement> elements = band.getRow(i);
			for (BandElement be : elements) {
				if (be instanceof FunctionBandElement) {					
					return true;
				}
				if (be instanceof ExpressionBandElement) {
					if (((ExpressionBandElement)be).getExpression().contains("$F")) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private static List<FunctionBandElement> getHeaderFunctions(ReportLayout layout) {
		List<FunctionBandElement> result = new ArrayList<FunctionBandElement>();
		Band header = layout.getHeaderBand();
		for (int i=0, size = header.getRowCount(); i<size; i++) {
			List<BandElement> elements = header.getRow(i);
			for (BandElement be : elements) {
				if (be instanceof FunctionBandElement) {					
					result.add(ObjectCloner.silenceDeepCopy((FunctionBandElement)be));
				}
			}
		}
		return result;
	}
	
	private static List<FunctionBandElement> getGroupHeaderFunctions(ReportLayout layout, String groupName) {
		List<FunctionBandElement> result = new ArrayList<FunctionBandElement>();
		List<Band> groupHeaderBands = layout.getGroupHeaderBands();
		for (Band band : groupHeaderBands) {
			if (band.getName().equals(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX + groupName)) {				
				for (int i=0, size = band.getRowCount(); i<size; i++) {
					List<BandElement> elements = band.getRow(i);
					for (BandElement be : elements) {
						if (be instanceof FunctionBandElement) {					
							result.add(ObjectCloner.silenceDeepCopy((FunctionBandElement)be));
						}
					}
				}
				return result;
				
			}
		}
		return result;
	}
	
	private static void insertFunctionsInFooterBands(List<FunctionBandElement> functions, Band band, int cols) {
		for (FunctionBandElement fbe : functions) {	
			if (band.getRowCount() == 0) {
				band.insertFirstRow(0, cols);
			} else {
				band.insertRow(0);
			}
			fbe.setHideWhenExpression("1 > 0");
			band.getRow(0).set(0,fbe);
		}	
	}
	
	public static List<FunctionBandElement> getFunctionsFromExpression(String expression) {
		List<FunctionBandElement> result = new ArrayList<FunctionBandElement>();
		String regex = "[^\\$]*\\$F_([^_\\$]+)_([^_\\$\\s\\.\\+\\*/-]+)[^\\$]*";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(expression);
		while(m.find()) {                 
			String function = m.group(1);
			String column = m.group(2);
			FunctionBandElement fbe = new FunctionBandElement(function, column);
			result.add(fbe);
		}				
		return result;
	}		
	
	
	public static List<String> getKeys(ReportLayout layout) {
		List<String> result = new ArrayList<String>();
		List<Band> bands = layout.getBands();
		for (Band band : bands) {
			for (int i=0, size = band.getRowCount(); i<size; i++) {
				List<BandElement> elements = band.getRow(i);
				for (BandElement be : elements) {
					if (be != null) {
						if (be.getText().contains(I18nString.MARKUP)) {							
							result.add(StringUtil.getKey(be.getText()));	
						}
						if (be instanceof FieldBandElement) {
							String pattern = ((FieldBandElement)be).getPattern();
							if ((pattern != null) && (pattern.indexOf(I18nString.MARKUP) != -1)) {								
								result.add(StringUtil.getKey(pattern));
							}
						}
					}
				}
			}
		}
		return result;
	}		

}
