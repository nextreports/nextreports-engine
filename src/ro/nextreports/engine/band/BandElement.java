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

import java.awt.Color;
import java.awt.Font;
import java.io.ObjectStreamException;
import java.io.Serializable;

import javax.swing.SwingConstants;
import javax.swing.UIManager;

import ro.nextreports.engine.condition.FormattingConditions;

/**
 * @author Decebal Suiu
 */
public class BandElement implements Serializable {

    private static final long serialVersionUID = 5552400589177343195L;

    public static final int LEFT = SwingConstants.LEFT;
    public static final int CENTER = SwingConstants.CENTER;
    public static final int RIGHT = SwingConstants.RIGHT;

    public static final int TOP = SwingConstants.TOP;
    public static final int MIDDLE = SwingConstants.CENTER;
    public static final int BOTTOM = SwingConstants.BOTTOM;
    
    protected int rowSpan;
    protected int colSpan;
    protected Font font;
    protected Color foreground;
    protected Color background;
    protected String text;
    protected int horizontalAlign;
    protected int verticalAlign;
    protected Padding padding;
    protected Border border;

    protected String htmlAccHeaders;
    protected String htmlAccId;
    protected String htmlAccScope;

    private boolean wrapText;
    // line spacing if wrapText=true (100% is default)
    private int percentLineSpacing; 
    private boolean repeatedValue;
    private String hideWhenExpression;
    private short textRotation;

    private FormattingConditions formattingConditions;     

    public BandElement(String name) {
    	this.rowSpan = 1;
    	this.colSpan = 1;
        this.text = name;
        this.horizontalAlign = LEFT;
        this.verticalAlign = MIDDLE;
        this.foreground = Color.BLACK;
        this.background = Color.WHITE;
        Font defaultFont = UIManager.getFont("Label.font");
//        System.out.println(defaultFont.getClass().getName());
        // defaultFont is a FontUIResource, we want a java.awt.Font
        this.font = new Font(defaultFont.getName(), defaultFont.getStyle(), defaultFont.getSize());        
        this.padding = new Padding(0, 0, 0, 0);
        this.border = new Border(0,0,0,0);
    }
    
    public int getRowSpan() {
		return rowSpan;
	}

