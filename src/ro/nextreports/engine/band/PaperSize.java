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

public class PaperSize implements Serializable {
	
	private static final long serialVersionUID = 1213104593379132110L ;
	
	public static final String UNIT_IN = "in";
	public static final String UNIT_CM = "cm";
	
	public static final PaperSize A4 = new PaperSize(UNIT_CM, 21, 29.7f);
	
	private String unit;
	private float width;
	private float height;
	
	public PaperSize(String unit, float width, float height) {
		super();
		this.unit = unit;
		this.width = width;
		this.height = height;
	}

	public String getUnit() {
		return unit;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(height);
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
		result = prime * result + Float.floatToIntBits(width);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		PaperSize other = (PaperSize) obj;
		if (Float.floatToIntBits(height) != Float.floatToIntBits(other.height))	return false;
		if (unit == null) {
			if (other.unit != null)	return false;
		} else if (!unit.equals(other.unit)) return false;
		if (Float.floatToIntBits(width) != Float.floatToIntBits(other.width)) return false;
		return true;
	}

	@Override
	public String toString() {
		return width + " x " + height + " (" + unit + ")" ;
	}	
	
	public int getWidthPoints() {
		return getPoints(width);
	}
	
	public int getHeightPoints() {
		return getPoints(height);
	}
	
	private int getPoints(float dim) {
		int points;
		if (UNIT_CM.equals(unit)) {
			points = (int)(dim * 72 / 2.54);
		} else {
			points = (int)(dim * 72);
		}
		return points;
	}
	
}
