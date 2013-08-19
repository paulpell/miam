package logic.actions;

import logic.draw.snakes.Snake;


public abstract class SnakeAction {
	protected int snakeIndex;
	protected SnakeAction(int i) {
		snakeIndex = i;
	}
	public int getSnakeIndex() {
		return snakeIndex;
	}
	public abstract void perform(Snake s);
}
