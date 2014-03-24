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
package ro.nextreports.engine.chart;

import java.io.Serializable;

/**
 * User: mihai.panaitescu
 * Date: 14-Dec-2009
 * Time: 15:21:48
 */
public class ChartType implements Serializable {

    private static final long serialVersionUID = 4477633348941387500L;

    // types
    public static transient final byte NONE = 0;
    public static transient final byte BAR = 1;
    public static transient final byte HORIZONTAL_BAR = 2;    
    public static transient final byte STACKED_BAR = 5;
    public static transient final byte HORIZONTAL_STACKED_BAR = 6;
    public static transient final byte PIE = 10;
    public static transient final byte LINE = 20;
    public static transient final byte AREA = 50;
    public static transient final byte BAR_COMBO = 60;
    public static transient final byte STACKED_BAR_COMBO = 61;
    public static transient final byte BUBBLE = 70;

    // style for all types
    public static transient final byte STYLE_NORMAL = 1;

    // style bar types
    public static transient final byte STYLE_BAR_GLASS = 10;
    public static transient final byte STYLE_BAR_CYLINDER = 11;
    public static transient final byte STYLE_BAR_PARALLELIPIPED = 12;
    public static transient final byte STYLE_BAR_DOME = 13;

    // style line types
    public static transient final byte STYLE_LINE_DOT_ANCHOR = 20;
    public static transient final byte STYLE_LINE_DOT_STAR= 21;
    public static transient final byte STYLE_LINE_DOT_BOW = 22;
    public static transient final byte STYLE_LINE_DOT_SOLID = 23;
    public static transient final byte STYLE_LINE_DOT_HOLLOW = 24;

    private byte type;
    private byte style;

    public ChartType(byte type) {
        this(type, STYLE_NORMAL);
    }

    public ChartType(byte type, byte style) {
        this.type = type;
        this.style = style;
    }

    public byte getType() {
        return type;
    }

    public byte getStyle() {
        return style;
    }

    public void setStyle(byte style) {
        this.style = style;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChartType chartType = (ChartType) o;

        if (style != chartType.style) return false;
        if (type != chartType.type) return false;

        return true;
    }
    
    public boolean isLine() {
        return (type == LINE);
    }
    
    public boolean isPie() {
        return (type == PIE);
    }
    
    public boolean isBubble() {
        return (type == BUBBLE);
    }

    public boolean isHorizontal() {
        return (type == HORIZONTAL_BAR) || (type == HORIZONTAL_STACKED_BAR);
    }

    public boolean isStacked() {
        return (type == STACKED_BAR) || (type == HORIZONTAL_STACKED_BAR) || (type == STACKED_BAR_COMBO);
    }
    
    public boolean isHorizontalStacked() {
        return (type == HORIZONTAL_STACKED_BAR);
    }
    
    public static boolean isCombo(byte type) {
    	return (type == BAR_COMBO) || (type == STACKED_BAR_COMBO);
    }

    public static boolean hasNoFlashSupport(byte type) {
    	return (type == HORIZONTAL_STACKED_BAR) || isCombo(type);
    }
    
    public int hashCode() {
        int result;
        result = (int) type;
        result = 31 * result + (int) style;
        return result;
    }


    public String toString() {
        return "ChartType{" +
                "type=" + type +
                ", style=" + style +
                '}';
    }
}
