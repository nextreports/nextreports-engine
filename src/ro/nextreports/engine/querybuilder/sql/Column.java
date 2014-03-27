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

import java.io.ObjectStreamException;

import ro.nextreports.engine.querybuilder.sql.output.Output;
import ro.nextreports.engine.querybuilder.sql.output.Outputable;
import ro.nextreports.engine.querybuilder.sql.output.ToStringer;


/**
 * @author Decebal Suiu
 */
public class Column implements Outputable {

    private static final long serialVersionUID = -5015360894655058500L;

    protected Table table;
    protected String name;
    protected String alias;
    protected boolean output = true;
    private boolean fKey;
    private boolean pKey;
    private boolean iKey;
    
    protected transient String type;
    private transient boolean useTableName;

    public Column(Table table, String name, String type) {
        this.table = table;
        this.name = name;
        this.type = type;
        useTableName = true;
    }

    public Column(Table table, String name, String alias, String type) {
        this.table = table;
        this.name = name;
        this.alias = alias;
        this.type = type;
        useTableName = true;
    }

    public Table getTable() {
        return table;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    /**
     * Short alias of column
     */
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getQualifiedName() {
        return getTable().getQualifiedName().concat(".").concat(getName());
    }

    public boolean isOutput() {
        return output;
    }

    public void setOutput(boolean output) {
        this.output = output;
    }

    public boolean isfKey() {
        return fKey;
    }

    public void setfKey(boolean fKey) {
        this.fKey = fKey;
    }
        
    public boolean isiKey() {
		return iKey;
	}

	public void setiKey(boolean iKey) {
		this.iKey = iKey;
	}

	public boolean ispKey() {
        return pKey;
    }

    public void setpKey(boolean pKey) {
        this.pKey = pKey;
    }
        
    public boolean isUseTableName() {
		return useTableName;
	}

	public void setUseTableName(boolean useTableName) {
		this.useTableName = useTableName;
	}

	public void write(Output out) {
		if (isUseTableName()) {
			if (getTable() != null) {
				out.print(getTable().getQualifiedName());
				out.print('.');
			}
		}
		if (getName().contains(" ") && (table.getDialect() != null)) {
			out.print(table.getDialect().getEscapedKeyWord(getName()));
		} else {
			out.print(getName());
		}
    }

    public String toString() {
        return ToStringer.toString(this);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (getClass() != o.getClass()) {
            if (!(this instanceof ExpressionColumn) && !(o instanceof ExpressionColumn) &&
                !(this instanceof GroupByFunctionColumn) && !(o instanceof GroupByFunctionColumn)    ) {
               return false; 
            }
        }

        final Column column = (Column) o;

        //if (alias != null ? !alias.equals(column.alias) : column.alias != null) return false;
        if (name != null ? !name.equals(column.name) : column.name != null) return false;
        if (table != null ? !table.equals(column.table) : column.table != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (table != null ? table.hashCode() : 0);
        result = 29 * result + (name != null ? name.hashCode() : 0);
        //result = 29 * result + (alias != null ? alias.hashCode() : 0);
        return result;
    }
    
    private Object readResolve() throws ObjectStreamException {
    	useTableName = true;
    	return this;
    }

}
