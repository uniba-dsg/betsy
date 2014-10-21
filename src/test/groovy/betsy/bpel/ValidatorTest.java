package betsy.bpel;

import configuration.bpel.BPELProcessRepository;
import org.junit.Test;

public class ValidatorTest {
    @Test
    public void testValidityOfAllProcesses() {
        new Validator(new BPELProcessRepository().getByName("ALL")).validate();
    }

}
