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
package ro.nextreports.engine.band;

/**
 * @author Decebal Suiu
 */
public class ColumnBandElement extends FieldBandElement {

    protected String column;
    
    public ColumnBandElement(String column) {
        super("$C{" + column + "}");
        this.column = column;
    }

    public String getColumn() {
        return column;
    }
    
    public void setColumn(String column) {
    	this.column = column;
    	setText("$C{" + column + "}");
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ColumnBandElement that = (ColumnBandElement) o;

        if (column != null ? !column.equals(that.column) : that.column != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (column != null ? column.hashCode() : 0);
        return result;
    }
}
