package org.paulpell.miam.geom;

import java.awt.Color;

import org.paulpell.miam.gui.editor.tools.EditorCursorEnum;
import org.paulpell.miam.logic.Arith;

public class EditorCircle extends EditorDisplayElement
{
	boolean isCenterSelected_;
	Pointd selectedPoint_;

	public EditorCircle(Circle shape, Color c, boolean isWallElement)
	{
		super(shape, c, isWallElement);
	}

	@Override
	public Pointd getSelectedPoint()
	{
		return selectedPoint_;
	}

	@Override
	public EditorCursorEnum select(Pointd where)
	{
		Circle c = (Circle)shape_;
		double d = Arith.dist(where, c.position_);
		if (d < c.radius_ / 2.)
		{
			isCenterSelected_ = true;
			selectedPoint_ = c.position_;
		}
		else
		{
			Line l = new Line(c.position_, where);
			Pointd i = c.intersect(l);
			isCenterSelected_ = false;
			selectedPoint_ = i;
		}
		return EditorCursorEnum.MOVE;
	}

	@Override
	public void unselect()
	{
		selectedPoint_ = null;
	}

	@Override
	public void move(Pointd where)
	{
		Circle c = (Circle)shape_;
		if (isCenterSelected_)
		{
			double dx = where.x_ - selectedPoint_.x_;
			double dy = where.y_ - selectedPoint_.y_;
			Vector2D dv = new Vector2D(dx, dy);
			shape_ = c.translate(dv);
			selectedPoint_ = where;
		}
		else
		{
			c.radius_ = Arith.dist(where, c.position_);
		}
	}

}
