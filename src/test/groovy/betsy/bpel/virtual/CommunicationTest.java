package betsy.bpel.virtual;

import betsy.bpel.virtual.common.Constants;
import betsy.bpel.virtual.common.Protocol;
import betsy.bpel.virtual.common.exceptions.CommunicationException;
import betsy.bpel.virtual.common.messages.collect_log_files.LogFiles;
import betsy.bpel.virtual.common.messages.collect_log_files.LogFilesRequest;
import betsy.bpel.virtual.common.messages.collect_log_files.LogFilesResponse;
import betsy.bpel.virtual.common.messages.deploy.DeployRequest;
import betsy.bpel.virtual.common.messages.deploy.DeployResponse;
import betsy.bpel.virtual.common.messages.deploy.FileMessage;
import betsy.bpel.virtual.host.comm.HostTcpClient;
import betsy.bpel.virtual.server.comm.VirtualMachineTcpServer;
import org.junit.Test;

import java.util.LinkedList;

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
