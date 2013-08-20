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

public class ReportNameVariable implements Variable {
	
	public String getName() {
        return Variable.REPORT_NAME_VARIABLE;
    }

	@Override
	public Object getCurrentValue(Map<String, Object> parameters) {
		if (parameters == null) {
            throw new IllegalArgumentException("FileVariable : parameters null.");
        }

        Object no = parameters.get(Variable.REPORT_NAME_VARIABLE);
        if  (no == null) {
            throw new IllegalArgumentException("FileVariable : invalid parameter.");
        }

        return no;
	}

}
