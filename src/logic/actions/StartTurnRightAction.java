package logic.actions;

import logic.draw.snakes.Snake;

public class StartTurnRightAction extends SnakeAction implements StartAction {

	public StartTurnRightAction(int i) {
		super(i);
	}
	@Override
	public void perform(Snake s) {
		if (s != null) {
			s.setTurnRight(true);
		}
	}

}
