package de.uniba.wiai.dsg.betsy.virtual.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.uniba.wiai.dsg.betsy.virtual.common.comm.CommServer;
import de.uniba.wiai.dsg.betsy.virtual.server.comm.TCPCommServer;

/**
 * The {@link BetsyVirtualMachineServer} can be used to start the server. This
 * can be done either by simply invoking the class / jar and running the main
 * method, or by using the {@link BetsyVirtualMachineServer} inside an init.d
 * start script under a linux environment. It therefore implements the Daemon
 * interface and thus provides methods to start, stop and destroy the daemon.<br>
 * <br>
 * The {@link CommServer} gets initialized and waits for new connections in a
 * permanent loop.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class BetsyVirtualMachineServer implements Daemon, Runnable {

	public static void main(String[] args) throws Exception {
		System.out.println("Starting 'Main' execution!");
		BetsyVirtualMachineServer vms = new BetsyVirtualMachineServer();
		vms.init(null);
		vms.start();
		System.out.println("Finished 'Main' execution!");
	}

	private volatile transient CommServer com;
	private volatile transient boolean keepRunning = true;

	/**
	 * The thread that does the actual work.
	 */
	private volatile transient Thread worker;

	private volatile transient Logger log = Logger.getLogger(getClass());

	public BetsyVirtualMachineServer() {
		// nothing yet
	}

	@Override
	public void destroy() {
		log.info("Destroying daemon instance: " + this.hashCode());
	}

	/**
	 * Used to read configuration files, create a trace file, create
	 * ServerSockets, Threads, etc.
	 * 
	 * @param context
	 *            the DaemonContext
	 * @throws DaemonInitException
	 *             on exception
	 */
	@Override
	public void init(DaemonContext context) throws DaemonInitException,
			Exception {
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
		this.worker = new Thread(this);
	}

	@Override
	public void start() throws Exception {
		worker.start();
		log.info("Starting daemon...");
	}

	@Override
	public void stop() throws Exception {
		log.info("Stopping daemon...");

		this.keepRunning = false;

		/* Close the socket. Forces thread to terminate */
		this.com.close();

		/* Wait for the main thread to exit and dump a message */
		this.worker.join(5000);
		log.info("betsy-Daemon: stopped");
	}

	/**
	 * Runs the thread. The application runs inside an "infinite" loop.
	 */
	@Override
	public void run() {
		log.info("betsy-Daemon: started acceptor loop");
		while (keepRunning) {
			com.waitForConnection();
		}
		log.info("betsy-Daemon: exiting acceptor loop");
	}

}