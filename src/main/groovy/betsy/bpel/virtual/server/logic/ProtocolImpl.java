package betsy.bpel.virtual.server.logic;

import betsy.bpel.virtual.common.Protocol;
import betsy.bpel.virtual.common.exceptions.ChecksumException;
import betsy.bpel.virtual.common.exceptions.CollectLogfileException;
import betsy.bpel.virtual.common.exceptions.ConnectionException;
import betsy.bpel.virtual.common.exceptions.DeployException;
import betsy.bpel.virtual.common.messages.collect_log_files.LogFilesRequest;
import betsy.bpel.virtual.common.messages.collect_log_files.LogFilesResponse;
import betsy.bpel.virtual.common.messages.deploy.DeployRequest;
import betsy.bpel.virtual.common.messages.deploy.DeployResponse;

public class ProtocolImpl implements Protocol {

    @Override
    public LogFilesResponse collectLogFilesOperation(LogFilesRequest request)
            throws ChecksumException, ConnectionException, CollectLogfileException {
        return CollectLogFilesOperation.collectLogfiles(request);
    }

    @Override
    public DeployResponse deployOperation(DeployRequest request)
            throws DeployException, ChecksumException, ConnectionException {
        return DeployOperation.deployOperation(request);
    }
}
