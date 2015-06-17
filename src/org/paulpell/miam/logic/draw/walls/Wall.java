package org.paulpell.miam.logic.draw.walls;

import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.Vector;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.draw.Drawable;
import org.paulpell.miam.logic.draw.snakes.Snake;

public class Wall extends Drawable
{

	protected final int width_;
	protected final int height_;
	
	protected Vector<WallElement> elements_ = new Vector<WallElement>();
	
	public Wall(int width, int height)
	{
		if (width != Constants.DEFAULT_IMAGE_WIDTH
				|| height != Constants.DEFAULT_IMAGE_HEIGHT)
			throw new UnsupportedOperationException("TODO: different sized wall");
		
		this.width_ = width;
		this.height_ = height;
		
			
	}
	
	public void pushElement(WallElement el)
	{
		elements_.add(0,el);
	}
	
	public WallElement popElement()
	{
		if (elements_.size() > 0)
			return elements_.remove(0);
		return null;
	}
	
	public Vector<WallElement> getElements()
	{
		return elements_;
	}
	
	public void draw(Graphics2D g)
	{
		Iterator<WallElement> it = elements_.iterator();
		for (; it.hasNext();)
			it.next().draw(g);
	}

	
	public final int getWidth()
	{
		return width_;
	}
	
	public final int getHeight()
	{
		return height_;
	}
	
	public Pointd isSnakeColliding(Snake s)
	{
		Iterator<WallElement> it = elements_.iterator();
		for (;it.hasNext();)
		{
			WallElement we = it.next();
			Pointd collide = we.isSnakeColliding(s);
			if (collide != null)
				return collide;
		}
		return null;
	}
	
}
