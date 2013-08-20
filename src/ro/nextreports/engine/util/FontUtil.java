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

import com.lowagie.text.FontFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.HashSet;
import java.util.Arrays;
import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Feb 17, 2009
 * Time: 5:35:12 PM
 */
public class FontUtil {

    private static Log LOG = LogFactory.getLog(FontUtil.class);

    public static void registerFonts(String... dirs) {
        HashSet<String> set = new HashSet<String>();
        set.addAll(Arrays.asList(dirs));
        set.addAll(getDefaultFontDirectories());
        for (String dir : set) {
            if (new File(dir).exists()) {
                FontFactory.registerDirectory(dir);
                LOG.debug("Register font directory : " + dir);
            }
        }
    }

    private static List<String> getDefaultFontDirectories() {
       final String osname = System.getProperty("os.name");
       if (OS.isWindows()) {
           return getDefaultWindowFontDirectories();
       } else if (OS.isLinux()) {
           return getDefaultLinuxFontDirectories();
       } else {
           return new ArrayList<String>();
       }
    }

    private static List<String> getDefaultWindowFontDirectories() {
        List<String> result = new ArrayList<String>();
        final String windirs = System.getProperty("java.library.path");
        final String fs = System.getProperty("file.separator");

        if (windirs != null) {
            final StringTokenizer strtok = new StringTokenizer(windirs, System.getProperty("path.separator"));
            while (strtok.hasMoreTokens()) {
                final String token = strtok.nextToken();
                if (token.toLowerCase().endsWith("system32")) {
                    // found windows folder
                    final int lastBackslash = token.lastIndexOf(fs);
                    result.add(token.substring(0, lastBackslash) + fs + "Fonts");
                    return result;
                }
            }
        }
        return result;
    }

    private static List<String> getDefaultLinuxFontDirectories() {
        List<String> result = new ArrayList<String>();
        result.add("/usr/X11R6/lib/X11/fonts");
        result.add("/usr/share/fonts");
        return result;
    }
}
