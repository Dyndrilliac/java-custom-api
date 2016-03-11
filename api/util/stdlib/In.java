
package api.util.stdlib;

/*************************************************************************
 * Compilation: javac In.java Execution: java In (basic test --- see source for required files) Reads in data of various types from standard input,
 * files, and URLs.
 *************************************************************************/

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * <i>Input</i>. This class provides methods for reading strings and numbers from standard input, file input, URLs, and sockets.
 * <p>
 * The Locale used is: language = English, country = US. This is consistent with the formatting conventions with Java floating-point literals,
 * command-line arguments (via {@link Double#parseDouble(String)}) and standard output.
 * <p>
 * For additional documentation, see <a href="http://introcs.cs.princeton.edu/31datatype">Section 3.1</a> of <i>Introduction to Programming in Java:
 * An Interdisciplinary Approach</i> by Robert Sedgewick and Kevin Wayne.
 * <p>
 * Like {@link Scanner}, reading a token also consumes preceding Java whitespace, reading a full line consumes the following end-of-line delimeter,
 * while reading a character consumes nothing extra.
 * <p>
 * Whitespace is defined in {@link Character#isWhitespace(char)}. Newlines consist of \n, \r, \r\n, and Unicode hex code points 0x2028, 0x2029,
 * 0x0085; see <tt><a href="http://www.docjar.com/html/api/java/util/Scanner.java.html">
 *  Scanner.java</a></tt> (NB: Java 6u23 and earlier uses only \r, \r, \r\n).
 *
 * @author David Pritchard
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public final class In
{

    /*** begin: section (1 of 2) of code duplicated from In to StdIn */

    // assume Unicode UTF-8 encoding
    private static final String  CHARSET_NAME       = "UTF-8";

    // makes whitespace characters significant
    private static final Pattern EMPTY_PATTERN      = Pattern.compile("");

    // used to read the entire input. source:
    // http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
    private static final Pattern EVERYTHING_PATTERN = Pattern.compile("\\A");

    // assume language = English, country = US for consistency with System.out.
    private static final Locale  LOCALE             = Locale.US;

    // the default token separator; we maintain the invariant that this value
    // is held by the scanner's delimiter between calls
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\p{javaWhitespace}+");

    /**
     * Test client.
     */
    public static void main(final String[] args)
    {
        In in;
        String urlName = "http://introcs.cs.princeton.edu/stdlib/InTest.txt";

        // read from a URL
        System.out.println("readAll() from URL " + urlName);
        System.out.println("---------------------------------------------------------------------------");
        try
        {
            in = new In(urlName);
            System.out.println(in.readAll());
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        System.out.println();

        // read one line at a time from URL
        System.out.println("readLine() from URL " + urlName);
        System.out.println("---------------------------------------------------------------------------");
        try
        {
            in = new In(urlName);
            while (!in.isEmpty())
            {
                String s = in.readLine();
                System.out.println(s);
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        System.out.println();

        // read one string at a time from URL
        System.out.println("readString() from URL " + urlName);
        System.out.println("---------------------------------------------------------------------------");
        try
        {
            in = new In(urlName);
            while (!in.isEmpty())
            {
                String s = in.readString();
                System.out.println(s);
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        System.out.println();

        // read one line at a time from file in current directory
        System.out.println("readLine() from current directory");
        System.out.println("---------------------------------------------------------------------------");
        try
        {
            in = new In("./InTest.txt");
            while (!in.isEmpty())
            {
                String s = in.readLine();
                System.out.println(s);
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        System.out.println();

        // read one line at a time from file using relative path
        System.out.println("readLine() from relative path");
        System.out.println("---------------------------------------------------------------------------");
        try
        {
            in = new In("../stdlib/InTest.txt");
            while (!in.isEmpty())
            {
                String s = in.readLine();
                System.out.println(s);
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        System.out.println();

        // read one char at a time
        System.out.println("readChar() from file");
        System.out.println("---------------------------------------------------------------------------");
        try
        {
            in = new In("InTest.txt");
            while (!in.isEmpty())
            {
                char c = in.readChar();
                System.out.print(c);
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        System.out.println();
        System.out.println();

        // read one line at a time from absolute OS X / Linux path
        System.out.println("readLine() from absolute OS X / Linux path");
        System.out.println("---------------------------------------------------------------------------");
        in = new In("/n/fs/introcs/www/java/stdlib/InTest.txt");
        try
        {
            while (!in.isEmpty())
            {
                String s = in.readLine();
                System.out.println(s);
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        System.out.println();

        // read one line at a time from absolute Windows path
        System.out.println("readLine() from absolute Windows path");
        System.out.println("---------------------------------------------------------------------------");
        try
        {
            in = new In("G:\\www\\introcs\\stdlib\\InTest.txt");
            while (!in.isEmpty())
            {
                String s = in.readLine();
                System.out.println(s);
            }
            System.out.println();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        System.out.println();

    }

    /*** end: section (1 of 2) of code duplicated from In to StdIn */

    /**
     * Reads all doubles from stdin
     *
     * @deprecated Clearer to use {@link StdIn#readAllDoubles()}
     */
    @Deprecated
    public static double[] readDoubles()
    {
        return new In().readAllDoubles();
    }

    /**
     * Reads all doubles from a file
     *
     * @deprecated Clearer to use <tt>new In(filename)</tt>.{@link #readAllDoubles()}
     */
    @Deprecated
    public static double[] readDoubles(final String filename)
    {
        return new In(filename).readAllDoubles();
    }

    /**
     * Reads all ints from stdin
     *
     * @deprecated Clearer to use {@link StdIn#readAllInts()}
     */
    @Deprecated
    public static int[] readInts()
    {
        return new In().readAllInts();
    }

    /**
     * Reads all ints from a file
     *
     * @deprecated Clearer to use <tt>new In(filename)</tt>.{@link #readAllInts()}
     */
    @Deprecated
    public static int[] readInts(final String filename)
    {
        return new In(filename).readAllInts();
    }

    /**
     * Reads all strings from stdin
     *
     * @deprecated Clearer to use {@link StdIn#readAllStrings()}
     */
    @Deprecated
    public static String[] readStrings()
    {
        return new In().readAllStrings();
    }

    /**
     * Reads all strings from a file
     *
     * @deprecated Clearer to use <tt>new In(filename)</tt>.{@link #readAllStrings()}
     */
    @Deprecated
    public static String[] readStrings(final String filename)
    {
        return new In(filename).readAllStrings();
    }

    private Scanner scanner;

    /***
     * begin: section (2 of 2) of code duplicated from In to StdIn, with all methods changed from "public" to "public static"
     ***/

    /**
     * Create an input stream from standard input.
     */
    public In()
    {
        this.scanner = new Scanner(new BufferedInputStream(System.in), In.CHARSET_NAME);
        this.scanner.useLocale(In.LOCALE);
    }

    /**
     * Create an input stream from a file.
     */
    public In(final File file)
    {
        try
        {
            this.scanner = new Scanner(file, In.CHARSET_NAME);
            this.scanner.useLocale(In.LOCALE);
        }
        catch (IOException ioe)
        {
            System.err.println("Could not open " + file);
        }
    }

    /**
     * Create an input stream from a socket.
     */
    public In(final java.net.Socket socket)
    {
        try
        {
            InputStream is = socket.getInputStream();
            this.scanner = new Scanner(new BufferedInputStream(is), In.CHARSET_NAME);
            this.scanner.useLocale(In.LOCALE);
        }
        catch (IOException ioe)
        {
            System.err.println("Could not open " + socket);
        }
    }

    /**
     * Create an input stream from a given Scanner source; use with <tt>new Scanner(String)</tt> to read from a string.
     * <p>
     * Note that this does not create a defensive copy, so the scanner will be mutated as you read on.
     */
    public In(final Scanner scanner)
    {
        this.scanner = scanner;
    }

    /**
     * Create an input stream from a filename or web page name.
     */
    public In(final String s)
    {
        try
        {
            // first try to read file from local file system
            File file = new File(s);
            if (file.exists())
            {
                this.scanner = new Scanner(file, In.CHARSET_NAME);
                this.scanner.useLocale(In.LOCALE);
                return;
            }

            // next try for files included in jar
            URL url = this.getClass().getResource(s);

            // or URL from web
            if (url == null)
            {
                url = new URL(s);
            }

            URLConnection site = url.openConnection();

            // in order to set User-Agent, replace above line with these two
            // HttpURLConnection site = (HttpURLConnection) url.openConnection();
            // site.addRequestProperty("User-Agent", "Mozilla/4.76");

            InputStream is = site.getInputStream();
            this.scanner = new Scanner(new BufferedInputStream(is), In.CHARSET_NAME);
            this.scanner.useLocale(In.LOCALE);
        }
        catch (IOException ioe)
        {
            System.err.println("Could not open " + s);
        }
    }

    /**
     * Create an input stream from a URL.
     */
    public In(final URL url)
    {
        try
        {
            URLConnection site = url.openConnection();
            InputStream is = site.getInputStream();
            this.scanner = new Scanner(new BufferedInputStream(is), In.CHARSET_NAME);
            this.scanner.useLocale(In.LOCALE);
        }
        catch (IOException ioe)
        {
            System.err.println("Could not open " + url);
        }
    }

    /**
     * Close the input stream.
     */
    public void close()
    {
        this.scanner.close();
    }

    /**
     * Does the input stream exist?
     */
    public boolean exists()
    {
        return this.scanner != null;
    }

    /**
     * Is the input empty (including whitespace)? Use this to know whether the next call to {@link #readChar()} will succeed.
     * <p>
     * Functionally equivalent to {@link #hasNextLine()}.
     */
    public boolean hasNextChar()
    {
        this.scanner.useDelimiter(In.EMPTY_PATTERN);
        boolean result = this.scanner.hasNext();
        this.scanner.useDelimiter(In.WHITESPACE_PATTERN);
        return result;
    }

    /**
     * Does the input have a next line? Use this to know whether the next call to {@link #readLine()} will succeed.
     * <p>
     * Functionally equivalent to {@link #hasNextChar()}.
     */
    public boolean hasNextLine()
    {
        return this.scanner.hasNextLine();
    }

    /**
     * Is the input empty (except possibly for whitespace)? Use this to know whether the next call to {@link #readString()}, {@link #readDouble()},
     * etc will succeed.
     */
    public boolean isEmpty()
    {
        return !this.scanner.hasNext();
    }

    /**
     * Read and return the remainder of the input as a string.
     */
    public String readAll()
    {
        if (!this.scanner.hasNextLine())
        {
            return "";
        }

        String result = this.scanner.useDelimiter(In.EVERYTHING_PATTERN).next();
        // not that important to reset delimeter, since now scanner is empty
        this.scanner.useDelimiter(In.WHITESPACE_PATTERN); // but let's do it anyway
        return result;
    }

    /**
     * Read all doubles until the end of input is reached, and return them.
     */
    public double[] readAllDoubles()
    {
        String[] fields = this.readAllStrings();
        double[] vals = new double[fields.length];
        for (int i = 0; i < fields.length; i++)
        {
            vals[i] = Double.parseDouble(fields[i]);
        }
        return vals;
    }

    /**
     * Read all ints until the end of input is reached, and return them.
     */
    public int[] readAllInts()
    {
        String[] fields = this.readAllStrings();
        int[] vals = new int[fields.length];
        for (int i = 0; i < fields.length; i++)
        {
            vals[i] = Integer.parseInt(fields[i]);
        }
        return vals;
    }

    /**
     * Reads all remaining lines from input stream and returns them as an array of strings.
     *
     * @return all remaining lines on input stream, as an array of strings
     */
    public String[] readAllLines()
    {
        ArrayList<String> lines = new ArrayList<String>();
        while (this.hasNextLine())
        {
            lines.add(this.readLine());
        }
        return lines.toArray(new String[0]);
    }

    /**
     * Read all strings until the end of input is reached, and return them.
     */
    public String[] readAllStrings()
    {
        // we could use readAll.trim().split(), but that's not consistent
        // since trim() uses characters 0x00..0x20 as whitespace
        String[] tokens = In.WHITESPACE_PATTERN.split(this.readAll());
        if ((tokens.length == 0) || (tokens[0].length() > 0))
        {
            return tokens;
        }
        String[] decapitokens = new String[tokens.length - 1];
        for (int i = 0; i < (tokens.length - 1); i++)
        {
            decapitokens[i] = tokens[i + 1];
        }
        return decapitokens;
    }

    /**
     * Read and return the next boolean, allowing case-insensitive "true" or "1" for true, and "false" or "0" for false.
     */
    public boolean readBoolean()
    {
        String s = this.readString();
        if (s.equalsIgnoreCase("true"))
        {
            return true;
        }
        if (s.equalsIgnoreCase("false"))
        {
            return false;
        }
        if (s.equals("1"))
        {
            return true;
        }
        if (s.equals("0"))
        {
            return false;
        }
        throw new InputMismatchException();
    }

    /**
     * Read and return the next byte.
     */
    public byte readByte()
    {
        return this.scanner.nextByte();
    }

    /*** end: section (2 of 2) of code duplicated from In to StdIn */

    /**
     * Read and return the next character.
     */
    public char readChar()
    {
        this.scanner.useDelimiter(In.EMPTY_PATTERN);
        String ch = this.scanner.next();
        assert (ch.length() == 1) : "Internal (Std)In.readChar() error!" + " Please contact the authors.";
        this.scanner.useDelimiter(In.WHITESPACE_PATTERN);
        return ch.charAt(0);
    }

    /**
     * Read and return the next double.
     */
    public double readDouble()
    {
        return this.scanner.nextDouble();
    }

    /**
     * Read and return the next float.
     */
    public float readFloat()
    {
        return this.scanner.nextFloat();
    }

    /**
     * Read and return the next int.
     */
    public int readInt()
    {
        return this.scanner.nextInt();
    }

    /**
     * Read and return the next line.
     */
    public String readLine()
    {
        String line;
        try
        {
            line = this.scanner.nextLine();
        }
        catch (Exception e)
        {
            line = null;
        }
        return line;
    }

    /**
     * Read and return the next long.
     */
    public long readLong()
    {
        return this.scanner.nextLong();
    }

    /**
     * Read and return the next short.
     */
    public short readShort()
    {
        return this.scanner.nextShort();
    }

    /**
     * Read and return the next string.
     */
    public String readString()
    {
        return this.scanner.next();
    }

}
