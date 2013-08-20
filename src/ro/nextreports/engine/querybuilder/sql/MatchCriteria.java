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
package ro.nextreports.engine.querybuilder.sql;

import java.util.ArrayList;
import java.util.List;

import ro.nextreports.engine.querybuilder.sql.output.Output;


/**
 * @author Decebal Suiu
 */
public class MatchCriteria extends Criteria {

    private static final long serialVersionUID = -7884631436540725937L;

    public static final String EQUALS = "=";
    public static final String NOT_EQUALS = "<>";
    public static final String GREATER = ">";
    public static final String GREATER_EQUALS = ">=";
    public static final String LESS = "<";
    public static final String LESS_EQUALS = "<=";
    public static final String LIKE = "LIKE";
    public static final String NOT_LIKE = "NOT LIKE";
    public static final String BETWEEN = "BETWEEN";
    public static final String AND = "AND";
    public static final String IN = "IN";
    public static final String NOT_IN = "NOT IN";
    public static final String IS_NULL = "IS NULL";
    public static final String IS_NOT_NULL = "IS NOT NULL";

    public static final String SEPARATOR = ",";

    private static final List<String> allOperators;

    private Column column;
    private String value;
    private String value2;
    private String operator;
    private boolean parameter;
    private boolean parameter2;

    // !!! Important : GREATER_EQUALS must be before GREATER
    // and  LESS_EQUALS must be before LESS (see method getOperatorValue(String value))
    static {
        allOperators = new ArrayList<String>();
        allOperators.add(EQUALS);
        allOperators.add(NOT_EQUALS);
        allOperators.add(GREATER_EQUALS);
        allOperators.add(GREATER);
        allOperators.add(LESS_EQUALS);
        allOperators.add(LESS);        
        allOperators.add(LIKE);
        allOperators.add(NOT_LIKE);
        allOperators.add(BETWEEN);
        allOperators.add(IN);
        allOperators.add(NOT_IN);
        allOperators.add(IS_NULL);
        allOperators.add(IS_NOT_NULL);
    }

    public MatchCriteria(Column column, String value) {
        this(column, EQUALS, value);
    }

    public MatchCriteria(Column column, String operator, float value) {
        this(column, operator, String.valueOf(value));
    }

    public MatchCriteria(Column column, String operator, float value, float value2) {
        this(column, operator, String.valueOf(value), String.valueOf(value2));
    }

    public MatchCriteria(Column column, String operator, int value) {
        this(column, operator, String.valueOf(value));
    }

    public MatchCriteria(Column column, String operator, int value, int value2) {
        this(column, operator, String.valueOf(value), String.valueOf(value2));
    }

    public MatchCriteria(Column column, String operator, boolean value) {
        this(column, operator, String.valueOf(value));
    }

    public MatchCriteria(Column column, String operator, boolean value, boolean value2) {
        this(column, operator, String.valueOf(value), String.valueOf(value2));
    }

    public MatchCriteria(Table table, String columnName, String operator, boolean value) {
        this(table.getColumn(columnName), operator, value);
    }

    public MatchCriteria(Table table, String columnName, String operator, boolean value, boolean value2) {
        this(table.getColumn(columnName), operator, value, value2);
    }

    public MatchCriteria(Table table, String columnName, String operator, int value) {
        this(table.getColumn(columnName), operator, value);
    }

    public MatchCriteria(Table table, String columnName, String operator, int value, int value2) {
        this(table.getColumn(columnName), operator, value, value2);
    }

    public MatchCriteria(Table table, String columnName, String operator, float value) {
        this(table.getColumn(columnName), operator, value);
    }

    public MatchCriteria(Table table, String columnName, String operator, float value, float value2) {
        this(table.getColumn(columnName), operator, value, value2);
    }

    public MatchCriteria(Table table, String columnName, String operator, String value) {
        this(table.getColumn(columnName), operator, value);
    }

    public MatchCriteria(Table table, String columnName, String operator, String value, String value2) {
        this(table.getColumn(columnName), operator, value, value2);
    }

    public MatchCriteria(Column column, String operator, String value) {
        this.column = column;
        this.value = value;
        setOperator(operator);
    }

