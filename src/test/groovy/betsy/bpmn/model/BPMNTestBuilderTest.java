package betsy.bpmn.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class BPMNTestBuilderTest {

    @Test
    public void testGetAssertionString() throws Exception {
        assertEquals("{}", BPMNTestBuilder.getAssertionString(Collections.emptyList()));
        assertEquals("{\"A\"}", BPMNTestBuilder.getAssertionString(Arrays.asList("A")));
        assertEquals("{\"A\",\"B\",\"C\"}", BPMNTestBuilder.getAssertionString(Arrays.asList("A", "B", "C")));
    }

}