package configuration.bpel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import betsy.bpel.model.BPELTestCase;
import betsy.common.model.feature.Construct;
import betsy.common.model.feature.Feature;
import betsy.common.model.input.EngineIndependentProcess;
import betsy.common.model.input.TestPartner;
import betsy.common.util.FileTypes;

public class BPELProcessBuilder {

    public static final TestPartner ERROR_TEST_PARTNER = createErrorTestPartner("http://localhost:2000/bpel-testpartner");

    private static TestPartner createErrorTestPartner(String url) {
        List<TestPartner.OperationInputActionOutput> tcpActions = new ArrayList<>();
        tcpActions.add(new TestPartner.OperationInputActionOutput(
                        "startProcessSync",
                        new TestPartner.IntegerInput(50_001),
                        new TestPartner.TimeoutInsteadOfOutput()));

        List<TestPartner.OperationInputActionOutput> httpActions = IntStream.of(
                100, 101,
                201, 202, 203, 204, 205, 206,
                300, 301, 302, 303, 304, 305, 306, 307,
                400, 401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417,
                500, 501, 502, 503, 504, 505).mapToObj(i -> new TestPartner.OperationInputActionOutput(
                        "startProcessSync",
                        new TestPartner.IntegerInput(i + 22_000),
                        new TestPartner.IntegerOutputWithStatusCode(0, i)
                )
        ).collect(Collectors.toList());

        List<TestPartner.OperationInputActionOutput> soapActions = IntStream.range(60_001, 60_027).mapToObj(i -> {
            String rawOutput = "";
            try {
                Path folder = Paths.get("src/tests/files/bpel/errrorsbase/soap");
                Optional<Path> foundXmlFile = Files.walk(folder).filter(f -> f.getFileName().toString().startsWith(String.valueOf(i))).findFirst();
                if(foundXmlFile.isPresent()) {
                    rawOutput = String.join("\n", Files.readAllLines(foundXmlFile.get()));
                }
            } catch (IOException e) {
            }

            return new TestPartner.OperationInputActionOutput(
                    "startProcessSync",
                    new TestPartner.IntegerInput(i),
                    new TestPartner.RawOutput(rawOutput));
        }).collect(Collectors.toList());

        List<TestPartner.OperationInputActionOutput> appActions = IntStream.range(40_001, 40_027).mapToObj(i -> {
            String rawOutput = "";
            try {
                Path folder = Paths.get("src/tests/files/bpel/errrorsbase/app");
                Optional<Path> foundXmlFile = Files.walk(folder).filter(f -> f.getFileName().toString().startsWith(String.valueOf(i))).findFirst();
                if(foundXmlFile.isPresent()) {
                    rawOutput = String.join("\n", Files.readAllLines(foundXmlFile.get()));
                }
            } catch (IOException e) {
            }

            return new TestPartner.OperationInputActionOutput(
                    "startProcessSync",
                    new TestPartner.IntegerInput(i),
                    new TestPartner.RawOutput(rawOutput));
        }).collect(Collectors.toList());

        List<TestPartner.OperationInputActionOutput> actions = new ArrayList<>();
        actions.addAll(httpActions);
        actions.addAll(soapActions);
        actions.addAll(appActions);
        actions.add(new TestPartner.OperationInputActionOutput("startProcessSync", new TestPartner.AnyInput(), new TestPartner.EchoInputAsOutput()));

        return new TestPartner(
                Paths.get("TestPartner.wsdl"),
                url,
                actions.toArray(new TestPartner.OperationInputActionOutput[] {})
        );
    }

    public static EngineIndependentProcess buildPatternProcess(final String name, Feature feature, BPELTestCase... testCases) {
        return new EngineIndependentProcess(PATH_PREFIX.resolve("cfpatterns/" + name + FileTypes.BPEL), "", Arrays.asList(testCases), feature, Collections.singletonList(testInterface), Collections.emptyList());
    }

