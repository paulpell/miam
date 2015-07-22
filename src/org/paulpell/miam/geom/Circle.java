package org.paulpell.miam.geom;


import java.awt.Graphics2D;

import org.paulpell.miam.logic.Arith;


public class Circle extends GeometricObject
{

	
	protected double radius_;
	protected Pointd position_;
	
	public Circle(Pointd p, double r)
	{
		position_ = p;
		radius_ = r;
	}
	
	public Pointd getPoint()
	{
		return position_;
	}
	public double getRadius()
	{
		return radius_;
	}

	@Override
	public Pointd intersect(Segment seg)
	{
		Line l = new Line(seg.p1_, seg.p2_);
		Pointd p = intersect(l);
		if (null == p)
			return null;
		
		if (seg.isPointInside(p))
			return p;
		return null;
	}

	@Override
	public Pointd intersect(Line other)
	{
		Line myline = new Line(position_, other.getNormal());
		Pointd p = myline.intersect(other);
		if (p == null)
			throw new UnsupportedOperationException("There must be an intersection!");
		
		double d = Arith.dist(p, position_);
		
		if (Arith.equalsd(d, radius_))
			return p;
		else if (d > radius_)
			return null;
		
		// factor to add line tangent vector to p, to be on the radius
		double f = Math.sqrt(radius_*radius_ - d*d);
		Vector2D v = other.getTangent().normalized().multiply(f);
		return v.add(p);
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
	public Pointd intersect(Rectangle rect)
	{
		if (isPointInside(rect.getP1())
				&& isPointInside(rect.getP2()))
			return rect.getP1();
		return rect.intersect(this);
	}

	@Override
	public Pointd getPointd()
	{
		return position_;
	}

	@Override
	public void draw(Graphics2D g)
	{
		int x = (int)(position_.x_ - radius_);
		int y = (int)(position_.y_ - radius_);
		int w = 2 * (int)radius_;
		g.drawOval(x, y, w, w);
	}

	@Override
	public boolean isPointInside(Pointd p)
	{
		return Arith.dist(p, position_) <= radius_;
	}

	@Override
	public double minDistanceToPoint(Pointd p)
	{
		return Arith.absd(Arith.dist(p, position_) - radius_);
	}

	@Override
	public GeometricObject translate(Vector2D dv)
	{
		return new Circle(dv.add(position_), radius_);
	}

	@Override
	public GeometricObject clone()
	{
		return new Circle(position_.clone(), radius_);
	}
}
