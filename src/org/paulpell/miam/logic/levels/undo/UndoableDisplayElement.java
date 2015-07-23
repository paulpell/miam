package org.paulpell.miam.logic.levels.undo;

import java.util.Collection;

import org.paulpell.miam.geom.EditorDisplayElement;

public class UndoableDisplayElement implements UndoableAction
{

	EditorDisplayElement ede_;
	Collection <EditorDisplayElement> edes_;
	
	public UndoableDisplayElement(EditorDisplayElement ede, Collection <EditorDisplayElement> edes)
	{
		ede_ = ede;
		edes_ = edes;
	}

	@Override
	public void doAction()
	{
		boolean add = edes_.add(ede_);
		assert add : "Bad doAction : did not add element";
	}

	@Override
	public void undoAction()
	{
		boolean add = edes_.remove(ede_);
		assert add : "Bad undoAction : did not remove element";
	}

}
