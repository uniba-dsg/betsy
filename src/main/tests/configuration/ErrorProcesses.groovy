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
            0 : "happy-path.xml",
	    11001 : "content-empty.xml",
	    12001 : "content-simple-text.xml",
	    12002 : "content-simple-stackTrace.xml",
	    13101 : "content-xml-notWellFormed-missingClosingTag.xml",
	    13102 : "content-xml-notWellFormed-twoRootElements.xml",
	    13103 : "content-xml-notWellFormed-missingAttributeClosing.xml",
	    13211 : "content-xml-wrongFormat-elementWithoutContent-emptyElementShort.xml",
	    13212 : "content-xml-wrongFormat-elementWithoutContent-emptyElementLong.xml",
	    13221 : "content-xml-wrongFormat-dataFormats-stringInstreadOfInteger.xml",
	    13222 : "content-xml-wrongFormat-dataFormats-doubleInsteadOfInteger.xml",
	    13223 : "content-xml-wrongFormat-dataFormats-doubleWithCommaInsteadOfInteger.xml",
	    13231 : "content-xml-wrongFormat-namespace-noNamespace.xml",
	    13232 : "content-xml-wrongFormat-namespace-partiallyUsageOfNamespace.xml",
	    13233 : "content-xml-wrongFormat-namespace-wrongNamespace.xml",
	    13234 : "content-xml-wrongFormat-namespace-unusedPrefix.xml",
	    13241 : "content-xml-wrongFormat-additionalContent-element.xml",
	    13242 : "content-xml-wrongFormat-additionalContent-attribute.xml"
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
