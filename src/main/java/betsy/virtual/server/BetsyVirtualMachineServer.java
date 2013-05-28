package betsy.virtual.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;

import org.apache.commons.daemon.DaemonInitException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import betsy.virtual.server.comm.CommServer;
import betsy.virtual.server.comm.TCPCommServer;

/**
 * The {@link BetsyVirtualMachineServer} can be used to start the server.<br>
 * <br>
 * The {@link CommServer} gets initialized and waits for new connections in a
 * permanent loop.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class BetsyVirtualMachineServer {

    public static void main(String[] args) throws Exception {
        try {
            System.out.println("Starting betsy Virtual Machine Server (bVMS)");
            new BetsyVirtualMachineServer().start();
        } catch (Exception exception) {
            log.error("bVMS execution failed", exception);
        }
    }

    private final static Logger log = Logger
			.getLogger(BetsyVirtualMachineServer.class);

    private volatile boolean keepRunning = true;

	public BetsyVirtualMachineServer() throws Exception {
        log.info("bVMS: initializing");
        logStandardErrorToFile();
    }

    private void logStandardErrorToFile() throws FileNotFoundException {
        // Redirect error output
        File logFolder = new File("log");
        if(!logFolder.mkdirs()) {
            throw new IllegalStateException("Could not create folder " + logFolder.getPath());
        }
        File errFile = new File(logFolder, "betsyVirtualMachineServer.err");
        System.setErr(new PrintStream(new FileOutputStream(errFile), true));

        URL log4jURL = BetsyVirtualMachineServer.class
                .getResource("/virtual/server/log4j.properties");
        PropertyConfigurator.configure(log4jURL);
    }

    public void start() throws Exception {
		log.info("bVMS: starting");
        try(CommServer com = new TCPCommServer(48888)) {
            while (keepRunning) {
                com.handleNextConnection();
            }
        }
	}

    public void stop() {
        log.info("bVMS: stopping");
        keepRunning = false;
    }

}