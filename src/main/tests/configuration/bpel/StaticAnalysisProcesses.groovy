package configuration.bpel

import betsy.bpel.model.BetsyProcess
import betsy.bpel.model.BPELTestCase

import java.nio.file.Path
import java.nio.file.Paths

class StaticAnalysisProcesses {

    public static List<BetsyProcess> getStaticAnalysisProcesses() {
        String path = "src/main/tests/files/bpel/sa-rules"

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
                        testCases: [new BPELTestCase().checkFailedDeployment()]
                ))
            }
        }

        return result
    }

    public static Map<String, List<BetsyProcess>> getGroupsPerRuleForSAProcesses(List<BetsyProcess> processes) {
        Map<String, List<BetsyProcess>> result = new HashMap<>();

        for(int i = 1; i <= 95; i++) {
            String rule = convertIntegerToSARuleNumber(i)
            List<BetsyProcess> processList = processes.findAll { it.name.startsWith(rule) }

            if(!processList.isEmpty()) {
                result.put(rule, processList);
            }
        }


        return result;
    }

    private static String convertIntegerToSARuleNumber(int i) {
        String ruleNumber = "" + i;
        if (ruleNumber.length() == 1) {
            ruleNumber = "0" + ruleNumber;
        }
        String rule = "SA000" + ruleNumber;
        rule
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
