package peal;

import java.util.Set;

import peal.identifier.EngineId;
import peal.observer.EngineState;
import peal.packages.LogPackage;

public interface EngineService {
    Set<EngineId> getSupportedEngines();
    void install(EngineId engineId);
    void uninstall(EngineId engineId);

    ProcessLanguage getSupportedLanguage(EngineId engineId);

    void start(EngineId engineId);
    void stop(EngineId engineId);

    EngineState getState(EngineId engineId);

    LogPackage retrieveLogFiles(EngineId engineId);
}
