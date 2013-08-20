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
public class GroupByFunctionColumn extends Column {
    
    public static final String MIN = "Min"; 
    public static final String MAX = "Max"; 
    public static final String AVG = "Avg"; 
    public static final String COUNT = "Count"; 
    public static final String SUM = "Sum";
    
    private static final List<String> allFunctions;
    
    static {
        allFunctions = new ArrayList<String>();
        allFunctions.add(MIN);
        allFunctions.add(MAX);
        allFunctions.add(AVG);
        allFunctions.add(COUNT);
        allFunctions.add(SUM);
    }
    
    private String function;
    private boolean expression = false;

    public GroupByFunctionColumn(Column column, String function) {
        super(column.getTable(), column.getName(), column.getAlias());
        if ((column instanceof ExpressionColumn) ||
            ((column instanceof GroupByFunctionColumn) && ( ((GroupByFunctionColumn)column).isExpression()) ) ) {
           expression = true;
        }
        setFunction(function);
    }

    public String getFunction() {
        return function;
    }

    /**
     * Returns an array containing all of the functions supported by GroupByFunctionColumn.
     * @return all functions
     */
    public static List<String> getAllFunctions() {
        return allFunctions;
    }

    public static boolean existsFunction(String function) {
        return allFunctions.contains(function);
    }

    public void write(Output out) {
        if (function == null) {
            super.write(out);
            return;
        }

        out.print(function);
        out.print('(');
        super.write(out);
        out.print(')');
    }

    private void setFunction(String function) {
        if (!existsFunction(function)) {
            throw new IllegalArgumentException("Function '" + function + "' is not supported");
        }
        this.function = function;
    }

    public boolean isExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        if (!isExpression()) {
            throw new IllegalArgumentException("Cannot set expression on a non-expression column!");
        }
        name = expression;
    }

    public boolean equals(Object o) {
        boolean result = super.equals(o);
        if (!result) {
            return false;
        }

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final GroupByFunctionColumn that = (GroupByFunctionColumn) o;

        if (function != null ? !function.equals(that.function) : that.function != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (function != null ? function.hashCode() : 0);
        return result;
    }

}
