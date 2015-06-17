package org.paulpell.miam.geom;


import java.awt.Graphics2D;

import org.paulpell.miam.logic.Arith;


public class Segment extends Line
{
	
	public Segment(Pointd p1, Pointd p2)
	{
		super(p1, new Vector2D(p1, p2));
	}
	
	public boolean isPointInside(Pointd p)
	{
		if (p.equals(p1_) || p.equals(p2_))
			return true;

		double dx1 = p1_.x_ - p.x_;
		double dx2 = p1_.x_ - p2_.x_;
		double t;
		if (Arith.equalsd(0, dx2))
		{
			double dy1 = p1_.y_ - p.y_;
			double dy2 = p1_.y_ - p2_.y_;
			
			if (Arith.equalsd(0, dy2))
				throw new UnsupportedOperationException("Segment: p1_ equals p2_!!");

			t = dy1 / dy2;
			if (t >= 0 
					&& t <= 1
					&& Arith.equalsd(getPointOn(t).x_, p.x_))
				return true;

		}
		else
		{
			t = dx1 / dx2;
			if (t >= 0 
					&& t <= 1
					&& Arith.equalsd(getPointOn(t).y_, p.y_))
				return true;
		}
		return false;
	}
	
	

	// use Line intersection, then check if the point is on the segment
	@Override
	public Pointd intersect(Line other)
	{
		Pointd p = super.intersect(other);
		if (null == p)
			return null;
		
		if (isPointInside(p))
			return p;

		return null;
	}
	
	public Pointd intersect(Segment seg)
	{
		Pointd p3 = seg.getP1();
		Pointd p4 = seg.getP2();
		
		// some simple tests to avoid calculations
		if (p1_.x_ < p3.x_ && p1_.x_ < p4.x_ && p2_.x_ < p3.x_ && p2_.x_ < p4.x_) return null;
		if (p1_.x_ > p3.x_ && p1_.x_ > p4.x_ && p2_.x_ > p3.x_ && p2_.x_ > p4.x_) return null;
		if (p1_.y_ < p3.y_ && p1_.y_ < p4.y_ && p2_.y_ < p3.y_ && p2_.y_ < p4.y_) return null;
		if (p1_.y_ > p3.y_ && p1_.y_ > p4.y_ && p2_.y_ > p3.y_ && p2_.y_ > p4.y_) return null;
		
		double dx = (p2_.x_ - p1_.x_);
		double dy = (p2_.y_ - p1_.y_);
		if (Arith.equalsd(0, dx))
			return intersectVerticalSegment(seg);

		if (Arith.equalsd(0, dy))
			return intersectHorizontalSegment(seg);

		double d1 = (p3.x_ - p1_.x_) / dx - (p3.y_ - p1_.y_) / dy;
		double d2 = (p4.y_ - p3.y_)/dy - (p4.x_ - p3.x_)/dx;
		double t = d1 / d2;
		if (t < 0 || t > 1)
			return null;

		return seg.getPointOn(t);
	}
	
	private Pointd intersectVerticalSegment(Segment other)
	{
		Pointd p3 = other.getP1(), p4 = other.getP2();
		double dx = p4.x_ - p3.x_;
		if (Arith.equalsd(0, dx)) // other also vertical
		{
			if (Arith.equalsd(p1_.x_, p3.x_)) { // must be on same x_ to intersect
				if ((p3.y_ <= p1_.y_ && p3.y_ >= p2_.y_) || (p3.y_ >= p1_.y_ && p3.y_ <= p2_.y_))
					return p3;
				
				if ((p4.y_ <= p1_.y_ && p4.y_ >= p2_.y_) || (p4.y_ >= p1_.y_ && p4.y_ <= p2_.y_))
					return p4;

				if ((p3.y_ <= p1_.y_ && p4.y_ >= p2_.y_) || (p3.y_ >= p1_.y_ && p4.y_ <= p2_.y_))
					return p1_;

			}
			return null;
		}
		double t = (p1_.x_ - p3.x_) / dx;
		Pointd p = other.getPointOn(t);
		if (t >= 0 && t <= 1 &&
				((p.y_ <= p1_.y_ && p.y_ >= p2_.y_)
						|| (p.y_ >= p1_.y_ && p.y_ <= p2_.y_)))
			return p;
		
		return null;
		
	}
	
	private Pointd intersectHorizontalSegment(Segment other)
	{
		Pointd p3 = other.getP1(), p4 = other.getP2();
		double dy = p4.y_ - p3.y_;
		if (Arith.equalsd(0, dy)) // other also horizontal
		{
			if (Arith.equalsd(p1_.y_, p3.y_))
			{
				if ((p3.x_ <= p1_.x_ && p3.x_ >= p2_.x_) || (p3.x_ >= p1_.x_ && p3.x_ <= p2_.x_))
					return p3;

				if ((p4.x_ <= p1_.x_ && p4.x_ >= p2_.x_) || (p4.x_ >= p1_.x_ && p4.x_ <= p2_.x_))
					return p4;

				if ((p4.x_ <= p1_.x_ && p3.x_ >= p2_.x_) || (p4.x_ >= p1_.x_ && p3.x_ <= p2_.x_))
					return p1_;
			}
			return null;	
		}
		
		double t = (p1_.y_ - p3.y_) / dy;
		Pointd p = other.getPointOn(t);
		if (t >= 0 && t <= 1 &&
				((p.x_ <= p1_.x_ && p.x_ >= p2_.x_)
						|| (p.x_ >= p1_.x_ && p.x_ <= p2_.x_)))
			return p;

		return null;
	}

	@Override
	public Pointd intersect(Circle other)
	{
		return other.intersect(this);
	}

	@Override
	public Pointd intersect(Rectangle other)
	{
		return other.intersect(this);
	}

	@Override
	public Pointd getPointd()
	{
		return p1_;
	}
	

	@Override
	public double minDistanceToPoint(Pointd p)
	{
		if (isPointColinear(p))
		{
			if (isPointInside(p))
				return 0;
			return Arith.mind(Arith.dist(p, p1_), Arith.dist(p, p2_));
		}
		
		// if not colinear, use line intersection
		Line other = new Line(p, getNormal());
		Line me = new Line(p1_, p2_);
		Pointd i = me.intersect(other);
		if (isPointColinear(i))
			return Arith.dist(p, i);
		return Arith.mind(Arith.dist(p, p1_), Arith.dist(p, p2_));
	}

	@Override
	public void draw(Graphics2D g)
	{
		g.drawLine((int)p1_.x_, (int)p1_.y_, (int)p2_.x_, (int)p2_.y_);
	}
	

	@Override
	public GeometricObject translate(Vector2D dv)
	{
		return new Segment(dv.add(p1_), dv.add(p2_));
	}
	
	public String toString()
	{
		return "Segment(" + p1_ + " - " + p2_ + ")";
	}
	
}
