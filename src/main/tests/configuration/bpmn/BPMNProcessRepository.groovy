package configuration.bpmn

import betsy.bpmn.model.BPMNProcess
import betsy.common.repositories.Repository

import java.lang.reflect.Field

import static GatewayProcesses.GATEWAYS
import static EventProcesses.EVENTS
import static MiscProcesses.MISCS

class BPMNProcessRepository {
    private Repository<BPMNProcess> repo = new Repository<>()

    public BPMNProcessRepository(){
        repo.put("ALL",
                GATEWAYS +
                TaskProcesses.TASKS +
                EVENTS +
                SubProcesses.SUB_PROCESSES +
                MISCS +
                ErrorProcesses.ERRORS
                as List<BPMNProcess>)

        Field[] fields = [
            GatewayProcesses.class.declaredFields +
            TaskProcesses.class.declaredFields +
            EventProcesses.class.declaredFields +
            SubProcesses.class.declaredFields +
            MiscProcesses.class.declaredFields
        ].flatten()

        fields.each { Field f ->
            // adds only the static fields that are lists (groups)
            if (f.type == List.class) {
                // f.get(null) returns the value of the field. the null parameter is ignored as the field is static.
                repo.put(f.name, f.get(null) as List<BPMNProcess>)
            }
        }

        // insert every process into the map
        repo.getByName("ALL").each { BPMNProcess process ->
            repo.put(process.name, [process])
        }
    }

    public List<BPMNProcess> getByName(String name) {
        return repo.getByName(name);
    }

    public List<BPMNProcess> getByNames(String[] names) {
        return repo.getByNames(names);
    }

    public List<String> getNames() {
        return repo.getNames();
    }
}
