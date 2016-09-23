package peal;

import java.util.List;

import peal.identifier.InstanceId;
import peal.identifier.ProcessModelId;
import peal.observer.InstanceState;
import peal.packages.LogPackage;

public interface InstanceService {

    InstanceId start(ProcessModelId processID, List<Variable> variables) throws RuntimeException;

    /**
     * Is applied on the most recent instance of the process
     *
     * @param name the name of the process
     * @return
     */
    public InstanceStateDetailed getStateDetailed(InstanceId instanceId);

    public InstanceState getState(InstanceId instanceId);

    /**
     * Returns log files related to this particular instance
     *
     * @param instanceId
     * @return
     */
    public LogPackage getLogs(InstanceId instanceId);

}
