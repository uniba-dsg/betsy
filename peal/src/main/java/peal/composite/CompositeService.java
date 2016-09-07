package peal.composite;

import peal.DeploymentException;
import peal.identifier.EngineId;
import peal.identifier.ProcessModelId;
import peal.packages.ProcessModelPackage;

public interface CompositeService {

    ProcessModelId makeAvailable(EngineId engineId, ProcessModelPackage processModelPackage) throws DeploymentException;
    void makeUnavailable(ProcessModelId processModelId);

}
