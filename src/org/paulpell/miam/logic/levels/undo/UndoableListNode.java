package org.paulpell.miam.logic.levels.undo;

class UndoableListNode
{
	UndoableListNode prev_;
	UndoableListNode next_;
	UndoableAction action_;
	
	public UndoableListNode(UndoableListNode prev, UndoableListNode next, UndoableAction action)
	{
		prev_ = prev;
		next_ = next;
		action_ = action;
	}

	// this will become the last element
	/*public void cut()
	{
		next_ = null;
	}*/
	
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
		//UndoableListNode node = this;
		//while (null != node.next_)
		///	node = node.next_;
		//node.next_ = new UndoableListNode(node, null, action);
		next_ = new UndoableListNode(this, null, action);
		return next_;
	}
}
