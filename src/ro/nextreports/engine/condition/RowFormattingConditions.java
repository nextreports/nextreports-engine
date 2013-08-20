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

public class RowFormattingConditions extends FormattingConditions {
	
	private static final long serialVersionUID = 7065626620375025461L;

	private String expressionText;

	public RowFormattingConditions(String expressionText) {
		super();
		this.expressionText = expressionText;
	}

	public String getExpressionText() {
		return expressionText;
	}

	public boolean equals(Object o) {
		boolean eq = super.equals(o);
        if (eq) {
        	RowFormattingConditions that = (RowFormattingConditions) o;
        	if (expressionText != null ? !expressionText.equals(that.expressionText) : that.expressionText != null) return false;
        } else {
        	return false;
        }	
        return true;
	}

	public int hashCode() {
		int result = super.hashCode();	    
		result = 31 * result +  (expressionText != null ? expressionText.hashCode() : 0);
		return result;
	}

	public String toString() {		
		return super.toString();
	}
}
