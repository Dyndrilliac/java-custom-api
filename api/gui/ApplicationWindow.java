/*
	Title:  ApplicationWindow
	Author: Matthew Boyette
	Date:   1/21/2012
	
	This class allows me to quickly and easily create custom application windows. Since virtually every window is almost identical
	it makes little sense to write the same GUI code over and over again when only small changes are needed for each application.
*/

package api.gui;

import api.util.EventHandler;
import api.util.Support;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class ApplicationWindow extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private List<Component>   elements         = new ArrayList<Component>();
	private EventHandler      actionPerformed  = null;
	private EventHandler      drawGUI          = null;
	private boolean           isDebugging      = false;
	
	public ApplicationWindow(final Component parent, final String applicationTitle, final Dimension size, final boolean isDebugging, 
		final boolean isResizable, final EventHandler actionPerformed, final EventHandler drawGUI)
	{
		super(applicationTitle);
		
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception exception) {}
		
		this.setDebugging(isDebugging);
		this.setSize(size);
		this.setResizable(isResizable);
		
		if (parent == null)
		{
			this.setLocationByPlatform(true);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		else
		{
			this.setLocationRelativeTo(parent);
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
		
		this.setActionPerformed(actionPerformed);
		this.setDrawGUI(drawGUI);
		this.drawGUI();
		this.setVisible(true);
	}
	
	public void actionPerformed(final ActionEvent event)
	{
		if (this.isDebugging())
		{
			Support.displayDebugMessage(this, event);
		}
		
		if (this.getActionPerformed() != null)
		{
			try // This could be code supplied by a third-party in another module.
			{   // Execute it in a try/catch block to be safe.
				this.getActionPerformed().run(event, this);
			}
			catch (Exception exception)
			{
				Support.displayException(this, exception, true);
			}
		}
	}
	
	public void drawGUI()
	{
		if (this.getDrawGUI() != null)
		{
			try // This could be code supplied by a third-party in another module.
			{   // Execute it in a try/catch block to be safe.
				this.getDrawGUI().run(this);
			}
			catch (Exception exception)
			{
				Support.displayException(this, exception, true);
			}
		}
	}
	
	public EventHandler getActionPerformed()
	{
		return this.actionPerformed;
	}
	
	public EventHandler getDrawGUI()
	{
		return this.drawGUI;
	}
	
	public List<Component> getElements()
	{
		return this.elements;
	}
	
	public boolean isDebugging()
	{
		return this.isDebugging;
	}
	
	public void setActionPerformed(final EventHandler actionPerformed)
	{
		this.actionPerformed = actionPerformed;
	}
	
	public void setDebugging(final boolean isDebugging)
	{
		this.isDebugging = isDebugging;
	}
	
	public void setDrawGUI(final EventHandler drawGUI)
	{
		this.drawGUI = drawGUI;
	}
	
	public void setElements(final List<Component> elements)
	{
		this.elements = elements;
	}
	
	public void setIconImageByResourceName(String resourceName)
	{
		InputStream input = Support.getResourceByName(resourceName);
		Image icon = null;
		
		try
		{
			icon = ImageIO.read(input);
		}
		catch (Exception exception)
		{
			Support.displayException(this, exception, true);
		}
		
		this.setIconImage(icon);
	}
}