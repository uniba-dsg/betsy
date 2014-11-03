package betsy.common.tasks;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.nio.file.Paths;

public class FileTasksTest {

    @Test
    public void testGetFilenameWithoutExtension() {
        assertEquals("", FileTasks.getFilenameWithoutExtension(Paths.get(".gitignore")));
        assertEquals("LICENSE", FileTasks.getFilenameWithoutExtension(Paths.get("LICENSE")));
        assertEquals("build", FileTasks.getFilenameWithoutExtension(Paths.get("build.gradle")));

    }

}
