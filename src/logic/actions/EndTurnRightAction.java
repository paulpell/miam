package logic.actions;

import logic.draw.snakes.Snake;

public class EndTurnRightAction extends SnakeAction implements EndAction {

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
