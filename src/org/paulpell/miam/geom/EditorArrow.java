package org.paulpell.miam.geom;

import java.awt.Color;

import org.paulpell.miam.gui.editor.tools.EditorCursorEnum;
import org.paulpell.miam.logic.Arith;

public class EditorArrow extends EditorDisplayElement
{
	private boolean tailSelected_ = false;
	private boolean headSelected_ = false;
	
	

	public EditorArrow(Arrow shape, Color c)
	{
		super(shape, c, false);
	}

	@Override
	public EditorCursorEnum select(Pointd where)
	{
		Pointd p1 = ((Arrow)shape_).p1_;
		Pointd p2 = ((Arrow)shape_).p2_;
		double d1 = Arith.dist(where, p1);
		double d2 = Arith.dist(where, p2);
		if (d1 < d2)
		{
			headSelected_ = true;
			tailSelected_ = false;
			return EditorCursorEnum.ROTATE;
		}
		else 
		{
			headSelected_ = false;
			tailSelected_ = true;
			return EditorCursorEnum.MOVE;
		}
	}

	@Override
	public void move(Pointd where)
	{
		Arrow a = (Arrow)shape_;
		if (headSelected_)
			shape_ = new Arrow(where, a.p2_);
		else if (tailSelected_)
			shape_ = new Arrow(a.p1_, where);
	}

	@Override
	public void unselect()
	{
		tailSelected_ = false;
		headSelected_ = false;
	}

	@Override
	public Pointd getSelectedPoint()
	{
		Arrow a = (Arrow)shape_;
		if (tailSelected_)
			return a.p2_;
		if (headSelected_)
			return a.p1_;
		
		throw new IllegalArgumentException("Not selected");
	}

}
