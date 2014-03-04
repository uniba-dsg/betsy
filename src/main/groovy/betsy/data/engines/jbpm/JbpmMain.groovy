package betsy.data.engines.jbpm

import ant.tasks.AntUtil

import java.nio.file.Paths

/**
 * Created with IntelliJ IDEA.
 * User: stavorndran
 * Date: 04.03.14
 * Time: 13:05
 * To change this template use File | Settings | File Templates.
 */
class JbpmMain {
    private static final AntBuilder ant = AntUtil.builder()

    public static void main(String[] args){
        JbpmEngine engine = new JbpmEngine(parentFolder: Paths.get("test"))
        engine.install()
        engine.startup()
        //engine.isRunning()
        engine.shutdown()
    }
}
