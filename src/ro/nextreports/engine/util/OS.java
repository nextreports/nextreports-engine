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

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Feb 17, 2009
 * Time: 5:25:33 PM
 */
public class OS {

    private static final String osName;

    static {
        osName = System.getProperty("os.name");
    }

    /**
     * Returns true if the current operating system is
     * Microsoft Windows.
     */
    public static boolean isWindows() {
        return osName.startsWith("Windows");
    }

    /**
     * Returns true if the current operating system is
     * Linux.
     */
    public static boolean isLinux() {
        return osName.startsWith("Linux");
    }

    /**
     * Returns true if the current operating system is
     * Sun Microsystem's Solaris.
     */
    public static boolean isSolaris() {
        return osName.startsWith("Solaris");
    }

    /**
     * Returns true if the current operating system is
     * either Linux or Solaris. Need to add support of other OSes
     */
    public static boolean isUnix() {
        return isLinux() || isSolaris();
    }

}
