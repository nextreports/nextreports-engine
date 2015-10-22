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
 * Time: 2:45:44 PM
 */
public interface Variable {

    public final static String DATE_VARIABLE = "DATE";
    public static final String ROW_VARIABLE = "ROW";
    public static final String GROUP_ROW_VARIABLE = "GROUP_ROW";
    public final static String USER_VARIABLE = "USER";    
    public final static String PRODUCT_VARIABLE = "PRODUCT";
    public final static String PAGE_NO_VARIABLE = "PAGE_NO";
    public final static String TOTAL_PAGE_NO_VARIABLE = "TOTAL_PAGE_NO";
    public final static String REPORT_NAME_VARIABLE = "REPORT_NAME";
    public final static String EMPTY_DATA_VARIABLE = "EMPTY_DATA";
    

    public String getName();

    public Object getCurrentValue(Map<String, Object> parameters);
    
}
