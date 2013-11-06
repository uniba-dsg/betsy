package betsy

import betsy.executables.Validator
import configuration.ProcessRepository
import org.junit.Test

class ValidatorTest {

    @Test
    public void testValidityOfAllProcesses() {
        new Validator(processes: new ProcessRepository().getByName("ALL")).validate()
    }

}
