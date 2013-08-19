package logic.draw.items;

import logic.Game;
import logic.draw.snakes.Snake;

/*
 * This class represents any item a player can use after he collected it.
 * For instance, it could be some mariokart-style banana =) 
 */

public abstract class SpecialItem extends Item {

	//Game game; // we often need to place items or such things
	
	// When the snake uses its special item.
	// return true if the object is consumed (not usable again)
	public abstract boolean activate(Snake s);
	

	//public abstract Object clone(Game g);
	/*final public Object clone() {
		throw new UnsupportedOperationException("Impossible to clone a special item without specifying a game");
	}*/
	
	public boolean effectStep(Snake s){ return true;} // no during effect, normally
	public void startEffect(Snake s) {}
}
