package org.paulpell.miam.logic.gameactions;

import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.draw.snakes.Snake;

public class EndTurnLeftAction
	extends SnakeAction
	//implements EndAction
{

	public EndTurnLeftAction(int i) {
		super(i);
	}
	public void perform(Snake s)
	{
		if (s != null)
			s.setTurnLeft(false);
	}
	
	public String toString()
	{
		return "EndTurnLeftAction(" + snakeIndex_ +")" ;
	}
	
	@Override
	public int getActionType()
	{
		return Constants.SNAKE_ACTION_TURN_LEFT | Constants.SNAKE_END_ACTION;
	}
}
