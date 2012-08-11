package logic.draw.snakes;


import java.util.Iterator;
import java.util.LinkedList;
import java.awt.Color;
import java.awt.Graphics;

import logic.Arith;
import logic.Constants;
import logic.Globals;
import logic.draw.Drawable;
import logic.draw.items.Banana;
import logic.draw.items.Item;
import logic.draw.items.SpecialItem;
import geom.GeometricObject;
import geom.Pointd;
import geom.Line;

public class Snake extends Drawable {
	
	/* Static constants/functions ***************************/
	
	// internal counter, reset at new Game()
	private static int globalId = 0;
	
	// some default colors
	final private static Color[] colors = { new Color(160, 20, 50),
									new Color(50, 140, 20),
									new Color(30, 30, 190),
									new Color(10, 150, 60),
									new Color(130, 100, 10)};
	final private static Color deadColor = new Color(200,200,0);
	
	// for a new game, globalId should be 0
	public static void resetIds() {
		globalId = 0;
	}
	
	
	
	
	/* Individual properties *********************/
	int id = Snake.globalId++;
	
	int score = 0;
	boolean isAlive = true;
	// physics/geometry
	int speed = Globals.SNAKE_NORMAL_SPEED;
	int extraspeed = Globals.SNAKE_SPEEDUP_EXTRA;
	boolean speedup = false;
	int direction; // either 0-3 or an angle based on 360 degrees (std trigo)
	double dx, dy; // cached values for how to advance, use computeDs() to update
	int angleDiff = Globals.SNAKE_ANGLE_DIFF;
	double length = Constants.INIT_SNAKE_LENGTH;
	double toGrow = length;
	double extraThickness = Globals.SNAKE_DEFAULT_EXTRA_THICKNESS;
	Color color = colors[id];
	
	// to make sure we don't add several points between two advance() calls
	boolean turnLeft = false, turnRight = false;
	
	// the first point will be the head!
	LinkedList<Pointd> points = new LinkedList<Pointd>();
	Pointd previousHead;
	
	/* ****** Item stuff **********/
	int randomTurn = 0; // how many remaining steps turning is not mastered
	
	// for now, let's accept only one SpecialItem per snake
	SpecialItem specialItem;

	/* Constructor ***************/
	public Snake(int x0, int y0, int dir) {
		direction = dir;
		computeDs();
		previousHead = new Pointd(x0, y0); 
		points.add(previousHead);
		double x1 = x0 + dx;
		double y1 = y0 + dy;
		points.addFirst(new Pointd(x1,y1));
	}
	
	/* Properties of the snake **********************************************/
	public boolean isAlive() {
		return isAlive;
	}
	// we give a point where we want to draw the head
	public void kill(Pointd newHead) {
		points.removeFirst();
		points.addFirst(newHead);
		isAlive = false;
	}
	public void resurrect() {
		if (isAlive) {
			return;
		}
		reverse();
		isAlive = true;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeedup(boolean b) {
		if (b && !speedup) {
			speedup = true;
			speed += extraspeed;
		}
		else if (!b && speedup) {
			speedup = false;
			speed -= extraspeed;
		}
		computeDs();
	}

	public int getScore() {
		return score;
	}
	
	public void special() {
		if (specialItem != null) {
			specialItem.activate(this);
		}
	}
	
	public void acceptItem(Item it) {
		if (it instanceof SpecialItem) {
			specialItem = (SpecialItem)it;
		}
		else if(it instanceof Banana) {
			randomTurn = 30 + Constants.rand.nextInt(10);
		}
		else {
			score += it.getScore();
			growBy(it.getGrowth());
			addThickness(it.getThickness());
			if (it.isReversing()) {
				reverse();
			}
		}
	}
	private void reverse() {
		LinkedList<Pointd> ps = new LinkedList<Pointd>();
		Iterator<Pointd> it = points.iterator();
		while(it.hasNext()) {
			ps.addFirst(it.next());
		}
		points = ps;
		direction = Arith.dir(ps.get(1), ps.get(0));
		computeDs();
	}
	
	public double getExtraThickness() {
		return extraThickness;
	}
	
	private void addThickness(double delta) {
		extraThickness += delta;
	}

	
	/* **********************************************************************/
	/* *** Movement methods ************************************************/
	
	public void growBy(int l) {
		toGrow += l;
	}
	

	public void setTurnLeft(boolean val) {
		turnLeft  = val;
	}

	public void setTurnRight(boolean val) {
		turnRight  = val;
	}
	
	/* ********** computes dx and dy, the distances the snake will cover on the x and y axis */
	private void computeDs() {
		if (Globals.USE_CLASSIC_SNAKE) {
			dx = ((direction + 1) % 2) * (direction - 1) * speed;
			dy = (direction % 2) * (direction - 2) * speed;
		}
		else {
			dx = Math.cos(direction / 180. * Math.PI) * speed;
			dy = Math.sin(direction / 180. * Math.PI) * speed;
		}
	}
	
	/* ********** turn *********/
	private void turnLeft() {
		direction = (direction - angleDiff + 360) % 360;
		computeDs();
	}
	private void turnRight() {
		direction = (direction + angleDiff + 360) % 360;
		computeDs();
	}
	
	private void updateTurn() {
		// to handle the uncontrolled turn
		int nextDir = -1;
		if (randomTurn > 0) {
			--randomTurn;
			nextDir = Constants.rand.nextInt(3);
		}
		
		if (nextDir == -1) {
			if (turnLeft && !turnRight) {
				turnLeft();
			}
			if (turnRight && !turnLeft) {
				turnRight();
			}
			
		}
		else if (nextDir == 1) {
			turnLeft();
		}
		else if (nextDir == 2) {
			turnRight();
		}
		
		if ((turnLeft ^ turnRight) || nextDir == 1 || nextDir == 2) {
			Pointd newHead = (Pointd) points.getFirst().clone();
			points.addFirst(newHead);
		}
	}
	

	/* ************ one step for the snake ***************/
	// the head shall advance, as well as the tail
	public void advance()
	{
		// dead or immobile snakes don't move!!!
		if (!isAlive || speed == 0) {
			return;
		}
		
		// set the variables for turning
		updateTurn();
		
		// advance the head
		{
		Pointd head = points.getFirst();
		previousHead = (Pointd)head.clone();
		head.x += dx;
		head.y += dy;
		}

		// advance the tail
		double s = speed; // we have to remove s units
		if (toGrow >= speed) { // don't move the last point while growing
			toGrow -= speed;
		}
		else {
			if (toGrow> 0) {
				s = speed - toGrow;
				toGrow = 0;
			}
			
			// remove as many points at the end and shorten as needed
			Iterator<Pointd> it = points.descendingIterator();
			Pointd p1 = it.next(), p2 = it.next();
			int toRem = 0; // how many points to remove? (impossible while traversing)
			do {
				double l = Math.sqrt((p1.x - p2.x)*(p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y));
				if (l > s) { // anything remains in the last segment?
					double dx = (p2.x - p1.x),
							dy = (p2.y - p1.y);
					p1.x += dx * s / l;
					p1.y += dy * s / l;
					s = 0; // to exit the loop
				}
				else { // no => remove last segment (increase toRem)
					s -= l;
					p1 = p2;
					p2 = it.next();
					++toRem;
				}
			} while (s > 0);
			
			for (int i=0; i<toRem; i++) {
				points.removeLast();
			}
		}
	}
	


	/* Drawable methods *******************************************/
	
	public void draw(Graphics g) {
		if (isAlive) {
			g.setColor(color);
		}
		else {
			g.setColor(deadColor);
		}
		
		Iterator<Pointd> e = points.iterator();
		Pointd p1 = e.next(), p2;
		do {
			p2 = e.next();
			g.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);

			p1 = p2;
		} while (e.hasNext());
		g.setColor(new Color(255,255,255));
		p1 = getHead();
		g.fillRect((int)p1.x, (int)p1.y, 1, 1);
	}
	
