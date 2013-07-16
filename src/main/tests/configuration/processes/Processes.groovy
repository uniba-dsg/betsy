package configuration.processes

import betsy.data.BetsyProcess
import betsy.data.SoapTestStep
import betsy.data.assertions.ExitAssertion
import betsy.data.assertions.SoapFaultTestAssertion

class Processes {

    BasicActivityProcesses basicActivityProcesses = new BasicActivityProcesses()

    StructuredActivityProcesses structuredActivityProcesses = new StructuredActivityProcesses()

    ScopeProcesses scopeProcesses = new ScopeProcesses()

    PatternProcesses patternProcesses = new PatternProcesses()

    public final List<BetsyProcess> ALL = [
            structuredActivityProcesses.STRUCTURED_ACTIVITIES,
            basicActivityProcesses.BASIC_ACTIVITIES,
            scopeProcesses.SCOPES,
            patternProcesses.CONTROL_FLOW_PATTERNS
    ].flatten() as List<BetsyProcess>

    public final List<BetsyProcess> FAULTS = ALL.findAll { process ->
        process.testCases.any {
            it.testSteps.any { it instanceof SoapTestStep && it.assertions.any { it instanceof SoapFaultTestAssertion } }
        }
    }

    public final List<BetsyProcess> WITH_EXIT_ASSERTION = ALL.findAll { process ->
        process.testCases.any { it.testSteps.any { it instanceof SoapTestStep && it.assertions.any { it instanceof ExitAssertion } } }
    }

    public List<BetsyProcess> get(String name) {
        String upperCaseName = name.toUpperCase()
        if ("ALL" == upperCaseName) {
            return ALL
        } else if ("WITH_EXIT_ASSERTION" == upperCaseName) {
            return WITH_EXIT_ASSERTION
        } else if ("FAULTS" == upperCaseName) {
            return FAULTS
        }

        List<BetsyProcess> result = getBasicProcess(upperCaseName)
        if (result == null) {
            result = getStructuredProcess(upperCaseName)
            if (result == null) {
                result = getScopeProcess(upperCaseName)
                if (result == null) {
                    result = getPatternProcess(upperCaseName)
                    if (result == null) {
                        result = ALL.findAll({ it.bpelFileNameWithoutExtension.toUpperCase() == upperCaseName })
                        if (result.isEmpty()) {
                            throw new IllegalArgumentException("Process ${name} does not exist")
                        }
                    }
                }
            }
        }
        return result
    }

    List<BetsyProcess> getBasicProcess(String name) {
        try {
            return [basicActivityProcesses."$name"]
        } catch (MissingPropertyException e) {
            return null
        }
    }

    List<BetsyProcess> getStructuredProcess(String name) {
        try {
            return [structuredActivityProcesses."$name"]
        } catch (MissingPropertyException e) {
            return null
        }
    }

    List<BetsyProcess> getPatternProcess(String name) {
        try {
            return [patternProcesses."$name"]
        } catch (MissingPropertyException e) {
            return null
        }
    }


    List<BetsyProcess> getScopeProcess(String name) {
        try {
            return [scopeProcesses."$name"]
        } catch (MissingPropertyException e) {
            return null
        }
    }

}
