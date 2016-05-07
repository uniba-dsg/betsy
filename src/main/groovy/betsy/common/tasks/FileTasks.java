package betsy.common.tasks;

import com.google.common.base.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.tools.ant.taskdefs.Move;
import org.apache.tools.ant.taskdefs.Replace;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

public final class FileTasks {

    private static final Logger LOGGER = Logger.getLogger(FileTasks.class);

    private FileTasks() {}

    public static void createFile(Path file, String content) {
        mkdirs(file.getParent());

        // normalize string to unix line breaks
        content = content.replaceAll("\\r\\n", "\n");

        String[] lines = content.split("\n");

        String showOutput = Integer.toString(lines.length) + " lines";
        if (lines.length < 5) {
            showOutput = content.replace("\n", "@LINE_BREAK@");
        }

        String message = "Creating file " + file.toAbsolutePath() + " with " + showOutput;
        LOGGER.info(message);
        try {
            Files.write(file, Arrays.asList(lines), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(message, e);
        }
    }

    public static void deleteDirectory(Path directory) {
        LOGGER.info("Deleting directory " + directory.toAbsolutePath());
        if (Files.isDirectory(directory)) {
            if (!FileUtils.deleteQuietly(directory.toAbsolutePath().toFile())) {
                LOGGER.info("Deletion failed -> retrying after short wait");
                WaitTasks.sleep(5000);

                try {
                    FileUtils.deleteDirectory(directory.toAbsolutePath().toFile());
                } catch (FileNotFoundException ignore) {
                    LOGGER.info("File " + directory.toAbsolutePath().toFile() + " could not be found. Ignoring deletion", ignore);
                } catch (IOException e) {
                    LOGGER.info("Retry of deletion also failed.");
                    throw new IllegalStateException("could not delete directory " + directory, e);
                }
            }
        } else {
            LOGGER.info("Directory is already deleted!");
        }
    }

    public static void mkdirs(Path dir) {
        try {
            LOGGER.info("Creating directory " + dir.toAbsolutePath());
            if (Files.isDirectory(dir)) {
                LOGGER.info("Directory already there - skipping creation");
            } else {
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            throw new IllegalStateException("could not create directory " + dir.toAbsolutePath(), e);
        }
    }

    public static void assertDirectory(Path dir) {
        if (!Files.isDirectory(dir)) {
            throw new IllegalArgumentException("the path " + dir.toAbsolutePath() + " is no directory");
        }
    }

    public static void assertExecutableFile(Path p) {
        assertFile(p);

        if (!Files.isExecutable(p)) {
            throw new IllegalArgumentException("the file " + p.toAbsolutePath() + " is not executable but should be executed");
        }
    }

    public static void assertFile(Path p) {
        if (!Files.isRegularFile(p)) {
            throw new IllegalArgumentException("the file " + p + " is no file");
        }
    }

    public static void assertFileExtension(Path path, String extension) {
        assertFile(path);

        if (!path.getFileName().toString().endsWith(extension)) {
            throw new IllegalArgumentException("the file " + path + " does not have the extension " + extension);
        }
    }

    public static List<String> readAllLines(Path path) {
        try {
            return Files.readAllLines(path, Charsets.UTF_8);
        } catch (IOException e) {
            try {
                return Files.readAllLines(path, Charsets.ISO_8859_1);
            } catch (IOException e1) {
                throw new RuntimeException("could not read file with either ISO 8859 1 or UTF 8" + path, e1);
            }
        }
    }

    public static void deleteLine(Path path, int lineNumber) {
        LOGGER.info("Deleting line #" + lineNumber + " from file " + path.toAbsolutePath());

        try {
            // line numbers start with 1, not with 0!
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            LOGGER.info("File has " + lines.size() + " lines -> removing #" + lineNumber + " with content " + lines.get(lineNumber - 1));

            lines.remove(lineNumber - 1);

            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not delete line #" + lineNumber + " in file " + path, e);
        }
    }

    public static void replaceLine(Path path, String oldContent, String newLineContent) {
        LOGGER.info("Replacing contents of a line which has exactly " + oldContent + " from file " + path.toAbsolutePath() + " with the new content " + newLineContent);

        try {
            // line numbers start with 1, not with 0!
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            LOGGER.info("File has " + lines.size() + " lines");

            int index = findIndex(oldContent, lines);
            if (index == -1) {
                throw new IllegalStateException("Could not find a line with content [" + oldContent + "] in file " + path);
            }

            lines.set(index, newLineContent);

            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not replace line in file " + path, e);
        }
    }

    private static int findIndex(String oldContent, List<String> lines) {
        int line = -1;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).trim().equals(oldContent)) {
                line = i;
            }
        }
        return line;
    }

    public static void deleteFile(Path file) {
        LOGGER.info("Deleting file " + file.toAbsolutePath());

        if (!Files.isRegularFile(file)) {
            LOGGER.info("File does not exist - must not be deleted");
            return;
        }

        try {
            Files.delete(file);
        } catch (IOException e) {
            throw new IllegalStateException("Could not delete file " + file, e);
        }
    }

    public static void copyFileIntoFolder(Path file, Path targetFolder) {
        assertFile(file);
        assertDirectory(targetFolder);

        try {
            LOGGER.info("Copying file " + file.toAbsolutePath() + " into folder " + targetFolder.toAbsolutePath());
            Files.copy(file, targetFolder.resolve(file.getFileName()));
        } catch (IOException e) {
            throw new IllegalStateException("Could not copy file " + file + " into folder " + targetFolder, e);
        }
    }

    public static void copyFileIntoFolderAndOverwrite(Path file, Path targetFolder) {
        assertFile(file);
        assertDirectory(targetFolder);

        try {
            LOGGER.info("Copying file " + file.toAbsolutePath() + " into folder " + targetFolder.toAbsolutePath());
            Files.copy(file, targetFolder.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Could not copy file " + file + " into folder " + targetFolder, e);
        }
    }

    public static void copyFileContentsToNewFile(Path source, Path target) {
        assertFile(source);

        try {
            LOGGER.info("Copying contents of file " + source.toAbsolutePath() + " to file " + target.toAbsolutePath());
            List<String> lines = Files.readAllLines(source);
            mkdirs(target.getParent());
            Files.write(target, lines, StandardCharsets.UTF_8);
            LOGGER.info("Copying contents of file " + source.toAbsolutePath() + " to file " + target.toAbsolutePath() + " successful");
        } catch (IOException e) {
            throw new IllegalStateException("Could not copy contents of file " + source + " to file " + target, e);
        }
    }

    public static boolean hasFile(Path file) {
        LOGGER.info("Checking for the existence of file " + file.toAbsolutePath());
        return Files.isRegularFile(file);
    }

    public static boolean hasFolder(Path folder) {
        LOGGER.info("Checking for the existence of folder " + folder.toAbsolutePath());
        return Files.isDirectory(folder);
    }

    public static boolean hasNoFile(Path file) {
        LOGGER.info("Checking for the absence of file " + file.toAbsolutePath());
        return !Files.isRegularFile(file);
    }

    public static boolean hasFileSpecificSubstring(Path path, String substring) {
        LOGGER.info("Searching for substring " + substring + " in file " + path.toAbsolutePath());
        try {
            assertFile(path);
            List<String> lines = readAllLines(path);

            for (String line : lines) {
                if (line.contains(substring)) {
                    return true;
                }
            }

        } catch (Exception e) {
            LOGGER.info("Could not read file " + path, e);
        }
        return false;
    }

    public static void move(Path from, Path to) {
        LOGGER.info("Moving file " + from + " to " + to);
        try {
            Files.move(from, to);
        } catch (IOException e) {
            throw new IllegalStateException("Could not move file " + from + " to " + to, e);
        }
    }

    public static void moveFolderWithForcedCleanup(Path from, Path to) {
        LOGGER.info("Moving file " + from + " to " + to);

        Move move = new Move();
        move.setFile(from.toFile());
        move.setTofile(to.toFile());
        move.setForce(true);
        move.setPerformGcOnFailedDelete(true);

        move.setTaskName("move");
        move.setProject(AntUtil.builder().getProject());

        move.execute();
    }

    public static Path findFirstMatchInFolder(Path folder, String glob) {
        LOGGER.info("Finding first file in dir [" + folder + "] with pattern [" + glob + "]");

        try {
            FileTasks.assertDirectory(folder);
        } catch (Exception ignore) {
            return null;
        }

        try {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, glob)) {
                return stream.iterator().next();
            }
        } catch (IOException e) {
            throw new RuntimeException("could not iterate in folder " + folder, e);
        }
    }

    public static List<Path> findAllInFolder(Path folder) {
        LOGGER.info("Finding all files in dir [" + folder + "]");

        List<Path> result = new LinkedList<>();

        try {
            FileTasks.assertDirectory(folder);
        } catch (Exception ignore) {
            return result;
        }

        try {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
                for (Path p : stream) {
                    result.add(p);
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("could not iterate in folder " + folder, e);
        }
    }

    public static List<Path> findAllInFolder(Path folder, String glob) {
        LOGGER.info("Finding all files in dir [" + folder + "] with pattern [" + glob + "]");

        List<Path> result = new LinkedList<>();

        try {
            FileTasks.assertDirectory(folder);
        } catch (Exception ignore) {
            return result;
        }

        try {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, glob)) {
                for (Path p : stream) {
                    result.add(p);
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("could not iterate in folder " + folder, e);
        }
    }

    public static void copyFilesInFolderIntoOtherFolder(Path from, Path to) {
        LOGGER.info("Copying files from " + from + " to folder " + to);

        assertDirectory(from);
        assertDirectory(to);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(from)) {
            for (Path path : stream) {
                if (Files.isRegularFile(path)) {
                    FileTasks.copyFileIntoFolder(path, to);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not copy files from " + from + " to " + to, e);
        }
    }

    public static void replaceTokensInFile(Path targetFile, Map<String, ?> replacements) {
        LOGGER.info("Replacing tokens in " + targetFile.toAbsolutePath() + " {" + new PrettyPrintingMap<>(replacements).toString() + "}");

        assertFile(targetFile);

        Replace replaceTask = new Replace();
        replaceTask.setFile(targetFile.toFile());

        for (Map.Entry<String, ?> stringEntry : replacements.entrySet()) {
            String value = String.valueOf(stringEntry.getValue());
            Replace.Replacefilter filter = replaceTask.createReplacefilter();
            filter.setToken(stringEntry.getKey());
            filter.setValue(value);
        }

        replaceTask.setTaskName("replace");
        replaceTask.setProject(AntUtil.builder().getProject());

        replaceTask.validateReplacefilters();
        replaceTask.execute();
    }

    public static void replaceTokensInFolder(Path targetFile, String token, Object replacement) {
        Map<String, Object> map = new HashMap<>();
        map.put(token, replacement);
        replaceTokensInFolder(targetFile, map);
    }

    public static void replaceTokensInFolder(Path targetFile, Map<String, ?> replacements) {
        LOGGER.info("Replacing tokens in " + targetFile.toAbsolutePath() + " {" + new PrettyPrintingMap<>(replacements).toString() + "}");

        assertDirectory(targetFile);

        Replace replaceTask = new Replace();
        replaceTask.setDir(targetFile.toFile());

        for (Map.Entry<String, ?> stringEntry : replacements.entrySet()) {
            String value = String.valueOf(stringEntry.getValue());
            Replace.Replacefilter filter = replaceTask.createReplacefilter();
            filter.setToken(stringEntry.getKey());
            filter.setValue(value);
        }

        replaceTask.setTaskName("replace");
        replaceTask.setProject(AntUtil.builder().getProject());

        replaceTask.validateReplacefilters();
        replaceTask.execute();
    }

    public static void replaceTokenInFile(Path targetFile, String token, String value) {
        Map<String, String> replacements = new HashMap<>();
        replacements.put(token, value);
        replaceTokensInFile(targetFile, replacements);
    }


    public static String getFilenameWithoutExtension(Path file) {
        assertFile(file);

        String filename = file.getFileName().toString();
        return getFilenameWithoutExtension(filename);
    }

    public static String getFilenameWithoutExtension(String filename) {
        String[] elements = filename.split("\\.");

        if (elements.length == 1) {
            return elements[0]; // no . in filename
        }

        StringJoiner joiner = new StringJoiner(".");
        for (int i = 0; i < elements.length - 1; i++) {
            joiner.add(elements[i]);
        }

        return joiner.toString();
    }

    public static void copyMatchingFilesIntoFolder(Path from, Path to, String globPattern) {
        assertDirectory(from);
        mkdirs(to);

        try (DirectoryStream<Path> paths = Files.newDirectoryStream(from, globPattern)) {
            for (Path path : paths) {
                copyFileIntoFolder(path, to);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not copy files [" + globPattern + "] from " + from + " to " + to);
        }

    }

    public static void replaceLogFileContent(List<String> listToReplace, String replacement, Path logPath) {
        LOGGER.info("Check if content of " + logPath.getFileName().toString() + " needs to be replaced.");
        List<String> allLinesOfLogFile = readAllLines(logPath);

        if (allLinesOfLogFile.removeAll(listToReplace)) {
            try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(logPath))) {
                allLinesOfLogFile.forEach(printWriter::println);
                printWriter.println(replacement);
                LOGGER.info("Replaced " + listToReplace.toString() + " with " + replacement + " in " + logPath.getFileName().toString() + ".");
            } catch (IOException e) {
                LOGGER.warn("Error while writing to " + logPath.getFileName() + ".");
            }
        }
    }
}
