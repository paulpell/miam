package logic.draw.items;


import java.util.Iterator;
import java.util.Vector;

import logic.Constants;
import logic.Globals;
import logic.draw.snakes.Snake;

public class ItemCreator {

	Vector<FutureItem> futureItems = new Vector<FutureItem>();
	boolean scoreItemsOnly = false;
	
	public ItemCreator() {}
	public ItemCreator(boolean scoreOnly) {
		scoreItemsOnly = scoreOnly;
	}
	
	public Item createItem(Vector<Snake> snakes) {//, Vector<Item> items) {
		double prob = Constants.rand.nextDouble();
		if (scoreItemsOnly || prob <= Globals.SCORE_ITEM_PROBABILITY) {
			return createScoreItem(snakes);
		}
		System.out.println("TODO: createItem, non scoreItem");
		return null;
	}
	
	// let's admit colliding items
	public ScoreItem createScoreItem(Vector<Snake> snakes) {//, Vector<Item> items) {
		boolean inter;
		ScoreItem item;
		
		do {
			double x = Constants.rand.nextDouble() * Constants.IMAGE_WIDTH,
					y = Constants.rand.nextDouble() * Constants.IMAGE_WIDTH;
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

class FutureItem {
	Item item;
	long time;
	public FutureItem(Item i, long t) {
		item = i;
		time = t;
	}
}