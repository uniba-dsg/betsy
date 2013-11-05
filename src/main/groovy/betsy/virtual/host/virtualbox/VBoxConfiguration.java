package betsy.virtual.host.virtualbox;

import betsy.Configuration;
import betsy.virtual.host.exceptions.ConfigurationException;

import java.io.File;

/**
 * The {@link VBoxConfiguration} provides access to mandatory options required
 * by virtualized testing using VirtualBox.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public class VBoxConfiguration {

    /**
     * Verify if all mandatory options are set.
     *
     * @throws ConfigurationException thrown if at least one config setting is missing or contains
     *                                invalid values
     */
    public void verify() throws ConfigurationException {
        Configuration.assertDirectory("virtualisation.vbox.home", "Path to virtualbox directory");
    }

    /**
     * Get the File where VirtualBox is installed in.
     *
     * @return directory file
     */
    File getVboxDir() {
        return new File(Configuration.getValueAsString("virtualisation.vbox.home"));
    }

    /**
     * Get the VirtualBox WebService File
     *
     * @return file of VirtualBox WebService
     */
    File getVBoxWebSrv() {
        return new File(getVboxDir(), "VBoxWebSrv.exe");
    }

}
