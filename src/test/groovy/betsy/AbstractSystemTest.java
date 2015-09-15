package betsy;

import betsy.common.tasks.FileTasks;
import org.junit.After;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AbstractSystemTest {

    @After
    public void cleanupTestFolders() throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("."), "test-*")) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    try {
                        FileTasks.deleteDirectory(path);
                    } catch (Exception ignored) {
                        // try to clean up even if it would fail
                    }
                }
            }
        }
    }

    @After
    public void cleanupEngineFolders() throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(".").resolve("server"))) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    try {
                        FileTasks.deleteDirectory(path);
                    } catch (Exception ignored) {
                        // try to clean up even if it would fail
                    }
                }
            }
        }
    }

}
