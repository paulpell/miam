package org.paulpell.miam.logic.draw.items;


import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.paulpell.miam.geom.Rectangle;
import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.Control;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.Log;
import org.paulpell.miam.logic.Utils;
import org.paulpell.miam.logic.draw.snakes.Snake;
import org.paulpell.miam.logic.draw.walls.Wall;
import org.paulpell.miam.logic.draw.walls.WallElement;


/** 
 * This class maintains a list of future items, which will be placed
 * later in the game. This is handled by a timer and an associated timerTask.
 * 
 * The class creates items on demand too, when its createItem() or createScoreItem()
 * functions are called.
 */

public class ItemFactory
{

	final private Timer timer_;
	
	final private Control control_;
	final private boolean scoreItemsOnly_;
	
	// working_ controls whether TimerTasks are created 
	private boolean working_ = false;
	
	// used to pause the factory
	private boolean sleeping_ = false;
	

	
	public ItemFactory(Control control, boolean scoreOnly)
	{
		control_ = control;
		scoreItemsOnly_ = scoreOnly;

		working_ = true;
		timer_ = new Timer("item-factory");
		scheduleTimerTask();
	}
	
	public boolean isWorking()
	{
		return working_;
	}

	public void scheduleSleep()
	{
		synchronized (this)
		{
			sleeping_ = true;
		}
	}
	
	public void wakeup()
	{
		synchronized (this)
		{
			if ( sleeping_ )
			{
				sleeping_ = false;
				notify();
			}
		}
	}
	
	private void scheduleTimerTask()
	{
		// first check if game is in pause:
		// the factory should not run in pause =)
		
		long appearanceDelay = (long)(Math.random() *
				(Globals.TIME_BETWEEN_EXTRA_ITEMS_MAX - Globals.TIME_BETWEEN_EXTRA_ITEMS_MIN) +
				Globals.TIME_BETWEEN_EXTRA_ITEMS_MIN);
		TimerTask tt = createTimerTask(appearanceDelay);
		timer_.schedule(tt, appearanceDelay);
	}
	
	private TimerTask createTimerTask(final long appearanceDelay)
	{
		if (!working_)
			return null;
		
		return new TimerTask()
		{
			public void run()
			{
				if (!working_)
					return;
				
				long timeStart = System.currentTimeMillis();
				
				Item item = createItem();
				boolean waited = false;
				
				// if we should sleep, do it before adding an item
				synchronized (ItemFactory.this)
				{
					while ( sleeping_ )
					{
						try
						{
							ItemFactory.this.wait();
						} catch (InterruptedException e)
						{
							Log.logErr("Cannot pause!");
							Log.logException(e);
						}
						waited = true;
					}
				}
				
				if ( ! working_)
					return;

				// if we waited, give some more delay when game resumes..
				if (waited)
				{
					long dt = System.currentTimeMillis() - timeStart;
					long delay = dt > appearanceDelay ? appearanceDelay : dt;
					Utils.threadSleep(delay);
				}

				control_.addItem(item);
				// re-run the timer for the next item
				scheduleTimerTask();
			}
		};
	}
	
	// create an item, randomly a score or a special item 
	public Item createItem()
	{
		// create an initial point, maybe it does not collide with anything =)
		double x = Utils.rand.nextDouble() *
				(Constants.DEFAULT_IMAGE_WIDTH - ScoreItem.s_width);
		double y = Utils.rand.nextDouble() *
				(Constants.DEFAULT_IMAGE_HEIGHT - ScoreItem.s_height);
		
		Item testItem;
		
		if (scoreItemsOnly_ || Utils.rand.nextDouble() <= Globals.SCORE_ITEM_PROBABILITY)
			testItem = new ScoreItem(x ,y);
		else
			testItem = AllTheItems.getRandomItem(x, y);
		
		if ( moveToNoCollision(testItem) )
			return testItem;
		
		return null;
	}
	
	private boolean itemCollidesWithSnakes (Item item)
	{
		Vector <Snake> snakes = control_.getCurrentGame().getAllSnakes();
		for (Snake snake : snakes)
			if (null != item.isSnakeColliding(snake))
				return true;
		return false;
	}
	
	private boolean itemCollidesWithItems (Item item)
	{
		Rectangle shape = item.getShape();
		Vector <Item> items = control_.getCurrentGame().getItems();
		for (Item item2 : items)
			if (null != item2.getShape().intersect(shape))
				return true;
		return false;
	}
	
	private boolean itemCollidesWithWall (Item item)
	{
		Rectangle shape = item.getShape();
		Wall wall = control_.getCurrentGame().getLevel().getWall();
		for (WallElement we : wall.getElements())
			if (null != we.getGeometricObject().intersect(shape))
				return true;
		return false;
	}
	
	// return true if a place for the item was found
	private boolean moveToNoCollision(Item item)
	{
		int trials = 0;
		
		while ( ++trials < Constants.MAX_ITEM_MOVE_TRIAL )
		{
			if (    ! itemCollidesWithSnakes(item)
				 && ! itemCollidesWithWall(item)
				 && ! itemCollidesWithItems(item))
				return true;
		
			// or try another place
			double x = Utils.rand.nextDouble() *
					(Constants.DEFAULT_IMAGE_WIDTH - ScoreItem.s_width);
			double y = Utils.rand.nextDouble() *
					(Constants.DEFAULT_IMAGE_HEIGHT - ScoreItem.s_height);
			item.moveToPoint(x, y);
		}
		
		if (Globals.DEBUG)
			Log.logErr("Could not find a place for item: " + item);
		
		return false;
	}
	
	public void shutdown()
	{
		working_ = false;
		wakeup(); // do it anyways, the isSleeping check is inside the function
		timer_.cancel();
		if (Globals.DEBUG)
			Log.logMsg ( "factory shut down");
	}
}