	public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
	}

	public int getColSpan() {
		return colSpan;
	}

	public void setColSpan(int colSpan) {        
        this.colSpan = colSpan;
	}

	public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Color getForeground() {
        return foreground;
    }

    public void setForeground(Color foreground) {
        this.foreground = foreground;
    }

    public Color getBackground() {
        return background;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public String getText() {
        return text;
    }
    
    public void setText(String text) {
    	this.text = text;
    }
    
    public int getHorizontalAlign() {
        return horizontalAlign;
    }

    public void setHorizontalAlign(int horizontalAlign) {
        this.horizontalAlign = horizontalAlign;
    }

    public int getVerticalAlign() {
        return verticalAlign;
    }

    public void setVerticalAlign(int verticalAlign) {
        this.verticalAlign = verticalAlign;
    }

    public Padding getPadding() {
        return padding;
    }
    
    public void setPadding(Padding padding) {
        this.padding = padding;
    }

	public Border getBorder() {
		return border;
	}

	public void setBorder(Border border) {
		this.border = border;
	}

    public String getHtmlAccHeaders() {
        return htmlAccHeaders;
    }

    public void setHtmlAccHeaders(String htmlAccHeaders) {
        this.htmlAccHeaders = htmlAccHeaders;
    }

    public String getHtmlAccId() {
        return htmlAccId;
    }

    public void setHtmlAccId(String htmlAccId) {
        this.htmlAccId = htmlAccId;
    }

    public String getHtmlAccScope() {
        return htmlAccScope;
    }

    public void setHtmlAccScope(String htmlAccScope) {
        this.htmlAccScope = htmlAccScope;
    }

    public boolean isWrapText() {
        return wrapText;
    }

    public void setWrapText(boolean wrapText) {
        this.wrapText = wrapText;
    }
                
    public int getPercentLineSpacing() {
		return percentLineSpacing;
	}

	public void setPercentLineSpacing(int percentLineSpacing) {
		this.percentLineSpacing = percentLineSpacing;
	}

	public short getTextRotation() {
		return textRotation;
	}

	public void setTextRotation(short textRotation) {
		this.textRotation = textRotation;
	}

	public boolean isRepeatedValue() {
        return repeatedValue;
    }

    public void setRepeatedValue(boolean repeatedValue) {
        this.repeatedValue = repeatedValue;
    }

    public String getHideWhenExpression() {
        return hideWhenExpression;
    }

    public void setHideWhenExpression(String hideWhenExpression) {
        this.hideWhenExpression = hideWhenExpression;
    }

    public FormattingConditions getFormattingConditions() {
        return formattingConditions;
    }

    public void setFormattingConditions(FormattingConditions formattingConditions) {
        this.formattingConditions = formattingConditions;
    }

    private Object readResolve() throws ObjectStreamException {
      // Read/initialize additional fields
      if (rowSpan == 0) {
          rowSpan = 1;
      }
      if (colSpan == 0) {
          colSpan = 1;
      }
      if (percentLineSpacing == 0) {
    	  percentLineSpacing = 100;
      }
      
      return this;
    }         

	public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BandElement that = (BandElement) o;

        if (colSpan != that.colSpan) return false;
        if (horizontalAlign != that.horizontalAlign) return false;
        if (repeatedValue != that.repeatedValue) return false;
        if (hideWhenExpression != null ? !hideWhenExpression.equals(that.hideWhenExpression) : that.hideWhenExpression != null) return false;        
        if (rowSpan != that.rowSpan) return false;
        if (verticalAlign != that.verticalAlign) return false;
        if (wrapText != that.wrapText) return false;
        if (percentLineSpacing != that.percentLineSpacing) return false;
        if (textRotation != that.textRotation) return false;
        if (background != null ? !background.equals(that.background) : that.background != null) return false;
        if (border != null ? !border.equals(that.border) : that.border != null) return false;
        if (font != null ? !font.equals(that.font) : that.font != null) return false;
        if (foreground != null ? !foreground.equals(that.foreground) : that.foreground != null) return false;
        if (htmlAccHeaders != null ? !htmlAccHeaders.equals(that.htmlAccHeaders) : that.htmlAccHeaders != null)
            return false;
        if (htmlAccId != null ? !htmlAccId.equals(that.htmlAccId) : that.htmlAccId != null) return false;
        if (htmlAccScope != null ? !htmlAccScope.equals(that.htmlAccScope) : that.htmlAccScope != null) return false;
        if (padding != null ? !padding.equals(that.padding) : that.padding != null) return false;
        if (formattingConditions != null ? !formattingConditions.equals(that.formattingConditions) : that.formattingConditions != null)
            return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;        

        return true;
    }

    public int hashCode() {
        int result;
        result = rowSpan;
        result = 31 * result + colSpan;
        result = 31 * result + (font != null ? font.hashCode() : 0);
        result = 31 * result + (foreground != null ? foreground.hashCode() : 0);
        result = 31 * result + (background != null ? background.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + horizontalAlign;
        result = 31 * result + verticalAlign;
        result = 31 * result + (padding != null ? padding.hashCode() : 0);
        result = 31 * result + (border != null ? border.hashCode() : 0);
        result = 31 * result + (htmlAccHeaders != null ? htmlAccHeaders.hashCode() : 0);
        result = 31 * result + (htmlAccId != null ? htmlAccId.hashCode() : 0);
        result = 31 * result + (htmlAccScope != null ? htmlAccScope.hashCode() : 0);
        result = 31 * result + (wrapText ? 1 : 0);
        result = 31 * result + percentLineSpacing;
        result = 31 * result + textRotation;
        result = 31 * result + (repeatedValue ? 1 : 0);
        result = 31 * result + (hideWhenExpression != null ? hideWhenExpression.hashCode() : 0);
        result = 31 * result + (formattingConditions != null ? formattingConditions.hashCode() : 0);        
        return result;
    }
   
}
