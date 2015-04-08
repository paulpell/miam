package org.paulpell.miam.net;

import java.io.IOException;
import java.net.Socket;

import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.Log;

public class ServerWorker extends Thread{
	
	private Server server_;
	private Socket socket_;
	private boolean listening_ = true;
	
	private int id_; // client id
	
	public ServerWorker(Socket s, Server server)
	{
		super("ServerWorker");
		this.socket_ = s;
		this.server_ = server;
		start();
	}

	public void setId(int id)
	{
		id_ = id;
		setName("ServerWorker - " + id_);
	}
	
	public int getClientId()
	{
		return id_;
	}
	
	public void run ()
	{
		Log.logMsg("ServerWorker ("+id_+") starting");
		
		while (listening_)
		{
			try
			{
				TimestampedMessage message = NetMethods.receiveMessage(socket_);
				if (Globals.NETWORK_DEBUG)
					Log.logMsg("ServerWorker (" + id_ + ")  receives msg = " + message);
				server_.onReceive(message, this);
			}
			catch (IOException e)
			{
				Log.logErr("ServerWorker ("+id_+") could not receive message: " + e.getLocalizedMessage());
				Log.logException(e);
				end();
			}
		}
	}
	
	public void end()
	{
		if (Globals.NETWORK_DEBUG)
			Log.logMsg("ServerWorked (" + id_ + ") ending");
		
		listening_ = false;
		server_.removeWorker(this);
		try
		{
			socket_.close();
			socket_ = null;
		} catch (IOException e)
		{
			Log.logErr("Could not close socket_...:"+e.getMessage());
		}
	}
	
	void sendMessage(TimestampedMessage message)
	{
		try
		{
			NetMethods.sendMessage(socket_, message);
		} catch (IOException e)
		{
			Log.logErr("ServerWorker (" +id_ + ") cannot send: " + e.getMessage());
			
		}
	}
	

	void forwardMessage(TimestampedMessage message)
	{
		if (Globals.NETWORK_DEBUG)
			Log.logMsg("ServerWorker (" +id_ + ") forwards: " + message);
			
		if (socket_ == null || socket_.isClosed())
		{
			if (Globals.NETWORK_DEBUG)
				Log.logMsg("ServerWorker (" +id_ + ") cannot forward, socket_ invalid");
			return;
		}
		

		try
		{
			NetMethods.sendMessage(socket_, message);
		} catch (IOException e)
		{
			Log.logErr("ServerWorker (" +id_ + ") cannot forward: " + e.getMessage());
			
		}
	}
	
}
