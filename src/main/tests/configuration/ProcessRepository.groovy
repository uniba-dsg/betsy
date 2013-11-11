package configuration

import betsy.data.BetsyProcess
import betsy.data.steps.SoapTestStep
import betsy.data.assertions.ExitAssertion
import betsy.data.assertions.SoapFaultTestAssertion
import betsy.repositories.Repository

import java.lang.reflect.Field

import static BasicActivityProcesses.BASIC_ACTIVITIES
import static PatternProcesses.CONTROL_FLOW_PATTERNS

class ProcessRepository {

    private Repository<BetsyProcess> repo = new Repository<>();

    public ProcessRepository() {
        repo.put("ALL", BASIC_ACTIVITIES + configuration.ScopeProcesses.SCOPES + configuration.StructuredActivityProcesses.STRUCTURED_ACTIVITIES + CONTROL_FLOW_PATTERNS + configuration.StaticAnalysisProcesses.STATIC_ANALYSIS as List<BetsyProcess>)

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
            repo.put(process.name, [process])
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
