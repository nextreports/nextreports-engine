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
package ro.nextreports.engine.querybuilder;


import javax.swing.*;

import ro.nextreports.engine.queryexec.IdName;

import java.awt.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Nov 9, 2007
 * Time: 4:17:23 PM
 */
public class IdNameRenderer extends DefaultListCellRenderer {

    public static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public Component getListCellRendererComponent(JList list, Object value,
												  int index, boolean isSelected, boolean cellHasFocus) {
		if (value != null) {
            if (!(value instanceof String)) {
				IdName in = (IdName) value;
				if (in.getName() != null) {
					value = in.getName();
				} else {
					value = in.getId();
				}                
            }

            if (value instanceof Date) {
                value = sdf.format((Date)value);
            } else if (value instanceof Time) {
                value = sdf.format(new Date(((Time)value).getTime()));
            } else if (value instanceof Timestamp)  {
                value = sdf.format(new Date(((Timestamp)value).getTime()));
            }
        }
		return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	}
}
