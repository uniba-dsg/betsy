package betsy.virtual.host.virtualbox;

import betsy.config.Configuration;
import betsy.config.ConfigurationException;

import java.nio.file.Path;
import java.nio.file.Paths;

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
        Configuration.assertDirectory("virtual.vbox.home", "Path to virtualbox directory");
    }

    /**
     * Get the File where VirtualBox is installed in.
     *
     * @return directory file
     */
    Path getVboxDir() {
        return Paths.get(Configuration.getValueAsString("virtual.vbox.home"));
    }

    /**
     * Get the VirtualBox WebService File
     *
     * @return file of VirtualBox WebService
     */
    Path getVBoxWebSrv() {
        return getVboxDir().resolve("VBoxWebSrv.exe");
    }

}
