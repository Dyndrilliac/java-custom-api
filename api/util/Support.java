/*
 * Title: Support
 * Author: Matthew Boyette
 * Date: 1/21/2012
 * 
 * This class is merely a collection of useful static methods that support code recycling. Specifically, this
 * class offers methods and classes which provide a uniform support structure for all of my personal projects.
 */

package api.util;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import api.util.stdlib.StdAudio;
import api.util.stdlib.StdIn;
import api.util.stdlib.StdOut;

public final class Support
{
	public final static Font	DEFAULT_TEXT_FONT	= new Font("Lucida Console", Font.PLAIN, 14);
	
	public final static void clearConsole(final Component parent)
	{
		try
		{
			final String os = System.getProperty("os.name");
			
			if (os.contains("Windows"))
			{
				Runtime.getRuntime().exec("cls");
			}
			else
			{
				Runtime.getRuntime().exec("clear");
			}
		}
		catch (final Exception e)
		{
			Support.displayException(parent, e, false);
		}
	}
	
	public static final String constructAlphabetString(final int begin, final int end)
	{
		boolean badInputs = ((begin < 0) || (begin > end));
		
		if (badInputs)
		{
			Support.displayException(null, new Exception("Invalid alphabet string!"), true);
		}
		
		String alphabet = "";
		
		for (int i = begin; i <= end; i++)
		{
			alphabet = (alphabet + (char)i);
		}
		
		return alphabet;
	}
	
	public final static int countLinesInTextFile(final String fileName)
	{
		LineNumberReader lineNumReader = null;
		
		try
		{
			lineNumReader = new LineNumberReader(new FileReader(new File(fileName)));
			while ((lineNumReader.readLine()) != null)
			{
				;
			}
			return lineNumReader.getLineNumber();
		}
		catch (final Exception exception)
		{
			exception.printStackTrace();
		}
		finally
		{
			if (lineNumReader != null)
			{
				try
				{
					lineNumReader.close();
				}
				catch (IOException exception)
				{
					exception.printStackTrace();
				}
			}
		}
		
		return -1;
	}
	
