package betsy.bpel.virtual.server.logic;

import betsy.bpel.virtual.common.exceptions.CollectLogfileException;
import betsy.bpel.virtual.common.exceptions.CommunicationException;
import betsy.bpel.virtual.common.messages.collect_log_files.LogFile;
import betsy.bpel.virtual.common.messages.collect_log_files.LogFiles;
import betsy.bpel.virtual.common.messages.collect_log_files.LogFilesRequest;
import betsy.bpel.virtual.common.messages.collect_log_files.LogFilesResponse;
import betsy.common.tasks.FileTasks;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@link CollectLogFilesOperation} collects the log files of the engine and betsy
 * server upon a received {@link betsy.bpel.virtual.common.messages.collect_log_files.LogFilesRequest}.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public final class CollectLogFilesOperation {

    private static final Logger log = Logger.getLogger(CollectLogFilesOperation.class);

    private CollectLogFilesOperation() {}

    /**
     * Collect the log files of an engine and the betsy server. The directories
     * where the logs can be collected from are submitted in the
     * {@link betsy.bpel.virtual.common.messages.collect_log_files.LogFilesRequest}.
     *
     * @param request request containing information where to collect the logs from
     * @return {@link betsy.bpel.virtual.common.messages.collect_log_files.LogFiles} containing all collected logs
     * @throws CollectLogfileException thrown if collecting the logs failed
     */
    public static LogFilesResponse collectLogfiles(final LogFilesRequest request)
            throws CollectLogfileException {

        log.info("Collecting log files");

        // validate
        for (String path : request.getPaths()) {
            if (!new File(path).isDirectory()) {
                throw new CollectLogfileException("The dir " + path + " is no directory");
            }
        }

        // collect
        List<LogFiles> logFilesList = new LinkedList<>();
        for (String path : request.getPaths()) {
            List<LogFile> logFiles = collectLogFiles(path);
            LogFiles logFilesClass = new LogFiles(path);
            logFilesClass.getLogFiles().addAll(logFiles);
            logFilesList.add(logFilesClass);
        }

        log.info("Log files collected -> Sending response");

        return new LogFilesResponse(logFilesList);
    }

    private static List<LogFile> collectLogFiles(final String directory) {
        List<LogFile> list = new LinkedList<>();

        File[] files = new File(directory).listFiles();

        if (files == null) {
            return list;
        }

        for (File file : files) {
            if (file.isFile()) {
                String filename = file.getName();
                try {
                    List<String> lines = FileTasks.readAllLines(file.toPath());
                    list.add(new LogFile(filename, lines));
                } catch (RuntimeException e) {
                    throw new CommunicationException("Log file " + filename + " could not be read", e);
                }
            }
        }

        if (list.isEmpty()) {
            log.warn("There were no log files to be collected in " + directory);
        }

        return list;
    }

}
