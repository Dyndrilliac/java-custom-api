/*
	Title:  Networking
	Author: Matthew Boyette
	Date:   1/12/2014

	This class is merely a collection of useful static methods that support code recycling. Specifically, this 
	class offers methods and classes which would be useful to networking applications such as clients and servers.
*/

package api.util;

import api.gui.ApplicationWindow;
import api.gui.RichTextPane;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Networking
{
	/*
		This object encapsulates the necessary networking functionality for a simple socket-based TCP/IP client application.
	*/
	public static abstract class SimpleClientThread extends Thread
	{
		// Built-in Variables & Objects
		private   boolean           isConnected = false;
		private   String            remoteHost  = null;
		private   int               remotePort  = 0;
		protected Socket            socket      = null;
		protected ApplicationWindow window      = null;
		
		// Creates a new instance of a SimpleClientThread object.
		public SimpleClientThread(final ApplicationWindow window, final String remoteHost, final int remotePort)
		{
			super("SimpleClientThread");
			this.window = window;
			this.setRemoteHost(remoteHost);
			this.setRemotePort(remotePort);
			this.open();
		}
		
		// Closes the connection and frees up I/O resources.
		public void close()
		{
			try
			{
				if (this.socket != null) this.socket.close();
			}
			catch (final Exception e)
			{
				Support.displayException(this.window, e, false);
			}
			finally
			{
				this.socket = null;
				this.setConnected(false);
			}
		}
		
		// Returns the remote host.
		public String getRemoteHost()
		{
			return this.remoteHost;
		}
		
		// Returns the remote TCP port.
		public int getRemotePort()
		{
			return this.remotePort;
		}
		
		// Returns whether or not a connection is open.
		public boolean isConnected()
		{
			return this.isConnected;
		}
		
		// Opens the connection and allocates needed I/O resources.
		public void open()
		{
			try
			{
				this.socket = new Socket(this.remoteHost, this.remotePort);
				this.setConnected(true);
			}
			catch (final Exception e)
			{
				this.close();
				Support.displayException(this.window, e, true);
			}
		}
		
		// Empty stub. This method is defined by a sub-class.
		public abstract void run();
		
		// Sets whether or not a connection is open.
		protected void setConnected(final boolean isConnected)
		{
			this.isConnected = isConnected;
		}
		
		// Sets the remote host.
		protected void setRemoteHost(final String remoteHost)
		{
			this.remoteHost = remoteHost;
		}
		
		// Sets the remote TCP port.
		protected void setRemotePort(final int remotePort)
		{
			this.remotePort = remotePort;
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
		private   List<SimpleChildServerThread> clientList      = null;
		private   boolean                       isListening     = false;
		private   int                           listeningPort   = 0;
		protected ServerSocket                  listeningSocket = null;
		protected ApplicationWindow             window          = null;
		
		// Creates a new instance of a SimpleServerThread object.
		public SimpleServerThread(final ApplicationWindow window, final int listeningPort)
		{
			super("SimpleServerThread");
			this.window = window;
			this.clientList = new LinkedList<SimpleChildServerThread>();
			this.setListeningPort(listeningPort);
			this.open();
		}
		
		// Closes the socket and stops the listening process.
		public void close()
		{
			try
			{
				if (this.listeningSocket != null) this.listeningSocket.close();
			}
			catch (final Exception e)
			{
				Support.displayException(this.window, e, false);
			}
			finally
			{
				this.listeningSocket = null;
				this.setListening(false);
			}
		}
		
		// Returns the list of clients currently being handled by this server.
		public List<SimpleChildServerThread> getClientList()
		{
			return this.clientList;
		}
		
		// Returns the TCP port which the server is listening on for new connections.
		public int getListeningPort()
		{
			return this.listeningPort;
		}
		
		// Returns whether or not the server is listening for new connections.
		public boolean isListening()
		{
			return this.isListening;
		}
		
		// Opens the socket and begins the listening process.
		public void open()
		{
			try
			{
				this.listeningSocket = new ServerSocket(this.getListeningPort());
				this.setListening(true);
			}
			catch (final Exception e)
			{
				this.close();
				Support.displayException(this.window, e, true);
			}
		}
		
		// Empty stub. This method is defined by a sub-class.
		public abstract void run();
		
		// Sets the TCP port which the server is listening on for new connections.
		protected void setListeningPort(final int listeningPort)
		{
			this.listeningPort = listeningPort;
		}
		
		// Sets whether or not the server is listening for new connections.
		protected void setListening(final boolean isListening)
		{
			this.isListening = isListening;
		}
	}
	
	/*
		This object encapsulates part of the necessary networking functionality for a simple socket-based TCP/IP server application.
	
		Specifically, this class represents the portion of the server which services a user's requests. Every time a SimpleServerThread object
		accepts a new client connection, it spawns a SimpleChildServerThread object to handle that client. This allows for concurrency and a 
		more robust server.
	*/
	public static abstract class SimpleChildServerThread extends Thread
	{
		// Built-in Variables & Objects
		private   boolean            isConnected = false;
		protected SimpleServerThread parent      = null;
		protected Socket             socket      = null;
		private   String             userID      = null;
		protected ApplicationWindow  window      = null;
		
		// Creates a new instance of a SimpleChildServerThread object.
		public SimpleChildServerThread(final ApplicationWindow window, final SimpleServerThread parent, final Socket socket)
		{
			super("SimpleChildServerThread");
			this.window = window;
			this.parent = parent;
			this.socket = socket;
			this.open();
		}
		
		// Closes the connection and frees up I/O resources.
		public void close()
		{
			try
			{
				if (this.socket != null) this.socket.close();
			}
			catch (final Exception e)
			{
				Support.displayException(this.window, e, false);
			}
			finally
			{
				this.socket = null;
				this.userID = null;
				this.setConnected(false);
			}
		}
		
		// Returns the identifier for the user being handled by this server thread, which is a combination of the user's IP address and local TCP port.
		public String getUserID()
		{
			return this.userID;
		}
		
		// Returns whether or not a connection is open. 
		public boolean isConnected()
		{
			return this.isConnected;
		}
		
		// Opens the connection and allocates needed I/O resources.
		public void open()
		{
			try
			{
				this.userID = this.socket.getRemoteSocketAddress() + ":" + this.socket.getPort();
				this.setConnected(true);
			}
			catch (final Exception e)
			{
				this.close();
				Support.displayException(this.window, e, true);
			}
		}
		
		// Empty stub. This method is defined by a sub-class.
		public abstract void run();
		
		// Sets whether or not a connection is open.
		protected void setConnected(final boolean isConnected)
		{
			this.isConnected = isConnected;
		}
	}
	
	/*
		This interface specifies the methods necessary to implement a simple socket-based TCP client/server application protocol.
	*/
	public static interface SimpleProtocol
	{
		public String processInput(final String input);
	}
}