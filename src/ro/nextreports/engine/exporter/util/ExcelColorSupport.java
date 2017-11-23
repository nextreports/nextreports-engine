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

import java.awt.Color;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.hssf.util.HSSFColor;

/**
 * POI Excel utility methods.
 *
 * @author Heiko Evermann
 */
public final class ExcelColorSupport {
    /**
     * DefaultConstructor.
     */
    private ExcelColorSupport() {
    }

    /**
     * the pre-defined excel color triplets.
     */
    private static Map<String, HSSFColor> triplets;


    /**
     * Find a suitable color for the cell.
     * <p/>
     * The algorithm searches all available triplets, weighted by tripletvalue and
     * tripletdifference to the other triplets. The color wins, which has the
     * smallest triplet difference and where all triplets are nearest to the
     * requested color.
     *
     * @param awtColor the awt color that should be transformed into an Excel color.
     * @return the excel color index that is nearest to the supplied color.
     */
    public static synchronized short getNearestColor(
            final Color awtColor) {
        if (triplets == null) {
            triplets = HSSFColor.getTripletHash();
        }

        if (triplets == null || triplets.isEmpty()) {
            System.out.println("Unable to get triplet hashtable");
            return HSSFColor.BLACK.index;
        }

        short color = HSSFColor.BLACK.index;
        double minDiff = Double.MAX_VALUE;

        // get the color without the alpha chanel
        final float[] hsb = Color.RGBtoHSB(awtColor.getRed(), awtColor
                .getGreen(), awtColor.getBlue(), null);

        float[] excelHsb = null;
        final Iterator elements = triplets.values().iterator();
        while (elements.hasNext()) {
            final HSSFColor crtColor = (HSSFColor) elements.next();
            final short[] rgb = crtColor.getTriplet();
            excelHsb = Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], excelHsb);

            final double weight = 3.0d * Math.abs(excelHsb[0] - hsb[0])
                    + Math.abs(excelHsb[1] - hsb[1])
                    + Math.abs(excelHsb[2] - hsb[2]);

            if (weight < minDiff) {
                minDiff = weight;
                if (minDiff == 0) {
                    // we found the color ...
                    return crtColor.getIndex();
                }
                color = crtColor.getIndex();
            }
        }
        return color;
    }


}
