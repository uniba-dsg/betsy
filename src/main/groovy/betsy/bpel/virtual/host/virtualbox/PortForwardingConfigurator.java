package betsy.bpel.virtual.host.virtualbox;

import java.util.List;
import java.util.Set;

import betsy.bpel.virtual.common.Constants;
import betsy.bpel.virtual.host.VirtualBoxException;
import betsy.bpel.virtual.host.exceptions.vm.PortRedirectException;
import betsy.bpel.virtual.host.virtualbox.utils.port.PortUsageException;
import betsy.bpel.virtual.host.virtualbox.utils.port.PortVerifier;
import betsy.common.timeouts.timeout.TimeoutRepository;
import org.apache.log4j.Logger;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.INATEngine;
import org.virtualbox_4_2.INetworkAdapter;
import org.virtualbox_4_2.NATProtocol;

public class PortForwardingConfigurator {

    private static final Logger log = Logger.getLogger(PortForwardingConfigurator.class);

    private final IMachine machine;

    public PortForwardingConfigurator(IMachine machine) {
        this.machine = machine;
    }

    /**
     * Setup port forwarding for all the given forwardingPorts. The port on the
     * host is always equal to the port on the guest system. All existing
     * redirections are deleted before applying the new redirections.<br>
     * <br>
     * This method is using the JAXWS methods which do have a severe issue until
     * version 4.2.11 if running VirtualBox on OS X.
     * (https://www.virtualbox.org/ticket/11635)
     *
     * @param forwardingPorts all ports to create a forwarding for.
     * @throws betsy.bpel.virtual.host.exceptions.vm.PortRedirectException thrown if redirection could not be created
     */
    public void applyPortForwarding(final Set<Integer> forwardingPorts) throws VirtualBoxException {
        if (!isAlreadyRedirected(forwardingPorts)) {
            log.debug("Applying new port redirects...");

            try {
                PortVerifier.failForUsedPorts(forwardingPorts);
            } catch (PortUsageException e) {
                throw new VirtualBoxException("ports could not be forwarded", e);
            }

            // remove old redirections first
            clearPortForwarding();

            // add bVMS port if not yet contained
            forwardingPorts.add(Constants.SERVER_PORT);

            INATEngine natEngine = getNATEngine();
            for (Integer port : forwardingPorts) {
                // assuming every service uses TCP, which is true for HTTP
                natEngine.addRedirect("", NATProtocol.TCP, "", port, "", port);
            }

            long timeout = TimeoutRepository.getTimeout("PortForwardingConfigurator.applyPortForwarding").getTimeoutInMs();
            long start = -System.currentTimeMillis();

            while (natEngine.getRedirects().size() != forwardingPorts.size()) {
                if (System.currentTimeMillis() + start > timeout) {
                    throw new PortRedirectException("Could not set redirected "
                            + "ports within " + TimeoutRepository.getTimeout("PortForwardingConfigurator.applyPortForwarding").getTimeoutInMs() + "s");
                }
                try {
                    Thread.sleep(TimeoutRepository.getTimeout("PortForwardingConfigurator.applyPortForwarding").getTimeToRepetitionInMs());
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        } else {
            log.trace("All ports are already redirected, skip.");
        }
    }

    private void clearPortForwarding() throws PortRedirectException {
        log.debug("Deleting all existing port redirections");
        INATEngine natEngine = getNATEngine();
        for (String redirect : natEngine.getRedirects()) {
            String[] rds = redirect.split(",");
            String redirectName = rds[0];
            natEngine.removeRedirect(redirectName);
        }

        long start = -System.currentTimeMillis();

        while (natEngine.getRedirects().size() != 0) {
            if (System.currentTimeMillis() + start > TimeoutRepository.getTimeout("PortForwardingConfigurator.clearPortForwarding").getTimeoutInMs()) {
                throw new PortRedirectException("Could not delete all "
                        + "redirected ports within " + TimeoutRepository.getTimeout("PortForwardingConfigurator.clearPortForwarding").getTimeoutInMs() + "s");
            }
            try {
                Thread.sleep(TimeoutRepository.getTimeout("PortForwardingConfigurator.clearPortForwarding").getTimeToRepetitionInMs());
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    private boolean isAlreadyRedirected(final Set<Integer> forwardingPorts) {
        // get existing redirects
        INATEngine natEngine = getNATEngine();
        List<String> redirects = natEngine.getRedirects();

        int matchingForwards = 0;

        for (String redirect : redirects) {
            // resolve host and guest port
            String[] rds = redirect.split(",");
            int hostPort = Integer.parseInt(rds[3]);
            int guestPort = Integer.parseInt(rds[5]);
            // verify both are equal, ignoring any other manually created
            // redirection
            if (hostPort == guestPort && forwardingPorts.contains(hostPort)) {
                // is ok, increase count
                matchingForwards++;
            }
        }

        return matchingForwards == forwardingPorts.size();
    }

    private INATEngine getNATEngine() {
        long idForFirstAdapater = 0l; // first adapter is always the NAT adapter
        INetworkAdapter networkAdapter = machine.getNetworkAdapter(idForFirstAdapater);
        return networkAdapter.getNATEngine();
    }

}
