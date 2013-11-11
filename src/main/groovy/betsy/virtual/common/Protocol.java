package betsy.virtual.common;

import betsy.virtual.common.messages.collect_log_files.LogFilesRequest;
import betsy.virtual.common.messages.collect_log_files.LogFilesResponse;
import betsy.virtual.common.messages.deploy.DeployRequest;
import betsy.virtual.common.messages.deploy.DeployResponse;

/**
 * The {@link Protocol} is running on the host and offers several methods to
 * tell the server what has to be done next. This includes to send deployment
 * instructions as well as to gather log files from the remote server.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public interface Protocol {

    /**
     * Collect log files.
     */
    public LogFilesResponse collectLogFilesOperation(LogFilesRequest request) throws Exception;

    /**
     * Deploy.
     */
    public DeployResponse deployOperation(DeployRequest request) throws Exception;

}
