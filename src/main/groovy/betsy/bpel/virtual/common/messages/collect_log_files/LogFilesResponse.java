package betsy.bpel.virtual.common.messages.collect_log_files;

import java.io.Serializable;
import java.util.List;

public class LogFilesResponse implements Serializable{

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    private final List<LogFiles> logFiles;

    public LogFilesResponse(List<LogFiles> logFiles) {
        this.logFiles = logFiles;
    }

    public List<LogFiles> getLogFiles() {
        return logFiles;
    }

    @Override
    public String toString() {
        return "LogFilesResponse{" +
                "logFiles=" + logFiles +
                '}';
    }
}
