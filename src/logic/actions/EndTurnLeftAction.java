package logic.actions;

import logic.draw.snakes.Snake;

public class EndTurnLeftAction extends SnakeAction implements EndAction {

	public EndTurnLeftAction(int i) {
		super(i);
	}
	public void perform(Snake s) {
		if (s != null) {
			s.setTurnLeft(false);
		}
	}
}
