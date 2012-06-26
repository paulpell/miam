package logic;

import java.util.Vector;

import logic.draw.Item;
import logic.draw.ScoreItem;
import logic.draw.Drawable;

public class ItemCreator {

	Vector<FutureItem> toPlace = new Vector<FutureItem>();
	boolean scoreItemsOnly = false;
	
	public ItemCreator() {}
	public ItemCreator(boolean scoreOnly) {
		scoreItemsOnly = scoreOnly;
	}
	
	public ScoreItem createScoreItem(Vector<Drawable> existing) {
		int x = (int)(Constants.rand.nextDouble() * Constants.IMAGE_WIDTH),
				y = (int)(Constants.rand.nextDouble() * Constants.IMAGE_WIDTH);
		
		return null;
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