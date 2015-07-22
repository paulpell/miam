package org.paulpell.miam.logic.draw.items;

import javax.swing.ImageIcon;

import org.paulpell.miam.geom.Rectangle;
import org.paulpell.miam.geom.Segment;
import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.Game;
import org.paulpell.miam.logic.draw.Drawable;
import org.paulpell.miam.logic.draw.snakes.Snake;


/**
 * Extending this class forces an implementation to provide the following methods:
 * clone() -- for the ItemFactory
 * effectStep() -- here we can decide when to cancel for instance
 * startEffect()
 * 
 */

public abstract class Item extends Drawable
{
	
	
	protected Rectangle shape_;
	protected Pointd position_;
	protected int effectDuration_; // for lasting effects
	
	
	public Rectangle getShape()
	{
		return shape_;
	}
	
	// needed to display the items in the info panel
	public abstract ImageIcon getImageIcon();
	public abstract boolean shouldDisplayInPanelInfo();
	
	public Pointd isSnakeColliding(Snake s)
	{
		if (s == null || shape_ == null)
			return null;
		
		Segment l = s.getIntersectionTestLine();
		return shape_.intersect(l);
	}
	
	
	/*public boolean isPointInside(Pointd p)
	{
		if (p == null || shape_ == null)
			return false;
		
		return shape_.isPointInside(p);
	}*/
	
	
	public Pointd getPosition()
	{
		return position_;
	}
	
	public int getEffectDuration()
	{
		return effectDuration_;
	}
	
	
	public abstract Object clone(Game g);

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