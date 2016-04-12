package configuration.bpel;

import betsy.bpel.model.BPELProcess;
import betsy.bpel.model.BPELTestCase;
import betsy.common.model.EngineIndependentProcess;
import betsy.common.model.feature.Construct;
import betsy.common.model.feature.Feature;
import betsy.common.util.FileTypes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class BPELProcessBuilder {

    public static EngineIndependentProcess buildPatternProcess(final String name, Feature feature, BPELTestCase... testCases) {
        return new EngineIndependentProcess(PATH_PREFIX.resolve("cfpatterns/" + name + FileTypes.BPEL), "", Arrays.asList(testCases), feature, Collections.singletonList(testInterface));
    }

    public static EngineIndependentProcess buildPatternProcessWithPartner(final String name, Feature feature, BPELTestCase... testCases) {
        return new EngineIndependentProcess(PATH_PREFIX.resolve("cfpatterns/" + name + FileTypes.BPEL), "", Arrays.asList(testCases), feature, Arrays.asList(testInterface, partnerInterface));
    }

    public static EngineIndependentProcess buildStructuredActivityProcess(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new EngineIndependentProcess(PATH_PREFIX.resolve("structured/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Collections.singletonList(testInterface));
    }

    public static EngineIndependentProcess buildScopeProcess(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new EngineIndependentProcess(PATH_PREFIX.resolve("scopes/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Collections.singletonList(testInterface));
    }

    public static EngineIndependentProcess buildScopeProcessWithPartner(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new EngineIndependentProcess(PATH_PREFIX.resolve("scopes/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Arrays.asList(testInterface, partnerInterface));
    }

    public static EngineIndependentProcess buildBasicActivityProcess(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new EngineIndependentProcess(PATH_PREFIX.resolve("basic/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Collections.singletonList(testInterface));
    }

    public static EngineIndependentProcess buildBasicProcessWithXsd(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new EngineIndependentProcess(PATH_PREFIX.resolve("basic/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Arrays.asList(testInterface, PATH_PREFIX.resolve("basic/months.xsd")));
    }

    public static EngineIndependentProcess buildBasicProcessWithPartner(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new EngineIndependentProcess(PATH_PREFIX.resolve("basic/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Arrays.asList(testInterface, partnerInterface));
    }

    public static EngineIndependentProcess buildErrorProcessWithPartner(String constructName, String name, String description, BPELTestCase... testCases) {
        return new EngineIndependentProcess(PATH_PREFIX.resolve(name + FileTypes.BPEL),
                description,
                Arrays.asList(testCases),
                new Feature(new Construct(Groups.ERROR, constructName), name),
                Arrays.asList(testInterface, partnerInterface));
    }

    public static EngineIndependentProcess buildStructuredProcessWithPartner(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new EngineIndependentProcess(PATH_PREFIX.resolve("structured/" + name + FileTypes.BPEL),
                description,
                Arrays.asList(testCases),
                feature,
                Arrays.asList(testInterface, partnerInterface));
    }

    public static EngineIndependentProcess buildBasicProcessWithXslt(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new EngineIndependentProcess(PATH_PREFIX.resolve(name + FileTypes.BPEL),
                description,
                Arrays.asList(testCases),
                feature,
                Arrays.asList(testInterface, PATH_PREFIX.resolve("basic/echo.xslt"), PATH_PREFIX.resolve("basic/notCompileable.xslt")));
    }

    public static final Path PATH_PREFIX = Paths.get("src/main/tests/files/bpel");
    public static final Path testInterface = PATH_PREFIX.resolve("TestInterface.wsdl");
    public static final Path partnerInterface = PATH_PREFIX.resolve("TestPartner.wsdl");
    public static final int UNDECLARED_FAULT = -5;
    public static final int DECLARED_FAULT = -6;
}
