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

/**
 * @author Decebal Suiu
 */
public class Padding implements Serializable {

    private static final long serialVersionUID = -1216704563379136330L;

    private int left;
	private int right;
	private int top;
	private int bottom;

    public Padding() {
    }

	public Padding(int left, int right, int top, int bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Padding padding = (Padding) o;

        if (bottom != padding.bottom) return false;
        if (left != padding.left) return false;
        if (right != padding.right) return false;
        if (top != padding.top) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = left;
        result = 29 * result + right;
        result = 29 * result + top;
        result = 29 * result + bottom;
        return result;
    }

    @Override
	public String toString() {
		return "(" + top + "," + left + "," + bottom + "," + right + ")";
	}

}
