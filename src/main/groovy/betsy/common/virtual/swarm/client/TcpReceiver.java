package betsy.common.virtual.swarm.client;


import betsy.common.virtual.exceptions.NetworkException;
import betsy.common.virtual.exceptions.SystemException;
import betsy.common.virtual.swarm.common.ConnectionService;
import betsy.common.virtual.swarm.messages.*;
import org.apache.log4j.Logger;

/**
 * The {@link TcpReceiver} receives the {@link Message} via
 * {@link ConnectionService} and handles the {@link Message}.
 *
 * @author Christoph Broeker
 * @version 1.0
 */
public class TcpReceiver extends Thread {

    private static final Logger LOGGER = Logger.getLogger(TcpReceiver.class);
    private ConnectionService connectionService;
    private Client client;
    private boolean isRunning = true;

    /**
     * @param connectionService The {@link ConnectionService} to receive a {@link Message}.
     * @param client The {@link Client}
     *
     */
    public TcpReceiver(ConnectionService connectionService, Client client) {
        this.connectionService = connectionService;
        this.client = client;
    }

    @Override
    public void run() {
        while (isRunning) {
            Object object = null;
            try {
                object = connectionService.receive();
            } catch (NetworkException | SystemException e) {
                LOGGER.info(e.getMessage());
                isRunning = false;
            }
            if (!(object == null)) {
                if(object instanceof ArgumentsMessage){
                    ArgumentsMessage argumentsMessage = (ArgumentsMessage) object;
                    client.setArguments(argumentsMessage.getArgs());
                }else if(object instanceof TemplatesMessage){
                    TemplatesMessage templatesMessage = (TemplatesMessage) object;
                    client.setTemplates(templatesMessage.getTemplates());
                }else if(object instanceof ReadyMessage){
                    ReadyMessage readyMessage = (ReadyMessage) object;
                    client.isReady(readyMessage.isReady());
                }else if(object instanceof FinishMessage){
                    FinishMessage finishMessage = (FinishMessage) object;
                    client.isFinished(finishMessage.isFinish());
                }
            }
        }
    }

    /**
     * With this method it is possible to set the status of the {@link TcpReceiver}.
     *
     * @param isRunning The status of the {@link TcpReceiver}
     */
    public void setRunning(boolean isRunning){
        this.isRunning = isRunning;
    }
}
