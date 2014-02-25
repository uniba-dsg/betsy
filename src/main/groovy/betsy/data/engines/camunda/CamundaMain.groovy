package betsy.data.engines.camunda

import ant.tasks.AntUtil
import ant.tasks.HttpContains

/**
 * Created with IntelliJ IDEA.
 * User: Andreas Vorndran, Mathias Casar
 * Date: 25.02.14
 * Time: 10:13
 */
class CamundaMain {

    private static final AntBuilder ant = AntUtil.builder()

    public static void main(String[] args) {
        CamundaEngine engine = new CamundaEngine()
        /*engine.install()
        engine.startup()

        ant.waitfor(maxwait: "30", maxwaitunit: "second", checkevery: "500") {
            http url: "http://localhost:8080"
        }
        engine.deployTest()
        Thread.sleep(15000)*/
        CamundaTester tester = new CamundaTester(restURL: "http://localhost:8080/engine-rest/engine/default")
        tester.runTest()
        //engine.shutdown()

        //engine.isRunning()
    }
}
