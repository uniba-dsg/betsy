package peal.impl;

import java.util.Set;

import org.junit.Test;
import peal.identifier.EngineId;
import peal.impl.engine.EngineServiceImpl;

import static org.junit.Assert.assertEquals;

public class EngineServiceImplTest {

    @Test
    public void getSupportedEngines() throws Exception {
        Set<EngineId> supportedEngines = new EngineServiceImpl().getSupportedEngines();
        assertEquals(38, supportedEngines.size());
    }

}
