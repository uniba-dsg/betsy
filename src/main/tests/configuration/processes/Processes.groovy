package configuration.processes

import betsy.data.Process

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

    public List<Process> get(String name){
        if("ALL" == name){
            return ALL
        } else if("NOT_DEPLOYABLE" == name) {
            return NOT_DEPLOYABLE
        }

        List<Process> result = getBasicProcess(name)
        if(result == null){
            result = getStructuredProcess(name)
            if(result == null){
                result = getScopeProcess(name)
                if(result == null){
                    result = ALL.findAll( {it.bpelFileNameWithoutExtension == name})
                    if(result.isEmpty()) {
                        throw new IllegalArgumentException("Process ${name} does not exist")
                    }
                }
            }
        }
        return result
    }

    List<Process> getBasicProcess(String name){
        try{
            return [basicActivityProcesses."$name"]
        } catch(MissingPropertyException e){
            return null
        }
    }

    List<Process> getStructuredProcess(String name){
        try{
            return [structuredActivityProcesses."$name"]
        } catch(MissingPropertyException e){
            return null
        }
    }


    List<Process> getScopeProcess(String name){
        try{
            return [scopeProcesses."$name"]
        } catch(MissingPropertyException e){
            return null
        }
    }

}
