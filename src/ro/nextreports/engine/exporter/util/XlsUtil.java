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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * http://www.coderanch.com/t/420958/open-source/Copying-sheet-excel-file-another
 */
public final class XlsUtil {
	
	private XlsUtil() {
	}

	/**
	 * Copy a sheet to another sheet at a specific (row, column) position
	 * 
	 * @param parentSheet the sheet to copy into
	 * @param parentSheetRow the row inside parentSheet where we start to copy
	 * @param parentSheetColumn the column inside parentSheet where we start to copy
	 * @param sheet the sheet that is copied
	 * @return column number
	 */
	public static int copyToSheet(HSSFSheet parentSheet, int parentSheetRow, int parentSheetColumn, HSSFSheet sheet) {
		return copyToSheet(parentSheet, parentSheetRow, parentSheetColumn, sheet, true);
	}

	/**
	 * Copy a sheet to another sheet at a specific (row, column) position
	 * 
	 * @param parentSheet the sheet to copy into
	 * @param parentSheetRow the row inside parentSheet where we start to copy
	 * @param parentSheetColumn the column inside parentSheet where we start to copy
	 * @param sheet the sheet that is copied
	 * @param copyStyle true to copy the style
	 * @return column number
	 */
	public static int copyToSheet(HSSFSheet parentSheet, int parentSheetRow, int parentSheetColumn, HSSFSheet sheet, boolean copyStyle) {
		int maxColumnNum = 0;
		Map<Integer, HSSFCellStyle> styleMap = (copyStyle) ? new HashMap<Integer, HSSFCellStyle>() : null;
		for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
			HSSFRow srcRow = sheet.getRow(i);
			HSSFRow destRow;
			// subreport is not the first cell in a row
			if ((parentSheetColumn > 0) && (i == sheet.getFirstRowNum())) {
				destRow = parentSheet.getRow(parentSheetRow);
			} else {
				destRow = parentSheet.getRow(parentSheetRow+i);
				if (destRow == null) {
					destRow = parentSheet.createRow(parentSheetRow + i);
				}
			}
			if (srcRow != null) {
				XlsUtil.copyRow(sheet, parentSheet, parentSheetRow, parentSheetColumn, srcRow, destRow, styleMap);
				if (srcRow.getLastCellNum() > maxColumnNum) {
					maxColumnNum = srcRow.getLastCellNum();
				}
			}
		}
		for (int i = 0; i <= maxColumnNum; i++) {
			parentSheet.setColumnWidth(i, sheet.getColumnWidth(i));
		}
		return maxColumnNum;
	}

	/**
	 * Copy a row from a sheet to another sheet
	 * 
	 * 
	 * @param srcSheet the sheet to copy
	 * @param destSheet the sheet to copy into
	 * @param parentSheetRow the row inside destSheet where we start to copy
	 * @param parentSheetColumn the column inside destSheet where we start to copy
	 * @param srcRow the row to copy
	 * @param destRow the row to create
	 * @param styleMap style map
	 *       
	 */
	public static void copyRow(HSSFSheet srcSheet, HSSFSheet destSheet, int parentSheetRow, int parentSheetColumn, HSSFRow srcRow, HSSFRow destRow,
			Map<Integer, HSSFCellStyle> styleMap) {
		// manage a list of merged zone in order to not insert two times a
		// merged zone
		Set<CellRangeAddressWrapper> mergedRegions = new TreeSet<CellRangeAddressWrapper>();
		destRow.setHeight(srcRow.getHeight());
		// pour chaque row
		for (int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++) {
			HSSFCell oldCell = srcRow.getCell(j); // ancienne cell			
			if (oldCell != null) {				
				HSSFCell newCell = destRow.createCell(parentSheetColumn + j);				
				copyCell(oldCell, newCell, styleMap);
				
				CellRangeAddress mergedRegion = getMergedRegion(srcSheet, srcRow.getRowNum(), (short) oldCell.getColumnIndex());

				if (mergedRegion != null) {
					
					CellRangeAddress newMergedRegion = new CellRangeAddress(parentSheetRow + mergedRegion.getFirstRow(),
							parentSheetRow + mergedRegion.getLastRow(), 
							parentSheetColumn + mergedRegion.getFirstColumn(), 
							parentSheetColumn + mergedRegion.getLastColumn());
					
					CellRangeAddressWrapper wrapper = new CellRangeAddressWrapper(newMergedRegion);
					if (isNewMergedRegion(wrapper, mergedRegions)) {
						mergedRegions.add(wrapper);
						destSheet.addMergedRegion(wrapper.range);
					}
				}
			}
		}

	}

	/**
	 * Copy a cell to another cell
	 * 
	 * @param oldCell cell to be copied
	 * @param newCell cell to be created
	 * @param styleMap style map
	 */
	public static void copyCell(HSSFCell oldCell, HSSFCell newCell, Map<Integer, HSSFCellStyle> styleMap) {
		if (styleMap != null) {			
			if (oldCell.getSheet().getWorkbook() == newCell.getSheet().getWorkbook()) {
				newCell.setCellStyle(oldCell.getCellStyle());
			} else {				
				int stHashCode = oldCell.getCellStyle().hashCode();
				HSSFCellStyle newCellStyle = styleMap.get(stHashCode);				
				if (newCellStyle == null) {					
					newCellStyle = newCell.getSheet().getWorkbook().createCellStyle();
					newCellStyle.cloneStyleFrom(oldCell.getCellStyle());					
					styleMap.put(stHashCode, newCellStyle);
				}
				newCell.setCellStyle(newCellStyle);
			}			
		}
		switch (oldCell.getCellType()) {
		case HSSFCell.CELL_TYPE_STRING:
			newCell.setCellValue(oldCell.getStringCellValue());
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:
			newCell.setCellValue(oldCell.getNumericCellValue());
			break;
		case HSSFCell.CELL_TYPE_BLANK:
			newCell.setCellType(HSSFCell.CELL_TYPE_BLANK);
			break;
		case HSSFCell.CELL_TYPE_BOOLEAN:
			newCell.setCellValue(oldCell.getBooleanCellValue());
			break;
		case HSSFCell.CELL_TYPE_ERROR:
			newCell.setCellErrorValue(oldCell.getErrorCellValue());
			break;
		case HSSFCell.CELL_TYPE_FORMULA:
			newCell.setCellFormula(oldCell.getCellFormula());
			break;
		default:
			break;
		}

	}
	
	public static CellRangeAddress getMergedRegion(HSSFSheet sheet, int rowNum, short cellNum) {
		for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
			CellRangeAddress merged = sheet.getMergedRegion(i);
			if (merged.isInRange(rowNum, cellNum)) {
				return merged;
			}
		}
		return null;
	}

	/**
	 * Check that the merged region has been created in the destination sheet.
	 * 
	 * @param newMergedRegion the merged region to copy or not in the destination sheet.
	 * @param mergedRegions the list containing all the merged region.
	 * @return true if the merged region is already in the list or not.
	 */
	private static boolean isNewMergedRegion(CellRangeAddressWrapper newMergedRegion, Set<CellRangeAddressWrapper> mergedRegions) {
		return !mergedRegions.contains(newMergedRegion);
	}

}
