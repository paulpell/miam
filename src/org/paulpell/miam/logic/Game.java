package org.paulpell.miam.logic;


import java.awt.Dimension;
import java.util.Iterator;
import java.util.Vector;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.draw.Drawable;
import org.paulpell.miam.logic.draw.items.GlobalEffectItem;
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
	protected Vector<Snake> allSnakes_; // this vector keeps the original order
	protected Vector<Snake> aliveSnakes_;
	protected Vector<Snake> deadSnakes_;

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
		deadSnakes_ = new Vector<Snake>();
		aliveSnakes_ = new Vector<Snake>();

		GameModesEnum mode = level_.getActualGameMode();
		
		int numSnakes = level_.getNumberSnakes();
		for (int id=0; id<numSnakes; ++id)
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
			
			aliveSnakes_.add(s);
		}
		allSnakes_ = new Vector <Snake> (aliveSnakes_);
	}
	
	
	/* *************************************
	 * Helper functions
	 */

	public Vector<Snake> getAllSnakes()
	{
		return allSnakes_;
	}
	
	public Vector<Snake> getDeadSnakes()
	{
		return deadSnakes_;
	}
	
	
	public Vector<Item> getItems()
	{
		return items_;
	}
	
	
	public Snake getSnake(int index)
	{
		return allSnakes_.get(index);
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
		drawables.addAll(allSnakes_);
		drawables.addAll(items_);
		
		return drawables.iterator();
	}
	
	public void kill(Snake s, Pointd collision)
	{
		s.kill(collision);
		control_.snakeDied(s, collision);
	}
	
	public void resurrectDeadSnakes()
	{
		for ( Snake s : deadSnakes_ )
			s.resurrect();
		aliveSnakes_.addAll(deadSnakes_);
		deadSnakes_.clear();
	}
	
	private void snakeCollidesItem (int snakeIndex, int itemIndex)
	{
		control_.snakeEncounteredItem(snakeIndex, itemIndex);
		snakeTakesItem (snakeIndex, itemIndex);
	}
	
	public void snakeTakesItem (int snakeIndex, int itemIndex)
	{
		Item i = items_.remove(itemIndex);
		if ( Item.ItemType.GLOBAL == i.getType() )
			handleGlobalItem ((GlobalEffectItem)i);
		else
		{
			Snake s = allSnakes_.get(snakeIndex);
			s.acceptItem(i);
		}
	}
	
	public void handleGlobalItem ( GlobalEffectItem geItem )
	{
		geItem.globalEffect(this);
	}
	

	/**************** The main function for a game, where snakes are moved */
	
	// in this function, we need to remove snakes or items from their
	// vectors. We do it only after the loop, or ConcurrentBlahBlahException.
	public void update()
	{
		// we check this here, so the final step can be drawn (paint is called one last time after previous update())
		if ( 0 == aliveSnakes_.size() )
			control_.endGame();
		
		// advance the snakes
		advanceSnakes();
		
		Vector <Snake> victoriousSnakes = new Vector <Snake> ();
		Vector <Snake> copiedSnakes = new Vector <Snake> (aliveSnakes_);
		// check for collisions..
		for ( Snake s : copiedSnakes )
		{
			assert s.isAlive() : "";

			// first check whether the player made a fault
			if ( null != checkCollisions(s) )
			{
				aliveSnakes_.remove(s);
				deadSnakes_.add(s);
			}
			
			// A snake dying on an item also receives it
			checkItems (s);
			
			// Check victory only here.
			// This way, a dead snake can win, ie.
			// a sacrifice is worth it
			if (doesSnakeWin(s))
				victoriousSnakes.add(s);
		}
		
		// announce the winners
		if (victoriousSnakes.size() > 0)
			control_.snakesWon(victoriousSnakes);
		
		if ( Globals.SNAKE_DEBUG)
		{
			for (Snake s : deadSnakes_)
				if  (s.isAlive())
					Log.logErr("Alive snakes in dead list!");
			for (Snake s : aliveSnakes_) 
				if ( ! s.isAlive() )
					Log.logErr("Dead snakes in alive list!");
		}
	}
	
	protected final void advanceSnakes ()
	{
		for ( Snake s : aliveSnakes_ )
			s.advance(this);
	}
	
	private final Pointd checkCollisions (Snake s)
	{
		// .. against the wall
		final Wall wall = level_.getWall();
		Pointd collision = wall.isSnakeColliding(s);
		if (collision != null)
		{
			kill (s, collision);
			return collision;
		}
		// and the other snakes
		for  ( Snake s2 : allSnakes_ )
		{
			collision = s2.isSnakeColliding(s);
			if (collision != null)
			{
				kill (s, collision);
				return collision;
			}
		}
		return null;
	}
	
	private final void checkItems (Snake s)
	{
		int itemIndex = 0;
		Vector <Item> items2 = new Vector<Item> (items_);
		for (Iterator<Item> it = items2.iterator(); it.hasNext() ; )
		{
			Item item = it.next();
			if ( null != item.isSnakeColliding(s) )
				//snakeTakesItem(s.getId(), itemIndex);
				snakeCollidesItem(s.getId(), itemIndex);
			else
				 ++itemIndex;
		}
	}
	
	private final boolean doesSnakeWin (Snake s)
	{
		Vector <VictoryCondition> vcs = level_.getVictoryConditions(); 
		if ( 0 == vcs.size() )
			return false; // no condition ? Cannot win
		for (VictoryCondition vc : vcs)
			if ( ! vc.checkVictory(s) )
				return false; // must satisfy all
		// here all conditions are ok
		return true;
	}
}