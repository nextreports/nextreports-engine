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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ro.nextreports.engine.XStreamFactory;
import ro.nextreports.engine.chart.Chart;

/**
 * User: mihai.panaitescu
 * Date: 12-Jan-2010
 * Time: 16:59:41
 */
public class NextChartUtil {

    public static byte CHART_VALID = 1;
    public static byte CHART_INVALID_NEWER = 2;

    public static Chart loadChart(InputStream is) {
        XStream xstream = XStreamFactory.createXStream();
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(is, "UTF-8");
            return (Chart) xstream.fromXML(reader);
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
    }

    public static String getVersion(String chartFile) {
        FileInputStream fis;
        try {
            String text = ReportUtil.readAsString(chartFile);
            return getVersionFromText(text);
        } catch (IOException e) {
            return null;
        }
    }

    public static String getVersion(byte[] reportContent) {
        String text = new String(reportContent);
        return getVersionFromText(text);
    }

     public static String getVersionFromText(String reportText) {
        String start = "<chart version=\"";
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

     public static byte isValidChartVersion(byte[] reportContent) {
        String reportVersion = getVersion(reportContent);
        return isValid(reportVersion);
    }

    public static byte isValidChartVersion(Chart chart) {
        String chartVersion = chart.getVersion();
        return isValid(chartVersion);
    }

    public static byte isValidChartVersion(String reportFile) {
        String reportVersion = getVersion(reportFile);
        return isValid(reportVersion);
    }

    public static byte isValid(String chartVersion) {
       if (ReportUtil.isNewerUnsupportedVersion(chartVersion)) {
            return CHART_INVALID_NEWER;
        } else {
            return CHART_VALID;
        }
    }
}
