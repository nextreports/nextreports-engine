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
package ro.nextreports.engine.condition;

import java.io.Serializable;

/**
 * User: mihai.panaitescu
 * Date: 23-Apr-2010
 * Time: 11:12:50
 */
public class BandElementCondition implements Serializable {
	
	private static final long serialVersionUID = -8331510661912368816L;

    private ConditionalExpression expression;
    private int property;
    private Serializable propertyValue;

    public BandElementCondition(ConditionalExpression expression, int property, Serializable propertyValue) {
        this.expression = expression;
        this.property = property;
        this.propertyValue = propertyValue;
    }

    public ConditionalExpression getExpression() {
        return expression;
    }

    public int getProperty() {
        return property;
    }

    public Serializable getPropertyValue() {
        return propertyValue;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BandElementCondition that = (BandElementCondition) o;

        if (property != that.property) return false;
        if (expression != null ? !expression.equals(that.expression) : that.expression != null) return false;
        if (propertyValue != null ? !propertyValue.equals(that.propertyValue) : that.propertyValue != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (expression != null ? expression.hashCode() : 0);
        result = 31 * result + property;
        result = 31 * result + (propertyValue != null ? propertyValue.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "BandElementCondition{" +
                "expression=" + expression +
                ", property='" + property + '\'' +
                ", propertyValue=" + propertyValue +
                '}';
    }
}
