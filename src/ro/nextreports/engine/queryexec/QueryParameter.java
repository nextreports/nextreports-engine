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
package ro.nextreports.engine.queryexec;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ro.nextreports.engine.util.StringUtil;


/** Parameter defined for report
 *
 * @author Decebal Suiu
 */
public class QueryParameter implements Serializable {

    private static final long serialVersionUID = -4076375031406410286L;

    /** Value for Integer java class */
    public static final String INTEGER_VALUE = "java.lang.Integer";
    /** Value for String java class */
    public static final String STRING_VALUE = "java.lang.String";
    /** Value for Boolean java class */
    public static final String BOOLEAN_VALUE = "java.lang.Boolean";
    /** Value for Byte java class */
    public static final String BYTE_VALUE = "java.lang.Byte";
    /** Value for Date java class */
    public static final String DATE_VALUE = "java.util.Date";
    /** Value for Timestamp java class */
    public static final String TIMESTAMP_VALUE = "java.sql.Timestamp";
    /** Value for Time java class */
    public static final String TIME_VALUE = "java.sql.Time";
    /** Value for Double java class */
    public static final String DOUBLE_VALUE = "java.lang.Double";
    /** Value for Float java class */
    public static final String FLOAT_VALUE = "java.lang.Float";
    /** Value for Long java class */
    public static final String LONG_VALUE = "java.lang.Long";
    /** Value for Short java class */
    public static final String SHORT_VALUE = "java.lang.Short";
    /** Value for BigDecimal java class */
    public static final String BIGDECIMAL_VALUE = "java.math.BigDecimal";
    /** Value for Object java class */
    public static final String OBJECT_VALUE = "java.lang.Object";

    /** Parameter single selection : only ine value can be selected at runtime */
    public static final String SINGLE_SELECTION = "Single";
    /** Parameter multiple selection : more than one value can be selected at runtime */
    public static final String MULTIPLE_SELECTION = "Multiple";

    /** No order for parameter source */
    public static final byte NO_ORDER = -1;

    // These values are used in the orderByCombo from SourceDialog (do not modify)
    /** Select order for parameter source */
    public static final byte ORDER_BY_SELECT = 0;
    /** Order by name for parameter with manual source */
    public static final byte ORDER_BY_NAME = 1;
    /** Order by id for parameter with manual source */
    public static final byte ORDER_BY_ID = 2;

    /** Name of start date parameter used in intervals */
    public static String INTERVAL_START_DATE_NAME = "start_date";
    
    /** Name of end date parameter used in intervals */
    public static String INTERVAL_END_DATE_NAME = "end_date";

    /** All values for java classes */
    public static final String[] ALL_VALUES = {
        INTEGER_VALUE,
        STRING_VALUE,
        BOOLEAN_VALUE,
        BYTE_VALUE,
        DATE_VALUE,
        TIMESTAMP_VALUE,
        TIME_VALUE,
        DOUBLE_VALUE,
        FLOAT_VALUE,
        LONG_VALUE,
        SHORT_VALUE,
        BIGDECIMAL_VALUE,
        OBJECT_VALUE
    };

    /** All value sfor parameter selection */
    public static final String[] SELECTIONS = {
        SINGLE_SELECTION,
        MULTIPLE_SELECTION
    };
    
    protected String name;
    protected String runtimeName;
    protected String description;
    protected String valueClassName = STRING_VALUE;
    protected transient Class valueClass;
    protected String source;
    protected String selection;
    protected boolean ignore;
    protected boolean mandatory;
    protected boolean manualSource;   // source is a "select" written by user
    protected String schema;
    protected boolean isProcedureParameter;
    protected String previewValue;    // preview value for procedure parameter
    protected byte orderBy;

    protected ArrayList<Serializable> defaultValues;
    protected String defaultSource;

    // for true parameter does not appear at runtime
    // a hidden parameter must have a default value
    protected boolean hidden;

    // from external parameters
    protected transient List values = new ArrayList();
    // values from default source
    protected transient ArrayList<Serializable> defaultSourceValues;
    // set if it is used inside a subreport
    protected transient boolean subreportParameter = false;

    /** Create a query parameter
     *
     * @param name parameter name
     * @param valueClassName name for the value class
     */
    public QueryParameter(String name, String valueClassName) {
        this(name, "", valueClassName);
    }

    /** Create a query parameter
     *
     * @param name parameter name
     * @param description parameter description
     * @param valueClassName name for the value class
     */
    public QueryParameter(String name, String description,
            String valueClassName) {
        this.name = name;
        this.description = description;
        this.valueClassName = valueClassName;
    }

    /** Get parameter name
     *
     * @return parameter name
     */
    public String getName() {
        return this.name;
    }
        
    /** Set parameter name
     * 
     * This method is useful if we want to clone a parameter because we need to change the name
     */
    public void setName(String name) {
		this.name = name;
	}

	/** Get parameter name at runtime
     *
     * @return parameter name at runtime
     */
    public String getRuntimeName() {
        return runtimeName;
    }

