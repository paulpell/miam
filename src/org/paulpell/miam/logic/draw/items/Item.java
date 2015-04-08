package org.paulpell.miam.logic.draw.items;

import javax.swing.ImageIcon;

import org.paulpell.miam.geom.GeometricObject;
import org.paulpell.miam.geom.Line;
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
	
	
	protected GeometricObject shape_;
	protected Pointd position_;
	protected int duration_; // for lasting effects
	
	
	public GeometricObject getShape()
	{
		return shape_;
	}
	
	// needed to display the items in the info panel
	public abstract ImageIcon getImageIcon();
	
	public Pointd isSnakeColliding(Snake s)
	{
		if (s == null || shape_ == null)
			return null;
		
		Line l = new Line(s.getHead(), s.getPreviousHead());
		return shape_.intersect(l);
	}
	
	
	public boolean isPointInside(Pointd p)
	{
		if (p == null || shape_ == null)
			return false;
		
		return shape_.isPointInside(p);
	}
	
	
	public Pointd getPointd()
	{
		return position_;
	}
	
	public int getDuration()
	{
		return duration_;
	}
	
	
	public abstract Object clone(Game g);

	// used by slave game to create fake items
	public abstract Item newItem(double x, double y, Game game);
	
	// one step of duration, returns true when it's finished
	public abstract boolean effectStep(Snake s);
	public abstract void startEffect(Snake s);
	
	// these methods will be used to send/receive network data
	public abstract String getExtraParamsDescription();
	public abstract void applyExtraParamsDescription(String params);
	
	public abstract String getTextDescription();
	
	
	
}