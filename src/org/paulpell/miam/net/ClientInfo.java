package org.paulpell.miam.net;

import java.util.ArrayList;

import org.paulpell.miam.logic.Constants;

public class ClientInfo
{
	
	final private int clientId_;
	final private char letter_; // greek letter ;)
	final private String name_;
	
	final private ArrayList <PlayerInfo> playerInfos_;

	public ClientInfo(int clientId, String canonicalName)
	{
		clientId_ = clientId;
		letter_ = (char)(Constants.START_GREEK_ALPHABET + clientId);
		name_ = canonicalName;
		playerInfos_ = new ArrayList <PlayerInfo> ();
	}
	
	public int getClientId()
	{
		return clientId_;
	}
	
	public char getLetter()
	{
		return letter_;
	}
	
	public String getName()
	{
		return name_;
	}
	
	@Override
	public String toString()
	{
		return letter_ + " - " + name_;
	}

	public ArrayList <PlayerInfo> getPlayerInfos()
	{
		return playerInfos_;
	}
	
	public void addPlayerInfo(PlayerInfo pi)
	{
		playerInfos_.add(pi);
	}
}
