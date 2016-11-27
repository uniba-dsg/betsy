package configuration.bpel;

import pebl.benchmark.test.Test;
import pebl.benchmark.test.assertions.AssertExit;
import pebl.benchmark.test.assertions.AssertSoapFault;
import pebl.benchmark.test.steps.soap.SendSoapMessage;
import betsy.common.repositories.Repository;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class BPELProcessRepository {

    private final Repository<Test> repo = new Repository<>();

    public static final BPELProcessRepository INSTANCE = new BPELProcessRepository();

    private BPELProcessRepository() {
        List<Test> all = new LinkedList<>();
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
                    repo.put(f.getName(), (List<Test>) value);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Could not retrieve field value " + f.getName(), e);
                }
            }
        }

        repo.put("STATIC_ANALYSIS", StaticAnalysisProcesses.STATIC_ANALYSIS);

        Map<String, List<Test>> ruleGroups = StaticAnalysisProcesses.getGroupsPerRuleForSAProcesses(StaticAnalysisProcesses.STATIC_ANALYSIS);
        for (Map.Entry<String, List<Test>> entry : ruleGroups.entrySet()) {
            repo.put(entry.getKey(), entry.getValue());
        }

        repo.put("ERRORS", ErrorProcesses.getProcesses());

        repo.put("MINIMAL", Collections.singletonList(StructuredActivityProcesses.SEQUENCE));

        // automatic group
        repo.put(
                "FAULTS",
                repo.getByName("ALL").stream().filter((p) ->
                        p.getTestCases().stream().anyMatch((tc) ->
                                tc.getTestSteps().stream().anyMatch((ts) -> ts instanceof SendSoapMessage && ((SendSoapMessage) ts).getTestAssertions().stream().anyMatch((a) ->
                                        a instanceof AssertSoapFault)))).collect(Collectors.toList()));

        // automatic group
        repo.put(
                "WITH_EXIT_ASSERTION",
                repo.getByName("ALL").stream().filter((p) ->
                        p.getTestCases().stream().anyMatch((tc) ->
                                tc.getTestSteps().stream().anyMatch((ts) -> ts instanceof SendSoapMessage && ((SendSoapMessage) ts).getTestAssertions().stream().anyMatch((a) ->
                                        a instanceof AssertExit)))).collect(Collectors.toList()));

        // insert every process into the map
        for (Test process : repo.getByName("ALL")) {
            repo.put(process.getName(), new ArrayList<>(Collections.singletonList(process)));
        }

        for (Test process : repo.getByName("STATIC_ANALYSIS")) {
            repo.put(process.getName(), new ArrayList<>(Collections.singletonList(process)));
        }

        for (Test process : repo.getByName("ERRORS")) {
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

    public List<String> getGroups() {
        return repo.getGroups();
    }

}
