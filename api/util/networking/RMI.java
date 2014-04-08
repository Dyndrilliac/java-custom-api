/*
	Title:  RMI
	Author: Matthew Boyette
	Date:   4/5/2014
	
	This class is merely a collection of useful static methods that support code recycling. Specifically, this
	class offers methods and classes which would be useful to networking applications that are implemented using
	RMI.
*/

package api.util.networking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.LinkedList;
import java.util.List;

import api.gui.ApplicationWindow;
import api.util.Support;

public class RMI
{
	/*
		This object encapsulates part of the necessary networking functionality for a simple RMI-based TCP/IP server application.
		
		Specifically, this class represents the portion of the server which services a user's requests. Every time a SimpleServerThread object
		accepts a new client connection, it spawns a SimpleChildServerThread object to handle that client. This allows for concurrency and a
		more robust server.
	*/
	public static abstract class SimpleChildServerThread extends Thread
	{
		// Built-in Variables & Objects
		private boolean				isConnected	= false;
		private BufferedReader		input		= null;
		private BufferedWriter		output		= null;
		private SimpleServerThread	parent		= null;
		private ApplicationWindow	window		= null;
		
		// Creates a new instance of a SimpleChildServerThread object.
		public SimpleChildServerThread(final SimpleServerThread parent, final ApplicationWindow window)
		{
			super("SimpleChildServerThread");
			this.setParent(parent);
			this.setWindow(window);
			this.open();
		}
		
		// Closes the connection and frees up I/O resources.
		public void close()
		{
			try
			{
				if (this.getOutput() != null)
				{
					this.getOutput().close();
				}
				
				if (this.getInput() != null)
				{
					this.getInput().close();
				}
			}
			catch (final Exception e)
			{
				Support.displayException(this.getWindow(), e, false);
			}
			finally
			{
				this.setOutput(null);
				this.setInput(null);
				this.setConnected(false);
			}
		}
		
		public final BufferedReader getInput()
		{
			return this.input;
		}
		
		public final BufferedWriter getOutput()
		{
			return this.output;
		}
		
		public final SimpleServerThread getParent()
		{
			return this.parent;
		}
		
		public final ApplicationWindow getWindow()
		{
			return this.window;
		}
		
		// Returns whether or not a connection is open.
		public final boolean isConnected()
		{
			return this.isConnected;
		}
		
		// Opens the connection and allocates needed I/O resources.
		public void open()
		{
			try
			{
				// TODO
				this.setConnected(true);
			}
			catch (final Exception e)
			{
				this.close();
				Support.displayException(this.getWindow(), e, true);
			}
		}
		
		// Empty stub. This method is defined by a sub-class.
		@Override
		public abstract void run();
		
		// Sets whether or not a connection is open.
		protected final void setConnected(final boolean connected)
		{
			this.isConnected = connected;
		}
		
		protected final void setInput(final BufferedReader input)
		{
			this.input = input;
		}
		
		protected final void setOutput(final BufferedWriter output)
		{
			this.output = output;
		}
		
		protected final void setParent(final SimpleServerThread parent)
		{
			this.parent = parent;
		}
		
		protected final void setWindow(final ApplicationWindow window)
		{
			this.window = window;
		}
	}
	
	/*
		This object encapsulates the necessary networking functionality for a simple RMI-based TCP/IP client application.
	*/
	public static abstract class SimpleClientThread extends Thread
	{
		// Built-in Variables & Objects
		private boolean				isConnected	= false;
		private BufferedReader		input		= null;
		private BufferedWriter		output		= null;
		private String				remoteHost	= null;
		private int					remotePort	= 0;
		private ApplicationWindow	window		= null;
		
		// Creates a new instance of a SimpleClientThread object.
		public SimpleClientThread(final String remoteHost, final int remotePort, final ApplicationWindow window)
		{
			super("SimpleClientThread");
			this.setRemoteHost(remoteHost);
			this.setRemotePort(remotePort);
			this.setWindow(window);
			this.open();
		}
		
		// Closes the connection and frees up I/O resources.
		public void close()
		{
			try
			{
				if (this.getOutput() != null)
				{
					this.getOutput().close();
				}
				
				if (this.getInput() != null)
				{
					this.getInput().close();
				}
			}
			catch (final Exception e)
			{
				Support.displayException(this.getWindow(), e, false);
			}
			finally
			{
				this.setOutput(null);
				this.setInput(null);
				this.setConnected(false);
			}
		}
		
