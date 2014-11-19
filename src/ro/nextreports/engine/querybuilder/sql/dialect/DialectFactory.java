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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A factory for generating Dialect instances.
 * 
 * @author Decebal Suiu
 * @author Mihai Dinca-Panaitescu
 */
public class DialectFactory {
	
	private static final String DATABASE = "next.dialect.database";
	private static final String DIALECT = "next.dialect.class";
	
	private static Log LOG = LogFactory.getLog(DialectFactory.class);

    private static final Map<String,DialectMapper> MAPPERS = new HashMap<String,DialectMapper>();

    public static final String ORACLE = "Oracle";
    public static final String SYBASE9 = "Adaptive Server Anywhere";
    public static final String MSSQL = "Microsoft SQL Server";
    public static final String MySQL = "MySQL";
    public static final String DERBY = "Apache Derby";
    public static final String POSTGRES = "PostgreSQL";
    public static final String FIREBIRD = "Firebird";
    public static final String SQLITE = "SQLite";
    public static final String CSV = "CsvJdbc";
    public static final String VERTICA = "Vertica Database";
    public static final String PERVASIVE = "Pervasive.SQL";
    public static final String TERADATA = "Teradata";
    public static final String MSACCESS = "Ucanaccess";

    static {
        // add buit-in dialect mappers
        addDialect(ORACLE, OracleDialect.class.getName());
        addDialect(SYBASE9, MSSQLDialect.class.getName());
        addDialect(MSSQL, MSSQLDialect.class.getName());
        addDialect(MySQL, MySQLDialect.class.getName());
        addDialect(DERBY, DerbyDialect.class.getName());
        addDialect(POSTGRES, PostrgreSQLDialect.class.getName());
        addDialect(SQLITE, SQLiteDialect.class.getName());
        addDialect(CSV, CSVDialect.class.getName());
        addDialect(VERTICA, VerticaDialect.class.getName());
        addDialect(PERVASIVE, PervasiveDialect.class.getName());
        addDialect(TERADATA, TeradataDialect.class.getName());
        addDialect(MSACCESS, MSAccessDialect.class.getName()); 
        addDialectsFromVMParameters();
    }
    
    /**
     * Determine the appropriate Dialect to use given the database product name
     * and major version.
     *
     * @param databaseName The name of the database product (obtained from metadata).
     * @param databaseMajorVersion The major version of the database product (obtained from metadata).
     *
     * @return An appropriate dialect instance.
     * @throws DialectException 
     */
    public static Dialect determineDialect(String databaseName, String databaseMajorVersion) 
            throws DialectException {
        if (databaseName == null) {
            throw new DialectException("Dialect must be explicitly set");
        }        
        String dialectName;
        
        // TODO workaround for firebird (databaseName='Firebird 2.0.LI-V2.0.3.12981 Firebird 2.0/tcp (decebal)/P10')
        if (databaseName.startsWith(FIREBIRD)) {
        	dialectName = FirebirdDialect.class.getName();
        } else if (databaseName.startsWith(MSACCESS)) {
        	dialectName = MSAccessDialect.class.getName();	
        } else {        	
        	DialectMapper mapper = MAPPERS.get(databaseName);        	
        	if (mapper == null) {
        		throw new DialectException( "Dialect must be explicitly set for database: " + databaseName );
        	}
        	dialectName = mapper.getDialectClass(databaseMajorVersion);
        }
                
        return buildDialect(dialectName);
    }
    
    /**
     * Returns a dialect instance given the name of the class to use.
     *
     * @param dialectName The name of the dialect class.
     *
     * @return The dialect instance.
     * @throws DialectException 
     */
    public static Dialect buildDialect(String dialectName) throws DialectException {
        try {
            return (Dialect) loadDialect(dialectName).newInstance();
        } catch (ClassNotFoundException e) {
            throw new DialectException("Dialect class not found: " + dialectName);
        } catch (Exception e) {
            throw new DialectException("Could not instantiate dialect class", e);
        }
    }
    
    private static Class loadDialect(String dialectName) throws ClassNotFoundException {
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                return contextClassLoader.loadClass(dialectName);
            } else {
                return Class.forName(dialectName);
            }
        } catch (Exception e) {
            return Class.forName(dialectName);
        }
    }
    
    /**
     * For a given database product name, instances of DialectMapper know 
     * which Dialect to use for different versions.
     */
    public static interface DialectMapper {
        
        public String getDialectClass(String majorVersion);
        
    }
    
    /**
     * A simple DialectMapper for dialects which are independent
     * of the underlying database product version.
     */
     public static class VersionInsensitiveMapper implements DialectMapper {
         
         private String dialectClassName;
         
         public VersionInsensitiveMapper(String dialectClassName) {
             this.dialectClassName = dialectClassName;
         }
         
         public String getDialectClass(String majorVersion) {
             return dialectClassName;
         }
         
     }

    /**
     * Add dialect
     * @param dialectName dialect name
     * @param dialectClass dialect class
     */
    public static void addDialect(String dialectName, String dialectClass) {
        MAPPERS.put(dialectName, new VersionInsensitiveMapper(dialectClass));
        LOG.info("Dialect added: " + dialectName + " = " + dialectClass);        
    }
    
    // add dialects specified as VM parameters
    //
    // -Dnext.dialect.database_1="Metadata database name" (DataBaseMetaData.getDatabaseProductName())
    // -Dnext.dialect.class_1="com.my.MyDialect"
    // With _<n> we can specify any number of dialects 
    private static void addDialectsFromVMParameters() {
    	Properties properties = System.getProperties();
    	for (Object key : properties.keySet()) {
    		if (key instanceof String) {
    			String name = (String) key;
    			if (name.startsWith(DATABASE)) {    				
    				String databaseName = properties.getProperty(name);    				
    				int index = name.indexOf("_");
    				if (index > 0) {
    					String suffix = name.substring(index);
    					String dialectClass = properties.getProperty(DIALECT + suffix);    					
    					if (dialectClass != null) {
    						addDialect(databaseName, dialectClass);
    					}
    				}    				
    			}
    		}
    	}
    }
     
}
