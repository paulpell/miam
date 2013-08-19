package logic.actions;

import logic.draw.snakes.Snake;

public class SpecialAction extends SnakeAction {

	public SpecialAction(int i) {
		super(i);
	}
	
	@Override
	public void perform(Snake s) {
		s.special();
	}

}
