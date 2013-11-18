package betsy.tool

import betsy.data.BetsyProcess
import configuration.ProcessRepository
import corebpel.CoreBPEL

import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

/**
 * Applies all CoreBPEL transformations separately on every BPEL test case. The output is stored in corebpel-results.
 */
class CoreBPELEnumerator {

    public static void main(String[] args) {
        Path outputFolder = Paths.get("corebpel-results")

        if (Files.exists(outputFolder)) {
            Files.walkFileTree(outputFolder, new SimpleFileVisitor<Path>() {

                @Override
                FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file)
                    return FileVisitResult.CONTINUE
                }

                @Override
                FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc != null) {
                        throw exc
                    }
                    Files.delete(dir)
                    return FileVisitResult.CONTINUE
                }
            })
        }
        Files.createDirectories(outputFolder)

        String[] groups = ["BASIC_ACTIVITIES", "SCOPES", "STRUCTURED_ACTIVITIES"]
        List<BetsyProcess> processes = new ProcessRepository().getByNames(groups)

        for (String transformation : CoreBPEL.XSL_SHEETS) {

            Path transformationDirectory = outputFolder.resolve(transformation)
            Files.createDirectories(transformationDirectory)

            for (BetsyProcess process : processes) {

                Path processDirectory = transformationDirectory.resolve(process.name)
                Files.createDirectories(processDirectory)

                Path bpelDirectory = processDirectory.resolve("bpel")
                Files.createDirectories(bpelDirectory)

                // copy BPEL file
                Path targetBpelFilePath = bpelDirectory.resolve(process.bpelFileName)
                Files.copy(process.bpelFilePath, targetBpelFilePath)

                // copy WSDL files and XSD files
                for (Path wsdl : process.wsdlPaths) {
                    Files.copy(wsdl, processDirectory.resolve(wsdl.last().toString()))
                }

                Path tmpDirectory = processDirectory.resolve("tmp")
                Files.createDirectories(tmpDirectory)

                new CoreBPEL(temporaryDirectory: tmpDirectory, bpelFilePath: targetBpelFilePath).toCoreBPEL([transformation])
            }

        }

    }

}
