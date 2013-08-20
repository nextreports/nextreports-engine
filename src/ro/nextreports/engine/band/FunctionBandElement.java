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
package ro.nextreports.engine.band;

/**
 * @author Decebal Suiu
 */
public class FunctionBandElement extends FieldBandElement {

    protected String function;
    protected String column;
    protected boolean isExpression;

    public FunctionBandElement(String function, String column) {
        this(function, column, false);
    }

    public FunctionBandElement(String function, String column, boolean isExpression) {
        super("$F{" + function + "(" + column + ")}");
        this.function = function;
        this.column = column;
        this.isExpression = isExpression;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
        setText("$F{" + function + "(" + column + ")}");

    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
        setText("$F{" + function + "(" + column + ")}");
    }

    public boolean isExpression() {
        return isExpression;
    }

    public void setExpression(boolean expression) {
        isExpression = expression;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        FunctionBandElement that = (FunctionBandElement) o;

        if (isExpression != that.isExpression) return false;
        if (column != null ? !column.equals(that.column) : that.column != null) return false;
        if (function != null ? !function.equals(that.function) : that.function != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (function != null ? function.hashCode() : 0);
        result = 31 * result + (column != null ? column.hashCode() : 0);
        result = 31 * result + (isExpression ? 1 : 0);
        return result;
    }
}
