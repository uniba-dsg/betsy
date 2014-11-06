package betsy.bpmn;

import betsy.bpmn.validation.ProcessValidator;
import org.junit.Test;

public class ProcessValidatorTest {

    @Test
    public void testValidityOfAllProcesses() {
        new ProcessValidator().validate();
    }
}
