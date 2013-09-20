package configuration.engines

import betsy.data.engines.Engine
import betsy.data.engines.LocalEngines
import betsy.virtual.host.engines.VirtualizedEngines


/**
 * CAPITAL LETTERS for GROUPS of engines, lower case letters for engines
 */
class EngineRepository {

    private Map<String, Engine[]> repository = [:]

    public EngineRepository() {
        repository.put("LOCALS", LocalEngines.availableEngines() as Engine[])
        repository.put("VMS", VirtualizedEngines.availableEngines() as Engine[])
        repository.put("ALL", repository.get("LOCALS") + repository.get("VMS"))

        // insert every engine into the map
        repository.get("ALL").each { Engine engine ->
            repository.put(engine.name, [engine] as Engine[])
        }
    }

    public List<Engine> getByName(String name) {
        String trimmedName = name.trim()
        String key = repository.keySet().find { it.toUpperCase() == trimmedName.toUpperCase() }
        if (key == null) {
            key = trimmedName
        }

        List<Engine> result = repository.get(key)

        if (result == null) {
            throw new IllegalArgumentException("Name '${trimmedName}' does not exist in repository.")
        }

        return result
    }

    public List<Engine> getByNames(String[] names) {
        List<Engine> result = []

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
