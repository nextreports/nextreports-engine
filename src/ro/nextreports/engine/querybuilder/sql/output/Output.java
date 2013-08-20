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
package ro.nextreports.engine.querybuilder.sql.output;

/**
 * The Output is where the elements of the query output their bits of SQL to.
 *
 * @author Decebal Suiu
 */
public class Output {

    private StringBuffer result = new StringBuffer();
    private StringBuffer currentIndent = new StringBuffer();
    private boolean newLineComing;

    private final String indent;

    /**
     * @param indent String to be used for indenting (e.g. "", "  ", "       ", "\t")
     */
    public Output(String indent) {
        this.indent = indent;
    }

    public String toString() {
        return result.toString();
    }

    public Output print(Object o) {
        writeNewLineIfNeeded();
        result.append(o);
        return this;
    }

    public Output print(char c) {
        writeNewLineIfNeeded();
        result.append(c);
        return this;
    }

    public Output println(Object o) {
        writeNewLineIfNeeded();
        result.append(o);
        newLineComing = true;
        return this;
    }

    public Output println() {
        newLineComing = true;
        return this;
    }

    public void indent() {
        currentIndent.append(indent);
    }

    public void unindent() {
        currentIndent.setLength(currentIndent.length() - indent.length());
    }

    private void writeNewLineIfNeeded() {
        if (newLineComing) {
            result.append('\n').append(currentIndent);
            newLineComing = false;
        }
    }

}
