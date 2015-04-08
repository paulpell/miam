package org.paulpell.miam.logic;


import java.util.Enumeration;
import java.util.Vector;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.draw.items.Item;
import org.paulpell.miam.logic.draw.snakes.Snake;
import org.paulpell.miam.logic.draw.walls.DefaultWall;
import org.paulpell.miam.logic.draw.walls.Wall;
import org.paulpell.miam.net.Client;



public class RemoteGame extends Game
{

	public RemoteGame(Control gc, GameSettings settings, Vector<Item> init_items, Wall init_wall, Vector<Snake> snakes, Client client)
	{
		super(gc, settings, init_items, init_wall);
		// TODO
		snakes_ = snakes;
	}
	
	public RemoteGame(Control gc, GameSettings settings, Client client)
	{
		super(gc, settings, null, null);
	}
	
	public void setItems (Vector<Item> is)
	{
		items_ = is;
	}
	
	public void setSnakes(Vector<Snake> ss)
	{
		snakes_ = ss;
	}
	
	public void setWall(Wall w)
	{
		wall_ = w;
	}
	
	public void update()
	{
		// advance the snakes
		for (Enumeration<Snake> e = snakes_.elements(); e.hasMoreElements();)
		{
			Snake s = e.nextElement();
			s.advance();
		}
	}

}
