package org.paulpell.miam.logic.levels.undo;

import org.paulpell.miam.geom.EditorDisplayElement;
import org.paulpell.miam.logic.levels.LevelEditorControl;

public class UndoableMove implements UndoableAction
{

	EditorDisplayElement newElement_;
	EditorDisplayElement oldElement_;
	
	public UndoableMove(EditorDisplayElement newElement, EditorDisplayElement oldElement)
	{
		newElement_ = newElement;
		oldElement_ = oldElement;
	}

	@Override
	public void doAction(LevelEditorControl lec)
	{
		lec.removeDisplayElement(oldElement_);
		lec.addDisplayElement(newElement_);
	}

	@Override
	public void undoAction(LevelEditorControl lec)
	{
		lec.removeDisplayElement(newElement_);
		lec.addDisplayElement(oldElement_);
	}

}
