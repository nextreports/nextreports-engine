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
package ro.nextreports.integration;

import java.awt.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.io.Serializable;

import javax.swing.*;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportRunner;
import ro.nextreports.engine.querybuilder.IdNameRenderer;
import ro.nextreports.engine.queryexec.IdName;
import ro.nextreports.engine.queryexec.QueryParameter;
import ro.nextreports.engine.util.ParameterUtil;

import com.toedter.calendar.JDateChooser;

/**
 * Method populateDependentParameters will give you a hint on how to work with dependent parameters
 * in your user interface
 *
 * This example class does not take parameters default values into account (which must be automatically
 * selected in user interface). Default parameter values can be obtained like this :
 *    A parameter may have static default values : parameter.getDefaultValues()
 *    or dynamic default values taken from a source : parameter.getDefaultSource()
 *    To get values for a default source you may use the helper method
 *    ParameterUtil.getDefaultSourceValues(connection, parameter);
 *
 * @author Decebal Suiu
 */
public class RuntimeParametersPanel extends JPanel {

    private static final String NULL = "__NULL__";

    private Connection connection;
    private List<QueryParameter> parameters;

    private JComboBox formatComboBox;
    private List<JComponent> components;
    private Report report;

    public RuntimeParametersPanel(Connection connection, Report report) {
        super();

        this.connection = connection;
        this.report = report;
        this.parameters = new ArrayList<QueryParameter>(
                    ParameterUtil.getUsedParametersMap(report).values());

        initComponents();
    }

    public String getFormat() {
        return (String) formatComboBox.getSelectedItem();
    }

