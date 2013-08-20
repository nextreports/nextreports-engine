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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Decebal Suiu
 */
class ColumnTypeParser {

	public static final String WILDCARD = "*";
    public static final String GREATER = ">";
    public static final String GREATER_EQUAL = ">=";
    public static final String LESS = "<";
    public static final String LESS_EQUAL = "<=";
    
    public static final int ALL = Integer.MIN_VALUE;
	
    private static final List<String> allOperators;
    
    private String columnType;
	private String type;
	private int precision;
	private String precissionOperator;
	private int scale;
	private String scaleOperator;
	
    static {
        allOperators = new ArrayList<String>();
        allOperators.add(GREATER);
        allOperators.add(GREATER_EQUAL);
        allOperators.add(LESS);
        allOperators.add(LESS_EQUAL);        
    }

	public ColumnTypeParser(String columnType) {
		this.columnType = columnType;
	}
	
	public String getColumnType() {
		return columnType;
	}

	public String getType() {
		return type;
	}

	public int getPrecision() {
		return precision;
	}

	public String getPrecissionOperator() {
		return precissionOperator;
	}

	public int getScale() {
		return scale;
	}

	public String getScaleOperator() {
		return scaleOperator;
	}

	public void parse() throws Exception {
		String column = new String(columnType).trim();
		int x = column.indexOf('(');
		if (x == -1) {
			type = column;
			precision = ALL;
			scale = ALL;
		} else {		
			type = column.substring(0, x).trim();
			
			String s = column.substring(x);
//			System.out.println("s = " + s);
	
			if ('(' != s.charAt(0)) {
				throw new Exception("Missing '(' for '" + columnType + "'");
			}		
			if (')' != s.charAt(s.length() - 1)) {
				throw new Exception("Missing ')' for '" + columnType + "'");
			}
			
			s = s.substring(1, s.length() - 1).trim();
			
			String[] tmp = s.split(",");
			if (tmp.length != 2) {
				throw new Exception("Missing precision and/or scale for '" + columnType + "'");
			}
			s = tmp[0].trim();
			precissionOperator = getOperator(s);
			if (precissionOperator != null) {
				s = s.substring(precissionOperator.length());
			}
			s = s.trim();
			if (WILDCARD.equals(s)) {
				precision = ALL;
			} else {
				precision = Integer.parseInt(s.trim());
			}
			
			s = tmp[1].trim();
			scaleOperator = getOperator(s);
			if (scaleOperator != null) {
				s = s.substring(scaleOperator.length());
			}
			s = s.trim();
			if (WILDCARD.equals(s)) {
				scale = ALL;
			} else {
				scale = Integer.parseInt(s.trim());
			}
		}		
	}

	private String getOperator(String s) {
		if (s.startsWith(GREATER_EQUAL)) {
			return GREATER_EQUAL;
		} else if (s.startsWith(LESS_EQUAL)) {
			return LESS_EQUAL;
		} else if (s.startsWith(GREATER)) {
			return GREATER;
		} else if (s.startsWith(LESS)) {
			return LESS;
		}		
		
		return null;
	}
	
	public static void main(String[] args) {
//		ColumnTypeParser parser = new ColumnTypeParser("varchar");
//		ColumnTypeParser parser = new ColumnTypeParser("varchar(3,5)");
//		ColumnTypeParser parser = new ColumnTypeParser("varchar(" + GREATER + "3,5)");
//		ColumnTypeParser parser = new ColumnTypeParser("varchar(3)");
		ColumnTypeParser parser = new ColumnTypeParser("varchar(3, *)");
//		ColumnTypeParser parser = new ColumnTypeParser("varchar( " + GREATER_EQUAL + "3, " + LESS_EQUAL + "5 ) ");
		
		try {
			parser.parse();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		System.out.println("type = " + parser.getType());
		System.out.println("precision = " + parser.getPrecision());
		System.out.println("precissionOperator = " + parser.getPrecissionOperator());
		System.out.println("scale = " + parser.getScale());
		System.out.println("scaleOperator = " + parser.getScaleOperator());		
	}

}
