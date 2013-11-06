package configuration.processes

import betsy.data.BetsyProcess
import betsy.data.steps.SoapTestStep
import betsy.data.assertions.ExitAssertion
import betsy.data.assertions.SoapFaultTestAssertion
import configuration.util.Repository

import java.lang.reflect.Field

import static configuration.processes.BasicActivityProcesses.BASIC_ACTIVITIES
import static configuration.processes.PatternProcesses.CONTROL_FLOW_PATTERNS
import static configuration.processes.ScopeProcesses.SCOPES
import static configuration.processes.StaticAnalysisProcesses.STATIC_ANALYSIS
import static configuration.processes.StructuredActivityProcesses.STRUCTURED_ACTIVITIES

class ProcessRepository {

    private Repository<BetsyProcess> repo = new Repository<>();

    public ProcessRepository() {
        repo.put("ALL", BASIC_ACTIVITIES + SCOPES + STRUCTURED_ACTIVITIES + CONTROL_FLOW_PATTERNS + STATIC_ANALYSIS as List<BetsyProcess>)

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
                repo.put(f.name, f.get(null) as List<BetsyProcess>)
            }
        }

        repo.put(
                "FAULTS",
                repo.getByName("ALL").findAll { BetsyProcess process ->
                    process.testCases.any {
                        it.testSteps.any { it instanceof SoapTestStep && it.assertions.any { it instanceof SoapFaultTestAssertion } }
                    }
                }
        )

        repo.put(
                "WITH_EXIT_ASSERTION",
                repo.getByName("ALL").findAll { process ->
                    process.testCases.any { it.testSteps.any { it instanceof SoapTestStep && it.assertions.any { it instanceof ExitAssertion } } }
                }
        )

        // insert every process into the map
        repo.getByName("ALL").each { BetsyProcess process ->
            repo.put(process.bpelFileNameWithoutExtension, [process])
        }
    }

    public List<BetsyProcess> getByName(String name) {
        return repo.getByName(name);
    }

    public List<BetsyProcess> getByNames(String[] names) {
        return repo.getByNames(names);
    }

    public List<String> getNames() {
        return repo.getNames();
    }

}