    public Map<String, Object> getParametersValues() throws Exception {
        Map<String, Object> parametersValues = new HashMap<String, Object>();
        for (int i = 0; i < parameters.size(); i++) {
            QueryParameter parameter = parameters.get(i);
            if (parameter.isIgnore()) {
                continue;
            }

            JComponent component = getParameterComponent(i);
            Object value = getParameterValue(component, parameter);
            String name = parameter.getName();

            parametersValues.put(name, value);
        }

        return parametersValues;
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        JPanel panel = new JPanel(new GridBagLayout());

        panel.add(new JLabel("Format"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        formatComboBox = new JComboBox(ReportRunner.FORMATS);
        formatComboBox.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxx"); // for width
        panel.add(formatComboBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0));

        try {
            initParameterComponents();
        } catch (Exception e) {
            showError(e.getMessage());
            throw new RuntimeException(e);
        }
        for (int i = 0; i < components.size(); i++) {
            JComponent component = components.get(i);
            QueryParameter parameter = parameters.get(i);

            panel.add(new JLabel(getParameterLabel(parameter)), new GridBagConstraints(0, i + 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
            if (component instanceof JScrollPane) {
                panel.add(component, new GridBagConstraints(1, i + 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0));
            } else {
                panel.add(component, new GridBagConstraints(1, i + 2, 1, 1, 1.0, 1.1, GridBagConstraints.NORTHWEST,
                        GridBagConstraints.BOTH, new Insets(5, 0, 5, 5), 0, 0));
            }
        }

        add(new JScrollPane(panel), new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }

    private void initParameterComponents() throws Exception {
        components = new ArrayList<JComponent>();
        for (QueryParameter parameter : parameters) {
            JComponent component;
            if (!isEmpty(parameter.getSource())) {
                List<IdName> values = ParameterUtil.getParameterValues(connection, parameter);

                if (parameter.getSelection().equals(QueryParameter.SINGLE_SELECTION)) {
                    JComboBox comboBox = new JComboBox(values.toArray());
                    comboBox.insertItemAt("-- Select --", 0);
                    comboBox.setRenderer(new IdNameRenderer());
                    component = comboBox;
                } else { // QueryParameter.MULTIPLE_SELECTION
                    JList list = new JList(values.toArray());
                    list.setVisibleRowCount(5);
                    list.setCellRenderer(new IdNameRenderer());

                    // simulate a selection
                    int[] selection = new int[values.size()];
                    for (int i=0; i<values.size();  i++) {
                        selection[i] = i;
                    }
                    list.setSelectedIndices(selection);

                    component = new JScrollPane(list);
                }                
            } else {
                if ("java.util.Date".equals(parameter.getValueClassName())) {
                    component = new JDateChooser();
                } else if ("java.lang.Boolean".equals(parameter.getValueClassName())) {
                    component = new JCheckBox();
                } else {
                    component = new JTextField(25);
                }
            }

            // example to use default values
            if ("java.util.Date".equals(parameter.getValueClassName())) {                
                ArrayList<Serializable> defaultValues = parameter.getDefaultValues();
                String source = parameter.getDefaultSource();
                if ((source != null) && (!"".equals(source.trim())) ) {
                    defaultValues = ParameterUtil.getDefaultSourceValues(connection, parameter);
                }
                ((JDateChooser) component).setDate((Date) defaultValues.get(0));
            }

            components.add(component);
        }

        for (QueryParameter parameter : parameters) {
            populateDependentParameters(report , parameter);
        }
    }

    private void setValues(JComponent component, List<IdName> values) {
        if (component instanceof JComboBox) {
            JComboBox combo = ((JComboBox) component);
            combo.removeAllItems();
            for (int j = 0, len = values.size(); j < len; j++) {
                combo.addItem(values.get(j));
            }
        } else if (component instanceof JList) {
            JList list = ((JList)component);
            DefaultListModel model = new DefaultListModel();
            int[] selection = new int[values.size()];
            for (int j = 0, len = values.size(); j < len; j++) {
                model.addElement(values.get(j));
                selection[j] = j;
            }
            list.setModel(model);
            list.setSelectedIndices(selection);
        }
    }

    private String getParameterLabel(QueryParameter parameter) {
        String label = parameter.getRuntimeName();
        if (isEmpty(label)) {
            label = parameter.getName();
        }
        if (parameter.isMandatory()) {
            label += " *";
        }

        return label;
    }

    private boolean isEmpty(String s) {
        if ((s == null) || s.trim().equals("")) {
            return true;
        }

        return false;
    }

    private void showError(String error) {
        JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), error, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private Object getParameterValue(JComponent component, QueryParameter parameter) throws Exception {
        Object value = null;
        if (component instanceof JTextField) {
            value = ((JTextField) component).getText();
            if (value.equals("")) {
                if (parameter.isMandatory()) {
                    throw new Exception("Value for parameter '" + parameter.getRuntimeName() + "' is not entered.");
                } else {
                    value = null;
                }
            }
        } else if (component instanceof JComboBox) {
            JComboBox combo = (JComboBox) component;
            if (combo.getSelectedIndex() == 0) {
                if (parameter.isMandatory()) {
                    throw new Exception("Value for parameter '" + parameter.getRuntimeName() + "' is not selected.");
                } else {
                    value = null;
                }
            } else {
                value = combo.getSelectedItem();
            }
        } else if (component instanceof JList) {
            value = ((JList) component).getSelectedValues();
            if (((Object[]) value).length == 0) {
                if (parameter.isMandatory()) {
                    throw new Exception("Value for parameter '" + parameter.getRuntimeName() + "' is not selected.");
                } else {
                    value = new Object[]{NULL};
                }
            }
        } else if (component instanceof JDateChooser) {
            value = ((JDateChooser) component).getDate();
            if (value == null) {
                if (parameter.isMandatory()) {
                    throw new Exception("Value for parameter '" + parameter.getRuntimeName() + "' is not entered.");
                }
            }
        } else if (component instanceof JCheckBox) {
            value = ((JCheckBox) component).isSelected();
        }

        if (value == null) {
            return value;
        }

        try {
            if (QueryParameter.STRING_VALUE.equals(value.getClass().getName())) {
                String className = parameter.getValueClassName();
                value = ParameterUtil.getParameterValueFromString(className, (String)value);                                
            }
        } catch (Exception e) {
            throw new Exception("Invalid parameter value " + value +
                    " for parameter '" + parameter.getRuntimeName() +
                    "' of type " + parameter.getValueClassName() + " .");
        }

        return value;
    }

    // some parameters may depend on other parameters
    // when we select the values for a parameter we must take all children dependent parameters
    // and set their values too
    private void populateDependentParameters(Report nextReport, QueryParameter parameter) {
        Map<String, QueryParameter> childParams = ParameterUtil.getChildDependentParameters(nextReport, parameter);

        // update model parameter values for every child parameter
        for (QueryParameter childParam : childParams.values()) {
            if (!parameters.contains(childParam)) {
                continue;
            }

            int index = parameters.indexOf(childParam);
            JComponent childComponent = getParameterComponent(index);

            List<IdName> values = new ArrayList<IdName>();

            // a parameter may depend on more than one parameter (has more parents)
            // we must see if all the parents have selected values (this is not done in this example)
            Map<String, QueryParameter> allParentParams = ParameterUtil.getParentDependentParameters(nextReport, childParam);

            if ((childParam.getSource() != null) && (childParam.getSource().trim().length() > 0)) {
                try {
                    Map<String, Serializable> allParameterValues = new HashMap<String, Serializable>();

                    for (String name : allParentParams.keySet()) {
                        QueryParameter parent = allParentParams.get(name);
                        index = parameters.indexOf(parent);
                        JComponent parentComponent = getParameterComponent(index);
                        allParameterValues.put(name, (Serializable)getParameterValue( parentComponent, parent));
                    }

                    values = ParameterUtil.getParameterValues(connection, childParam,
                            ParameterUtil.toMap(parameters), allParameterValues);
                    setValues(childComponent,values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private JComponent getParameterComponent(int index) {
        JComponent component = components.get(index);
        if (component instanceof JScrollPane) {
            component = (JComponent) ((JScrollPane) component).getViewport().getView(); // real component
        }
        return component;
    }


}
