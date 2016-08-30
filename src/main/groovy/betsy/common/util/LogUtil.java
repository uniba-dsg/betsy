package betsy.common.util;

import betsy.common.logging.LogContext;
import betsy.common.model.AbstractTestSuite;
import betsy.common.model.TestSuiteFolderStructure;
import betsy.common.tasks.FileTasks;
import org.apache.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LogUtil {

    private final AbstractTestSuite suite;

    public LogUtil(AbstractTestSuite testSuite) {
        suite = testSuite;
    }

    public void log(final String name, Logger logger, Runnable closure) {
        String previous = LogContext.getContext();
        try {
            LogContext.setContext(name);

            FileTasks.mkdirs(Paths.get(name).toAbsolutePath().getParent());
            logger.info("Start ...");

            final Stopwatch stopwatch = new Stopwatch();
            stopwatch.start();

            try {
                closure.run();
            } finally {
                stopwatch.stop();
                logger.info("... finished in " + stopwatch.getFormattedDiff() + " | (" + stopwatch.getDiff() + "ms)");
                try{
                    // uses a non-default path if available
                    new DurationCsv(getCsvDurationFilePath()).saveTaskDuration(name, stopwatch.getDiff());
                } catch (Exception e) {
                    logger.error("could not save task duration", e);
                }
            }

        } finally {
            LogContext.setContext(previous);
        }

    }

    private Path getCsvDurationFilePath() {
        if(suite == null){
            return ((TestSuiteFolderStructure) () -> Paths.get("test")).getCsvDurationFilePath();
        } else {
            return suite.getCsvDurationFilePath();
        }
    }

    public void log(Path path, Logger logger, Runnable closure) {
        log(path.toString(), logger, closure);
    }

}
