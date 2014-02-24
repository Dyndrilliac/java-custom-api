/*
	Title:  EventHandler
	Author: Matthew Boyette
	Date:   3/12/2013
	
	This class allows me to keep my application window class separate from my application logic.
	Objects (like my application window) call event handlers. By default, the handlers do nothing.
	Alternatively, custom event handling code can be supplied either when the object is created or
	afterward during application execution as a reaction to another event.
*/
package api.util;

public class EventHandler implements Runnable
{
	protected Object parent = null;
	
	public EventHandler() {}
	public EventHandler(final Object parent) { this.parent = parent; }
	public void run() {}
	public void run(final Object... arguments) {}
}