	// This method displays special debugging messages to be used for diagnostic purposes.
	public final static void displayDebugMessage(final Component parent, final Object object)
	{
		if (parent == null)
		{
			Support.normalizeUIX(parent);
		}
		
		String message = "";
		
		if (object == null)
		{
			Support.displayException(parent, new NullPointerException("displayDebugMessage(): 'object' is null."), false);
		}
		
		if (object instanceof AWTEvent)
		{
			AWTEvent event = (AWTEvent)object;
			message += "What: " + event.paramString() + "\n";
			message += "Where: " + event.getSource().getClass().getSimpleName() + " (" + event.getSource().getClass().getCanonicalName() + ")\n";
		}
		else
			if (object instanceof String)
			{
				message = (String)object;
			}
		
		message += "When: " + Support.getDateTimeStamp() + "\n";
		JOptionPane.showMessageDialog(parent, message, "Debugging Event", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public final static void displayException(final Component parent, final Exception exception, final boolean isFatal)
	{
		if (parent == null)
		{
			Support.normalizeUIX(parent);
		}
		/*
		 * Display error message along with some useful debugging information.
		 * Source file is where the error chain ended, which could be null in the case of a function in the Java API.
		 * Cause file is where the error chain began, which is the bottom of the stack and where the bad method is likely to be.
		 */
		String dialogTitle = null, recoveryMessage = null;
		
		if (isFatal)
		{
			dialogTitle = "Fatal Exception Occurred";
			recoveryMessage = "This error is fatal. The program cannot recover from the problem, and will be terminated following this message.";
		}
		else
		{
			dialogTitle = "Non-fatal Exception Occurred";
			recoveryMessage = "This error is not fatal. The program has recovered from the problem, and you may continue operating it.";
		}
		
		JOptionPane.showMessageDialog(parent,
			exception.toString() +
			"\n\nSource file: " + exception.getStackTrace()[0].getFileName() +
			"\nLine number: " + exception.getStackTrace()[0].getLineNumber() +
			"\n\nCause file: " + exception.getStackTrace()[exception.getStackTrace().length - 1].getFileName() +
			"\nLine number: " + exception.getStackTrace()[exception.getStackTrace().length - 1].getLineNumber() +
			"\n\nWhen: " + Support.getDateTimeStamp() +
			"\n\nRecovery: " + recoveryMessage,
			dialogTitle,
			JOptionPane.ERROR_MESSAGE);
		exception.printStackTrace();
		
		if (isFatal)
		{
			System.exit(-1);
		}
	}
	
	public final static Process executeShellCommand(final String command) throws Exception
	{
		return new ProcessBuilder(command).start();
	}
	
	public final static boolean getChoiceInput(final Component parent, final String message, final String title)
	{
		if (parent == null)
		{
			Support.normalizeUIX(parent);
		}
		
		return ((JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION));
	}
	
	// Get Date/Time stamp in the default format.
	public final static String getDateTimeStamp()
	{
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MM.dd.yyyy hh:mm:ss a z");
		return dateFormatter.format(new Date());
	}
	
	// Get Date/Time stamp in a custom format.
	public final static String getDateTimeStamp(final String dateFormat)
	{
		SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
		return dateFormatter.format(new Date());
	}
	
	public final static double getDoubleInputString(final Component parent, final String message, final String title)
	{
		if (parent == null)
		{
			Support.normalizeUIX(parent);
		}
		
		String s = null;
		
		do
		{
			s = Support.getInputString(parent, message, title);
		}
		while (Support.isStringParsedAsDouble(s) != true);
		
		return Double.parseDouble(s);
	}
	
	// This method prompts the user to either open or save a file using a generic dialog box and returns the path to the selected file.
	public final static String getFilePath(final Component parent, final boolean isOpen, final boolean isDebugging)
	{
		if (parent == null)
		{
			Support.normalizeUIX(parent);
		}
		
		JFileChooser fileDialog = new JFileChooser();
		String filePath = null;
		boolean isDone = false;
		int choice = 0;
		
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
					catch (final Exception exception)
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
			Support.displayDebugMessage(null, "File Path: " + filePath + "\n");
		}
		
		return filePath;
	}
	
	public final static Image getImageByResourceName(final Component parent, final String resourceName)
	{
		if (parent == null)
		{
			Support.normalizeUIX(parent);
		}
		
		InputStream input = Support.getResourceByName(resourceName);
		Image image = null;
		
		try
		{
			image = ImageIO.read(input);
		}
		catch (final Exception exception)
		{
			image = null;
			Support.displayException(parent, exception, false);
		}
		
		return image;
	}
	
	public final static String getInputString(final Component parent, final String message, final String title)
	{
		if (parent == null)
		{
			Support.normalizeUIX(parent);
		}
		
		String s = null;
		
		do
		{
			s = JOptionPane.showInputDialog(parent, message, title, JOptionPane.QUESTION_MESSAGE);
		}
		while ((s == null) || s.isEmpty());
		
		return s;
	}
	
	public final static int getIntegerInputString(final Component parent, final String message, final String title)
	{
		if (parent == null)
		{
			Support.normalizeUIX(parent);
		}
		
		String s = null;
		
		do
		{
			s = Support.getInputString(parent, message, title);
		}
		while (Support.isStringParsedAsInteger(s) != true);
		
		return Integer.parseInt(s);
	}
	
	public final static InputStream getResourceByName(final String resourceName)
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		return classLoader.getResourceAsStream(resourceName);
	}
	
	// This method takes a string and determines if it can be safely parsed as a boolean.
	// Return value of true indicates that the string is safe to parse, and false means that the string is not safe to parse.
	public final static boolean isStringParsedAsBoolean(final String s)
	{
		try
		{
			// parseBoolean throws an exception if the string can't be parsed.
			Boolean.parseBoolean(s);
		}
		catch (final Exception exception)
		{
			// If we catch an exception, then we return false.
			return false;
		}
		
		// Base case; return true if the string was parsed without an exception being thrown.
		return true;
	}
	
	// This method takes a string and determines if it can be safely parsed as a double.
	// Return value of true indicates that the string is safe to parse, and false means that the string is not safe to parse.
	public final static boolean isStringParsedAsDouble(final String s)
	{
		try
		{
			// parseDouble throws an exception if the string can't be parsed.
			Double.parseDouble(s);
		}
		catch (final Exception exception)
		{
			// If we catch an exception, then we return false.
			return false;
		}
		
		// Base case; return true if the string was parsed without an exception being thrown.
		return true;
	}
	
