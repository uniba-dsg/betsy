package configuration.bpel

import betsy.bpel.model.BPELProcess
import betsy.common.model.TestCase

import java.nio.file.Path
import java.nio.file.Paths

class BPELProcessBuilder {

    public static Path PATH_PREFIX = Paths.get("src/main/tests/files/bpel")
    public static final Path testInterface = PATH_PREFIX.resolve("TestInterface.wsdl")
    public static final Path partnerInterface = PATH_PREFIX.resolve("TestPartner.wsdl")

    public static final int UNDECLARED_FAULT_CODE = -5
    public static final int DECLARED_FAULT_CODE = -6

    public static BPELProcess buildPatternProcess(String name, List<TestCase> testCases) {
        new BPELProcess(bpel: PATH_PREFIX.resolve("cfpatterns/${name}.bpel"),
                wsdls: [testInterface],
                testCases: testCases
        )
    }

    public static BPELProcess buildPatternProcessWithPartner(String name, List<TestCase> testCases) {
        new BPELProcess(bpel: PATH_PREFIX.resolve("cfpatterns/${name}.bpel"),
                wsdls: [testInterface, partnerInterface],
                testCases: testCases
        )
    }

    public static BPELProcess buildProcess(String name, List<TestCase> testCases) {
        new BPELProcess(bpel: PATH_PREFIX.resolve("${name}.bpel"),
                wsdls: [testInterface],
                testCases: testCases
        )
    }

    public static BPELProcess buildProcessWithXsd(String name, List<TestCase> testCases) {
        new BPELProcess(bpel: PATH_PREFIX.resolve("${name}.bpel"),
                wsdls: [testInterface],
                testCases: testCases,
                additionalFiles: [PATH_PREFIX.resolve("basic/months.xsd")],
        )
    }

    public static BPELProcess buildProcessWithPartner(String name, List<TestCase> testCases) {
        new BPELProcess(bpel: PATH_PREFIX.resolve("${name}.bpel"),
                wsdls: [testInterface, partnerInterface],
                testCases: testCases
        )
    }

    public static BPELProcess buildProcessWithXslt(String name, List<TestCase> testCases) {
        new BPELProcess(bpel: PATH_PREFIX.resolve("${name}.bpel"),
                wsdls: [testInterface],
                additionalFiles: [PATH_PREFIX.resolve("basic/echo.xslt"),
                        PATH_PREFIX.resolve("basic/notCompileable.xslt")],
                testCases: testCases
        )
    }

    public static BPELProcess buildStructuredActivityProcess(String name, List<TestCase> testCases) {
        buildProcess("structured/${name}", testCases)
    }

    public static BPELProcess buildScopeProcess(String name, List<TestCase> testCases) {
        buildProcess("scopes/${name}", testCases)
    }

    public static BPELProcess buildBasicActivityProcess(String name, List<TestCase> testCases) {
        buildProcess("basic/${name}", testCases)
    }

    public
    static BPELProcess buildStructuredActivityProcess(String name, String description, List<TestCase> testCases) {
        BPELProcess process = buildStructuredActivityProcess(name, testCases)
        process.description = description
        return process
    }

    public static BPELProcess buildScopeProcess(String name, String description, List<TestCase> testCases) {
        BPELProcess process = buildScopeProcess(name, testCases)
        process.description = description
        return process
    }

    public static BPELProcess buildBasicActivityProcess(String name, String description, List<TestCase> testCases) {
        BPELProcess process = buildBasicActivityProcess(name, testCases)
        process.description = description
        return process
    }

    public static BPELProcess buildProcessWithXsd(String name, String description, List<TestCase> testCases) {
        BPELProcess process = buildProcessWithXsd(name, testCases)
        process.description = description
        return process
    }

    public static BPELProcess buildProcessWithPartner(String name, String description, List<TestCase> testCases) {
        BPELProcess process = buildProcessWithPartner(name, testCases)
        process.description = description
        return process
    }

    public static BPELProcess buildProcessWithXslt(String name, String description, List<TestCase> testCases) {
        BPELProcess process = buildProcessWithXslt(name, testCases)
        process.description = description
        return process
    }


}
