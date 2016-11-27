package loader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import pebl.xsd.PEBL;

public class PEBLStoreFilesAlongMain {

    public static void main(String[] args) throws JAXBException {
        final Path path = Paths.get(args[0]);
        final PEBL pebl = PEBL.from(path);
        copyFilesRelative(pebl, path.toAbsolutePath());
        pebl.writeTo(path.toAbsolutePath().getParent());
    }

    static void copyFilesRelative(PEBL pebl, Path path) {
        final Path parent = path.getParent();
        pebl.benchmark.tests.forEach(t -> {
            final String id = t.getId();
            final List<Path> files = t.getFiles()
                    .stream()
                    .map(file -> copyAndGetRelativePath(id, file, s -> "tests/" + s + "/files", parent))
                    .collect(Collectors.toList());

            assertNoDuplicates(id, files);

            t.getFiles().clear();
            t.getFiles().addAll(files);

            t.setProcess(copyAndGetRelativePath(id, t.getProcess(), s -> "tests/" + s + "/process", parent));
        });

        pebl.result.testResults.forEach(t -> {
            final String id = t.getId();
            final List<Path> files = t.getFiles()
                    .stream()
                    .map(file -> copyAndGetRelativePath(id, file, s -> "testResults/" + s + "/files", parent))
                    .collect(Collectors.toList());

            assertNoDuplicates(id, files);

            t.getFiles().clear();
            t.getFiles().addAll(files);

            final List<Path> logFiles = t.getLogFiles()
                    .stream()
                    .map(file -> copyAndGetRelativePath(id, file, s -> "testResults/" + s + "/logFiles", parent))
                    .collect(Collectors.toList());

            assertNoDuplicates(id, files);

            t.getLogFiles().clear();
            t.getLogFiles().addAll(logFiles);
        });
    }

    private static void assertNoDuplicates(String id, List<Path> files) {
        if(new HashSet<>(files).size() < files.size()) {
            throw new IllegalStateException("Duplicates detected in " + id + ": " + files);
        }
    }

    private static Path copyAndGetRelativePath(String id, Path file, Function<String, String> testfiles, Path enclosingFolder) {
        Path relativePath = enclosingFolder.resolve("data").resolve(testfiles.apply(hash(id))).resolve(file.getFileName()).toAbsolutePath();
        Path currentFile = enclosingFolder.resolve(file);
        if (!currentFile.equals(relativePath)) {
            try {
                Files.createDirectories(relativePath.getParent());
                Files.copy(currentFile, relativePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        return relativePath;
    }

    private static String hash(String value) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        try {
            digest.update(value.getBytes("utf8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        byte[] digestBytes = digest.digest();
        String digestStr = javax.xml.bind.DatatypeConverter.printHexBinary(digestBytes);

        return digestStr;
    }
}
