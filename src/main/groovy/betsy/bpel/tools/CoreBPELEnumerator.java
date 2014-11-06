package betsy.bpel.tools;

import betsy.bpel.model.BPELProcess;
import configuration.bpel.BPELProcessRepository;
import corebpel.CoreBPEL;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

/**
 * Applies all CoreBPEL transformations separately on every BPEL test case. The output is stored in corebpel-results.
 */
public class CoreBPELEnumerator {
    public static void main(String[] args) throws IOException, TransformerException {
        Path outputFolder = Paths.get("corebpel-results");

        if (Files.exists(outputFolder)) {
            Files.walkFileTree(outputFolder, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc != null) {
                        throw exc;
                    }

                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }

            });
        }

        Files.createDirectories(outputFolder);

        String[] groups = new String[]{"BASIC_ACTIVITIES", "SCOPES", "STRUCTURED_ACTIVITIES"};
        List<BPELProcess> processes = new BPELProcessRepository().getByNames(groups);

        for (String transformation : CoreBPEL.XSL_SHEETS) {

            Path transformationDirectory = outputFolder.resolve(transformation);
            Files.createDirectories(transformationDirectory);

            for (BPELProcess process : processes) {

                Path processDirectory = transformationDirectory.resolve(process.getName());
                Files.createDirectories(processDirectory);

                Path bpelDirectory = processDirectory.resolve("bpel");
                Files.createDirectories(bpelDirectory);

                // copy BPEL file
                Path targetBpelFilePath = bpelDirectory.resolve(process.getProcessFileName());
                Files.copy(process.getProcess(), targetBpelFilePath);

                // copy WSDL files and XSD files
                for (Path wsdl : process.getWsdlPaths()) {
                    Files.copy(wsdl, processDirectory.resolve(wsdl.getFileName().toString()));
                }


                Path tmpDirectory = processDirectory.resolve("tmp");
                Files.createDirectories(tmpDirectory);

                new CoreBPEL(tmpDirectory, targetBpelFilePath).toCoreBPEL(transformation);
            }


        }


    }

}
