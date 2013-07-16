package betsy.executables

import betsy.data.BetsyProcess
import betsy.virtual.host.exceptions.TemporaryFailedTestException

class Retry {

    AntBuilder ant
    BetsyProcess process

    public void atMostThreeTimes(Closure closure) {
        boolean testProcess = true
        int testCount = 0;
        while(testProcess) {
            testProcess = false
            testCount++

            try {
                String repeat = testCount > 1 ? "Repeating process" : "Process"
                println repeat
                closure.run()
            } catch(TemporaryFailedTestException exception) {
                println("Process ${process} failed on engine ${process.engine}")
                if(testCount <= 1) {
                    testProcess = true
                    // delete old log output by moving to failed tests

                    String repeatedDir = "${process.engine.path}/failed_repeated_tests"
                    String destDir = "${repeatedDir}/${testCount}_${process.normalizedId}"

                    ant.sleep(milliseconds: 500)
                    ant.mkdir(dir: repeatedDir)
                    // TODO does not work. fails for petalsesb
                    ant.move(file: process.targetPath, tofile: destDir, force: true, performGCOnFailedDelete: true)

                }else if(testCount > 1) {
                    throw new IllegalStateException("Process ${process} failed on engine ${process.engine} repeatedly:", exception);
                }else {
                    throw new IllegalStateException("Process ${process} failed on engine ${process.engine} with a severe exception: ", exception);
                }
            }
        }
    }

}
