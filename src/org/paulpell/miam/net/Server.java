package org.paulpell.miam.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import org.paulpell.miam.logic.Control;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.Log;
import org.paulpell.miam.net.TimestampedMessage.MsgTypes;


/**
 * The Server class maintains a list of clients to which it is connected.
 * There is one master client, and zero or more slave clients.
 * The server has to forward the slave clients' messages to the master client and vice-versa.
 * The master client has id 0 and is a special case: the messages are not sent over sockets,
 * but by fuction calls.
 * 
 * @author paul
 *
 */

public class Server extends Thread
{
	

	public final static int MESSAGE_BUFFER_SIZE = 256;
	
	
	
	private final int port_ = Globals.ONLINE_PORT;
	private final ServerSocket serverSocket_;
	
	Control control_;
	
	Client masterClient_;
	
	// keeps the thread alive 
	private boolean listening_ = true;
	
	
	private int maxNumSlaveClients_; // how many clients can be handled
	private LinkedList<ServerWorker> serverWorkers_;
	int nextId_ = 1; // master client has id 0
	
	boolean ending_ = false;
	
	public Server(Control control, Client masterClient)
			throws IOException
	{
		super("Server");
		control_ = control;
		masterClient_ = masterClient;
		
		serverWorkers_ = new LinkedList<ServerWorker>();
		
		serverSocket_ = new ServerSocket(port_);
		
		maxNumSlaveClients_ = Globals.ONLINE_DEFAULT_CLIENT_MAX_NUMBER;
		
		start();
	}
	
	public void run()
	{
		if (Globals.NETWORK_DEBUG)
			Log.logMsg("Starting server, hosting game on port "+port_);
		
		while (listening_)
		{
			handleNewClient();
		}
	}
	
	private void handleNewClient()
	{
		try
		{
			Socket newSocket = serverSocket_.accept();
			
			if ( ! listening_
					|| serverWorkers_.size() >= maxNumSlaveClients_)
			{
				reject(new ServerWorker(newSocket, this));
				newSocket.close(); // mouhahaha
			}
			else
			{
				ServerWorker sw = new ServerWorker(newSocket, this); 
				giveNextId(sw);
				serverWorkers_.add(sw);
				
				String name = newSocket.getInetAddress().getCanonicalHostName();
				control_.clientJoined(new ClientInfo(sw.getClientId(), name));
			}
		} catch (IOException e)
		{
			if (!ending_)
			{
				Log.logErr("Server accept failed: "+e.getLocalizedMessage());
			}
		}
	}
	

	public void removeWorker(ServerWorker serverWorker)
	{
		if (Globals.NETWORK_DEBUG)
			Log.logMsg("Server removes ServerWorker(" + serverWorker.getClientId() + ")");
		
		serverWorkers_.remove(serverWorker);
	}
	
	public void end()
	{
		if (Globals.NETWORK_DEBUG)
			Log.logMsg("Server shutting down");
		
		ending_ = true;
		listening_ = false;
		for (ServerWorker sw: serverWorkers_)
		{
			if (sw != null)
			{
				try
				{
					sendServerCommand(sw, new TimestampedMessage(getTimestamp(), -1, MsgTypes.SERVER_STOPS, null));
				}
				catch (IOException e)
				{
					control_.networkFeedback("Could not send STOP to client: "+sw+": "+e.getMessage());
				}
				sw.end();
			}
		}
		
		serverWorkers_.clear();
		
		// then close server socket
		try
		{
			serverSocket_.close();
		}
		catch (IOException e)
		{
		}
	}
	
	public int getSlaveNumber()
	{
		return serverWorkers_.size();
	}
	
	public void setMaxClientNumber(int n)
	{
		this.maxNumSlaveClients_ = n - 1;
		while (serverWorkers_.size() > maxNumSlaveClients_)
		{
			ServerWorker sw = serverWorkers_.getLast();
			reject(sw);
			sw.end();
		}
	}
	
	protected void onReceive(TimestampedMessage message)
	{
		masterClient_.handleMessage(message);
	}
	
	public void broadcastToSlave(TimestampedMessage message)
	{
		if (Globals.NETWORK_DEBUG)
			Log.logMsg("Server.broadcastToSlaves: " + message);
		
		for (ServerWorker sw : serverWorkers_)
			sw.forwardMessage(message);
	}
	
	private void reject(ServerWorker sw)
	{
		try
		{
			if (sw != null)
				sendServerCommand(sw, new TimestampedMessage(getTimestamp(), -1, MsgTypes.CLIENT_REJECTED, null));
		}
		catch (IOException e)
		{
			Log.logErr("Could not send REJECT to client: "+sw+": "+e.getMessage());
		}
	}
	
	private void giveNextId(ServerWorker sw) throws IOException
	{
		int id = nextId_++;
		sw.setId(id);
		TimestampedMessage message = new TimestampedMessage(getTimestamp(), -1, MsgTypes.SET_ID, new byte[]{(byte)(0xFF&id)});
		sendServerCommand(sw, message);
	}
	
	private void sendServerCommand(ServerWorker sw, TimestampedMessage message) throws IOException
	{
		sw.sendMessage(message);
	}
	
	public int getTimestamp()
	{
		return 0;
	}
	
	/*public String getCanonicalName()
	{
		return serverSocket_.getInetAddress().getCanonicalHostName();
	}*/

}