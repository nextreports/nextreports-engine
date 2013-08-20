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
package ro.nextreports.engine.querybuilder.sql;

import ro.nextreports.engine.querybuilder.sql.output.Output;

/**
 * @author Decebal Suiu
 */
public class GenericCriteria extends Criteria {

    private static final long serialVersionUID = -3955040991148880007L;

    private String criteria;
    
    public GenericCriteria(String criteria) {
        this.criteria = criteria;
    }
    
    public GenericCriteria() {
    }
    
    public String getCriteria() {
        return criteria;
    }
    
    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public void write(Output out) {
        out.print(criteria);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GenericCriteria that = (GenericCriteria) o;

        if (criteria != null ? !criteria.equals(that.criteria) : that.criteria != null) return false;

        return true;
    }

    public int hashCode() {
        return (criteria != null ? criteria.hashCode() : 0);
    }
}
