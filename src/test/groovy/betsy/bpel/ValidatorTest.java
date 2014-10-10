package betsy.bpel;

import configuration.bpel.ProcessRepository;
import org.junit.Test;

public class ValidatorTest {
    @Test
    public void testValidityOfAllProcesses() {
        new Validator(new ProcessRepository().getByName("ALL")).validate();
    }

}
