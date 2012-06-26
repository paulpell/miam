package logic.actions;

import logic.draw.Snake;

public class EndTurnLeftAction extends SnakeAction {

	public EndTurnLeftAction(int i) {
		super(i);
	}
	public void perform(Snake s) {
		if (s != null) {
			s.setTurnLeft(false);
		}
	}
}
