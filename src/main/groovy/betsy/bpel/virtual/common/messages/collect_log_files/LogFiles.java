package betsy.bpel.virtual.common.messages.collect_log_files;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * A {@link LogFiles} groups the engine's and betsy's logfiles. The
 * collection is used to transport them to the client.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public class LogFiles implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    private final List<LogFile> logfiles = new LinkedList<>();
    private final String folder;

    public LogFiles(String folder) {
        this.folder = folder;
    }

    public List<LogFile> getLogFiles() {
        return this.logfiles;
    }

    public String getFolder() {
        return folder;
    }

    @Override
    public String toString() {
        return "LogFiles{" +
                "logfiles=" + logfiles +
                ", folder='" + folder + '\'' +
                '}';
    }
}
