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

import java.io.Serializable;

/**
 * User: mihai.panaitescu
 * Date: 05-Jan-2010
 * Time: 16:50:10
 */
public class NameType implements Serializable {

    private String name;
    private String type;

    public NameType(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NameType nameType = (NameType) o;

        if (name != null ? !name.equals(nameType.name) : nameType.name != null) return false;
        if (type != null ? !type.equals(nameType.type) : nameType.type != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }


    public String toString() {
        return "NameType{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
