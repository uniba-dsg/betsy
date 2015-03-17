package betsy.common.tasks;

import ant.tasks.AntUtil;
import betsy.common.util.FileTypes;
import org.apache.tools.ant.taskdefs.XSLTProcess;

import java.nio.file.Path;

public class XSLTTasks {

    public static void transform(Path xslt, Path input, Path output) {
        FileTasks.assertFile(xslt);
        FileTasks.assertFile(input);

        FileTasks.assertFileExtension(xslt, FileTypes.XSL);

        XSLTProcess transform = new XSLTProcess();
        transform.setStyle(xslt.toString());
        transform.setIn(input.toFile());
        transform.setOut(output.toFile());

        transform.setTaskName("xslt");
        transform.setProject(AntUtil.builder().getProject());

        transform.execute();
    }

}
