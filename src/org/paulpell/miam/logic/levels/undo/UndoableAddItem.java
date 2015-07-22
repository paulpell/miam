package org.paulpell.miam.logic.levels.undo;

import org.paulpell.miam.geom.EditorItem;
import org.paulpell.miam.logic.draw.items.Item;
import org.paulpell.miam.logic.levels.LevelEditorControl;

public class UndoableAddItem implements UndoableAction
{
	EditorItem item_;
	public UndoableAddItem(EditorItem i)
	{
		item_ = i;
	}

	@Override
	public void doAction(LevelEditorControl lec)
	{
		lec.addItem(item_);
	}
	
	@Override
	public void undoAction(LevelEditorControl lec)
	{
		lec.removeItem(item_);
	}

}
