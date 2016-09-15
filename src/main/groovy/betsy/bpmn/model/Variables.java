package betsy.bpmn.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

import pebl.test.steps.vars.Variable;

public class Variables {

    private final List<Variable> variables;

    public Variables(List<Variable> variables) {
        this.variables = Objects.requireNonNull(variables);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        for (Variable entry : variables) {
            Map<String, Object> submap = new HashMap<>();
            submap.put("value", entry.getValue());
            submap.put("type", entry.getType());
            map.put(entry.getName(), submap);
        }

        return map;
    }

    public String toQueryParameter() {
        if(variables.isEmpty()) {
            return "";
        }

        Map<String, Object> variables = new HashMap<>();
        for (Variable variable : this.variables) {
            variables.put(variable.getName(), variable.getValue());
        }

        StringJoiner joiner = new StringJoiner("&", "?", "");
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            joiner.add("map_" + entry.getKey() + "=" + entry.getValue());
        }
        return joiner.toString();
    }

    public Object[] toArray() {
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
