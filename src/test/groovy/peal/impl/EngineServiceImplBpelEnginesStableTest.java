package peal.impl;

import java.util.stream.Collectors;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import peal.ProcessLanguage;
import peal.identifier.EngineId;
import peal.impl.engine.EngineServiceImpl;

@RunWith(Parameterized.class)
public class EngineServiceImplBpelEnginesStableTest extends AbstractEngineServiceImplBpelEnginesTest {

    public EngineServiceImplBpelEnginesStableTest(EngineId engineId) {
        super(engineId);
    }

    @Parameterized.Parameters(name = "{index} {0}")
    public static Iterable<Object[]> data() {
        return new EngineServiceImpl().getSupportedEngines().stream()
                .filter(p -> new EngineServiceImpl().getSupportedLanguage(p).equals(ProcessLanguage.BPEL))
                .filter(p -> !(p.toString().startsWith("orchestra") || p.toString().startsWith("petalsesb")))
                .map(p -> new Object[] {p})
                .collect(Collectors.toList());
    }
}
