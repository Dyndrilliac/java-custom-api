
package api.util.stdlib;

/*************************************************************************
 * Compilation: javac StdOut.java
 * Execution: java StdOut
 *
 * Writes data of various types to standard output.
 *
 *************************************************************************/

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * <i>Standard output</i>. This class provides methods for writing strings
 * and numbers to standard output.
 * <p>
 * For additional documentation, see <a href="http://introcs.cs.princeton.edu/15inout">Section 1.5</a> of <i>Introduction to Programming in Java: An
 * Interdisciplinary Approach</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public final class StdOut
{
	
	// force Unicode UTF-8 encoding; otherwise it's system dependent
	private static final String	CHARSET_NAME	= "UTF-8";
	
	// assume language = English, country = US for consistency with StdIn
	private static final Locale	LOCALE			= Locale.US;
	
	// send output here
	private static PrintWriter	out;
	
	// this is called before invoking any methods
	static
	{
		try
		{
			StdOut.out = new PrintWriter(new OutputStreamWriter(System.out, StdOut.CHARSET_NAME), true);
		}
		catch (UnsupportedEncodingException e)
		{
			System.out.println(e);
		}
	}
	
	// close the output stream (not required)
	/**
	 * Close standard output.
	 */
	public static void close()
	{
		StdOut.out.close();
	}
	
	// This method is just here to test the class
	public static void main(final String[] args)
	{
		
		// write to stdout
		StdOut.println("Test");
		StdOut.println(17);
		StdOut.println(true);
		StdOut.printf("%.6f\n", 1.0 / 7.0);
	}
	
	/**
	 * Flush standard output.
	 */
	public static void print()
	{
		StdOut.out.flush();
	}
	
	/**
	 * Print a boolean to standard output and flush standard output.
	 */
	public static void print(final boolean x)
	{
		StdOut.out.print(x);
		StdOut.out.flush();
	}
	
	/**
	 * Print a byte to standard output and flush standard output.
	 */
	public static void print(final byte x)
	{
		StdOut.out.print(x);
		StdOut.out.flush();
	}
	
	/**
	 * Print a char to standard output and flush standard output.
	 */
	public static void print(final char x)
	{
		StdOut.out.print(x);
		StdOut.out.flush();
	}
	
	/**
	 * Print a double to standard output and flush standard output.
	 */
	public static void print(final double x)
	{
		StdOut.out.print(x);
		StdOut.out.flush();
	}
	
	/**
	 * Print a float to standard output and flush standard output.
	 */
	public static void print(final float x)
	{
		StdOut.out.print(x);
		StdOut.out.flush();
	}
	
	/**
	 * Print an int to standard output and flush standard output.
	 */
	public static void print(final int x)
	{
		StdOut.out.print(x);
		StdOut.out.flush();
	}
	
	/**
	 * Print a long to standard output and flush standard output.
	 */
	public static void print(final long x)
	{
		StdOut.out.print(x);
		StdOut.out.flush();
	}
	
	/**
	 * Print an Object to standard output and flush standard output.
	 */
	public static void print(final Object x)
	{
		StdOut.out.print(x);
		StdOut.out.flush();
	}
	
	/**
	 * Print a short to standard output and flush standard output.
	 */
	public static void print(final short x)
	{
		StdOut.out.print(x);
		StdOut.out.flush();
	}
	
	/**
	 * Print a formatted string to standard output using the specified
	 * locale, format string, and arguments, and flush standard output.
	 */
	public static void printf(final Locale locale, final String format, final Object... args)
	{
		StdOut.out.printf(locale, format, args);
		StdOut.out.flush();
	}
	
	/**
	 * Print a formatted string to standard output using the specified
	 * format string and arguments, and flush standard output.
	 */
	public static void printf(final String format, final Object... args)
	{
		StdOut.out.printf(StdOut.LOCALE, format, args);
		StdOut.out.flush();
	}
	
	/**
	 * Terminate the current line by printing the line separator string.
	 */
	public static void println()
	{
		StdOut.out.println();
	}
	
	/**
	 * Print a boolean to standard output and then terminate the line.
	 */
	public static void println(final boolean x)
	{
		StdOut.out.println(x);
	}
	
	/**
	 * Print a byte to standard output and then terminate the line.
	 */
	public static void println(final byte x)
	{
		StdOut.out.println(x);
	}
	
	/**
	 * Print a char to standard output and then terminate the line.
	 */
	public static void println(final char x)
	{
		StdOut.out.println(x);
	}
	
	/**
	 * Print a double to standard output and then terminate the line.
	 */
	public static void println(final double x)
	{
		StdOut.out.println(x);
	}
	
	/**
	 * Print a float to standard output and then terminate the line.
	 */
	public static void println(final float x)
	{
		StdOut.out.println(x);
	}
	
	/**
	 * Print an int to standard output and then terminate the line.
	 */
	public static void println(final int x)
	{
		StdOut.out.println(x);
	}
	
	/**
	 * Print a long to standard output and then terminate the line.
	 */
	public static void println(final long x)
	{
		StdOut.out.println(x);
	}
	
	/**
	 * Print an object to standard output and then terminate the line.
	 */
	public static void println(final Object x)
	{
		StdOut.out.println(x);
	}
	
	/**
	 * Print a short to standard output and then terminate the line.
	 */
	public static void println(final short x)
	{
		StdOut.out.println(x);
	}
	
	// don't instantiate
	private StdOut()
	{
	}
	
}
