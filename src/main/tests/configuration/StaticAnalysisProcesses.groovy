package configuration

import betsy.data.BetsyProcess
import betsy.data.TestCase

import java.nio.file.Path
import java.nio.file.Paths

class StaticAnalysisProcesses {

    public static List<BetsyProcess> getStaticAnalysisProcesses() {
        String path = "src/main/tests/sa-rules"

        if (!new File(path).exists()) {
            return []
        }

        List<BetsyProcess> result = []

        new File(path).eachDirRecurse { dir ->
            boolean isTestDirectory = dir.list().any({ String elem -> elem.endsWith(".bpel") })
            if (isTestDirectory) {
                String testDir = dir.path.replace("\\", "/")

                result.add(new BetsyProcess(
                        bpel: Paths.get("${testDir}/${dir.list().find { String elem -> elem.endsWith(".bpel") }}"),
                        wsdls: createWSDLPaths(dir, testDir),
                        additionalFiles: createXSDPaths(dir, testDir),
                        testCases: [new TestCase().checkFailedDeployment()]
                ))
            }
        }

        return result
    }

    private static ArrayList<Path> createXSDPaths(File dir, String testDir) {
        List<Path> xsds = []
        dir.list().each { elem ->
            if (elem.endsWith(".xsd")) {
                xsds.add(Paths.get("${testDir}/${elem}"))
            }
        }
        xsds
    }

    private static ArrayList<Path> createWSDLPaths(File dir, String testDir) {
        List<Path> wsdls = []
        dir.list().each { elem ->
            if (elem.endsWith(".wsdl")) {
                wsdls.add(Paths.get("${testDir}/${elem}"))
            }
        }
        wsdls
    }

    public static List<BetsyProcess> STATIC_ANALYSIS = getStaticAnalysisProcesses()

}
