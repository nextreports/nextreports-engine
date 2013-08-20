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
package ro.nextreports.engine.util;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Feb 3, 2009
 * Time: 1:35:09 PM
 */
public class ParameterNotFoundException extends Exception {

    private String paramName;

    public ParameterNotFoundException(String paramName) {
        super();
        this.paramName= paramName;
    }

    public ParameterNotFoundException(String paramName, String message) {
        super(message);
        this.paramName= paramName;
    }

    public ParameterNotFoundException(String paramName, String message, Throwable cause) {
        super(message, cause);
        this.paramName= paramName;
    }

    public ParameterNotFoundException(String paramName, Throwable cause) {
        super(cause);
        this.paramName= paramName;
    }

    public String getParamName() {
        return paramName;
    }
}
