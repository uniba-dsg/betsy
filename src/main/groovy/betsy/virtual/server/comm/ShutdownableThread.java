package betsy.virtual.server.comm;

import org.apache.log4j.Logger;

public class ShutdownableThread extends Thread {

    private final static Logger log = Logger.getLogger(ShutdownableThread.class);

    protected volatile boolean isRunning = true;

    public void shutdown() {
        log.info("Shutting down thread " + this.getName());
        isRunning = false;
    }
}
