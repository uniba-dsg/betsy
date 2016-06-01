package betsy.common.virtual.swarm.host;


import betsy.common.virtual.exceptions.NetworkException;
import betsy.common.virtual.exceptions.SystemException;
import betsy.common.virtual.swarm.common.ConnectionService;
import betsy.common.virtual.swarm.messages.DataMessage;
import betsy.common.virtual.swarm.messages.Message;
import betsy.common.virtual.swarm.messages.NumberMessage;

/**
 * The {@link TcpReceiver} receives the {@link Object} via
 * {@link ConnectionService} and handles the {@link Message}.
 *
 * @author Christoph Broeker
 * @version 1.0
 */
public class TcpReceiver extends Thread {

    private ConnectionService connectionService;
    private TcpMessageService tcpMessageService;
    private boolean isRunning = true;

    /**
     * @param connectionService The {@link ConnectionService} to receive a {@link Message}.
     * @param tcpMessageService The {@link TcpMessageService} to handle all connections.
     */
    public TcpReceiver(ConnectionService connectionService, TcpMessageService tcpMessageService) {
        this.connectionService = connectionService;
        this.tcpMessageService = tcpMessageService;
    }

    @Override
    public void run() {
        while (isRunning) {
            isRunning = !tcpMessageService.isFinished();
            Object object = null;
            try {
                object =connectionService.receive();
            } catch (NetworkException | SystemException e) {
                System.out.println(e);
                isRunning = false;
            }
            if (!(object == null)) {
                if(object instanceof NumberMessage){
                    NumberMessage numberMessage = (NumberMessage) object;
                    tcpMessageService.addNumber(connectionService, numberMessage.getNumber());
                }else if(object instanceof DataMessage){
                    DataMessage dataMessage = (DataMessage) object;
                    tcpMessageService.writeFiles(connectionService, dataMessage.getFileList(), dataMessage.getDirectories());
                }
            }
        }
    }
    /**
     * With this method it is possible to set the status of the {@link betsy.common.virtual.swarm.client.TcpReceiver}.
     *
     * @param isRunning The status of the {@link betsy.common.virtual.swarm.client.TcpReceiver}
     */
    public void setRunning(boolean isRunning){
        this.isRunning = isRunning;
    }
}
