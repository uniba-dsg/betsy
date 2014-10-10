package betsy.bpmn;

import org.junit.Test;

public class ProcessValidatorTest {

    @Test
    public void testValidityOfAllProcesses() {
        ProcessValidator validator = new ProcessValidator();
        validator.validate();
    }
}
