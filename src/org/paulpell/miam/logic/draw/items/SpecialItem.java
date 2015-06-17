package org.paulpell.miam.logic.draw.items;

import org.paulpell.miam.logic.draw.snakes.Snake;


/*
 * This class represents any item a player can use after he collected it.
 * For instance, it could be some mariokart-style banana =) 
 */

public abstract class SpecialItem extends Item
{

	//Game game; // we often need to place items or such things
	
	// When the snake uses its special item.
	// return true if the object is consumed (not usable again)
	public abstract boolean activate(Snake s);
	

	public boolean effectStep(Snake s){ return true;} // no lasting effect, normally
	public void startEffect(Snake s) {}
}
