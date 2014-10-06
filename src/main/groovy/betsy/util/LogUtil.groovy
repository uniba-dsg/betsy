package betsy.util

import betsy.logging.LogContext
import betsy.tasks.FileTasks
import org.apache.log4j.Logger

import java.nio.file.Path
import java.nio.file.Paths

public class LogUtil {
    public static log(String name, Logger logger, Closure closure) {
        String previous = LogContext.context;
        try {
            LogContext.context = name

            FileTasks.mkdirs(Paths.get(name).toAbsolutePath().parent)
            logger.info "Start ..."

            Stopwatch stopwatch = new Stopwatch()
            stopwatch.start()

            try {
                closure.call()
            } finally {
                stopwatch.stop()
                logger.info "... finished in ${stopwatch.formattedDiff} | (${stopwatch.diff}ms)"
                new File("test/timings.csv").withWriterAppend { out -> out.println("${stopwatch.diff};${name};${name.replaceAll("\\\\","/").split("/").join(";")}") }
            }

        } finally {
            LogContext.context = previous
        }

    }

    public static log(Path path, Logger logger, Closure closure) {
        log(path.toString(), logger, closure)
    }
}
