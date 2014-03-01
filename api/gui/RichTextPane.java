/*
	Title:  RichTextPane
	Author: Matthew Boyette
	Date:   1/21/2012
	
	I wrote this class in a previous Java course because I was not satisfied with JTextPane,
	and lusted after a text box object with much richer features like the RichTextBox control
	available in Visual Basic and C#. Thus, RichTextPane was born.
*/
package api.gui;

import api.util.Support;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class RichTextPane extends JTextPane
{
	private static final long serialVersionUID = 1L;
	private Component         parent           = null;
	private boolean           isDebugging      = false;
	
	public RichTextPane(final Component parent, final boolean isReadOnly, final boolean isDebugging, final Font defaultFont)
	{
		this.setFocusable(!isReadOnly);
		this.setEditable(!isReadOnly);
		this.setFont(defaultFont);
		this.isDebugging = isDebugging;
		this.parent = parent;
		this.clear();
	}
	
	public void append(final Object... arguments) throws IllegalArgumentException
	{
		/*
			The append() helper method takes three arguments. In order for variable number of arguments
			to work, the number of arguments must be evenly divisible by three and they must also be in
			the correct order: Color, Color, String
		*/
		if ((arguments.length % 3) != 0)
		{
			throw new IllegalArgumentException("The variable argument append method received a number of arguments not evenly divisible by three.");
		}

		// Call the append() helper method for each set of arguments.
		for (int i = 0; i < arguments.length; i += 3)
		{
			this.append((Color)arguments[i], (Color)arguments[i+1], arguments[i+2].toString());
		}
	}
	
	public void append(final Color fgc, final Color bgc, final String string)
	{
		// Call getAttributeSet to generate the formatting settings for the text.
		SimpleAttributeSet attributeSet = getAttributeSet(fgc, bgc, false, false, false);

		try
		{
			// Append a string to the current document using the desired attribute set.
			this.getDocument().insertString(this.getDocument().getLength(), string, attributeSet);
		}
		catch (final Exception exception)
		{
			Support.displayException(this.parent, exception, true);
		}
	}
	
	public void clear()
	{
		this.setDocument(new DefaultStyledDocument());
	}
	
	public static SimpleAttributeSet getAttributeSet(final Color fgc, final Color bgc, final boolean isBolded, 
		final boolean isItalicized, final boolean isUnderlined)
	{
		// Create an attribute set.
		SimpleAttributeSet attributeSet = new SimpleAttributeSet();

		// Set styles.
		attributeSet.addAttribute(StyleConstants.CharacterConstants.Bold, isBolded);
		attributeSet.addAttribute(StyleConstants.CharacterConstants.Italic, isItalicized);
		attributeSet.addAttribute(StyleConstants.CharacterConstants.Underline, isUnderlined);

		// Set colors.
		attributeSet.addAttribute(StyleConstants.CharacterConstants.Foreground, fgc);
		attributeSet.addAttribute(StyleConstants.CharacterConstants.Background, bgc);

		// Return the attribute set for use in text formatting.
		return attributeSet;
	}
	
	public void openFile()
	{
		String filePath = Support.getFilePath(this.parent, true, this.isDebugging);

		if ((filePath == null) || filePath.isEmpty())
		{
			return;
		}

		ObjectInputStream inputStream = null;

		try
		{
			// Use binary file manipulation to import a file containing a Document object.
			inputStream = new ObjectInputStream(new FileInputStream(filePath));
			this.setDocument((StyledDocument)inputStream.readObject());
		}
		catch (final Exception exception)
		{
			Support.displayException(this.parent, exception, false);
		}
		finally
		{
			try
			{
				inputStream.close();
			}
			catch (final Exception exception)
			{
				Support.displayException(this.parent, exception, false);
			}
		}
	}
	
	public void saveFile()
	{
		String filePath = Support.getFilePath(this.parent, false, this.isDebugging);

		if ((filePath == null) || filePath.isEmpty())
		{
			return;
		}

		ObjectOutputStream outputStream = null;

		try
		{
			// Use binary file manipulation to export a file containing a Document object.
			outputStream = new ObjectOutputStream(new FileOutputStream(filePath));
			outputStream.writeObject(this.getDocument());
		}
		catch (final Exception exception)
		{
			Support.displayException(this.parent, exception, false);
		}
		finally
		{
			try
			{
				outputStream.close();
			}
			catch (final Exception exception)
			{
				Support.displayException(this.parent, exception, false);
			}
		}
	}
}