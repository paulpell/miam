package org.paulpell.miam.logic.draw.items;


import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.Control;
import org.paulpell.miam.logic.Game;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.Utils;
import org.paulpell.miam.logic.draw.snakes.Snake;


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
	
	final private Game game_;
	final private Control control_;
	final private boolean scoreItemsOnly_;
	
	// working_ controls whether TimerTasks are created 
	private boolean working_ = false;
	
	

	public ItemFactory(Control control, Game g)
	{
		this(control, g, true); // conservative: only score_ items
	}
	
	public ItemFactory(Control control, Game g, boolean scoreOnly)
	{
		control_ = control;
		scoreItemsOnly_ = scoreOnly;
		game_ = g;

		working_ = true;
		timer_ = new Timer();
		scheduleTimerTask();
	}
	
	private void scheduleTimerTask()
	{
		// first check if game is in pause
		if (!control_.isGameRunning())
		{
			synchronized (this)
			{
				while (!control_.isGameRunning())
				{
					try
					{
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		long whenItemAppears = (long)(Math.random() *
				(Globals.TIME_BETWEEN_ITEMS_MAX - Globals.TIME_BETWEEN_ITEMS_MIN) +
				Globals.TIME_BETWEEN_ITEMS_MIN);
		timer_.schedule(createTimerTask(), whenItemAppears);
	}
	
	private TimerTask createTimerTask()
	{
		if (!working_)
			return null;
		
		return new TimerTask()
		{
			public void run()
			{
				if (!working_)
					return;
				control_.addItem(createItem(game_.getSnakes(), game_.getItems()));
				// re-run the timer for the next item
				scheduleTimerTask();
			}
		};
	}
	
	// create an item, randomly a score or a special item 
	// TODO: when creating items, we don't want them to collide (maybe even to be close)
	public Item createItem(Vector<Snake> snakes, Vector<Item> items)
	{
		if (scoreItemsOnly_ || Utils.rand.nextDouble() <= Globals.SCORE_ITEM_PROBABILITY)
			return createScoreItem(snakes, items);

		return AllTheItems.getRandomItem(game_);
	}
	
	
	// let's admit colliding items
	public ScoreItem createScoreItem(Vector<Snake> snakes, Vector<Item> items)
	{
		do {
			
			ScoreItem item = new ScoreItem(
					Utils.rand.nextDouble() * (Constants.DEFAULT_IMAGE_WIDTH - ScoreItem.s_width),
					Utils.rand.nextDouble() * (Constants.DEFAULT_IMAGE_HEIGHT - ScoreItem.s_height)
					);
			
			// check whether the item is on a snake
			boolean colliding = false;
			for (Snake snake : snakes)
				if (snake.isShapeInside(item.getShape()))
					colliding = true;

			// check whether the item is on another item
			for (Item item2 : items)
				if (null != item2.getShape().intersectGeneric(item.getShape()))
					colliding = true;
			if (!colliding)
				return item;
			
		} while (true);
	}
	
	public void shutdown()
	{
		working_ = false;
		timer_.cancel();
	}
}