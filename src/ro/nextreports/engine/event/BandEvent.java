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
package ro.nextreports.engine.event;

import java.util.EventObject;

import ro.nextreports.engine.band.Band;

/**
 * @author Decebal Suiu
 */
public class BandEvent extends EventObject {

    public static final int INSERT = 0;
    public static final int UPDATE = 1;
    public static final int DELETE = 2;
    
    public static final int ALL_ROWS = -1;
    public static final int ALL_COLUMNS = -2;

    protected int column;
    protected int row;
    protected int type;
    
    public BandEvent(Band source, int row, int column) {
        this(source, row, column, UPDATE);
    }
    
    public BandEvent(Band source, int row, int column, int type) {
        super(source);
        this.row = row;
        this.column = column;
        this.type = type;
    }

    public int getColumn() {
        return column;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getType() {
        return type;
    }

}
