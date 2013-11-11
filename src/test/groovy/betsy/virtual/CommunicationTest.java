package betsy.virtual;

import betsy.virtual.common.Constants;
import betsy.virtual.common.Protocol;
import betsy.virtual.common.exceptions.CommunicationException;
import betsy.virtual.common.messages.collect_log_files.LogFiles;
import betsy.virtual.common.messages.collect_log_files.LogFilesRequest;
import betsy.virtual.common.messages.collect_log_files.LogFilesResponse;
import betsy.virtual.common.messages.deploy.DeployRequest;
import betsy.virtual.common.messages.deploy.DeployResponse;
import betsy.virtual.common.messages.deploy.FileMessage;
import betsy.virtual.host.comm.HostTcpClient;
import betsy.virtual.server.comm.VirtualMachineTcpServer;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertTrue;

public class CommunicationTest {

    @Test
    public void testCommunication() throws Exception {
        Protocol protocol = new Protocol() {
            @Override
            public LogFilesResponse collectLogFilesOperation(LogFilesRequest request) throws CommunicationException {
                return new LogFilesResponse(new LinkedList<LogFiles>());
            }

            @Override
            public DeployResponse deployOperation(DeployRequest request) throws CommunicationException {
                return new DeployResponse();
            }
        };

        int server_port = 50000;
        VirtualMachineTcpServer virtualMachineTcpServer = new VirtualMachineTcpServer(server_port, protocol);
        virtualMachineTcpServer.start();

        Thread.sleep(500);

        HostTcpClient client = new HostTcpClient(Constants.SERVER_HOSTNAME, server_port);
        client.deployOperation(new DeployRequest());
        client.collectLogFilesOperation(new LogFilesRequest());
        virtualMachineTcpServer.shutdown();
    }

    @Test(expected = IllegalStateException.class)
    public void testExceptionCommunication() throws Exception {
        Protocol protocol = new Protocol() {
            @Override
            public LogFilesResponse collectLogFilesOperation(LogFilesRequest request) throws CommunicationException {
                return new LogFilesResponse(new LinkedList<LogFiles>());
            }

            @Override
            public DeployResponse deployOperation(DeployRequest request) throws CommunicationException {
                throw new IllegalStateException("asdf");
            }
        };

        int server_port = 50001;
        VirtualMachineTcpServer virtualMachineTcpServer = new VirtualMachineTcpServer(server_port, protocol);
        virtualMachineTcpServer.start();

        Thread.sleep(500);

        HostTcpClient client = new HostTcpClient(Constants.SERVER_HOSTNAME, server_port);
        client.deployOperation(new DeployRequest());
    }

    @Test
    public void testCommunicationWithTimeout() throws Exception {
        Protocol protocol = new Protocol() {
            @Override
            public LogFilesResponse collectLogFilesOperation(LogFilesRequest request) throws CommunicationException {
                return new LogFilesResponse(new LinkedList<LogFiles>());
            }

            @Override
            public DeployResponse deployOperation(DeployRequest request) throws CommunicationException {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return new DeployResponse();
            }
        };

        int server_port = 50002;
        VirtualMachineTcpServer virtualMachineTcpServer = new VirtualMachineTcpServer(server_port, protocol);
        virtualMachineTcpServer.start();

        Thread.sleep(500);

        HostTcpClient client = new HostTcpClient(Constants.SERVER_HOSTNAME, server_port);
        client.deployOperation(new DeployRequest());
    }

    @Test
    public void testCommunicationWithLargeMessage() throws Exception {
        Protocol protocol = new Protocol() {
            @Override
            public LogFilesResponse collectLogFilesOperation(LogFilesRequest request) throws CommunicationException {
                return new LogFilesResponse(new LinkedList<LogFiles>());
            }

            @Override
            public DeployResponse deployOperation(DeployRequest request) throws CommunicationException {
                return new DeployResponse();
            }
        };

        int server_port = 50003;
        VirtualMachineTcpServer virtualMachineTcpServer = new VirtualMachineTcpServer(server_port, protocol);
        virtualMachineTcpServer.start();

        Thread.sleep(500);

        HostTcpClient client = new HostTcpClient(Constants.SERVER_HOSTNAME, server_port);
        DeployRequest request = new DeployRequest();
        byte[] data = new byte[4525224];
        data[5] = 1;
        request.setFileMessage(new FileMessage("some-file-name.txt", data));
        client.deployOperation(request);
    }

}