		public final BufferedReader getInput()
		{
			return this.input;
		}
		
		public final BufferedWriter getOutput()
		{
			return this.output;
		}
		
		// Returns the remote host.
		public final String getRemoteHost()
		{
			return this.remoteHost;
		}
		
		// Returns the remote TCP port.
		public final int getRemotePort()
		{
			return this.remotePort;
		}
		
		public final ApplicationWindow getWindow()
		{
			return this.window;
		}
		
		// Returns whether or not a connection is open.
		public final boolean isConnected()
		{
			return this.isConnected;
		}
		
		// Opens the connection and allocates needed I/O resources.
		public void open()
		{
			try
			{
				// TODO
				this.setConnected(true);
			}
			catch (final Exception e)
			{
				this.close();
				Support.displayException(this.getWindow(), e, true);
			}
		}
		
		// Empty stub. This method is defined by a sub-class.
		@Override
		public abstract void run();
		
		// Sets whether or not a connection is open.
		protected final void setConnected(final boolean connected)
		{
			this.isConnected = connected;
		}
		
		protected final void setInput(final BufferedReader input)
		{
			this.input = input;
		}
		
		protected final void setOutput(final BufferedWriter output)
		{
			this.output = output;
		}
		
		// Sets the remote host.
		protected final void setRemoteHost(final String remoteHost)
		{
			this.remoteHost = remoteHost;
		}
		
		// Sets the remote TCP port.
		protected final void setRemotePort(final int remotePort)
		{
			this.remotePort = remotePort;
		}
		
		protected final void setWindow(final ApplicationWindow window)
		{
			this.window = window;
		}
	}
	
	/*
		This object encapsulates part of the necessary networking functionality for a simple socket-based TCP/IP server application.
		
		Specifically, this class represents the portion of the server which listens for new connection attempts. Every time a SimpleServerThread
		object accepts a new client connection, it spawns a SimpleChildServerThread object to handle that client. This allows for concurrency and
		a more robust server.
	*/
	public static abstract class SimpleServerThread extends Thread
	{
		// Built-in Variables & Objects
		private List<SimpleChildServerThread>	clientList		= null;
		private boolean							listening		= false;
		private int								listeningPort	= 0;
		private ApplicationWindow				window			= null;
		
		// Creates a new instance of a SimpleServerThread object.
		public SimpleServerThread(final int listeningPort, final ApplicationWindow window)
		{
			super("SimpleServerThread");
			this.setClientList(new LinkedList<SimpleChildServerThread>());
			this.setListeningPort(listeningPort);
			this.setWindow(window);
			this.open();
		}
		
		// Closes the socket and stops the listening process.
		public void close()
		{
			try
			{
				// TODO
			}
			catch (final Exception e)
			{
				Support.displayException(this.getWindow(), e, false);
			}
			finally
			{
				this.setListening(false);
			}
		}
		
		// Returns the list of clients currently being handled by this server.
		public final List<SimpleChildServerThread> getClientList()
		{
			return this.clientList;
		}
		
		// Returns the TCP port which the server is listening on for new connections.
		public final int getListeningPort()
		{
			return this.listeningPort;
		}
		
		public final ApplicationWindow getWindow()
		{
			return this.window;
		}
		
		// Returns whether or not the server is listening for new connections.
		public final boolean isListening()
		{
			return this.listening;
		}
		
		// Opens the socket and begins the listening process.
		public void open()
		{
			try
			{
				this.setListening(true);
			}
			catch (final Exception e)
			{
				this.close();
				Support.displayException(this.getWindow(), e, true);
			}
		}
		
		// Empty stub. This method is defined by a sub-class.
		@Override
		public abstract void run();
		
		protected final void setClientList(final List<SimpleChildServerThread> clientList)
		{
			this.clientList = clientList;
		}
		
		// Sets whether or not the server is listening for new connections.
		protected final void setListening(final boolean listening)
		{
			this.listening = listening;
		}
		
		// Sets the TCP port which the server is listening on for new connections.
		protected final void setListeningPort(final int listeningPort)
		{
			this.listeningPort = listeningPort;
		}
		
		protected final void setWindow(final ApplicationWindow window)
		{
			this.window = window;
		}
	}
}