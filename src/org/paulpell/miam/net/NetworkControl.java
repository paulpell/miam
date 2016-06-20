package org.paulpell.miam.net;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.gui.net.OnlinePlayersPanel;
import org.paulpell.miam.gui.net.OnlineServersPanel;
import org.paulpell.miam.logic.Control;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.ONLINE_STATE;
import org.paulpell.miam.logic.draw.items.Item;
import org.paulpell.miam.logic.draw.snakes.Snake;
import org.paulpell.miam.logic.gameactions.SnakeAction;
import org.paulpell.miam.logic.levels.Level;
import org.paulpell.miam.logic.players.PlayerInfo;
import org.paulpell.miam.logic.players.PlayersManager;
import org.paulpell.miam.net.TimestampedMessage.MsgTypes;

public class NetworkControl
{
	
	final Control  control_;

	ONLINE_STATE onlineState_;
	
	Server server_ = null;
	Client client_ = null; // can also be a MasterClient
	PlayersManager playerMgr_;
	
	// connected clients
	private HashMap <Integer, ClientInfo> clientId2Infos_;
	
	// gui elements
	OnlinePlayersPanel onlinePlayersPanel_;
	OnlineServersPanel onlineServersPanel_;

	public NetworkControl(Control control, OnlinePlayersPanel onlinePlayersPanel,
			OnlineServersPanel onlineServersPanel)
	{
		control_ = control;
		onlineState_ = ONLINE_STATE.OFFLINE;
		onlinePlayersPanel_ = onlinePlayersPanel;
		onlinePlayersPanel_.setNetworkControl(this);
		onlineServersPanel_ = onlineServersPanel;
	}
	
	public void setPlayerManager(PlayersManager playerMgr)
	{
		playerMgr_ = playerMgr;
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
	public ONLINE_STATE onlineState()
	{
		return onlineState_;
	}
	

	public void resetConnectedInfo()
	{
		clientId2Infos_ = new HashMap <Integer, ClientInfo> ();
	}
	
	public PlayerInfo makePlayerInfo(String name, int snakeId)
	{
		int cid = client_.getClientId();
		ClientInfo info = clientId2Infos_.get(cid);
		char letter = null == info ? '?' : info.getLetter();
		return new PlayerInfo(name, snakeId, cid, letter);
	}
	
	public ClientInfo getClientFromId(int id)
	{
		return clientId2Infos_.get(id);
	}
	
	public void setRemoteClientInfos(HashMap <Integer, ClientInfo> clientId2Infos)
	{
		assert isClient() : "Only client should receive clients list";
		clientId2Infos_ = clientId2Infos;
		control_.updateConnectedInfoGUI();
	}
	
	public Collection<ClientInfo> clientInfos()
	{
		return clientId2Infos_.values();
	}

	private void updateConnectedInfo()
	{
		assert isHosting() : "Connected info update only on server";
		control_.updateConnectedInfoGUI();

		sendClientsList(clientId2Infos_.values());
		int numSeats = playerMgr_.getNumberSeats();
		Vector<Integer> unusedIds = playerMgr_.getUnusedColors();
		sendPlayersInfo(playerMgr_.getPlayerList(), unusedIds, numSeats);
	}

	
	public void clientJoined(ClientInfo info)
	{
		assert isHosting() : "clientJoined() only for server!";
		int cid = info.getClientId();
		clientId2Infos_.put(cid, info);
		if ( 0 != cid )
			control_.networkFeedback("New client accepted : " + info);
		updateConnectedInfo();
	}
	
	public void clientLeft(int cid, String msg)
	{
		assert isHosting() : "clientLeft() only for server!";
		goneClientHandling(cid, msg, false);
	}
	
	private void goneClientHandling(int cid, String msg, boolean isError)
	{
		ClientInfo ci = clientId2Infos_.remove(cid);
		
		assert null != ci : "null client to remove??";
		
		removeClient(cid);
		
		String s = ci + " " + (isError ? "error" : "left")
								+ ": " + msg;
		control_.networkFeedback(s);
		
		playerMgr_.removeByCliendIt(cid);

		updateConnectedInfo();
	}

	// this is announced by the server itself
	public void clientError(int cid, String reason)
	{
		assert isHosting() : "clientError() only for server!";
		goneClientHandling(cid, reason, true);
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
	
	/*public void setClient ( Client client )
	{
		
	}*/
	// END to remove
	
	
	public boolean startServer()
	{
		try
		{
			MasterClient master = new MasterClient(control_, this);
			client_ = master;
			server_ = new Server(control_, master, this);
			master.setServer(server_);
			server_.setMaxClientNumber(Globals.ONLINE_DEFAULT_CLIENT_MAX_NUMBER);
			onlineState_ = ONLINE_STATE.SERVER;
		}
		catch (IOException e)
		{
			client_ = null;
			String msg = "Could not start server: "+e.getLocalizedMessage();
			control_.networkFeedback(msg);
		}
		
		return client_ != null;
	}
	
	public void leaveServer()
	{
		if ( isClient() )
			stopClient();
		
		if (isHosting())
			stopServer(); // calls stopClient()
		
		control_.showNetworkPanel(null);
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
		client_ = new Client(control_, this);
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
	
	public void sendPlayersInfo(Vector<PlayerInfo> players, Vector<Integer> unusedIds, int numSeats)
	{
		client_.sendPlayersList(players, unusedIds, numSeats);
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
