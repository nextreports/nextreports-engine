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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.io.ObjectStreamException;

import ro.nextreports.engine.querybuilder.sql.dialect.Dialect;
import ro.nextreports.engine.querybuilder.sql.output.Output;
import ro.nextreports.engine.querybuilder.sql.output.Outputable;
import ro.nextreports.engine.querybuilder.sql.output.ToStringer;
import ro.nextreports.engine.querybuilder.sql.util.CollectionUtil;
import ro.nextreports.engine.util.xstream.XStreamable;


/**
 * @author Decebal Suiu
 */
public class SelectQuery implements Outputable, XStreamable {

    private static final long serialVersionUID = -2137979893531895771L;

    private static OrderIndexComparator orderIndexComparator = new OrderIndexComparator();

    private boolean distinct;
    private List<Column> columns;
    private List<Criteria> criterias;
    private List<Order> orders;
    private List<Column> groupByColumns;
    private LinkedList<LinkedList<Criteria>> orCriterias;

    private transient Dialect dialect;

    public SelectQuery() {
        columns = new ArrayList<Column>();
        criterias = new ArrayList<Criteria>();
        LinkedList<Criteria> firstOr = new LinkedList<Criteria>();
        orCriterias = new LinkedList<LinkedList<Criteria>>();
        orCriterias.add(firstOr);
        orders = new ArrayList<Order>();
        groupByColumns = new ArrayList<Column>();
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    public void addColumn(Column column, int index) {
        addColumn(column);
        moveColumn(column, index);
    }

    public void addColumn(Table table, String columname) {
        addColumn(table.getColumn(columname));
    }

    public List<Column> getColumns(Table table) {
        List<Column> result = new ArrayList<Column>();
        for (Column column : columns) {
            if (column.getTable().equals(table)) {
                result.add(column);
            }
        }

        return result;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public List<ExpressionColumn> getExpressionColumn() {
        List<ExpressionColumn> list = new ArrayList<ExpressionColumn>();
        for (Column column : columns) {
            if (column instanceof ExpressionColumn) {
                list.add((ExpressionColumn) column);
            }
        }

        return list;
    }

    public boolean removeColumn(Column column) {
        return columns.remove(column);
    }

    public boolean removeColumnAndDependencies(Column column) {
        removeMatchCriteria(column);
        removeOrMatchCriteria(column, 0);
        removeGroupByColumn(column);
        removeOrder(column);
        return columns.remove(column);
    }

    public void changeColumn(Column oldColumn, Column newColumn) {
        CollectionUtil.changeItem(columns, oldColumn, newColumn);
    }

    public void moveColumn(Column oldColumn, int newIndex) {
        CollectionUtil.moveItem(columns, oldColumn, newIndex);
    }

    public int getColumnIndex(Column column) {
        return columns.indexOf(column);
    }

    public List listColumns() {
        return Collections.unmodifiableList(columns);
    }

    public int getColumnsCount() {
        return columns.size();
    }

    public void addCriteria(Criteria criteria) {
        this.criterias.add(criteria);
    }

    public MatchCriteria getMatchCriteria(Column column) {
        for (Criteria criteria : criterias) {
            if (criteria instanceof MatchCriteria) {
                MatchCriteria mc = (MatchCriteria) criteria;
                if (mc.getColumn().equals(column)) {
                    return mc;
                }
            }
        }

        return null;
    }

    public List<MatchCriteria> getParameterMatchCriterias() {
        List<MatchCriteria> result = new ArrayList<MatchCriteria>();
        for (Criteria criteria : criterias) {
            if (criteria instanceof MatchCriteria) {
                MatchCriteria mc = (MatchCriteria) criteria;
                if (mc.isParameter()) {
                    result.add(mc);
                }
            }
        }

        return result;
    }

    public void updateParameterMatchCriterias(List<MatchCriteria> pmcList) {    	
        for (MatchCriteria mc : pmcList) {
            MatchCriteria oldmc = getMatchCriteria(mc.getColumn());            
            if ((oldmc != null) && mc.isParameter()) {            	
                oldmc.setParameter(true);
            }
            if ((oldmc != null) && mc.isParameter2()) {
                oldmc.setParameter2(true);
            }
        }
    }

    public void removeMatchCriterias(String tableAlias) {
        for (Column column : columns) {
            Table table = column.getTable();
            if ((table != null) && table.getAlias().equals(tableAlias)) {
                removeMatchCriteria(column);
            }
        }
    }

    public void removeMatchCriteria(Column column) {
        MatchCriteria criteria = getMatchCriteria(column);
        if (criteria != null) {
            removeCriteria(criteria);
        }
    }


    public boolean removeCriteria(Criteria criteria) {
        return criterias.remove(criteria);
    }


    public LinkedList<Criteria> getOrList(int position) {
        LinkedList<Criteria> or;
        if (orCriterias.size() == 0) {
            or = new LinkedList<Criteria>();
            orCriterias.add(or);
            return or;
        }
        or = orCriterias.get(position);
        if (or == null) {
            or = new LinkedList<Criteria>();
            orCriterias.add(position, or);
        }
        return or;
    }

    public void addOrCriteria(Criteria criteria, int position) {
        LinkedList<Criteria> or = getOrList(position);
        or.add(criteria);
    }

    public MatchCriteria getOrMatchCriteria(Column column, int position) {
        LinkedList<Criteria> or = getOrList(position);
        for (Criteria criteria : or) {
            if (criteria instanceof MatchCriteria) {
                MatchCriteria mc = (MatchCriteria) criteria;
                if (mc.getColumn().equals(column)) {
                    return mc;
                }
            }
        }
        return null;
    }

    public List<MatchCriteria> getOrParameterMatchCriterias(int position) {
        LinkedList<Criteria> or = getOrList(position);
        List<MatchCriteria> result = new ArrayList<MatchCriteria>();
        for (Criteria criteria : or) {
            if (criteria instanceof MatchCriteria) {
                MatchCriteria mc = (MatchCriteria) criteria;
                if (mc.isParameter()) {
                    result.add(mc);
                }
            }
        }
        return result;
    }

    public void updateOrParameterMatchCriterias(List<MatchCriteria> pmcList, int position) {
        for (MatchCriteria mc : pmcList) {
            MatchCriteria oldmc = getOrMatchCriteria(mc.getColumn(), position);
            if ((oldmc != null) && mc.isParameter()) {
                oldmc.setParameter(true);
            }
            if ((oldmc != null) && mc.isParameter2()) {
                oldmc.setParameter2(true);
            }
        }
    }

    public void removeOrMatchCriterias(String tableAlias, int position) {
        for (Column column : columns) {
            Table table = column.getTable();
            if ((table != null) && table.getAlias().equals(tableAlias)) {
                removeOrMatchCriteria(column, position);
            }
        }
    }

    public void removeOrMatchCriteria(Column column, int position) {
        MatchCriteria criteria = getOrMatchCriteria(column, position);
        if (criteria != null) {
            removeOrCriteria(criteria, position);
        }
    }


    public boolean removeOrCriteria(Criteria criteria, int position) {
        LinkedList<Criteria> or = getOrList(position);
        return or.remove(criteria);
    }


    public void clear() {
        columns.clear();
        criterias.clear();
        orCriterias.clear();
        orders.clear();
        groupByColumns.clear();
        distinct = false;
    }

    public boolean containsCriteria(Criteria criteria) {
        return criterias.contains(criteria);
    }

    public void changeCriteria(Criteria oldCriteria, Criteria newCriteria) {
        CollectionUtil.changeItem(criterias, oldCriteria, newCriteria);
    }

    public List listCriterias() {
        return Collections.unmodifiableList(criterias);
    }

    public int getCriteriasCount() {
        return criterias.size();
    }

    public List<JoinCriteria> getInnerJoins() {
        List<JoinCriteria> joins = new ArrayList<JoinCriteria>();
        for (Criteria criteria : criterias) {
            if (criteria instanceof JoinCriteria) {
            	JoinCriteria jc = (JoinCriteria) criteria;
            	if (!JoinType.isOuter(jc.getJoinType())) {
            		joins.add(jc);
            	}
            }
        }
        return joins;
    }
    
    public List<JoinCriteria> getJoins() {
        List<JoinCriteria> joins = new ArrayList<JoinCriteria>();
        for (Criteria criteria : criterias) {
            if (criteria instanceof JoinCriteria) {
                joins.add((JoinCriteria) criteria);
            }
        }
        return joins;
    }

    public List<Criteria> getNotJoins() {
        List<Criteria> list = new ArrayList<Criteria>();
        for (Criteria criteria : criterias) {
            if (!(criteria instanceof JoinCriteria)) {
                list.add(criteria);
            }
        }

        return list;
    }

    public void addJoin(JoinCriteria join) {
        addCriteria(join);
    }

    public JoinCriteria addJoin(Column srcColumn, Column destColumn) {
        JoinCriteria jc = new JoinCriteria(srcColumn, destColumn);
        addCriteria(jc);
        return jc;
    }

    public void addJoin(Table srcTable, String srcColumnName, Table destTable,
                        String destColumnname) {
        addCriteria(new JoinCriteria(srcTable.getColumn(srcColumnName),
                destTable.getColumn(destColumnname)));
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public void addOrder(Column column, boolean ascending) {
        addOrder(new Order(column, ascending));
    }

    public void addOrder(Table table, String columnName, boolean ascending) {
        addOrder(new Order(table.getColumn(columnName), ascending));
    }

    public boolean removeOrder(Order order) {
        return this.orders.remove(order);
    }

    public boolean removeOrder(Column column) {
        return this.orders.remove(getOrder(column));
    }

    public List<Order> listOrders() {
        return Collections.unmodifiableList(orders);
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public int getOrdersCount() {
        return orders.size();
    }

    public Order getOrder(Column column) {
        for (Order order : orders) {
            if (order.getColumn().equals(column)) {
                return order;
            }
        }

        return null;
    }

    public void addGroupByColumn(Column column) {
        this.groupByColumns.add(column);
    }

    public void addGroupByColumn(Column column, int index) {
        int size  = groupByColumns.size();
        // in column has output=false groupby columns may be not successive (and we must add nulls till
        // the current index)
        if (index > size) {
           for (int i=size; i<index; i++) {
               groupByColumns.add(i, null);
           }
        }
        this.groupByColumns.add(index, column);
    }

    public boolean removeGroupByColumn(Column column) {
        int index = getGroupByColumnIndex(column);
        if (index != -1) {
            groupByColumns.set(index, null);
        }
        return true;
    }

    private List<Column> getNotNullGroupByColumns() {
        List<Column> list = new ArrayList<Column>();
        for (Column col : groupByColumns) {
            if (col != null) {
                list.add(col);
            }
        }
        return list;
    }

    private int getGroupByColumnIndex(Column column) {
        if (column instanceof GroupByFunctionColumn) {
            GroupByFunctionColumn fc = (GroupByFunctionColumn) column;
            for (int i = 0, size = groupByColumns.size(); i < size; i++) {
                Column col = groupByColumns.get(i);
                Table table = null;
                if (col != null) {
                    table = col.getTable();
                }
                // table can be null for an ExpressionColumn
                if (table == null) {
                    if ((col != null) && col.getName().equals(fc.getName())) {
                        return i;
                    }
                } else {
                    if ((col != null) && table.equals(fc.getTable()) && col.getName().equals(fc.getName())) {
                        return i;
                    }
                }
            }
        } else {
            for (int i = 0, size = groupByColumns.size(); i < size; i++) {
                Column col = groupByColumns.get(i);
                if (col != null) {
                    if (col.getTable() == null) {
                        if ((col != null) && col.getName().equals(column.getName())) {
                            return i;
                        }
                    } else {
                        if ((col != null) && col.getTable().equals(column.getTable()) && col.getName().equals(column.getName())) {
                            return i;
                        }
                    }
                }
            }
        }
        return -1;
    }

    public List<Column> getGroupByColumns() {
        return groupByColumns;
    }

    public boolean hasNotNullGroupByColumn() {
        for (Column col : groupByColumns) {
            if (col != null) {
                return true;
            }
        }
        return false;
    }

    public void setGroupByColumns(List<Column> groupByColumns) {
        this.groupByColumns = groupByColumns;
    }

    public void removeGroupByColumns(String tableAlias) {
        for (Column column : columns) {
            Table table = column.getTable();
            if ((table != null) && table.getAlias().equals(tableAlias)) {
                int index = getGroupByColumnIndex(column);
                if (index != -1) {
                    this.groupByColumns.set(index, null);
                }
            }
        }
    }

    public boolean containsGroupByColumn(Column column) {
        return this.groupByColumns.contains(column);
    }

    public void removeAllGroupByColumns() {
        this.groupByColumns.clear();
    }

    public String toString() {
        return ToStringer.toString(this);
    }

    public void write(Output out) {
        if (this.columns.size() == 0) {
            return;
        }

        if (this.distinct) {
            out.println("SELECT DISTINCT");
        } else {
            out.println("SELECT");
        }

        // add columns to select
        out.indent();
        appendList(out, getOutputColumns(columns), ",", true);
        out.println();
        out.unindent();

        // add tables to select from
        out.println("FROM");

        // determine all tables used in query
        out.indent();
        appendList(out, findAllUsedTablesInFrom(), ",", false);
        out.println();
        out.unindent();

        // add criterias : 'joins', 'not joins', 'or criterias'
        int orSize = 0;
        if (this.orCriterias != null) {
            orSize = this.orCriterias.get(0).size();
        }
        
        // inner joins are written in WHERE clause
        // outer joins are written in Table class
        List<JoinCriteria> innerJoins = getInnerJoins();
        List<Criteria> notJoins = getNotJoins();
        if (innerJoins.size() > 0) {
            out.println("WHERE");
            out.indent();

            appendList(out, innerJoins, " AND ", false);

            if (notJoins.size() > 0) {
                if (innerJoins.size() > 0) {
                    out.print(" AND ");
                    if (orSize > 0) {
                        out.println("(");
                        out.indent();
                    } else {
                        out.println();
                    }
                }
                if (orSize > 0) {
                    out.print("(");
                }
                appendList(out, notJoins, " AND ", false);
                if (orSize > 0) {
                    out.println(")");
                } else {
                    out.unindent();
                }
            } else {
                out.println();
            }
        } else if (notJoins.size() > 0) {
        	 out.println("WHERE");
             out.indent();
             if (orSize > 0) {
                 out.print("(");
             }
             appendList(out, notJoins, " AND ", false);
             if (orSize > 0) {
                 out.println(")");
             } else {
                 out.unindent();
             }
        }

        if (orSize > 0) {
            // no criterias : must add WHERE clause
            if (this.criterias.size() == 0) {
                out.println("WHERE");
                out.indent();
            }
            if (notJoins.size() > 0) {
                out.println(" OR ");
                out.print("(");
            } else if (innerJoins.size() > 0) {
                out.println(" AND ");
                out.print("(");
            }
            appendList(out, this.orCriterias.get(0), " AND ", false);
            if ((notJoins.size() > 0) || (innerJoins.size() > 0)) {
                out.println(")");
            }
            if (this.criterias.size() == 0) {
                out.unindent();
            }
        }

        if (notJoins.size() > 0) {
            if (innerJoins.size() > 0) {
                if (orSize > 0) {
                    out.unindent();
                    out.println(")");
                } else {
                    out.println();
                }
            } else {
                out.println();
            }
        }

        if ((this.criterias.size() > 0) && (orSize > 0)) {
            out.unindent();
        }

        // group by
        if (this.groupByColumns.size() > 0) {
            out.println("GROUP BY");
            out.indent();
            appendList(out, getNotNullGroupByColumns(), ",", false);
            out.println();
            out.unindent();
        }

        // add orders
        if (this.orders.size() > 0) {
            out.println("ORDER BY");
            out.indent();
            appendList(out, sortOrders(orders), ",", false);
            out.println();
            out.unindent();
        }
    }

    /**
     * Iterate through a Collection and append all entries (using .toString()) to
     * a StringBuffer.
     */
    private void appendList(Output out, Collection collection, String separator, boolean areColumns) {
        Iterator it = collection.iterator();
        boolean hasNext = it.hasNext();
        //boolean areColumns = (columns == collection);

        while (hasNext) {
            Outputable sqlToken = (Outputable) it.next();
            hasNext = it.hasNext();
            sqlToken.write(out);
            if (areColumns) {
                Column column = (Column) sqlToken;
                String columnAlias = column.getAlias();
                if (columnAlias != null) {
                    out.print(" AS ");
                    out.print("\"");
                    out.print(columnAlias);
                    out.print("\"");
                }
            }
            if (hasNext) {
                out.print(separator);
                out.println();
            }
        }
    }

    private List<Column> getOutputColumns(List<Column> columns) {
        List<Column> result = new ArrayList<Column>();
        for (Column column : columns) {
            if (column.isOutput()) {
                result.add(column);
            }
        }

        return result;
    }

    /**
     * Find all the tables used in the query in FROM clause (from columns, criterias and orders).
     *
     * @return List of {@link Table}s
     */
    private List<Table> findAllUsedTablesInFrom() {
        List<Table> allTables = new ArrayList<Table>();
        Map<Table, List<JoinCriteria>> sourceMap = new HashMap<Table, List<JoinCriteria>>();
        Map<Table, List<JoinCriteria>> destMap = new HashMap<Table, List<JoinCriteria>>();
        
        // see what tables are used in outer joins        
        for (Object criteria : criterias) {
            try {
                JoinCriteria joinCriteria = (JoinCriteria) criteria;
                Table sourceTable = joinCriteria.getSource().getTable();
                Table destTable = joinCriteria.getDestination().getTable();
                List<JoinCriteria> sourceList = sourceMap.get(sourceTable);
                List<JoinCriteria> destList = sourceMap.get(destTable);
                if (sourceList == null) {
                	sourceList = new ArrayList<JoinCriteria>();
                	sourceMap.put(sourceTable, sourceList);
                }
                sourceList.add(joinCriteria);        
                if (destList == null) {
                	destList = new ArrayList<JoinCriteria>();
                	destMap.put(destTable, destList);
                }
                destList.add(joinCriteria);        
            } catch (ClassCastException ex) {
                // not a JoinCriteria
            }
        }    
        
        // add tables from column selection
        for (Column column : columns) {
            Table table = column.getTable();
            if (table != null) {
                table.setDialect(dialect);
            }
            if (canAddTableToFromClause(table, destMap, allTables)) {
            	table.setJoins(sourceMap.get(table));
        		allTables.add(table);
        	}    
        }        
        
        // add tables from criterias 
        // it is possible that a table is used only in joins without any column selection from it
        for (Object criteria : criterias) {
            try {            	
                JoinCriteria joinCriteria = (JoinCriteria) criteria;
                Table sourceTable = joinCriteria.getSource().getTable();
                Table destTable = joinCriteria.getDestination().getTable();
                if (canAddTableToFromClause(sourceTable, destMap, allTables)) {
                	sourceTable.setJoins(sourceMap.get(sourceTable));
            		allTables.add(sourceTable);
            	}                
                if (canAddTableToFromClause(destTable, destMap, allTables)) {
                	destTable.setJoins(destMap.get(destTable));
                    allTables.add(destTable);
                }                                 
            } catch (ClassCastException ex) {
                // not a JoinCriteria
            }
        }

        // add tables used by order columns
        for (Order order : orders) {
            Table table = order.getColumn().getTable();
            if (table != null) {
                table.setDialect(dialect);
            }
            if (canAddTableToFromClause(table, destMap, allTables)) {
                allTables.add(table);
            }
        }

        return allTables;
    }
    
    // if a table is used in destination of an outer join, it must not be added in the FROM clause
    // outer joins are written in Table class
    private boolean canAddTableToFromClause(Table table, Map<Table, List<JoinCriteria>> destMap, List<Table> allTables) {
    	if ((table == null) || allTables.contains(table)) {
    		return false;
    	}
    	if ((destMap.get(table) != null) && (destMap.get(table).size() > 0)) {
    		List<JoinCriteria> list = destMap.get(table);    		
    		for (JoinCriteria jc : list) {
    			if (JoinType.isOuter(jc.getJoinType())) {
    				return false;
    			}
    		}
    	}    	
    	return true;
    }

    @SuppressWarnings("unchecked")
    private List<Order> sortOrders(List<Order> orders) {
        List<Order> sortedOrders = new ArrayList<Order>(orders);
        Collections.sort(sortedOrders, orderIndexComparator);
        return sortedOrders;
    }

    /**
     * Compare orders by index.
     *
     * @author Decebal Suiu
     */
    static class OrderIndexComparator implements Comparator {

        public int compare(Object objectA, Object objectB) {
            Order orderA = (Order) objectA;
            Order orderB = (Order) objectB;
            int indexA = orderA.getIndex();
            int indexB = orderB.getIndex();
            if (indexA < indexB) {
                return -1;
            } else if (indexA > indexB) {
                return 1;
            } else {
                return 0;
            }
        }

    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SelectQuery that = (SelectQuery) o;

        if (distinct != that.distinct) return false;
        if (columns != null ? !columns.equals(that.columns) : that.columns != null) return false;
        if (criterias != null && that.criterias != null && (!criterias.containsAll(that.criterias) ||
                !that.criterias.containsAll(criterias))) return false;
        if (groupByColumns != null ? !groupByColumns.equals(that.groupByColumns) : that.groupByColumns != null)
            return false;
        if (orCriterias != null ? !orCriterias.equals(that.orCriterias) : that.orCriterias != null) return false;        
        if (orders != null ? !orders.equals(that.orders) : that.orders != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (distinct ? 1 : 0);
        result = 31 * result + (columns != null ? columns.hashCode() : 0);
        result = 31 * result + (criterias != null ? criterias.hashCode() : 0);
        result = 31 * result + (orders != null ? orders.hashCode() : 0);
        result = 31 * result + (groupByColumns != null ? groupByColumns.hashCode() : 0);
        result = 31 * result + (orCriterias != null ? orCriterias.hashCode() : 0);
        return result;
    }

    private Object readResolve() throws ObjectStreamException {
        // Read/initialize additional fields
        if (orCriterias == null) {
            LinkedList<Criteria> firstOr = new LinkedList<Criteria>();
            orCriterias = new LinkedList<LinkedList<Criteria>>();
            orCriterias.add(firstOr);
        }
        if (groupByColumns == null) {
            groupByColumns = new ArrayList<Column>();
        }
        return this;
    }
}
