package ro.nextreports.engine.queryexec;

import java.util.HashMap;
import java.util.Map;

import ro.fortsoft.dataset.core.DataSet;
import ro.fortsoft.dataset.core.DataSetMetaData;

public class DataSetResult implements XResult {

    private DataSet resultSet;
    private int numberOfRows;
    private int numberOfColumns;
    private long executeTime;

    // cache
    private Map<Integer,String> columnNames;
    private Map<String, Integer> columnIndexes;
    private Map<Integer,String> columnClassNames;
    
    public DataSetResult(DataSet ds, int count, long executeTime) {
        this.resultSet = ds;
        this.executeTime = executeTime;
        this.numberOfRows = count;

        initCache();
    }

	private void initCache() {
		columnNames = new HashMap<Integer, String>();
		columnIndexes = new HashMap<String, Integer>();
		columnClassNames = new HashMap<Integer, String>();

		DataSetMetaData metadata = resultSet.getMetaData();
		numberOfColumns = metadata.getFieldCount();
		for (int i = 0; i < numberOfColumns; i++) {
			columnNames.put(i, metadata.getFieldName(i));
			columnIndexes.put(metadata.getFieldName(i), i);
			columnClassNames.put(i, metadata.getFieldClass(i).getName());
		}
		System.out.println("columnNames = " + columnNames.values());
		System.out.println("columnIndexes = " + columnIndexes.keySet() + " - " + columnIndexes.values());
	}
    
    public boolean hasNext() {         	
        return resultSet.next();        
    }
    
    public Object nextValue(String columnName) {
        if (resultSet == null) {
            return null;
        }
        
        return resultSet.getObject(columnName);   
    }
    
    public Object nextBlobValue(String columnName) {
        throw new UnsupportedOperationException();
    }

    public Object nextValue(int columnIndex) {
        if (resultSet == null) {
            return null;
        }
        
        return resultSet.getObject(columnIndex);      
    }
    
    public DataSet getDataSet() {
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
    
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (resultSet == null) {
			return null;
		}

		int row = resultSet.getCursorPosition();
		resultSet.absolute(rowIndex + 1);
		Object object = resultSet.getObject(columnIndex + 1);
		if (row == 0) {
			resultSet.absolute(0);
		} else {
			resultSet.absolute(row);
		}
		return object;

	}

	public Object getValueAt(int rowIndex, String columnName) {
		if (resultSet == null) {
			return null;
		}

		int row = resultSet.getCursorPosition();
		resultSet.absolute(rowIndex + 1);
		Object object = resultSet.getObject(columnName);
		if (row == 0) {
			resultSet.absolute(0);
		} else {
			resultSet.absolute(row);
		}
		return object;
	}
    
    /**
     * Get the execution time in milliseconds.
     */
    public long getExecuteTime() {
        return executeTime;
    }

    public void close() {    	
        if (resultSet != null) {           
            resultSet.close();          
        }
    }
    
    /**
     * Test if DataSetResult is empty
     * Should be called before starting to use the <code>DataSet</code>
     * @return true if DataSetResult is empty, false otherwise
     */
	public boolean isEmpty() {
		return resultSet.isEmpty();
	}

	@Override
	public int getColumnType(int columnIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getColumnType(String columnName) {
		throw new UnsupportedOperationException();
	}
	
	public void beforeFirst() {
		resultSet.absolute(0);
	}
    
}
