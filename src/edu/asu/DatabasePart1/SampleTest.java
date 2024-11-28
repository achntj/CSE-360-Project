package edu.asu.DatabasePart1;

import org.junit.Test;
import static org.junit.Assert.*;

public class SampleTest {

    @Test
    public void testAddition() {
        int result = 2 + 3;
        assertEquals("2 + 3 should equal 5", 5, result);
    }

    @Test
    public void testSubtraction() {
        int result = 5 - 3;
        assertEquals("5 - 3 should equal 2", 1, result);
    }
}
