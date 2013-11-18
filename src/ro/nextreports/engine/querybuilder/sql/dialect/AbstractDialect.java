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
package ro.nextreports.engine.querybuilder.sql.dialect;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.sql.Timestamp;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Represents a dialect of SQL implemented by a particular RDBMS.
 * <br>
 * Subclasses should provide a public default constructor that <tt>register()</tt>
 * a set of type mappings and.<br>
 * <br>
 * 
 * @author Decebal Suiu
 */
public abstract class AbstractDialect implements Dialect {
	
	protected static final Log LOG = LogFactory.getLog(AbstractDialect.class);

    private List<ColumnTypeMatcher> columnTypeMatchers = new ArrayList<ColumnTypeMatcher>();
//    private Map<String, Integer> jdbcTypes = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
    private Map<String, Integer> jdbcTypes = new HashMap<String, Integer>();
    private Map<Integer, String> javaTypes = new HashMap<Integer, String>();

    // uppercase keywords : an array of known keywords that are allowed to be used as
    // table or column names
    protected String[] keywords = new String[0];
        
    public AbstractDialect() {
    	LOG.info("Using dialect: " + this);
        setKeywords();
        registerDefaultJavaTypes();
    }
    
    /**
     * Get <tt>java.sql.Types</tt> typecode of the column database associated 
     * with the given sql type, precision and scale.
     * 
     * @param type      sql type
     * @param precision the precision of the column
     * @param scale the scale of the column
     *
     * @return the column typecode
     * @throws DialectException
     */
    public final int getJdbcType(String type, int precision, int scale)  throws DialectException {
    	// TODO ?!
    	// SQLite returns "null" for type if we use rsmd.getColumnTypeName
    	if ((type == null) || "null".equals(type))  {
    		return Types.OTHER;
    	}
    	
    	List<ColumnTypeMatcher> typeMatchers = matchType(type, columnTypeMatchers);
    	if (typeMatchers.size() == 0) {
    		throw new DialectException("Cannot match the type '" + type + "' to a jdbc type");
    	}
    	if (typeMatchers.size() == 1) {
    		return jdbcTypes.get(typeMatchers.get(0).getColumnType());
    	}
    	
    	List<ColumnTypeMatcher> precisionMatchers = matchPrecision(precision, typeMatchers);
    	if (precisionMatchers.size() == 0) {
    		throw new DialectException("Cannot match the precision '" + precision + "' to a jdbc type");
    	}
    	if (precisionMatchers.size() == 1) {
    		return jdbcTypes.get(precisionMatchers.get(0).getColumnType());
    	}

    	List<ColumnTypeMatcher> scaleMatchers = matchScale(scale, typeMatchers);
    	if (scaleMatchers.size() == 0) {
    		throw new DialectException("Cannot match the scale '" + scale + "' to a jdbc type");
    	}
    	if (scaleMatchers.size() == 1) {
    		return jdbcTypes.get(scaleMatchers.get(0).getColumnType());
    	}
    	
    	return jdbcTypes.get(chooseOne(scaleMatchers).getColumnType());
    }

    public final String getJavaType(String type, int precision, int scale)  throws DialectException {
    	return javaTypes.get(getJdbcType(type, precision, scale));
    }

    /**
     * Subclasses register a typename for the given type code.
     *
     * @param columnType the database column type
     * @param jdbcType <tt>java.sql.Types</tt> typecode
     */    
    protected void registerColumnType(String columnType, int jdbcType) {
        columnTypeMatchers.add(new ColumnTypeMatcher(columnType));
        jdbcTypes.put(columnType, jdbcType);
    }
    
    public boolean hasProcedureWithCursor() {
        return false;
    }

    public boolean schemaBeforeCatalog() {
        return true;
    }
    
    protected void registerDefaultJavaTypes() {
    	registerJavaType(Types.BIT, Boolean.class.getName());
    	registerJavaType(Types.TINYINT, Byte.class.getName());
    	registerJavaType(Types.SMALLINT, Short.class.getName());
//    	registerJavaType(Types.CHAR, Character.class.getName());
    	registerJavaType(Types.CHAR, String.class.getName());
    	registerJavaType(Types.VARCHAR, String.class.getName());
    	registerJavaType(Types.DATE, Date.class.getName());
    	registerJavaType(Types.TIME, Time.class.getName());
    	registerJavaType(Types.TIMESTAMP, Timestamp.class.getName());
    	registerJavaType(Types.DOUBLE, Double.class.getName());
    	registerJavaType(Types.FLOAT, Float.class.getName());
    	registerJavaType(Types.INTEGER, Integer.class.getName());    	
    	registerJavaType(Types.BIGINT, BigInteger.class.getName());
//    	registerJavaType(Types.BIGINT, Long.class.getName());    	
    	registerJavaType(Types.NUMERIC, BigDecimal.class.getName());
    	registerJavaType(Types.DECIMAL, BigDecimal.class.getName());
    	registerJavaType(Types.BINARY, byte[].class.getName());
    	registerJavaType(Types.VARBINARY, byte[].class.getName());

    	registerJavaType(Types.BLOB, String.class.getName());
    	registerJavaType(Types.CLOB, String.class.getName());
    	registerJavaType(Types.REAL, String.class.getName());    	
    	registerJavaType(Types.OTHER, Object.class.getName());
    }
    
    protected void registerJavaType(int jdbcType, String javaType) {
    	javaTypes.put(jdbcType, javaType);
    }
    
    protected List<ColumnTypeMatcher> matchType(String type, List<ColumnTypeMatcher> columnTypeMatchers) {
    	List<ColumnTypeMatcher> typeMatchers = new ArrayList<ColumnTypeMatcher>();
    	for (ColumnTypeMatcher matcher : columnTypeMatchers) {
    		if (matcher.matchType(type)) {
    			typeMatchers.add(matcher);
    		}
    	}
    	
    	return typeMatchers;
	}

    protected List<ColumnTypeMatcher> matchPrecision(int precision, List<ColumnTypeMatcher> columnTypeMatchers) {
    	List<ColumnTypeMatcher> precisionMatchers = new ArrayList<ColumnTypeMatcher>();
    	for (ColumnTypeMatcher matcher : columnTypeMatchers) {
    		if (matcher.matchPrecision(precision)) {
    			precisionMatchers.add(matcher);
    		}
    	}
    	
    	return precisionMatchers;
	}

    protected List<ColumnTypeMatcher> matchScale(int scale, List<ColumnTypeMatcher> columnTypeMatchers) {
    	List<ColumnTypeMatcher> scaleMatchers = new ArrayList<ColumnTypeMatcher>();
    	for (ColumnTypeMatcher matcher : columnTypeMatchers) {
    		if (matcher.matchScale(scale)) {
    			scaleMatchers.add(matcher);
    		}
    	}
    	
    	return scaleMatchers;
	}
    
    protected ColumnTypeMatcher chooseOne(List<ColumnTypeMatcher> columnTypeMatchers) {
    	return columnTypeMatchers.get(0);
    }

    public boolean isKeyWord(String word) {
        if (word == null) {
            return false;
        }
        String wordUpper = word.toUpperCase();
        for (String keyword : keywords) {
            if (keyword.equals(wordUpper)) {
                return true;
            }
        }
        return false;
    }

    protected void setKeywords() {}

    public String getEscapedKeyWord(String keyword) {
        return keyword;
    }
    
    public String getCurrentTimestamp() throws DialectException {
    	return getCurrentDate();
    }
    
    public String getCurrentTime() throws DialectException {
    	return getCurrentDate();
    }
    
    public boolean needsHoldCursorsForPreparedStatement() {
    	return false;
    }
}