    public static EngineIndependentProcess buildPatternProcessWithPartner(final String name, Feature feature, BPELTestCase... testCases) {
        return new EngineIndependentProcess(PATH_PREFIX.resolve("cfpatterns/" + name + FileTypes.BPEL), "", Arrays.asList(testCases), feature, Arrays.asList(testInterface, partnerInterface), Collections.singletonList(TestPartner.REGULAR_TEST_PARTNER));
    }

    public static EngineIndependentProcess buildStructuredActivityProcess(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new EngineIndependentProcess(PATH_PREFIX.resolve("structured/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Collections.singletonList(testInterface), Collections.emptyList());
    }

    public static EngineIndependentProcess buildScopeProcess(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new EngineIndependentProcess(PATH_PREFIX.resolve("scopes/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Collections.singletonList(testInterface), Collections.emptyList());
    }

    public static EngineIndependentProcess buildScopeProcessWithPartner(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new EngineIndependentProcess(PATH_PREFIX.resolve("scopes/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Arrays.asList(testInterface, partnerInterface), Collections.singletonList(TestPartner.REGULAR_TEST_PARTNER));
    }

    public static EngineIndependentProcess buildBasicActivityProcess(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new EngineIndependentProcess(PATH_PREFIX.resolve("basic/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Collections.singletonList(testInterface), Collections.emptyList());
    }

    public static EngineIndependentProcess buildBasicProcessWithXsd(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new EngineIndependentProcess(PATH_PREFIX.resolve("basic/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Arrays.asList(testInterface, PATH_PREFIX.resolve("basic/months.xsd")), Collections.emptyList());
    }

    public static EngineIndependentProcess buildBasicProcessWithPartner(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new EngineIndependentProcess(PATH_PREFIX.resolve("basic/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Arrays.asList(testInterface, partnerInterface), Collections.singletonList(TestPartner.REGULAR_TEST_PARTNER));
    }

    public static EngineIndependentProcess buildBasicProcessWithRegularAndDummyPartner(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new EngineIndependentProcess(PATH_PREFIX.resolve("basic/" + name + FileTypes.BPEL),
                description,
                Arrays.asList(testCases),
                feature,
                Arrays.asList(testInterface, partnerInterface),
                Arrays.asList(TestPartner.REGULAR_TEST_PARTNER, TestPartner.DUMMY_TEST_PARTNER));
    }

    public static EngineIndependentProcess buildErrorProcessWithPartner(String constructName, String name, String description, BPELTestCase... testCases) {
        return new EngineIndependentProcess(PATH_PREFIX.resolve(name + FileTypes.BPEL),
                description,
                Arrays.asList(testCases),
                new Feature(new Construct(Groups.ERROR, constructName), name),
                Arrays.asList(testInterface, partnerInterface), Arrays.asList(ERROR_TEST_PARTNER));
    }

    public static EngineIndependentProcess buildStructuredProcessWithPartner(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new EngineIndependentProcess(PATH_PREFIX.resolve("structured/" + name + FileTypes.BPEL),
                description,
                Arrays.asList(testCases),
                feature,
                Arrays.asList(testInterface, partnerInterface), Collections.singletonList(TestPartner.REGULAR_TEST_PARTNER));
    }

    public static EngineIndependentProcess buildBasicProcessWithXslt(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new EngineIndependentProcess(PATH_PREFIX.resolve("basic/" + name + FileTypes.BPEL),
                description,
                Arrays.asList(testCases),
                feature,
                Arrays.asList(testInterface, PATH_PREFIX.resolve("basic/echo.xslt"), PATH_PREFIX.resolve("basic/notCompileable.xslt")), Collections.emptyList());
    }

    public static final Path PATH_PREFIX = Paths.get("src/main/tests/files/bpel");
    public static final Path testInterface = PATH_PREFIX.resolve("TestInterface.wsdl");
    public static final Path partnerInterface = PATH_PREFIX.resolve("TestPartner.wsdl");
    public static final int UNDECLARED_FAULT = -5;
    public static final int DECLARED_FAULT = -6;
}
