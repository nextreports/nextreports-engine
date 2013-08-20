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


import java.util.List;
import java.util.Iterator;

import ro.nextreports.engine.querybuilder.MyRow;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: 23-Feb-2009
 * Time: 15:08:37
 */
public class EqualsUtil {

    public static boolean equals(List<MyRow> listOne, List<MyRow> listTwo) {
        if ((listOne == null) && (listTwo == null))  {
            return true;
        } else if ((listOne!= null) && (listTwo != null)) {
            int size=listOne.size();
            if (size != listTwo.size()) {
                return false;
            } else {
                for (int i = 0; i < size; i++) {
                    MyRow o1 = listOne.get(i);
                    MyRow o2 = listTwo.get(i);
                    if (!(o1 == null ? o2 == null : o1.equalsFull(o2)))
                        return false;
                }
                return true;
            }
        } else {
            return false;
        }
    }

    public static int hashCode(List<MyRow> list) {
        int hashCode = 1;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            MyRow obj = (MyRow) i.next();
            hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCodeFull());
        }
        return hashCode;
    }
}
