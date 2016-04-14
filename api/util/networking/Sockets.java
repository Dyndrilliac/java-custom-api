/*
 * Title: Sockets Author: Matthew Boyette Date: 1/12/2014 This class is merely a collection of useful static methods that support code recycling.
 * Specifically, this class offers methods and classes which would be useful to networking applications that are implemented using sockets.
 */

package api.util.networking;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;

import api.gui.swing.ApplicationWindow;
import api.util.Support;

public final class Sockets
{
    /*
     * This object encapsulates the necessary networking functionality for a simple socket-based TCP/IP client application.
     */
    public static abstract class SimpleAbstractClientThread extends Thread
    {
        private BufferedReader    input      = null;
        private BufferedWriter    output     = null;
        private String            remoteHost = null;
        private int               remotePort = 0;
        private Socket            socket     = null;
        private ApplicationWindow window     = null;
        
        public SimpleAbstractClientThread(final ApplicationWindow window, final String remoteHost, final int remotePort)
        {
            super();
            this.setRemoteHost(remoteHost);
            this.setRemotePort(remotePort);
            this.setWindow(window);
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
    }
    
    /*
     * This object encapsulates part of the necessary networking functionality for a simple socket-based TCP/IP server application. Specifically, this
     * class represents the portion of the server which listens for new connection attempts. Every time a SimpleAbstractServer object accepts a new
     * client, it spawns a SimpleAbstractServerThread object to handle the client. This allows for concurrency and results in a more robust server.
     */
    public static abstract class SimpleAbstractServer implements Runnable
    {
        private List<SimpleAbstractServerThread> clients         = null;
        private int                              listeningPort   = -1;
        private ServerSocket                     listeningSocket = null;
        private Thread                           thread          = null;
        private ApplicationWindow                window          = null;
        
        public SimpleAbstractServer(final int listeningPort)
        {
            this.setClients(new LinkedList<SimpleAbstractServerThread>());
            this.setListeningPort(listeningPort);
            this.print(Color.MAGENTA, "[" + Support.getDateTimeStamp() + "] ", Color.BLUE, "Binding to port " + this.getListeningPort() + "...\n");
            
            try
            {
                this.setListeningSocket(new ServerSocket(this.getListeningPort()));
            }
            catch (final Exception e)
            {
                this.print(Color.MAGENTA, "[" + Support.getDateTimeStamp() + "] ", Color.RED, "Could not bind to port " + this.getListeningPort() +
                    ": " +
                    e.getMessage() +
                    "\n");
                Support.displayException(this.getWindow(), e, false);
            }
            
            this.print(Color.MAGENTA,
                "[" + Support.getDateTimeStamp() + "] ",
                Color.GREEN,
                "Server started: " + this.getListeningSocket().toString() + "\n");
            this.start();
        }
        
        protected abstract void addThread(final Socket socket);
        
        protected int findClient(final String identifier)
        {
            for (int i = 0; i < this.getClients().size(); i++)
            {
                if (identifier.equals(this.getClients().get(i).getIdentifier()))
                {
                    return i;
                }
            }
            
            return -1;
        }
        
        public final List<SimpleAbstractServerThread> getClients()
        {
            return this.clients;
        }
        
        public final int getListeningPort()
        {
            return this.listeningPort;
        }
        
        public final ServerSocket getListeningSocket()
        {
            return this.listeningSocket;
        }
        
        public final Thread getThread()
        {
            return this.thread;
        }
        
        public final ApplicationWindow getWindow()
        {
            return this.window;
        }
        
        public abstract void handle(final String identifier, final String input);
        
        public abstract void print(final Object... args);
        
        @SuppressWarnings("deprecation")
        public synchronized void remove(final String identifier)
        {
            int pos = this.findClient(identifier);
            
            if (pos >= 0)
            {
                this.print(Color.MAGENTA, "[" + Support.getDateTimeStamp() + "] ", Color.BLUE, "Removing client " + identifier +
                    " at " +
                    pos +
                    "...\n");
                SimpleAbstractServerThread toTerminate = this.getClients().remove(pos);
                
                try
                {
                    toTerminate.close();
                }
                catch (final Exception e)
                {
                    this.print(Color.MAGENTA, "[" + Support.getDateTimeStamp() + "] ", Color.RED, "Error removing client: " + e.getMessage() + "\n");
                    Support.displayException(this.getWindow(), e, false);
                }
                
                toTerminate.stop();
                toTerminate = null;
            }
        }
        
        @Override
        public void run()
        {
            while (this.getThread() != null)
            {
                try
                {
                    this.print(Color.MAGENTA, "[" + Support.getDateTimeStamp() + "] ", Color.BLUE, "Waiting for a client...\n");
                    this.addThread(this.getListeningSocket().accept());
                }
                catch (final Exception e)
                {
                    this.print(Color.MAGENTA, "[" + Support.getDateTimeStamp() + "] ", Color.RED, "Server accept error: " + e.getMessage() + "\n");
                    this.stop();
                    Support.displayException(this.getWindow(), e, false);
                }
            }
        }
        
        protected final void setClients(final List<SimpleAbstractServerThread> clients)
        {
            this.clients = clients;
        }
        
        protected final void setListeningPort(final int listeningPort)
        {
            this.listeningPort = listeningPort;
        }
        
        protected final void setListeningSocket(final ServerSocket listeningSocket)
        {
            this.listeningSocket = listeningSocket;
        }
        
        protected final void setThread(final Thread thread)
        {
            this.thread = thread;
        }
        
        protected final void setWindow(final ApplicationWindow window)
        {
            this.window = window;
        }
        
