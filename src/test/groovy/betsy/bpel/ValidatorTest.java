package betsy.bpel;

import betsy.bpel.validation.Validator;
import configuration.bpel.BPELProcessRepository;
import org.junit.Test;

public class ValidatorTest {
    @Test
    public void testValidityOfAllProcesses() {
        new Validator(BPELProcessRepository.INSTANCE.getByName("ALL")).validate();
    }

}
