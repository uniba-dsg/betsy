package betsy

import betsy.executables.Validator
import configuration.processes.Processes
import org.junit.Test

class ValidatorTest {

    @Test
    public void testValidityOfAllProcesses() {
        new Validator(processes: new Processes().ALL).validate()
    }

}
