package configuration.bpel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StaticAnalysisProcessesTest {

    @Test
    public void testConvertIntegerToSARuleNumber() throws Exception {
        assertEquals("SA00042", StaticAnalysisProcesses.convertIntegerToSARuleNumber(42));
    }
}