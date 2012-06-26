package logic.actions;

import logic.draw.Snake;

public class StartSpeedupAction extends SnakeAction {

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
