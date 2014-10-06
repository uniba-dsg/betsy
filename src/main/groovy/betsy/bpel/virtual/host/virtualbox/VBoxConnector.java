package betsy.bpel.virtual.host.virtualbox;

import betsy.config.Configuration;
import org.apache.log4j.Logger;
import org.virtualbox_4_2.IVirtualBox;
import org.virtualbox_4_2.VirtualBoxManager;

/**
 * The {@link VBoxConnector} establishes the connection between betsy and
 * VirtualBox. It also provides objects that should be created only once per
 * connection, such as the {@link VirtualBoxManager} or the
 * {@link VBoxApplianceImporter}.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
class VBoxConnector {

    private static final Logger log = Logger.getLogger(VBoxConnector.class);

    private final VirtualBoxManager vBoxManager;

    private IVirtualBox vBox = null;
    private VBoxApplianceImporter vBoxImporter = null;

    private boolean isConnected = false;

    public VBoxConnector() {
        this.vBoxManager = VirtualBoxManager.createInstance(null);
    }

    private boolean isNotConnected() {
        return !isConnected;
    }

    public VirtualBoxManager getVBoxManager() {
        return this.vBoxManager;
    }

    public VBoxApplianceImporter getVBoxImporter() {
        return this.vBoxImporter;
    }

    /**
     * The LinkUpDelay specifies how many milliseconds the network adapter of
     * the guest machine remains silent until he resumes his work stored in his
     * networkstack.
     *
     * @param milliSeconds timeout to set in ms
     */
    private void setLinkUpDelay(int milliSeconds) {
        int existingDelay = -1;
        try {
            existingDelay = Integer.parseInt(vBox.getExtraData("VBoxInternal"
                    + "/Devices/e1000/0/Config/LinkUpDelay"));
        } catch (NumberFormatException exception) {
            // ignore, usually was an empty value
        }

        if (existingDelay != milliSeconds) {
            log.info("Disabling LinkUpDelay for this VirtualBox instance...");
            vBox.setExtraData(
                    "VBoxInternal/Devices/e1000/0/Config/LinkUpDelay",
                    Integer.toString(milliSeconds));
        }
    }

    /**
     * Establish the connection to the VirtualBox web service of not already
     * done.<br>
     * Creates also other important objects as the {@link VirtualBoxManager} and
     * a {@link VBoxApplianceImporter}.
     *
     * @return VirtualBox interface object
     */
    public IVirtualBox connect() {
        if (isNotConnected()) {
            log.trace("Connecting in VBoxConnector");
            String host = Configuration.get("virtual.vbox.websrv.host");
            String port = Configuration.get("virtual.vbox.websrv.port");
            String username = Configuration.get("virtual.vbox.websrv.user");
            String password = Configuration.get("virtual.vbox.websrv.password");

            try {
                this.vBoxManager.connect(host + ":" + port, username, password);
                isConnected = true;

                vBox = vBoxManager.getVBox();
                log.debug(String.format("Using VirtualBox version '%s'",
                        vBox.getVersion()));
                vBoxImporter = new VBoxApplianceImporter(vBox);

                // no delay, continue with network usage immediately
                this.setLinkUpDelay(0);
            } catch (org.virtualbox_4_2.VBoxException exception) {
                if (exception.getMessage().contains(
                        "reasonText argument for "
                                + "createFault was passed NULL")) {
                    log.error("Connecting to vboxWebSrv failed, please deactivate"
                            + " the websrvauthlibrary manually: 'VBoxManage.exe "
                            + "setproperty websrvauthlibrary null'");
                    throw exception;
                } else if (exception.getMessage().equals(
                        "HTTP transport error: java.net.ConnectException: "
                                + "Connection refused")) {
                    log.error("VBoxWebSrv does not seem to be running on the specified address!");
                    throw exception;
                } else {
                    // unknown exception, can't solve situation
                    log.error("Unknown exception while connecting to vboxWebSrv");
                    throw exception;
                }
            }
        }
        return vBox;
    }

}
