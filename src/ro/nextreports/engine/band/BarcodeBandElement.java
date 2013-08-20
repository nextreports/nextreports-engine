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

import com.itextpdf.text.pdf.Barcode;
import com.itextpdf.text.pdf.BarcodeEAN;

/**
 * @author Mihai Dinca-Panaitescu
 */
public class BarcodeBandElement extends ImageBandElement {
	
	public static int EAN13 = BarcodeEAN.EAN13;
	public static int EAN8 = BarcodeEAN.EAN8;
	public static int UPCA = BarcodeEAN.UPCA;
	public static int UPCE = BarcodeEAN.UPCE;
	public static int SUPP2 = BarcodeEAN.SUPP2;
	public static int SUPP5 = BarcodeEAN.SUPP5;
	public static int CODE128 = Barcode.CODE128;
	public static int CODE128_RAW = Barcode.CODE128_RAW;
	public static int INTER25 = 100;
	public static int CODE39 = 105;
	public static int CODE39EXT = 106;
	public static int CODABAR = 110;
	public static int PDF417 = 120;
	public static int DATAMATRIX = 125;
	public static int QRCODE = 130;	
	
	private int barcodeType;
	private String value;
	private boolean isColumn;
	
	public BarcodeBandElement(int barcodeType, String value, boolean isColumn) {
        super("");
        this.text = "$B(" + value + ")";
        this.barcodeType = barcodeType;
        this.value = value;
        this.isColumn = isColumn;
    }

	public int getBarcodeType() {
		return barcodeType;
	}

	public void setBarcodeType(int barcodeType) {
		this.barcodeType = barcodeType;		
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
		setText("$B(" + value + ")");
	}

	public boolean isColumn() {
		return isColumn;
	}

	public void setColumn(boolean isColumn) {
		this.isColumn = isColumn;		
	}
	
	public void setImage(String image) {
        this.image = image;             
    }	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + barcodeType;
		result = prime * result + (isColumn ? 1231 : 1237);
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj))	return false;
		if (getClass() != obj.getClass()) return false;
		BarcodeBandElement other = (BarcodeBandElement) obj;
		if (barcodeType != other.barcodeType) return false;
		if (isColumn != other.isColumn)	return false;
		if (value == null) {
			if (other.value != null) return false;
		} else if (!value.equals(other.value)) return false;
		return true;
	}
	
	public static boolean isEANFamily(int type) {
		return (type == EAN13) ||
				(type == EAN8) ||
				(type == UPCA) ||
				(type == UPCE) ||
				(type == SUPP2) ||
				(type == SUPP5);
	}
	
	
	

}
