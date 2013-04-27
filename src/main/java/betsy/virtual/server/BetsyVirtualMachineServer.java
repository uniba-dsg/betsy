package betsy.virtual.server;

import java.io.File;
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

	private final static Logger log = Logger
			.getLogger(BetsyVirtualMachineServer.class);

	public static void main(String[] args) throws Exception {
		try {
			System.out.println("Starting 'Main' execution!");
			BetsyVirtualMachineServer vms = new BetsyVirtualMachineServer();
			vms.init();
			vms.start();
		} catch (Exception exception) {
			log.error("bVMS execution failed:", exception);
		}
	}

	private CommServer com;

	/**
	 * Used to read configuration files, create a trace file, create
	 * ServerSockets, Threads, etc.
	 * 
	 * @param context
	 *            the DaemonContext
	 * @throws DaemonInitException
	 *             on exception
	 */
	public void init() throws DaemonInitException, Exception {
		// Redirect error output
		File logFolder = new File("log");
		logFolder.mkdirs();
		File errFile = new File(logFolder, "betsyVirtualMachineServer.err");
		System.setErr(new PrintStream(new FileOutputStream(errFile), true));

		URL log4jURL = BetsyVirtualMachineServer.class
				.getResource("/virtual/server/log4j.properties");
		PropertyConfigurator.configure(log4jURL);

		log.info("Initializing daemon instance: " + this.hashCode());

		// initializing the TCP communicator
		com = new TCPCommServer(48888);
	}

	public void start() {
		log.info("bVMS: starting acceptor loop");
		while (true) {
			com.waitForConnection();
		}
	}

}