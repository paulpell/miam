package logic.actions;

import logic.draw.Snake;


public abstract class SnakeAction {
	protected int snakeIndex;
	public SnakeAction(int i) {
		snakeIndex = i;
	}
	public int getSnakeIndex() {
		return snakeIndex;
	}
	public abstract void perform(Snake s);
}
