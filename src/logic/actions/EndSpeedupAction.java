package logic.actions;

import logic.draw.Snake;

public class EndSpeedupAction extends SnakeAction {

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
