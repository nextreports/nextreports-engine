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
package ro.nextreports.engine;

import java.awt.Color;
import java.awt.Font;

import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.BarcodeBandElement;
import ro.nextreports.engine.band.Border;
import ro.nextreports.engine.band.ChartBandElement;
import ro.nextreports.engine.band.ColumnBandElement;
import ro.nextreports.engine.band.ExpressionBandElement;
import ro.nextreports.engine.band.ForReportBandElement;
import ro.nextreports.engine.band.FunctionBandElement;
import ro.nextreports.engine.band.HyperlinkBandElement;
import ro.nextreports.engine.band.ImageBandElement;
import ro.nextreports.engine.band.ImageColumnBandElement;
import ro.nextreports.engine.band.ParameterBandElement;
import ro.nextreports.engine.band.ReportBandElement;
import ro.nextreports.engine.band.RowElement;
import ro.nextreports.engine.band.VariableBandElement;
import ro.nextreports.engine.chart.Chart;
import ro.nextreports.engine.chart.ChartTitle;
import ro.nextreports.engine.chart.ChartType;
import ro.nextreports.engine.condition.BandElementCondition;
import ro.nextreports.engine.persistence.TablePersistentObject;
import ro.nextreports.engine.querybuilder.MyRow;
import ro.nextreports.engine.querybuilder.sql.Column;
import ro.nextreports.engine.querybuilder.sql.ExpressionColumn;
import ro.nextreports.engine.querybuilder.sql.GroupByFunctionColumn;
import ro.nextreports.engine.querybuilder.sql.JoinCriteria;
import ro.nextreports.engine.querybuilder.sql.MatchCriteria;
import ro.nextreports.engine.querybuilder.sql.Order;
import ro.nextreports.engine.querybuilder.sql.WildCardColumn;
import ro.nextreports.engine.queryexec.QueryParameter;
import ro.nextreports.engine.template.ChartTemplate;
import ro.nextreports.engine.template.ReportTemplate;
import ro.nextreports.engine.util.xstream.FontConverter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Aug 24, 2006
 * Time: 2:09:32 PM
 */

public class XStreamFactory {

    /**
     * Create XStream for query and report load/save.
     * 
     * @return XStream
     */
     public static XStream createXStream() {
        XStream xstream = new XStream(new DomDriver("UTF-8"));
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.registerConverter(new FontConverter());

        xstream.alias("report", Report.class);
        xstream.alias("chart", Chart.class);
        xstream.useAttributeFor(Report.class, "version");
        xstream.useAttributeFor(Chart.class, "version");

        xstream = createQueryXStream(xstream);
        xstream = createReportXStream(xstream);
        xstream = createChartXStream(xstream);

        return xstream;
    }

    /**
     * Create XStream for template load/save.
     * 
     * @return XStream
     */
    public static XStream createTemplateXStream() {
        XStream xstream = new XStream(new DomDriver("UTF-8"));
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.registerConverter(new FontConverter());
        
        xstream.alias("template", ReportTemplate.class);
        xstream.useAttributeFor(ReportTemplate.class, "version");
        xstream.alias("band-element", BandElement.class);
        xstream.alias("column-band-element", ColumnBandElement.class);
        
        return xstream;
    }
    
    /**
     * Create XStream for chart template load/save.
     * 
     * @return XStream
     */
    public static XStream createChartTemplateXStream() {
        XStream xstream = new XStream(new DomDriver("UTF-8"));
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.registerConverter(new FontConverter());
        
        xstream.alias("chart-template", ChartTemplate.class);
        xstream.useAttributeFor(ChartTemplate.class, "version");
        xstream.alias("color", Color.class);
        
        return xstream;
    }

    private static XStream createQueryXStream(XStream xstream) {
        xstream.alias("column", Column.class);
        xstream.alias("join-criteria", JoinCriteria.class);
        xstream.alias("match-criteria", MatchCriteria.class);
        xstream.alias("parameter", QueryParameter.class);
        xstream.alias("designer-table", TablePersistentObject.class);
        xstream.alias("order", Order.class);
        xstream.alias("row", MyRow.class);
        xstream.alias("group-by-column", GroupByFunctionColumn.class);
        xstream.alias("expression-column", ExpressionColumn.class);
        xstream.alias("wildcard-column", WildCardColumn.class);
        
        return xstream;
    }

    private static XStream createReportXStream(XStream xstream) {
        xstream.alias("band-element", BandElement.class);
        xstream.alias("row-element", RowElement.class);
        xstream.alias("var-band-element", VariableBandElement.class);
        xstream.alias("param-band-element", ParameterBandElement.class);
        xstream.alias("col-band-element", ColumnBandElement.class);
        xstream.alias("exp-band-element", ExpressionBandElement.class);
        xstream.alias("func-band-element", FunctionBandElement.class);
        xstream.alias("image-band-element", ImageBandElement.class);
        xstream.alias("image-col-band-element", ImageColumnBandElement.class);
        xstream.alias("chart-band-element", ChartBandElement.class);
        xstream.alias("report-band-element", ReportBandElement.class);
        xstream.alias("for-report-band-element", ForReportBandElement.class);
        xstream.alias("hyperlink-band-element", HyperlinkBandElement.class);
        xstream.alias("barcode-band-element", BarcodeBandElement.class);
        xstream.alias("condition", BandElementCondition.class);        
        xstream.alias("border", Border.class);
        xstream.alias("font", Font.class);
        xstream.alias("color", Color.class);

        xstream.alias("group", ReportGroup.class);
        xstream.alias("band", Band.class);
      
//        xstream.addImplicitCollection(FormattingConditions.class, "conditions");
        
        return xstream;
    }

    private static XStream createChartXStream(XStream xstream) {
        xstream.alias("chart-title", ChartTitle.class);
        xstream.alias("chart-type", ChartType.class);
        
        return xstream;
    }
    
}
