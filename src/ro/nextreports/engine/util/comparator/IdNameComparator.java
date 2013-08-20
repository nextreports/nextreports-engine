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
package ro.nextreports.engine.util.comparator;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Time;
import java.util.Comparator;
import java.math.BigDecimal;

import ro.nextreports.engine.queryexec.IdName;
import ro.nextreports.engine.queryexec.QueryParameter;


/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Nov 9, 2007
 * Time: 1:40:07 PM
 */
public class IdNameComparator implements Comparator<IdName> {

    private byte orderBy = QueryParameter.ORDER_BY_NAME;

    public IdNameComparator(byte orderBy) {        
        this.orderBy = orderBy;
    }

    public int compare(IdName i1, IdName i2) {
        if (i1 == null) {
            return -1;
        } else if (i2 == null) {
            return 1;
        } else {
            Serializable name1 = i1.getName();
            Serializable name2 = i2.getName();
            if ( ((name1 != null) || (name2 != null)) && (orderBy == QueryParameter.ORDER_BY_NAME)) {
                if (name1 == null) {
                    return -1;
                }
                if (name2 == null) {
                    return 1;
                }
                if (name1 instanceof Integer) {
                    return ((Integer) name1).compareTo((Integer) name2);
                } else if (name1 instanceof Double) {
                    return ((Double) name1).compareTo((Double) name2);
                } else if (name1 instanceof Date) {
                    return ((Date) name1).compareTo((Date) name2);
                } else if (name1 instanceof Timestamp) {
                    return ((Timestamp) name1).compareTo((Timestamp) name2);
                } else if (name1 instanceof Time) {
                    return ((Time) name1).compareTo((Time) name2);
                } else if (name1 instanceof String) {
                    return ((String) name1).compareTo((String) name2);
                } else if (name1 instanceof BigDecimal) {
                    return ((BigDecimal) name1).compareTo((BigDecimal) name2);
                } else {
                    return (name1.toString()).compareTo(name2.toString());
                }
            } else {
                Serializable id1 = i1.getId();
                Serializable id2 = i2.getId();
                if ((id1 != null) && (id2 != null)) {
                    if (id1 instanceof Integer) {
                        return ((Integer) id1).compareTo((Integer) id2);
                    } else if (id1 instanceof Double) {
                        return ((Double) id1).compareTo((Double) id2);
                    } else if (id1 instanceof Date) {
                        return ((Date) id1).compareTo((Date) id2);
                    } else if (id1 instanceof Timestamp) {
                        return ((Timestamp) id1).compareTo((Timestamp) id2);
                    } else if (id1 instanceof Time) {
                        return ((Time) id1).compareTo((Time) id2);
                    } else if (id1 instanceof String) {
                        return ((String) id1).compareTo((String) id2);
                    } else if (id1 instanceof BigDecimal) {
                        return ((BigDecimal) id1).compareTo((BigDecimal) id2);    
                    } else {
                        return (id1.toString()).compareTo(id2.toString());
                    }
                } else if (id1 == null) {
                    return -1;
                } else {                
                    return 1;
                }
            }
        }
    }
}
