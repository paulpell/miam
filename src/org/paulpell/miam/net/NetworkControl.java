package org.paulpell.miam.net;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Vector;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.Control;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.ONLINE_STATE;
import org.paulpell.miam.logic.draw.items.Item;
import org.paulpell.miam.logic.draw.snakes.Snake;
import org.paulpell.miam.logic.gameactions.SnakeAction;
import org.paulpell.miam.logic.levels.Level;
import org.paulpell.miam.net.TimestampedMessage.MsgTypes;

public class NetworkControl
{
	
	final Control  control_;

	ONLINE_STATE onlineState_;
	
	Server server_ = null;
	Client client_ = null; // can also be a MasterClient

	public NetworkControl(Control control)
	{
		control_ = control;
		onlineState_ = ONLINE_STATE.OFFLINE;
	}
	
	public String getNetStatusStr ()
	{
		String name = "";
		switch (onlineState_)
		{
		case CLIENT:
			name += "client";
			break;
		case OFFLINE:
			name += "local";
			break;
		case SERVER:
			name += "server";
			break;
		}
		return name;
	}
	
	public boolean isHosting()
	{
		return onlineState_ == ONLINE_STATE.SERVER;
	}
	public boolean isOffline()
	{
		return onlineState_ == ONLINE_STATE.OFFLINE;
	}
	public boolean isClient()
	{
		return onlineState_ == ONLINE_STATE.CLIENT;
	}
	
	
	//////////////
	// The following methods will have to be removed,
	// when we pass the networkControl itself to server and client
	
	// BEGIN to remove
	public void removeClient ( int cid )
	{
		server_.removeClient(cid);
	}
	
	public void broadcastToSlavesExcept (TimestampedMessage msg, int id_except)
	{
		server_.broadcastToSlavesExcept(msg, id_except);
	}
	
	public void setClient ( Client client )
	{
		
	}
	// END to remove
	
	
	public void startHosting()
	{
		try
		{
			MasterClient master = new MasterClient(control_);
			client_ = master;
			server_ = new Server(control_, master);
			master.setServer(server_);
			server_.setMaxClientNumber(Globals.ONLINE_DEFAULT_CLIENT_MAX_NUMBER);
			onlineState_ = ONLINE_STATE.SERVER;
		}
		catch (IOException e)
		{
			client_ = null;
			String msg = "Could not start server: "+e.getLocalizedMessage();
			control_.displayServerMessage(msg);
		}
	}

	
	public void stopServer()
	{
		stopClient();
		if (null != server_)
		{
			server_.end();
			server_ = null;
		}
		onlineState_ = ONLINE_STATE.OFFLINE;
	}
	
	public void sendChatMessage(String msg)
			throws IOException
	{
		client_.sendChatMessage(msg);
	}
	
	public void sendErrorMessage()
	{
		client_.sendErrorMessage();
	}
	
	public void sendAction (SnakeAction action)
	{
		client_.sendAction(action);
	}
	
	

	public void joinGame(InetAddress addr)
			throws IOException
	{
		client_ = new Client(control_);
		client_.connect(addr);
		client_.start();
		onlineState_ = ONLINE_STATE.CLIENT;
	}
	
	public void stopClient()
	{
		if (null != client_)
		{
			client_.end();
			client_ = null;
		}

		onlineState_ = ONLINE_STATE.OFFLINE;
	}
	
	public int getClientId()
	{
		return client_.getClientId();
	}
	
	
	// following methods are only valid when hosting.
	
	public void sendLevel(Level level)
			throws IOException, Exception
	{
		client_.sendLevel(level);
	}
	
	public void sendSnakeAcceptedItem(int snakeid, int itemid)
	{
		client_.sendSnakeAcceptedItem(snakeid, itemid);
	}

	public void sendSnakesWon(Vector <Snake> ss)
	{
		client_.sendSnakesWon(ss);
	}
	public void sendSnakeDeath(Snake s, Pointd collision)
	{
		client_.sendSnakeDeath(s, collision);
	}
	
	public void sendGameEnd()
	{
		client_.sendGameEnd();
	}
	
	public void sendStartCommand()
			throws IOException
	{
		client_.sendStartCommand();
	}
	
	public void sendItem(Item i)
	{
		client_.sendItem(i);
	}
	
	public void sendStep (String stepstr)
			throws IOException
	{
		client_.increaseTimestamp();
		client_.sendStepCommand(stepstr);
	}
	
	
	public boolean hasSlaveClients ()
	{
		return server_.getSlaveNumber() > 0;
	}
	
	
	public void sendClientsList (Collection<ClientInfo> clients)
	{
		assert null != client_ : "Null client!";
		byte[] listbs = ClientInfo.makeClientListNetBytes(clients);
		
		TimestampedMessage msg =
			new TimestampedMessage(-1, client_.getClientId(), MsgTypes.CLIENT_LIST, listbs);
		server_.broadcastToSlaves(msg);
	}
	
	public void sendPlayersList(Vector<PlayerInfo> players, Vector<Integer> unusedIds)
	{
		client_.sendPlayersList(players, unusedIds);
	}
	
	public void sendAddPlayerRequest (PlayerInfo pi)
	{
		client_.sendAddPlayerRequest(pi);
	}
	
	public void sendDenyAddPlayerRequest(int cid, String reason)
	{
		assert null != client_ : "Null client!";
		TimestampedMessage tmsg =
				new TimestampedMessage(-1, client_.getClientId(), MsgTypes.ADD_PLAYER_DENY, reason.getBytes());
		server_.sendIndividualMessage(cid, tmsg);
	}

}
