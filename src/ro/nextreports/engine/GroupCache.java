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


import java.util.List;

import ro.nextreports.engine.band.Band;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Nov 26, 2008
 * Time: 11:04:50 AM
 */
public class GroupCache {

    private ReportGroup group;
    private boolean start = true;
    private Band hgBand;
    private Band fgBand;
    private List<FunctionCache> funcCache;
    // groupRow means how many times the inner group starts
    // if there is not an inner group, means the number of detail rows from the group
    private int groupRow = 0;

    public ReportGroup getGroup() {
        return group;
    }

    public void setGroup(ReportGroup group) {
        this.group = group;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;        
    }

    public Band getHgBand() {
        return hgBand;
    }

    public boolean headerHasRows() {
        return getHgBand().getRowCount() > 0;
    }

    public boolean footerHasRows() {
        return getFgBand().getRowCount() > 0;
    }

    public void setHgBand(Band hgBand) {
        this.hgBand = hgBand;
    }

    public Band getFgBand() {
        return fgBand;
    }

    public void setFgBand(Band fgBand) {
        this.fgBand = fgBand;
    }

    public List<FunctionCache> getFuncCache() {
        return funcCache;
    }

    public void setFuncCache(List<FunctionCache> funcCache) {
        this.funcCache = funcCache;
    }

    public void incrementGroupRow()  {
        groupRow++;        
    }

    public void resetGroupRow() {
        groupRow = 0;
    }

    public int getGroupRow() {
        return groupRow;
    }

    public String toString() {
        return "GroupCache{" +
                "group=" + group +
                ", start=" + start +
                ", hgBand=" + hgBand.getName() +
                ", fgBand=" + fgBand.getName() +
                ", funcCache=" + funcCache +
                ", groupRow=" + groupRow + 
                '}';
    }
}
