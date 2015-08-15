package com.davidconneely.bluemixreg2015;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.Arrays;

/**
 * <h1>Question 2: Change</h1>
 *
 * You are writing a software module for a ticket machine that will be based at a railway station. Your module controls
 * how the ticket machine gives change to customers who pay by cash.
 *
 * Your module will be provided with a number of data sets. Each data set has two elements: the list of coins available,
 * and the amount of money that needs to be dispensed to the customer as his or her change for the ticket they just
 * bought. The program must decide the set of coins to be dispensed on the basis that:
 *
 * <ul>
 *     <li>As few coins as possible must be dispensed.</li>
 *     <li>If there is more than one way to dispense the minimal number of coins, higher denomination coins take
 *         precedence.</li>
 * </ul>
 *
 * For each data set there must be exactly one output line. You may assume that if a coin denomination is listed in the
 * coin list, there is sufficient stock of that coin to service the requirement. You may also assume that each
 * denomination will be listed at most once in a data set.
 *
 * <h2>Input data</h2>
 *
 * Each transaction your program deals with is represented by a line in the input file. The line begins with a
 * comma-separated list of coin denominations available (in pence), followed by a colon, followed by the amount of money
 * (in pence) that needs to be served to the customer. The coin list may be in any order, i.e. the denominations are not
 * necessarily sorted in any way.
 * For example, imagine we have coins with values of £1 (100p), 50p, 20p, 10p, 5p, 2p and 1p and we wish to give the
 * customer 57p. The input line would be:
 *
 * <blockquote>100,50,20,10,5,2,1:57</blockquote>
 *
 * The input data may contain space or tab characters, which must be ignored. The end of the input data file will be
 * signified by a line with a # mark as the first character. You may assume that there will be no more than 20 coin
 * denominations, that no line will be more than 100 characters in length, and that there are no malformed lines.
 *
 * <h2>Output data</h2>
 *
 * You must list the coins that are to be dispensed. For each coin, state its denomination, followed by 'x', followed
 * by the quantity of that coin that must be dispensed. Coin outputs must be separated with commas. Coins must be listed
 * in descending order of denomination. So the answer for the above example would be:
 *
 * <blockquote>50x1,5x1,2x1</blockquote>
 *
 * ... as the most efficient way to dispense 57p from the given set of coins is a single 50p, one 5p and one 2p. There
 * should be no spaces or other whitespace in the output data, and the termination line in the input file (the one
 * beginning with #) should have no corresponding line in the output file.
 *
 * <h2>Sample input</h2>
 *
 * <ul>
 *     <li>100,50,20,10,5,2,1:57</li>
 *     <li>100, 10, 50, 20, 5, 2, 1:36</li>
 *     <li>#</li>
 * </ul>
 *
 * <h2>Sample output</h2>
 *
 * <ul>
 *     <li>50x1,5x1,2x1</li>
 *     <li>20x1,10x1,5x1,1x1</li>
 * </ul>
 */
@Path("/")
@Produces(MediaType.TEXT_HTML)
public final class Question2 {
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
    private static void process(final BufferedReader in, final String lineSeparator, final Writer out) throws IOException {
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
        // Parse the available coins.
        final int colon = line.lastIndexOf(':');
        final String[] strAvailCoins = (colon >= 0 ? line.substring(0, colon) : line).trim().split("\\s*,\\s*");
        final int len = strAvailCoins.length;
        final int[] availCoins = new int[len];
        for (int i = 0; i < len; ++i) {
            availCoins[i] = parseNum(strAvailCoins[i], 0);
        }
        // Parse the target change.
        String strTargetChange = (colon >= 0 ? line.substring(colon + 1) : "").trim();
        final int targetChange = parseNum(strTargetChange, 0);
        // Output the coins dispensed.
        processChange(availCoins, targetChange, out);
    }

    private static int parseNum(final String strNum, final int defVal) {
        try {
            return Integer.parseInt(strNum);
        } catch (NumberFormatException e) {
            return defVal;
        }
    }

    /**
     * Look for change. Remember that this is a consumer-facing railway ticket machine, so it may have to dispense more
     * change than necessary if it doesn't have exact change (but it should overpay the minimum excess possible).
     * This method is non-private because we want to unit test it.
     */
    static void processChange(int[] availCoins, final int targetChange, final Writer out) throws IOException {
        Arrays.sort(availCoins);
        // If there are no available coins, negative or 0 valued coins, negative or 0 target change, then no change.
        if (availCoins.length == 0 || availCoins[0] <= 0 || targetChange <= 0 || targetChange > MAX_TARGET_CHANGE) {
            return;
        }
        // (We are allowed to assume the coins have no duplicates. If we were not, we would de-duplicate them here.)

        // We may have to pay an excess of up to a penny less than the smallest coin.
        final int maxChange = targetChange + availCoins[0] - 1;
        final int[] minCounts = new int[maxChange + 1];
        final int[] minIndexes = new int[maxChange + 1];
        makeChange(availCoins, maxChange, minCounts, minIndexes);

        // Find the lowest value we can dispense and print out the coins for it.
        for (int change = targetChange; change <= maxChange; ++change) {
            if (minIndexes[change] != -1) {
                printChange(availCoins, change, minIndexes, out);
                return;
            }
        }
    }

