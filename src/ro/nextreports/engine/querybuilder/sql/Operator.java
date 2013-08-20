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

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Mar 28, 2006
 * Time: 4:36:21 PM
 */
public class Operator {

    public static final String EQUAL = "=";
    public static final String NOT_EQUAL = "<>";
    public static final String GREATER = ">";
    public static final String GREATER_EQUAL = ">=";
    public static final String LESS = "<";
    public static final String LESS_EQUAL = "<=";
    public static final String LIKE = "LIKE";
    public static final String NOT_LIKE = "NOT LIKE";
    public static final String IN = "IN";
    public static final String NOT_IN = "NOT IN";

    public static final String BETWEEN = "BETWEEN";

    public static final String IS_NULL = "IS NULL";
    public static final String IS_NOT_NULL = "IS NOT NULL";

    public static boolean validOperator(String operator) {
        return  EQUAL.equals(operator) || NOT_EQUAL.equals(operator) || GREATER.equals(operator) ||
                GREATER_EQUAL.equals(operator) || LESS.equals(operator) || LESS_EQUAL.equals(operator) ||
                LIKE.equals(operator) || NOT_LIKE.equals(operator) || IN.equals(operator) ||
                NOT_IN.equals(operator) || IS_NULL.equals(operator) || IS_NOT_NULL.equals(operator) ||
                BETWEEN.equals(operator);
    }

    public static final String[] operators = {
        Operator.EQUAL,
        Operator.NOT_EQUAL,
        Operator.GREATER,
        Operator.GREATER_EQUAL,
        Operator.LESS,
        Operator.LESS_EQUAL,
        Operator.LIKE,
        Operator.NOT_LIKE,
        Operator.IN,
        Operator.NOT_IN,
        Operator.BETWEEN,
        Operator.IS_NULL,
        Operator.IS_NOT_NULL
    };

    public static final String[] multipleOperators = {
        Operator.IN,
        Operator.NOT_IN
    };

    public static boolean isUnar(String operator) {
        return IS_NULL.equals(operator) || IS_NOT_NULL.equals(operator);
    }

    public static boolean isDoubleValue(String operator) {
        return BETWEEN.equals(operator);
    }

}