    public MatchCriteria(Column column, String operator, String value, String value2) {
        this.column = column;
        this.value = value;
        this.value2 = value2;
        setOperator(operator);
    }

    public boolean isParameter() {
        return parameter;
    }

    public void setParameter(boolean parameter) {
        this.parameter = parameter;
    }

    public boolean isParameter2() {
        return parameter2;
    }


    public void setParameter2(boolean parameter2) {
        this.parameter2 = parameter2;
    }

    public Column getColumn() {
        return column;
    }

    public String getOperator() {
        return operator;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        if ((value != null) && value.startsWith("(") && value.endsWith(")")) {
            value=value.substring(1, value.length()-1);
        }
        return value;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getValue2() {
        if ((value2 != null) && value2.startsWith("(") && value2.endsWith(")")) {
            value2=value2.substring(1, value2.length()-1);
        }
        return value2;
    }

    /**
     * Returns an array containing all of the operators supported by MatchCriteria.
     */
    public static List getAllOperators() {
        return allOperators;
    }

    public static boolean existsOperator(String operator) {
        return allOperators.contains(operator);
    }

    public void write(Output out) {
        out.print(column)
                .print(' ')
                .print(operator)
                .print(' ')
                .print(getFullValue());
    }

    public void setOperator(String operator) {
        if (!existsOperator(operator)) {
            throw new IllegalArgumentException("Operator '" + operator + "' is not supported");
        }
        this.operator = operator;
    }


    public String getFullValue() {
        //System.out.println("****** TYPE="+column.getType());
        StringBuilder sb = new StringBuilder();
        if (!value.startsWith(ParameterConstants.START_PARAM)) {
            if (LIKE.equals(operator) || NOT_LIKE.equals(operator)) {
                if (!value.startsWith("'")) {
                    sb.append("'");
                }
                sb.append(value);
                if (!value.endsWith("'")) {
                    sb.append("'");
                }
            } else if (IN.equals(operator) || NOT_IN.equals(operator)) {

                if (!value.startsWith("(")) {
                    sb.append("(");
                }
                String[] values = value.split(SEPARATOR);
                for (int i = 0, size = values.length; i < size; i++) {
                    if ("java.lang.String".equals(column.getType())) {
                       sb.append("'");
                    }
                    sb.append(values[i]);
                    if (i < size - 1) {
                        if ("java.lang.String".equals(column.getType())) {
                         sb.append("'");
                        }
                        sb.append(",");
                    }
                }
                if ("java.lang.String".equals(column.getType())) {
                    sb.append("'");
                }
                if (!value.endsWith(")")) {
                    sb.append(")");
                }
            } else if (BETWEEN.equals(operator)) {
                sb.append(value);
                sb.append(" ");
                sb.append(AND);
                sb.append(" ");
                sb.append(value2);
            } else {
                sb.append(value);
            }
        } else {
            if (BETWEEN.equals(operator)) {
                sb.append(value);
                sb.append(" ");
                sb.append(AND);
                sb.append(" ");
                sb.append(value2);
            } else {
                sb.append(value);
            }
        }
        return sb.toString();
    }

    public static String[] getOperatorValue(String value) {
        String[] result = new String[3];
        for (String op : allOperators) {
            int index = value.indexOf(op);
            if (index == 0) {
                result[0] = op;
                if (!Operator.isDoubleValue(op)) {
                    result[1] = value.substring(op.length() + 1);                    
                } else {
                    int andIndex = value.indexOf(" " + AND);
                    result[1] = value.substring(op.length() + 1, andIndex);
                    result[2] = value.substring(andIndex + AND.length() + 2);
                }
                break;
            }
        }
        return result;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatchCriteria that = (MatchCriteria) o;

        if (column != null ? !column.equals(that.column) : that.column != null) return false;
        if (getFullValue() !=  null ? !getFullValue().equals(that.getFullValue()) : that.getFullValue() !=  null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (column != null ? column.hashCode() : 0);
        result = 31 * result + (getFullValue() != null ? getFullValue().hashCode() : 0);        
        return result;
    }
}
