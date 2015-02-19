/*
 * Title: ApplicationWindow
 * Author: Matthew Boyette
 * Date: 1/21/2012
 *
 * This class allows me to quickly and easily create custom application windows. Since virtually every window is almost identical
 * it makes little sense to write the same GUI code over and over again when only small changes are needed for each application.
 */

package api.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import api.util.EventHandler;
import api.util.Support;

public class ApplicationWindow extends JFrame implements ActionListener
{
	private final static long	serialVersionUID	= 1L;
	private EventHandler<?>		actionPerformed		= null;
	private EventHandler<?>		drawGUI				= null;
	private boolean				isDebugging			= false;

	public ApplicationWindow(final Component parent, final String applicationTitle, final Dimension size, final boolean isDebugging,
		final boolean isResizable, final EventHandler<?> actionPerformed, final EventHandler<?> drawGUI)
	{
		super(applicationTitle);

		Support.normalizeUIX(this);

		this.setDebugging(isDebugging);
		this.setSize(size);
		this.setResizable(isResizable);
		this.setFont(Support.DEFAULT_TEXT_FONT);

		if (parent == null)
		{
			this.setLocationByPlatform(true);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		else
		{
			this.setLocationRelativeTo(parent);
			this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		}

		this.setActionPerformed(actionPerformed);
		this.setDrawGUI(drawGUI);
		this.drawGUI();
		this.setVisible(true);
	}

	@Override
	public final void actionPerformed(final ActionEvent event)
	{
		if (this.isDebugging())
		{
			Support.displayDebugMessage(this, event);
		}

		if (this.getActionPerformed() != null)
		{
			try
			// This could be code supplied by a third-party in another module.
			{	// Execute it in a try/catch block to be safe.
				this.getActionPerformed().run(event);
			}
			catch (final Exception exception)
			{
				Support.displayException(this, exception, false);
			}
		}
	}

	public final void drawGUI()
	{
		if (this.getDrawGUI() != null)
		{
			try
			// This could be code supplied by a third-party in another module.
			{	// Execute it in a try/catch block to be safe.
				this.getDrawGUI().run(this);
			}
			catch (final Exception exception)
			{
				Support.displayException(this, exception, false);
			}
		}
	}

	protected final EventHandler<?> getActionPerformed()
	{
		return this.actionPerformed;
	}

	protected final EventHandler<?> getDrawGUI()
	{
		return this.drawGUI;
	}

	public final boolean isDebugging()
	{
		return this.isDebugging;
	}

	public void reDrawGUI()
	{
		this.getContentPane().removeAll();
		this.drawGUI();
		this.validate();
	}

	protected final void setActionPerformed(final EventHandler<?> actionPerformed)
	{
		this.actionPerformed = actionPerformed;
	}

	protected final void setDebugging(final boolean isDebugging)
	{
		this.isDebugging = isDebugging;
	}

	protected final void setDrawGUI(final EventHandler<?> drawGUI)
	{
		this.drawGUI = drawGUI;
	}

	public void setIconImageByResourceName(final String resourceName)
	{
		Image icon = Support.getImageByResourceName(this, resourceName);
		this.setIconImage(icon);
	}
}