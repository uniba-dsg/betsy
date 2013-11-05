package betsy.virtual.server;

import betsy.virtual.common.exceptions.CollectLogfileException;
import betsy.virtual.common.messages.FileMessage;
import betsy.virtual.common.messages.LogRequest;
import betsy.virtual.common.messages.LogfileCollection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@link LogfileCollector} collects the logfiles of the engine and betsy
 * server upon a received {@link LogRequest}.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public class LogfileCollector {

    private static final Logger log = Logger.getLogger(LogfileCollector.class);

    /**
     * Collect the logfiles of an engine and the betsy server. The directories
     * where the logs can be collected from are submitted in the
     * {@link LogRequest}.
     *
     * @param request request containing information where to collect the logs from
     * @return {@link LogfileCollection} containing all collected logs
     * @throws CollectLogfileException thrown if collecting the logs failed
     */
    public LogfileCollection collectLogfiles(final LogRequest request)
            throws CollectLogfileException {

        File betsyServerInstallDir = new File(request.getBetsyInstallDir());
        if (!betsyServerInstallDir.isDirectory()) {
            throw new CollectLogfileException("The dir to collect the bVMS "
                    + "logs from does not exist.");
        }
        File engineLogDirectory = new File(request.getEngineLogfileDir());
        if (!engineLogDirectory.isDirectory()) {
            throw new CollectLogfileException("The dir to collect the engines "
                    + "logs from does not exist.");
        }

        int leftAttempts = 3;
        while (leftAttempts > 0) {
            leftAttempts--;
            LogfileCollection lfc = new LogfileCollection();
            try {
                lfc.addEngineLogfiles(collectEngineLogfiles(engineLogDirectory));
            } catch (IOException exception) {
                log.error("Engine logfile could not be read:", exception);
                // try again
                continue;
            }

            try {
                lfc.addBetsyLogfiles(collectBetsyServerLogfiles(betsyServerInstallDir));
            } catch (IOException exception) {
                log.error("Betsy logfile could not be read:", exception);
                // try again
                continue;
            }

            return lfc;
        }

        throw new CollectLogfileException("Logfiles could not be collected "
                + "because the file's data could not be read.");
    }

    private List<FileMessage> collectBetsyServerLogfiles(
            final File betsyServerInstallDir) throws IOException {
        List<FileMessage> list = new LinkedList<>();

        Collection<?> logfiles = FileUtils.listFiles(betsyServerInstallDir,
                TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

        for (Object o : logfiles) {
            File file = (File) o;
            byte[] data = FileUtils.readFileToByteArray(file);
            FileMessage lf = new FileMessage(file.getName(), data);
            list.add(lf);
        }

        if (list.isEmpty()) {
            log.warn("There were no betsy server logfiles to be collected.");
        }

        return list;
    }

    private List<FileMessage> collectEngineLogfiles(
            final File engineLogDirectory) throws IOException {
        List<FileMessage> list = new LinkedList<>();

        Collection<?> logfiles = FileUtils.listFiles(engineLogDirectory,
                TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

        for (Object o : logfiles) {
            File file = (File) o;
            byte[] data = FileUtils.readFileToByteArray(file);
            FileMessage lf = new FileMessage(file.getName(), data);
            list.add(lf);
        }

        if (list.isEmpty()) {
            log.warn("There were no engine logfiles to be collected.");
        }

        return list;
    }

}
