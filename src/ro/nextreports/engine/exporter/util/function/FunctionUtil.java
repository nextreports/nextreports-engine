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
package ro.nextreports.engine.exporter.util.function;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Nov 13, 2008
 * Time: 11:46:33 AM
 */
public class FunctionUtil {

    public static boolean parameterEquals(Object v1, Object v2) {
        if (v1 == null || v2 == null) {
            return false;
        }
        if (v1 instanceof Integer) {
            if (v2 instanceof Integer) {
                if (((Integer) v1).intValue() == ((Integer) v2).intValue()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        if (v1 instanceof Long) {
            if (v2 instanceof Long) {
                if (((Long) v1).longValue() == ((Long) v2).longValue()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        if (v1 instanceof Double) {
            if (v2 instanceof Double) {
                if (((Double) v1).doubleValue() == ((Double) v2).doubleValue()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        if (v1 instanceof String) {
            if (v2 instanceof String) {
                String s1 = (String) v1;
                String s2 = (String) v2;
                if (s1.trim().equalsIgnoreCase(s2.trim())) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        if (v1 instanceof BigDecimal) {
            if (v2 instanceof BigDecimal) {
                if (((BigDecimal) v1).doubleValue() == ((BigDecimal) v2).doubleValue()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        if (v1 instanceof Date) {
            if (v2 instanceof Date) {
                if (((Date) v1).getTime() == ((Date) v2).getTime()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        return v1.equals(v2);
    }

    public static Object increase(Object val) {
		if (val == null) {
			return null;
		}
		if (val instanceof Number) {
			Number number = (Number) val;
			return number.floatValue() + 1;
		}
		return val;
	}
}
