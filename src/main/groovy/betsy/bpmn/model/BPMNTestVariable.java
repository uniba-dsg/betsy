package betsy.bpmn.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BPMNTestVariable {

    public BPMNTestVariable(String name, String type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
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

    private final String name;
    private final String type;
    private final Object value;

    public static Object[] mapToArrayWithMaps(List<BPMNTestVariable> variables) {
        Object[] result = new Object[variables.size()];

        int i = 0;
        for (BPMNTestVariable entry : variables) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", entry.getName());
            map.put("value", entry.getValue());
            result[i] = map;
            i++;
        }

        return result;
    }
}
