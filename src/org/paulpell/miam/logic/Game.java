package org.paulpell.miam.logic;


import java.awt.Dimension;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.draw.Drawable;
import org.paulpell.miam.logic.draw.items.Item;
import org.paulpell.miam.logic.draw.snakes.ClassicSnake;
import org.paulpell.miam.logic.draw.snakes.Snake;
import org.paulpell.miam.logic.draw.walls.DefaultWall;
import org.paulpell.miam.logic.draw.walls.Wall;
import org.paulpell.miam.net.Client;



public class Game
{

	
	private final Control control_;
	
	
	// here we store all the game objects
	protected Vector<Snake> snakes_;
	private int aliveSnakes_;
	protected Vector<Item> items_;
	protected Wall wall_;


	public Game(Control gc, GameSettings settings)
	{
		this(gc, settings, new Vector<Item>(), new DefaultWall());
	}

	public Game(Control gc, GameSettings settings, Wall wall)
	{
		this(gc, settings, new Vector<Item>(), wall);
	}
	
	public Game(Control gc, GameSettings settings, Vector<Item> items)
	{
		this(gc, settings, items, new DefaultWall());
	}
	
	public Game(Control gc, GameSettings settings, Vector<Item> init_items, Wall init_wall)
	{
		// TODO control.configure(this); or configure(someGameConfiguration);
		control_ = gc;
		
		
		////////////////////
		/////// add the stuff to draw
		
		items_ = init_items;
		
		// snakes 
		snakes_ = new Vector<Snake>();
		Snake s = null;
		aliveSnakes_ = settings.numberOfSnakes_;
		for (int id=0; id<aliveSnakes_; ++id) {
			int x = 30 + 30 * id, y = 200 - 40 * id;
			if (settings.classicMode_)
				s = new ClassicSnake(id, settings, x, y, 2);
			else
				s = new Snake(id, settings, x, y, 0);
			snakes_.add(s);
		}
		
		wall_ = init_wall;
		
		
		Globals.currentGame = this;
	}
	
	
	/* *************************************
	 * Helper functions
	 */
	public int getNumberOfSnakes() {
		return snakes_.size();
	}
	
	public Vector<Snake> getSnakes() {
		return snakes_;
	}
	
	public Vector<Item> getItems() {
		return items_;
	}
	
	
	public Snake getSnake(int index)
	{
		if (index < snakes_.size() && index > -1)
			return snakes_.get(index);
		return null;
	}
	
	public void addItem(Item item)
	{
		if (item != null)
			items_.add(item);
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(wall_.getWidth(), wall_.getHeight());
	}
	
	public Iterator<Drawable> getDrawablesIterator()
	{
		Vector<Drawable> drawables = new Vector<Drawable>();
		
		drawables.add(wall_);
		drawables.addAll(snakes_);
		drawables.addAll(items_);
		
		return drawables.iterator();
	}
	
	public void kill(Snake s, Pointd collision)
	{
		s.kill(collision);
		--aliveSnakes_;
	}
	

	/**************** The main function for a game, where snakes are moved */
	
	public void update()
	{	
		
		// we check this here, so the final step can be drawn (paint is called one last time after previous update())
		if (aliveSnakes_ == 0)
			control_.endGame();
		
		// advance the snakes
		for (Enumeration<Snake> e = snakes_.elements(); e.hasMoreElements();)
		{
			Snake s = e.nextElement();
			s.advance();
		}
		
		Pointd collision;
		// check for collisions..
		for (Enumeration<Snake> e = snakes_.elements(); e.hasMoreElements();)
		{
			Snake s = e.nextElement();
			// if the snake is dead, we don't care
			if (s.isAlive())
			{
				
				// .. against the wall
				collision = wall_.isSnakeColliding(s);
				if (collision != null)
					control_.snakeDied(s, collision);
				
				// .. and against the other snakes
				for  (Enumeration<Snake> e2 = snakes_.elements(); e2.hasMoreElements(); )
				{
					Snake s2 = e2.nextElement();
					collision = s2.isSnakeColliding(s);
					if (collision != null)
						control_.snakeDied(s, collision);
					
				}
				
			}
			// .. check the items
			if (s.isAlive())
			{ // s might have died in between
				for (Enumeration<Item> its = items_.elements(); its.hasMoreElements(); )
				{
					Item item = its.nextElement();
					collision = item.isSnakeColliding(s);
					if (collision != null)
						control_.snakeAcceptedItem(s, item);
				}
			}
		}
	}
}