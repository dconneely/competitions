package com.davidconneely.bluemixreg2015;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class Question3Test {
    @Test
    public void testParseEqn() {
        Question3.Eqn eqn1 = Question3.parseEqn("4x+3y=24");
        assertEquals(new Question3.Eqn(4, 3, 24), eqn1);
        Question3.Eqn eqn2 = Question3.parseEqn("5 x + y =19");
        assertEquals(new Question3.Eqn(5, 1, 19), eqn2);
        Question3.Eqn eqn3 = Question3.parseEqn("  2x+y=3");
        assertEquals(new Question3.Eqn(2, 1, 3), eqn3);
        Question3.Eqn eqn4 = Question3.parseEqn("3x-y=2  ");
        assertEquals(new Question3.Eqn(3, -1, 2), eqn4);
        Question3.Eqn eqn5 = Question3.parseEqn("\tx -y = -1000  ");
        assertEquals(new Question3.Eqn(1, -1, -1000), eqn5);
    }

    @Test
    public void testNotParseEqn() {
        Question3.Eqn eqn1 = Question3.parseEqn("");
        assertNull(eqn1);
        Question3.Eqn eqn2 = Question3.parseEqn("-5x +y =19");
        assertNull(eqn2);
        Question3.Eqn eqn3 = Question3.parseEqn("500x + 3y =6");
        assertNull(eqn3);
        Question3.Eqn eqn4 = Question3.parseEqn("4x = 3y -1");
        assertNull(eqn4);
        Question3.Eqn eqn5 = Question3.parseEqn("\t   \r\n4x + 2y = -9999");
        assertNull(eqn5);
    }

    @Test
    public void testSolveEqnsInt() throws IOException {
        Question3.Eqn eqn1, eqn2;
        StringWriter writer;

        eqn1 = new Question3.Eqn(4, 3, 24);
        eqn2 = new Question3.Eqn(5, 1, 19);
        writer = new StringWriter();
        Question3.solveEqns(eqn1, eqn2, writer);
        assertEquals("x=3 y=4", writer.toString());

        eqn1 = new Question3.Eqn(2, 1, 3);
        eqn2 = new Question3.Eqn(3, -1, 2);
        writer = new StringWriter();
        Question3.solveEqns(eqn1, eqn2, writer);
        assertEquals("x=1 y=1", writer.toString());

        eqn1 = new Question3.Eqn(15, 13, 40);
        eqn2 = new Question3.Eqn(3, -1, -136);
        writer = new StringWriter();
        Question3.solveEqns(eqn1, eqn2, writer);
        assertEquals("x=-32 y=40", writer.toString());
    }

    /* We are told P and Q, the solutions for x and y, are integers so this test is not needed. */
    @Test
    public void testSolveEqnsNonInt() throws IOException {
        Question3.Eqn eqn1, eqn2;
        StringWriter writer;

        eqn1 = new Question3.Eqn(4, 5, 17);
        eqn2 = new Question3.Eqn(6, -10, -13);
        writer = new StringWriter();
        Question3.solveEqns(eqn1, eqn2, writer);
        assertEquals("x=1.5 y=2.2", writer.toString());

        eqn1 = new Question3.Eqn(8, 4, 2);
        eqn2 = new Question3.Eqn(-8, -8, 7);
        writer = new StringWriter();
        Question3.solveEqns(eqn1, eqn2, writer);
        assertEquals("x=1.375 y=-2.25", writer.toString());

    }
}
