package configuration

import betsy.data.BetsyProcess
import betsy.data.TestCase

class ProcessBuilder {

    public final int DECLARED_FAULT_CODE = -6

    public final int UNDECLARED_FAULT_CODE = -5

    public static BetsyProcess buildProcess(String name, List<TestCase> testCases) {
        new BetsyProcess(bpel: "files/${name}.bpel",
                wsdls: ["files/TestInterface.wsdl"],
                testCases: testCases
        )
    }

    public static BetsyProcess buildProcessWithXsd(String name, List<TestCase> testCases) {
        new BetsyProcess(bpel: "files/${name}.bpel",
                wsdls: ["files/TestInterface.wsdl"],
                testCases: testCases,
                additionalFiles: ["files/basic/months.xsd"],
        )
    }

    public static BetsyProcess buildProcessWithPartner(String name, List<TestCase> testCases) {
        new BetsyProcess(bpel: "files/${name}.bpel",
                wsdls: ["files/TestInterface.wsdl", "files/TestPartner.wsdl"],
                testCases: testCases
        )
    }

    public static BetsyProcess buildProcessWithXslt(String name, List<TestCase> testCases) {
        new BetsyProcess(bpel: "files/${name}.bpel",
                wsdls: ["files/TestInterface.wsdl"],
                additionalFiles: ["files/basic/echo.xslt", "files/basic/notCompileable.xslt"],
                testCases: testCases
        )
    }

    public static BetsyProcess buildStructuredActivityProcess(String name, List<TestCase> testCases) {
        buildProcess("structured/${name}", testCases)
    }

    public static BetsyProcess buildScopeProcess(String name, List<TestCase> testCases) {
        buildProcess("scopes/${name}", testCases)
    }

    public static BetsyProcess buildBasicActivityProcess(String name, List<TestCase> testCases) {
        buildProcess("basic/${name}", testCases)
    }

    public static BetsyProcess buildStructuredActivityProcess(String name, String description, List<TestCase> testCases) {
        BetsyProcess process = buildStructuredActivityProcess(name, testCases)
        process.description = description
        return process
    }

    public static BetsyProcess buildScopeProcess(String name, String description, List<TestCase> testCases) {
        BetsyProcess process = buildScopeProcess(name, testCases)
        process.description = description
        return process
    }

    public static BetsyProcess buildBasicActivityProcess(String name, String description, List<TestCase> testCases) {
        BetsyProcess process = buildBasicActivityProcess(name, testCases)
        process.description = description
        return process
    }

    public static BetsyProcess buildProcessWithXsd(String name, String description, List<TestCase> testCases) {
        BetsyProcess process = buildProcessWithXsd(name, testCases)
        process.description = description
        return process
    }

    public static BetsyProcess buildProcessWithPartner(String name, String description, List<TestCase> testCases) {
        BetsyProcess process = buildProcessWithPartner(name, testCases)
        process.description = description
        return process
    }

    public static BetsyProcess buildProcessWithXslt(String name, String description, List<TestCase> testCases) {
        BetsyProcess process = buildProcessWithXslt(name, testCases)
        process.description = description
        return process
    }


}
