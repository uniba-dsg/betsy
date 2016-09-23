package betsy.common.tasks;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class URLTasksTests {

    @Test
    public void testGoogleURL() throws MalformedURLException {
        assertTrue(URLTasks.hasUrlSubstring(new URL("https://www.google.de"), "<title>Google</title>"));
    }

}
