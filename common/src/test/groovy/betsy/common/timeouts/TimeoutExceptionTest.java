package betsy.common.timeouts;

import betsy.common.timeouts.timeout.Timeout;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class TimeoutExceptionTest {

    @Test
    public void testConstructorTimeout() throws Exception {
        Timeout timeout = new Timeout("ode", "sequence", 20_000, 500);
        TimeoutException timeoutException = new TimeoutException(timeout);
        assertEquals("The timeouts should be equal.", timeout, timeoutException.getTimeout());
    }
}
