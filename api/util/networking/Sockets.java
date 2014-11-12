/*
	Title:  Sockets
	Author: Matthew Boyette
	Date:   1/12/2014
	
	This class is merely a collection of useful static methods that support code recycling. Specifically, this
	class offers methods and classes which would be useful to networking applications that are implemented using
	sockets.
*/

package api.util.networking;

import api.gui.ApplicationWindow;
import api.util.Support;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;

public final class Sockets
{
	/*
		This object encapsulates part of the necessary networking functionality for a simple socket-based TCP/IP server application.
		
		Specifically, this class represents the portion of the server which services a user's requests. Every time a SimpleServerThread object
		accepts a new client connection, it spawns a SimpleChildServerThread object to handle that client. This allows for concurrency and a
		more robust server.
	*/
	public static abstract class SimpleChildServerThread extends Thread
	{
		private boolean				isConnected	= false;
		private BufferedReader		input		= null;
		private BufferedWriter		output		= null;
		private SimpleServerThread	parent		= null;
		private Socket				socket		= null;
		private String				userID		= null;
		private ApplicationWindow	window		= null;
		
		public SimpleChildServerThread(final ApplicationWindow window, final SimpleServerThread parent, final Socket socket)
		{
			super("SimpleChildServerThread");
			this.setParent(parent);
			this.setSocket(socket);
			this.setWindow(window);
			this.open();
		}
		
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
				this.setOutput(null);
				this.setInput(null);
				this.setSocket(null);
				this.setUserID(null);
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
		
		public final Socket getSocket()
		{
			return this.socket;
		}
		
		public final String getUserID()
		{
			return this.userID;
		}
		
		public final ApplicationWindow getWindow()
		{
			return this.window;
		}
		
		public final boolean isConnected()
		{
			return this.isConnected;
		}
		
		public void open()
		{
			try
			{
				this.setInput(new BufferedReader(new InputStreamReader(this.getSocket().getInputStream())));
				this.setOutput(new BufferedWriter(new OutputStreamWriter(this.getSocket().getOutputStream())));
				this.setUserID(this.getSocket().getRemoteSocketAddress().toString().substring(1));
				this.setConnected(true);
			}
			catch (final Exception e)
			{
				this.close();
				Support.displayException(this.getWindow(), e, true);
			}
		}
		
		public final String readFromSocket() throws Exception
		{
			try
			{
				StringBuilder sb = new StringBuilder();
				String str;
				
				while ((str = this.getInput().readLine()) != null)
				{
					sb.append(str + "\n");
				}
				
				this.getInput().close();
				return sb.toString();
			}
			catch (final Exception e) 
			{
				Support.displayException(this.getWindow(), e, false);
			}
			
			return "";
		}
		
		@Override
		public abstract void run();
		
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
		
		protected final void setSocket(final Socket socket)
		{
			this.socket = socket;
		}
		
		protected final void setUserID(final String userID)
		{
			this.userID = userID;
		}
		
		protected final void setWindow(final ApplicationWindow window)
		{
			this.window = window;
		}
		
		public final void writeToSocket(final String s) throws Exception
		{
			try
			{
				this.getOutput().write(s);
				this.getOutput().flush();
			}
			catch (final Exception e) 
			{
				Support.displayException(this.getWindow(), e, false);
			}
		}
	}
	
	/*
		This object encapsulates the necessary networking functionality for a simple socket-based TCP/IP client application.
	*/
	public static abstract class SimpleClientThread extends Thread
	{
		private boolean				isConnected	= false;
		private BufferedReader		input		= null;
		private BufferedWriter		output		= null;
		private String				remoteHost	= null;
		private int					remotePort	= 0;
		private Socket				socket		= null;
		private ApplicationWindow	window		= null;
		
		public SimpleClientThread(final ApplicationWindow window, final String remoteHost, final int remotePort)
		{
			super("SimpleClientThread");
			this.setRemoteHost(remoteHost);
			this.setRemotePort(remotePort);
			this.setWindow(window);
			this.open();
		}
		
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
				this.setOutput(null);
				this.setInput(null);
				this.setSocket(null);
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
		
		public final String getRemoteHost()
		{
			return this.remoteHost;
		}
		
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
		
		public final boolean isConnected()
		{
			return this.isConnected;
		}
		
		public void open()
		{
			try
			{
				this.setSocket(Sockets.connectSocketWithTimeout(this.getRemoteHost(), this.getRemotePort(), 10));
				this.setInput(new BufferedReader(new InputStreamReader(this.getSocket().getInputStream())));
				this.setOutput(new BufferedWriter(new OutputStreamWriter(this.getSocket().getOutputStream())));
				this.setConnected(true);
			}
			catch (final Exception e)
			{
				this.close();
				Support.displayException(this.getWindow(), e, true);
			}
		}
		
		public final String readFromSocket() throws Exception
		{
			try
			{
				StringBuilder sb = new StringBuilder();
				String str;
				
				while ((str = this.getInput().readLine()) != null)
				{
					sb.append(str + "\n");
				}
				
				this.getInput().close();
				return sb.toString();
			}
			catch (final Exception e) 
			{
				Support.displayException(this.getWindow(), e, false);
			}
			
			return "";
		}
		
		@Override
		public abstract void run();
		
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
		
		protected final void setRemoteHost(final String remoteHost)
		{
			this.remoteHost = remoteHost;
		}
		
		protected final void setRemotePort(final int remotePort)
		{
			this.remotePort = remotePort;
		}
		
		protected final void setSocket(final Socket socket)
		{
			this.socket = socket;
		}
		
		protected final void setWindow(final ApplicationWindow window)
		{
			this.window = window;
		}
		
		public final void writeToSocket(final String s) throws Exception
		{
			try
			{
				this.getOutput().write(s);
				this.getOutput().flush();
			}
			catch (final Exception e) 
			{
				Support.displayException(this.getWindow(), e, false);
			}
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
		private List<SimpleChildServerThread>	clientList		= null;
		private boolean							isListening		= false;
		private int								listeningPort	= 0;
		private ServerSocket					listeningSocket	= null;
		private ApplicationWindow				window			= null;
		
		public SimpleServerThread(final ApplicationWindow window, final int listeningPort)
		{
			super("SimpleServerThread");
			this.setClientList(new LinkedList<SimpleChildServerThread>());
			this.setListeningPort(listeningPort);
			this.setWindow(window);
			this.open();
		}
		
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
		
		public final List<SimpleChildServerThread> getClientList()
		{
			return this.clientList;
		}
		
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
		
		public final boolean isListening()
		{
			return this.isListening;
		}
		
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
		
		@Override
		public abstract void run();
		
		protected final void setClientList(final List<SimpleChildServerThread> clientList)
		{
			this.clientList = clientList;
		}
		
		protected final void setListening(final boolean listening)
		{
			this.isListening = listening;
		}
		
		protected final void setListeningPort(final int listeningPort)
		{
			this.listeningPort = listeningPort;
		}
		
		protected final void setListeningSocket(final ServerSocket listeningSocket)
		{
			this.listeningSocket = listeningSocket;
		}
		
		protected final void setWindow(final ApplicationWindow window)
		{
			this.window = window;
		}
	}
	
	public final static Socket connectSocketWithTimeout(final String remoteHost, final int remotePort, final int seconds) throws Exception
	{
		Socket socket = new Socket();
		
		InetAddress		internetAddress	= InetAddress.getByName(remoteHost);
		SocketAddress	socketAddress	= new InetSocketAddress(internetAddress, remotePort);
		
		socket.connect(socketAddress, (seconds*1000));
		return socket;
	}
}