package configuration.processes

import betsy.data.BetsyProcess
import betsy.data.SoapTestStep
import betsy.data.assertions.ExitAssertion
import betsy.data.assertions.SoapFaultTestAssertion

import java.lang.reflect.Field

import static configuration.processes.BasicActivityProcesses.BASIC_ACTIVITIES
import static configuration.processes.PatternProcesses.CONTROL_FLOW_PATTERNS
import static configuration.processes.ScopeProcesses.SCOPES
import static configuration.processes.StaticAnalysisProcesses.STATIC_ANALYSIS
import static configuration.processes.StructuredActivityProcesses.STRUCTURED_ACTIVITIES

class ProcessRepository {

    private Map<String, List<BetsyProcess>> repository = [:]

    public ProcessRepository() {
        repository.put("ALL", BASIC_ACTIVITIES + SCOPES + STRUCTURED_ACTIVITIES + CONTROL_FLOW_PATTERNS + STATIC_ANALYSIS as List<BetsyProcess>)

        Field[] fields = [
                BasicActivityProcesses.class.declaredFields +
                        ScopeProcesses.class.declaredFields +
                        StructuredActivityProcesses.class.declaredFields +
                        PatternProcesses.class.declaredFields +
                        StaticAnalysisProcesses.class.declaredFields
        ].flatten()

        fields.each { Field f ->
            // adds only the static fields that are lists (groups)
            if (f.type == List.class) {
                // f.get(null) returns the value of the field. the null parameter is ignored as the field is static.
                repository.put(f.name, f.get(null) as List<BetsyProcess>)
            }
        }

        repository.put(
                "FAULTS",
                repository.get("ALL").findAll { BetsyProcess process ->
                    process.testCases.any {
                        it.testSteps.any { it instanceof SoapTestStep && it.assertions.any { it instanceof SoapFaultTestAssertion } }
                    }
                }
        )

        repository.put(
                "WITH_EXIT_ASSERTION",
                repository.get("ALL").findAll { process ->
                    process.testCases.any { it.testSteps.any { it instanceof SoapTestStep && it.assertions.any { it instanceof ExitAssertion } } }
                }
        )

        // insert every process into the map
        repository.get("ALL").each { BetsyProcess process ->
            repository.put(process.bpelFileNameWithoutExtension, [process])
        }
    }

    public List<BetsyProcess> getByName(String name) {
        String trimmedName = name.trim()
        String key = repository.keySet().find { it.toUpperCase() == trimmedName.toUpperCase() }
        if (key == null) {
            key = trimmedName
        }

        List<BetsyProcess> result = repository.get(key)

        if (result == null) {
            throw new IllegalArgumentException("Name '${trimmedName}' does not exist in repository.")
        }

        return result
    }

    public List<BetsyProcess> getByNames(String[] names) {
        List<BetsyProcess> result = []

        for (String name : names) {
            result << getByName(name)
        }

        result = result.flatten().unique()

        if (result.isEmpty()) {
            throw new IllegalArgumentException("Names ${names.join(",")} do not exist in repository.")
        }

        return result
    }

    public List<String> getNames() {
        return new ArrayList<String>(repository.keySet())
    }

}
