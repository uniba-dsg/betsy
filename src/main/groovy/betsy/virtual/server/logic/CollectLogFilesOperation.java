package betsy.virtual.server.logic;

import betsy.virtual.common.exceptions.CollectLogfileException;
import betsy.virtual.common.exceptions.CommunicationException;
import betsy.virtual.common.messages.collect_log_files.LogFile;
import betsy.virtual.common.messages.collect_log_files.LogFiles;
import betsy.virtual.common.messages.collect_log_files.LogFilesRequest;
import betsy.virtual.common.messages.collect_log_files.LogFilesResponse;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@link CollectLogFilesOperation} collects the logfiles of the engine and betsy
 * server upon a received {@link betsy.virtual.common.messages.collect_log_files.LogFilesRequest}.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public class CollectLogFilesOperation {

    private static final Logger log = Logger.getLogger(CollectLogFilesOperation.class);

    /**
     * Collect the logfiles of an engine and the betsy server. The directories
     * where the logs can be collected from are submitted in the
     * {@link betsy.virtual.common.messages.collect_log_files.LogFilesRequest}.
     *
     * @param request request containing information where to collect the logs from
     * @return {@link betsy.virtual.common.messages.collect_log_files.LogFiles} containing all collected logs
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
                    List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
                    list.add(new LogFile(filename, lines));
                } catch (IOException e) {
                    throw new CommunicationException("Log file " + filename + " could not be read");
                }
            }
        }

        if (list.isEmpty()) {
            log.warn("There were no log files to be collected in " + directory);
        }

        return list;
    }

}
