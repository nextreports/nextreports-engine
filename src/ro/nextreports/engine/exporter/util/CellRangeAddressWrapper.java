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
package ro.nextreports.engine.exporter.util;

import org.apache.poi.ss.util.CellRangeAddress;

public class CellRangeAddressWrapper implements Comparable<CellRangeAddressWrapper> {

	public CellRangeAddress range;

	/**
	 * @param theRange the CellRangeAddress object to wrap.
	 */
	public CellRangeAddressWrapper(CellRangeAddress theRange) {
		this.range = theRange;
	}

	/**
	 * @param o
	 *            the object to compare.
	 * @return -1 the current instance is prior to the object in parameter, 0:
	 *         equal, 1: after...
	 */
	public int compareTo(CellRangeAddressWrapper o) {

		if (range.getFirstColumn() < o.range.getFirstColumn() && range.getFirstRow() < o.range.getFirstRow()) {
			return -1;
		} else if (range.getFirstColumn() == o.range.getFirstColumn() && range.getFirstRow() == o.range.getFirstRow()) {
			return 0;
		} else {
			return 1;
		}

	}

}
