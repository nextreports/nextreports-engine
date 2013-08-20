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

public class PageNoVariable implements Variable {
	
	// because PDF has an event to notify for start and end of page we can compute the value inside the exporter
	// RTF has a special field RtfPageNumber which must be use (there is no way to know the page number)

    public static final String PAGE_NO_PARAM = "PAGE_NO";

    public String getName() {
        return Variable.PAGE_NO_VARIABLE;
    }

    public Object getCurrentValue(Map<String, Object> parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("PageNoVariable : parameters null.");
        }

        Object no = parameters.get(PAGE_NO_PARAM);
        if  ((no == null) || !(no instanceof Integer)) {
            throw new IllegalArgumentException("PageNoVariable : invalid parameter.");
        }

        return no;
    }
}
