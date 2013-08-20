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
package ro.nextreports.engine.querybuilder.sql;

import ro.nextreports.engine.querybuilder.sql.output.Output;
import ro.nextreports.engine.querybuilder.sql.output.Outputable;
import ro.nextreports.engine.querybuilder.sql.output.ToStringer;

/**
 * ORDER BY clause. See SelectQuery.addOrder(Order).
 *
 * @author Decebal Suiu
 */
public class Order implements Outputable {

    private static final long serialVersionUID = -6481276482151086935L;

    private Column column;
    private boolean ascending;
    private int index;

    /**
     * @param column    Column to order by.
     * @param ascending true or false
     */
    public Order(Column column, boolean ascending) {
        this.column = column;
        this.ascending = ascending;
    }

    public Column getColumn() {
        return column;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String toString() {
        return ToStringer.toString(this);
    }

    public void write(Output out) {
        column.write(out);
        if (!ascending) {
            out.print(" DESC");
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (ascending != order.ascending) return false;
        if (index != order.index) return false;
        if (column != null ? !column.equals(order.column) : order.column != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (column != null ? column.hashCode() : 0);
        result = 31 * result + (ascending ? 1 : 0);
        result = 31 * result + index;
        return result;
    }
}
