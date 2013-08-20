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

import ro.nextreports.engine.exporter.util.function.GFunction;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Nov 26, 2008
 * Time: 11:02:11 AM
 */
public class FunctionCache {

    private GFunction function;
    private String functionColumn;
    private boolean isExpression;

    public FunctionCache() {
    }

    public GFunction getFunction() {
        return function;
    }

    public void setFunction(GFunction function) {
        this.function = function;
    }

    public String getFunctionColumn() {
        return functionColumn;
    }

    public void setFunctionColumn(String functionColumn) {
        this.functionColumn = functionColumn;
    }

    public boolean isExpression() {
        return isExpression;
    }

    public void setExpression(boolean expression) {
        isExpression = expression;
    }

    public String toString() {
        return "FunctionCache{" +
                "function=" + function.getName() +
                ", functionColumn='" + functionColumn + '\'' +
                ", isExpression=" + isExpression + '\'' +      
                '}';
    }
}
