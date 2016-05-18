package betsy.common.virtual.swarm;

import betsy.common.virtual.WorkerTemplate;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christoph Broeker
 * @version 1.0
 *          This class handles the connection to a client.
 */
public class ConnectionHandler extends Thread {

    private static final Logger LOGGER = Logger.getLogger(ConnectionHandler.class);
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
    public ConnectionHandler(SocketChannel client, Host host) {
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
            while (isFinished) {
                ObjectInputStream objectStream = new ObjectInputStream(inputStream);
                Integer number = objectStream.readInt();
                host.addNumber(this, number);

                isReady = objectStream.readBoolean();

                int filesCount = objectStream.readInt();
                File[] files = new File[filesCount];
                objectStream.close();

                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
                Path result = Paths.get("results");
                for (int i = 0; i < filesCount; i++) {
                    long fileLength = dataInputStream.readLong();
                    String fileName = dataInputStream.readUTF();

                    files[i] = result.resolve(fileName).toFile();

                    FileOutputStream fos = new FileOutputStream(files[i]);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);

                    for (int j = 0; j < fileLength; j++) bos.write(bufferedInputStream.read());
                }
                dataInputStream.close();
                bufferedInputStream.close();
                isReady = true;
            }
        } catch (IOException e) {
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
            stream.close();
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
            stream.writeObject(templates);
            stream.close();
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
            stream.close();
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
            stream.close();
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
