package configuration.engines

import betsy.data.engines.Engine
import betsy.data.engines.LocalEngines
import betsy.virtual.host.engines.VirtualizedEngines
import configuration.util.Repository


/**
 * CAPITAL LETTERS for GROUPS of engines, lower case letters for engines
 */
class EngineRepository {

    private Repository<Engine> repo = new Repository<>();

    public EngineRepository() {
        List<Engine> locals = LocalEngines.availableEngines()
        List<Engine> vms = VirtualizedEngines.availableEngines().collect { it as Engine }
        List<Engine> all = [locals, vms].flatten()

        repo.put("ALL", all)
        repo.put("LOCALS", locals)
        repo.put("VMS", vms)

        // insert every engine into the map
        for (Engine engine : repo.getByName("ALL")) {
            repo.put(engine.name, [engine])
        }
    }

    public List<Engine> getByName(String name) {
        return repo.getByName(name);
    }

    public List<Engine> getByNames(String[] names) {
        return repo.getByNames(names);
    }

    public List<String> getNames() {
        return repo.getNames();
    }

}
