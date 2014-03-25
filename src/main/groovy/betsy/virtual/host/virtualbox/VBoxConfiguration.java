package betsy.virtual.host.virtualbox;

import betsy.config.Configuration;
import betsy.config.ConfigurationException;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang.SystemUtils;

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
    	if(SystemUtils.IS_OS_WINDOWS) {
    		return getVboxDir().resolve("VBoxWebSrv.exe");
    	}else if(SystemUtils.IS_OS_UNIX) {
    		return getVboxDir().resolve("vboxwebsrv");
    	}else {
    		throw new IllegalStateException("Incompatible OS running");
    	}
    }
    
    /**
     * Get the VirtualBox Manage File
     *
     * @return file of VirtualBox Manage
     */
    public Path getVBoxManage() {
    	if(SystemUtils.IS_OS_WINDOWS) {
    		return getVboxDir().resolve("VBoxManage.exe");
    	}else if(SystemUtils.IS_OS_UNIX) {
    		return getVboxDir().resolve("VBoxManage");
    	}else{
    		throw new IllegalStateException("Incompatible OS running");
    	}
    }

}
