package org.paulpell.miam.geom;

import java.awt.Color;

import org.paulpell.miam.gui.editor.tools.EditorCursorEnum;
import org.paulpell.miam.logic.Arith;

public class EditorSegment extends EditorDisplayElement
{
	private Pointd selectedPoint_;

	public EditorSegment(Segment shape, Color c, boolean isWallElement)
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
		Pointd p1 = ((Segment)shape_).p1_;
		Pointd p2 = ((Segment)shape_).p2_;
		double d1 = Arith.dist(p1, where);
		double d2 = Arith.dist(p2, where);
		if (d1 < d2)
			selectedPoint_ = p1;
		else
			selectedPoint_ = p2;
		
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
		Pointd p1 = ((Segment)shape_).p1_;
		Pointd p2 = ((Segment)shape_).p2_;
		
		if (selectedPoint_ == p1)
			shape_ = new Segment(where, p2);
		else
			shape_ = new Segment(p1, where);
	}

}
