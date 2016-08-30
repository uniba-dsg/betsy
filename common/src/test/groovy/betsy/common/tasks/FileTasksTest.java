package betsy.common.tasks;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
        listToReplace.add("error-deployment");
        listToReplace.add("error-runtime");

        List<String> listExpected = new ArrayList<>();
        listExpected.add("task1");
        listExpected.add("error-generic");

        FileTasks.createFile(PATH_LOG_FILE, "error-deployment" + "\n" + "error-runtime" +"\n" + "task1");
        FileTasks.replaceLogFileContent(listToReplace, "error-generic", PATH_LOG_FILE);
        assertEquals(listExpected, FileTasks.readAllLines(PATH_LOG_FILE));
    }

}
