package configuration.bpmn;

import pebl.benchmark.test.Test;
import betsy.common.repositories.Repository;

import java.lang.reflect.Field;
import java.util.*;

public class BPMNProcessRepository {
    private final Repository<Test> repo = new Repository<>();

    public BPMNProcessRepository() {
        List<Test> all = new LinkedList<>();
        all.addAll(GatewayProcesses.GATEWAYS);
        all.addAll(ActivityProcesses.ACTIVITIES);
        all.addAll(EventProcesses.EVENTS);
        all.addAll(BasicProcesses.BASICS);
        all.addAll(ErrorProcesses.ERRORS);
        all.addAll(DataProcesses.DATA);
        all.addAll(PatternProcesses.PATTERNS);
        repo.put("ALL", all);

        List<Field> fields = new LinkedList<>();
        fields.addAll(Arrays.asList(GatewayProcesses.class.getDeclaredFields()));
        fields.addAll(Arrays.asList(ActivityProcesses.class.getDeclaredFields()));
        fields.addAll(Arrays.asList(ErrorProcesses.class.getDeclaredFields()));
        fields.addAll(Arrays.asList(EventProcesses.class.getDeclaredFields()));
        fields.addAll(Arrays.asList(BasicProcesses.class.getDeclaredFields()));
        fields.addAll(Arrays.asList(DataProcesses.class.getDeclaredFields()));
        fields.addAll(Arrays.asList(PatternProcesses.class.getDeclaredFields()));
        for (Field f : fields) {
            // adds only the static fields that are lists (groups)
            if (f.getType().equals(List.class)) {
                // f.get(null) returns the value of the field. the null parameter is ignored as the field is static.
                try {
                    Object value = f.get(null);
                    repo.put(f.getName(), (List<Test>) value);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Could not retrieve field value", e);
                }
            }
        }

        repo.put("BPMN_CONSTRAINTS", BpmnConstraintsProcesses.BPMN_CONSTRAINTS);

        repo.put("MINIMAL", Collections.singletonList(BasicProcesses.SEQUENCE_FLOW));

        // insert every process into the map
        for (Test process : repo.getByName("ALL")) {
            repo.put(process.getName(), new ArrayList<>(Collections.singletonList(process)));
        }

    }

    public List<Test> getByName(String name) {
        return repo.getByName(name);
    }

    public List<Test> getByNames(String... names) {
        return repo.getByNames(names);
    }

    public List<String> getNames() {
        return repo.getNames();
    }

}
