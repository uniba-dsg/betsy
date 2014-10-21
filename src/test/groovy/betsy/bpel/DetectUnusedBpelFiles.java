package betsy.bpel;

import betsy.bpel.model.BetsyProcess;
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
        BPELProcessRepository processRepository = new BPELProcessRepository();
        List<BetsyProcess> processed = processRepository.getByName("ALL");

        List<Path> bpelFiles = getBetsyProcessesPaths(processed);
        List<Path> bpelFilesInSrcTestDir = getBpelFiles(Paths.get("src/test"));

        bpelFilesInSrcTestDir.removeAll(bpelFiles);

        Assert.assertEquals("all bpel files should be referenced", "[]", bpelFilesInSrcTestDir.toString());
    }

    private List<Path> getBetsyProcessesPaths(List<BetsyProcess> processed) {
        List<Path> bpelFiles = new LinkedList<>();
        for(BetsyProcess process : processed) {
            bpelFiles.add(process.getBpelFilePath());
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
                } else if (Files.isRegularFile(path) && path.toString().endsWith(".bpel")) {
                    result.add(path);
                }
            }
        }

        return result;
    }

}
