package betsy.common;

import java.nio.file.Path;
import java.util.List;

public interface HasLogs {

    /**
     * Returns a list of files and directories.
     *
     * @return a list of files and directories
     */
    List<Path> getLogs();
}
