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

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Nov 13, 2008
 * Time: 11:48:41 AM
 */
public abstract class AbstractGFunction implements GFunction {

    public static final String NOOP = "NOOP";
    public static final String MIN = "MIN";
	public static final String MAX = "MAX";
	public static final String AVERAGE = "AVERAGE";
	public static final String COUNT = "COUNT";
	public static final String COUNT_DISTINCT = "COUNT DISTINCT";
	public static final String SUM = "SUM";

    protected Object computedValue = getNeutralElement();

    public abstract String getName();    

    public abstract Object getNeutralElement();

    public abstract Object compute(Object value);    

    public double getDouble(Object object) {
        if (object == null) {
            return 0;
        }
        if (object instanceof Number) {
            return ((Number) object).doubleValue();
        } else {
            throw new IllegalArgumentException("Function " + getName() + " : value '" +  object + "' is not a number!");
        }
    }

    public void reset(){
        computedValue = getNeutralElement();
    }

    public Object getComputedValue() {
        return computedValue;
    }
}
