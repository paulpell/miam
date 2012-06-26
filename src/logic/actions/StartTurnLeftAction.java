package logic.actions;

import logic.draw.Snake;

public class StartTurnLeftAction extends SnakeAction {

	public StartTurnLeftAction(int i) {
		super(i);
	}
	public void perform(Snake s) {
		if (s != null) {
			s.setTurnLeft(true);
		}
	}
}
