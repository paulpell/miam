package org.paulpell.miam.geom;

import java.awt.Color;

import org.paulpell.miam.gui.editor.tools.EditorCursorEnum;
import org.paulpell.miam.logic.Arith;

public class EditorArrow extends EditorDisplayElement
{	
	byte selIndex_;
	Arrow arrow_;

	public EditorArrow(Arrow shape, Color c)
	{
		super(shape, c, false);
		arrow_ = shape;
		selIndex_ = 0;
	}

	@Override
	public EditorCursorEnum select(Pointd where)
	{
		
		double d1 = Arith.dist(where, arrow_.p1_);
		double d2 = Arith.dist(where, arrow_.p2_);
		if (d1 < d2)
		{
			selIndex_ = 1;
			return EditorCursorEnum.ROTATE;
		}
		else 
		{
			selIndex_ = 2;
			return EditorCursorEnum.MOVE;
		}
	}

	@Override
	public void move(Pointd where)
	{
		if (selIndex_ == 1)
			shape_ = arrow_ = new Arrow(where, arrow_.p2_);
		else if (selIndex_ == 2)
			shape_ = arrow_ = new Arrow(arrow_.p1_, where);
	}

	@Override
	public void unselect()
	{
		selIndex_ = 0;
	}

	@Override
	public Pointd getSelectedPoint()
	{
		if (selIndex_ == 1)
			return arrow_.p1_;
		if (selIndex_== 2)
			return arrow_.p2_;
		
		throw new IllegalArgumentException("Not selected");
	}

	public double getAngleRad()
	{
		return arrow_.angle_;
	}
	
	public Pointd getPoint()
	{
		return arrow_.p1_;
	}

}
