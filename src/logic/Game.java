package logic;

import java.util.Enumeration;
import java.util.Vector;

import logic.draw.Item;
import logic.draw.ReversingItem;
import logic.draw.Snake;
import logic.draw.ClassicSnake;
import logic.draw.ScoreItem;
import logic.draw.SurroundingWall;
import logic.draw.Wall;

public class Game {

	
	private Control control;
	
	// here we store all the game objects
	private Vector<Snake> snakes = new Vector<Snake>();
	int aliveSnakes;
	private Vector<Item> items = new Vector<Item>();
	private Wall wall;

	public Game(Control gc) {
		// TODO gc.configure(this); or configure(someGameConfiguration);
		int numberOfSnakes = Globals.NUMBER_OF_SNAKES;
		control = gc;
		// add the stuff to draw
		Snake s = null;
		for (int i=0; i<numberOfSnakes; ++i) {
			int x = 30 + 30 * i, y = 200 - 40 * i;
			if (Globals.USE_CLASSIC_SNAKE) {
				s = new ClassicSnake(x, y, 2);
			}
			else {
				s = new Snake(x, y, 0);
			}
			snakes.add(s);
		}
		aliveSnakes = numberOfSnakes;
		
		items.add(new ScoreItem(20, 9));
		items.add(new ScoreItem(40, 8));
		items.add(new ScoreItem(12, 10));
		
		items.add(new ReversingItem(100, 50));

		wall = new SurroundingWall();
		
		control.addDrawables(snakes);
		control.addDrawables(items);
		control.addDrawable(wall);

		Snake.resetIds();
	}
	
	public Snake getSnake(int index) {
		if (index < snakes.size() && index > -1) {
			return snakes.get(index);
		}
		return null;
	}
	
	public void update() {
		
		// we place it here, so the final step can be drawn
		if (aliveSnakes == 0) {
			control.endGame();
		}
		
		// advance the snakes
		for (Enumeration<Snake> e = snakes.elements(); e.hasMoreElements();) {
			Snake s = e.nextElement();
			s.advance();
		}
		
		// check for collisions..
		for (Enumeration<Snake> e = snakes.elements(); e.hasMoreElements();) {
			Snake s = e.nextElement();
			// if the snake is dead, we don't care
			if (s.isAlive()) {
				
				// .. against the wall
				boolean kill = wall.isSnakeColliding(s);
				// .. and against the other snakes
				if (!kill) {
					for  (Enumeration<Snake> e2 = snakes.elements(); e2.hasMoreElements(); ) {
						Snake s2 = e2.nextElement();
						kill = s2.isSnakeColliding(s);
						if (kill) {
							break;
						}
					}
				}
				if (kill) { // don't remove from the lists
					System.out.println("Snake dies!");
					s.kill();
					--aliveSnakes;
				}
				// .. check the remaining items
				if (s.isAlive()) {
					for (Enumeration<Item> its = items.elements(); its.hasMoreElements(); ) {
						Item item = its.nextElement();
						if (item.isSnakeColliding(s)) {
							s.acceptItem(item);
							if (!item.isPersistent()) {
								items.remove(item);
								control.removeDrawable(item);
							}
							break;
						}
					}
				}
			}
		}
	}

	public void setTurnLeft(int snakeIndex, boolean val) {
		snakes.get(snakeIndex).setTurnLeft(val);
	}
	public void setTurnRight(int snakeIndex, boolean val) {
		snakes.get(snakeIndex).setTurnRight(val);
	}
	public void setSpeedup(int snakeIndex, boolean val) {
		snakes.get(snakeIndex).setSpeedup(val);
	}
}