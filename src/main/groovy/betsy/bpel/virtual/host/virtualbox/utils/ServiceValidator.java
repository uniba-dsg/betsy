package betsy.bpel.virtual.host.virtualbox.utils;

import betsy.bpel.engines.AbstractBPELEngine;
import betsy.bpel.virtual.common.Constants;
import betsy.bpel.virtual.host.ServiceAddress;
import betsy.bpel.virtual.host.comm.HostTcpClient;
import betsy.common.tasks.WaitTasks;

import java.util.List;

/**
 * The {@link ServiceValidator} validates a {@link ServiceAddress} and can
 * therefore determine if an {@link AbstractBPELEngine} is ready for usage.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public final class ServiceValidator {

    private ServiceValidator() {}

    /**
     * Check whether the {@link AbstractBPELEngine} is ready for usage.
     *
     * @param engineServices services to verify
     * @param secondsToWait  maximum time to wait for the services to become available
     * @return true if all services are ready, false if not
     */
    public static boolean isEngineReady(final List<ServiceAddress> engineServices,
                                        final int secondsToWait) {

        //TODO how to handle wait if it never was true?
        for (ServiceAddress address : engineServices) {
            if (address.isRequiringHtmlContent()) {
                WaitTasks.waitForContentInUrl(secondsToWait * 1000, 500, address.getAddress(), address.getRequiredHtmlContent());
            } else {
                WaitTasks.waitForAvailabilityOfUrl(secondsToWait * 1000, 500, address.getAddress());
            }
        }
        return true;

    }

    /**
     * Check whether the betsy endpoint is ready for usage.
     *
     * @param timeoutInMs maximum time to wait for the server
     * @return true if the server is available
     */
    public static boolean isBetsyServerReady(final int timeoutInMs) {
        try (HostTcpClient cc = new HostTcpClient(Constants.SERVER_HOSTNAME, Constants.SERVER_PORT)) {
            return cc.isReachable(timeoutInMs);
        } catch (Exception e) {
            throw new RuntimeException("Error during BVMS availability check", e);
        }
    }

}
