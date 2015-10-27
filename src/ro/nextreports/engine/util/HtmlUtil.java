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
package ro.nextreports.engine.util;

import java.awt.Color;
import java.util.Map;

import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.exporter.util.StyleFormatConstants;


public class HtmlUtil {
	
	public static String getCssCode(BandElement be, Map<String, Object> style) { 
		return getCssCode(be, style, true);
	}

	// needed is false for table widget exporter when the following are not exported :
	// -> borders
	// -> font family
	// -> font size
	// -> background (if it is white)
	public static String getCssCode(BandElement be, Map<String, Object> style, boolean needed) {

		StringBuilder css = new StringBuilder();
		if (needed) {
			if (style.containsKey(StyleFormatConstants.FONT_FAMILY_KEY)) {
				String val = (String) style.get(StyleFormatConstants.FONT_FAMILY_KEY);
				css.append(" font-family: ").append(val).append(" ;\n");
			}

			if (style.containsKey(StyleFormatConstants.FONT_SIZE)) {
				Float val = (Float) style.get(StyleFormatConstants.FONT_SIZE);
				css.append("font-size: ").append(val.intValue()).append("pt ;\n");
			}
		}
		
		if (style.containsKey(StyleFormatConstants.FONT_COLOR)) {
			Color val = (Color) style.get(StyleFormatConstants.FONT_COLOR);
			css.append("color: rgb(").append(val.getRed()).append(",").append(val.getGreen()).append(",").append(val.getBlue())
					.append(") ;\n");
		}
		if (style.containsKey(StyleFormatConstants.FONT_STYLE_KEY)) {
			if (StyleFormatConstants.FONT_STYLE_NORMAL.equals(style.get(StyleFormatConstants.FONT_STYLE_KEY))) {
				css.append("font-weight: normal ;\n");
				css.append("font-style: normal;  \n");
			}
			if (StyleFormatConstants.FONT_STYLE_BOLD.equals(style.get(StyleFormatConstants.FONT_STYLE_KEY))) {
				css.append("font-weight: bold; \n");
				css.append("font-style: normal;  \n");
			}
			if (StyleFormatConstants.FONT_STYLE_ITALIC.equals(style.get(StyleFormatConstants.FONT_STYLE_KEY))) {
				css.append("font-weight: normal; \n");
				css.append("font-style: italic;  \n");
			}
			if (StyleFormatConstants.FONT_STYLE_BOLDITALIC.equals(style.get(StyleFormatConstants.FONT_STYLE_KEY))) {
				css.append("font-weight: bold; \n");
				css.append("font-style: italic; \n");
			}
		}
		if (style.containsKey(StyleFormatConstants.BACKGROUND_COLOR)) {
			Color val = (Color) style.get(StyleFormatConstants.BACKGROUND_COLOR);
			if ((val.getRed() != 255) || (val.getGreen() != 255) || (val.getBlue() != 255)) {				
				css.append("background-color: rgb(").append(val.getRed()).append(",").append(val.getGreen()).append(",")
						.append(val.getBlue()).append(") ;\n");
			}
		}
		if (style.containsKey(StyleFormatConstants.HORIZONTAL_ALIGN_KEY)) {
			if (StyleFormatConstants.HORIZONTAL_ALIGN_LEFT.equals(style.get(StyleFormatConstants.HORIZONTAL_ALIGN_KEY))) {
				css.append("text-align:left; \n");
			}
			if (StyleFormatConstants.HORIZONTAL_ALIGN_RIGHT.equals(style.get(StyleFormatConstants.HORIZONTAL_ALIGN_KEY))) {
				css.append("text-align:right; \n");
			}
			if (StyleFormatConstants.HORIZONTAL_ALIGN_CENTER.equals(style.get(StyleFormatConstants.HORIZONTAL_ALIGN_KEY))) {
				css.append("text-align:center; \n");
			}
		}
		if (style.containsKey(StyleFormatConstants.VERTICAL_ALIGN_KEY)) {
			if (StyleFormatConstants.VERTICAL_ALIGN_MIDDLE.equals(style.get(StyleFormatConstants.VERTICAL_ALIGN_KEY))) {
				css.append("vertical-align:middle; \n");
			}
			if (StyleFormatConstants.VERTICAL_ALIGN_TOP.equals(style.get(StyleFormatConstants.VERTICAL_ALIGN_KEY))) {
				css.append("vertical-align:top; \n");
			}
			if (StyleFormatConstants.VERTICAL_ALIGN_BOTTOM.equals(style.get(StyleFormatConstants.VERTICAL_ALIGN_KEY))) {
				css.append("vertical-align:bottom; \n");
			}
		}
		if (style.containsKey(StyleFormatConstants.PADDING_LEFT)) {
			Float val = (Float) style.get(StyleFormatConstants.PADDING_LEFT);
			css.append("padding-left:").append(val).append("pt; \n");
		}
		if (style.containsKey(StyleFormatConstants.PADDING_RIGHT)) {
			Float val = (Float) style.get(StyleFormatConstants.PADDING_RIGHT);
			css.append("padding-right:").append(val).append("pt; \n");
		}
		if (style.containsKey(StyleFormatConstants.PADDING_TOP)) {
			Float val = (Float) style.get(StyleFormatConstants.PADDING_TOP);
			css.append("padding-top:").append(val).append("pt; \n");
		}
		if (style.containsKey(StyleFormatConstants.PADDING_BOTTOM)) {
			Float val = (Float) style.get(StyleFormatConstants.PADDING_BOTTOM);
			css.append("padding-bottom:").append(val).append("px; \n");
		}
		
		if (needed) {
			if (style.containsKey(StyleFormatConstants.BORDER_LEFT)) {
				Float val = (Float) style.get(StyleFormatConstants.BORDER_LEFT);
				css.append("border-left:").append(val.intValue()).append("px; \n");
				css.append("border-left-style:solid; \n");
				Color color = (Color) style.get(StyleFormatConstants.BORDER_LEFT_COLOR);
				css.append("border-left-color: ").append(Integer.toHexString(color.getRGB() & 0x00ffffff)).append(" ;\n");

			} else {
				css.append("border-left: none; \n");
			}
			if (style.containsKey(StyleFormatConstants.BORDER_RIGHT)) {
				Float val = (Float) style.get(StyleFormatConstants.BORDER_RIGHT);
				css.append("border-right:").append(val.intValue()).append("px; \n");
				css.append("border-right-style:solid; \n");
				Color color = (Color) style.get(StyleFormatConstants.BORDER_RIGHT_COLOR);
				css.append("border-right-color: ").append(Integer.toHexString(color.getRGB() & 0x00ffffff)).append(" ;\n");
			} else {
				css.append("border-right: none; \n");
			}
			if (style.containsKey(StyleFormatConstants.BORDER_TOP)) {
				Float val = (Float) style.get(StyleFormatConstants.BORDER_TOP);
				css.append("border-top:").append(val.intValue()).append("px; \n");
				css.append("border-top-style:solid; \n");
				Color color = (Color) style.get(StyleFormatConstants.BORDER_TOP_COLOR);
				css.append("border-top-color: ").append(Integer.toHexString(color.getRGB() & 0x00ffffff)).append(" ;\n");
			} else {
				css.append("border-top: none; \n");
			}
			if (style.containsKey(StyleFormatConstants.BORDER_BOTTOM)) {
				Float val = (Float) style.get(StyleFormatConstants.BORDER_BOTTOM);
				css.append("border-bottom:").append(val.intValue()).append("px; \n");
				css.append("border-bottom-style:solid; \n");
				Color color = (Color) style.get(StyleFormatConstants.BORDER_BOTTOM_COLOR);
				css.append("border-bottom-color: ").append(Integer.toHexString(color.getRGB() & 0x00ffffff)).append(" ;\n");
			} else {
				css.append("border-bottom: none; \n");
			}
		}
		if (be != null) {
			if (!be.isWrapText()) {
				css.append("white-space: nowrap; \n");
			} else {
				css.append("word-wrap: break-word; \n");
				css.append("line-height: " + be.getPercentLineSpacing() + "%; \n");
			}
			// if (be.getTextRotation() != 0) {
			// css.append(getRotationStyle(be.getTextRotation()));
			// }
		}

		return css.toString();

	}
}
