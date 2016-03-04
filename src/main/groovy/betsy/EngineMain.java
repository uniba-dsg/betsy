package betsy;

import betsy.bpel.repositories.BPELEngineRepository;
import betsy.bpmn.repositories.BPMNEngineRepository;
import betsy.common.engines.EngineLifecycle;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EngineMain {

    private static Map<String, Consumer<EngineLifecycle>> commands = new HashMap<>();

    public static void main(String... args) {
        commands.put("install", EngineLifecycle::install);
        commands.put("uninstall", EngineLifecycle::uninstall);
        commands.put("isInstalled", EngineLifecycle::isInstalled);
        commands.put("startup", EngineLifecycle::startup);
        commands.put("shutdown", EngineLifecycle::shutdown);
        commands.put("isRunning", EngineLifecycle::isRunning);

        if (args.length != 2 || args[0] == null || args[1] == null) {
            usage();
            return;
        }


        String commandName = args[1];

        Consumer<EngineLifecycle> command = commands.get(commandName);
        if (command == null) {
            usage();
            return;
        }

        String engineName = args[0];

        Optional<EngineLifecycle> engine = getEngines().stream().filter((e) -> e.toString().equalsIgnoreCase(engineName)).findFirst();
        if (!engine.isPresent()) {
            usage();
            return;
        }

        System.out.format("Executing command %s on engine %s", commandName, engineName);

        command.accept(engine.get());
    }

    private static List<EngineLifecycle> getEngines() {
        final List<EngineLifecycle> bpelEngines = new BPELEngineRepository().getByName("ALL").stream().collect(Collectors.toList());
        final List<EngineLifecycle> bpmnEngines = new BPMNEngineRepository().getByName("ALL").stream().collect(Collectors.toList());
        final List<EngineLifecycle> engines = new LinkedList<>();
        engines.addAll(bpelEngines);
        engines.addAll(bpmnEngines);

        return engines;
    }

    private static void usage() {
        System.out.println("betsy engine ENGINE COMMAND");
        System.out.println("");
        System.out.println("ENGINE: " + getEngines().stream().map(Object::toString).collect(Collectors.joining(", ")));
        System.out.println("COMMAND: " + String.join(", ", commands.keySet()));
    }

}
