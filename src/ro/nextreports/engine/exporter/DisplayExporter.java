package ro.nextreports.engine.exporter;

import java.awt.Color;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.ColumnBandElement;
import ro.nextreports.engine.exporter.util.DisplayData;
import ro.nextreports.engine.exporter.util.StyleFormatConstants;
import ro.nextreports.engine.queryexec.QueryException;
import ro.nextreports.engine.util.ColorUtil;
import ro.nextreports.engine.util.StringUtil;

public class DisplayExporter extends ResultExporter {

	private DisplayData data;

	public DisplayExporter(ExporterBean bean) {
		super(bean);
		data = new DisplayData();
	}

	protected void exportCell(String bandName, BandElement bandElement, Object value, int gridRow, int row, int column, int cols,
			int rowSpan, int colSpan, boolean isImage) {

		int headerRows = getHeaderRows();
		int detailRows = getDetailRows();
		Map<String, Object> style = buildCellStyleMap(bandElement, value, gridRow, column, colSpan);

		if (ReportLayout.HEADER_BAND_NAME.equals(bandName)) {
			if (headerRows == 1) {
				if (row == 0) {
					switch (column) {
					case 0:
						data.setTitle(replaceParameters(getBandElementValueAsString(bandElement)));
						if (bandElement.getHorizontalAlign() == BandElement.CENTER) {
							data.setTitleAlignment(DisplayData.TITLE_ALIGNMENT_CENTER);
						} else {
							data.setTitleAlignment(DisplayData.TITLE_ALIGNMENT_VALUE);
						}
						data.setBackground(ColorUtil.getHexColor(bandElement.getBackground()));
						if (style.containsKey(StyleFormatConstants.BACKGROUND_COLOR)) {
							data.setBackground(ColorUtil.getHexColor((Color) style.get(StyleFormatConstants.BACKGROUND_COLOR)));
						}
						data.setTitleColor(ColorUtil.getHexColor(bandElement.getForeground()));
						if (style.containsKey(StyleFormatConstants.FONT_COLOR)) {
							data.setTitleColor(ColorUtil.getHexColor((Color) style.get(StyleFormatConstants.FONT_COLOR)));
						}
						break;
					case 1:
						data.setShouldRise(Boolean.parseBoolean(bandElement.getText()));
						break;
					case 2:
						data.setShadow(Boolean.parseBoolean(bandElement.getText()));
						break;
					}
				} 
			}
		} else if (ReportLayout.DETAIL_BAND_NAME.equals(bandName)) {
			if (detailRows == 1) {
				if (column == 0) {					
					data.setValueColor(ColorUtil.getHexColor(bandElement.getForeground()));
					if (style.containsKey(StyleFormatConstants.FONT_COLOR)) {
						data.setValueColor(ColorUtil.getHexColor((Color) style.get(StyleFormatConstants.FONT_COLOR)));
					}
					data.setValue(StringUtil.getValueAsString(value, ((ColumnBandElement)bandElement).getPattern()));
					for (Alert alert : alerts) {                	
						if (isAlert(alert, value)) {
							executeAlert(alert, value, "");
						} 
					}
				} else if (column == 1) {					
					data.setPreviousColor(ColorUtil.getHexColor(bandElement.getForeground()));
					if (style.containsKey(StyleFormatConstants.FONT_COLOR)) {
						data.setPreviousColor(ColorUtil.getHexColor((Color) style.get(StyleFormatConstants.FONT_COLOR)));
					}					
					data.setPrevious(StringUtil.getValueAsString(value, ((ColumnBandElement)bandElement).getPattern()));
				} else if (column == 2) {										
					data.setUp((Boolean)value);
				} else if (column == 3) {	
					String prev = getBandElementValueAsString(bandElement);
					if ((prev != null) && !prev.endsWith("%")) {
						prev = prev + "%";
					}
					data.setPrevious(prev);
				}
			}
			return;
		}

	}
	
	// replace $P{...} parameters (used in title and x,y legends
	
    private String replaceParameters(String text) {
    	Map<String, Object> params  = bean.getParametersBean().getParamValues();
        for  (String param : params.keySet()) {
             text = StringUtil.replace(text, "\\$P\\{" + param + "\\}", StringUtil.getValueAsString(params.get(param), null));
        }
        return text;
    }    

	protected void flush() {
	}

	protected void flushNow() {
	}

	protected void initExport() throws QueryException {
	}

	protected void finishExport() {
	}

	protected Set<CellElement> getIgnoredCells(Band band) {
		return new HashSet<CellElement>();
	}

	protected void afterRowExport() {
	}

	protected void close() {
	}

	protected String getNullElement() {
		return "";
	}

	public DisplayData getData() {
		return data;
	}

}
