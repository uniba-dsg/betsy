package betsy.virtual.host.virtualbox;

import ant.tasks.AntUtil;
import betsy.Configuration;
import betsy.virtual.host.virtualbox.utils.InputStreamLogger;
import groovy.util.AntBuilder;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@link VBoxWebService} offers methods to start and stop the VBoxWebSrv
 * executable of VirtualBox. This service is mandatory for the usage of the
 * JAXWS that is used by betsy to control the virtual machines.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public class VBoxWebService {

    private static final Logger log = Logger.getLogger(VBoxWebService.class);

    private final AntBuilder ant = AntUtil.builder();
    private final VBoxConfiguration vBoxConfiguration = new VBoxConfiguration();

    private Process vboxServiceProcess;

    /**
     * Installs the VBoxWebSrv by starting it and making sure it is terminated
     * if the current application is being closed again. A ShutdownHook is used
     * to shutdown the process.
     *
     * @throws IOException thrown if starting the VBoxWebSrv failed
     */
    public void startAndInstall() throws IOException {
        this.start();
        // install a ShutdownHook to terminate the service if the application
        // is being closed
        Thread shutdownThread = new Thread(new Runnable() {
            @Override
            public void run() {
                VBoxWebService.this.stop();
            }
        });
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }

    /**
     * Start the VBoxWebSrv and wait 3 seconds to assure it is running. The
     * VBoxWebSrv is started with a disabled authentication library.
     *
     * @throws IOException thrown if starting the VBoxWebSrv failed
     */
    public void start() throws IOException {

        // start VBoxService
        Runtime r = Runtime.getRuntime();
        String[] startArgs = new String[]{vBoxConfiguration.getVBoxWebSrv().getAbsolutePath(), "-A", "null", "-t", "0"};
        log.debug("Starting '" + Arrays.toString(startArgs) + "'");
        vboxServiceProcess = r.exec(startArgs);

		/*
         * By default, the created subprocess does not have its own terminal or
		 * console. All its standard I/O operations will be redirected to the
		 * parent process. Because some native platforms only provide limited
		 * buffer size for standard input and output streams, failure to
		 * promptly write the input stream or read the output stream of the
		 * subprocess may cause the subprocess to block, or even deadlock.
		 * 
		 * --> Deadlock happened on Windows and OS X on each test run!
		 */
        InputStreamLogger inputStream = new InputStreamLogger(vboxServiceProcess.getInputStream(), "INPUT_STREAM");
        inputStream.start();

        InputStreamLogger errorStream = new InputStreamLogger(vboxServiceProcess.getErrorStream(), "ERROR_STREAM");
        errorStream.start();

        // give the webSrv some time to start
        Integer startHaltDuration = Configuration.getValueAsInteger("virtual.vbox.websrv.wait");

        log.debug("Waiting " + startHaltDuration + " seconds for the VBoxWebSrv to start...");

        Map<String, Object> map = new HashMap<>();
        map.put("seconds", startHaltDuration);
        ant.invokeMethod("sleep", map);

        log.debug("...VBoxWebSrv started!");
    }

    /**
     * Destroy the process VBoxWebSrv is running in
     */
    public void stop() {
        log.debug("Stopping VBoxWebSrv...");
        if (vboxServiceProcess != null) {
            vboxServiceProcess.destroy();
            vboxServiceProcess = null;
        }
    }

}
