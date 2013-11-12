package betsy.virtual.host.virtualbox.utils

import ant.tasks.AntUtil
import betsy.data.engines.Engine
import betsy.virtual.common.exceptions.CommunicationException
import betsy.virtual.host.ServiceAddress
import betsy.virtual.host.comm.HostTcpClient
import betsy.virtual.common.Constants
import org.apache.log4j.Logger

/**
 * The {@link ServiceValidator} validates a {@link ServiceAddress} and can
 * therefore determine if an {@link Engine} is ready for usage.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public class ServiceValidator {

    private static final Logger log = Logger.getLogger(ServiceValidator.class);

    /**
     * Check whether the {@link Engine} is ready for usage.
     *
     * @param engineServices
     *            services to verify
     * @param secondsToWait
     *            maximum time to wait for the services to become available
     * @return true if all services are ready, false if not
     *
     * @throws MalformedURLException
     *             thrown if one of the {@link ServiceAddress} did contain an
     *             invalid destination.
     * @throws InterruptedException
     *             thrown if waiting on the services was interrupted
     */
    public static boolean isEngineReady(final List<ServiceAddress> engineServices,
                                        final int secondsToWait) throws MalformedURLException,
            InterruptedException {

        AntBuilder ant = AntUtil.builder()
        ant.typedef(name: "httpcontains", classname: "ant.tasks.HttpContains")

        ant.waitfor maxwait: secondsToWait, maxwaitunit: "second", checkevery: 500, {
            for (ServiceAddress address : engineServices) {
                if (address.isRequiringHtmlContent()) {
                    httpcontains url: address, contains: address.getRequiredHtmlContent()
                } else {
                    http url: address
                }
            }
        }
    }

    /**
     * Check whether the betsy endpoint is ready for usage.
     *
     * @param timeoutInMs
     *            maximum time to wait for the server
     * @return true if the server is available
     */
    public static boolean isBetsyServerReady(final int timeoutInMs) {
        HostTcpClient cc;
        try {
            cc = new HostTcpClient(Constants.SERVER_HOSTNAME, Constants.SERVER_PORT)
            cc.reconnect(timeoutInMs);
            return cc.isConnectionAlive();
        } catch (IOException | CommunicationException exception) {
            // ignore
            log.debug("Exception in bVMS availability test: ", exception);
        } finally {
            if (cc != null) {
                cc.close()
            }
        }
        return false;
    }

}
