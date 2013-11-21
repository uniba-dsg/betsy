package betsy.executables

import ant.tasks.AntUtil
import betsy.data.BetsyProcess
import betsy.tasks.FileTasks
import betsy.virtual.host.exceptions.TemporaryFailedTestException
import org.apache.log4j.Logger

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

                    String repeatedDir = "${process.engine.path}/failed_repeated_tests"
                    String destDir = "${repeatedDir}/${testCount}_${process.normalizedId}"

                    ant.sleep(milliseconds: 500)
                    FileTasks.mkdirs(repeatedDir)
                    // TODO does not work. fails for petalsesb
                    ant.move(file: process.targetPath, tofile: destDir, force: true, performGCOnFailedDelete: true)

                } else if (testCount > 1) {
                    throw new IllegalStateException("Process ${process} failed on engine ${process.engine} repeatedly:", exception);
                } else {
                    throw new IllegalStateException("Process ${process} failed on engine ${process.engine} with a severe exception: ", exception);
                }
            }
        }
    }

}
