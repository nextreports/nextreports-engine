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
package ro.nextreports.engine.exporter.util.function;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Nov 13, 2008
 * Time: 1:04:42 PM
 */
public class FunctionFactory {

    public static GFunction getFunction(String functionName) {
        if (AbstractGFunction.MIN.equalsIgnoreCase(functionName)) {
            return new MinFunction();
        } else if (AbstractGFunction.MAX.equalsIgnoreCase(functionName)) {
            return new MaxFunction();
        } else if (AbstractGFunction.AVERAGE.equalsIgnoreCase(functionName)) {
            return new AverageFunction();
        } else if (AbstractGFunction.COUNT.equalsIgnoreCase(functionName)) {
            return new CountFunction();
        } else if (AbstractGFunction.COUNT_DISTINCT.equalsIgnoreCase(functionName)) {
            return new CountDistinctFunction();
        } else if (AbstractGFunction.SUM.equalsIgnoreCase(functionName)) {
            return new SumFunction();
        } else if (AbstractGFunction.NOOP.equalsIgnoreCase(functionName)) {
            return new NoopFunction();
        }

        throw new IllegalArgumentException("Function '" + functionName + "' is not defined!");
    }

    public static List<String> getAllFunctionNames() {
        List<String> names = new ArrayList<String>();
        names.add(AbstractGFunction.NOOP);
        names.addAll(getFunctionNames());
        return names;
    }

    public static List<String> getFunctionNames() {
        List<String> names = new ArrayList<String>();
        names.add(AbstractGFunction.SUM);
        names.add(AbstractGFunction.MIN);
        names.add(AbstractGFunction.MAX);
        names.add(AbstractGFunction.AVERAGE);
        names.add(AbstractGFunction.COUNT);
        names.add(AbstractGFunction.COUNT_DISTINCT);
        return names;
    }

    public static List<GFunction> getFunctions() {
        List<GFunction> functions = new ArrayList<GFunction>();
        for (String name : getAllFunctionNames()) {
            functions.add(getFunction(name));
        }
        return functions;
    }

    public static List<GFunction> getCountFunctions() {
        List<GFunction> functions = new ArrayList<GFunction>();
        functions.add(getFunction(AbstractGFunction.COUNT));
        functions.add(getFunction(AbstractGFunction.COUNT_DISTINCT));
        return functions;
    }

    public static boolean isCountFunction(String functionName) {
        return AbstractGFunction.COUNT.equals(functionName) ||
               AbstractGFunction.COUNT_DISTINCT.equals(functionName) ||
               AbstractGFunction.NOOP.equals(functionName); 
    }

}
