package ant.tasks

class AntUtil {

    public static builder() {
        def ant = new AntBuilder()
        //removing all registered build listeners, including default (that writes to console)
        ant.project.getBuildListeners().each {
            ant.project.removeBuildListener(it)
        }
        //registering Log4j build listener
        //ant.project.addBuildListener(new Log4jListener())
        return ant
    }

    // 'org.apache.ant:ant-apache-log4j:1.9.2'

}
