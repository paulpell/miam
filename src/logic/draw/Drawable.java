package logic.draw;
import geom.Pointd;

import java.awt.Graphics;
//import java.awt.Point;


// to put them in a TreeSet
public abstract class Drawable implements Comparable<Drawable> {
	public abstract void draw(Graphics g);
	public abstract Pointd getPointd();
	public abstract boolean isPointInside(Pointd p);
	public abstract boolean isSnakeColliding(Snake s);
	public int compareTo(Drawable other) {
		Pointd p1 = getPointd(), p2 = other.getPointd();
		if (p1.x < p2.x
			|| (p1.x == p2.x && p1.y < p2.y)) return -1;
		
		if (p1.x == p2.x && p1.y == p2.y) return 0; 
		
		return 1;
	}
}
