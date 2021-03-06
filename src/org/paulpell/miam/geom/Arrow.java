package org.paulpell.miam.geom;

import java.awt.Graphics2D;

import org.paulpell.miam.logic.Arith;

public class Arrow extends GeometricObject
{
	Pointd p1_;
	Pointd p2_;
	double angle_;
	double length_;
	Segment[] segments_;
	
	// angle in radians
	public Arrow(Pointd p, Pointd p2)
	{
		p1_ = p;
		double angle = Arith.dirModern(p, p2) * Math.PI / 180.;
		double length = Arith.dist(p, p2);
		init(p2, angle, length);
	}
	
	public Arrow(Pointd p, double angle, double length)
	{
		p1_ = p;
		Vector2D v1 = new Vector2D(length, 0).rotate(angle);
		Pointd p2 = v1.add(p1_);
		init(p2, angle, length);
	}
	
	private void init(Pointd p2, double angle, double length)
	{
		p2_ = p2;
		angle_ = angle;
		length_ = length;
		
		Vector2D v2 = new Vector2D(-5,  5).rotate(angle);
		Vector2D v3 = new Vector2D(-5, -5).rotate(angle);
		
		Pointd p3 = v2.add(p2_);
		Pointd p4 = v3.add(p2_);
		
		segments_ = new Segment[]
				{
					new Segment(p1_, p2_),
					new Segment(p2_, p3),
					new Segment(p2_, p4)
				};
	}

	@Override
	public Pointd getPointd()
	{
		return p1_;
	}

	@Override
	public boolean isPointInside(Pointd p)
	{
		for (Segment s: segments_)
			if (s.isPointInside(p))
				return true;
		return false;
	}

	@Override
	public Pointd intersect(Segment other)
	{
		Pointd p = null;
		for (Segment s: segments_)
			if (null != (p = s.intersect(other)))
				return p;
		return null;
	}

	@Override
	public Pointd intersect(Line other)
	{
		Pointd p = null;
		for (Segment s: segments_)
			if (null != (p = s.intersect(other)))
				return p;
		return null;
	}

	@Override
	public Pointd intersect(Circle other)
	{
		Pointd p = null;
		for (Segment s: segments_)
			if (null != (p = s.intersect(other)))
				return p;
		return null;
	}

	@Override
	public Pointd intersect(Rectangle other)
	{
		Pointd p = null;
		for (Segment s: segments_)
			if (null != (p = s.intersect(other)))
				return p;
		return null;
	}

	@Override
	public double minDistanceToPoint(Pointd p)
	{
		double d;
		double min = Double.MAX_VALUE;
		for (Segment s: segments_)
			if (min > (d = s.minDistanceToPoint(p)))
				min = d;
		return min;
	}

	@Override
	public void draw(Graphics2D g)
	{
		for (Segment s : segments_)
			s.draw(g);
	}

	@Override
	public GeometricObject translate(Vector2D dv)
	{
		return new Arrow(dv.add(p1_), angle_, length_);
	}

	@Override
	public Arrow clone()
	{
		return new Arrow(p1_.clone(), angle_, length_);
	}
	
	@Override
	public String toString()
	{
		return "Arrow("+p1_+"->"+p2_+")";
	}

}
