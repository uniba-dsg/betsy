package betsy;

import betsy.bpel.Validator;
import configuration.bpel.ProcessRepository;
import org.junit.Test;

public class ValidatorTest {
    @Test
    public void testValidityOfAllProcesses() {
        Validator validator = new Validator();
        validator.setProcesses(new ProcessRepository().getByName("ALL"));
        validator.validate();
    }

}
