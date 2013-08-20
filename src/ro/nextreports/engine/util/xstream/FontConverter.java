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
package ro.nextreports.engine.util.xstream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Sep 19, 2008
 * Time: 5:26:41 PM
 */
public class FontConverter implements Converter {

    public boolean canConvert(Class clazz) {
        return clazz.getName().equals("java.awt.Font") ||
                clazz.getName().equals("javax.swing.plaf.FontUIResource");
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {

        Font font = (Font) value;

        writer.startNode("name");
        writer.setValue(font.getName());
        writer.endNode();

        writer.startNode("size");
        writer.setValue(String.valueOf(font.getSize()));
        writer.endNode();

        writer.startNode("style");
        writer.setValue(String.valueOf(font.getStyle()));
        writer.endNode();
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

        String name = "Arial";
        int size = 12;
        int style = Font.PLAIN;
        boolean oldFont = false;
        float posture = 0;
        float weight = 1;

        while (reader.hasMoreChildren()) {
            reader.moveDown();

            String childName = reader.getNodeName();

            // for new saved reports
            if ("name".equals(childName)) {
                name = reader.getValue();
            } else if ("size".equals(childName)) {
                String sizeS = reader.getValue();
                size = Integer.parseInt(sizeS);
            } else if ("style".equals(childName)) {
                String styleS = reader.getValue();
                style = Integer.parseInt(styleS);

            // for old saved reports -> before report version 1.8
            } else if ("attributes".equals(childName)) {
                oldFont = true;

                while (reader.hasMoreChildren()) {
                    reader.moveDown();
                    // for every entry
                    boolean isName = false;
                    boolean isSize = false;
                    boolean isWeight = false;  // 2.0=BOLD 1.0=NORMAL
                    boolean isPosture = false; // 0.0=REGULAR  0.2=ITALIC
                    while (reader.hasMoreChildren()) {
                        reader.moveDown();
                        if ("awt-text-attribute".equals(reader.getNodeName())) {
                            if ("family".equals(reader.getValue())) {
                                isName = true;
                            } else if ("size".equals(reader.getValue())) {
                                isSize = true;
                            } else if ("weight".equals(reader.getValue())) {
                                isWeight = true;
                            } else if ("posture".equals(reader.getValue())) {
                                isPosture = true;
                            }
                        } else if ("string".equals(reader.getNodeName())) {
                            if (isName) {
                                name = reader.getValue();
                                isName = false;
                            }
                        } else if ("float".equals(reader.getNodeName())) {
                            if (isSize) {
                                size = (int) Float.parseFloat(reader.getValue());
                                isSize = false;
                            } else if (isPosture) {
                                posture = Float.parseFloat(reader.getValue());
                                isPosture = false;
                            } else if (isWeight) {
                                weight = Float.parseFloat(reader.getValue());
                                isWeight = false;
                            }
                        }
                        reader.moveUp();
                    }
                    reader.moveUp();
                }
            }

            reader.moveUp();
        }

        if (oldFont == true) {
            style = getStyle(posture, weight);
        }
        return new Font(name, style, size);

    }

    private int getStyle(float posture, float weight) {
        if (posture == 0) {
            if (weight == 1) {
                return Font.PLAIN;
            } else {
                return Font.BOLD;
            }
        } else {
            if (weight == 1) {
                return Font.ITALIC;
            } else {
                return Font.BOLD | Font.ITALIC;
            }
        }
    }

}
