package ro.nextreports.engine.exporter.util;

import java.awt.Color;
import java.io.Serializable;
import java.io.StringWriter;

import ro.nextreports.engine.util.ColorUtil;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DisplayData implements Serializable {
		
	private static final long serialVersionUID = 2512427516851551606L;
	
	private String title;
	private String titleAlignment;
	private String titleColor;
	private String value;
	private String valueColor;
	private String previous;
	private String previousColor;
	private String background;
	private boolean up;
	private boolean shadow;
	private boolean shouldRise;
	
	public static final String TITLE_ALIGNMENT_CENTER = "center";
	public static final String TITLE_ALIGNMENT_VALUE = "alignToValue";
	
	public DisplayData() {
		super();
		title = "";
		titleAlignment = TITLE_ALIGNMENT_VALUE;
		titleColor = ColorUtil.getHexColor(Color.BLACK);
		value = "0";
		valueColor = ColorUtil.getHexColor(Color.BLACK);
		previous = null;
		previousColor = ColorUtil.getHexColor(Color.GRAY);
		background = ColorUtil.getHexColor(Color.WHITE);
		up = true;
		shadow = false;
		shouldRise = true;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
		
	public String getTitleAlignment() {
		return titleAlignment;
	}

	public void setTitleAlignment(String titleAlignment) {
		this.titleAlignment = titleAlignment;
	}

	public String getTitleColor() {
		return titleColor;
	}

	public void setTitleColor(String titleColor) {
		this.titleColor = titleColor;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValueColor() {
		return valueColor;
	}

	public void setValueColor(String valueColor) {
		this.valueColor = valueColor;
	}

	public String getPrevious() {
		return previous;
	}

	public void setPrevious(String previous) {
		this.previous = previous;
	}

	public String getPreviousColor() {
		return previousColor;
	}

	public void setPreviousColor(String previousColor) {
		this.previousColor = previousColor;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public boolean isShadow() {
		return shadow;
	}

	public void setShadow(boolean shadow) {
		this.shadow = shadow;
	}

	public boolean isShouldRise() {
		return shouldRise;
	}

	public void setShouldRise(boolean shouldRise) {
		this.shouldRise = shouldRise;
	}
	
	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();		
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		StringWriter writer = new StringWriter();
		try {
			mapper.writeValue(writer, this);
			return writer.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return "Error : " + ex.getMessage();
		}
	}	
}
