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
 * Time: 12:47:40 PM
 */
public class CountDistinctFunction extends AbstractGFunction {

    private List<Object> objects = new ArrayList<Object>();

    public String getName() {
        return COUNT_DISTINCT;
    }    

    public Object getNeutralElement() {
        return null;
    }

    public Object compute(Object value) {
        double val = getDouble(computedValue);
        if (!contains(value)) {
            objects.add(value);
            computedValue = val + 1;
        } 
        return computedValue;
    }

    private boolean contains(Object value) {
        for (Object object : objects) {
            if (FunctionUtil.parameterEquals(object, value)) {
                return true;
            }
        }        
        return false;
    }

    public void reset() {
        super.reset();
        objects.clear();
    }


}
