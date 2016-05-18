package betsy.common.virtual.swarm;

import betsy.common.virtual.cbetsy.WorkerTemplate;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Christoph Broeker
 * @version 1.0
 *          This class handles the connection to a client.
 */
public class ClientConnectionHandler extends Thread {

    private static final Logger LOGGER = Logger.getLogger(ClientConnectionHandler.class);
    private OutputStream outputStream;
    private InputStream inputStream;
    private Socket host;
    private ArrayList<WorkerTemplate> templates;
    private String[] args;
    private boolean isFinished = false;
    private List<WorkerTemplate> workerTemplates = new ArrayList<>();

    /**
     * @param host The socket for the interaction.
     */
    public ClientConnectionHandler(Socket host) {
        this.host = host;
        try {
            this.outputStream = host.getOutputStream();
            this.inputStream = host.getInputStream();
        } catch (IOException e) {
            LOGGER.info("The connection to " + host.getInetAddress() + " failed.");
        }
    }

    @Override
    public void run() {
        try {
            while (!isFinished) {
                ObjectInputStream objectStream = new ObjectInputStream(inputStream);
                while (isFinished) {
                    Object object = objectStream.readObject();
                    if (object instanceof String[]) {
                        args = (String[]) object;
                    } else if (object instanceof WorkerTemplateMessage) {
                        WorkerTemplateMessage message = (WorkerTemplateMessage) object;
                        workerTemplates = message.getTemplates();
                    } else if (object instanceof Boolean) {
                        isFinished = (boolean) object;
                    }
                }

            }
            inputStream.close();
            outputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.info("The connection to " + host.getInetAddress() + " failed.");
        }
    }


    /**
     * @param number
     */
    public void sendNumber(Integer number) {
        try {
            ObjectOutputStream stream = new ObjectOutputStream(outputStream);
            stream.writeObject(number);
            stream.flush();
        } catch (IOException e) {
            LOGGER.info("The connection to " + host.getInetAddress() + " failed.");
        }
    }

    /**
     * @param variable
     */
    public void sendBoolean(Boolean variable) {
        try {
            ObjectOutputStream stream = new ObjectOutputStream(outputStream);
            stream.writeObject(variable);
            stream.flush();
        } catch (IOException e) {
            LOGGER.info("The connection to " + host.getInetAddress() + " failed.");
        }
    }

    public void sendData(Boolean variable) {
        //try {
            //ObjectOutputStream stream = new ObjectOutputStream(outputStream);
            //stream.writeObject(variable);
            //stream.flush();
        //} catch (IOException e) {
            LOGGER.info("The connection to " + host.getInetAddress() + " failed.");
        //}
    }

    /**
     * @return
     */
    public synchronized Optional<Boolean> getFinished() {
        return Optional.ofNullable(isFinished);
    }


    /**
     * @return
     */
    public synchronized Optional<String[]> getArgs() {
        return Optional.ofNullable(args);
    }

    /**
     * @return
     */
    public synchronized Optional<List<WorkerTemplate>> getWorkerTemplates() {
        return Optional.ofNullable(workerTemplates);
    }
}