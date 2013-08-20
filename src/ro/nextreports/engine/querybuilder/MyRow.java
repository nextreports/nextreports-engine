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
package ro.nextreports.engine.querybuilder;

import java.io.Serializable;
import java.io.ObjectStreamException;
import java.util.LinkedList;

import ro.nextreports.engine.querybuilder.sql.Column;


/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Apr 11, 2006
 * Time: 11:21:36 AM
 */
public class MyRow implements Serializable {

    private static final long serialVersionUID = 729136442219706554L;

    public Column column;
    public boolean output;
    public String sortType = ""; // "", Ascending, Descending
    public int sortOrder;
    public String groupBy = "";
    public String criteria;
    public LinkedList<String> orCriterias = new LinkedList<String>();

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyRow myRow = (MyRow) o;

        if (column != null ? !column.equals(myRow.column) : myRow.column != null) return false;

        return true;
    }

    public int hashCode() {
        return (column != null ? column.hashCode() : 0);
    }


    public boolean equalsFull(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyRow myRow = (MyRow) o;

        if (output != myRow.output) return false;
        if (sortOrder != myRow.sortOrder) return false;
        if (column != null ? !column.equals(myRow.column) : myRow.column != null) return false;
        if (criteria != null ? !criteria.equals(myRow.criteria) : myRow.criteria != null) return false;
        if (groupBy != null ? !groupBy.equals(myRow.groupBy) : myRow.groupBy != null) return false;
        if (sortType != null ? !sortType.equals(myRow.sortType) : myRow.sortType != null) return false;        
        if (orCriterias != null ? !orCriterias.equals(myRow.orCriterias) : myRow.orCriterias != null) return false;

        return true;
    }


    public int hashCodeFull() {
        int result;
        result = (column != null ? column.hashCode() : 0);
        result = 31 * result + (output ? 1 : 0);
        result = 31 * result + (sortType != null ? sortType.hashCode() : 0);
        result = 31 * result + sortOrder;
        result = 31 * result + (groupBy != null ? groupBy.hashCode() : 0);
        result = 31 * result + (criteria != null ? criteria.hashCode() : 0);
        result = 31 * result + (orCriterias != null ? orCriterias.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "MyRow{" +
                "column=" + column +
                ", output=" + output +
                ", sortType='" + sortType + '\'' +
                ", sortOrder=" + sortOrder +
                ", groupBy='" + groupBy + '\'' +
                ", criteria='" + criteria + '\'' +
                ", orCriterias=" + orCriterias +
                '}';
    }

    private Object readResolve() throws ObjectStreamException {
        // Read/initialize additional fields
        if (orCriterias == null) {
            orCriterias = new LinkedList<String>();
        }
        return this;
    }
}
