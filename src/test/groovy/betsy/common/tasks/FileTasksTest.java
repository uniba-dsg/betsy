package betsy.common.tasks;

import static org.junit.Assert.assertEquals;

import betsy.bpmn.model.BPMNAssertions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileTasksTest {

    private static final Path ROOT = Paths.get(System.getProperty("user.dir"));
    private static final Path TEST_FOLDER = Paths.get("src", "test", "temp");
    private static final Path PATH_LOG_FILE = ROOT.resolve(TEST_FOLDER).resolve("logTest.txt");

    @AfterClass
    public static void tearDown(){
        FileTasks.deleteFile(PATH_LOG_FILE);
    }

    @Test
    public void testGetFilenameWithoutExtension() {
        assertEquals("", FileTasks.getFilenameWithoutExtension(Paths.get(".gitignore")));
        assertEquals("LICENSE", FileTasks.getFilenameWithoutExtension(Paths.get("LICENSE")));
        assertEquals("build", FileTasks.getFilenameWithoutExtension(Paths.get("build.gradle")));

    }

    @Test
    public void testReplaceLogFileContent(){
        List<String> listToReplace = new ArrayList<>();
        listToReplace.add(BPMNAssertions.ERROR_DEPLOYMENT.toString());
        listToReplace.add(BPMNAssertions.ERROR_RUNTIME.toString());

        List<String> listExpected = new ArrayList<>();
        listExpected.add(BPMNAssertions.SCRIPT_task1.toString());
        listExpected.add(BPMNAssertions.ERROR_GENERIC.toString());

        FileTasks.createFile(PATH_LOG_FILE, BPMNAssertions.ERROR_DEPLOYMENT.toString() + "\n" + BPMNAssertions.ERROR_RUNTIME.toString() +"\n" + BPMNAssertions.SCRIPT_task1.toString());
        FileTasks.replaceLogFileContent(listToReplace, BPMNAssertions.ERROR_GENERIC.toString(), PATH_LOG_FILE);
        assertEquals(listExpected, FileTasks.readAllLines(PATH_LOG_FILE));
    }

}
