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
public class JoinCriteria extends Criteria {

    private static final long serialVersionUID = 2688061568992497650L;

    private Column source;
    private Column destination;
    private String operator;
    private String joinType;

    public JoinCriteria(Column source, Column destination) {
        this(source, destination, JoinType.INNER_JOIN, Operator.EQUAL);
    }

    public JoinCriteria(Column source, Column destination, String joinType) {
        this(source, destination, joinType, Operator.EQUAL);
    }

    public JoinCriteria(Column source, Column destination, String joinType, String operator) {
        this.source = source;
        this.destination = destination;
        this.joinType = joinType;
        this.operator = operator;
    }

    public Column getSource() {
        return source;
    }

    public Column getDestination() {
        return destination;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        if (!Operator.validOperator(operator)) {
            throw new IllegalArgumentException("Invalid operator " + operator + "!");
        }
        this.operator = operator;
    }

    public String getJoinType() {
        return joinType;
    }

    public void setJoinType(String joinType) {
        if (!JoinType.validType(joinType)) {
            throw new IllegalArgumentException("Invalid join type " + joinType + "!");
        }
        this.joinType = joinType;
    }

    public void write(Output out) {
    	// outer joins are written in Table!
    	if (JoinType.isOuter(joinType)) {
    		return;
    	}    	
        out.print(source);
        out.print(" ").print(operator).print(" ");
        out.print(destination);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JoinCriteria that = (JoinCriteria) o;

        if (destination != null ? !destination.equals(that.destination) : that.destination != null) return false;
        if (joinType != null ? !joinType.equals(that.joinType) : that.joinType != null) return false;
        if (operator != null ? !operator.equals(that.operator) : that.operator != null) return false;
        if (source != null ? !source.equals(that.source) : that.source != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (source != null ? source.hashCode() : 0);
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        result = 31 * result + (operator != null ? operator.hashCode() : 0);
        result = 31 * result + (joinType != null ? joinType.hashCode() : 0);
        return result;
    }
}