	// This method takes a string and determines if it can be safely parsed as a float.
	// Return value of true indicates that the string is safe to parse, and false means that the string is not safe to parse.
	public final static boolean isStringParsedAsFloat(final String s)
	{
		try
		{
			// parseFloat throws an exception if the string can't be parsed.
			Float.parseFloat(s);
		}
		catch (final Exception exception)
		{
			// If we catch an exception, then we return false.
			return false;
		}
		
		// Base case; return true if the string was parsed without an exception being thrown.
		return true;
	}
	
	// This method takes a string and determines if it can be safely parsed as an integer.
	// Return value of true indicates that the string is safe to parse, and false means that the string is not safe to parse.
	public final static boolean isStringParsedAsInteger(final String s)
	{
		try
		{
			// parseInt throws an exception if the string can't be parsed.
			Integer.parseInt(s);
		}
		catch (final Exception exception)
		{
			// If we catch an exception, then we return false.
			return false;
		}
		
		// Base case; return true if the string was parsed without an exception being thrown.
		return true;
	}
	
	public final static void normalizeUIX(final Component parent)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (final Exception exception)
		{
			Support.displayException(parent, exception, false);
		}
	}
	
	public final static void openWebPageInDefaultBrowser(final Component parent, final String url)
	{
		if (Desktop.isDesktopSupported())
		{
			try
			{
				Desktop.getDesktop().browse(new URI(url));
			}
			catch (final Exception exception)
			{
				Support.displayException(parent, exception, false);
			}
		}
	}
	
	public final static String padLeftEvenly(final String s, final char c)
	{
		String result = s;
		
		if ((result != null) && Mathematics.isOdd(result.length()))
		{
			StringBuilder sb = new StringBuilder();
			
			sb.append(c);
			sb.append(result);
			
			result = sb.toString();
		}
		
		return result;
	}
	
	public final static String padLeftEvenly(final String s, final char c, final long n)
	{
		String result = Support.padLeftEvenly(s, c);
		
		if ((result != null) && (result.length() <= n))
		{
			StringBuilder sb = new StringBuilder();
			
			for (int i = 0; i < n; i++)
			{
				sb.append(c);
			}
			
			String padding = sb.toString();
			result = padding.substring(Math.min((int)n, result.length())) + result;
		}
		
		return result;
	}
	
	public final static String padRightEvenly(final String s, final char c)
	{
		String result = s;
		
		if ((result != null) && Mathematics.isOdd(result.length()))
		{
			StringBuilder sb = new StringBuilder();
			
			sb.append(result);
			sb.append(c);
			
			result = sb.toString();
		}
		
		return result;
	}
	
	public final static String padRightEvenly(final String s, final char c, final long n)
	{
		String result = Support.padRightEvenly(s, c);
		
		if ((result != null) && (result.length() <= n))
		{
			StringBuilder sb = new StringBuilder();
			
			for (int i = 0; i < n; i++)
			{
				sb.append(c);
			}
			
			String padding = sb.toString();
			result = result + padding.substring(Math.min((int)n, result.length()));
		}
		
		return result;
	}
	
	public final static void pauseConsole()
	{
		StdOut.println("Press the [Enter] key to continue...");
		StdIn.readLine();
	}
	
	public final static void playAudioClipFromURL(final Component parent, final String url)
	{
		try
		{
			StdAudio.play(url);
		}
		catch (final Exception exception)
		{
			Support.displayException(parent, exception, false);
		}
	}
	
	/*
	 * This method is a wrapper for a specific invocation of JOptionPane.showConfirmDialog that I use frequently to prompt test users
	 * for debugging modes.
	 */
	public final static boolean promptDebugMode(final Component parent)
	{
		if (parent == null)
		{
			Support.normalizeUIX(parent);
		}
		
		return Support.getChoiceInput(parent,
			"Do you wish to activate debugging mode?\n\n" +
			"Turning on debugging mode will enable extra diagnostic features that are helpful when testing this application for errors.",
			"Debugging Mode");
	}
}