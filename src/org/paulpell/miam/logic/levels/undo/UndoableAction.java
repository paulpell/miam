package org.paulpell.miam.logic.levels.undo;




/**
 * Classes implementing this interface will act on a level editor control.
 * @author paul
 *
 */
public interface UndoableAction
{
	public void doAction();
	public void undoAction();
}
