package org.paulpell.miam.logic.draw.walls;


import java.awt.Graphics;
import java.util.Iterator;
import java.util.Vector;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.draw.Drawable;
import org.paulpell.miam.logic.draw.snakes.Snake;



public class Wall extends Drawable
{
	
	
	protected final int width_;
	protected final int height_;
	
	
	protected Vector<WallElement> elements = new Vector<WallElement>();
	
	public Wall(int width, int height)
	{
		this.width_ = width;
		this.height_ = height;
	}
	
	public void addElement(WallElement el) {
		elements.add(0,el);
	}
	
	public void removeLastElement() {
		if (elements.size() > 0)
			elements.remove(0);
	}
	
	public void draw(Graphics g)
	{
		Iterator<WallElement> it = elements.iterator();
		for (; it.hasNext();)
			it.next().draw(g);
	}

	public Pointd getPointd() {
		return new Pointd(0,0);
	}
	
	public int getWidth()
	{
		return width_;
	}
	
	public int getHeight()
	{
		return height_;
	}
	
	public boolean isPointInside(Pointd p)
	{
		return false;
	}
	
	public Pointd isSnakeColliding(Snake s)
	{
		Iterator<WallElement> it = elements.iterator();
		for (;it.hasNext();)
		{
			Pointd collide = it.next().isSnakeColliding(s);
			if (collide != null)
				return collide;
		}
		return null;
	}
}
