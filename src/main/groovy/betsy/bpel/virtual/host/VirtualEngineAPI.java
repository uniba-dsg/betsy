package betsy.bpel.virtual.host;

import betsy.bpel.model.BPELProcess;
import betsy.bpel.virtual.common.messages.collect_log_files.LogFilesRequest;
import betsy.bpel.virtual.common.messages.deploy.DeployRequest;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * The {@link VirtualEngineAPI} offers methods that are required if dealing
 * with virtualized testing using betsy.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public interface VirtualEngineAPI {

    /**
     * Get the name of the VirtualMachine that is used by this engines.<br>
     * The name shall be the prefix 'betsy-' concatenated with the name of the
     * virtualized engine.
     *
     * @return name of the engine's VirtualMachine
     */
    String getVirtualMachineName();

    /**
     * Get every {@link ServiceAddress} that must be available for the engine
     * before testing it.
     *
     * @return the list of required services for the engine
     */
    List<ServiceAddress> getVerifiableServiceAddresses();

    /**
     * Get all ports that are required by the engine to be testable.
     *
     * @return list of required ports
     */
    Set<Integer> getRequiredPorts();

    DeployRequest buildDeployRequest(BPELProcess process) throws IOException;

    LogFilesRequest buildLogFilesRequest();

}
