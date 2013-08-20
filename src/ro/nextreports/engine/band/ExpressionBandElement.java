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
 * User: mihai.panaitescu
 * Date: 03-May-2010
 * Time: 12:56:12
 */
public class ExpressionBandElement extends FieldBandElement {

    protected String expressionName;
    protected String expression;

    public ExpressionBandElement(String expressionName, String expression) {
        super("$E{exp}");
        this.expressionName = expressionName;
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
    	this.expression = expression;
    	setText("$E{" + expressionName + "}");
    }

    public String getExpressionName() {
        return expressionName;
    }

    public void setExpressionName(String expressionName) {
        this.expressionName = expressionName;
        setText("$E{" + expressionName + "}");
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ExpressionBandElement that = (ExpressionBandElement) o;

        if (expression != null ? !expression.equals(that.expression) : that.expression != null) return false;
        if (expressionName != null ? !expressionName.equals(that.expressionName) : that.expressionName != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (expressionName != null ? expressionName.hashCode() : 0);
        result = 31 * result + (expression != null ? expression.hashCode() : 0);
        return result;
    }
}
