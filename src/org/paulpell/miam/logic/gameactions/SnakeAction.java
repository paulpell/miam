package org.paulpell.miam.logic.gameactions;

import org.paulpell.miam.logic.Constants;
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
	
	public int getSimpleActionType()
	{
		return getActionType() & ( ~ Constants.SNAKE_END_ACTION );
	}
	
	public boolean isStartAction()
	{
		return 0 == (getActionType() & Constants.SNAKE_END_ACTION);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return 
				o instanceof SnakeAction
			//	&& StartAction.class.isAssignableFrom(this.getClass()) == StartAction.class.isAssignableFrom(o.getClass())
				&& snakeIndex_ == ((SnakeAction)o).snakeIndex_
				&& getActionType() == ((SnakeAction)o).getActionType();
	}
	
	@Override
	public int hashCode()
	{
		return 100 * snakeIndex_
				+ 10 * getActionType()
				;//+ (StartAction.class.isAssignableFrom(this.getClass()) ? 1 : 0); 
	}
	
}
