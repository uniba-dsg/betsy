package configuration.bpel;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import betsy.bpel.model.BPELTestCase;
import pebl.feature.FeatureSet;
import pebl.feature.Feature;
import pebl.test.Test;
import pebl.test.partner.InternalWSDLTestPartner;
import betsy.common.util.FileTypes;

public class BPELProcessBuilder {

    public static final Path PATH_PREFIX = Paths.get("src/main/tests/files/bpel");
    public static final Path testInterface = PATH_PREFIX.resolve("TestInterface.wsdl");
    public static final Path partnerInterface = PATH_PREFIX.resolve("TestPartner.wsdl");
    public static final int UNDECLARED_FAULT = -5;
    public static final int DECLARED_FAULT = -6;

    public static Test buildPatternProcess(final String name, Feature feature, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("cfpatterns/" + name + FileTypes.BPEL), "", Arrays.asList(testCases), feature, Collections.singletonList(testInterface), Collections.emptyList());
    }

    public static Test buildPatternProcessWithPartner(final String name, Feature feature, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("cfpatterns/" + name + FileTypes.BPEL), "", Arrays.asList(testCases), feature, Arrays.asList(testInterface, partnerInterface), Collections.singletonList(InternalWSDLTestPartner.REGULAR_TEST_PARTNER));
    }

    public static Test buildStructuredActivityProcess(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("structured/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Collections.singletonList(testInterface), Collections.emptyList());
    }

    public static Test buildScopeProcess(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("scopes/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Collections.singletonList(testInterface), Collections.emptyList());
    }

    public static Test buildScopeProcessWithPartner(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("scopes/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Arrays.asList(testInterface, partnerInterface), Collections.singletonList(InternalWSDLTestPartner.REGULAR_TEST_PARTNER));
    }

    public static Test buildBasicActivityProcess(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("basic/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Collections.singletonList(testInterface), Collections.emptyList());
    }

    public static Test buildBasicProcessWithXsd(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("basic/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Arrays.asList(testInterface, PATH_PREFIX.resolve("basic/months.xsd")), Collections.emptyList());
    }

    public static Test buildBasicProcessWithPartner(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("basic/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Arrays.asList(testInterface, partnerInterface), Collections.singletonList(InternalWSDLTestPartner.REGULAR_TEST_PARTNER));
    }

    public static Test buildBasicProcessWithRegularAndDummyPartner(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("basic/" + name + FileTypes.BPEL),
                description,
                Arrays.asList(testCases),
                feature,
                Arrays.asList(testInterface, partnerInterface),
                Arrays.asList(InternalWSDLTestPartner.REGULAR_TEST_PARTNER, InternalWSDLTestPartner.DUMMY_TEST_PARTNER));
    }

    public static Test buildErrorProcessWithPartner(String constructName, String name, String description, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("errorsbase").resolve(name + FileTypes.BPEL),
                description,
                Arrays.asList(testCases),
                new Feature(new FeatureSet(Groups.ERROR, constructName), name),
                Arrays.asList(testInterface, partnerInterface), Arrays.asList(ErrorProcesses.ERROR_TEST_PARTNER));
    }

    public static Test buildStructuredProcessWithPartner(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("structured/" + name + FileTypes.BPEL),
                description,
                Arrays.asList(testCases),
                feature,
                Arrays.asList(testInterface, partnerInterface), Collections.singletonList(InternalWSDLTestPartner.REGULAR_TEST_PARTNER));
    }

    public static Test buildBasicProcessWithXslt(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("basic/" + name + FileTypes.BPEL),
                description,
                Arrays.asList(testCases),
                feature,
                Arrays.asList(testInterface, PATH_PREFIX.resolve("basic/echo.xslt"), PATH_PREFIX.resolve("basic/notCompileable.xslt")), Collections.emptyList());
    }

}
