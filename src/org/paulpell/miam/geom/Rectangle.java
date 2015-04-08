package org.paulpell.miam.geom;

import java.awt.Graphics;

public class Rectangle extends GeometricObject {
	
	Pointd position, otherCorner; // the two opposite corners
	Line[] lines;
	int width, height;
	
	public Rectangle(double x, double y, int w, int h) {
		width = w; height = h;
		position = new Pointd(x,y);
		Pointd b = new Pointd(x + w, y),
				c = new Pointd(x + w, y + h),
				d = new Pointd(x, y + h);
		lines = new Line[]
				{ new Line(position, b),
				  new Line(b, c),
				  new Line(c,d),
				  new Line(d, position)};
		otherCorner = c;
	}
	
	public Pointd getPoint() {
		return position;
	}
	@Override
	public Pointd getPointd() {
		return position;
	}
	public Pointd getPoint2() {
		return otherCorner;
	}

	@Override
	public Pointd intersect(Line other) {
		Pointd p, p1 = other.getP1(), p2 = other.getP2();
		// if entirely inside, return a point
		/*if (p1.x >= position.x
				&& p1.x <= otherCorner.x
				&& p1.y >= position.y
				&& p1.y <= otherCorner.y
				&& p2.x >= position.x
				&& p2.x <= otherCorner.x
				&& p2.y >= position.y
				&& p2.y <= otherCorner.y) {*/
		if (isPointInside(p1) && isPointInside(p2)) {
			return p1;
		}
		// or any side intersecting?
		for (int i=0; i<lines.length; ++i) {
			p = other.intersect(lines[i]);
			if (p != null) {
				return p;
			}
		}
		return null;
	}

	@Override
	public Pointd intersect(Circle other) {
		Pointd p;
		// any line intersecting?
		for (int i=0; i<lines.length; ++i) {
			p = other.intersect(lines[i]);
			if (p != null) {
				return p;
			}
		}
		// circle inside?
		p = other.getPoint();
		/*if (p.x >= position.x
				&& p.x <= otherCorner.x
				&& p.y >= position.y
				&& p.y <= otherCorner.y) {*/
		if (isPointInside(p)) {
			return p;
		}
		return null;
	}

	@Override
	public Pointd intersect(Rectangle other) {
		// any line intersecting?
		Pointd p, p2;
		for (int i=0; i<lines.length; ++i) {
			p = other.intersect(lines[i]);
			if (p != null) {
				return p;
			}
		}
		// inside?
		p = other.getPoint();
		p2 = other.getPoint2(); // corner opposite to p
		/*if (p.x >= position.x
				&& p.x <= otherCorner.x
				&& p.y >= position.y
				&& p.y <= otherCorner.y
				&& p2.x >= position.x
				&& p2.x <= otherCorner.x
				&& p2.y >= position.y
				&& p2.y <= otherCorner.y) {*/
		if (isPointInside(p) && isPointInside(p2)) {
			return p;
		}
		return null;
	}
	

	@Override
	public void draw(Graphics g) {
		g.fillRect((int)position.x_, (int)position.y_, (int)width, (int)height);
	}

	@Override
	public boolean isPointInside(Pointd p) {
		return p.x_ >= position.x_
				&& p.x_ <= otherCorner.x_
				&& p.y_ >= position.y_
				&& p.y_ <= otherCorner.y_;
	}

}
