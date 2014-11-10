package betsy.bpel

import ant.tasks.AntUtil
import betsy.common.model.BetsyProcess
import betsy.common.tasks.FileTasks
import betsy.common.tasks.WaitTasks
import betsy.bpel.virtual.host.exceptions.TemporaryFailedTestException
import org.apache.log4j.Logger

import java.nio.file.Path

class Retry {

    private static final Logger LOGGER = Logger.getLogger(Retry)

    final AntBuilder ant = AntUtil.builder()

    BetsyProcess process

    public void atMostThreeTimes(Runnable closure) {
        boolean testProcess = true
        int testCount = 0;
        while (testProcess) {
            testProcess = false
            testCount++

            try {
                closure.run()
            } catch (TemporaryFailedTestException exception) {
                LOGGER.info "Process ${process} failed on engine ${process.engine}"
                if (testCount <= 1) {
                    testProcess = true
                    // delete old log output by moving to failed tests

                    Path archiveDir = process.engine.path.resolve("failed_repeated_tests").resolve("${testCount}_${process.normalizedId}")

                    WaitTasks.sleep(500);
                    FileTasks.mkdirs(archiveDir)

                    ant.move(file: process.targetPath, tofile: archiveDir, force: true, performGCOnFailedDelete: true)

                } else if (testCount > 1) {
                    throw new IllegalStateException("Process ${process} failed on engine ${process.engine} repeatedly:", exception);
                } else {
                    throw new IllegalStateException("Process ${process} failed on engine ${process.engine} with a severe exception: ", exception);
                }
            }
        }
    }

}
