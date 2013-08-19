package logic.actions;

import logic.draw.snakes.Snake;

public class StartSpeedupAction extends SnakeAction implements StartAction {

	public StartSpeedupAction(int i) {
		super(i);
	}
	@Override
	public void perform(Snake s) {
		if (s != null) {
			s.setSpeedup(true);
		}
	}

}
