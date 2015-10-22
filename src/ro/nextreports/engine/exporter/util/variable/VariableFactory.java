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

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Dec 2, 2008
 * Time: 2:56:03 PM
 */
public class VariableFactory {

    public static List<Variable> getVariables() {
        List<Variable> list = new ArrayList<Variable>();
        list.add(new DateVariable());
        list.add(new GroupRowVariable());                
        list.add(new PageNoVariable());    
        list.add(new TotalPageNoVariable()); 
        list.add(new ProductVariable());
        list.add(new ReportNameVariable());
        list.add(new RowVariable());
        list.add(new UserVariable());
        list.add(new EmptyDataVariable());
        return list;
    }

    public static List<String> getVariableNames() {
        List<String> names = new ArrayList<String>();
        for (Variable var : getVariables()){
            names.add(var.getName());
        }
        return names;
    }

    public static Variable getVariable(String name) {
        for (Variable var : getVariables()){
            if (var.getName().equals(name)) {
                return var;
            }
        }
        throw new IllegalArgumentException("Unknown variable : " + name);        
    }
}
