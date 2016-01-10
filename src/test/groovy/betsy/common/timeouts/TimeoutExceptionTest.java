package betsy.common.timeouts;

import org.junit.Test;
import betsy.common.timeouts.timeout.Timeout;

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
        assertEquals(timeout, timeoutException.getTimeout().get());
    }

    @Test
    public void testConstructorTimeoutNull() throws Exception {
        Timeout timeout = null;
        TimeoutException timeoutException = new TimeoutException(timeout);
        assertEquals(false, timeoutException.getTimeout().isPresent());
    }

    @Test
    public void testConstructorMessage() throws Exception {
        String message = "This is a message.";
        TimeoutException timeoutException = new TimeoutException(message);
        assertEquals(message, timeoutException.getMessage());
        assertEquals(false, timeoutException.getTimeout().isPresent());
    }

    @Test
    public void testConstructorMessageTimeout() throws Exception {
        String message = "This is a message.";
        Timeout timeout = new Timeout("ode", "sequence", 20_000, 500);
        TimeoutException timeoutException = new TimeoutException(message, timeout);
        assertEquals(message, timeoutException.getMessage());
        assertEquals(timeout, timeoutException.getTimeout().get());
    }

    @Test
    public void testConstructorMessageTimeoutNull() throws Exception {
        String message = "This is a message.";
        Timeout timeout = null;
        TimeoutException timeoutException = new TimeoutException(message, timeout);
        assertEquals(message, timeoutException.getMessage());
        assertEquals(false, timeoutException.getTimeout().isPresent());
    }

    @Test
    public void testConstructorMessageThrowable() throws Exception {
        String message = "This is a message.";
        Throwable throwable = new Throwable();
        TimeoutException timeoutException = new TimeoutException(message, throwable);
        assertEquals(message, timeoutException.getMessage());
        assertEquals(false, timeoutException.getTimeout().isPresent());
    }

    @Test
    public void testConstructorMessageThrowableTimeout() throws Exception {
        String message = "This is a message.";
        Throwable throwable = new Throwable();
        Timeout timeout = new Timeout("ode", "sequence", 20_000, 500);
        TimeoutException timeoutException = new TimeoutException(message, throwable, timeout);
        assertEquals(message, timeoutException.getMessage());
        assertEquals(timeout, timeoutException.getTimeout().get());
    }

    @Test
    public void testConstructorMessageThrowableTimeoutNull() throws Exception {
        String message = "This is a message.";
        Throwable throwable = new Throwable();
        Timeout timeout = null;
        TimeoutException timeoutException = new TimeoutException(message, throwable, timeout);
        assertEquals(message, timeoutException.getMessage());
        assertEquals(false, timeoutException.getTimeout().isPresent());
    }

    @Test
    public void testConstructorThrowable() throws Exception {
        Throwable throwable = new Throwable();
        TimeoutException timeoutException = new TimeoutException(throwable);
        assertEquals(false, timeoutException.getTimeout().isPresent());
    }

    @Test
    public void testConstructorThrowableTimeout() throws Exception {
        Throwable throwable = new Throwable();
        Timeout timeout = new Timeout("ode", "sequence", 20_000, 500);
        TimeoutException timeoutException = new TimeoutException(throwable, timeout);
        assertEquals(timeout, timeoutException.getTimeout().get());
    }

    @Test
    public void testConstructorThrowableTimeoutNull() throws Exception {
        Throwable throwable = new Throwable();
        Timeout timeout = null;
        TimeoutException timeoutException = new TimeoutException(throwable, timeout);
        assertEquals(false, timeoutException.getTimeout().isPresent());
    }

    @Test
    public void testGetTimeout() throws Exception {
        Timeout timeout = new Timeout("ode", "sequence", 20_000, 500);
        TimeoutException timeoutException = new TimeoutException(timeout);
        assertEquals(timeout, timeoutException.getTimeout().get());
    }
}