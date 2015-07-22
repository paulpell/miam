package org.paulpell.miam.geom;

import java.awt.Color;
import java.awt.Graphics2D;

import org.paulpell.miam.gui.editor.tools.EditorCursorEnum;
import org.paulpell.miam.logic.draw.items.Item;

public class EditorItem extends EditorRectangle
{

	private Item item_;
	private boolean isSelected_ = false;
	
	public EditorItem(Color c, Item item)
	{
		super(item.getShape(), c, false);
		item_ = item;
	}
	
	public Item getItem()
	{
		return item_;
	}
	
	// first draw item, then the rect if selected
	@Override
	public void draw(Graphics2D g)
	{
		item_.draw(g);
		if (isSelected_)
			super.draw(g);
	}
	
	@Override
	public void move(Pointd where)
	{
		item_ = item_.newItem(where.x_, where.y_);
		shape_ = item_.getShape();
	}
	
	@Override
	public Pointd getSelectedPoint()
	{
		return shape_.getPointd();
	}
	
	@Override
	public EditorCursorEnum select(Pointd where)
	{
		isSelected_ = true;
		return EditorCursorEnum.MOVE;
	}

	@Override
	public void unselect()
	{
		isSelected_ = false;
	}

}
