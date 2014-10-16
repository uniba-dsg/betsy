package betsy.common.tasks;


import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertTrue;

public class URLTasksTests {

    @Test
    public void testGoogleURL() throws MalformedURLException {
        assertTrue(URLTasks.hasUrlSubstring(new URL("https://www.google.de"), "<title>Google</title>"));
    }

}