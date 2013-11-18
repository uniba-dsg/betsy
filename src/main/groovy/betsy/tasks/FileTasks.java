package betsy.tasks;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class FileTasks {

    private static final Logger log = Logger.getLogger(FileTasks.class);

    public static void createFile(Path file, String content) {
        String[] lines = content.split("\n");

        String showOutput = "" + lines.length + " lines";
        if (lines.length < 5) {
            showOutput = content.replace("\n", "@LINE_BREAK@");
        }

        String message = "Creating file " + file + " with " + showOutput;
        log.info(message);
        try {
            Files.write(file, Arrays.asList(lines), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(message, e);
        }
    }

    public static void assertDirectory(Path dir) {
        if (!Files.isDirectory(dir)) {
            throw new IllegalArgumentException("the path " + dir + " is no directory");
        }
    }

    public static void assertExecutableFile(Path p) {
        assertFile(p);

        if (!Files.isExecutable(p)) {
            throw new IllegalArgumentException("the file " + p + " is not executable but should be executed");
        }
    }

    public static void assertFile(Path p) {
        if (!Files.isRegularFile(p)) {
            throw new IllegalArgumentException("the file " + p + " is no file");
        }
    }
}
