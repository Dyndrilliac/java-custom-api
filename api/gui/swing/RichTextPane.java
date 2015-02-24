/*
 * Title: RichTextPane
 * Author: Matthew Boyette
 * Date: 1/21/2012
 *
 * I wrote this class in a previous Java course because I was not satisfied with JTextPane,
 * and lusted after a text box object with much richer features like the RichTextBox control
 * available in Visual Basic and C#. Thus, RichTextPane was born.
 */

package api.gui.swing;

import java.awt.Color;
import java.awt.Component;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import api.util.Support;

public class RichTextPane extends JTextPane
{
	private final static long	serialVersionUID	= 1L;

	public static SimpleAttributeSet getAttributeSet(final Color fgc, final Color bgc,
		final boolean isBolded, final boolean isItalicized, final boolean isUnderlined)
	{
		// Create an attribute set.
		SimpleAttributeSet attributeSet = new SimpleAttributeSet();

		// Set styles.
		attributeSet.addAttribute(StyleConstants.Bold, isBolded);
		attributeSet.addAttribute(StyleConstants.Italic, isItalicized);
		attributeSet.addAttribute(StyleConstants.Underline, isUnderlined);

		// Set colors.
		attributeSet.addAttribute(StyleConstants.Foreground, fgc);
		attributeSet.addAttribute(StyleConstants.Background, bgc);

		// Return the attribute set for use in text formatting.
		return attributeSet;
	}

	private boolean		isDebugging	= false;
	private Component	parent		= null;

	public RichTextPane(final Component parent, final boolean isReadOnly, final boolean isDebugging)
	{
		this.setFocusable(!isReadOnly);
		this.setEditable(!isReadOnly);
		this.setFont(Support.DEFAULT_TEXT_FONT);
		this.setBackground(Color.WHITE);
		this.setDebugging(isDebugging);
		this.parent = parent;
		this.clear();
	}

	protected final void append(final Color fgc, final Color bgc, final String string)
	{
		// Call getAttributeSet to generate the formatting settings for the text.
		SimpleAttributeSet attributeSet = RichTextPane.getAttributeSet(fgc, bgc, false, false, false);

		try
		{
			// Append a string to the current document using the desired attribute set.
			this.getDocument().insertString(this.getDocument().getLength(), string, attributeSet);
		}
		catch (final Exception exception)
		{
			Support.displayException(this.parent, exception, true);
		}

		this.setCaretPosition(this.getDocument().getLength());
	}

	public final void append(final Object... arguments) throws IllegalArgumentException
	{
		/*
		 * The append() helper method takes three arguments. In order for variable number of arguments
		 * to work, the number of arguments must be evenly divisible by three and they must also be in
		 * the correct order: Color, Color, String
		 */
		if ((arguments.length % 3) != 0)
		{
			throw new IllegalArgumentException("The variable argument append method received a number of arguments not evenly divisible by three.");
		}

		// Call the append() helper method for each set of arguments.
		for (int i = 0; i < arguments.length; i += 3)
		{
			this.append((Color)arguments[i], (Color)arguments[i + 1], arguments[i + 2].toString());
		}
	}

	public final void clear()
	{
		this.setDocument(new DefaultStyledDocument());
	}

	public final boolean isDebugging()
	{
		return this.isDebugging;
	}

	public final void openOrSaveFile(final boolean isOpen)
	{
		Object stream = null;
		String filePath = Support.getFilePath(this.parent, isOpen, this.isDebugging);

		if ((filePath == null) || filePath.isEmpty())
		{
			return;
		}

		try
		{
			if (isOpen)
			{
				// Use binary file manipulation to import a file containing a Document object.
				stream = new ObjectInputStream(new FileInputStream(filePath));
				this.setDocument((StyledDocument)((ObjectInputStream)stream).readObject());
			}
			else
			{
				// Use binary file manipulation to export a file containing a Document object.
				stream = new ObjectOutputStream(new FileOutputStream(filePath));
				((ObjectOutputStream)stream).writeObject(this.getDocument());
			}

		}
		catch (final Exception exception)
		{
			Support.displayException(this.parent, exception, false);
		}
		finally
		{
			if (stream != null)
			{
				try
				{
					if (isOpen)
					{
						((ObjectInputStream)stream).close();
					}
					else
					{
						((ObjectOutputStream)stream).close();
					}
				}
				catch (final Exception exception)
				{
					Support.displayException(this.parent, exception, false);
				}
			}
		}
	}

	protected final void setDebugging(final boolean isDebugging)
	{
		this.isDebugging = isDebugging;
	}
}