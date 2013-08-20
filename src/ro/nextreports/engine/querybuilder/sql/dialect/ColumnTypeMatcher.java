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
package ro.nextreports.engine.querybuilder.sql.dialect;

/**
 * @author Decebal Suiu
 */
public class ColumnTypeMatcher {

	private ColumnTypeParser parser;
	
	public ColumnTypeMatcher(String columnType) {
		parser = new ColumnTypeParser(columnType);
		try {
			parser.parse();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
		}
	}
	
	public boolean matchType(String type) {
		if (parser.getType().equalsIgnoreCase(type)) {
			return true; 
		}
		
		return false;
	}
	
	public boolean matchPrecision(int precision) {
		if (parser.getPrecision() == ColumnTypeParser.ALL) {
			return true;
		}
		
		String precissionOperator = parser.getPrecissionOperator();
		if (precissionOperator == null) {
			if (parser.getPrecision() == precision) {
				return true;
			}
		} else {
			return evaluateExpression(precision, precissionOperator, parser.getPrecision());
		}
		
		return false;
	}
	
	public boolean matchScale(int scale) {
		if (parser.getScale() == ColumnTypeParser.ALL) {
			return true;
		}
		
		String scaleOperator = parser.getScaleOperator();
		if (scaleOperator == null) {
			if (parser.getScale() == scale) {
				return true;
			}
		} else {
			return evaluateExpression(scale, scaleOperator, parser.getScale());
		}
		
		return false;		
	}
	
	public String getColumnType() {
		return parser.getColumnType();
	}

	private boolean evaluateExpression(int leftOperand, String operator, int rightOperand) {
		if (ColumnTypeParser.GREATER_EQUAL.equals(operator)) {
			return (leftOperand >= rightOperand);
		} else if (ColumnTypeParser.LESS_EQUAL.equals(operator)) {
			return (leftOperand <= rightOperand);
		} else if (ColumnTypeParser.GREATER.equals(operator)) {
			return (leftOperand > rightOperand);
		} else if (ColumnTypeParser.LESS.equals(operator)) {
			return (leftOperand < rightOperand);
		}
		
		throw new IllegalStateException("Unsupported operator");
	}
	
}
