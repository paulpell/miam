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
import org.paulpell.miam.logic.draw.walls.Wall;
import org.paulpell.miam.logic.levels.Level;
import org.paulpell.miam.logic.levels.VictoryCondition;
import org.paulpell.miam.logic.levels.Level.GameModesEnum;



public class Game
{

	
	private final Control control_;
	
	protected Level level_;
	
	
	// here we store all the game objects
	protected Vector<Snake> snakes_;
	private int aliveSnakes_;
	protected Vector<Item> items_;
	
	public Game(Control gc, Level level)
	{
		control_ = gc;
		
		setLevel(level);
	}
	
	public Level getLevel()
	{
		return level_;
	}
	
	public void setLevel(Level level)
	{
		level_ = level;
		GameSettings settings = level_.getGameSettings();
		
		items_ = level_.getInitialItems();
		
		// snakes 
		snakes_ = new Vector<Snake>();
		aliveSnakes_ = level_.getNumberSnakes();
		GameModesEnum mode = level_.getActualGameMode();
		
		for (int id=0; id<aliveSnakes_; ++id)
		{
			Pointd startPos = level_.getSnakeStartPosition(id);
			int x = (int)startPos.x_;
			int y = (int)startPos.y_;
			int a = level_.getSnakeStartAngle(id);

			Snake s = null;
			if (mode == GameModesEnum.CLASSIC)
			{
				int a2 = Arith.classicDirFromDegrees(a);
				s = new ClassicSnake(id, settings, x, y, a2);
			}
			else if (mode == GameModesEnum.MODERN)
			{
				s = new Snake(id, settings, x, y, a);
			}
			else
				throw new IllegalArgumentException("Unacceptable game mode!");
			
			snakes_.add(s);
		}
	}
	
	
	/* *************************************
	 * Helper functions
	 */
	public int getNumberOfSnakes()
	{
		return snakes_.size();
	}
	
	public Vector<Snake> getSnakes()
	{
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
		Wall w = level_.getWall();
		return new Dimension(w.getWidth(), w.getHeight());
	}
	
	public Iterator<Drawable> getDrawablesIterator()
	{
		Vector<Drawable> drawables = new Vector<Drawable>();
		
		drawables.add(level_.getWall());
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
			s.advance(this);
		}
		
		final Wall wall = level_.getWall();
		Pointd collision;
		Vector <Snake> victoriousSnakes = new Vector <Snake> ();
		// check for collisions..
		for (Enumeration<Snake> e = snakes_.elements(); e.hasMoreElements();)
		{
			Snake s = e.nextElement();
			// if the snake is dead, we don't care
			if (s.isAlive())
			{
				
				// .. against the wall
				collision = wall.isSnakeColliding(s);
				if (collision != null)
					control_.snakeDied(s, collision);
				
				else
				{
					// .. and against the other snakes
					for  (Enumeration<Snake> e2 = snakes_.elements(); e2.hasMoreElements(); )
					{
						Snake s2 = e2.nextElement();
						collision = s2.isSnakeColliding(s);
						if (collision != null)
						{
							control_.snakeDied(s, collision);
							break;
						}
					}
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
			
			// check victory:
			// this way, a dead snake can win, ie.
			// a sacrifice is worth it
			Vector <VictoryCondition> vcs = level_.getVictoryConditions(); 
			boolean snakeWon = vcs.size() > 0;
			for (VictoryCondition vc : vcs)
				snakeWon &= vc.checkVictory(s);
			if (snakeWon)
				victoriousSnakes.add(s);
		}
		
		if (victoriousSnakes.size() > 0)
			control_.snakesWon(victoriousSnakes);
			
	}
}