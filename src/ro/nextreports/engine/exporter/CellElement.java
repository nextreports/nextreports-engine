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
package ro.nextreports.engine.exporter;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Nov 25, 2008
 * Time: 10:24:00 AM
 */
public class CellElement {

    protected int row;
	protected int column;

	public CellElement() {
	}

	public CellElement(int row, int column) {
		this.row = row;
		this.column = column;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CellElement that = (CellElement) o;

        if (column != that.column) return false;
        if (row != that.row) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = row;
        result = 31 * result + column;
        return result;
    }


    public String toString() {
        return "CellElement{" +
                "row=" + row +
                ", column=" + column +
                '}';
    }
}
