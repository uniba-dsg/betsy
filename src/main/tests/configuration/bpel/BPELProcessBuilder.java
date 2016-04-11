package configuration.bpel;

import betsy.bpel.model.BPELProcess;
import betsy.bpel.model.BPELTestCase;
import betsy.common.util.FileTypes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class BPELProcessBuilder {

    public static BPELProcess buildPatternProcess(final String name, BPELTestCase... testCases) {
        BPELProcess process = new BPELProcess();
        process.setProcess(PATH_PREFIX.resolve("cfpatterns/" + name + FileTypes.BPEL));
        process.setWsdls(new ArrayList<>(Collections.singletonList(testInterface)));
        process.setTestCases(Arrays.asList(testCases));
        process.setGroup(Groups.CFPATTERNS);
        return process;
    }

    public static BPELProcess buildPatternProcessWithPartner(final String name, BPELTestCase... testCases) {
        BPELProcess process = new BPELProcess();
        process.setProcess(PATH_PREFIX.resolve("cfpatterns/" + name + FileTypes.BPEL));
        process.setWsdls(new ArrayList<>(Arrays.asList(testInterface, partnerInterface)));
        process.setTestCases(Arrays.asList(testCases));
        process.setGroup(Groups.CFPATTERNS);
        return process;
    }

    private static BPELProcess buildProcess(final String name, BPELTestCase... testCases) {
        BPELProcess process = new BPELProcess();
        process.setProcess(PATH_PREFIX.resolve(name + FileTypes.BPEL));
        process.setWsdls(new ArrayList<>(Collections.singletonList(testInterface)));
        process.setTestCases(Arrays.asList(testCases));
        return process;
    }

    private static BPELProcess buildBasicProcessWithXsd(final String name, BPELTestCase... testCases) {
        BPELProcess process = new BPELProcess();
        process.setProcess(PATH_PREFIX.resolve(name + FileTypes.BPEL));
        process.setWsdls(new ArrayList<>(Collections.singletonList(testInterface)));
        process.setTestCases(Arrays.asList(testCases));
        process.setAdditionalFiles(new ArrayList<>(Collections.singletonList(PATH_PREFIX.resolve("basic/months.xsd"))));
        return process;

    }

    private static BPELProcess buildProcessWithPartner(final String name, BPELTestCase... testCases) {
        BPELProcess process = new BPELProcess();
        process.setProcess(PATH_PREFIX.resolve(name + FileTypes.BPEL));
        process.setWsdls(new ArrayList<>(Arrays.asList(testInterface, partnerInterface)));
        process.setTestCases(Arrays.asList(testCases));
        return process;
    }

    private static BPELProcess buildBasicProcessWithXslt(final String name, BPELTestCase... testCases) {
        BPELProcess process = new BPELProcess();
        process.setProcess(PATH_PREFIX.resolve(name + FileTypes.BPEL));
        process.setWsdls(new ArrayList<>(Arrays.asList(testInterface, partnerInterface)));
        process.setTestCases(Arrays.asList(testCases));
        process.setAdditionalFiles(new ArrayList<>(Arrays.asList(PATH_PREFIX.resolve("basic/echo.xslt"), PATH_PREFIX.resolve("basic/notCompileable.xslt"))));

        return process;
    }

    public static BPELProcess buildStructuredActivityProcess(final String name, BPELTestCase... testCases) {
        BPELProcess bpelProcess = buildProcess("structured/" + name, testCases);
        bpelProcess.setGroup(Groups.STRUCTURED);
        return bpelProcess;
    }

    public static BPELProcess buildScopeProcess(final String name, BPELTestCase... testCases) {
        BPELProcess bpelProcess = buildProcess("scopes/" + name, testCases);
        bpelProcess.setGroup(Groups.SCOPES);
        return bpelProcess;
    }

    public static BPELProcess buildBasicActivityProcess(final String name, BPELTestCase... testCases) {
        BPELProcess bpelProcess = buildProcess("basic/" + name, testCases);
        bpelProcess.setGroup(Groups.BASIC);
        return bpelProcess;
    }

    public static BPELProcess buildStructuredActivityProcess(String name, String description, BPELTestCase... testCases) {
        BPELProcess process = buildStructuredActivityProcess(name, testCases);
        process.setDescription(description);
        return process;
    }

    public static BPELProcess buildScopeProcess(String name, String description, BPELTestCase... testCases) {
        BPELProcess process = buildScopeProcess(name, testCases);
        process.setDescription(description);
        return process;
    }

    public static BPELProcess buildBasicActivityProcess(String name, String description, BPELTestCase... testCases) {
        BPELProcess process = buildBasicActivityProcess(name, testCases);
        process.setDescription(description);
        return process;
    }

    public static BPELProcess buildBasicProcessWithXsd(String name, String description, BPELTestCase... testCases) {
        BPELProcess process = buildBasicProcessWithXsd(name, testCases);
        process.setGroup(Groups.BASIC);
        process.setDescription(description);
        return process;
    }

    public static BPELProcess buildBasicProcessWithPartner(String name, String description, BPELTestCase... testCases) {
        BPELProcess process = buildProcessWithPartner(name, testCases);
        process.setDescription(description);
        process.setGroup(Groups.BASIC);
        return process;
    }

    public static BPELProcess buildScopeProcessWithPartner(String name, String description, BPELTestCase... testCases) {
        BPELProcess process = buildProcessWithPartner(name, testCases);
        process.setDescription(description);
        process.setGroup(Groups.SCOPES);
        return process;
    }

    public static BPELProcess buildErrorProcessWithPartner(String name, String description, BPELTestCase... testCases) {
        BPELProcess process = buildProcessWithPartner(name, testCases);
        process.setDescription(description);
        process.setGroup(Groups.ERROR);
        return process;
    }

    public static BPELProcess buildStructuredProcessWithPartner(String name, String description, BPELTestCase... testCases) {
        BPELProcess process = buildProcessWithPartner(name, testCases);
        process.setDescription(description);
        process.setGroup(Groups.STRUCTURED);
        return process;
    }

    public static BPELProcess buildBasicProcessWithXslt(String name, String description, BPELTestCase... testCases) {
        BPELProcess process = buildBasicProcessWithXslt(name, testCases);
        process.setDescription(description);
        process.setGroup(Groups.BASIC);
        return process;
    }

    public static final Path PATH_PREFIX = Paths.get("src/main/tests/files/bpel");
    public static final Path testInterface = PATH_PREFIX.resolve("TestInterface.wsdl");
    public static final Path partnerInterface = PATH_PREFIX.resolve("TestPartner.wsdl");
    public static final int UNDECLARED_FAULT = -5;
    public static final int DECLARED_FAULT = -6;
}
