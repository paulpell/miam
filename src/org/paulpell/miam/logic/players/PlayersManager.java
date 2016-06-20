package org.paulpell.miam.logic.players;

import java.util.HashMap;
import java.util.Vector;


import org.paulpell.miam.gui.GlobalColorTable;
import org.paulpell.miam.gui.net.OnlinePlayersPanel;
import org.paulpell.miam.logic.Log;
import org.paulpell.miam.net.NetworkControl;

public class PlayersManager {
	
	private class PlayerAddRequest
	{
		boolean answered_ = false;
		boolean accepted_ = false;
		String denialReason_ = "";
	}
	
	private static final String[] DEFAULT_NAMES = {
			"Chilipepper",
			"Marteau",
	};
	private static int nextDefaultName = 0;
	
	int currentPlayerSeats_;
	
	private Vector<PlayerInfo> playerInfos_;
	private HashMap<Integer, Boolean> areSnakesLocal_;
	
	NetworkControl netControl_;
	
	PlayerAddRequest pendingPlayerAddRequest_ = null;
	//boolean isAddRequestPending_ = false;
	
	// GUI
	OnlinePlayersPanel onlinePlayersPanel_;

	public PlayersManager(NetworkControl netControl, int currentPlayerSeats, OnlinePlayersPanel onlinePlayersPanel)
	{
		netControl_ = netControl;
		onlinePlayersPanel_ = onlinePlayersPanel;
		currentPlayerSeats_ = currentPlayerSeats;
		playerInfos_ = new Vector<PlayerInfo>();
		areSnakesLocal_ = new HashMap<Integer, Boolean>();
	}
	
	public void setRemotePlayersInfo(Vector<PlayerInfo> playerInfos, int numSeats)
	{
		handlePendingPlayerAddRequest(true, "");
		playerInfos_ = playerInfos;
		setNumberSeats(numSeats);
		for (PlayerInfo pi : playerInfos_) {
			boolean isLocal = pi.clientId_ == netControl_.getClientId();
			areSnakesLocal_.put(pi.snakeId_, isLocal);
		}
	}
	
	public int getNumberSnakes()
	{
		return playerInfos_.size();
	}
	
	public int getNumberSeats()
	{
		return currentPlayerSeats_;
	}
	
	public void setNumberSeats(int numSeats)
	{
		currentPlayerSeats_ = numSeats;
		if (numSeats < playerInfos_.size()) {
			for (int i=numSeats; i<playerInfos_.size(); ++i)
				playerInfos_.remove(i);
		}
		if (numSeats == playerInfos_.size()) {
			// if above condition true, this also
			onlinePlayersPanel_.setMaxPlayerNumReached(true);
		}
		if (netControl_.isHosting())
			netControl_.sendPlayersInfo(playerInfos_, getUnusedColors(), currentPlayerSeats_);
	}

	public Vector<PlayerInfo> getPlayerList()
	{
		return playerInfos_;
	}
	
	public static String getDefaultPlayerName()
	{
		String name = DEFAULT_NAMES[nextDefaultName++];
		if (nextDefaultName == DEFAULT_NAMES.length)
			nextDefaultName = 0;
		return name;
	}


	public void removeByCliendIt(int cid)
	{
		synchronized (playerInfos_) {
			Vector<PlayerInfo> pis = new Vector<PlayerInfo>(playerInfos_);
			for (PlayerInfo pi : pis)
				if ( pi.getClientId() == cid )
					playerInfos_.remove(pi);	
		}	
	}
	
	public boolean tryUpdatePlayer(int iSnake, String name, int snakeId)
	{
		PlayerInfo pi = netControl_.makePlayerInfo(name, snakeId);
		for (int i=0; i<playerInfos_.size(); ++i) {
			if ( i != iSnake ) {
				if (playerInfos_.get(i).equalsOnline(pi))
					return false;
			}
		}
		playerInfos_.set(iSnake, pi);
		netControl_.sendPlayersInfo(playerInfos_, getUnusedColors(), currentPlayerSeats_);
		return true;
	}
	
