package logic.draw.snakes;

import geom.Pointd;
import geom.Vector2D;

import java.awt.Polygon;
import java.util.Iterator;
import java.util.LinkedList;

import logic.Arith;
import logic.Globals;

public class ClassicSnake extends Snake {

	// dir has a different encoding
	public ClassicSnake(int x0, int y0, int dir) {
		super(x0, y0, dir);
	}

	long lastTurnTime = 0; // we don't want to be able to turn too much
	
	// checks if the last key hit was not too late, updates lastTurnTime if ok
	private boolean checkLastTurnTime() {
		long now = System.currentTimeMillis();
		long diff = now - lastTurnTime;
		if (diff >= Globals.SNAKE_TIME_BETW_TURNS_MILLIS) {
			lastTurnTime = now;
			return true;
		}
		return false;
	}
	
	private void turnLeft() {
		if (checkLastTurnTime()) {
			direction = (this.direction + 3) % 4;
			dx = ((direction + 1) % 2) * (direction - 1) * speed;
			dy = (direction % 2) * (direction - 2) * speed;
		}
	}
	private void turnRight() {
		if (checkLastTurnTime()) {
			direction= (this.direction + 1) % 4;
			dx = ((direction + 1) % 2) * (direction - 1) * speed;
			dy = (direction % 2) * (direction - 2) * speed;
		}
	}

	// the head shall advance, as well as the tail
	public void advance()
	{
		// dead or immobile snakes don't move!!!
		if (!isAlive || speed == 0) {
			return;
		}
		
		if (turnLeft && !turnRight) {
			turnLeft();
		}
		if (turnRight && !turnLeft) {
			turnRight();
		}
		if (turnLeft ^ turnRight) {
			Pointd newHead = (Pointd) points.getFirst().clone();
			points.addFirst(newHead);
		}
		
		// the head
		{
		Pointd head = points.getFirst();
		head.x += dx;
		head.y += dy;
		}

		// the tail
		double s = speed; // we have to remove s units (int?)
		if (toGrow > 0) {
			if (toGrow <= speed) {
				toGrow = 0;
				s = speed - toGrow;
			}
			else {
				toGrow -= speed;
			}
		}
		else if (toGrow <= 0) {
			if (toGrow < 0) { // shorten more
				double maxNegGrowth = -5;
				if (toGrow >= maxNegGrowth) {
					s -= toGrow; 
					toGrow = 0;
				}
				else {
					s -= maxNegGrowth;
					toGrow -= maxNegGrowth;
				}
			}
			
			// remove as many points at the end and shorten as needed
			Iterator<Pointd> it = points.descendingIterator();
			Pointd p1 = it.next(), p2 = it.next();
			int toRem = 0; // how many points to remove? (impossible while traversing)
			do {
				int d = Arith.dir(p2, p1);
				int l = (int)((p1.x - p2.x) + (p1.y - p2.y));
				l = l < 0 ? -l : l;
				if (l >= s) { // anything remains in the last segment?
					p1.x += ((d + 1) % 2) * (d - 1) * s;
					p1.y += (d % 2) * (2 - d) * s;
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

	public void computeHull() {
		LinkedList<Pointd> newHull = new LinkedList<Pointd>();

		// do a first forward traversal, then a backward traversal of points
		Iterator<Pointd> it_pts = points.iterator();
		for (int i=0; i<2; ++i) {
			Pointd p1 = it_pts.next(), p2 = it_pts.next(), p3;
			
			Vector2D v = new Vector2D(p1, p2);
			Vector2D v_norm = v.normal();
			newHull.add(v_norm.add(p1));
			while (it_pts.hasNext()) {
				p3 = it_pts.next();
				
				
				// TODO not correct for now
				boolean turn_right;
				if (p1.x == p2.x) {
					turn_right = (p2.y - p1.y)*(p3.x - p2.x) > 0;
				}
				else {
					turn_right = (p2.x - p1.x) * (p3.y - p2.y) < 0; 
				}
				Pointd p;
				if (turn_right) { // turning right
					v = new Vector2D(p2, p1).add(new Vector2D(p2, p3));
					p = v.add(p2);
					newHull.add(p);
				}
				else { // turning left
					v = new Vector2D(p3, p2);
					p = v.add(p2);
					newHull.add(p);
					v = new Vector2D(p1, p2);
					p = v.add(p2);
					newHull.add(p);
				}
				p1 = p2;
				p2 = p3;
			}
			v = new Vector2D(p1, p2);
			newHull.add(v.add(p2));
			it_pts = points.descendingIterator(); // for the 2nd traversal
		}
		
		// now actually update the variables
		hull_points = newHull;
		hull = new Polygon();
		it_pts = hull_points.iterator();
		while (it_pts.hasNext()) {
			Pointd p = it_pts.next();
			hull.addPoint((int)p.x, (int)p.y);
		}
		
		throw new UnsupportedOperationException("TODO: computeHull() for classic mode");
	}
	
}
