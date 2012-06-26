package logic.actions;

import logic.draw.Snake;

public class EndTurnRightAction extends SnakeAction {

	public EndTurnRightAction(int i) {
		super(i);
	}
	@Override
	public void perform(Snake s) {
		if (s != null) {
			s.setTurnRight(false);
		}
	}

}
