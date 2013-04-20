/*
	Title:  Support
	Author: Matthew Boyette
	Date:   1/21/2012
	
	This class is merely a collection of useful static methods that support code recycling. Specifically, 
	this class offers utility methods which form a uniform support structure for all of my personal projects. 
*/
package api.util;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Desktop;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Support
{
	// This method displays special debugging messages to be used for diagnostic purposes.
	public static void displayDebugMessage(final Component parent, final Object object)
	{
		String message = "";
		
		if (object == null) displayException(parent, new NullPointerException("displayDebugMessage(): 'object' is null."), true);
		
		if (object instanceof AWTEvent)
		{
			AWTEvent event = (AWTEvent)object;
			message += "What: " + event.paramString() + "\n";
			message += "Where: " + event.getSource().getClass().getSimpleName() + 
				" (" + event.getSource().getClass().getCanonicalName() + ")\n";
		}
		else if (object instanceof String)
		{
			message = (String)object;
		}
		
		message += "When: " + getDateTimeStamp() + "\n";
		JOptionPane.showMessageDialog(parent, message, "Debugging Event", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void displayException(final Component parent, final Exception exception, final boolean isFatal)
	{
		/*
			Display error message along with some useful debugging information.
			Source file is where the error chain ended, which could be null in the case of a function in the Java API.
			Cause file is where the error chain began, which is the bottom of the stack and where the bad method is likely to be.
		*/
		String dialogTitle     = null;
		String recoveryMessage = null;
		
		if (isFatal)
		{
			dialogTitle = "Fatal Exception Occurred";
			recoveryMessage = "This error is fatal. The program cannot recover from the problem, and will be terminated following this message.";
		}
		else
		{
			dialogTitle = "Exception Occurred";
			recoveryMessage = "This error is not fatal. The program has recovered from the problem, and you may continue operating it.";
		}
		
		JOptionPane.showMessageDialog(parent,
			exception.toString() + 
			"\n\nSource file: " + exception.getStackTrace()[0].getFileName() +
			"\nLine number: " + exception.getStackTrace()[0].getLineNumber() +
			"\n\nCause file: " + exception.getStackTrace()[exception.getStackTrace().length-1].getFileName() +
			"\nLine number: " + exception.getStackTrace()[exception.getStackTrace().length-1].getLineNumber() +
			"\n\nWhen: " + getDateTimeStamp() +
			"\n\nRecovery: " + recoveryMessage,
			dialogTitle,
			JOptionPane.ERROR_MESSAGE);
		exception.printStackTrace();
		
		if (isFatal)
		{
			System.exit(-1);
		}
	}
	
	// Get Date/Time stamp in the default format.
	public static String getDateTimeStamp()
	{
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MM.dd.yyyy hh:mm:ss a z");
		return dateFormatter.format(new Date());
	}
	
	// Get Date/Time stamp in a custom format.
	public static final String getDateTimeStamp(final String dateFormat)
	{
		SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
		return dateFormatter.format(new Date());
	}
	
	// This method prompts the user to either open or save a file using a generic dialog box and returns the path to the selected file.
	public static String getFilePath(final Component parent, final boolean isOpen, final boolean isDebugging)
	{
		JFileChooser fileDialog = new JFileChooser();
		String       filePath   = null;
		boolean      isDone     = false;
		int          choice     = 0;

		do // Loop while 'isDone' equals false, post-test.
		{
			if (isOpen)
			{
				choice = fileDialog.showOpenDialog(parent);
			}
			else
			{
				choice = fileDialog.showSaveDialog(parent);
			}

			switch (choice)
			{
				case JFileChooser.APPROVE_OPTION:

					try
					{
						filePath = fileDialog.getSelectedFile().getCanonicalPath();
						isDone = true;
					}
					catch (Exception exception)
					{
						filePath = null;
						isDone = false;
					}
					break;

				case JFileChooser.CANCEL_OPTION:

					filePath = null;
					isDone = true;
					break;

				default:

					filePath = null;
					isDone = false;
					break;
			}
		}
		while (!isDone);

		if (isDebugging)
		{
			displayDebugMessage(null, "File Path: " + filePath + "\n");
		}

		return filePath;
	}
	
	public static InputStream getResourceByName(final String resourceName)
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream input = classLoader.getResourceAsStream(resourceName);
		return input;
	}
	
	public static boolean isPrime(final long n)
	{
		// Every prime number is an integer greater than one. If 'n' is less than or equal to one, mark it as composite (not prime).
		if ( n > 1 )
		{	// Check to make sure 'n' is not two or three. If it is either, than we can go ahead and mark it as prime.
			if ( (n != 2) && (n != 3) )
			{	// Since two and three have been handled, we want to know if 'n' is evenly divisible by two or three and mark it as composite.
				if ( ((n % 2) != 0) || ((n % 3) != 0) )
				{
					// Every prime number can be represented by the form 6k+1 or 6k-1. If 'n' cannot be represented this way, 
					// then we mark it as composite.
					if ( (((n+1) % 6) == 0) || (((n-1) % 6) == 0) )
					{
						// If a number can be factored into two numbers, at least one of them should be less than or equal to its square root.
						long limit = (long)Math.ceil(Math.sqrt((double)n));
						// Since we have eliminated all primes less than five, and two is the only even prime, 
						// we only need to check odd numbers from here on out.
						for (long i = 5; i <= limit; i += 2)
						{
							// Every prime number is only evenly divisible by itself and one. 
							// 'i' will never equal 'n' and 'i' will never equal one.
							// Thus, if 'n' is evenly divisible by 'i' then it cannot be prime.
							if ( (n % i) == 0 )
							{
								return false;
							}
						}

						return true;
					}
				}
			}
			else
			{
				return true;
			}
		}

		return false;
	}
	
	// This method takes a string and determines if it can be safely parsed as a boolean.
	// Return value of true indicates that the string is safe to parse, and false means that the string is not safe to parse.
	public static boolean isStringParsedAsBoolean(final String s)
	{
		try
		{
			// parseBoolean throws an exception if the string can't be parsed.
			Boolean.parseBoolean(s);
		}
		catch (Exception exception)
		{
			// If we catch an exception, then we return false.
			return false;
		}

		// Base case; return true if the string was parsed without an exception being thrown.
		return true;
	}

	// This method takes a string and determines if it can be safely parsed as a float.
	// Return value of true indicates that the string is safe to parse, and false means that the string is not safe to parse.
	public static boolean isStringParsedAsFloat(final String s)
	{
		try
		{
			// parseFloat throws an exception if the string can't be parsed.
			Float.parseFloat(s);
		}
		catch (Exception exception)
		{
			// If we catch an exception, then we return false.
			return false;
		}

		// Base case; return true if the string was parsed without an exception being thrown.
		return true;
	}

	// This method takes a string and determines if it can be safely parsed as an integer.
	// Return value of true indicates that the string is safe to parse, and false means that the string is not safe to parse.
	public static boolean isStringParsedAsInteger(final String s)
	{
		try
		{
			// parseInt throws an exception if the string can't be parsed.
			Integer.parseInt(s);
		}
		catch (Exception exception)
		{
			// If we catch an exception, then we return false.
			return false;
		}

		// Base case; return true if the string was parsed without an exception being thrown.
		return true;
	}
	
	public static void openWebPageInDefaultBrowser(final String s)
	{
		if (Desktop.isDesktopSupported())
		{
			try
			{
				Desktop.getDesktop().browse(new URI(s));
			}
			catch (Exception exception)
			{
				displayException(null, exception, false);
			}
		}
	}
	
	public static void playAudioClipFromURL(final String s)
	{
		try
		{
			URL url = new URL(s);
			AudioClip ac = Applet.newAudioClip(url);
			ac.play();
	    }
		catch (Exception exception)
		{
			displayException(null, exception, false);
		}
	}
	
	// This method is a wrapper for a specific invocation of JOptionPane.showConfirmDialog that I use frequently to prompt test 
	// users for debugging modes.
	public static int promptDebugMode(final Component parent)
	{
		return JOptionPane.showConfirmDialog(parent, 
			"Do you wish to activate the debugging mode?\n\n" + 
			"Turning on the debugging mode will cause diagnostic messages to be displayed during special events that are helpful when testing this application.\n\n" + 
			"Note: This choice has no effect on error messages. Error messages will always be displayed!\n\n" + 
			"Closing this message without choosing \"Yes\" or \"No\" will close this application.", 
			"Debugging Mode", 
			JOptionPane.YES_NO_OPTION);
	}
}