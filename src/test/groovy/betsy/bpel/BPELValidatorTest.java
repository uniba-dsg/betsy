package betsy.bpel;

import betsy.bpel.validation.BPELValidator;
import configuration.bpel.BPELProcessRepository;
import org.junit.Test;

public class BPELValidatorTest {
    @Test
    public void testValidityOfAllProcesses() {
        new BPELValidator(BPELProcessRepository.INSTANCE.getByName("ALL")).validate();
    }

}
