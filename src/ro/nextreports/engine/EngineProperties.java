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
package ro.nextreports.engine;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 27-Aug-2009
// Time: 10:32:41

//
public class EngineProperties {

    //@todo this is used only in nextreports designer now (remove from here)
    /** Thread priority for running next reports queries and exporters */
    public static final String RUN_PRIORITY_PROPERTY = "nextreports.run.priority";

    /** Number of query records after the exporter waits a little to degrevate the processor */
    public static final String RECORDS_YIELD_PROPERTY = "nextreports.records.yield";

    /** Number of milliseconds the exporter will wait after RECORDS_YIELD are exported */
    public static final String MILLIS_YIELD_PROPERTY = "nextreports.millis.yield";

    /** Default number of milliseconds the exporter will wait after RECORDS_YIELD are exported */
    public static int DEFAULT_MILLIS_YIELD = 100;

    /** Get priority for running next reports queries and exporters
     *
     * @return priority for running next reports queries and exporters
     */
    public static int getRunPriority() {
        String s = System.getProperty(RUN_PRIORITY_PROPERTY);
        int priority = Thread.NORM_PRIORITY;
        if (s != null) {
            try {
                priority  = Integer.parseInt(s);
            } catch (NumberFormatException ex) {
                // priority remains Thread.NORM_PRIORITY
            }
        }
        return priority;
    }

    /** Get number of query records after the exporter waits a little to degrevate the processor
     *
     * @return number of query records after the exporter waits a little to degrevate the processor
     */
    public static int getRecordsYield() {
        String s = System.getProperty(RECORDS_YIELD_PROPERTY);
        int records = Integer.MAX_VALUE;
        if (s != null) {
            try {
                records  = Integer.parseInt(s);
            } catch (NumberFormatException ex) {
                // records remains Integer.MAX_VALUE
            }
        }
        return records;
    }

    /** Get number of milliseconds the exporter will wait after RECORDS_YIELD are exported
     *
     * @return number of milliseconds the exporter will wait after RECORDS_YIELD are exported
     */
    public static int getMillisYield() {
        String s = System.getProperty(MILLIS_YIELD_PROPERTY);
        int millis = DEFAULT_MILLIS_YIELD;
        if (s != null) {
            try {
                millis  = Integer.parseInt(s);
            } catch (NumberFormatException ex) {
                // records remains DEFAULT_MILLIS_YIELD
            }
        }
        return millis;
    }
}
