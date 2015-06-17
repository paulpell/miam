package org.paulpell.miam.logic.draw.walls;

import java.awt.Color;
import java.awt.Graphics2D;

import org.paulpell.miam.geom.GeometricObject;
import org.paulpell.miam.geom.Segment;
import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.draw.snakes.Snake;



public class WallElement
{

	GeometricObject geomObject_;
	Color color_;
	
	public WallElement(GeometricObject obj, Color c)
	{
		geomObject_ = obj;
		color_ = c;
	}
	
	public Pointd isSnakeColliding(Snake s)
	{
		Segment l = s.getIntersectionTestLine();
		return geomObject_.intersect(l);
	}
	
	public void draw(Graphics2D g)
	{
		g.setColor(color_);
		if (null != geomObject_)
			geomObject_.draw(g);
	}
	
	public Color getColor()
	{
		return color_;
	}
	
	public GeometricObject getGeometricObject()
	{
		return geomObject_;
	}

}
