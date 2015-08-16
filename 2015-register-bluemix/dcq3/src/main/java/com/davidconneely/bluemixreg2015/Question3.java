package com.davidconneely.bluemixreg2015;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h1>Question 3: Simultaneous equations</h1>
 *
 * Given a pair of simultaneous equations, it is possible to deduce the values of the variables contained therein.
 * Imagine, for instance, we have the following pair of simultaneous equations:
 *
 * <ul>
 *     <li>4x+3y=24 (call this equation [1])</li>
 *     <li>5x+y=19 (call this equation [2])</li>
 * </ul>
 *
 * Using simple GCSE-level mathematics, we can calculate the value of x thus:
 *
 * <ul>
 *     <li>Multiply [2] by 3 throughout: 5x+y=19 becomes 15x+3y=57</li>
 *     <li>Subtract 15x from both sides: 15x+3y=57 becomes 3y=57-15x</li>
 *     <li>Subtract 4x from both sides of [1]: 4x+3y=24 becomes 3y=24-4x</li>
 *     <li>Because 3y=57-15x (from [2]) and 3y=24-4x (from [1]) we can say 57-15x = 24-4x</li>
 *     <li>Subtract 24 from both sides: 33-15x = -4x</li>
 *     <li>Now add 15x to both sides: 33=11x</li>
 *     <li>Finally, divide both sides by 11: 3=x (or x=3).</li>
 *     <li>Now we know that x=3, we can substitute its value into one of the equations. 4x+3y=24 [1] becomes 4x3+3y=24,
 *         or 12+3y=24</li>
 *     <li>Subtract 12 from both sides: 3y=12</li>
 *     <li>Divide both sides by 3: y=4</li>
 *     <li>We have therefore deduced that x=3 and y=4.</li>
 * </ul>
 *
 * Write a program that takes a series of pairs of simultaneous equations, and computes x and y in each case. Each
 * equation takes the form:
 *
 * <ul>
 *     <li>NxSMy=R</li>
 * </ul>
 *
 * ... where:
 *
 * <ul>
 *     <li>N and M are either blank or an integer between 1 and 100 inclusive</li>
 *     <li>S is a sign, either + or -</li>
 *     <li>R is an integer between -5000 and 5000 inclusive</li>
 * </ul>
 *
 * Each equation in a pair will appear on a separate line, and will always contain some multiple of x and y. Each pair
 * will be followed by a single # mark on a line by itself. The end of the input stream will be denoted by the '##' mark
 * on a line by itself. The elements of any equation will be separated by zero or more elements of whitespace (either
 * spaces or tab characters).
 *
 * Your program should write a solution for each pair of equations, each on a line by itself and each of the form:
 *
 * <ul>
 *     <li>x=P and y=Q</li>
 * </ul>
 *
 * ... where P and Q are integers between -40 and 40 inclusive. You may assume that every equation set is solvable, and
 * that there is only one possible value for each of x and y for any pair of equations.
 *
 * <h2>Sample Input</h2>
 *
 * <ul>
 *     <li>4x+3y=24</li>
 *     <li>5 x + y =19</li>
 *     <li>#</li>
 *     <li>2x+y=3</li>
 *     <li>3x-y=2</li>
 *     <li>#</li>
 *     <li>##</li>
 * </ul>
 *
 * <h2>Sample Output</h2>
 *
 * <ul>
 *     <li>x=3 y=4</li>
 *     <li>x=1 y=1</li>
 * </ul>
 */
