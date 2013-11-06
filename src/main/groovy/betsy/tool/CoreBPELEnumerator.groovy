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

        List<BetsyProcess> processes = new ProcessRepository().getByNames(["BASIC_ACTIVITIES", "SCOPES", "STRUCTURED_ACTIVITIES"] as String[])

        CoreBPEL.XSL_SHEETS.each { transformation ->

            Path transformationDirectory = outputFolder.resolve(transformation)
            Files.createDirectories(transformationDirectory)

            processes.each { process ->

                Path processDirectory = transformationDirectory.resolve(process.bpelFileNameWithoutExtension)
                Files.createDirectories(processDirectory)

                Path bpelDirectory = processDirectory.resolve("bpel")
                Files.createDirectories(bpelDirectory)

                // copy BPEL file
                Path targetBpelFilePath = bpelDirectory.resolve(process.bpelFileName)
                Files.copy(Paths.get(process.bpelFilePath), targetBpelFilePath)

                // copy WSDL files and XSD files
                process.wsdlPaths.each { wsdl ->
                    Path wsdlPath = Paths.get(wsdl)
                    Files.copy(wsdlPath, processDirectory.resolve(wsdlPath.last().toString()))
                }

                Path tmpDirectory = processDirectory.resolve("tmp")
                Files.createDirectories(tmpDirectory)

                new CoreBPEL(temporaryDirectory: tmpDirectory.toFile(), bpelFilePath: targetBpelFilePath).toCoreBPEL([transformation])
            }

        }

    }

}