	private void handlePendingPlayerAddRequest(boolean accepted, String denialReason)
	{
		synchronized (this) {
			if (pendingPlayerAddRequest_ != null) {
				pendingPlayerAddRequest_.answered_ = true;
				pendingPlayerAddRequest_.accepted_ = accepted;
				pendingPlayerAddRequest_.denialReason_ = denialReason;
				notify();
			}
		}
	}
	
	public void playerAddRequestDenied(String reason)
	{
		handlePendingPlayerAddRequest(false, reason);
	}
	
	// send a request and wait for the answer
	private boolean tryAddPlayerAsClient(PlayerInfo pi)
	{
		synchronized (this) {
			
			netControl_.sendAddPlayerRequest(pi);
			pendingPlayerAddRequest_ = new PlayerAddRequest();
			
			try {
				wait(1000);
				if ( ! pendingPlayerAddRequest_.answered_) {
					Log.logErr("Player add request timeout");
					return false;
				}
				boolean accepted = pendingPlayerAddRequest_.accepted_;
				if ( ! accepted )
					onlinePlayersPanel_.displayMessage(pendingPlayerAddRequest_.denialReason_, false);
				pendingPlayerAddRequest_ = null;
				return accepted;
				
			} catch (InterruptedException e) {
				Log.logException(e);
				return false;
			}
		}
	}
	
	private boolean tryAddPlayerAsServer(PlayerInfo pi)
	{
		if (playerInfos_.size() >= currentPlayerSeats_)
			return false;
		
		if (pi.snakeId_ == -1) {
			Vector<Integer> unused = getUnusedColors();
			assert unused.size() > 0;
			pi.snakeId_ = unused.get(0);
		}
		
		String msg = checkAddPlayer(pi);
		if (msg != null) {
			onlinePlayersPanel_.displayMessage(msg, false);
			return false;
		}
		addPlayer(pi);
		return true;
	}


	// return whether the player is added
	public boolean tryAddPlayer(String name, int snakeId)
	{
		PlayerInfo pi = netControl_.makePlayerInfo(name, snakeId);
		if (netControl_.isHosting())
			return tryAddPlayerAsServer(pi);
		else
			return tryAddPlayerAsClient(pi);
	}

	public Vector<Integer> getUnusedColors()
	{
		Vector<Integer> unused = new Vector<Integer>();
		int max = GlobalColorTable.getMaxSnakeColor();
		for (int i=0; i<max; ++i) {
			boolean used = false;
			for (PlayerInfo pi: playerInfos_) {
				if (pi.clientId_ == i) {
					used = true;
					continue;
				}
			}
			if ( ! used )
				unused.add(i);
		}
		return unused;
	}
	
	// return null if we can add the player, otherwise
	// a message explaining why not
	@SuppressWarnings("incomplete-switch")
	public String checkAddPlayer(PlayerInfo pi)
	{
		for ( PlayerInfo pi2 : playerInfos_ )
		{
			switch (pi.equalsOnlineDetail(pi2))
			{
			case NAME_EQUAL: return "That name is already used";
			case SNAKE_ID_EQUAL: return "That snake color ("+pi.snakeId_+") is already used";
			}
		}
		return null;
	}
	
	public void addPlayer(PlayerInfo pi)
	{
		playerInfos_.add(pi);
		boolean isLocal = pi.clientId_ == netControl_.getClientId();
		areSnakesLocal_.put(pi.snakeId_, isLocal);
		if ( playerInfos_ .size() == currentPlayerSeats_ )
			onlinePlayersPanel_.setMaxPlayerNumReached(true);
		//onlinePlayersPanel_.setPlayers(playerInfos_, getUnusedColors());
		onlinePlayersPanel_.repaint();
		netControl_.sendPlayersInfo(playerInfos_, getUnusedColors(), currentPlayerSeats_);
	}
	
	public void reset()
	{
		playerInfos_.removeAllElements();
	}

	public boolean isSnakeHere(int sid)
	{
		return areSnakesLocal_.containsKey(sid)
			&& areSnakesLocal_.get(sid);
	}
	
}

