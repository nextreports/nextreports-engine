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

/**
 * User: mihai.panaitescu
 * Date: 23-Apr-2010
 * Time: 10:23:35
 */
public class ConditionalOperator {

    public static final String EQUAL = "=";
    public static final String NOT_EQUAL = "!=";
    public static final String GREATER = ">";
    public static final String GREATER_EQUAL = ">=";
    public static final String LESS = "<";
    public static final String LESS_EQUAL = "<=";
    public static final String BETWEEN = "[]";

    public static final String[] operators = {
        EQUAL,
        NOT_EQUAL,
        GREATER,
        GREATER_EQUAL,
        LESS,
        LESS_EQUAL,
        BETWEEN     
    };

    public static boolean isValid(String operator) {
        return  EQUAL.equals(operator) || NOT_EQUAL.equals(operator) || GREATER.equals(operator) ||
                GREATER_EQUAL.equals(operator) || LESS.equals(operator) || LESS_EQUAL.equals(operator) ||
                BETWEEN.equals(operator);
    }

    public static boolean isBoolean(String operator) {
        return EQUAL.equals(operator) || NOT_EQUAL.equals(operator);
    }

    public static boolean isString(String operator){
        return EQUAL.equals(operator) || NOT_EQUAL.equals(operator);
    }
    
}
