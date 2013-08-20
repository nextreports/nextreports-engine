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

import java.io.Serializable;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.event.EventListenerList;

import ro.nextreports.engine.event.BandEvent;
import ro.nextreports.engine.event.BandListener;

/**
 * @author Decebal Suiu
 */
public class Band implements Serializable {

    private static final long serialVersionUID = 8698695353980903707L;

    protected String name;
    protected Vector<RowElement> rows;
    
    protected transient EventListenerList listenerList;

    public Band(String name) {
    	this(name, 0, 0);
    }
    
    public Band(String name, int rowNo, int columnNo) {        
    	this.name = name;
    	initListenerList();
    	rows = new Vector<RowElement>();
    	rows.setSize(rowNo);
    	for (RowElement row : rows) {
    		row.getElements().setSize(columnNo);
    	}
    }
    
    public void initListenerList() {
        listenerList = new EventListenerList();
    }

    public String getName() {
        return name;
    }

//    public List<List<BandElement>> getElements() {
//        return new ArrayList<List<BandElement>>(elements.);
//    }
    
    public List<RowElement> getElements() {
    	return new ArrayList<RowElement>(rows);
    }
    
    public void setElements(List<List<BandElement>> elements) {
        if (elements == null) {
            throw new IllegalArgumentException("Null parameter");
        }

        // add the new rows.
        this.rows = new Vector<RowElement>();
        for (List<BandElement> row : elements) {
        	this.rows.add(new RowElement(row));
        }

        // generate notification.
        fireBandChanged();        
    }
    
    public int getColumnCount() {
        return (getRowCount() == 0) ? 0 : getRow(0).size();
    }
    

    public void setColumnCount(int columnCount) { 
        for (RowElement row : rows) { 
            row.getElements().setSize(columnCount); 
        }
        
//        fireStructureChanged();
    }
     
    
    public int getRowCount() {
        return (rows == null) ? 0 : rows.size();
    }
      
    /*
    public void setRowCount(int rowCount) {
        if (rowCount == getRowCount()) {
            return;
        }

//        int oldNumRows = getRowCount();
        if (rowCount <= getRowCount()) {
            // rowCount is smaller than our current size, so we can just
            // let Vector discard the extra rows
            elements.setSize(rowCount);

            // generate notification
//            fireRowsDeleted(getRowCount(), oldNumRows-1);
        } else {
            int columnCount = getColumnCount();
            // we are adding rows to the model
            while(getRowCount() < rowCount) {
                Vector<BandElement> newRow = new Vector<BandElement>(columnCount);
                newRow.setSize(columnCount);
                elements.addElement(newRow);
            }

            // generate notification
//            fireRowsInserted(oldNumRows, getRowCount()-1);
        }        
    }
    */

    public List<BandElement> getRow(int index) {
        return rows.get(index).getElements();
    }

    public void insertRow(int index) {
    	Vector<BandElement> row = new Vector<BandElement>();
    	row.setSize(getColumnCount());
    	insertRow(index, row);
    }
    
    public void insertFirstRow(int index, int cols) {
    	Vector<BandElement> row = new Vector<BandElement>();
    	row.setSize(cols);
    	insertRow(index, row);
    }
    
    public void insertRow(int index, List<BandElement> row) {
    	rows.add(index, new RowElement(row));

        // generate notification
//        fireRowsInserted(oldNumRows, getRowCount() - 1);
    }
    
    public void removeRow(int index) {
    	rows.remove(index);
        
        // generate notification
//        fireRowsInserted(oldNumRows, getRowCount()-1);
    }

    public List<BandElement> getColumn(int index) {
    	List<BandElement> column = new ArrayList<BandElement>();
    	for (RowElement row : rows) {
    		column.add(row.getElements().get(index));
    	}
    	
        return column;
    }
    
    public void insertColumn(int index) {
    	Vector<BandElement> column = new Vector<BandElement>();
    	column.setSize(getRowCount());
    	insertColumn(index, column);
    }
    
    public void insertColumn(int index, List<BandElement> column) {
    	int rowCount = getRowCount();
    	for (int i = 0; i < rowCount; i++) {
    		getRow(i).add(index, column.get(i));
    	}

        // generate notification
//        fireColumnsInserted(oldNumRows, getColumnCount() - 1);
    }
    
    public void removeColumn(int index) {
    	for (RowElement row : rows) {
    		row.getElements().remove(index);
    	}
        
        // generate notification
//        fireRowsInserted(oldNumRows, getRowCount()-1);
    }

    public BandElement getElementAt(int row, int column) {
        return getRow(row).get(column);
    }
    
    public void setElementAt(BandElement element, int row, int column) {
        getRow(row).set(column, element);

        // generate notification
        fireBandChanged(new BandEvent(this, row, column));
    }
    
    public void clear() {
        setElements(new ArrayList<List<BandElement>>());
    }
    
    public void addBandListener(BandListener listener) {
        listenerList.add(BandListener.class, listener);
    }
    
    public void removeBandListener(BandListener listener) {
        listenerList.remove(BandListener.class, listener);
    }
    
    public BandListener[] getBandListeners() {
        return (BandListener[]) listenerList.getListeners(BandListener.class);
    }
    
    public void fireBandChanged() {
        fireBandChanged(new BandEvent(this, BandEvent.ALL_ROWS, BandEvent.ALL_COLUMNS, BandEvent.UPDATE));
    }
        
    protected void fireBandChanged(BandEvent event) {
        BandListener[] list = getBandListeners();
        
        BandListener listener;
        for (int i = 0; i < list.length; i++) {
            listener = (BandListener) list[i];
            listener.bandChanged(event);
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Band band = (Band) o;

        if (rows != null ? !rows.equals(band.rows) : band.rows != null) return false;
        if (name != null ? !name.equals(band.name) : band.name != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (rows != null ? rows.hashCode() : 0);
        return result;
    }

    private Object readResolve() throws ObjectStreamException {
       if (rows == null) {
    	   rows = new Vector<RowElement>();
       }
       if (listenerList == null) {
    	   initListenerList(); 
       }
       return this;
    }
}
