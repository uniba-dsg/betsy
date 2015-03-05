package configuration.bpel;

import betsy.bpel.model.BPELProcess;
import betsy.bpel.model.BPELTestCase;
import betsy.common.util.FileTypes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BPELProcessBuilder {

    public static BPELProcess buildPatternProcess(final String name, List<BPELTestCase> testCases) {
        BPELProcess process = new BPELProcess();
        process.setProcess(PATH_PREFIX.resolve("cfpatterns/" + name + FileTypes.BPEL));
        process.setWsdls(new ArrayList<>(Collections.singletonList(testInterface)));
        process.setTestCases(testCases);
        return process;
    }

    public static BPELProcess buildPatternProcessWithPartner(final String name, List<BPELTestCase> testCases) {
        BPELProcess process = new BPELProcess();
        process.setProcess(PATH_PREFIX.resolve("cfpatterns/" + name + FileTypes.BPEL));
        process.setWsdls(new ArrayList<>(Arrays.asList(testInterface, partnerInterface)));
        process.setTestCases(testCases);
        return process;
    }

    public static BPELProcess buildProcess(final String name, List<BPELTestCase> testCases) {
        BPELProcess process = new BPELProcess();
        process.setProcess(PATH_PREFIX.resolve(name + FileTypes.BPEL));
        process.setWsdls(new ArrayList<>(Collections.singletonList(testInterface)));
        process.setTestCases(testCases);
        return process;
    }

    public static BPELProcess buildProcessWithXsd(final String name, List<BPELTestCase> testCases) {
        BPELProcess process = new BPELProcess();
        process.setProcess(PATH_PREFIX.resolve(name + FileTypes.BPEL));
        process.setWsdls(new ArrayList<>(Collections.singletonList(testInterface)));
        process.setTestCases(testCases);
        process.setAdditionalFiles(new ArrayList<>(Collections.singletonList(PATH_PREFIX.resolve("basic/months.xsd"))));
        return process;

    }

    public static BPELProcess buildProcessWithPartner(final String name, List<BPELTestCase> testCases) {
        BPELProcess process = new BPELProcess();
        process.setProcess(PATH_PREFIX.resolve(name + FileTypes.BPEL));
        process.setWsdls(new ArrayList<>(Arrays.asList(testInterface, partnerInterface)));
        process.setTestCases(testCases);
        return process;
    }

    public static BPELProcess buildProcessWithXslt(final String name, List<BPELTestCase> testCases) {
        BPELProcess process = new BPELProcess();
        process.setProcess(PATH_PREFIX.resolve(name + FileTypes.BPEL));
        process.setWsdls(new ArrayList<>(Arrays.asList(testInterface, partnerInterface)));
        process.setTestCases(testCases);
        process.setAdditionalFiles(new ArrayList<>(Arrays.asList(PATH_PREFIX.resolve("basic/echo.xslt"), PATH_PREFIX.resolve("basic/notCompileable.xslt"))));

        return process;
    }

    public static BPELProcess buildStructuredActivityProcess(final String name, List<BPELTestCase> testCases) {
        return buildProcess("structured/" + name, testCases);
    }

    public static BPELProcess buildScopeProcess(final String name, List<BPELTestCase> testCases) {
        return buildProcess("scopes/" + name, testCases);
    }

    public static BPELProcess buildBasicActivityProcess(final String name, List<BPELTestCase> testCases) {
        return buildProcess("basic/" + name, testCases);
    }

    public static BPELProcess buildStructuredActivityProcess(String name, String description, List<BPELTestCase> testCases) {
        BPELProcess process = buildStructuredActivityProcess(name, testCases);
        process.setDescription(description);
        return process;
    }

    public static BPELProcess buildScopeProcess(String name, String description, List<BPELTestCase> testCases) {
        BPELProcess process = buildScopeProcess(name, testCases);
        process.setDescription(description);
        return process;
    }

    public static BPELProcess buildBasicActivityProcess(String name, String description, List<BPELTestCase> testCases) {
        BPELProcess process = buildBasicActivityProcess(name, testCases);
        process.setDescription(description);
        return process;
    }

    public static BPELProcess buildProcessWithXsd(String name, String description, List<BPELTestCase> testCases) {
        BPELProcess process = buildProcessWithXsd(name, testCases);
        process.setDescription(description);
        return process;
    }

    public static BPELProcess buildProcessWithPartner(String name, String description, List<BPELTestCase> testCases) {
        BPELProcess process = buildProcessWithPartner(name, testCases);
        process.setDescription(description);
        return process;
    }

    public static BPELProcess buildProcessWithXslt(String name, String description, List<BPELTestCase> testCases) {
        BPELProcess process = buildProcessWithXslt(name, testCases);
        process.setDescription(description);
        return process;
    }

    public static final Path PATH_PREFIX = Paths.get("src/main/tests/files/bpel");
    public static final Path testInterface = PATH_PREFIX.resolve("TestInterface.wsdl");
    public static final Path partnerInterface = PATH_PREFIX.resolve("TestPartner.wsdl");
    public static final int UNDECLARED_FAULT = -5;
    public static final int DECLARED_FAULT = -6;
}
