package org.paulpell.miam.logic.levels.undo;

import java.util.Collection;

import org.paulpell.miam.geom.EditorItem;

public class UndoableAddItem implements UndoableAction
{
	EditorItem item_;
	Collection <EditorItem> items_;
	
	
	public UndoableAddItem(EditorItem i, Collection <EditorItem> items)
	{
		item_ = i;
		items_ = items;
	}

	@Override
	public void doAction()
	{
		boolean add = items_.add(item_);
		assert add : "Bad doAction, did not add item";
	}
	
	@Override
	public void undoAction()
	{
		boolean add = items_.remove(item_);
		assert add : "Bad undoAction, did not remove item";
	}

}
