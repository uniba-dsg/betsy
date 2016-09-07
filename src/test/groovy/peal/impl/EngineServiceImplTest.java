package peal.impl;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import peal.identifier.EngineId;

import static org.junit.Assert.*;

public class EngineServiceImplTest {

    @Test
    public void getSupportedEngines() throws Exception {
        Set<EngineId> supportedEngines = new EngineServiceImpl().getSupportedEngines();
        assertEquals(38, supportedEngines.size());
    }

}
