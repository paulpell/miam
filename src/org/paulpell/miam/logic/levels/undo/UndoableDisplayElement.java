package org.paulpell.miam.logic.levels.undo;

import org.paulpell.miam.geom.EditorDisplayElement;
import org.paulpell.miam.logic.levels.LevelEditorControl;

public class UndoableDisplayElement implements UndoableAction
{

	EditorDisplayElement ede_;
	
	public UndoableDisplayElement(EditorDisplayElement ede)
	{
		ede_ = ede;
	}

	@Override
	public void doAction(LevelEditorControl lec)
	{
		lec.addDisplayElement(ede_);
	}

	@Override
	public void undoAction(LevelEditorControl lec)
	{
		lec.removeDisplayElement(ede_);
	}

}
