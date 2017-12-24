package betsy.bpmn.model;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BPMNTestBuilderTest {

    @Test
    public void testGetAssertionString() throws Exception {
        assertEquals("{}", BPMNTestBuilder.getAssertionString(Collections.emptyList()));
        assertEquals("{\"A\"}", BPMNTestBuilder.getAssertionString(Arrays.asList("A")));
        assertEquals("{\"A\",\"B\",\"C\"}", BPMNTestBuilder.getAssertionString(Arrays.asList("A", "B", "C")));
    }

}
