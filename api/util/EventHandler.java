/*
 * Title: EventHandler
 * Author: Matthew Boyette
 * Date: 3/12/2013
 * 
 * This class allows me to keep my application window class separate from my application logic.
 * Objects (like my application window) call event handlers. By default, the handlers do nothing.
 * Alternatively, custom event handling code can be supplied either when the object is created or
 * afterward during application execution as a reaction to another event.
 */

package api.util;

import java.awt.AWTEvent;
import java.io.Serializable;

import api.gui.swing.ApplicationWindow;

public class EventHandler<T> implements Runnable, Serializable
{
	private static final long	serialVersionUID	= 1L;
	private T					parent				= null;
	private Class<T>			parentType			= null;
	
	@SuppressWarnings("unchecked")
	public EventHandler(final T parent)
	{
		this.setParent(parent);
		this.setParentType((Class<T>)parent.getClass());
	}
	
	public final T getParent()
	{
		return this.parent;
	}
	
	public final Class<T> getParentType()
	{
		return this.parentType;
	}
	
	@Override
	public void run()
	{
	}
	
	public void run(final ApplicationWindow window)
	{
	}
	
	public void run(final AWTEvent event)
	{
	}
	
	public void run(final Object argument)
	{
	}
	
	public void run(final Object... arguments)
	{
	}
	
	protected final void setParent(final T parent)
	{
		this.parent = parent;
	}
	
	protected final void setParentType(final Class<T> parentType)
	{
		this.parentType = parentType;
	}
}