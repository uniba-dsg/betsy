package ant.tasks

import org.apache.log4j.Logger
import org.apache.log4j.MDC
import org.apache.tools.ant.BuildEvent
import org.apache.tools.ant.Project
import org.apache.tools.ant.Task
import org.apache.tools.ant.listener.Log4jListener

class AntUtil {

    private static AntBuilder ant

    public static AntBuilder builder() {
        if(ant == null) {
            ant = new AntBuilder()

            //removing all registered build listeners, including default (that writes to console)
            ant.project.getBuildListeners().each {
                ant.project.removeBuildListener(it)
            }

            //registering Log4j build listener
            Log4jListener listener = new Log4jListener() {
                @Override
                void buildStarted(BuildEvent event) {
                }

                @Override
                void buildFinished(BuildEvent event) {
                }

                @Override
                void targetStarted(BuildEvent event) {
                }

                @Override
                void targetFinished(BuildEvent event) {
                }

                @Override
                void taskStarted(BuildEvent event) {
                }

                @Override
                void taskFinished(BuildEvent event) {
                }

                @Override
                void messageLogged(BuildEvent event) {
                    // ensure logging only the important ant messages
                    // with format [ant.$TASK] $MESSAGE
                    // example: [ant.mkdir] Created Dir ...
                    Task task = event.getTask()
                    String taskName = "ant." + (task == null ? "unknown" : task.getTaskName())
                    event.setMessage("[" + taskName + "] " + event.getMessage(), event.getPriority());

                    super.messageLogged(event);
                }
            }
            ant.project.addBuildListener(listener)


        }

        return ant
    }

}