    /**
     * Populate the minCounts and minIndexes arrays with the optimal values for each value of change up to maxChange.
     * @param availCoins Sorted (ascending order) array of available denominations (input).
     * @param maxChange We will calculate for all values of change up to the maximum specified (input).
     * @param minCounts For each amount of change (represented by the index into this array) what the minimal number of
     *                  coins to add up to that total is (output: array of length maxChange + 1).
     * @param minIndexes Each element in the array is an index into the array of availCoins of the last coin needed to
     *                   make change for a particular amount of change, so minIndexes[10] == 3 means that the last coin
     *                   to make change for 10 is the 3rd availCoin (output: array be of length maxChange + 1).
     */
    private static void makeChange(final int[] availCoins, final int maxChange, final int[] minCounts, final int[] minIndexes) {
        minCounts[0] = 0;
        for (int change = 1; change <= maxChange; ++change) {
            int minCount = maxChange + 1; // as good as infinity
            int minIndex = -1;
            for (int index = availCoins.length - 1; index >= 0; --index) {
                final int availCoin = availCoins[index];
                if (availCoin <= change) {
                    int count = minCounts[change - availCoin] + 1;
                    if (count < minCount) {
                        minCount = count;
                        minIndex = index;
                    }
                }
            }
            minCounts[change] = minCount;
            minIndexes[change] = minIndex;
        }
    }

    /**
     * Calculate the number of coins of each index and print them out in the specified format (largest first).
     * @param availCoins Sorted (ascending order) array of available denominations (input).
     * @param maxChange We will calculate for all values of change up to the maximum specified (input).
     * @param minIndexes Each element in the array is an index into the array of availCoins of the last coin needed to
     *                   make change for a particular amount of change, so minIndexes[10] == 3 means that the last coin
     *                   to make change for 10 is the 3rd availCoin (input).
     * @param out Where to print to (input).
     */
    private static void printChange(final int[] availCoins, final int maxChange, final int[] minIndexes, final Writer out) throws IOException {
        final int[] counts = new int[availCoins.length];
        int change = maxChange;
        while (change > 0) {
            int minIndex = minIndexes[change];
            counts[minIndex]++;
            change -= availCoins[minIndex];
        }
        // Output the coins.
        boolean firstCoin = true;
        for (int index = availCoins.length - 1; index >= 0; --index) {
            final int count = counts[index];
            if (count > 0) {
                if (firstCoin) {
                    firstCoin = false;
                } else {
                    out.write(',');
                }
                out.write(Integer.toString(availCoins[index]));
                out.write('x');
                out.write(Integer.toString(count));
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * In real life, being asked to give GBP 100,000.00 (10,000,000 pence) in change for a railway ticket almost
     * certainly indicates an error in some other component of the ticket machine. Technically, we need a maximum
     * because we use O(N) space to solve the problem.
     */
    private static final int MAX_TARGET_CHANGE = 10000000;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The input form page.
     */
    private static final String HTML_FORM = String.join("\r\n",
            "<!DOCTYPE html>",
            "<html>",
            "<head>",
            "<meta charset='UTF-8'>",
            "<title>Question 2 Data Input</title>",
            "<style>",
            "html{font-family:sans-serif;font-size:18px;}",
            ".container{margin:auto;width:750px;}",
            "h1{font-size:22px;text-align:center;}",
            "textarea{display:block;font-family:monospace;font-size:18px;margin:auto;overflow:auto;width:100%;}",
            "input[type=submit]{background-color:#059;border-radius:4px;border-width:0;color:#fff;cursor:pointer;" +
                    "display:block;font-size:18px;margin:12px auto;padding:6px;width:90px;}",
            "</style>",
            "</head>",
            "<body>",
            "<div class='container'>",
            "<h1>Question 2 Data Input</h1>",
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
            "<title>Question 2 Results</title>",
            "<style>",
            "html{font-family:sans-serif;font-size:18px;}",
            ".container{margin:auto;width:750px;}",
            "h1{font-size:22px;text-align:center;}",
            "pre{background-color:#eee;border-radius:4px;font-family:monospace;font-size:18px;margin:auto;" +
                    "overflow:auto;padding:6px 12px;width:100%;}",
            "</style>",
            "</head>",
            "<body>",
            "<div class='container'>",
            "<h1>Question 2 Results</h1>",
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
