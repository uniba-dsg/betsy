package peal;

import java.util.List;

import peal.identifier.EngineId;
import peal.identifier.ProcessModelId;
import peal.observer.ProcessModelState;
import peal.packages.DeploymentPackage;
import peal.packages.ProcessModelPackage;

public interface ProcessModelService {
    DeploymentPackage makeDeployable(EngineId engineId, ProcessModelPackage processModelPackage);
    ProcessModelId deploy(EngineId engineId, DeploymentPackage bpelPackage) throws DeploymentException;
    void undeploy(ProcessModelId processModelId);
    ProcessModelState getState(ProcessModelId processModelId);
    List<ProcessModelId> getDeployedProcessModels();
}
