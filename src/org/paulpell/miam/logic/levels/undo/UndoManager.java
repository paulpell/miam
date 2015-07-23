package org.paulpell.miam.logic.levels.undo;

import java.util.Collection;

import org.paulpell.miam.geom.EditorDisplayElement;

/**
 * This class is responsible to maintain a list of actions.
 * It is this class that actually has the action executed.
 * @author paul
 *
 */
public class UndoManager
{

	
	UndoableListNode first_;
	UndoableListNode current_;
	
	
	
	
	public UndoManager()
	{
		first_ = current_ = null;
	}
	
	public boolean canUndo()
	{
		return null != current_;
	}
	
	public void undo()
	{
		if (canUndo())
		{
			current_.getAction().undoAction();
			current_ = current_.prev_;
		}
	}
	
	public boolean canRedo()
	{
		//return null != current_ && null != current_.next_
		//		|| null != first_;
		return null != current_ && null != current_.next_ // means not last action
				|| null == current_ && null != first_;// means everything to redo
	}
	
	public void redo()
	{
		if (canRedo())
		{
			if (null != current_)
				current_ = current_.next_;
			else
				current_ = first_;
			current_.getAction().doAction();
		}
	}

	// this will destroy all the the undone actions "after" in the list
	public void actionTaken(UndoableAction action)
	{
		if (current_ == null)
		{
			first_ = new UndoableListNode(null, null, action);
			current_ = first_;
		}
		else
		{
			current_ = current_.createNext(action); // removes all others after
		}
		action.doAction();
	}
}
