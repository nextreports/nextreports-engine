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

/**
 * @author alexandru.parvulescu
 */
public class StyleFormatConstants {

	public static final String ROMAN_PATTERN = "MCMLXXXVII";
	public static final String PATTERN = "pattern";
    public static final String URL = "url";

    public static final String FONT_FAMILY_KEY = "font_family"; // ?
    public static final String FONT_NAME_KEY = "font_name";

    /**
	 * Font Size - value type Float
	 */
	public static final String FONT_SIZE = "font_size";

	public static final String FONT_STYLE_KEY = "font_style_key";

	public static final String FONT_STYLE_ITALIC = "font_style_italic";

	public static final String FONT_STYLE_NORMAL = "font_stylet_normal";

	public static final String FONT_STYLE_BOLD = "font_style_bold";

	public static final String FONT_STYLE_BOLDITALIC = "font_style_bold_italic";

	/**
	 * Font Color - value type java.awt.Color
	 */
	public static final String FONT_COLOR = "font_color";

	/**
	 * Cell Background Color - value type java.awt.Color
	 */
	public static final String BACKGROUND_COLOR = "bg_color";

	/**
	 * Cell Horizontal Alignment - key
	 */
	public static final String HORIZONTAL_ALIGN_KEY = "halign";

	/**
	 * Cell Horizontal Alignment - value 'LEFT'
	 */
	public static final String HORIZONTAL_ALIGN_LEFT = "halign_left";

	/**
	 * Cell Horizontal Alignment - value 'RIGHT'
	 */
	public static final String HORIZONTAL_ALIGN_RIGHT = "halign_right";

	/**
	 * Cell Horizontal Alignment - value 'CENTER'
	 */
	public static final String HORIZONTAL_ALIGN_CENTER = "halign_center";

    public static final String VERTICAL_ALIGN_KEY = "valign";
    public static final String VERTICAL_ALIGN_TOP = "valign_top";
    public static final String VERTICAL_ALIGN_MIDDLE = "valign_middle";
    public static final String VERTICAL_ALIGN_BOTTOM = "valign_bottom";

    public static final String DOC_TYPE_PDF = "pdf";

	public static final String DOC_TYPE_EXCEL = "excel";

	public static final String DOC_TYPE_HTML = "html";

	public static final Long STYLE_GLOBAL_KEY = new Long(-1);

	public static final Long STYLE_TITLE_KEY = new Long(-2);

	public static final Long STYLE_TABLE_HEADER_KEY = new Long(-3);

	public static final Long STYLE_TABLE_EXTRA_ROW_KEY = new Long(-4);

	public static final Long STYLE_TABLE_DEFAUL_COLUMN_KEY = new Long(0);

	/**
	 * Cell Border - value type Float
	 */
	public static final String BORDER = "border";

	public static final String BORDER_LEFT = "border_left";

	public static final String BORDER_RIGHT = "border_right";

	public static final String BORDER_TOP = "border_top";

	public static final String BORDER_BOTTOM = "border_bottom";
	
	public static final String BORDER_LEFT_COLOR = "border_left_color";

	public static final String BORDER_RIGHT_COLOR = "border_right_color";

	public static final String BORDER_TOP_COLOR = "border_top_color";

	public static final String BORDER_BOTTOM_COLOR = "border_bottom_color";

	// public static final String MARGIN = "margin"; // ?
	/**
	 * Cell Padding - value type Float
	 */
	public static final String PADDING_LEFT = "padding_left";

	public static final String PADDING_RIGHT = "padding_right";

	public static final String PADDING_TOP = "padding_top";

	public static final String PADDING_BOTTOM = "padding_bottom";
}