	public boolean isPointInside(Pointd p) {
		Iterator<Pointd> it = points.iterator();
		Pointd p1  = it.next(), p2;
		do {
			p2 = it.next();
			if (new Line(p1, p2).isPointInside(p)) {
				return true;
			}
			p1 = p2;
		} while (it.hasNext());
		return false;
	}
	
	public boolean isShapeInside(GeometricObject shape) {
		Iterator<Pointd> iter = points.iterator();
		Pointd p1 = iter.next(), p2;
		for (;iter.hasNext();) {
			p2 = iter.next();
			if (null != shape.intersect(new Line(p1,p2))) {
				return true;
			}
		}
		return false;
	}
	
	//public boolean isSnakeColliding(Snake other) {
	public Pointd isSnakeColliding(Snake other) {
		Pointd p1other = other.getHead(),
				p2other = other.getPreviousHead();
		Line lineOther = new Line(p1other, p2other);
		boolean otherHorizontal = Arith.equalsd(p1other.y, p2other.y);//p1other.y == p2other.y;
		
		Iterator<Pointd> it = points.iterator();
		
		
		// if it's the same Snake, it can't collide with its first 3 segments
		if (other == this) {
			if (points.size() < 5) {
				//return false; // collision is impossible
				return null;
			}
			it.next(); it.next(); it.next(); // skip first points
		}
		Pointd p1 = it.next(), p2; // p1np2 will represent the segments of this Snake
		do {
			p2 = it.next(); // it's sure next() exists at the first step
			
			if (Globals.USE_CLASSIC_SNAKE) {
				// both horizontal or both vertical means an impossible collision
				//if (otherHorizontal && p1.x == p2.x) {
				if (otherHorizontal && Arith.equalsd(p1.x,p2.x)) {
					if ((p1other.y >= p1.y && p1other.y <= p2.y)
							|| (p1other.y <= p1.y && p1other.y >= p2.y)) {
						if ((p1.x - p1other.x) * (p1.x - p2other.x) <= 0) {
							//return true;
							return new Pointd(p1.x, p1other.y);
						}
					}
				}
				//else if (!otherHorizontal && p1.y == p2.y) {
				else if (!otherHorizontal && Arith.equalsd(p1.y, p2.y)) {
					if ((p1other.x >= p1.x && p1other.x <= p2.x)
							|| (p1other.x <= p1.x && p1other.x >= p2.x)) {
						if ((p1.y - p1other.y) * (p1.y - p2other.y) <= 0) {
							//return true;
							return new Pointd(p1other.x,p1.y);
						}
					}
				}
			}
			else { // !Globals.USE_CLASSIC_SNAKE
				
				Line l = new Line(p1, p2);
				Pointd intersection = l.intersect(lineOther);
				if (intersection != null) {
					//return true;
					return intersection;
				}
				
			}
			
			
			p1 = p2;
		} while (it.hasNext());
		//return false;
		return null;
	}

	public Pointd getHead() {
		return points.getFirst();
	}
	public Pointd getPreviousHead() {
		return previousHead;
	}
	public Pointd getTail() {
		return points.getLast();
	}
	
	public Pointd getPointd() {
		return getHead();
	}

}
