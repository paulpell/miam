package geom;

import java.awt.Graphics;

import logic.Arith;

public class Circle extends GeometricObject {

	
	private double radius;
	private Pointd position;
	
	public Circle(Pointd p, double r) {
		position = p;
		radius = r;
	}
	
	public Pointd getPoint() {
		return position;
	}
	public double getRadius() {
		return radius;
	}

	@Override
	public Pointd intersect(Line other) {
		// first find the nearest point to position on other
		Vector2D norm = other.getNormal();
		Pointd p1 = norm.multiply(100).add(position);
		Pointd p2 = norm.multiply(-100).add(position);
		Line normLine = new Line(p1, p2);
		Pointd inter = other.intersect(normLine);
		if (inter == null) { // then other does not cross normLine,
				// but maybe it still intersects. TODO, find correct point
			double d = Arith.dist(position, p1);
			if (d <= radius) {
				return p1;
			}
			d = Arith.dist(position, p2);
			if (d <= radius) {
				return p2;
			}
			return null;
		}
		// now check if the line is further than the radius
		double len = Arith.dist(inter, position);
		if (len > radius) {
			return null;
		}
		normLine = new Line(position, inter);
		double ratio = radius / len;
		return normLine.getPoint(ratio);
	}

	@Override
	public Pointd intersect(Circle other) {
		Pointd otherPos = other.getPoint();
		double otherRad = other.getRadius();
		double radSum = radius + otherRad;
		
		double dx = otherPos.x - position.x,
				dy = otherPos.y - position.y;
		Vector2D centersDiff = new Vector2D(dx, dy);
		double dv = centersDiff.length();
		
		if (dv > radSum) {
			return null;
		}
		else { // whether tangent intersection or not, we return some "central" point
			// TODO, maybe do proper work
			double ratio = radius / radSum;
			centersDiff.multiplyChanging(ratio);
			return centersDiff.add(position);
		}
	}

	@Override
	public Pointd intersect(Rectangle other) {
		return other.intersect(this);
	}

	@Override
	public Pointd getPointd() {
		return position;
	}

	@Override
	public void draw(Graphics g) {
		int x = (int)(position.x - radius),
			y = (int)(position.y - radius),
			w = (int)(radius / 2);
		g.drawOval(x, y, w, w);
	}

	@Override
	public boolean isPointInside(Pointd p) {
		return Arith.dist(p, position) <= radius;
	}
	
}
