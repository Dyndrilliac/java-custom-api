/*
	Title:  Networking
	Author: Matthew Boyette
	Date:   1/12/2014

	This class is merely a collection of useful static methods that support code recycling. Specifically, this 
	class offers methods and classes which would be useful to networking applications such as clients and servers. 
*/

package api.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
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
		protected BufferedReader input       = null;
		private   boolean        isConnected = false;
		protected PrintWriter    output      = null;
		private   String         remoteHost  = null;
		private   int            remotePort  = 0;
		protected Socket         socket      = null;
		
		// Creates a new instance of a SimpleClientThread object.
		public SimpleClientThread(final String remoteHost, final int remotePort)
		{
			super("SimpleClientThread");
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
				if (this.output != null) this.output.close();
				if (this.input != null) this.input.close();
			}
			catch (final IOException e)
			{
				System.err.println("I/O exception encountered while closing connection to server: " + e.toString());
				e.printStackTrace();
			}
			finally
			{
				this.socket = null;
				this.output = null;
				this.input = null;
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
				this.output = new PrintWriter(this.socket.getOutputStream(), true);
				this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
				this.setConnected(true);
			}
			catch (final UnknownHostException e)
			{
				this.close();
				System.err.println("Unknown host exception encountered while opening connection to server: " + e.toString());
				e.printStackTrace();
			}
			catch (final ConnectException e)
			{
				this.close();
				System.err.println("Connect exception encountered while opening connection to server: " + e.toString());
				e.printStackTrace();
			}
			catch (final IOException e)
			{
				this.close();
				System.err.println("I/O exception encountered while opening connection to server: " + e.toString());
				e.printStackTrace();
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
		
		// Creates a new instance of a SimpleServerThread object.
		public SimpleServerThread(final int listeningPort)
		{
			super("SimpleServerThread");
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
			catch (final IOException e)
			{
				System.err.println("I/O exception encountered while closing the listening socket: " + e.toString());
				e.printStackTrace();
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
			catch (final IOException e)
			{
				this.close();
				System.err.println("I/O exception encountered while opening the listening socket: " + e.toString());
				e.printStackTrace();
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
		protected BufferedReader     input       = null;
		private   boolean            isConnected = false;
		protected PrintWriter        output      = null;
		protected SimpleServerThread parent      = null;
		protected Socket             socket      = null;
		private   String             userID      = null;
		
		// Creates a new instance of a SimpleChildServerThread object.
		public SimpleChildServerThread(final SimpleServerThread parent, final Socket socket)
		{
			super("SimpleChildServerThread");
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
				if (this.output != null) this.output.close();
				if (this.input != null) this.input.close();
			}
			catch (final IOException e)
			{
				System.err.println("I/O exception encountered while closing connection to client: " + e.toString());
				e.printStackTrace();
			}
			finally
			{
				this.socket = null;
				this.output = null;
				this.input = null;
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
				this.output = new PrintWriter(this.socket.getOutputStream(), true);
				this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
				this.userID = this.socket.getRemoteSocketAddress() + ":" + this.socket.getPort();
				this.setConnected(true);
			}
			catch (final IOException e)
			{
				this.close();
				System.err.println("I/O exception encountered while opening connection to client: " + e.toString());
				e.printStackTrace();
			}
		}
		
		// Empty stub. This method is defined by a sub-class.
		public abstract void run();
		
		// Sets whether or not a connection is open.
		public void setConnected(final boolean isConnected)
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
	
	// This code was taken almost verbatim from the official Java Sockets tutorial and adapted to a more modular format using the classes above.
	public static class KnockKnockJokeExample
	{
		public static class KnockKnockClientThread extends SimpleClientThread
		{
			public KnockKnockClientThread(final String remoteHost, final int remotePort)
			{
				super(remoteHost, remotePort);
			}

			public void run()
			{
				try
				{
					BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
					String fromServer = null;
					String fromUser   = null;

					while ((fromServer = this.input.readLine()) != null)
					{
						System.out.println("Server: " + fromServer);

						if (fromServer.equals("Bye."))
						{
							break;
						}

						fromUser = stdIn.readLine();

						if (fromUser != null)
						{
							System.out.println("Client: " + fromUser);
							this.output.println(fromUser);
						}
					}
				}
				catch (final SocketException e)
				{
					System.err.println("Socket exception encountered while connected to server: " + e.toString());
					e.printStackTrace();
				}
				catch (final IOException e)
				{
					System.err.println("I/O exception encountered while connected to server: " + e.toString());
					e.printStackTrace();
				}
				finally
				{
					this.close();
				}
			}
		}
		
		public static class KnockKnockServerThread extends SimpleServerThread
		{
			public KnockKnockServerThread(final int listeningPort)
			{
				super(listeningPort);
			}

			public void run()
			{
				while ((this.isListening()) && (this.listeningSocket != null))
				{
					try
					{
						Networking.SimpleChildServerThread newThread = new KnockKnockChildServerThread(this, this.listeningSocket.accept());
						newThread.start();
						this.getClientList().add(newThread);
					}
					catch (final IOException e)
					{
						System.err.println("I/O exception encountered while listening for clients: " + e.toString());
						e.printStackTrace();
					}
				}

				this.close();
			}
		}

		public static class KnockKnockChildServerThread extends SimpleChildServerThread
		{
			public KnockKnockChildServerThread(final KnockKnockServerThread parent, final Socket socket)
			{
				super(parent, socket);
			}

			public void run()
			{
				try
				{
					Networking.SimpleProtocol protocol = new KnockKnockProtocol();
					String outputLine = protocol.processInput(null);
					String inputLine  = null;

					this.output.println(outputLine);

					while ((inputLine = this.input.readLine()) != null)
					{
						outputLine = protocol.processInput(inputLine);
						this.output.println(outputLine);

						if (outputLine.equals("Bye"))
						{
							break;
						}
					}
				}
				catch (final SocketException e)
				{
					System.err.println("Socket exception encountered while connected to client: " + e.toString());
					e.printStackTrace();
				}
				catch (final IOException e)
				{
					System.err.println("I/O exception encountered while connected to client: " + e.toString());
					e.printStackTrace();
				}
				finally
				{
					this.close();
				}
			}
		}

		public static class KnockKnockProtocol implements SimpleProtocol
		{
			private static final int WAITING = 0;
			private static final int SENTKNOCKKNOCK = 1;
			private static final int SENTCLUE = 2;
			private static final int ANOTHER = 3;

			private static final int NUMJOKES = 5;

			private int state = WAITING;
			private int currentJoke = 0;

			private String[] clues = { "Turnip", "Little Old Lady", "Atch", "Who", "Who" };
			private String[] answers = { "Turnip the heat, it's cold in here!",
				"I didn't know you could yodel!",
				"Bless you!",
				"Is there an owl in here?",
				"Is there an echo in here?" };

			public String processInput(final String theInput)
			{
				String theOutput = null;

				if (this.state == WAITING)
				{
					theOutput = "Knock! Knock!";
					this.state = SENTKNOCKKNOCK;
				}
				else if (this.state == SENTKNOCKKNOCK)
				{
					if (theInput.equalsIgnoreCase("Who's there?"))
					{
						theOutput = this.clues[this.currentJoke];
						this.state = SENTCLUE;
					}
					else
					{
						theOutput = "You're supposed to say \"Who's there?\"! " + "Try again. Knock! Knock!";
					}
				}
				else if (this.state == SENTCLUE)
				{
					if (theInput.equalsIgnoreCase(this.clues[this.currentJoke] + " who?"))
					{
						theOutput = answers[this.currentJoke] + " Want another? (y/n)";
						this.state = ANOTHER;
					}
					else
					{
						theOutput = "You're supposed to say \"" + 
							this.clues[this.currentJoke] + 
							" who?\"" + 
							"! Try again. Knock! Knock!";
						this.state = SENTKNOCKKNOCK;
					}
				}
				else if (this.state == ANOTHER)
				{
					if (theInput.equalsIgnoreCase("y"))
					{
						theOutput = "Knock! Knock!";

						if (this.currentJoke == (NUMJOKES - 1))
						{
							this.currentJoke = 0;
						}
						else
						{
							this.currentJoke++;
						}

						this.state = SENTKNOCKKNOCK;
					}
					else
					{
						theOutput = "Bye.";
						this.state = WAITING;
					}
				}

				return theOutput;
			}
		}
	}
}