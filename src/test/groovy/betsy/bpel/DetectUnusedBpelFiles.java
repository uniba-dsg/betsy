package betsy.bpel;

import betsy.common.model.EngineIndependentProcess;
import betsy.common.util.FileTypes;
import configuration.bpel.BPELProcessRepository;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class DetectUnusedBpelFiles {

    @Test
    public void detectUnusedBpelFiles() throws IOException {
        BPELProcessRepository processRepository = BPELProcessRepository.INSTANCE;
        List<EngineIndependentProcess> processed = processRepository.getByName("ALL");

        List<Path> bpelFiles = getBetsyProcessesPaths(processed);
        List<Path> bpelFilesInSrcTestDir = getBpelFiles(Paths.get("src/test"));

        bpelFilesInSrcTestDir.removeAll(bpelFiles);

        Assert.assertEquals("all bpel files should be referenced", "[]", bpelFilesInSrcTestDir.toString());
    }

    private List<Path> getBetsyProcessesPaths(List<EngineIndependentProcess> processed) {
        List<Path> bpelFiles = new LinkedList<>();
        for(EngineIndependentProcess process : processed) {
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
