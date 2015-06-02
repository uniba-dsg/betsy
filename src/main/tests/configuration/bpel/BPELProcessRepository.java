package configuration.bpel;

import betsy.bpel.model.BPELProcess;
import betsy.bpel.model.assertions.ExitAssertion;
import betsy.bpel.model.assertions.SoapFaultTestAssertion;
import betsy.bpel.model.steps.SoapTestStep;
import betsy.common.repositories.Repository;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class BPELProcessRepository {

    private final Repository<BPELProcess> repo = new Repository<>();

    public static final BPELProcessRepository INSTANCE = new BPELProcessRepository();

    private BPELProcessRepository() {
        List<BPELProcess> all = new LinkedList<>();
        all.addAll(BasicActivityProcesses.BASIC_ACTIVITIES);
        all.addAll(ScopeProcesses.SCOPES);
        all.addAll(StructuredActivityProcesses.STRUCTURED_ACTIVITIES);
        all.addAll(PatternProcesses.CONTROL_FLOW_PATTERNS);
        repo.put("ALL", all);

        List<Field> fields = new LinkedList<>();
        fields.addAll(Arrays.asList(BasicActivityProcesses.class.getDeclaredFields()));
        fields.addAll(Arrays.asList(ScopeProcesses.class.getDeclaredFields()));
        fields.addAll(Arrays.asList(StructuredActivityProcesses.class.getDeclaredFields()));
        fields.addAll(Arrays.asList(PatternProcesses.class.getDeclaredFields()));
        for (Field f : fields) {
            // adds only the static fields that are lists (groups)
            if (f.getType().equals(List.class)) {
                // f.get(null) returns the value of the field. the null parameter is ignored as the field is static.
                try {
                    Object value = f.get(null);
                    repo.put(f.getName(), (List<BPELProcess>) value);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Could not retrieve field value " + f.getName(), e);
                }
            }
        }

        repo.put("STATIC_ANALYSIS", StaticAnalysisProcesses.STATIC_ANALYSIS);

        Map<String, List<BPELProcess>> ruleGroups = StaticAnalysisProcesses.getGroupsPerRuleForSAProcesses(StaticAnalysisProcesses.STATIC_ANALYSIS);
        for (Map.Entry<String, List<BPELProcess>> entry : ruleGroups.entrySet()) {
            repo.put(entry.getKey(), entry.getValue());
        }

        repo.put("ERRORS", ErrorProcesses.getProcesses());

        repo.put("MINIMAL", Collections.singletonList(StructuredActivityProcesses.SEQUENCE));

        // automatic group
        repo.put(
                "FAULTS",
                repo.getByName("ALL").stream().filter((p) ->
                        p.getTestCases().stream().anyMatch((tc) ->
                                tc.getTestSteps().stream().anyMatch((ts) -> ts instanceof SoapTestStep && ((SoapTestStep) ts).getAssertions().stream().anyMatch((a) ->
                                        a instanceof SoapFaultTestAssertion)))).collect(Collectors.toList()));

        // automatic group
        repo.put(
                "WITH_EXIT_ASSERTION",
                repo.getByName("ALL").stream().filter((p) ->
                        p.getTestCases().stream().anyMatch((tc) ->
                                tc.getTestSteps().stream().anyMatch((ts) -> ts instanceof SoapTestStep && ((SoapTestStep) ts).getAssertions().stream().anyMatch((a) ->
                                        a instanceof ExitAssertion)))).collect(Collectors.toList()));

        // insert every process into the map
        for (BPELProcess process : repo.getByName("ALL")) {
            repo.put(process.getName(), new ArrayList<>(Collections.singletonList(process)));
        }

        for (BPELProcess process : repo.getByName("STATIC_ANALYSIS")) {
            repo.put(process.getName(), new ArrayList<>(Collections.singletonList(process)));
        }

        for (BPELProcess process : repo.getByName("ERRORS")) {
            repo.put(process.getName(), new ArrayList<>(Collections.singletonList(process)));
        }
    }

    public List<BPELProcess> getByName(String name) {
        return repo.getByName(name);
    }

    public List<BPELProcess> getByNames(String[] names) {
        return repo.getByNames(names);
    }

    public List<String> getNames() {
        return repo.getNames();
    }

    public List<String> getGroups() {
        return repo.getGroups();
    }

}
