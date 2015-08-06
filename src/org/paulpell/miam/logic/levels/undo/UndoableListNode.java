package org.paulpell.miam.logic.levels.undo;

class UndoableListNode
{
	UndoableListNode prev_;
	UndoableListNode next_;
	UndoableAction action_;
	
	public UndoableListNode(UndoableListNode prev, UndoableListNode next, UndoableAction action)
	{
		assert action != null : "Null action given to undo list node";
		prev_ = prev;
		next_ = next;
		action_ = action;
	}

	
	public UndoableAction getAction()
	{
		return action_;
	}
	
	public UndoableListNode getNext()
	{
		return next_;
	}
	
	public UndoableListNode getPrev()
	{
		return prev_;
	}
	
	public UndoableListNode createNext(UndoableAction action)
	{
		next_ = new UndoableListNode(this, null, action);
		return next_;
	}
}
