package betsy.common.virtual.swarm.host;


import betsy.common.tasks.FileTasks;
import betsy.common.virtual.cbetsy.WorkerTemplate;
import betsy.common.virtual.exceptions.NetworkException;
import betsy.common.virtual.swarm.common.ConnectionService;
import betsy.common.virtual.swarm.messages.ArgumentsMessage;
import betsy.common.virtual.swarm.messages.FinishMessage;
import betsy.common.virtual.swarm.messages.ReadyMessage;
import betsy.common.virtual.swarm.messages.TemplatesMessage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The {@link TcpMessageService} handles the activities of the tcp connections.
 *
 * @author Christoph Broeker
 * @version 1.0
 */
public class TcpMessageService extends Observable {

    private CopyOnWriteArrayList<ConnectionService> connectionServiceList;
    private HashMap<ConnectionService, Integer> numbers = new HashMap<>();
    private HashSet<ConnectionService> readyClients = new HashSet<>();
    private ArrayList<WorkerTemplate> returnedTemplates = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger(TcpMessageService.class);
    private Boolean finished = false;

    public TcpMessageService(CopyOnWriteArrayList<ConnectionService> connectionServiceList) {
        this.connectionServiceList = connectionServiceList;
    }


    /**
     * With this method it is possible to send arguments to the client.
     * @param args The arguments to send.
     */
    public void sendArgsMessage(String[] args) {
        connectionServiceList.forEach(k -> {
            try {
                k.send(new ArgumentsMessage(args));
            } catch (NetworkException e) {
                try {
                    LOGGER.info(e.getMessage());
                    k.close();
                    connectionServiceList.remove(k);
                } catch (NetworkException e1) {
                    LOGGER.info("Can't close connection.");
                }
            }
        });
    }

    /**
     * With this method it is possible to send workerTemplates to the clients.
     * @param templates The workerTemplates to send.
     */
    public void sendWorkerTemplateMessage(HashMap<ConnectionService, List<WorkerTemplate>> templates) {
        templates.forEach((k, t) -> {
            try {
                k.send(new TemplatesMessage(t));
            } catch (NetworkException e) {
                try {
                    LOGGER.info(e.getMessage());
                    k.close();
                    connectionServiceList.remove(k);
                } catch (NetworkException e1) {
                    LOGGER.info("Can't close connection.");
                }
            }
        });
    }

    /**
     * With this method it is possible to send the status of the hole process to clients.
     * @param isFinish The status of the hole process.
     */
    public void sendFinishMessage(boolean isFinish) {
        connectionServiceList.forEach(k -> {
            try {
                k.send(new FinishMessage(isFinish));
                if (isFinish) {
                    finished = true;
                    k.close();
                }
            } catch (NetworkException e) {
                try {
                    LOGGER.info(e.getMessage());
                    k.close();
                    connectionServiceList.remove(k);
                } catch (NetworkException e1) {
                    LOGGER.info("Can't close connection.");
                }
            }
        });
    }

    /**
     * With this method it is possible to send the status of the host to the clients.
     * @param isReady The status of the host.
     */
    public void sendReadyMessage(boolean isReady) {
        connectionServiceList.forEach(k -> {
            try {
                k.send(new ReadyMessage(isReady));
            } catch (NetworkException e) {
                try {
                    LOGGER.info(e.getMessage());
                    k.close();
                    connectionServiceList.remove(k);
                } catch (NetworkException e1) {
                    LOGGER.info("Can't close connection.");
                }
            }
        });
    }

    /**
     *
     * @return Returns the number of containers, which each client can run parallel.
     */
    public synchronized HashMap<ConnectionService, Integer> getNumbers() {
        return numbers;
    }

    /**
     *
     * @return Returns the number of connected clients.
     */
    public synchronized int getClientNumber() {
        return connectionServiceList.size();
    }

    /**
     *
     * @return Returns true is clients are ready.
     */
    public synchronized boolean clientsAreReady() {
        return readyClients.size() == connectionServiceList.size();
    }

    /**
     *
     * @return Returns the workerTemplates of clients, which connection failed.
     */
    public synchronized ArrayList<WorkerTemplate> getReturnedTemplates() {
        return returnedTemplates;
    }

    /**
     * With this method it is possible to add the number of containers, which can a client run parallel.
     *
     * @param connectionService The {@link ConnectionService} for the client.
     * @param number The number of containers.
     */
    public synchronized void addNumber(ConnectionService connectionService, Integer number) {
        numbers.put(connectionService, number);
    }

    /**
     *
     * @return Returns the status of the hole process.
     */
    public synchronized boolean isFinished() {
        return finished;
    }

    /**
     *
     * With this method it is possible to write the result files.
     *
     * @param connectionService The connectionService for the client, which sends the files.
     * @param fileList The list of send files.
     * @param directories The list with the directories.
     */
    public synchronized void writeFiles(ConnectionService connectionService, HashMap<String, byte[]> fileList, ArrayList<String> directories) {
        directories.forEach(e ->
                FileTasks.mkdirs(Paths.get(e))
        );
        fileList.forEach((e, k) -> {
            try {
                Files.write(Paths.get(e), k);
            } catch (IOException e1) {

                LOGGER.info("Can't write files.");

            }
        });
        readyClients.add(connectionService);
    }
}
