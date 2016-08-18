package betsy.bpmn.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class Variable {

    private final String name;
    private final String type;
    private final Object value;

    public Variable(String name, String type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public static Map<String, Object> toMap(List<Variable> variables) {
        Map<String, Object> map = new HashMap<>();

        for (Variable entry : variables) {
            Map<String, Object> submap = new HashMap<>();
            submap.put("value", entry.getValue());
            submap.put("type", entry.getType());
            map.put(entry.getName(), submap);
        }

        return map;
    }

    public static String toQueryParameter(List<Variable> vars) {
        Map<String, Object> variables = new HashMap<>();
        for (Variable variable : vars) {
            variables.put(variable.getName(), variable.getValue());
        }

        StringJoiner joiner = new StringJoiner("&", "?", "");
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            joiner.add("map_" + entry.getKey() + "=" + entry.getValue());
        }
        return joiner.toString();
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public static Object[] toArray(List<Variable> variables) {
        Object[] result = new Object[variables.size()];

        int i = 0;
        for (Variable entry : variables) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", entry.getName());
            map.put("value", entry.getValue());
            result[i] = map;
            i++;
        }

        return result;
    }
}
