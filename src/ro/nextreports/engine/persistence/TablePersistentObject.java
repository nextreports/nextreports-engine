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
package ro.nextreports.engine.persistence;

import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;

import ro.nextreports.engine.querybuilder.sql.Table;


/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Apr 10, 2006
 * Time: 4:36:49 PM
 */
public class TablePersistentObject implements Serializable {

    private static final long serialVersionUID = 4477852669913584452L;

    private Table table;
    private Point point;
    private Dimension dim;

    public TablePersistentObject() {
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Dimension getDim() {
        return dim;
    }

    public void setDim(Dimension dim) {
        this.dim = dim;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TablePersistentObject that = (TablePersistentObject) o;

        if (dim != null ? !dim.equals(that.dim) : that.dim != null) return false;
        if (point != null ? !point.equals(that.point) : that.point != null) return false;
        if (table != null ? !table.equals(that.table) : that.table != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (table != null ? table.hashCode() : 0);
        result = 31 * result + (point != null ? point.hashCode() : 0);
        result = 31 * result + (dim != null ? dim.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "TablePersistentObject{" +
                "table=" + table +
                ", point=" + point +
                ", dim=" + dim +
                '}';
    }
}
