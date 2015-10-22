package ro.nextreports.engine.exporter.util.variable;

import java.util.Map;

public class EmptyDataVariable implements Variable {

    public static final String EMPTY_DATA_PARAM = "EMPTY";

    public String getName() {
        return Variable.EMPTY_DATA_VARIABLE;
    }

    public Object getCurrentValue(Map<String, Object> parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("EmptyDataVariable : parameters null.");
        }

        Object empty = parameters.get(EMPTY_DATA_PARAM);
        if  ((empty == null) || !(empty instanceof Boolean)) {
            throw new IllegalArgumentException("EmptyDataVariable : invalid parameter.");
        }

        return empty;
    }
}