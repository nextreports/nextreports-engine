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
 * User: mihai.panaitescu
 * Date: 11-May-2010
 * Time: 13:38:40
 */
public class GroupRowVariable implements Variable {

    public static final String GROUP_ROW_PARAM = "GROUP_ROW";

    public String getName() {
        return Variable.GROUP_ROW_VARIABLE;
    }

    public Object getCurrentValue(Map<String, Object> parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("GroupRowVariable : parameters null.");
        }

        Object row = parameters.get(GROUP_ROW_PARAM);
        if  ((row == null) || !(row instanceof Integer)) {
            throw new IllegalArgumentException("GroupRowVariable : invalid parameter.");
        }

        return row;
    }
}