@Path("/")
@Produces(MediaType.TEXT_HTML)
public final class Question3 {
    /**
     * You can run this class as either a standalone console application from a command prompt / shell,
     * or you can deploy it as a JAX-RS resource that will present the input form and results as HTML.
     */
    public static void main(final String[] args) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        final Writer writer = new OutputStreamWriter(System.out);
        process(reader, System.getProperty("line.separator"), writer);
        writer.flush();
    }

    @GET
    public String get() {
        return HTML_FORM;
    }

    @POST
    public StreamingOutput post(@FormParam("data") @DefaultValue("##") final String data) throws IOException {
        final BufferedReader reader = new BufferedReader(new StringReader(data));
        return outputStream -> {
            Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(HTML_RES1);
            process(reader, "<br>", writer);
            writer.write(HTML_RES2);
            writer.flush();
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This method does the work of converting input into results.
     */
    static void process(final BufferedReader in, final String lineSeparator, final Writer out) throws IOException {
        Eqn[] eqns = new Eqn[2];
        int next = 0;
        boolean firstLine = true;
        String line = in.readLine();
        while (line != null) {
            if (line.equals("##")) {
                break;
            } else if (line.equals("#")) {
                if (firstLine) {
                    firstLine = false;
                } else {
                    out.write(lineSeparator);
                }
                solveEqns(eqns[0], eqns[1], out);
                eqns[0] = eqns[1] = null;
                next = 0;
            } else {
                // keep the last two equations in a rotating buffer.
                eqns[next] = parseEqn(line);
                next = 1 - next; // (next + 1) % 2
            }
            line = in.readLine();
        }
    }

    static void solveEqns(final Eqn eqn1, final Eqn eqn2, final Writer out) throws IOException {
        if (eqn1 == null || eqn2 == null) {
            return; // don't have two equations.
        }
        int det = eqn1.xcoeff * eqn2.ycoeff - eqn2.xcoeff * eqn1.ycoeff;
        if (det == 0) {
            return; // no unique solution.
        }

        int xmult = eqn2.ycoeff * eqn1.rvalue - eqn1.ycoeff * eqn2.rvalue;
        out.write("x=");
        if (xmult % det == 0) {
            out.write(Integer.toString(xmult / det)); // integer solution.
        } else {
            // We are told P, solution for x, is an integer, but just in case.
            out.write(Double.toString((double) xmult / det));
        }

        int ymult = eqn1.xcoeff * eqn2.rvalue - eqn2.xcoeff * eqn1.rvalue;
        out.write(" y=");
        if (ymult % det == 0) {
            out.write(Integer.toString(ymult / det)); // integer solution.
        } else {
            // We are told Q, solution for y, is an integer, but just in case.
            out.write(Double.toString((double) ymult / det));
        }
    }

    /**
     * An <tt>Eqn</tt> instance if parses correctly, or null if it doesn't.
     */
    static Eqn parseEqn(final String line) {
        final Matcher matcher = patternEqn.matcher(line);
        if (!matcher.matches()) {
            return null;
        }
        final String strN = matcher.group(1);
        final String strS = matcher.group(2);
        final String strM = matcher.group(3);
        final String strR = matcher.group(4);
        final int n, s, m, r;
        if (strN == null || strN.isEmpty()) {
            n = 1;
        } else {
            n = parseNum(strN, -1);
        }
        if (n < 1 || n > 100) {
            return null;
        }
        if ("+".equals(strS)) {
            s = 1;
        } else if ("-".equals(strS)) {
            s = -1;
        } else {
            return null;
        }
        if (strM == null || strM.isEmpty()) {
            m = 1;
        } else {
            m = parseNum(strM, -1);
        }
        if (m < 1 || m > 100) {
            return null;
        }
        try {
            r = Integer.parseInt(strR);
            if (r < -5000 || r > 5000) {
                return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return new Eqn(n, s * m, r);
    }

    private static int parseNum(final String strNum, final int defVal) {
        try {
            return Integer.parseInt(strNum);
        } catch (NumberFormatException e) {
            return defVal;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Regular expression to match "NxSMy=R" equations as described.
     */
    private static final Pattern patternEqn = Pattern.compile(
            "\\s*(\\d*)\\s*[xX]\\s*([+-])\\s*(\\d*)\\s*[yY]\\s*=\\s*(-?\\d+)\\s*");

    /**
     * Simple bean class to represent the coefficients in an equations. The equals() and toString() methods are only
     * used by the unit tests.
     */
    static final class Eqn {
        final int xcoeff, ycoeff, rvalue;
        Eqn(final int xcoeff, final int ycoeff, final int rvalue) {
            this.xcoeff = xcoeff; this.ycoeff = ycoeff; this.rvalue = rvalue;
        }
        @Override
        public boolean equals(final Object object) {
            if (!(object instanceof Eqn)) { return false; }
            Eqn eqn = (Eqn) object; return this.xcoeff == eqn.xcoeff && this.ycoeff == eqn.ycoeff && this.rvalue == eqn.rvalue;
        }
        @Override
        public String toString() {
            return xcoeff + "x " + (ycoeff >= 0 ? "+ " + ycoeff : "- " + -ycoeff) + "y = " + rvalue;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The input form page.
     */
    private static final String HTML_FORM = String.join("\r\n",
            "<!DOCTYPE html>",
            "<html>",
            "<head>",
            "<meta charset='UTF-8'>",
            "<title>Question 3 Data Input</title>",
            "<style>",
            "html{font-family:sans-serif;font-size:18px;}",
            ".container{margin:auto;width:750px;}",
            "h1{font-size:22px;text-align:center;}",
            "textarea{display:block;font-family:monospace;font-size:18px;margin:auto;overflow:auto;width:75%;}",
            "input[type=submit]{background-color:#059;border-radius:4px;border-width:0;color:#fff;cursor:pointer;" +
                    "display:block;font-size:18px;margin:12px auto;padding:6px;width:90px;}",
            "</style>",
            "</head>",
            "<body>",
            "<div class='container'>",
            "<h1>Question 3 Data Input</h1>",
            "<form method='POST'>",
            "<textarea name='data' rows='20' autofocus></textarea>",
            "<input type='submit' value='Run'>",
            "</form>",
            "</div>",
            "</body>",
            "</html>");
    /**
     * The prefix for the results page.
     */
    private static final String HTML_RES1 = String.join("\r\n",
            "<!DOCTYPE html>",
            "<html>",
            "<head>",
            "<meta charset='UTF-8'>",
            "<title>Question 3 Results</title>",
            "<style>",
            "html{font-family:sans-serif;font-size:18px;}",
            ".container{margin:auto;width:750px;}",
            "h1{font-size:22px;text-align:center;}",
            "pre{background-color:#eee;border-radius:4px;font-family:monospace;font-size:18px;margin:auto;" +
                    "overflow:auto;padding:6px 12px;width:75%;}",
            "</style>",
            "</head>",
            "<body>",
            "<div class='container'>",
            "<h1>Question 3 Results</h1>",
            "<pre>");
    /**
     * The suffix for the results page.
     */
    private static final String HTML_RES2 = String.join("\r\n",
            "</pre>",
            "</div>",
            "</body>",
            "</html>");
}
