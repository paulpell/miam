package org.paulpell.miam.geom;

import java.awt.Color;

import org.paulpell.miam.gui.editor.tools.EditorCursorEnum;
import org.paulpell.miam.logic.Arith;

public class EditorRectangle extends EditorDisplayElement
{

	int selectedPointIndex_; // see Rectangle.java for description
	
	public EditorRectangle(Rectangle shape, Color c, boolean isWallElement)
	{
		super(shape, c, isWallElement);
	}

	@Override
	public Pointd getSelectedPoint()
	{
		if (selectedPointIndex_ >= 0
				&& selectedPointIndex_ < 4)
			return ((Rectangle)shape_).points_[selectedPointIndex_];
		return null;
	}

	@Override
	public EditorCursorEnum select(Pointd where)
	{
		Pointd[] ps = ((Rectangle)shape_).points_;
		int selindex = 0;
		double dist = Double.MAX_VALUE;
		double d;
		for (int i=0; i<4; ++i)
		{
			if (dist > (d = Arith.dist(where, ps[i])))
			{
				dist = d;
				selindex = i;
			}
		}
		selectedPointIndex_ = selindex;
		return EditorCursorEnum.MOVE;
	}

	@Override
	public void unselect()
	{
		selectedPointIndex_ = -1;
	}

	// create a new rectangle with the 2 points:
	// - the new point
	// - the point opposite to the selected one
	@Override
	public void move(Pointd where)
	{
		Rectangle orig = (Rectangle)shape_;
		int otherIndex;
		switch (selectedPointIndex_)
		{
		case -1:
			return;
		case 0:
		case 1:
			otherIndex = 1 - selectedPointIndex_;
			break;
		case 2:
		case 3:
			otherIndex = 5 - selectedPointIndex_;
			break;
		default:
			throw new IllegalArgumentException("Bad selected index!");
		}
		
		shape_ = new Rectangle(where, orig.points_[otherIndex]);
	}

}
