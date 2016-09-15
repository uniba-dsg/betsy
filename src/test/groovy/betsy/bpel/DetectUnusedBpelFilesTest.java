package betsy.bpel;

import pebl.test.Test;
import betsy.common.util.FileTypes;
import configuration.bpel.BPELProcessRepository;
import org.junit.Assert;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class DetectUnusedBpelFilesTest {

    @org.junit.Test
    public void detectUnusedBpelFiles() throws IOException {
        BPELProcessRepository processRepository = BPELProcessRepository.INSTANCE;
        List<Test> processed = new LinkedList<>(processRepository.getByName("ALL"));
        processed.addAll(processRepository.getByName("STATIC_ANALYSIS"));
        List<Test> errors = processRepository.getByName("ERRORS");
        processed.addAll(errors);

        List<Path> referencedBpelFiles = getBetsyProcessesPaths(processed);
        List<Path> actualBpelFiles = getBpelFiles(Paths.get("src/main/tests"));

        List<Path> actualBpelFilesThatAreUnreferenced = new LinkedList<>(actualBpelFiles);
        actualBpelFilesThatAreUnreferenced.removeAll(referencedBpelFiles);

        String unreferencedFiles = actualBpelFilesThatAreUnreferenced
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
        Assert.assertEquals("all bpel files should be referenced", "src\\main\\tests\\files\\bpel\\errorsbase\\BackdoorRobustness.bpel\n"
                + "src\\main\\tests\\files\\bpel\\errorsbase\\ImprovedBackdoorRobustness.bpel", unreferencedFiles);
    }

    private List<Path> getBetsyProcessesPaths(List<Test> processed) {
        List<Path> bpelFiles = new LinkedList<>();
        for(Test process : processed) {
            bpelFiles.add(process.getProcess());
        }
        return bpelFiles;
    }

    public static List<Path> getBpelFiles(Path folder) throws IOException {

        List<Path> result = new LinkedList<>();

        try (DirectoryStream<Path> fileStream = Files.newDirectoryStream(folder)) {
            for (Path path : fileStream) {
                if (Files.isDirectory(path)) {
                    // recursion
                    result.addAll(getBpelFiles(path));
                } else if (FileTypes.isBpelFile(path)) {
                    result.add(path);
                }
            }
        }

        return result;
    }

}
