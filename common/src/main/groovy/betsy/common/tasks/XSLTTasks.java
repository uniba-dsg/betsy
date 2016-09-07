package betsy.common.tasks;

import java.nio.file.Path;

import betsy.common.util.FileTypes;
import org.apache.tools.ant.taskdefs.XSLTProcess;

public class XSLTTasks {

    public static void transform(Path xslt, Path input, Path output) {
        createXSLTProcess(xslt, input, output).execute();
    }

    public static void transform(Path xslt, Path input, Path output, String key, String value) {
        XSLTProcess transform = createXSLTProcess(xslt, input, output);

        // key/value are passed to XSL script
        XSLTProcess.Param param = transform.createParam();
        param.setName(key);
        param.setExpression(value);

        transform.execute();
    }

    private static XSLTProcess createXSLTProcess(Path xslt, Path input, Path output) {
        FileTasks.assertFile(xslt);
        FileTasks.assertFile(input);

        if (output.toAbsolutePath().toString().equals(input.toAbsolutePath().toString())) {
            throw new IllegalStateException("Cannot have the same input as output");
        }

        FileTasks.assertFileExtension(xslt, FileTypes.XSL);

        XSLTProcess transform = new XSLTProcess();
        transform.setStyle(xslt.toString());
        transform.setIn(input.toFile());
        transform.setOut(output.toFile());

        transform.setTaskName("xslt");
        transform.setProject(AntUtil.builder().getProject());

        transform.setProcessor("trax");
        transform.createFactory().setName("net.sf.saxon.TransformerFactoryImpl");

        return transform;
    }

}
