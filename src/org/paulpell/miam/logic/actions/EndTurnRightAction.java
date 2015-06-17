package org.paulpell.miam.logic.actions;

import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.draw.snakes.Snake;

public class EndTurnRightAction
	extends SnakeAction
	//implements EndAction
{

	public EndTurnRightAction(int i)
	{
		super(i);
	}
	@Override
	public void perform(Snake s) 
	{
		if (s != null)
			s.setTurnRight(false);
	}
	
	public String toString()
	{
		return "EndTurnRightAction(" + snakeIndex_ +")" ;
	}
	
	@Override
	public int getActionType()
	{
		return Constants.SNAKE_ACTION_TURN_RIGHT | Constants.SNAKE_END_ACTION;
	}

}
