package org.paulpell.miam.net;

public class PlayerInfo
{

	String name_;
	int snakeId_;
	
	public PlayerInfo(String name, int snakeId)
	{
		name_ = name;
		snakeId_ = snakeId;
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

}
