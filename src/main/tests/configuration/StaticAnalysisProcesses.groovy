package configuration

import betsy.data.BetsyProcess
import betsy.data.TestCase

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
                String testDir = dir.path.replace("\\", "/").replace("src/main/tests/", "")

                List<String> wsdls = []
                dir.list().each { elem ->
                    if (elem.endsWith(".wsdl")) {
                        wsdls.add("${testDir}/${elem}")
                    }
                }

                result.add(new BetsyProcess(
                        bpel: "${testDir}/${dir.list().find { String elem -> elem.endsWith(".bpel") }}",
                        wsdls: wsdls,
                        testCases: [new TestCase().checkFailedDeployment()]
                ))
            }
        }

        return result
    }

    public static List<BetsyProcess> STATIC_ANALYSIS = getStaticAnalysisProcesses()

}
