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

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Nov 25, 2008
 * Time: 3:38:00 PM
 */
public class ReportGroup implements Serializable {

    private static final long serialVersionUID = 8294689227478397410L;

    private String name;
    private String column;
    private boolean headerOnEveryPage;
    private boolean newPageAfter;
    
    public ReportGroup(String name, String column) {
        this(name, column, false);
    }

    public ReportGroup(String name, String column, boolean headerOnEveryPage) {
        this.name = name;
        this.column = column;
        this.headerOnEveryPage = headerOnEveryPage;
    }

    public String getName() {
        return name;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getColumn() {
        return column;
    }

    public boolean isHeaderOnEveryPage() {
        return headerOnEveryPage;
    }

    public void setHeaderOnEveryPage(boolean headerOnEveryPage) {
        this.headerOnEveryPage = headerOnEveryPage;
    }

    public boolean isNewPageAfter() {
        return newPageAfter;
    }

    public void setNewPageAfter(boolean newPageAfter) {
        this.newPageAfter = newPageAfter;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportGroup that = (ReportGroup) o;

        if (newPageAfter != that.newPageAfter) return false;
        if (headerOnEveryPage != that.headerOnEveryPage) return false;
        if (column != null ? !column.equals(that.column) : that.column != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (column != null ? column.hashCode() : 0);
        result = 31 * result + (headerOnEveryPage ? 1 : 0);
        result = 31 * result + (newPageAfter ? 1 : 0);
        return result;
    }

    public String toString() {
        return "ReportGroup{" +
                "name='" + name + '\'' +
                ", column='" + column + '\'' +
                ", headerOnEveryPage='" + headerOnEveryPage + '\'' +
                ", newPageAfter='" + newPageAfter + '\'' +
                '}';
    }
}
