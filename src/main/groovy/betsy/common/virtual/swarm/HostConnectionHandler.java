package betsy.common.virtual.swarm;

import betsy.common.virtual.cbetsy.WorkerTemplate;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christoph Broeker
 * @version 1.0
 *          This class handles the connection to a client.
 */
public class HostConnectionHandler extends Thread {

    private static final Logger LOGGER = Logger.getLogger(ClientConnectionHandler.class);
    private OutputStream outputStream;
    private InputStream inputStream;
    private SocketChannel client;
    private Host host;
    private boolean isReady = false;
    private boolean isFinished = false;
    private List<WorkerTemplate> workerTemplates = new ArrayList<>();

    /**
     *  @param client The socket for the interaction.
     * @param host The host, which starts the connectionHandler.
     */
    public HostConnectionHandler(SocketChannel client, Host host) {
        this.client = client;
        this.host = host;
        try {
            this.outputStream = client.socket().getOutputStream();
            this.inputStream = client.socket().getInputStream();
        } catch (IOException e) {
            host.connectionFailed(this, workerTemplates);
            LOGGER.info("The connection to " +client.socket().getInetAddress()+" failed.");
        }
    }

    @Override
    public void run() {
        try {
            ObjectInputStream objectStream = new ObjectInputStream(inputStream);
            while (isFinished) {
                Object object = objectStream.readObject();
                if(object instanceof Integer){
                    host.addNumber(this, (Integer) object);
                }else if(object instanceof Boolean){
                    isReady = objectStream.readBoolean();
                }
                //TODO: files
            }
        } catch (IOException | ClassNotFoundException e) {
            host.connectionFailed(this, workerTemplates);
            LOGGER.info("The connection to " +client.socket().getInetAddress()+" failed.");
        }
    }

    /**
     * With this method, it is possible to send the arguments for the calibration.
     *
     * @param args The arguments for the calibration.
     */
    public void sendArgs(String... args) {
        try {
            ObjectOutputStream stream = new ObjectOutputStream(outputStream);
            stream.writeObject(args);
        } catch (IOException e) {
            host.connectionFailed(this, workerTemplates);
            LOGGER.info("The connection to " +client.socket().getInetAddress()+" failed.");
        }
    }

    /**
     *
     * With this method, it is possible to send the workerTemplates.
     *
     * @param templates The workerTemplates to send.
     */
    public void sendWorkerTemplates(List<WorkerTemplate> templates) {
        try {
            workerTemplates = templates;
            ObjectOutputStream stream = new ObjectOutputStream(outputStream);
            stream.writeObject(new WorkerTemplateMessage(templates));
            stream.flush();
        } catch (IOException e) {
            host.connectionFailed(this, workerTemplates);
            LOGGER.info("The connection to " +client.socket().getInetAddress()+" failed.");
        }
    }

    /**
     *
     * With this method, it is possible to send the startDataMessage.
     */
    public void sendStartDataMessage() {
        try {
            ObjectOutputStream stream = new ObjectOutputStream(outputStream);
            stream.writeObject(true);
            stream.flush();
        } catch (IOException e) {
            host.connectionFailed(this, workerTemplates);
            LOGGER.info("The connection to " +client.socket().getInetAddress()+" failed.");
        }
    }

    /**
     *
     * With this method, it is possible to send the finishMessage.
     *
     * @param isFinished If the process is finished, it have to be true.
     */
    public void sendFinishMessage(boolean isFinished) {
        this.isFinished = isFinished;
        try {
            ObjectOutputStream stream = new ObjectOutputStream(outputStream);
            stream.writeObject(isFinished);
            stream.flush();
            outputStream.close();
        } catch (IOException e) {
            host.connectionFailed(this, workerTemplates);
            LOGGER.info("The connection to " +client.socket().getInetAddress()+" failed.");
        }
    }

    /**
     *
     * @return Returns true, if the client is ready.
     */
    public boolean isReady() {
        if(isReady){
            isReady = false;
            return true;
        }
        return false;
    }
}
