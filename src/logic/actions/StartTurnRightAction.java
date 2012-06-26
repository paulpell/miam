package logic.actions;

import logic.draw.Snake;

public class StartTurnRightAction extends SnakeAction {

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
