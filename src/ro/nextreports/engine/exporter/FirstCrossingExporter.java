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
package ro.nextreports.engine.exporter;

import java.util.HashSet;
import java.util.Set;

import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.queryexec.QueryException;

/**
 * Empty exporter (does not print anything)
 * 
 * It is used as a first data crossing to compute some values (which otherwise are not known at the moment of cell print)
 * This is used in ResultExporter 
 * 
 * @author Mihai Dinca-Panaitescu
 * @date 16.07.2013
 */
class FirstCrossingExporter extends ResultExporter {
	
	public FirstCrossingExporter(ExporterBean bean) {
        super(bean);
    }

	@Override
	protected String getNullElement() {
		return "";
	}

	@Override
	protected Set<CellElement> getIgnoredCells(Band band) {
		return new HashSet<CellElement>();
	}

	@Override
	protected void exportCell(String bandName, BandElement bandElement, Object value, int gridRow, int row, int column, int cols,
			int rowSpan, int colSpan, boolean isImage) {
		// nothing to do 
		// just need to compute template values inside base ResultExporter
	}

	@Override
	protected void afterRowExport() {		
	}

	@Override
	protected void close() {		
	}

	@Override
	protected void flush() {
	}

	@Override
	protected void flushNow() {
	}

	@Override
	protected void initExport() throws QueryException {
	}

	@Override
	protected void finishExport() {
	}

}
