package org.paulpell.miam.logic.players;

public class PlayerInfo
{
	public enum PlayerEq
	{
		NOT_EQUAL,
		NAME_EQUAL,
		SNAKE_ID_EQUAL	
	}

	String name_;
	int snakeId_; // this will be used for the color as well
	int clientId_;
	char clientLetter_; // the letter that corresponds to the client
	
	public PlayerInfo(String name, int snakeId, int clientId, char clientLetter)
	{
		name_ = name;
		snakeId_ = snakeId;
		clientId_ = clientId;
		clientLetter_ = clientLetter;
	}
	
	public String getName()
	{
		return name_;
	}
	
	public void setName(String name)
	{
		name_ = name;
	}
	
	public int getSnakeId()
	{
		return snakeId_;
	}
	
	public void setSnakeId ( int id )
	{
		snakeId_ = id;
	}
	
	public int getClientId()
	{
		return clientId_;
	}
	
	public char getClientLetter()
	{
		return clientLetter_;
	}
	
	// don't use clientid, since we test whether the two will clash on server
	public PlayerEq equalsOnlineDetail (PlayerInfo other)
	{
		if ( snakeId_ == other.snakeId_ )
			return PlayerEq.SNAKE_ID_EQUAL;
		
		if ( name_.equals(other.name_) )
			return PlayerEq.NAME_EQUAL;
		
		return PlayerEq.NOT_EQUAL;
	}
	
	public boolean equalsOnline(PlayerInfo other)
	{
		return equalsOnlineDetail(other) != PlayerEq.NOT_EQUAL;
	}
	
	public String toString()
	{
		return "Player(s" + snakeId_ + ":\"" + name_ +"\"@" + clientId_ + ")";
	}

}
