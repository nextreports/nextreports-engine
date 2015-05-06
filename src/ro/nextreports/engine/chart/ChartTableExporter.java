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

import java.util.ArrayList;

import ro.nextreports.engine.TableExporter;
import ro.nextreports.engine.exporter.exception.NoDataFoundException;
import ro.nextreports.engine.exporter.util.TableData;
import ro.nextreports.engine.exporter.util.function.AbstractGFunction;
import ro.nextreports.engine.exporter.util.function.FunctionFactory;
import ro.nextreports.engine.exporter.util.function.FunctionUtil;
import ro.nextreports.engine.exporter.util.function.GFunction;
import ro.nextreports.engine.i18n.I18nUtil;
import ro.nextreports.engine.queryexec.QueryException;
import ro.nextreports.engine.queryexec.QueryResult;
import ro.nextreports.engine.util.StringUtil;

/**
 * User: mihai.panaitescu
 * Date: 07-Apr-2010
 * Time: 13:15:16
 */
public class ChartTableExporter implements ChartExporter, TableExporter {

    private QueryResult result;
    private Chart chart;
    
    private TableData data;   
    private String language;
    // some columns may be strings (like 5th in bubble chart) and by default computed values is 1
    // but inside server 'Save To Excel' we want to show the actual string
    private boolean showStrings;

    public ChartTableExporter(QueryResult result, Chart chart, String language) {
        this.result = result;
        this.chart = chart;
        this.language = language;
        data = new TableData();
        data.setStyle(null);
    }
    
    public boolean isShowStrings() {
		return showStrings;
	}

	public void setShowStrings(boolean showStrings) {
		this.showStrings = showStrings;
	}

	public boolean export() throws QueryException, NoDataFoundException {
        testForData();
        createData();
        return true;
    }

    private void testForData() throws QueryException, NoDataFoundException {
        // for procedure call we do not know the row count (is -1)
        if (result == null || result.getColumnCount() <= 0 || result.getRowCount() == 0) {
            throw new NoDataFoundException();
        }
    }

    private void createData() throws QueryException {

        String xColumn = chart.getXColumn();
        String xPattern = chart.getXPattern();
        Object previous = null;
        String lastXValue = "";

        int row = 0;
        int chartsNo = chart.getYColumns().size();
        GFunction[] functions = new GFunction[chartsNo];
        data.getHeader().add(chart.getXColumn());
        for (int i = 0; i < chartsNo; i++) {
            functions[i] = FunctionFactory.getFunction(chart.getYFunction());
            String column = chart.getYColumns().get(i);
            if (!AbstractGFunction.NOOP.equals(functions[i].getName()))  {
                column = functions[i].getName() + "(" + column + ")";
            }
            data.getHeader().add(column);
        }

        // see JsonExporter
        while (result.hasNext()) {

            Object[] objects = new Object[chartsNo];
            Number[] computedValues = new Number[chartsNo];
            for (int i = 0; i < chartsNo; i++) {
                if (chart.getYColumns().get(i) != null) {
                    objects[i] = result.nextValue(chart.getYColumns().get(i));
                    Number value = null;
                    if (objects[i] instanceof Number) {
                        value = (Number) objects[i];
                    } else {
                        value = 1;
                    }
                    if (value == null) {
                    	value = 0;
                    }
                    computedValues[i] = value;
                }
            }

            Object xValue;
            if (row == 0) {
                xValue = result.nextValue(xColumn);
                lastXValue = getStringValue(xColumn, xPattern);
            } else {
                xValue = previous;
            }
            Object newXValue = result.nextValue(xColumn);

            boolean add = false;
            // no function : add the value
            if (AbstractGFunction.NOOP.equals(functions[0].getName())) {
                lastXValue = getStringValue(xColumn, xPattern);
                add = true;

                // compute function
            } else {
                boolean equals = FunctionUtil.parameterEquals(xValue, newXValue);
                if (equals) {
                    for (int i = 0; i < chartsNo; i++) {
                        functions[i].compute(objects[i]);
                    }
                } else {
                    for (int i = 0; i < chartsNo; i++) {
                        add = true;
                        computedValues[i] = (Number) functions[i].getComputedValue();
                        functions[i].reset();
                        functions[i].compute(objects[i]);
                    }
                }
            }

            if (add) {
                ArrayList<Object> rowData = new ArrayList<Object>();
                data.getData().add(rowData);
                rowData.add(lastXValue);
                for (int i = 0; i < chartsNo; i++) {
                	if (showStrings && !(objects[i] instanceof Number)) {
                		rowData.add(objects[i]);
                	} else {
                		rowData.add(computedValues[i]);
                	}
                }
                lastXValue = getStringValue(xColumn, xPattern);
            }
            row++;
            previous = newXValue;
        }

        // last group
        if (!AbstractGFunction.NOOP.equals(functions[0].getName())) {
            Number sum = 0;
            ArrayList<Object> rowData = new ArrayList<Object>();
            data.getData().add(rowData);
            rowData.add(lastXValue);
            for (int i = 0; i < chartsNo; i++) {
                Number value = (Number) functions[i].getComputedValue();
                rowData.add(value);
            }
        }

    }

    private String getStringValue(String column, String pattern) throws QueryException {
        Object xObject = result.nextValue(column);
        return StringUtil.getValueAsString(xObject, pattern, I18nUtil.getLanguageByName(chart, language));
    }  
	
	public TableData getTableData() {
    	return data;
    }
}
