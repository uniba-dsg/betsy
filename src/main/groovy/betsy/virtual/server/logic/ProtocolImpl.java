package betsy.virtual.server.logic;

import betsy.virtual.common.Protocol;
import betsy.virtual.common.exceptions.ChecksumException;
import betsy.virtual.common.exceptions.CollectLogfileException;
import betsy.virtual.common.exceptions.ConnectionException;
import betsy.virtual.common.exceptions.DeployException;
import betsy.virtual.common.messages.collect_log_files.LogFilesRequest;
import betsy.virtual.common.messages.collect_log_files.LogFilesResponse;
import betsy.virtual.common.messages.deploy.DeployRequest;
import betsy.virtual.common.messages.deploy.DeployResponse;

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
