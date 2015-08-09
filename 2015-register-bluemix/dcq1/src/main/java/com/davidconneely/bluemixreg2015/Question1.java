package com.davidconneely.bluemixreg2015;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;

/**
 * <h1>Question 1: Factorials</h1>
 * <p>
 * The factorial of a number is a multiple of all integers between 1 and that number inclusive. For example, the
 * factorial of 5 (expressed as 5!) would be 5x4x3x2x1 = 120.
 * <p>
 * Write a program that calculates the factorial of each input value.
 * <p>
 * <h2>Input data</h2>
 * <p>
 * The input will consist of a series of positive integers between 1 and 15 inclusive, each on a line by itself. The end
 * of the input data file will be signified by a line with a # mark as the first character.
 * <p>
 * <h2>Output data</h2>
 * <p>
 * You should output, one answer per line, the factorial of each given number, in the format shown in the sample output
 * below.
 * <p>
 * <h2>Sample input</h2>
 * <p>
 * <ul>
 * <li>1</li>
 * <li>4</li>
 * <li>3</li>
 * <li>#</li>
 * </ul>
 * <p>
 * <h2>Sample output</h2>
 * <p>
 * <ul>
 * <li>1</li>
 * <li>24</li>
 * <li>6</li>
 * </ul>
 */
@Path("/")
@Produces(MediaType.TEXT_HTML)
public final class Question1 {
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
    public StreamingOutput post(@FormParam("data") @DefaultValue("#") final String data) throws IOException {
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
     * This method does the work of converting input into results. It can be called by a JAX-RS resource or by command-line.
     */
    static void process(final BufferedReader in, final String lineSeparator, final Writer out) throws IOException {
        boolean firstLine = true;
        String line = in.readLine();
        while (line != null) {
            if (line.length() != 0 && line.charAt(0) == '#') {
                break;
            }
            if (firstLine) {
                firstLine = false;
            } else {
                out.write(lineSeparator);
            }
            processLine(line, out);
            line = in.readLine();
        }
    }

    private static void processLine(final String line, final Writer out) throws IOException {
        String strNum = line.trim();
        final int num = parseNum(strNum, -1);
        if (num >= 1 && num <= 15) {
            out.write(Long.toString(FACTORIAL[num]));
        }
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
     * Cache of factorial values.
     */
    private static final long[] FACTORIAL;

    static {
        long[] factorial = new long[16];
        factorial[0] = factorial(0);
        for (int i = 1; i <= 15; ++i) {
            factorial[i] = factorial[i - 1] * i;
        }
        FACTORIAL = factorial;
    }

    /**
     * Factorial calculation. Works on any number between 0 and 20.
     */
    static long factorial(int n) {
        if (n < 0 || n > 20) {
            throw new IllegalArgumentException();
        }
        long fac = 1;
        for (int i = 2; i <= n; ++i) {
            fac *= i;
        }
        return fac;
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
            "<title>Question 1 Data Input</title>",
            "<style>",
            "html{font-family:sans-serif;font-size:18px;}",
            ".container{margin:auto;width:750px;}",
            "h1{font-size:22px;text-align:center;}",
            "textarea{display:block;font-family:monospace;font-size:18px;margin:auto;overflow:auto;width:50%;}",
            "input[type=submit]{background-color:#059;border-radius:4px;border-width:0;color:#fff;cursor:pointer;" +
                    "display:block;font-size:18px;margin:12px auto;padding:6px;width:90px;}",
            "</style>",
            "</head>",
            "<body>",
            "<div class='container'>",
            "<h1>Question 1 Data Input</h1>",
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
            "<title>Question 1 Results</title>",
            "<style>",
            "html{font-family:sans-serif;font-size:18px;}",
            ".container{margin:auto;width:750px;}",
            "h1{font-size:22px;text-align:center;}",
            "pre{background-color:#eee;border-radius:4px;font-family:monospace;font-size:18px;margin:auto;" +
                    "overflow:auto;padding:6px 12px;width:50%;}",
            "</style>",
            "</head>",
            "<body>",
            "<div class='container'>",
            "<h1>Question 1 Results</h1>",
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
