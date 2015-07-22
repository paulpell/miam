package org.paulpell.miam.logic.gameactions;

import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.draw.snakes.Snake;

public class EndSpeedupAction
		extends SnakeAction
		//implements EndAction
{

	public EndSpeedupAction(int i) {
		super(i);
	}
	@Override
	public void perform(Snake s) {
		if (s != null)
			s.setSpeedup(false);
	}
	
	@Override
	public String toString()
	{
		return "EndSpeedUpAction(" + snakeIndex_ +")" ;
	}
	
	@Override
	public int getActionType()
	{
		return Constants.SNAKE_ACTION_SPEEDUP | Constants.SNAKE_END_ACTION;
	}

}
