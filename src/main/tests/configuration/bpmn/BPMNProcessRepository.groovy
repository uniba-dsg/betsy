package configuration.bpmn

import betsy.bpmn.model.BPMNProcess
import betsy.common.repositories.Repository

import java.lang.reflect.Field

import static BPMNGatewayProcesses.GATEWAYS
import static BPMNEventProcesses.EVENTS
import static BPMNMiscProcesses.MISCS

class BPMNProcessRepository {
    private Repository<BPMNProcess> repo = new Repository<>()

    public BPMNProcessRepository(){
        repo.put("ALL",
                GATEWAYS +
                BPMNTaskProcesses.TASKS +
                EVENTS +
                BPMNSubprocessProcesses.SUBPROCESSES +
                MISCS
                as List<BPMNProcess>)

        Field[] fields = [
            BPMNGatewayProcesses.class.declaredFields +
            BPMNTaskProcesses.class.declaredFields +
            BPMNEventProcesses.class.declaredFields +
            BPMNSubprocessProcesses.class.declaredFields +
            BPMNMiscProcesses.class.declaredFields
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
