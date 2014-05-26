package ro.nextreports.engine.util;

import java.awt.Color;

public class ColorUtil {
	
	public static String getHexColor(Color color) {
        String rgb = Integer.toHexString(color.getRGB());
        rgb = rgb.substring(2, rgb.length());
        return "#" + rgb;
    }

}
