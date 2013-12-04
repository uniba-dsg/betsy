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

                List<Path> wsdls = []
                dir.list().each { elem ->
                    if (elem.endsWith(".wsdl")) {
                        wsdls.add(Paths.get("${testDir}/${elem}"))
                    }
                }

                result.add(new BetsyProcess(
                        bpel: Paths.get("${testDir}/${dir.list().find { String elem -> elem.endsWith(".bpel") }}"),
                        wsdls: wsdls,
                        testCases: [new TestCase().checkFailedDeployment()]
                ))
            }
        }

        return result
    }

    public static List<BetsyProcess> STATIC_ANALYSIS = getStaticAnalysisProcesses()

}
