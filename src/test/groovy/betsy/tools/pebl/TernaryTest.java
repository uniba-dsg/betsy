package betsy.tools.pebl;

import org.junit.Test;

import static betsy.tools.pebl.Ternary.MINUS;
import static betsy.tools.pebl.Ternary.PLUS;
import static betsy.tools.pebl.Ternary.PLUS_MINUS;
import static org.junit.Assert.*;

public class TernaryTest {
    @Test
    public void aggregate() throws Exception {
        assertEquals(PLUS, PLUS.aggregate(PLUS));
        assertEquals(MINUS, MINUS.aggregate(MINUS));
        assertEquals(PLUS_MINUS, PLUS_MINUS.aggregate(PLUS_MINUS));
        assertEquals(PLUS_MINUS, PLUS.aggregate(PLUS_MINUS));
        assertEquals(PLUS_MINUS, MINUS.aggregate(PLUS_MINUS));
    }

    @Test
    public void max() throws Exception {
        assertEquals(PLUS, PLUS.max(PLUS));
        assertEquals(MINUS, MINUS.max(MINUS));
        assertEquals(PLUS_MINUS, PLUS_MINUS.max(PLUS_MINUS));
        assertEquals(PLUS, PLUS.max(PLUS_MINUS));
        assertEquals(PLUS_MINUS, MINUS.max(PLUS_MINUS));
    }

    @Test
    public void atMost() throws Exception {
        assertEquals(PLUS, PLUS.atMost(PLUS));
        assertEquals(MINUS, MINUS.atMost(MINUS));
        assertEquals(PLUS_MINUS, PLUS_MINUS.atMost(PLUS_MINUS));
        assertEquals(PLUS_MINUS, PLUS.atMost(PLUS_MINUS));
        assertEquals(MINUS, MINUS.atMost(PLUS_MINUS));
    }

}