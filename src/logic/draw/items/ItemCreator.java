package logic.draw.items;


import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import logic.Constants;
import logic.Game;
import logic.Globals;
import logic.draw.snakes.Snake;

/** 
 * This class maintains a list of future items, which will be placed
 * later in the game. This is handled by a timer and an associated timerTask.
 * 
 * The class creates items on demand too, when its createItem() or createScoreItem()
 * functions are called.
 */

public class ItemCreator {
	final private TimerTask timerTask;
	final private Timer timer;
	
	final private Game game;

	//final private Vector<FutureItem> futureItems = new Vector<FutureItem>();
	final private boolean scoreItemsOnly;
	
	

	public ItemCreator(Game g) {
		this(g, true); // conservative: only score items
	}
	public ItemCreator(Game g, boolean scoreOnly) {
		scoreItemsOnly = scoreOnly;
		game = g;
		/*// create 3 items in the game immediately
		Vector<Snake> snakes = g.getSnakes();
		game.addItem(createScoreItem(snakes));
		game.addItem(createScoreItem(snakes));
		game.addItem(createScoreItem(snakes));*/
		
		// to create the initial creation of future items
		//addFutureItem();

		timer = new Timer();
		timerTask = createTimerTask();
		//timer.schedule(timerTask, futureItems.get(0).getTime());
		timer.schedule(timerTask, Globals.TIME_BETWEEN_ITEMS_MIN);
	}
	
	private TimerTask createTimerTask() {
		return new TimerTask() {
			public void run() {
				long whenItemAppears = (int)(Math.random() *
						(Globals.TIME_BETWEEN_ITEMS_MAX - Globals.TIME_BETWEEN_ITEMS_MIN) +
						Globals.TIME_BETWEEN_ITEMS_MIN);
				
				//game.addItem(futureItems.remove(0).getItem());
				// items never stop! TODO clever way of acceleration / other trick
				/*if (futureItems.size() == 0) {
					//ItemCreator.this.addFutureItem(Globals.SCORE_ITEMS_ONLY);
					ItemCreator.this.addFutureItem();
				}*/
				
				game.addItem(createItem(null));
				// re-run the timer for the next item
				timer.schedule(createTimerTask(), whenItemAppears);
			}
		};
	}
	
	/*
	// create an item, place in the futureItemsList
	//private void addFutureItem(boolean scoreOnly) {
	private void addFutureItem() {
		Item item;
		if (scoreItemsOnly) {
			item = createScoreItem(game.getSnakes());
		}
		else {
			item = createItem(game.getSnakes());
		}
		long whenItemAppears = (int)(Math.random() *
				(Globals.TIME_BETWEEN_ITEMS_MAX - Globals.TIME_BETWEEN_ITEMS_MIN) +
				Globals.TIME_BETWEEN_ITEMS_MIN);
		futureItems.add(new FutureItem(item, whenItemAppears));
	}
	*/
	
	// create an item, randomly a score or a special item 
	// TODO: when creating items, we don't want them to collide (maybe even to be close)
	public Item createItem(Vector<Snake> snakes) {//, Vector<Item> items) {
		snakes = game.getSnakes();
		double prob = Constants.rand.nextDouble();
		if (scoreItemsOnly || prob <= Globals.SCORE_ITEM_PROBABILITY) {
			return createScoreItem(snakes);
		}
		return AllTheItems.getRandomItem(game);
	}
	
	
	// let's admit colliding items
	public ScoreItem createScoreItem(Vector<Snake> snakes) {//, Vector<Item> items) {
		boolean inter;
		ScoreItem item;
		
		do {
			double x = Constants.rand.nextDouble() * (Constants.IMAGE_WIDTH - 10),
					y = Constants.rand.nextDouble() * (Constants.IMAGE_HEIGHT - 10);
			item = new ScoreItem(x,y);
			inter = false;
			// check whether the item is on a snake
			Iterator<Snake> itSnakes = snakes.iterator();
			for (;itSnakes.hasNext();) {
				Snake s = itSnakes.next();
				inter = s.isShapeInside(item.getShape());
				if (inter) {
					break; // we have to find a new place
				}
			}
		} while (inter);
		
		return item;
	}
}
/*
class FutureItem {
	private Item item;
	private long time;
	public FutureItem(Item i, long t) {
		item = i;
		time = t;
	}
	public Item getItem() {
		return item;
	}
	public long getTime() {
		return time;
	}
}*/