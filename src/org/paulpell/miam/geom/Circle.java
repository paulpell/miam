package org.paulpell.miam.geom;

import java.awt.Graphics;

import org.paulpell.miam.logic.Arith;


public class Circle extends GeometricObject
{

	
	private double radius_;
	private Pointd position_;
	
	public Circle(Pointd p, double r)
	{
		position_ = p;
		radius_ = r;
	}
	
	public Pointd getPoint() {
		return position_;
	}
	public double getRadius() {
		return radius_;
	}

	@Override
	public Pointd intersect(Line line)
	{
		double d1 = Arith.dist(position_, line.getP1());
		double d2 = Arith.dist(position_, line.getP2());
		if (d1 < radius_ && d2 < radius_) // line inside => no intersection
			return null;
		
		Vector2D norm = line.getNormal();
		Pointd pi1 = norm.multiply(100).add(position_);
		Pointd pi2 = norm.multiply(-100).add(position_);
		Line normLine = new Line(pi1, pi2);
		Pointd inter = line.intersect(normLine);
		
		// no intersection
		if (null == inter)
			return null;
		
		// tangent => single point
		if (Arith.equalsd(radius_, Arith.dist(inter, position_)))
			return inter;
		
		// otherwise, return one of the two intersection points...
		// by default the one closer to p1, but if p1 is inside, the other one
		double dpi = Arith.dist(position_, inter);
		double dic = Math.sqrt(radius_*radius_ - dpi * dpi); // distance from inter to the circle border

		Pointd p1 = line.getP1();
		Pointd p2 = line.getP2();
		Pointd lp = p1;
		if (d1 < radius_ && d2 > radius_)
			lp = p2;
		
		double dlpi = Arith.dist(lp, inter);
		double dp1p2 = Arith.dist(p1, p2);
		double t = (dlpi - dic) / dp1p2;
		if (lp == p2)
			t = 1 - t;
		return line.getPointOn(t);
	}

	@Override
	public Pointd intersect(Circle other)
	{
		Pointd otherPos = other.getPoint();
		double otherRad = other.getRadius();
		double radSum = radius_ + otherRad;
		
		double dx = otherPos.x_ - position_.x_;
		double dy = otherPos.y_ - position_.y_;
		Vector2D centersDiff = new Vector2D(dx, dy);
		double dv = centersDiff.length();
		
		if (dv > radSum)
			return null;
		
		// whether tangent intersection or not, we return some "central" point
		// TODO, maybe do proper work
		double ratio = radius_ / radSum;
		centersDiff.multiplyChanging(ratio);
		return centersDiff.add(position_);
	}

	@Override
	public Pointd intersect(Rectangle other) {
		return other.intersect(this);
	}

	@Override
	public Pointd getPointd() {
		return position_;
	}

	@Override
	public void draw(Graphics g)
	{
		int x = (int)(position_.x_ - radius_);
		int y = (int)(position_.y_ - radius_);
		int w = (int)(radius_ / 2);
		g.drawOval(x, y, w, w);
	}

	@Override
	public boolean isPointInside(Pointd p) {
		return Arith.dist(p, position_) <= radius_;
	}
	
}
