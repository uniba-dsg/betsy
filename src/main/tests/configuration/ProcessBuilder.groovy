package configuration

import betsy.data.BetsyProcess
import betsy.data.TestCase

import java.nio.file.Path
import java.nio.file.Paths

class ProcessBuilder {

    public static Path PATH_PREFIX = Paths.get("src/main/tests")
    public static final Path testInterface = PATH_PREFIX.resolve("files/TestInterface.wsdl")
    public static final Path partnerInterface = PATH_PREFIX.resolve("files/TestPartner.wsdl")

    public static final int UNDECLARED_FAULT_CODE = -5
    public static final int DECLARED_FAULT_CODE = -6

    public static BetsyProcess buildPatternProcess(String name, List<TestCase> testCases) {
        new BetsyProcess(bpel: PATH_PREFIX.resolve("files/cfpatterns/${name}.bpel"),
                wsdls: [testInterface],
                testCases: testCases
        )
    }

    public static BetsyProcess buildPatternProcessWithPartner(String name, List<TestCase> testCases) {
        new BetsyProcess(bpel: PATH_PREFIX.resolve("files/cfpatterns/${name}.bpel"),
                wsdls: [testInterface, partnerInterface],
                testCases: testCases
        )
    }

    public static BetsyProcess buildProcess(String name, List<TestCase> testCases) {
        new BetsyProcess(bpel: PATH_PREFIX.resolve("files/${name}.bpel"),
                wsdls: [testInterface],
                testCases: testCases
        )
    }

    public static BetsyProcess buildProcessWithXsd(String name, List<TestCase> testCases) {
        new BetsyProcess(bpel: PATH_PREFIX.resolve("files/${name}.bpel"),
                wsdls: [testInterface],
                testCases: testCases,
                additionalFiles: [PATH_PREFIX.resolve("files/basic/months.xsd")],
        )
    }

    public static BetsyProcess buildProcessWithPartner(String name, List<TestCase> testCases) {
        new BetsyProcess(bpel: PATH_PREFIX.resolve("files/${name}.bpel"),
                wsdls: [testInterface, partnerInterface],
                testCases: testCases
        )
    }

    public static BetsyProcess buildProcessWithXslt(String name, List<TestCase> testCases) {
        new BetsyProcess(bpel: PATH_PREFIX.resolve("files/${name}.bpel"),
                wsdls: [testInterface],
                additionalFiles: [PATH_PREFIX.resolve("files/basic/echo.xslt"),
                        PATH_PREFIX.resolve("files/basic/notCompileable.xslt")],
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

    public
    static BetsyProcess buildStructuredActivityProcess(String name, String description, List<TestCase> testCases) {
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
