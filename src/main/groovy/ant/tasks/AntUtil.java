package ant.tasks;

import groovy.util.AntBuilder;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.listener.Log4jListener;

public class AntUtil {

    public static AntBuilder builder() {
        if (ant == null) {
            ant = new AntBuilder();

            //removing all registered build listeners, including default (that writes to console)
            for (BuildListener listener : ant.getProject().getBuildListeners()) {
                ant.getProject().removeBuildListener(listener);
            }


            //registering Log4j build listener
            Log4jListener listener = new Log4jListener() {
                @Override
                public void buildStarted(BuildEvent event) {
                }

                @Override
                public void buildFinished(BuildEvent event) {
                }

                @Override
                public void targetStarted(BuildEvent event) {
                }

                @Override
                public void targetFinished(BuildEvent event) {
                }

                @Override
                public void taskStarted(BuildEvent event) {
                }

                @Override
                public void taskFinished(BuildEvent event) {
                }

                @Override
                public void messageLogged(BuildEvent event) {
                    // ensure logging only the important ant messages
                    // with format [ant.$TASK] $MESSAGE
                    // example: [ant.mkdir] Created Dir ...
                    Task task = event.getTask();
                    String taskName = task == null ? "unknown" : task.getTaskName();
                    String message = String.format("[ant.%s] %s", taskName, event.getMessage());
                    event.setMessage(message, event.getPriority());

                    super.messageLogged(event);
                }

            };
            ant.getProject().addBuildListener(listener);

        }

        return ant;
    }

    private static AntBuilder ant;
}
