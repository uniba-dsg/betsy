package betsy.common.util;

import betsy.common.logging.LogContext;
import betsy.common.tasks.FileTasks;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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
                Path timingsCsv = Paths.get("test/timings.csv");
                try (BufferedWriter writer = Files.newBufferedWriter(timingsCsv, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                    writer.append(String.valueOf(stopwatch.getDiff()));
                    writer.append(";");
                    writer.append(name);
                    writer.append(name.replaceAll("\\\\", "/").replaceAll("/", ";"));
                    writer.newLine();
                } catch (IOException e) {
                    logger.error("could not write the " + timingsCsv + " file", e);
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
