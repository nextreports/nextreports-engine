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
import java.util.List;
import java.util.Vector;

import ro.nextreports.engine.condition.RowFormattingConditions;

public class RowElement implements Serializable {
	
	private static final long serialVersionUID = -471125249390470148L;
	
	private Vector<BandElement> elements;
	private RowFormattingConditions formattingConditions;
	private boolean startOnNewPage;
	
	public RowElement(Vector<BandElement> vElements) {
		this.elements = vElements;
		this.formattingConditions = new RowFormattingConditions("");
	}
	
	public RowElement(List<BandElement> elements) {
		this.elements = new Vector(elements);
		this.formattingConditions = new RowFormattingConditions("");
	}
	
	public Vector<BandElement> getElements() {
		return elements;
	}
		
	public RowFormattingConditions getFormattingConditions() {
		return formattingConditions;
	}
	
	public void setFormattingConditions(RowFormattingConditions formattingConditions) {
		if (formattingConditions == null) {
			formattingConditions = new RowFormattingConditions("");
		}
		this.formattingConditions = formattingConditions;
	}
		
	public boolean isStartOnNewPage() {
		return startOnNewPage;
	}

	public void setStartOnNewPage(boolean startOnNewPage) {
		this.startOnNewPage = startOnNewPage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result	+ ((formattingConditions == null) ? 0 : formattingConditions.hashCode());
		result = prime * result	+ ((elements == null) ? 0 : elements.hashCode());
		result = prime * result + (startOnNewPage ? 1 : 0);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RowElement other = (RowElement) obj;		
		if (formattingConditions == null) {
			if (other.formattingConditions != null)	return false;			
		} else if (!formattingConditions.equals(other.formattingConditions)) return false;
		if (elements == null) {
			if (other.elements != null) return false;
		} else if (!elements.equals(other.elements)) return false;
		if (startOnNewPage != other.startOnNewPage) return false;
		return true;
	}

	@Override
	public String toString() {
		return "RowElement [elements=" + elements + 
				", startOnNewPage=" + startOnNewPage +
				", formattingConditions=" + formattingConditions + "]";
	}		
		
}
