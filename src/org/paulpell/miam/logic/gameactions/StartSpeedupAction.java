package org.paulpell.miam.logic.gameactions;

import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.draw.snakes.Snake;

public class StartSpeedupAction
	extends SnakeAction
//	implements StartAction
{

	public StartSpeedupAction(int i)
	{
		super(i);
	}
	
	@Override
	public void perform(Snake s)
	{
		if (s != null)
			s.setSpeedup(true);
	}
	
	public String toString()
	{
		return "StartSpeedUpAction(" + snakeIndex_ +")" ;
	}
	
	@Override
	public int getActionType()
	{
		return Constants.SNAKE_ACTION_SPEEDUP;
	}

}
