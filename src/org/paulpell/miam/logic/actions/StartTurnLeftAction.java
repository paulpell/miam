package org.paulpell.miam.logic.actions;

import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.draw.snakes.Snake;

public class StartTurnLeftAction
	extends SnakeAction
	implements StartAction
{

	public StartTurnLeftAction(int i)
	{
		super(i);
	}
	public void perform(Snake s)
	{
		if (s != null)
			s.setTurnLeft(true);
	}
	
	public String toString()
	{
		return "StartTurnLeftAction(" + snakeIndex_ +")" ;
	}
	
	@Override
	public int getActionType()
	{
		return Constants.SNAKE_ACTION_TURN_LEFT;
	}
}
