package betsy.common.util;

import betsy.common.logging.LogContext;
import betsy.common.model.TestSuite;
import betsy.common.tasks.FileTasks;
import org.apache.log4j.Logger;
import org.apache.tools.ant.taskdefs.ExecTask;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LogUtil {
    public static void log(final String name, Logger logger, Runnable closure) {
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
                    // TODO should use the TestSuite#getDurationsCsvFilePath method instead of hardcoding it to test here
                    new DurationCsv(Paths.get("test").resolve(TestSuite.getCsvDurationFile())).saveTaskDuration(name, stopwatch.getDiff());
                } catch (Exception e) {
                    logger.error("could not save task duration", e);
                }
            }

        } finally {
            LogContext.setContext(previous);
        }

    }

    public static void log(Path path, Logger logger, Runnable closure) {
        log(path.toString(), logger, closure);
    }

}
