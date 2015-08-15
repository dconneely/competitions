package com.davidconneely.bluemixreg2015;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class Question2Test {
    @Test
    public void testMakeChange1() throws IOException {
        final int[] validCoins = { 1,2,4,8,16,32 };
        final StringWriter out = new StringWriter();
        Question2.processChange(validCoins, 63, out);
        assertEquals("32x1,16x1,8x1,4x1,2x1,1x1", out.toString());
    }

    @Test
    public void testMakeChange2() throws IOException {
        final int[] validCoins = { 50,20,2 };
        final StringWriter out = new StringWriter();
        Question2.processChange(validCoins, 60, out);
        assertEquals("20x3", out.toString());
    }

    @Test
    public void testMakeChange3() throws IOException {
        final int[] validCoins = { 10,5,2 };
        final StringWriter out = new StringWriter();
        Question2.processChange(validCoins, 6, out);
        assertEquals("2x3", out.toString());
    }

    @Test
    public void testMakeChange4() throws IOException {
        final int[] validCoins = { 10,5 };
        final StringWriter out = new StringWriter();
        Question2.processChange(validCoins, 6, out);
        assertEquals("10x1", out.toString());
    }

}
