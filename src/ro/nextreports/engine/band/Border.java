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

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.awt.Color;

/**
 * @author Decebal Suiu
 */
public class Border implements Serializable {

    private static final long serialVersionUID = -7417875051872527094L;

    private int left;
	private int right;
	private int top;
	private int bottom;
	private Color leftColor;
	private Color rightColor;
	private Color topColor;
	private Color bottomColor;

    public Border() {
    }

	public Border(int left, int right, int top, int bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		this.leftColor = Color.BLACK;
		this.rightColor = Color.BLACK;
		this.topColor = Color.BLACK;
		this.bottomColor = Color.BLACK;
	}

	public int getBottom() {
		return bottom;
	}

	public void setBottom(int bottom) {
		this.bottom = bottom;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getRight() {
		return right;
	}

	public void setRight(int right) {
		this.right = right;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}		

    public Color getLeftColor() {
		return leftColor;
	}

	public void setLeftColor(Color leftColor) {
		this.leftColor = leftColor;
	}

	public Color getRightColor() {
		return rightColor;
	}

	public void setRightColor(Color rightColor) {
		this.rightColor = rightColor;
	}

	public Color getTopColor() {
		return topColor;
	}

	public void setTopColor(Color topColor) {
		this.topColor = topColor;
	}

	public Color getBottomColor() {
		return bottomColor;
	}

	public void setBottomColor(Color bottomColor) {
		this.bottomColor = bottomColor;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Border border = (Border) o;

        if (bottom != border.bottom) return false;
        if (left != border.left) return false;
        if (right != border.right) return false;
        if (top != border.top) return false;
        if (bottomColor != null ? !bottomColor.equals(border.bottomColor) : border.bottomColor != null) return false;
        if (leftColor != null ? !leftColor.equals(border.leftColor) : border.leftColor != null) return false;
        if (rightColor != null ? !rightColor.equals(border.rightColor) : border.rightColor != null) return false;
        if (topColor != null ? !topColor.equals(border.topColor) : border.topColor != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = left;
        result = 29 * result + right;
        result = 29 * result + top;
        result = 29 * result + bottom;
        result = 29 * result + (bottomColor != null ? bottomColor.hashCode() : 0);
        result = 29 * result + (topColor != null ? topColor.hashCode() : 0);
        result = 29 * result + (leftColor != null ? leftColor.hashCode() : 0);
        result = 29 * result + (rightColor != null ? rightColor.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "(" + top + "," + left + "," + bottom + "," + right + ")";
    }
    
    private Object readResolve() throws ObjectStreamException {
        // Read/initialize additional fields
        if (leftColor == null) {
            leftColor = Color.BLACK;
        }
        if (rightColor == null) {
            rightColor = Color.BLACK;
        }
        if (topColor == null) {
            topColor = Color.BLACK;
        }
        if (bottomColor == null) {
            bottomColor = Color.BLACK;
        }        
        return this;
      }
    
    public Border clone() {
    	Border border = new Border(left, right, top, bottom);
    	border.setLeftColor(leftColor);
    	border.setRightColor(rightColor);
    	border.setTopColor(topColor);
    	border.setBottomColor(bottomColor);
    	return border;
    }

}
