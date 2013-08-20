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
package ro.nextreports.engine.exporter.event;

import java.util.EventObject;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 27-Aug-2009
// Time: 13:17:23

//
public class ExporterEvent extends EventObject {

    /**
     * Constructs a prototypical Event.
     *
     * @param object The exporter object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ExporterEvent(ExporterObject object) {
        super(object);
    }

    /** Get current exporter row
     *
     * @return current exporter row
     */
    public ExporterObject getExporterObject() {
        return (ExporterObject)source;
    }
}
