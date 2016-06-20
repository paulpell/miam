package org.paulpell.miam.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

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
	private final NetworkControl netControl_;
	
	Client masterClient_;
	
	// keeps the thread alive 
	private boolean listening_ = true;
	
	
	private int maxNumSlaveClients_; // how many clients can be handled
	private LinkedList<Integer> lastIds_;
	HashMap<Integer, ServerWorker> serverWorkers_;
	int nextId_ = 1; // master client has id 0
	
	boolean ending_ = false;
	
	public Server(Control control, Client masterClient, NetworkControl netControl)
			throws IOException
	{
		super("Server");
		control_ = control;
		masterClient_ = masterClient;
		netControl_ = netControl;
		
		serverWorkers_ = new HashMap<Integer, ServerWorker>();
		lastIds_ = new LinkedList<Integer>();
		
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
				int cid = giveNextId(sw);
				lastIds_.add(cid);
				serverWorkers_.put(cid, sw);
				
				String name = newSocket.getInetAddress().getCanonicalHostName();
				netControl_.clientJoined(new ClientInfo(sw.getClientId(), name));
			}
		} catch (IOException e)
		{
			if (!ending_)
			{
				Log.logErr("Server accept failed: "+e.getLocalizedMessage());
			}
		}
	}
	
	public void removeClient (int cid)
	{
		for ( ServerWorker sw : serverWorkers_.values() )
		{
			if ( cid == sw.getClientId() )
			{
				serverWorkers_.remove(cid);
				sw.end();
				lastIds_.remove((Integer)cid); // cast to remove(Object)
				break;
			}
		}
	}
	
	public void onWorkerError (ServerWorker sw, String msg)
	{
		int id = sw.getClientId();
		if (Globals.NETWORK_DEBUG)
		{
			Log.logMsg("Error ServerWorker(" + id + ")");
		}
		
		netControl_.clientError(id, msg);
	}
	
	public void end()
	{
		if (Globals.NETWORK_DEBUG)
			Log.logMsg("Server shutting down");
		
		ending_ = true;
		listening_ = false;
		for (ServerWorker sw: serverWorkers_.values())
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
		lastIds_.clear();
		
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
			int cid = lastIds_.getLast();
			ServerWorker sw = serverWorkers_.get(cid);
			reject(sw);
			sw.end();
		}
	}
	
	protected void onReceive(TimestampedMessage message)
	{
		masterClient_.handleMessage(message);
	}
	
	public void broadcastToSlaves(TimestampedMessage message)
	{
		if (Globals.NETWORK_DEBUG)
			Log.logMsg("Server.broadcastToSlaves: " + message);
		
		for (ServerWorker sw : serverWorkers_.values()) 
			sw.forwardMessage(message);
	}
	
	public void broadcastToSlavesExcept(TimestampedMessage message, int id_except)
	{
		if (Globals.NETWORK_DEBUG)
			Log.logMsg("Server.broadcastToSlaves: " + message);
		
		for (ServerWorker sw : serverWorkers_.values())
		{
			if ( id_except != sw.getClientId() )
				sw.forwardMessage(message);
		}
	}
	
	protected void sendIndividualMessage ( int clientId, TimestampedMessage tmsg)
	{
		if (clientId >= 1 && clientId <= serverWorkers_.size())
		{
			try
			{
				ServerWorker sw = serverWorkers_.get(clientId - 1);
				sendServerCommand (sw, tmsg);
			}
			catch (IOException e)
			{
				Log.logErr("Cannot send individual message: " + e.getMessage());
				Log.logException(e);
			}
		}
	}
	
	private void reject(ServerWorker sw)
	{
		try
		{
			if (sw != null) {
				int cid = sw.getClientId();
				lastIds_.remove((Integer)cid);
				serverWorkers_.remove(cid);
				sendServerCommand(sw, new TimestampedMessage(getTimestamp(), -1, MsgTypes.CLIENT_REJECTED, null));
			}
		}
		catch (IOException e)
		{
			Log.logErr("Could not send REJECT to client: "+sw+": "+e.getMessage());
		}
	}
	
	private int giveNextId(ServerWorker sw) throws IOException
	{
		int id = nextId_++;
		sw.setId(id);
		TimestampedMessage message = new TimestampedMessage(getTimestamp(), -1, MsgTypes.SET_ID, new byte[]{(byte)(0xFF&id)});
		sendServerCommand(sw, message);
		return id;
	}
	
	private void sendServerCommand(ServerWorker sw, TimestampedMessage message) throws IOException
	{
		sw.sendMessage(message);
	}
	
	public int getTimestamp()
	{
		return 0;
	}

}