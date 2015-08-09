package com.davidconneely.bluemixreg2015;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertTrue;

public class Question1Test {
    @Test
    public void testProcess() throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader("3\n4\n5\n#"));
        StringWriter writer = new StringWriter();
        Question1.process(reader, "\n", writer);
        assertTrue("6\n24\n120".equals(writer.toString()));
    }

    @Test
    public void testFactorial() {
        assertTrue(6L == Question1.factorial(3));
        assertTrue(720L == Question1.factorial(6));
        assertTrue(362880L == Question1.factorial(9));
    }
}
