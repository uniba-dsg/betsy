package betsy.bpel;

import betsy.bpel.virtual.host.exceptions.TemporaryFailedTestException;
import betsy.common.model.AbstractProcess;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import org.apache.log4j.Logger;
import betsy.common.timeouts.timeout.TimeoutRepository;

import java.nio.file.Path;
import java.util.Objects;

public class Retry {

    private static final Logger LOGGER = Logger.getLogger(Retry.class);

    public final AbstractProcess process;

    public Retry(AbstractProcess process) {
        this.process = Objects.requireNonNull(process);
    }

    public void atMostThreeTimes(Runnable closure) {

        boolean testProcess = true;
        int testCount = 0;

        while (testProcess) {
            testProcess = false;
            testCount++;

            try {
                closure.run();
            } catch (TemporaryFailedTestException exception) {
                LOGGER.info("Process " + String.valueOf(process) + " failed on engine " + String.valueOf(process.getEngine()));
                if (testCount <= 1) {
                    testProcess = true;
                    // delete old log output by moving to failed tests

                    Path archiveDir = process.getEngine().getPath().resolve("failed_repeated_tests").resolve(testCount + "_" + process.getNormalizedId());

                    WaitTasks.sleep(TimeoutRepository.getTimeout("Retry.atMostThreeTimes").get().getTimeoutInMs());
                    FileTasks.mkdirs(archiveDir);

                    FileTasks.moveFolderWithForcedCleanup(process.getTargetPath(), archiveDir);

                } else if (testCount > 1) {
                    throw new IllegalStateException("Process " + String.valueOf(process) + " failed on engine " + String.valueOf(process.getEngine()) + " repeatedly:", exception);
                } else {
                    throw new IllegalStateException("Process " + String.valueOf(process) + " failed on engine " + String.valueOf(process.getEngine()) + " with a severe exception: ", exception);
                }

            }

        }

    }


}
