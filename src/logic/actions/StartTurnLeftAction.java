package logic.actions;

import logic.draw.snakes.Snake;

public class StartTurnLeftAction extends SnakeAction implements StartAction {

	public StartTurnLeftAction(int i) {
		super(i);
	}
	public void perform(Snake s) {
		if (s != null) {
			s.setTurnLeft(true);
		}
	}
}
