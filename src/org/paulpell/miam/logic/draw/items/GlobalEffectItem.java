package org.paulpell.miam.logic.draw.items;

import org.paulpell.miam.logic.Game;

public abstract class GlobalEffectItem extends Item
{

	abstract public void globalEffect (Game game); 
	
	final public Item.ItemType getType ()
	{
		return Item.ItemType.GLOBAL;
	}
}
