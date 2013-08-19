package logic.draw.walls;

import geom.Pointd;

import java.awt.Graphics;
import java.util.Iterator;
import java.util.Vector;

import logic.draw.Drawable;
import logic.draw.snakes.Snake;


public class Wall extends Drawable {
	
	
	protected Vector<WallElement> elements = new Vector<WallElement>();
	
	public void addElement(WallElement el) {
		elements.add(0,el);
	}
	
	public void removeLastElement() {
		if (elements.size() > 0) {
			elements.remove(0);
		}
	}
	
	public void draw(Graphics g) {
		Iterator<WallElement> it = elements.iterator();
		for (; it.hasNext();) {
			System.out.print("i ");
			it.next().draw(g);
		}
		System.out.println();
	}

	public Pointd getPointd() {
		return new Pointd(0,0);
	}
	
	public boolean isPointInside(Pointd p) {
		return false;
	}
	
	public Pointd isSnakeColliding(Snake s) {
		Iterator<WallElement> it = elements.iterator();
		for (;it.hasNext();) {
			Pointd collide = it.next().isSnakeColliding(s);
			if (collide != null) {
				return collide;
			}
		}
		return null;
	}
}