    /** Set parameter runtime name
     *
     * @param runtimeName parameter runtime name
     */
    public void setRuntimeName(String runtimeName) {
        this.runtimeName = runtimeName;
    }

    /** Get parameter description
     *
     * @return parameter description
     */
    public String getDescription() {
        return this.description;
    }

    /** Set parameter description
     *
     * @param description parameter description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /** Get java class object for the parameter value
     *
     * @return java class object for the parameter value
     */
    public Class getValueClass() {
        if (valueClass == null) {
            if (valueClassName != null) {
                try {
                    valueClass = Class.forName(valueClassName);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return valueClass;
    }

    /** Get the name of the value class
     *
     * @return the name of the value class
     */
    public String getValueClassName() {
        return valueClassName;
    }

    /** Get parameter selection
     *
     * @return parameter selection
     */
    public String getSelection() {
        return selection;
    }

    /** Set parameter selection
     *
     * @param selection parameter selection
     */
    public void setSelection(String selection) {
        this.selection = selection;
    }

    /** Get parameter source
     *
     * @return parameter source
     */
    public String getSource() {
        return source;
    }

    /** Set parameter source
     *
     * @param source parameter source
     */
    public void setSource(String source) {
        this.source = source;
    }


    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }


    /** Get parameter values
     *
     * @return parameter values
     */
    public List getValues() {
        return values;
    }

    /** Set parameter values
     *
     * @param values parameter values
     */
    public void setValues(List values) {
        this.values = values;
    }

    /** Get default parameter values
     *
     * @return default parameter values
     */
    public ArrayList<Serializable> getDefaultValues() {
        return defaultValues;
    }

    /** Set default parameter values
     *
     * @param defaultValues default parameter values
     */
    public void setDefaultValues(ArrayList<Serializable> defaultValues) {
        this.defaultValues = defaultValues;
    }

    /** Get default source for parameters
     *
     * @return default source for parameters
     */
    public String getDefaultSource() {
        return defaultSource;
    }

    /** Set default source for parameters
     *
     * @param defaultSource default source for parameters
     */
    public void setDefaultSource(String defaultSource) {
        this.defaultSource = defaultSource;
    }

    /** Get default source parameter values
     *
     * @return default source parameter values
     */
    public ArrayList<Serializable> getDefaultSourceValues() {
        return defaultSourceValues;
    }

    /** Set default source parameter values
     *
     * @param defaultSourceValues default source parameter values
     */
    public void setDefaultSourceValues(ArrayList<Serializable> defaultSourceValues) {
        this.defaultSourceValues = defaultSourceValues;
    }

    /** See if parameter is mandatory (must enter value(s) at runtime)
     *
     * @return true if parameter is mandatory
     */
    public boolean isMandatory() {
        return mandatory;
    }

    /** Set mandatory flag
     *
     * @param mandatory mandatory flag
     */
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    /** See if parameter has a manual source (an sql select)
     *
     * @return true if parameter has a manual source
     */
    public boolean isManualSource() {
        return manualSource;
    }

    /** Set manual source flag
     *
     * @param manualSource manual source flag
     */
    public void setManualSource(boolean manualSource) {
        this.manualSource = manualSource;
    }

    /** Get database schema
     *
     * @return database schema
     */
    public String getSchema() {
        return schema;
    }

    /** Set database schema
     *
     * @param schema database schema
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /** See if this parameter is a procedure parameter
     *
     * @return true if this parameter is a procedure parameter
     */
    public boolean isProcedureParameter() {
        return isProcedureParameter;
    }

    /** Set procedure parameter
     *
     * @param procedureParameter true if this parameter is a procedure parameter, false otherwise
     */
    public void setProcedureParameter(boolean procedureParameter) {
        isProcedureParameter = procedureParameter;
    }

    /** Get preview value for procedure parameter
     * This value must be used inside the business procedure to exit
     * (so we can can the columns as fast  as possible)
     *
     * @return preview value as a string
     */
    public String getPreviewValue() {
        return previewValue;
    }

    /** Set preview value for procedure parameter
     * Allows to set null for any type
     *
     * @param previewValue preview value for procedure parameter
     */
    public void setPreviewValue(String previewValue) {
        if (!isProcedureParameter && (previewValue != null)) {
            throw new IllegalArgumentException("Parameter '" +  name + "' is not a procedure parameter.");
        }
        this.previewValue = previewValue;
    }

    /** Get order for parameter values : ORDER_BY_NAME, ORDER_BY_ID
     *  Has meaning only if parameter has a manual source
     *
     * @return order type of parameter values
     */
    public byte getOrderBy() {
        return orderBy;
    }

    /** Set order for parameter values :
     *
     * @param orderBy one of ORDER_BY_NAME, ORDER_BY_ID
     */
    public void setOrderBy(byte orderBy) {
        this.orderBy = orderBy;
    }

    /** See if current parameter is hidden (does not appear at runtime)
     *
     * @return true if parameter iss hidden
     */
    public boolean isHidden() {
        return hidden;
    }

    /** Set hidden
     *
     * @param hidden hidden
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /** See if parameter is dependent on other parameters (its manual source contains other parameters
     * names between tags '${' and '}'
     * @return true if parameter is dependent on other parameters, false otherwise
     */
    public boolean isDependent() {
        if (isManualSource()) {
            if (source.contains("${")){
                return true;
            }
        }
        return false;
    }

    /** Get dependent parameter names
     *
     * @return a list of the names of all parameters the current parameter depends on
     */
    public List<String> getDependentParameterNames() {
        List<String> names = new ArrayList<String>();
        if (isDependent()) {
            String chunk = source;
            int start = chunk.indexOf("${");
            while (start != -1) {
                int end = chunk.indexOf("}");
                String paramName = chunk.substring(start + 2, end);
                names.add(paramName);
                if (end == chunk.length()-1) {
                    start = -1;
                } else {
                    chunk = chunk.substring(end+1);
                    start = chunk.indexOf("${");
                }
            }
        }
        return names;
    }
        
     public boolean isSubreportParameter() {
		return subreportParameter;
	}

	public void setSubreportParameter(boolean subreportParameter) {
		this.subreportParameter = subreportParameter;
	}

	/** Equals
     *
     * @param o parameter object
     * @return true if current parameter object equals parameter object, false otherwise
     */
    public boolean equals(Object o) {
        
    	if (!basicEquals(o)) return false;
        
        QueryParameter that = (QueryParameter) o;     
        
        if (ignore != that.ignore) return false;
        if (mandatory != that.mandatory) return false;                        
        if (description != null ? !description.equals(that.description) : that.description != null) return false;                        
        if (runtimeName != null ? !runtimeName.equals(that.runtimeName) : that.runtimeName != null) return false;        
        if (schema != null ? !schema.equals(that.schema) : that.schema != null) return false;                                                        
        if (orderBy != that.getOrderBy()) return false;        
        
        if (source != null ? !source.equals(that.source) : that.source != null) return false;        
        if (defaultSource != null ? !defaultSource.equals(that.defaultSource) : that.defaultSource != null) return false;        
                
        return true;
    }
    
    private boolean basicEquals(Object o) {
    	if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueryParameter that = (QueryParameter) o;        
        if (manualSource != that.manualSource) return false;        
        if (name != null ? !name.equals(that.name) : that.name != null) return false;        
        if (selection != null ? !selection.equals(that.selection) : that.selection != null) return false;                        
        if (valueClassName != null ? !valueClassName.equals(that.valueClassName) : that.valueClassName != null)
            return false;        
        if (isProcedureParameter != that.isProcedureParameter) return false;        
        if (previewValue != null ? !previewValue.equals(that.previewValue) : that.previewValue != null) return false;        
        if (defaultValues != null && that.defaultValues != null && (!defaultValues.containsAll(that.defaultValues) ||
                !that.defaultValues.containsAll(defaultValues))) return false;
        if (hidden != that.hidden) return false;
        return true;
    }
    
    // functional comparison
    public boolean compare(Object o) {
        if (!basicEquals(o)) return false;
        
        QueryParameter that = (QueryParameter) o;     
        
        // compare sources to have the same select no matter of letter-case and white spaces
        if (!StringUtil.equalsText(source, that.source)) return false;
        if (!StringUtil.equalsText(defaultSource, that.defaultSource)) return false;
        
        return true;
    }

    /** Hash code value for this parameter
     *
     * @return a hash code value for this parameter
     */
    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (runtimeName != null ? runtimeName.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (valueClassName != null ? valueClassName.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (selection != null ? selection.hashCode() : 0);
        result = 31 * result + (ignore ? 1 : 0);
        result = 31 * result + (mandatory ? 1 : 0);
        result = 31 * result + (manualSource ? 1 : 0);
        result = 31 * result + (schema != null ? schema.hashCode() : 0);
        result = 31 * result + (isProcedureParameter ? 1 : 0);
        result = 31 * result + (previewValue != null ? previewValue.hashCode() : 0);
        result = 31 * result + orderBy;
        result = 31 * result + (defaultValues != null ? defaultValues.hashCode() : 0);
        result = 31 * result + (defaultSource != null ? defaultSource.hashCode() : 0);
        result = 31 * result + (hidden ? 1 : 0);
        return result;
    }


    public String toString() {
        return "QueryParameter{" +
                "name='" + name + '\'' +
                ", runtimeName='" + runtimeName + '\'' +
                ", description='" + description + '\'' +
                ", valueClassName='" + valueClassName + '\'' +
                ", source='" + source + '\'' +
                ", selection='" + selection + '\'' +
                ", ignore=" + ignore +
                ", mandatory=" + mandatory +
                ", manualSource=" + manualSource +
                ", schema='" + schema + '\'' +
                ", isProcedureParameter=" + isProcedureParameter +
                ", previewValue='" + previewValue + '\'' +
                ", orderBy=" + orderBy +
                ", hidden=" + hidden +
                ", defaultValues=" + defaultValues +
                ", defaultSource=" + defaultSource +
                ", defaultSourceValues=" + defaultSourceValues +
                '}';
    }
}
