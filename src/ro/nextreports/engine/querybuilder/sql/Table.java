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

import java.util.List;

import ro.nextreports.engine.querybuilder.sql.dialect.Dialect;
import ro.nextreports.engine.querybuilder.sql.output.Output;
import ro.nextreports.engine.querybuilder.sql.output.Outputable;
import ro.nextreports.engine.querybuilder.sql.output.ToStringer;


/**
 * @author Decebal Suiu
 */
public class Table implements Outputable {

    private static final long serialVersionUID = -7427248594197786883L;

    private String name;
    private String alias;
    private String schemaName;
    
    private transient Dialect dialect;
    private transient List<JoinCriteria> joins;

    public Table(String name) {
        this.name = name;
    }

    public Table(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    /**
     * Name of table.
     */
    public String getName() {
        return name;
    }

    public String getEscapedName() {
        if ((dialect != null) && dialect.isKeyWord(name)) {
            return dialect.getEscapedKeyWord(name);
        }
        return getName();
    }

    /**
     * Short alias of table.
     */
    public String getAlias() {
        return alias;
    }
        

    public Dialect getDialect() {
		return dialect;
	}

	/**
     * Get a column for a particular table.
     */
    public Column getColumn(String columnName) {
        return new Column(this, columnName, null);
    }

    public String getQualifiedName() {
        return (alias != null) ? alias : name;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }        

    public void setJoins(List<JoinCriteria> joins) {
		this.joins = joins;
	}

	public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Table)) {
            return false;
        }
        
        return getQualifiedName().equals(((Table) o).getQualifiedName());
    }

    public int hashCode() {
        return (getQualifiedName() != null ? getQualifiedName().hashCode() : 0);
    }

    public void write(Output out) {
        if ( ! ((schemaName == null) || "".equals(schemaName) || "%".equals(schemaName))) {
            out.print(getSchemaName());
            out.print(".");
        }
        String name = getName();        
        if ((dialect != null) && dialect.isKeyWord(name)) {
            out.print(dialect.getEscapedKeyWord(name));
        } else {
            boolean hasSpaces = name.contains(" ");
            if (hasSpaces) {
                out.print("\"");
            }
            out.print(name);
            if (hasSpaces) {
                out.print("\"");
            }
        }
        if (alias != null) {
            out.print(' ');
            out.print(getAlias());
        }
        
        // take care of outer joins
        // example
        //
        // select b.invoice_no, c.customer_id, invt.invoice_type_id
        // from bill_invoices b LEFT OUTER JOIN cus_customers c ON (b.customer_id = c.customer_id)
        //                      LEFT OUTER JOIN bill_invoice_types invt ON (b.invoice_type_id = invt.invoice_type_id)
        //        
        if (joins != null) {
        	int i=0;
        	boolean needUnindent = false;;
        	for (JoinCriteria jc : joins) {
        		Table destTable = jc.getDestination().getTable();
        		String joinType = jc.getJoinType();
        		if (JoinType.isOuter(joinType))  {        			
        			out.println();
        			if (i == 0) {
        				out.indent();
        				needUnindent = true;
        			}
        			out.print(joinType);
        			out.print(' ');
        			destTable.write(out);
        			out.print(" ON (");
        			jc.getSource().write(out);
        			out.print(" ");
        			out.print(jc.getOperator());
        			out.print(" ");
        			jc.getDestination().write(out);        			
        			out.print(")");        			
        		}
        		i++;
        	}
        	if (needUnindent) {
        		out.unindent();
        	}
        }
    }

    public String toString() {
        return ToStringer.toString(this);
    }

}
