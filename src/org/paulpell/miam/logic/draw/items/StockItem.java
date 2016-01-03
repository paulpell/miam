package org.paulpell.miam.logic.draw.items;

import org.paulpell.miam.logic.Game;
import org.paulpell.miam.logic.draw.snakes.Snake;


/*
 * This class represents any item a player can use after he collected it.
 * For instance, it could be some mariokart-style banana =) 
 */

public abstract class StockItem extends Item
{

	// When the snake uses its special item.
	// return true if the object is consumed (not usable again)
	public abstract boolean activate(Snake s, Game g);
	

	public boolean effectStep(Snake s){ return true;} // no lasting effect, normally
	public void startEffect(Snake s) {}
	
	final public Item.ItemType getType ()
	{
		return Item.ItemType.STOCK;
	}
}
