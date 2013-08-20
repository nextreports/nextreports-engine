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

import java.util.Collection;
import java.util.Iterator;

import ro.nextreports.engine.querybuilder.sql.output.Output;


/**
 * @author Decebal Suiu
 */
public class InCriteria extends Criteria {

    private static final long serialVersionUID = 2956464999031614266L;

    private Column column;
    private String value;
    private SelectQuery subSelect;

    public InCriteria(Column column, Collection values) {
        this.column = column;
        StringBuffer v = new StringBuffer();
        Iterator it = values.iterator();
        boolean hasNext = it.hasNext();
        while (hasNext) {
            Object curr = it.next();
            hasNext = it.hasNext();
            if (curr instanceof Number) {
                v.append(curr);
            } else {
                v.append(quote(curr.toString()));
            }
            if (hasNext) {
                v.append(',');
            }
        }
        this.value = v.toString();
    }

    public InCriteria(Column column, String[] values) {
        this.column = column;
        StringBuffer v = new StringBuffer();
        for (int i = 0; i < values.length; i++) {
            v.append(quote(values[i]));
            if (i < values.length - 1) {
                v.append(',');
            }
        }
        this.value = v.toString();
    }

    public InCriteria(Column column, int[] values) {
        this.column = column;
        StringBuffer v = new StringBuffer();
        for (int i = 0; i < values.length; i++) {
            v.append(values[i]);
            if (i < values.length - 1) {
                v.append(',');
            }
        }
        this.value = v.toString();
    }

    public InCriteria(Column column, float[] values) {
        this.column = column;
        StringBuffer v = new StringBuffer();
        for (int i = 0; i < values.length; i++) {
            v.append(values[i]);
            if (i < values.length - 1) {
                v.append(',');
            }
        }
        this.value = v.toString();
    }

    public InCriteria(Column column, SelectQuery subSelect) {
        this.column = column;
        this.subSelect = subSelect;
    }

    public InCriteria(Column column, String subSelect) {
        this.column = column;
        this.value = subSelect;
    }

    public InCriteria(Table table, String columnname, Collection values) {
        this(table.getColumn(columnname), values);
    }

    public InCriteria(Table table, String columnname, float[] values) {
        this(table.getColumn(columnname), values);
    }

    public InCriteria(Table table, String columnname, int[] values) {
        this(table.getColumn(columnname), values);
    }

    public InCriteria(Table table, String columnname, SelectQuery subSelect) {
        this(table.getColumn(columnname), subSelect);
    }

    public InCriteria(Table table, String columnname, String subSelect) {
        this(table.getColumn(columnname), subSelect);
    }

    public InCriteria(Table table, String columnname, String[] values) {
        this(table.getColumn(columnname), values);
    }

    public Column getColumn() {
        return column;
    }

    public void write(Output out) {
        out.print(column);
        out.println(" IN (");

        out.indent();
        if (subSelect != null) {
            subSelect.write(out);
        } else {
            out.print(value);
        }
        out.unindent();

        out.println();
        out.print(")");
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InCriteria that = (InCriteria) o;

        if (column != null ? !column.equals(that.column) : that.column != null) return false;
        if (subSelect != null ? !subSelect.equals(that.subSelect) : that.subSelect != null) return false;        
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (column != null ? column.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (subSelect != null ? subSelect.hashCode() : 0);
        return result;
    }
}
