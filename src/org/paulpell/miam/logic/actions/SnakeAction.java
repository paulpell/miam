package org.paulpell.miam.logic.actions;

import org.paulpell.miam.logic.draw.snakes.Snake;


public abstract class SnakeAction
{
	protected int snakeIndex_;

	// 
	public abstract void perform(Snake s);
	
	protected SnakeAction(int i)
	{
		snakeIndex_ = i;
	}
	
	public int getSnakeIndex()
	{
		return snakeIndex_;
	}
	
	public abstract int getActionType();
	
	@Override
	public boolean equals(Object o)
	{
		return 
				o instanceof SnakeAction
				&& StartAction.class.isAssignableFrom(this.getClass()) == StartAction.class.isAssignableFrom(o.getClass())
				&& snakeIndex_ == ((SnakeAction)o).snakeIndex_
				&& getActionType() == ((SnakeAction)o).getActionType();
	}
	
	@Override
	public int hashCode()
	{
		return 100 * snakeIndex_
				+ 10 * getActionType()
				+ (StartAction.class.isAssignableFrom(this.getClass()) ? 1 : 0); 
	}
	
}
