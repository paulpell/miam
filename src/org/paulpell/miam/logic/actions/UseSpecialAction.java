package org.paulpell.miam.logic.actions;

import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.draw.snakes.Snake;

public class UseSpecialAction extends SnakeAction {

	public UseSpecialAction(int i) {
		super(i);
	}
	
	@Override
	public void perform(Snake s) {
		s.useSpecialItem();
	}
	
	public String toString()
	{
		return "UseSpecialAction(" + snakeIndex_ +")" ;
	}
	
	@Override
	public int getActionType()
	{
		return Constants.SNAKE_ACTION_SPECIAL;
	}

}
