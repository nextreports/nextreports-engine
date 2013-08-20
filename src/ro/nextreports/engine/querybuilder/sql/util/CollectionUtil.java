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
package ro.nextreports.engine.querybuilder.sql.util;

import java.util.List;

/**
 * @author Decebal Suiu
 */
public class CollectionUtil {

    public static void moveItem(List list, Object oldItem, int newIndex) {
        if ((list == null) || (list.isEmpty())) {
            return;
        }
        
        int index = list.indexOf(oldItem);        
        if (index == -1) {
            return;
        }
        
        list.remove(index);
        list.add(newIndex, oldItem);
    }

    public static void changeItem(List list, Object oldItem, Object newItem) {
        if ((list == null) || (list.isEmpty())) {
            return;
        }

        int index = list.indexOf(oldItem);
        if (index == -1) {
            return;
        }

        list.remove(index);
        list.add(index, newItem);
    }
    
}
