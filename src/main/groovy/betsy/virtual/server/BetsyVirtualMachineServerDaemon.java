package betsy.virtual.server;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.log4j.Logger;

/**
 * The {@link BetsyVirtualMachineServerDaemon} can be used to start the server.
 * This can be done either by simply invoking the class / jar and running the
 * main method, or by using the {@link BetsyVirtualMachineServerDaemon} inside
 * an init.d start script under a linux environment. It therefore implements the
 * Daemon interface and thus provides methods to start, stop and destroy the
 * daemon.<br>
 * <br>
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class BetsyVirtualMachineServerDaemon implements Daemon, Runnable {

	private static final Logger log = Logger.getLogger(BetsyVirtualMachineServerDaemon.class);

    private volatile Thread worker;
    private BetsyVirtualMachineServer server;

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
		log.info("Initializing daemon instance: " + this.hashCode());
	}

	@Override
	public void start() throws Exception {
        log.info("bVMS daemon: starting");
        server  = new BetsyVirtualMachineServer();

        // initializing the TCP communicator
        this.worker = new Thread(this);
		worker.start();
	}

	@Override
	public void stop() throws Exception {
        log.info("bVMS daemon: stopping");

		/* Close the socket. Forces thread to terminate */
		this.server.stop();

		/* Wait for the main thread to exit and dump a message */
		this.worker.join(5000);
        log.info("bVMS daemon: stopped");
	}

	/**
	 * Runs the thread. The application runs inside an "infinite" loop.
	 */
	@Override
	public void run() {
		try {
            log.info("bVMS daemon: starting");
            server.start();
		} catch (Exception exception) {
			log.error("bVMS daemon: error", exception);
            server.stop();
		}
	}

}