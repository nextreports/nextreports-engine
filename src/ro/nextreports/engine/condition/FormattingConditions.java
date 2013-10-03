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
package ro.nextreports.engine.condition;

import java.io.Serializable;
import java.util.List;
import java.util.LinkedList;

/**
 * User: mihai.panaitescu
 * Date: 23-Apr-2010
 * Time: 11:22:54
 */
public class FormattingConditions implements Serializable {
	
	private static final long serialVersionUID = -203403968663601670L;
	
	private String cellExpressionText;

    private List<BandElementCondition> conditions;

    public FormattingConditions() {
        conditions = new LinkedList<BandElementCondition>();
    }

    public void set(List<BandElementCondition> conditions) {
        this.conditions = conditions;
    }

    public List<BandElementCondition> getConditions() {
        return conditions;
    }
        
    public String getCellExpressionText() {
		return cellExpressionText;
	}

	public void setCellExpressionText(String cellExpressionText) {
		this.cellExpressionText = cellExpressionText;
	}

	public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FormattingConditions that = (FormattingConditions) o;

        if (cellExpressionText != null ? !cellExpressionText.equals(that.cellExpressionText) : that.cellExpressionText != null) return false;
        if (conditions != null ? !conditions.equals(that.conditions) : that.conditions != null) return false;

        return true;
    }

    public int hashCode() {
    	int result = (cellExpressionText != null ? cellExpressionText.hashCode() : 0);
        return 31 * result + (conditions != null ? conditions.hashCode() : 0);
    }


    public String toString() {
        int size = (conditions == null) ? 0 : conditions.size();
        return String.valueOf(size);
    }
}
