package logic.actions;

import logic.draw.snakes.Snake;

public class EndSpeedupAction extends SnakeAction implements EndAction {

	public EndSpeedupAction(int i) {
		super(i);
	}
	@Override
	public void perform(Snake s) {
		if (s != null) {
			s.setSpeedup(false);
		}
	}

}
