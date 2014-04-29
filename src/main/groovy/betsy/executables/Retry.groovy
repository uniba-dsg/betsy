package betsy.executables

import ant.tasks.AntUtil
import betsy.data.BetsyProcess
import betsy.tasks.FileTasks
import betsy.tasks.WaitTasks
import betsy.virtual.host.exceptions.TemporaryFailedTestException
import org.apache.log4j.Logger

import java.nio.file.Path

class Retry {

    private static Logger log = Logger.getLogger(Retry)

    final AntBuilder ant = AntUtil.builder()

    BetsyProcess process

    public void atMostThreeTimes(Closure closure) {
        boolean testProcess = true
        int testCount = 0;
        while (testProcess) {
            testProcess = false
            testCount++

            try {
                closure.run()
            } catch (TemporaryFailedTestException exception) {
                log.info "Process ${process} failed on engine ${process.engine}"
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