        public void start()
        {
            if (this.getThread() == null)
            {
                this.setThread(new Thread(this));
                this.getThread().start();
            }
        }
        
        @SuppressWarnings("deprecation")
        public void stop()
        {
            if (this.getThread() != null)
            {
                this.getThread().stop();
                this.setThread(null);
            }
        }
    }
    
    /*
     * This object encapsulates part of the necessary networking functionality for a simple socket-based TCP/IP server application. Specifically, this
     * class represents the portion of the server which services a connected user's requests. Every time a SimpleAbstractServer object accepts a new
     * client, it spawns a SimpleAbstractServerThread object to handle the client. This allows for concurrency and results in a more robust server.
     */
    public static abstract class SimpleAbstractServerThread extends Thread
    {
        private static final String  DEFAULT_ID = "-1@~";
        private String               identifier = SimpleAbstractServerThread.DEFAULT_ID;
        private DataInputStream      input      = null;
        private DataOutputStream     output     = null;
        private SimpleAbstractServer server     = null;
        private Socket               socket     = null;
        private ApplicationWindow    window     = null;
        
        public SimpleAbstractServerThread(final ApplicationWindow window, final SimpleAbstractServer server, final Socket socket)
        {
            super();
            this.setIdentifier(socket.getPort() + "@" + socket.getRemoteSocketAddress());
            this.setServer(server);
            this.setSocket(socket);
            this.setWindow(window);
        }
        
        @SuppressWarnings("deprecation")
        public void close()
        {
            try
            {
                if (this.getSocket() != null)
                {
                    this.getSocket().close();
                }
                
                if (this.getInput() != null)
                {
                    this.getInput().close();
                }
                
                if (this.getOutput() != null)
                {
                    this.getOutput().close();
                }
            }
            catch (final Exception e)
            {
                this.getServer().print(Color.MAGENTA,
                    "[" + Support.getDateTimeStamp() + "] ",
                    Color.RED,
                    "Error closing connection to client " + this.getIdentifier() + "\n");
                Support.displayException(this.getWindow(), e, false);
            }
            finally
            {
                this.setIdentifier(SimpleAbstractServerThread.DEFAULT_ID);
                this.setWindow(null);
                this.setServer(null);
                this.setSocket(null);
                this.setInput(null);
                this.setOutput(null);
                this.stop();
            }
        }
        
        public final String getIdentifier()
        {
            return this.identifier;
        }
        
        public final DataInputStream getInput()
        {
            return this.input;
        }
        
        public final DataOutputStream getOutput()
        {
            return this.output;
        }
        
        public final SimpleAbstractServer getServer()
        {
            return this.server;
        }
        
        public final Socket getSocket()
        {
            return this.socket;
        }
        
        public final ApplicationWindow getWindow()
        {
            return this.window;
        }
        
        public void open()
        {
            try
            {
                this.setInput(new DataInputStream(new BufferedInputStream(this.getSocket().getInputStream())));
                this.setOutput(new DataOutputStream(new BufferedOutputStream(this.getSocket().getOutputStream())));
            }
            catch (final Exception e)
            {
                this.getServer().print(Color.MAGENTA,
                    "[" + Support.getDateTimeStamp() + "] ",
                    Color.RED,
                    "Error opening connection to client " + this.getIdentifier() + "\n");
                this.close();
                Support.displayException(this.getWindow(), e, false);
            }
        }
        
        @Override
        public void run()
        {
            this.getServer().print(Color.MAGENTA,
                "[" + Support.getDateTimeStamp() + "] ",
                Color.GREEN,
                "Server thread " + this.getIdentifier() + " running...\n");
            while (this.getSocket() != null)
            {
                try
                {
                    this.server.handle(this.getIdentifier(), this.getInput().readUTF());
                }
                catch (final Exception e)
                {
                    this.getServer().print(Color.MAGENTA,
                        "[" + Support.getDateTimeStamp() + "] ",
                        Color.RED,
                        "Error reading " + this.getIdentifier() + ": " + e.getMessage() + "\n");
                    this.getServer().remove(this.getIdentifier());
                    this.close();
                    Support.displayException(this.getWindow(), e, false);
                }
            }
        }
        
        public void send(final String message)
        {
            try
            {
                this.getOutput().writeUTF(message);
                this.getOutput().flush();
            }
            catch (final Exception e)
            {
                this.getServer().print(Color.MAGENTA,
                    "[" + Support.getDateTimeStamp() + "] ",
                    Color.RED,
                    "Error sending " + this.getIdentifier() + ": " + e.getMessage() + "\n");
                this.getServer().remove(this.getIdentifier());
                this.close();
                Support.displayException(this.getWindow(), e, false);
            }
        }
        
        protected final void setIdentifier(final String identifier)
        {
            this.identifier = identifier;
        }
        
        protected final void setInput(final DataInputStream input)
        {
            this.input = input;
        }
        
        protected final void setOutput(final DataOutputStream output)
        {
            this.output = output;
        }
        
        protected final void setServer(final SimpleAbstractServer server)
        {
            this.server = server;
        }
        
        protected final void setSocket(final Socket socket)
        {
            this.socket = socket;
        }
        
        protected final void setWindow(final ApplicationWindow window)
        {
            this.window = window;
        }
    }
    
    public final static Socket connectSocketWithTimeout(final String remoteHost, final int remotePort, final int seconds) throws Exception
    {
        Socket socket = new Socket();
        InetAddress internetAddress = InetAddress.getByName(remoteHost);
        SocketAddress socketAddress = new InetSocketAddress(internetAddress, remotePort);
        socket.connect(socketAddress, (seconds * 1000));
        return socket;
    }
}