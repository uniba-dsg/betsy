package configuration.cli

import betsy.data.engines.Engine
import betsy.data.engines.LocalEngines
import betsy.virtual.host.engines.VirtualizedEngines

class EngineParser {

    /**
     * Name of the engines, separated by comma (,), case does not matter, duplicates are filtered out
     *
     * Example:
     * ode,BPELG,OpenESB
     */
    String[] args

    List<Engine> parse() {
        return parseWithDuplicates().unique()
    }

    private List<Engine> parseWithDuplicates() {
        if (args.length == 0) {
            // local engines are default
            return LocalEngines.availableEngines()
        }

        if ("all" == args[0].toLowerCase()) {
            VirtualizedEngines.availableEngines() + LocalEngines.availableEngines()
        } else if ("vms" == args[0].toLowerCase()) {
            VirtualizedEngines.availableEngines()
        } else if ("locals" == args[0].toLowerCase()) {
            LocalEngines.availableEngines()
        } else {
            List<String> engineNames = args[0].toLowerCase().split(",") as List<String>
            List<Engine> all = []

            for (String name : engineNames) {
                try {
                    all.add(LocalEngines.build(name))
                    continue
                } catch (IllegalArgumentException ignore) {
                    //ignore
                }

                try {
                    all.add(VirtualizedEngines.build(name))
                    continue
                } catch (IllegalArgumentException ignore) {
                    //ignore
                }

                throw new IllegalArgumentException("passed engine '${name}' does not exist, neither as local, nor as virtualized engine")
            }
            return all
        }
    }

}
