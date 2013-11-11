package betsy.virtual.server;

import betsy.virtual.common.Constants;
import betsy.virtual.server.comm.VirtualMachineTcpServer;
import betsy.virtual.server.logic.ProtocolImpl;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;

/**
 * The {@link BetsyVirtualMachineServer} can be used to start the server.<br>
 * <br>
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public class BetsyVirtualMachineServer {

    private VirtualMachineTcpServer server;

    public static void main(String[] args) throws Exception {
        try {
            System.out.println("Starting betsy Virtual Machine Server (bVMS)");
            new BetsyVirtualMachineServer().start();
        } catch (Exception exception) {
            log.error("bVMS execution failed", exception);
        }
    }

    private final static Logger log = Logger.getLogger(BetsyVirtualMachineServer.class);

    public BetsyVirtualMachineServer() throws Exception {
        log.info("bVMS: initializing");
        logStandardErrorToFile();
    }

    private void logStandardErrorToFile() throws FileNotFoundException {
        URL log4jURL = BetsyVirtualMachineServer.class.getResource("/virtual/server/log4j.properties");
        PropertyConfigurator.configure(log4jURL);
    }

    public void start() throws Exception {
        log.info("bVMS: starting");
        server = new VirtualMachineTcpServer(Constants.SERVER_PORT, new ProtocolImpl());
        server.start();
    }

    public void stop() {
        log.info("bVMS: stopping");
        server.shutdown();
        server.close();
    }

}