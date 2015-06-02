package betsy.bpmn;

import betsy.bpmn.validation.BPMNValidator;
import org.junit.Test;

public class BPMNValidatorTest {

    @Test
    public void testValidityOfAllProcesses() {
        new BPMNValidator().validate();
    }
}
