package logic;

import geom.Pointd;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import logic.draw.Drawable;
import logic.draw.items.Item;
import logic.draw.items.ItemCreator;
import logic.draw.snakes.ClassicSnake;
import logic.draw.snakes.Snake;
import logic.draw.walls.DefaultWall;
import logic.draw.walls.Wall;

public class Game {

	
	private final Control control;
	private final ItemCreator itemCreator;
	
	// here we store all the game objects
	private Vector<Snake> snakes;
	private int aliveSnakes;
	private Vector<Item> items;
	private Wall wall;


	public Game(Control gc) {
		this(gc, new Vector<Item>(), new DefaultWall());
	}

	public Game(Control gc, Wall wall) {
		this(gc, new Vector<Item>(), wall);
	}
	
	public Game(Control gc, Vector<Item> items) {
		this(gc, items, new DefaultWall());
		//items.addAll(items);
	}
	
	public Game(Control gc, Vector<Item> init_items, Wall init_wall) {
		// TODO control.configure(this); or configure(someGameConfiguration);
		
		control = gc;
		
		
		////////////////////
		/////// add the stuff to draw
		
		items = init_items;
		
		// snakes 
		snakes = new Vector<Snake>();
		Snake s = null;
		aliveSnakes = Globals.NUMBER_OF_SNAKES;
		for (int i=0; i<aliveSnakes; ++i) {
			int x = 30 + 30 * i, y = 200 - 40 * i;
			if (Globals.USE_CLASSIC_SNAKE) {
				s = new ClassicSnake(x, y, 2);
			}
			else {
				s = new Snake(x, y, 0);
			}
			snakes.add(s);
		}
		//Snake.resetIds();

		// items and level (constructor initializes the automatic creation of new items --> timer)
		itemCreator = new ItemCreator(this, Globals.SCORE_ITEMS_ONLY);
		for (int i=0; i<3;++i) {
			addItem(itemCreator.createScoreItem(snakes));
		}
		
		wall = init_wall;
		
		
		Globals.currentGame = this;
	}
	
	
	
	/* *************************************
	 * Helper functions
	 */
	public int getNumberOfSnakes() {
		return snakes.size();
	}
	
	public Vector<Snake> getSnakes() {
		return snakes;
	}
	
	public Snake getSnake(int index) {
		if (index < snakes.size() && index > -1) {
			return snakes.get(index);
		}
		return null;
	}
	
	public void addItem(Item item) {
		items.add(item);
	}
	
	public Iterator<Drawable> getDrawablesIterator() {
		Vector<Drawable> drawables = new Vector<Drawable>();
		
		drawables.add(wall);
		drawables.addAll(snakes);
		drawables.addAll(items);
		
		return drawables.iterator();
	}
	

	/**************** The main function for a game, where snakes are moved */
	
	public void update() {
		
		
		// we check this here, so the final step can be drawn (paint is called one last time after previous update())
		if (aliveSnakes == 0) {
			control.endGame();
		}
		
		// advance the snakes
		for (Enumeration<Snake> e = snakes.elements(); e.hasMoreElements();) {
			Snake s = e.nextElement();
			s.advance();
		}
		
		Pointd collision;
		// check for collisions..
		for (Enumeration<Snake> e = snakes.elements(); e.hasMoreElements();) {
			Snake s = e.nextElement();
			// if the snake is dead, we don't care
			if (s.isAlive()) {
				
				// .. against the wall
				collision = wall.isSnakeColliding(s);
				// .. and against the other snakes
				if (collision == null) {
					for  (Enumeration<Snake> e2 = snakes.elements(); e2.hasMoreElements(); ) {
						Snake s2 = e2.nextElement();
						collision = s2.isSnakeColliding(s);
						if (collision != null) {
							break;
						}
					}
				}
				if (collision != null) {
					s.kill(collision);
					--aliveSnakes;
				}
			}
			// .. check the remaining items
			if (s.isAlive()) { // (might have died in between)
				for (Enumeration<Item> its = items.elements(); its.hasMoreElements(); ) {
					Item item = its.nextElement();
					collision = item.isSnakeColliding(s);
					if (collision != null) {
						s.acceptItem(item);
						items.remove(item);
						// replace by a new item
						Item i = itemCreator.createItem(snakes);
						addItem(i);
						break;
					}
				}
			}
		}
	}
}