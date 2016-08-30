package betsy.common.timeouts;

import java.util.NoSuchElementException;

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

    @Test(expected = NoSuchElementException.class)
    public void testConstructorMessage() throws Exception {
        String message = "This is a message.";
        TimeoutException timeoutException = new TimeoutException(message);
        assertEquals("The messages should be equal.", message, timeoutException.getMessage());
        timeoutException.getTimeout();
    }

    @Test
    public void testConstructorMessageTimeout() throws Exception {
        String message = "This is a message.";
        Timeout timeout = new Timeout("ode", "sequence", 20_000, 500);
        TimeoutException timeoutException = new TimeoutException(message, timeout);
        assertEquals("The messages should be equal.",  message, timeoutException.getMessage());
        assertEquals("The timeouts should be equal.", timeout, timeoutException.getTimeout());
    }

    @Test(expected = NoSuchElementException.class)
    public void testConstructorMessageThrowable() throws Exception {
        String message = "This is a message.";
        Throwable throwable = new Throwable();
        TimeoutException timeoutException = new TimeoutException(message, throwable);
        assertEquals("The messages should be equal.", message, timeoutException.getMessage());
        timeoutException.getTimeout();
    }

    @Test
    public void testConstructorMessageThrowableTimeout() throws Exception {
        String message = "This is a message.";
        Throwable throwable = new Throwable();
        Timeout timeout = new Timeout("ode", "sequence", 20_000, 500);
        TimeoutException timeoutException = new TimeoutException(message, throwable, timeout);
        assertEquals("The messages should be equal.",  message, timeoutException.getMessage());
        assertEquals("The timeouts should be equal.", timeout, timeoutException.getTimeout());
    }

    @Test(expected = NoSuchElementException.class)
    public void testConstructorThrowable() throws Exception {
        Throwable throwable = new Throwable();
        new TimeoutException(throwable).getTimeout();
    }

    @Test
    public void testConstructorThrowableTimeout() throws Exception {
        Throwable throwable = new Throwable();
        Timeout timeout = new Timeout("ode", "sequence", 20_000, 500);
        TimeoutException timeoutException = new TimeoutException(throwable, timeout);
        assertEquals("The timeouts should be equal.", timeout, timeoutException.getTimeout());
    }

    @Test
    public void testGetTimeout() throws Exception {
        Timeout timeout = new Timeout("ode", "sequence", 20_000, 500);
        TimeoutException timeoutException = new TimeoutException(timeout);
        assertEquals("The timeouts should be equal.", timeout, timeoutException.getTimeout());
    }
}
