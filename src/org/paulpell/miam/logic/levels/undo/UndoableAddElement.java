package org.paulpell.miam.logic.levels.undo;

import java.util.Collection;


public class UndoableAddElement <T>  implements UndoableAction
{
	
	T element_;
	Collection <T> collection_;

	public UndoableAddElement(T element, Collection <T> collection)
	{
		element_ = element;
		collection_ = collection;
	}

	@Override
	public void doAction()
	{
		boolean add = collection_.add(element_);
		assert add : "Bad doAction : did not add element";
	}

	@Override
	public void undoAction()
	{
		boolean add = collection_.remove(element_);
		assert add : "Bad undoAction : did not remove element";
	}

}
