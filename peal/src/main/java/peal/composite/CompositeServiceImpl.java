package peal.composite;

import java.util.Objects;

import peal.DeploymentException;
import peal.EngineService;
import peal.ProcessModelService;
import peal.identifier.EngineId;
import peal.identifier.ProcessModelId;
import peal.observer.EngineState;
import peal.packages.DeploymentPackage;
import peal.packages.ProcessModelPackage;

public class CompositeServiceImpl implements CompositeService{

    private final EngineService engineService;
    private final ProcessModelService processModelService;

    public CompositeServiceImpl(EngineService engineService, ProcessModelService processModelService) {
        this.engineService = Objects.requireNonNull(engineService);
        this.processModelService = Objects.requireNonNull(processModelService);
    }

    @Override
    public ProcessModelId makeAvailable(EngineId engineId, ProcessModelPackage processModelPackage)
            throws DeploymentException {
        if(engineService.getState(engineId) == EngineState.NOT_INSTALLED) {
            engineService.install(engineId);
        }
        if(engineService.getState(engineId) == EngineState.INSTALLED) {
            engineService.start(engineId);
        }
        DeploymentPackage deploymentPackage = processModelService.makeDeployable(engineId, processModelPackage);
        return processModelService.deploy(engineId, deploymentPackage);
    }

    @Override
    public void makeUnavailable(ProcessModelId processModelId) {
        processModelService.undeploy(processModelId);
        if(processModelService.getDeployedProcessModels().isEmpty()) {
            engineService.stop(processModelId);
            engineService.uninstall(processModelId);
        }
    }
}
