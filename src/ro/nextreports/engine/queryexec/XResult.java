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
package ro.nextreports.engine.queryexec;

/** 
 * Interface to get a Result after a computation step
 *    -> sql query execution: <code>QueryResult</code>
 *    -> data set creation: <code>DataSetResult</code>
 *    
 * @author mihai.panaitescu
 *
 */
public interface XResult {
	
	public boolean hasNext() throws QueryException;
	
	public Object nextValue(String columnName) throws QueryException;
	
	public Object nextBlobValue(String columnName) throws QueryException;
	
	public Object nextValue(int columnIndex) throws QueryException;
	
	public int getColumnCount();
	
	public int getRowCount();
	
	public String getColumnName(int columnIndex);
	
	public int getColumnIndex(String columnName);
	
	public String getColumnClassName(int columnIndex);
	
	public int getColumnType(int columnIndex);
	
	public int getColumnType(String columnName);
	
	public Object getValueAt(int rowIndex, int columnIndex) throws QueryException;
	
	public Object getValueAt(int rowIndex, String columnName) throws QueryException;
	
	public long getExecuteTime();
	
	public void close();
	
	public boolean isEmpty();
	
	public void beforeFirst() throws Exception;

}
