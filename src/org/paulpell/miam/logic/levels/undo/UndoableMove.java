package org.paulpell.miam.logic.levels.undo;

import java.util.Collection;

import org.paulpell.miam.geom.EditorDisplayElement;

public class UndoableMove implements UndoableAction
{

	EditorDisplayElement newElement_;
	EditorDisplayElement oldElement_;
	
	Collection <EditorDisplayElement> edes_;
	
	@SuppressWarnings("unchecked")
	public UndoableMove(EditorDisplayElement newElement, EditorDisplayElement oldElement, Collection <?> edes)
	{
		newElement_ = newElement;
		oldElement_ = oldElement;
		edes_ = (Collection <EditorDisplayElement>)edes;
	}

	@Override
	public void doAction()
	{
		boolean rem = edes_.remove(oldElement_);
		assert rem : "Bad doAction, did not remove old element";
		boolean add = edes_.add(newElement_);
		assert add : "Bad doAction, did not add new element";
	}

	@Override
	public void undoAction()
	{
		boolean rem = edes_.remove(newElement_);
		assert rem : "Bad undoAction, did not remove new element";
		boolean add = edes_.add(oldElement_);
		assert add : "Bad undoAction, did not add old element";
	}

}
