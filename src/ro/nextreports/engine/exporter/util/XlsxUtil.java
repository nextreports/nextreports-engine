package ro.nextreports.engine.exporter.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class XlsxUtil {
	
	private XlsxUtil() {
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
	public static int copyToSheet(XSSFSheet parentSheet, int parentSheetRow, int parentSheetColumn, XSSFSheet sheet) {
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
	public static int copyToSheet(XSSFSheet parentSheet, int parentSheetRow, int parentSheetColumn, XSSFSheet sheet, boolean copyStyle) {
		int maxColumnNum = 0;
		Map<Integer, CellStyle> styleMap = (copyStyle) ? new HashMap<Integer, CellStyle>() : null;
		for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
			XSSFRow srcRow = sheet.getRow(i);
			XSSFRow destRow;
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
				copyRow(sheet, parentSheet, parentSheetRow, parentSheetColumn, srcRow, destRow, styleMap);
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
	public static void copyRow(XSSFSheet srcSheet, XSSFSheet destSheet, int parentSheetRow, int parentSheetColumn, XSSFRow srcRow, XSSFRow destRow,
			Map<Integer, CellStyle> styleMap) {
		// manage a list of merged zone in order to not insert two times a
		// merged zone
		Set<CellRangeAddressWrapper> mergedRegions = new TreeSet<CellRangeAddressWrapper>();
		destRow.setHeight(srcRow.getHeight());
		// pour chaque row
		for (int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++) {
			XSSFCell oldCell = srcRow.getCell(j); // ancienne cell			
			if (oldCell != null) {				
				XSSFCell newCell = destRow.createCell(parentSheetColumn + j);				
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
	public static void copyCell(XSSFCell oldCell, XSSFCell newCell, Map<Integer, CellStyle> styleMap) {
		if (styleMap != null) {			
			if (oldCell.getSheet().getWorkbook() == newCell.getSheet().getWorkbook()) {
				newCell.setCellStyle(oldCell.getCellStyle());
			} else {				
				int stHashCode = oldCell.getCellStyle().hashCode();
				CellStyle newCellStyle = styleMap.get(stHashCode);				
				if (newCellStyle == null) {					
					newCellStyle = newCell.getSheet().getWorkbook().createCellStyle();
					newCellStyle.cloneStyleFrom(oldCell.getCellStyle());					
					styleMap.put(stHashCode, newCellStyle);
				}
				newCell.setCellStyle(newCellStyle);
			}			
		}
		switch (oldCell.getCellType()) {
		case XSSFCell.CELL_TYPE_STRING:
			newCell.setCellValue(oldCell.getStringCellValue());
			break;
		case XSSFCell.CELL_TYPE_NUMERIC:
			newCell.setCellValue(oldCell.getNumericCellValue());
			break;
		case XSSFCell.CELL_TYPE_BLANK:
			newCell.setCellType(XSSFCell.CELL_TYPE_BLANK);
			break;
		case XSSFCell.CELL_TYPE_BOOLEAN:
			newCell.setCellValue(oldCell.getBooleanCellValue());
			break;
		case XSSFCell.CELL_TYPE_ERROR:
			newCell.setCellErrorValue(oldCell.getErrorCellValue());
			break;
		case XSSFCell.CELL_TYPE_FORMULA:
			newCell.setCellFormula(oldCell.getCellFormula());
			break;
		default:
			break;
		}

	}
	
	public static CellRangeAddress getMergedRegion(XSSFSheet sheet, int rowNum, short cellNum) {
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
