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
import java.awt.*;

/**
 * User: mihai.panaitescu
 * Date: 14-Dec-2009
 * Time: 15:28:21
 */
public class ChartTitle implements Serializable {

    private static final long serialVersionUID = -2640868420524350873L;

    public static final transient byte LEFT_ALIGNMENT = 1;
    public static final transient byte CENTRAL_ALIGNMENT = 2;
    public static final transient byte RIGHT_ALIGNMENT = 3;

    private String title;
    private Font font;
    private Color color;
    private byte alignment;

    public ChartTitle(String title) {
        setTitle(title);
        font = getDefaultFont();
        color = getDefaultColor();
        alignment = getDefaultAlignment();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null) {
            throw new IllegalArgumentException("Title cannot be null.");
        }
        this.title = title;
    }

    public Font getFont() {
        if (font == null) {
            return getDefaultFont();
        }
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Color getColor() {
        if (color == null) {
            return getDefaultColor();
        }
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public byte getAlignment() {
        if  ((alignment != LEFT_ALIGNMENT) && (alignment != CENTRAL_ALIGNMENT) &&
             (alignment != RIGHT_ALIGNMENT) ){
            return LEFT_ALIGNMENT;
        }
        return alignment;
    }

    public void setAlignment(byte alignment) {
        this.alignment = alignment;
    }

    private Font getDefaultFont() {
       return new Font("SansSerif", Font.BOLD, 18);
    }

    private Color getDefaultColor() {
        return Color.BLACK;
    }

    private byte getDefaultAlignment() {
        return CENTRAL_ALIGNMENT;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChartTitle that = (ChartTitle) o;

        if (alignment != that.alignment) return false;
        if (color != null ? !color.equals(that.color) : that.color != null) return false;
        if (font != null ? !font.equals(that.font) : that.font != null) return false;
        if (!title.equals(that.title)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = title.hashCode();
        result = 31 * result + (font != null ? font.hashCode() : 0);
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (int) alignment;
        return result;
    }


    public String toString() {
        return "ChartTitle{" +
                "title='" + title + '\'' +
                ", font=" + font +
                ", color=" + color +
                ", alignment=" + alignment +
                '}';
    }
}
