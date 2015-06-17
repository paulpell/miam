package org.paulpell.miam.logic.actions;

import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.draw.snakes.Snake;

public class StartTurnRightAction
	extends SnakeAction
	//implements StartAction
{

	public StartTurnRightAction(int i)
	{
		super(i);
	}
	@Override
	public void perform(Snake s) {
		if (s != null)
			s.setTurnRight(true);
	}
	
	public String toString()
	{
		return "StartTurnRightAction(" + snakeIndex_ +")" ;
	}
	
	@Override
	public int getActionType()
	{
		return Constants.SNAKE_ACTION_TURN_RIGHT;
	}

}
