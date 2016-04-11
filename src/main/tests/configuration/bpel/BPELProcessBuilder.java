package configuration.bpel;

import betsy.bpel.model.BPELProcess;
import betsy.bpel.model.BPELTestCase;
import betsy.common.model.Group;
import betsy.common.model.ProcessLanguage;
import betsy.common.util.FileTypes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class BPELProcessBuilder {

    public static Group BASIC = new Group("basic", ProcessLanguage.BPEL, "Basic activities are the basic building blocks of a BPEL process.");
    public static Group STRUCTURED = new Group("structured", ProcessLanguage.BPEL, "Structured activities compose basic activities into a control-flow graph.");
    public static Group SCOPES = new Group("scopes", ProcessLanguage.BPEL, "Scopes provide the execution context of their enclosed activities.");
    public static Group CFPATTERNS = new Group("cfpatterns", ProcessLanguage.BPEL, "The original 20 Workflow Control-Flow patterns from van der Aalst et al.");

    public static Group ERROR = new Group("error", ProcessLanguage.BPEL, "The robustness or fault tolerant tests.");

    public static Group SA = new Group("sa", ProcessLanguage.BPEL, "The 94 static analysis rules of BPEL.");

    public static BPELProcess buildPatternProcess(final String name, BPELTestCase... testCases) {
        BPELProcess process = new BPELProcess();
        process.setProcess(PATH_PREFIX.resolve("cfpatterns/" + name + FileTypes.BPEL));
        process.setWsdls(new ArrayList<>(Collections.singletonList(testInterface)));
        process.setTestCases(Arrays.asList(testCases));
        process.setGroup(CFPATTERNS);
        return process;
    }

    public static BPELProcess buildPatternProcessWithPartner(final String name, BPELTestCase... testCases) {
        BPELProcess process = new BPELProcess();
        process.setProcess(PATH_PREFIX.resolve("cfpatterns/" + name + FileTypes.BPEL));
        process.setWsdls(new ArrayList<>(Arrays.asList(testInterface, partnerInterface)));
        process.setTestCases(Arrays.asList(testCases));
        process.setGroup(CFPATTERNS);
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
        bpelProcess.setGroup(STRUCTURED);
        return bpelProcess;
    }

    public static BPELProcess buildScopeProcess(final String name, BPELTestCase... testCases) {
        BPELProcess bpelProcess = buildProcess("scopes/" + name, testCases);
        bpelProcess.setGroup(SCOPES);
        return bpelProcess;
    }

    public static BPELProcess buildBasicActivityProcess(final String name, BPELTestCase... testCases) {
        BPELProcess bpelProcess = buildProcess("basic/" + name, testCases);
        bpelProcess.setGroup(BASIC);
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
        process.setGroup(BASIC);
        process.setDescription(description);
        return process;
    }

    public static BPELProcess buildBasicProcessWithPartner(String name, String description, BPELTestCase... testCases) {
        BPELProcess process = buildProcessWithPartner(name, testCases);
        process.setDescription(description);
        process.setGroup(BASIC);
        return process;
    }

    public static BPELProcess buildScopeProcessWithPartner(String name, String description, BPELTestCase... testCases) {
        BPELProcess process = buildProcessWithPartner(name, testCases);
        process.setDescription(description);
        process.setGroup(SCOPES);
        return process;
    }

    public static BPELProcess buildErrorProcessWithPartner(String name, String description, BPELTestCase... testCases) {
        BPELProcess process = buildProcessWithPartner(name, testCases);
        process.setDescription(description);
        process.setGroup(ERROR);
        return process;
    }

    public static BPELProcess buildStructuredProcessWithPartner(String name, String description, BPELTestCase... testCases) {
        BPELProcess process = buildProcessWithPartner(name, testCases);
        process.setDescription(description);
        process.setGroup(STRUCTURED);
        return process;
    }

    public static BPELProcess buildBasicProcessWithXslt(String name, String description, BPELTestCase... testCases) {
        BPELProcess process = buildBasicProcessWithXslt(name, testCases);
        process.setDescription(description);
        process.setGroup(BASIC);
        return process;
    }

    public static final Path PATH_PREFIX = Paths.get("src/main/tests/files/bpel");
    public static final Path testInterface = PATH_PREFIX.resolve("TestInterface.wsdl");
    public static final Path partnerInterface = PATH_PREFIX.resolve("TestPartner.wsdl");
    public static final int UNDECLARED_FAULT = -5;
    public static final int DECLARED_FAULT = -6;
}
