
package api.util.stdlib;

/*************************************************************************
 * Compilation: javac Out.java
 * Execution: java Out
 *
 * Writes data of various types to: stdout, file, or socket.
 *
 *************************************************************************/

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Locale;

/**
 * This class provides methods for writing strings and numbers to
 * various output streams, including standard output, file, and sockets.
 * <p>
 * For additional documentation, see <a href="http://introcs.cs.princeton.edu/31datatype">Section 3.1</a> of <i>Introduction to Programming in Java: An
 * Interdisciplinary Approach</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public class Out
{

	// force Unicode UTF-8 encoding; otherwise it's system dependent
	private static final String	CHARSET_NAME	= "UTF-8";

	// assume language = English, country = US for consistency with In
	private static final Locale	LOCALE			= Locale.US;

	/**
	 * A test client.
	 */
	public static void main(final String[] args)
	{
		Out out;

		// write to stdout
		out = new Out();
		out.println("Test 1");
		out.close();

		// write to a file
		out = new Out("test.txt");
		out.println("Test 2");
		out.close();
	}

	private PrintWriter	out;

	/**
	 * Create an Out object using standard output.
	 */
	public Out()
	{
		this(System.out);
	}

	/**
	 * Create an Out object using an OutputStream.
	 */
	public Out(final OutputStream os)
	{
		try
		{
			OutputStreamWriter osw = new OutputStreamWriter(os, Out.CHARSET_NAME);
			this.out = new PrintWriter(osw, true);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Create an Out object using a Socket.
	 */
	public Out(final Socket socket)
	{
		try
		{
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os, Out.CHARSET_NAME);
			this.out = new PrintWriter(osw, true);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Create an Out object using a file specified by the given name.
	 */
	public Out(final String s)
	{
		try
		{
			OutputStream os = new FileOutputStream(s);
			OutputStreamWriter osw = new OutputStreamWriter(os, Out.CHARSET_NAME);
			this.out = new PrintWriter(osw, true);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Close the output stream.
	 */
	public void close()
	{
		this.out.close();
	}

	/**
	 * Flush the output stream.
	 */
	public void print()
	{
		this.out.flush();
	}

	/**
	 * Print an boolean and then flush the output stream.
	 */
	public void print(final boolean x)
	{
		this.out.print(x);
		this.out.flush();
	}

	/**
	 * Print a byte and then flush the output stream.
	 */
	public void print(final byte x)
	{
		this.out.print(x);
		this.out.flush();
	}

	/**
	 * Print an char and then flush the output stream.
	 */
	public void print(final char x)
	{
		this.out.print(x);
		this.out.flush();
	}

	/**
	 * Print an double and then flush the output stream.
	 */
	public void print(final double x)
	{
		this.out.print(x);
		this.out.flush();
	}

	/**
	 * Print a float and then flush the output stream.
	 */
	public void print(final float x)
	{
		this.out.print(x);
		this.out.flush();
	}

	/**
	 * Print an int and then flush the output stream.
	 */
	public void print(final int x)
	{
		this.out.print(x);
		this.out.flush();
	}

	/**
	 * Print a long and then flush the output stream.
	 */
	public void print(final long x)
	{
		this.out.print(x);
		this.out.flush();
	}

	/**
	 * Print an object and then flush the output stream.
	 */
	public void print(final Object x)
	{
		this.out.print(x);
		this.out.flush();
	}

	/**
	 * Print a formatted string using the specified locale, format string and arguments,
	 * and then flush the output stream.
	 */
	public void printf(final Locale locale, final String format, final Object... args)
	{
		this.out.printf(locale, format, args);
		this.out.flush();
	}

	/**
	 * Print a formatted string using the specified format string and arguments,
	 * and then flush the output stream.
	 */
	public void printf(final String format, final Object... args)
	{
		this.out.printf(Out.LOCALE, format, args);
		this.out.flush();
	}

	/**
	 * Terminate the line.
	 */
	public void println()
	{
		this.out.println();
	}

	/**
	 * Print a boolean and then terminate the line.
	 */
	public void println(final boolean x)
	{
		this.out.println(x);
	}

	/**
	 * Print a byte and then terminate the line.
	 */
	public void println(final byte x)
	{
		this.out.println(x);
	}

	/**
	 * Print a char and then terminate the line.
	 */
	public void println(final char x)
	{
		this.out.println(x);
	}

	/**
	 * Print an double and then terminate the line.
	 */
	public void println(final double x)
	{
		this.out.println(x);
	}

	/**
	 * Print a float and then terminate the line.
	 */
	public void println(final float x)
	{
		this.out.println(x);
	}

	/**
	 * Print an int and then terminate the line.
	 */
	public void println(final int x)
	{
		this.out.println(x);
	}

	/**
	 * Print a long and then terminate the line.
	 */
	public void println(final long x)
	{
		this.out.println(x);
	}

	/**
	 * Print an object and then terminate the line.
	 */
	public void println(final Object x)
	{
		this.out.println(x);
	}

}
