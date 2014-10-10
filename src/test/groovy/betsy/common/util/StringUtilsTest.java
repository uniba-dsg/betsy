package betsy.common.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringUtilsTest {

    @Test
    public void testCapitalize() throws Exception {
        assertEquals("", StringUtils.capitalize(""));
        assertEquals("X", StringUtils.capitalize("x"));
        assertEquals("Xyz", StringUtils.capitalize("xyz"));
        assertEquals("Xyz", StringUtils.capitalize("Xyz"));
    }
}