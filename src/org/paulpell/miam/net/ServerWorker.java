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
				server_.onReceive(message);
			}
			catch (IOException e)
			{
				if ( listening_ )
					onSocketError (e, "client exit");
			}
		}
	}
	
	private void onSocketError (Exception e, String msg)
	{
		Log.logErr("Error in ServerWorker ("+id_+") " + msg);
		Log.logException(e);
		server_.onWorkerError(this, msg);
	}
	
	public void end()
	{
		if (Globals.NETWORK_DEBUG)
			Log.logMsg("ServerWorked (" + id_ + ") ending");
		
		listening_ = false;
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
			onSocketError (e, "can not send");
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
			onSocketError (null, "invalid socket");
			return;
		}
		

		try
		{
			NetMethods.sendMessage(socket_, message);
		} catch (IOException e)
		{
			onSocketError (e, "cannot forward");
		}
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (other instanceof ServerWorker) {
			int id2 = ((ServerWorker)other).id_;
			return id_ == id2;
		}
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return id_;
	}
	
}
