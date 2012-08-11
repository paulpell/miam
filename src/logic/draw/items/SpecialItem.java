package logic.draw.items;

import logic.Game;
import logic.draw.snakes.Snake;

/*
 * This class represents any item a player can use after he collected it.
 * For instance, it could be some mariokart-style banana =) 
 */

public abstract class SpecialItem extends Item {

	Game game; // we often need to place items or such things
	
	public abstract void activate(Snake s);
	

	// overrides
	public int getScore() {return 0;}
	public int getGrowth() {return 0;}
	public double getThickness() {return 0;}
	public boolean isPersistent() {return false;}
	public boolean isReversing() {return false;}

}
