package betsy.data.engines.bonita

/**
 * Created with IntelliJ IDEA.
 * User: stavorndran
 * Date: 03.03.14
 * Time: 16:17
 * To change this template use File | Settings | File Templates.
 */
class BonitaMain {
    public static void main(String[] args){
        BonitaEngine engine = new BonitaEngine()

        engine.install()
        engine.startup()
        //System.out.println(engine.isRunning())
        //engine.shutdown()
    }
}
