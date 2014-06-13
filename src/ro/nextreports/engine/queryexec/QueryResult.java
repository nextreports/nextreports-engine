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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Decebal Suiu
 */
public class QueryResult {

    private ResultSet resultSet;
    private int numberOfRows;
    private int numberOfColumns;
    private long executeTime;

    // cache
    private Map<Integer,String> columnNames;
    private Map<String, Integer> columnIndexes;
    private Map<Integer,String> columnClassNames;
    private Map<Integer, Integer> columnTypes;
    
    public QueryResult(ResultSet rs, int count, long executeTime) throws QueryException {
        this.resultSet = rs;
        this.executeTime = executeTime;
        this.numberOfRows = count;

        initCache();
    }

    private void initCache() throws QueryException {
        columnNames = new HashMap<Integer,String>();
        columnIndexes = new HashMap<String, Integer>();
        columnClassNames = new HashMap<Integer,String>();
        columnTypes = new HashMap<Integer, Integer>();
        
        try {
            ResultSetMetaData metadata = resultSet.getMetaData();
            numberOfColumns = metadata.getColumnCount();
            for (int i = 0; i < numberOfColumns; i++) {
                columnNames.put(i, metadata.getColumnLabel(i + 1));
                columnIndexes.put(metadata.getColumnLabel(i + 1), i);
                columnClassNames.put(i, metadata.getColumnClassName(i + 1));
                columnTypes.put(i, metadata.getColumnType(i + 1));
            }
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }
    
    public boolean hasNext() throws QueryException {
        if (resultSet == null) {
            return false;
        }
        
        try {
            return resultSet.next();
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }
    
    public Object nextValue(String columnName) throws QueryException {
        if (resultSet == null) {
            return null;
        }
        
        try {
            return resultSet.getObject(columnName);
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }
    
    public Object nextBlobValue(String columnName) throws QueryException {
        if (resultSet == null) {
            return null;
        }
        
        try {
            return resultSet.getBlob(columnName);
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    public Object nextValue(int columnIndex) throws QueryException {
        if (resultSet == null) {
            return null;
        }
        
        try {
            return resultSet.getObject(columnIndex + 1);
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }
    
    public ResultSet getResultSet() {
        return resultSet;
    }

    public int getColumnCount() {
        return numberOfColumns;
    }
    
    public int getRowCount() {
        return numberOfRows;
    }
    
    public String getColumnName(int columnIndex) {
        return columnNames.get(columnIndex);
    }

    public int getColumnIndex(String columnName) {
       return columnIndexes.get(columnName);  
    }

    public String getColumnClassName(int columnIndex) {
        return columnClassNames.get(columnIndex);
    }
    
    public int getColumnType(int columnIndex) {
    	return columnTypes.get(columnIndex);
    }
    
    public int getColumnType(String columnName) {    	
    	return getColumnType(getColumnIndex(columnName));
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) throws QueryException {
        if (resultSet == null) {
            return null;
        }
        
        try {
            int row = resultSet.getRow();
            resultSet.absolute(rowIndex + 1);
            Object object = resultSet.getObject(columnIndex + 1);
            if (row == 0) {
                resultSet.beforeFirst();
            } else {
                resultSet.absolute(row);
            }
            return object;            
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    public Object getValueAt(int rowIndex, String columnName) throws QueryException {
        if (resultSet == null) {
            return null;
        }

        try {
            int row = resultSet.getRow();
            resultSet.absolute(rowIndex + 1);
            Object object = resultSet.getObject(columnName);
            if (row == 0) {
                resultSet.beforeFirst();
            } else {
                resultSet.absolute(row);
            }
            return object;
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }
    
    /**
     * Get the execution time in milliseconds.
     */
    public long getExecuteTime() {
        return executeTime;
    }

    public void close() {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();  
            }
        }
    }
    
    /**
     * Test if QueryResult is empty
     * Should be called before starting to use the result set
     * @return true if QueryResult is empty, false otherwise
     */
	public boolean isEmpty() {
		try {
			if (!resultSet.isBeforeFirst()) {
				return true;
			}
		} catch (SQLException e) {
			// will fail for TYPE_FORWARD_ONLY result set
			// we will test also in ResultExporter printContentBands() to throw or not NoDataFoundException
			e.printStackTrace();
		}
		return false;
	}
    
}
