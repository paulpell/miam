package org.paulpell.miam.geom;

import java.awt.Color;
import java.awt.Graphics2D;

import org.paulpell.miam.gui.editor.tools.EditorCursorEnum;

public abstract class EditorDisplayElement
{
	GeometricObject shape_;
	Color color_;
	final public boolean isWallElement_;

	public EditorDisplayElement(GeometricObject shape, Color c, boolean isWallElement)
	{
		shape_ = shape;
		color_ = c;
		isWallElement_ = isWallElement;
	}

	public void draw(Graphics2D g)
	{
		g.setColor(color_);
		shape_.draw(g);
	}

	public Color getColor()
	{
		return color_;
	}

	public GeometricObject getGeometricObject()
	{
		return shape_;
	}
	
	public abstract Pointd getSelectedPoint();
	
	public abstract EditorCursorEnum select(Pointd where);
	
	public abstract void unselect();
	
	public abstract void move(Pointd where);
	
	public static EditorDisplayElement createElement(GeometricObject obj, Color c, boolean b)
	{
		if (obj instanceof Segment)
			return new EditorSegment((Segment)obj, c, b);
		if (obj instanceof Circle)
			return new EditorCircle((Circle)obj, c, b);
		if (obj instanceof Rectangle)
			return new EditorRectangle((Rectangle)obj, c, b);
		
		throw new IllegalArgumentException("Cannot create display element");
	}

}
