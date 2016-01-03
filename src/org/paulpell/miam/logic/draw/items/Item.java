package org.paulpell.miam.logic.draw.items;

import javax.swing.ImageIcon;

import org.paulpell.miam.geom.Rectangle;
import org.paulpell.miam.geom.Segment;
import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.draw.Drawable;
import org.paulpell.miam.logic.draw.snakes.Snake;


/**
 * Extending this class forces an implementation to provide the following methods:
 * newItem() -- equivalent of clone for the ItemFactory
 * effectStep() -- here we can decide when to cancel for instance
 * startEffect()
 * 
 */

public abstract class Item extends Drawable
{
	public static enum ItemType
	{
		SIMPLE, // when directly applied on the snake
		STOCK,  // snakes can store one item of this type
		GLOBAL, // items that have effect on the game in general
	}
	
	protected Rectangle shape_;
	protected int effectDuration_; // for lasting effects
	
	
	public Rectangle getShape()
	{
		return shape_;
	}
	public void moveToPoint(double x, double y)
	{
		double w = shape_.getWidth();
		double h = shape_.getHeight();
		shape_ = new Rectangle(x, y, w, h);
	}
	
	// needed to display the items in the info panel
	public abstract ImageIcon getImageIcon();
	public abstract boolean shouldDisplayInPanelInfo();
	
	final public Pointd isSnakeColliding(Snake s)
	{
		if (s == null || shape_ == null)
			return null;
		
		Segment l = s.getIntersectionTestLine();
		return shape_.intersect(l);
	}
	
	final public Pointd getPosition()
	{
		return shape_.getP1();
	}
	
	final public int getEffectDuration()
	{
		return effectDuration_;
	}
	
	public ItemType getType ()
	{
		return ItemType.SIMPLE; // by default
	}

	// used by slave game to create fake items
	public abstract Item newItem(double x, double y);
	
	// one step of duration, returns true when it's finished
	public abstract boolean effectStep(Snake s);
	public abstract void startEffect(Snake s);
	
	// these methods will be used to send/receive network data
	public abstract String getExtraParamsDescription();
	public abstract void applyExtraParamsDescription(String params);
	
	public abstract String getTextDescription();
	
	
	
}