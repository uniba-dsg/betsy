package configuration.processes

import betsy.data.Process
import betsy.data.assertions.ExitAssertion

class Processes {

    BasicActivityProcesses basicActivityProcesses = new BasicActivityProcesses()

    StructuredActivityProcesses structuredActivityProcesses = new StructuredActivityProcesses()

    ScopeProcesses scopeProcesses = new ScopeProcesses()

    PatternProcesses patternProcesses = new PatternProcesses()

    public final List<Process> ALL = [
            structuredActivityProcesses.STRUCTURED_ACTIVITIES,
            basicActivityProcesses.BASIC_ACTIVITIES,
            scopeProcesses.SCOPES,
            patternProcesses.CONTROL_FLOW_PATTERNS
    ].flatten() as List<Process>

    public final List<Process> NOT_DEPLOYABLE = ALL.findAll { process ->
        process.testCases.any { it.notDeployable }
    }

    public final List<Process> WITH_EXIT_ASSERTION = ALL.findAll { process ->
        process.testCases.any { it.testSteps.any { it.assertions.any { it instanceof ExitAssertion }} }
    }

    public List<Process> get(String name) {
        if ("ALL" == name.toUpperCase()) {
            return ALL
        } else if ("NOT_DEPLOYABLE" == name.toUpperCase()) {
            return NOT_DEPLOYABLE
        } else if ("WITH_EXIT_ASSERTION" == name.toUpperCase()){
            return WITH_EXIT_ASSERTION
        }

        List<Process> result = getBasicProcess(name)
        if (result == null) {
            result = getStructuredProcess(name)
            if (result == null) {
                result = getScopeProcess(name)
                if (result == null) {
                    result = getPatternProcess(name)
                    if (result == null) {
                        result = ALL.findAll({ it.bpelFileNameWithoutExtension == name })
                        if (result.isEmpty()) {
                            throw new IllegalArgumentException("Process ${name} does not exist")
                        }
                    }
                }
            }
        }
        return result
    }

    List<Process> getBasicProcess(String name) {
        try {
            return [basicActivityProcesses."$name"]
        } catch (MissingPropertyException e) {
            return null
        }
    }

    List<Process> getStructuredProcess(String name) {
        try {
            return [structuredActivityProcesses."$name"]
        } catch (MissingPropertyException e) {
            return null
        }
    }

    List<Process> getPatternProcess(String name) {
        try {
            return [patternProcesses."$name"]
        } catch (MissingPropertyException e) {
            return null
        }
    }


    List<Process> getScopeProcess(String name) {
        try {
            return [scopeProcesses."$name"]
        } catch (MissingPropertyException e) {
            return null
        }
    }

}
