/*
	Title: Networking
	Author: Matthew Boyette
	Date: 1/12/2014
	
	This class is merely a collection of useful static methods that support code recycling. Specifically, this
	class offers methods and classes which would be useful to networking applications such as clients and servers.
*/

package api.util;

import api.gui.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Networking
{
	/*
		This object encapsulates part of the necessary networking functionality for a simple socket-based TCP/IP server application.
	
		Specifically, this class represents the portion of the server which services a user's requests. Every time a SimpleServerThread object
		accepts a new client connection, it spawns a SimpleChildServerThread object to handle that client. This allows for concurrency and a
		more robust server.
	*/
	public static abstract class SimpleChildServerThread extends Thread
	{
		// Built-in Variables & Objects
		private boolean					connected	= false;
		private SimpleServerThread		parent		= null;
		private Socket					socket		= null;
		private String					userID		= null;
		private ApplicationWindow		window		= null;
		
		// Creates a new instance of a SimpleChildServerThread object.
		public SimpleChildServerThread(final SimpleServerThread parent, final Socket socket, final ApplicationWindow window)
		{
			super("SimpleChildServerThread");
			this.setParent(parent);
			this.setSocket(socket);
			this.setWindow(window);
			this.open();
		}
		
		// Closes the connection and frees up I/O resources.
		public void close()
		{
			try
			{
				if (this.getSocket() != null)
				{
					this.getSocket().close();
				}
			}
			catch (final Exception e)
			{
				Support.displayException(this.getWindow(), e, false);
			}
			finally
			{
				this.setSocket(null);
				this.setUserID(null);
				this.setConnected(false);
			}
		}
		
		public final SimpleServerThread getParent()
		{
			return this.parent;
		}
		
		public final Socket getSocket()
		{
			return this.socket;
		}
		
		// Returns the identifier for the user being handled by this server thread, which is a combination of the user's IP address and local TCP port.
		public final String getUserID()
		{
			return this.userID;
		}
		
		public final ApplicationWindow getWindow()
		{
			return this.window;
		}
		
		// Returns whether or not a connection is open.
		public final boolean isConnected()
		{
			return this.connected;
		}
		
		// Opens the connection and allocates needed I/O resources.
		public void open()
		{
			try
			{
				this.setUserID(this.getSocket().getRemoteSocketAddress() + ":" + this.getSocket().getPort());
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
		public final void setConnected(final boolean connected)
		{
			this.connected = connected;
		}
		
		public final void setParent(final SimpleServerThread parent)
		{
			this.parent = parent;
		}
		
		public final void setSocket(final Socket socket)
		{
			this.socket = socket;
		}
		
		public final void setUserID(final String userID)
		{
			this.userID = userID;
		}
		
		public final void setWindow(final ApplicationWindow window)
		{
			this.window = window;
		}
	}
	
	/*
		This object encapsulates the necessary networking functionality for a simple socket-based TCP/IP client application.
	*/
	public static abstract class SimpleClientThread extends Thread
	{
		// Built-in Variables & Objects
		private boolean				connected	= false;
		private String				remoteHost	= null;
		private int					remotePort	= 0;
		private Socket				socket		= null;
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
				if (this.getSocket() != null)
				{
					this.getSocket().close();
				}
			}
			catch (final Exception e)
			{
				Support.displayException(this.getWindow(), e, false);
			}
			finally
			{
				this.setSocket(null);
				this.setConnected(false);
			}
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
		
		public final Socket getSocket()
		{
			return this.socket;
		}

		public final ApplicationWindow getWindow()
		{
			return this.window;
		}
		
		// Returns whether or not a connection is open.
		public final boolean isConnected()
		{
			return this.connected;
		}
		
		// Opens the connection and allocates needed I/O resources.
		public void open()
		{
			try
			{
				this.setSocket(new Socket(this.getRemoteHost(), this.getRemotePort()));
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
		public final void setConnected(final boolean connected)
		{
			this.connected = connected;
		}
		
		// Sets the remote host.
		public final void setRemoteHost(final String remoteHost)
		{
			this.remoteHost = remoteHost;
		}
		
		// Sets the remote TCP port.
		public final void setRemotePort(final int remotePort)
		{
			this.remotePort = remotePort;
		}
		
		public final void setSocket(final Socket socket)
		{
			this.socket = socket;
		}
		
		public final void setWindow(final ApplicationWindow window)
		{
			this.window = window;
		}
	}
	
	/*
		This interface specifies the methods necessary to implement a simple socket-based TCP client/server application protocol.
	*/
	public static interface SimpleProtocol
	{
		public String processInput(final String input);
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
		private ServerSocket					listeningSocket	= null;
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
				if (this.getListeningSocket() != null)
				{
					this.getListeningSocket().close();
				}
			}
			catch (final Exception e)
			{
				Support.displayException(this.getWindow(), e, false);
			}
			finally
			{
				this.setListeningSocket(null);
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
		
		public final ServerSocket getListeningSocket()
		{
			return this.listeningSocket;
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
				this.setListeningSocket(new ServerSocket(this.getListeningPort()));
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
		
		public final void setClientList(final List<SimpleChildServerThread> clientList)
		{
			this.clientList = clientList;
		}
		
		// Sets whether or not the server is listening for new connections.
		public final void setListening(final boolean listening)
		{
			this.listening = listening;
		}
		
		// Sets the TCP port which the server is listening on for new connections.
		public final void setListeningPort(final int listeningPort)
		{
			this.listeningPort = listeningPort;
		}
		
		public final void setListeningSocket(final ServerSocket listeningSocket)
		{
			this.listeningSocket = listeningSocket;
		}
		
		public final void setWindow(final ApplicationWindow window)
		{
			this.window = window;
		}
	}
}