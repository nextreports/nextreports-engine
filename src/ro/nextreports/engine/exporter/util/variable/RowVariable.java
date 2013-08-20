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
package ro.nextreports.engine.exporter.util.variable;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Dec 2, 2008
 * Time: 2:50:49 PM
 */
public class RowVariable implements Variable {

    public static final String ROW_PARAM = "ROW";

    public String getName() {
        return Variable.ROW_VARIABLE;
    }

    public Object getCurrentValue(Map<String, Object> parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("RowVariable : parameters null.");
        }

        Object row = parameters.get(ROW_PARAM);
        if  ((row == null) || !(row instanceof Integer)) {
            throw new IllegalArgumentException("RowVariable : invalid parameter.");
        }

        return row;
    }
}
