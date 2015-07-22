package org.paulpell.miam.geom;

import java.awt.Color;

import org.paulpell.miam.gui.editor.tools.EditorCursorEnum;
import org.paulpell.miam.logic.Arith;

public class EditorArrow extends EditorDisplayElement
{	
	Pointd pSel1_;
	Pointd pSel2_;
	private Pointd selectedP_ = null;
	Arrow arrow_;

	public EditorArrow(Arrow shape, Color c)
	{
		super(shape, c, false);
		arrow_ = shape;
		pSel1_ = arrow_.p1_;
		pSel2_ = arrow_.p2_;
	}

	@Override
	public EditorCursorEnum select(Pointd where)
	{
		
		double d1 = Arith.dist(where, pSel1_);
		double d2 = Arith.dist(where, pSel2_);
		if (d1 < d2)
		{
			selectedP_ = pSel1_;
			return EditorCursorEnum.ROTATE;
		}
		else 
		{
			selectedP_ = pSel2_;
			return EditorCursorEnum.MOVE;
		}
	}

	@Override
	public void move(Pointd where)
	{
		if (selectedP_ == pSel1_)
			shape_ = new Arrow(where, arrow_.p2_);
		else if (selectedP_ == pSel2_)
			shape_ = new Arrow(arrow_.p1_, where);
	}

	@Override
	public void unselect()
	{
		selectedP_ = null;
	}

	@Override
	public Pointd getSelectedPoint()
	{
		if (selectedP_ == pSel1_)
			return arrow_.p1_;
		if (selectedP_ == pSel2_)
			return arrow_.p2_;
		
		throw new IllegalArgumentException("Not selected");
	}

	public double getAngle()
	{
		return arrow_.angle_;
	}
	
	public Pointd getPoint()
	{
		return arrow_.p1_;
	}

}
