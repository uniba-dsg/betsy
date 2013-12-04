package configuration

import betsy.data.BetsyProcess
import betsy.data.TestCase
import betsy.tasks.FileTasks
import groovy.util.slurpersupport.GPathResult
import groovy.xml.XmlUtil

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ErrorProcesses {

    public static final Map<Integer, String> inputToErrorCode = [
            0 : "happy_path",
            1 : "whatever"
    ]


    public static final List<BetsyProcess> baseProcesses = [
            BasicActivityProcesses.INVOKE_SYNC
    ]

    public static List<BetsyProcess> getProcesses() {

        Path errorsDir = Paths.get("src/main/tests/files/errors")
        FileTasks.deleteDirectory(errorsDir)
        FileTasks.mkdirs(errorsDir)

        List<BetsyProcess> result = new LinkedList<>();

        for (BetsyProcess baseProcess : baseProcesses) {

            for (Map.Entry<Integer, String> entry : inputToErrorCode) {

                BetsyProcess process = (BetsyProcess) baseProcess.clone()

                // copy file
                String filename = "ERR${entry.getKey()}_${entry.getValue()}"
                Path newPath = errorsDir.resolve("${filename}.bpel")
                Files.copy(process.bpel, newPath)

                // update filename
                GPathResult root = new XmlSlurper(false, false).parse(process.bpelFilePath.toFile())
                root.@name = filename
                root.@targetNamespace = filename
                writeXmlFile(newPath, root)

                process.bpel = newPath
                process.testCases = [new TestCase().checkDeployment().sendSync(entry.getKey(), entry.getKey())]

                result.add(process)
            }


        }

        result

    }

    private static void writeXmlFile(Path path, GPathResult root) {
        path.toFile().withPrintWriter { out ->
            out.print(XmlUtil.serialize(root))
        }
    }
}