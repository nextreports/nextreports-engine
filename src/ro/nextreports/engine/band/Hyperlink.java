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
package ro.nextreports.engine.band;

import java.io.Serializable;

/**
 * User: mihai.panaitescu
 * Date: 01-Mar-2010
 * Time: 12:22:58
 */
public class Hyperlink implements Serializable {

    private static final long serialVersionUID = 6198648727650548516L;

    private String text;
    private String url;
    
    public Hyperlink(String text, String url) {
        this.text = text;
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public String getUrl() {
        return url;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hyperlink hyperlink = (Hyperlink) o;

        if (text != null ? !text.equals(hyperlink.text) : hyperlink.text != null) return false;
        if (url != null ? !url.equals(hyperlink.url) : hyperlink.url != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (text != null ? text.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }


    public String toString() {
        return getText();
    }
}
