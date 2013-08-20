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
import java.io.ObjectStreamException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import ro.nextreports.engine.condition.exception.ConditionalException;

/**
 * User: mihai.panaitescu
 * Date: 23-Apr-2010
 * Time: 10:32:26
 */
public class ConditionalExpression implements Serializable {

    public static DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    protected transient Serializable leftOperand;
    protected transient String operator;
    protected transient Serializable rightOperand;
    protected transient Serializable rightOperand2;

    private String text;

    public ConditionalExpression(String operator) {
        this.operator = operator;
        setText();
    }

    // ${val}
    public void setLeftOperand(Serializable leftOperand) {
        this.leftOperand = leftOperand;
    }

    public void setRightOperand(Serializable rightOperand) {
        this.rightOperand = rightOperand;
        setText();
    }

    public void setRightOperand2(Serializable rightOperand2) {
        this.rightOperand2 = rightOperand2;
        setText();
    }

    public Serializable getLeftOperand() {
        return leftOperand;
    }

    public String getOperator() {
        return operator;
    }
    
    public void setOperator(String operator) {
    	this.operator = operator;
        setText();
    }

    public Serializable getRightOperand() {
        return rightOperand;
    }

    public Serializable getRightOperand2() {
        return rightOperand2;
    }

    public void setText() {
        String right = "";
        String right2 = "";
        if (rightOperand != null) {
            if (rightOperand instanceof Date) {
                right = DATE_FORMAT.format(rightOperand);
            } else {
                right = rightOperand.toString();
            }
            if (rightOperand2 != null) {
                if (rightOperand2 instanceof Date) {
                    right2 = DATE_FORMAT.format(rightOperand2);
                } else {
                    right2 = rightOperand2.toString();
                }
            }
        }
        text = "${val} " + operator + " " + right + " " + right2;
    }

    public String getText() {
        return text;
    }

    private void parse(String text) {
        if (text != null) {
            String[] elements = text.split(" ");            
            if (text.contains(ConditionalOperator.BETWEEN)) {
                // right operand are numbers (cannot contains space)
                operator = elements[1];
                rightOperand = getOperand(elements[2]);
                rightOperand2 = getOperand(elements[3]);
            } else {
                // right operand may contain space (if string)
                // space in right operand : add all other elements to third element
                if (elements.length > 3) {
                    for (int i = 3; i < elements.length; i++) {
                        elements[2] += " " + elements[i];
                    }
                }
                operator = elements[1];
                rightOperand = getOperand(elements[2]);
            }
        }
    }

    public Serializable getOperand(String operand) {
    	if (operand == null) {
    		return null;
    	}
        Serializable result = null;
        if (operand.contains("/")) {
            // date
            try {
                result = DATE_FORMAT.parse(operand);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else  if (operand.equals("true")) {
        	result = Boolean.TRUE;
        } else  if (operand.equals("false")) {
        	result = Boolean.FALSE;
        } else {
            try {
                result = Double.parseDouble(operand);
            } catch (NumberFormatException ex) {
                result = operand;
            }
        }
        return result;
    }

    public boolean evaluate() throws ConditionalException {
        if (!ConditionalOperator.isValid(operator)) {
            throw new ConditionalException("Invalid operator : " + operator);
        }
        if (!isValidOperands(leftOperand, rightOperand)) {        	
            throw new ConditionalException("Invalid operands left= " + leftOperand + " right=" + rightOperand);
        }

        if ((leftOperand instanceof Boolean) && !ConditionalOperator.isBoolean(operator)) {
            throw new ConditionalException("Invalid operator : " + operator + " for Boolean operands.");
        }

        if ((leftOperand instanceof String) && !ConditionalOperator.isString(operator)) {
            throw new ConditionalException("Invalid operator : " + operator + " for String operands.");
        }

        if (ConditionalOperator.EQUAL.equals(operator)) {
            if (leftOperand instanceof Number){
                return ((Number)leftOperand).doubleValue() == ((Number)rightOperand).doubleValue();
            } else {
                return leftOperand.equals(rightOperand);
            }
        } else if (ConditionalOperator.NOT_EQUAL.equals(operator)){
        	if (leftOperand instanceof Number){
        		return ((Number)leftOperand).doubleValue() != ((Number)rightOperand).doubleValue();
        	} else {
        		return !leftOperand.equals(rightOperand);
        	}
        } else if (ConditionalOperator.GREATER.equals(operator)) {
            if (leftOperand instanceof Number){
                return ((Number)leftOperand).doubleValue() > ((Number)rightOperand).doubleValue();
            } else if (leftOperand instanceof Date){
                return ((Date)leftOperand).compareTo((Date)rightOperand) > 0;
            }
        } else if (ConditionalOperator.GREATER_EQUAL.equals(operator)) {
            if (leftOperand instanceof Number){
                return ((Number)leftOperand).doubleValue() >= ((Number)rightOperand).doubleValue();
            } else if (leftOperand instanceof Date){
                return ((Date)leftOperand).compareTo((Date)rightOperand) >= 0;
            }
        } else if (ConditionalOperator.LESS.equals(operator)) {
            if (leftOperand instanceof Number){
                return ((Number)leftOperand).doubleValue() < ((Number)rightOperand).doubleValue();
            } else if (leftOperand instanceof Date){
                return ((Date)leftOperand).compareTo((Date)rightOperand) < 0;
            }
        } else if (ConditionalOperator.LESS_EQUAL.equals(operator)) {
            if (leftOperand instanceof Number){
                return ((Number)leftOperand).doubleValue() <= ((Number)rightOperand).doubleValue();
            } else if (leftOperand instanceof Date){
                return ((Date)leftOperand).compareTo((Date)rightOperand) <= 0;
            }
        }  else if (ConditionalOperator.BETWEEN.equals(operator)) {
            if (leftOperand instanceof Number){
                return ( ((Number)leftOperand).doubleValue() >= ((Number)rightOperand).doubleValue() ) &&
                       ( ((Number)leftOperand).doubleValue() <= ((Number)rightOperand2).doubleValue() );
            } else if (leftOperand instanceof Date){
                return ( ((Date)leftOperand).compareTo((Date)rightOperand) >= 0 ) &&
                       ( ((Date)leftOperand).compareTo((Date)rightOperand2) <= 0 ); 
            }
        }

        throw new ConditionalException("Invalid condition : " + toString());

    }

    private boolean isValidOperands(Serializable leftOperand, Serializable rightOperand) {
        return ((leftOperand instanceof Number) && (rightOperand instanceof Number)) ||
               ((leftOperand instanceof String) && (rightOperand instanceof String)) ||
               ((leftOperand instanceof Boolean) && (rightOperand instanceof Boolean)) ||
               ((leftOperand instanceof Date) && (rightOperand instanceof Date)); 
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConditionalExpression that = (ConditionalExpression) o;

        if (text != null ? !text.equals(that.text) : that.text != null) return false;

        return true;
    }

    public int hashCode() {
        return (text != null ? text.hashCode() : 0);
    }


    public String toString() {
        return "ConditionalExpression{" +
                "text='" + text + '\'' +
                '}';
    }

    private Object readResolve() throws ObjectStreamException {
        parse(text);
        return this;
    }
}
