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

import java.sql.Types;

public class CSVDialect extends AbstractDialect {
	
	public static String DRIVER_CLASS = "org.relique.jdbc.csv.CsvDriver";
	
	public CSVDialect() {
		super();
		registerColumnType("String", Types.VARCHAR);
		registerColumnType("Int", Types.INTEGER);
		registerColumnType("Long", Types.INTEGER);
		registerColumnType("Float", Types.FLOAT);
		registerColumnType("Double", Types.DOUBLE);
		registerColumnType("Date", Types.DATE);
		registerColumnType("Time", Types.TIME);
		registerColumnType("Boolean", Types.BOOLEAN);
	}		

	@Override
	public String getCurrentDate() throws DialectException {
		return "CURRENT_DATE";
	}

	@Override
	public String getCurrentDateSelect() {
		return "select CURRENT_DATE";
	}

	@Override
	public String getRecycleBinTablePrefix() {
		return null;
	}

	@Override
	public String getCursorSqlTypeName() {
		return null;
	}

	@Override
	public int getCursorSqlType() {
		return 0;
	}

	@Override
	public String getSqlChecker() {
		return "SELECT 1";
	}
	
}
