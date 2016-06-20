package org.paulpell.miam.geom;

import java.awt.Graphics2D;

import org.paulpell.miam.logic.Arith;

/*
 * Points on rectangle are defined as follows:
 * 
 * 
 * 1----3
 * |    |
 * 4----2
 *
 */

public class Rectangle extends GeometricObject
{
	
	private Pointd p1_;
	private Pointd p2_; // the opposite corner
	private Pointd p3_;
	private Pointd p4_;
	protected Pointd[] points_;
	private Segment[] segments_;
	private double width_;
	private double height_;
	private boolean fill_;
	
	public Rectangle(double x, double y, double w, double h, boolean fill)
	{
		init(x, y, w, h, fill);
	}
	
	public Rectangle(double x, double y, double w, double h)
	{
		init(x, y, w, h, false);
	}
	
	public Rectangle(Pointd p1, Pointd p2, boolean fill)
	{
		double x = Arith.mind(p1.x_, p2.x_);
		double y = Arith.mind(p1.y_, p2.y_);
		double w = Arith.absd(p1.x_ - p2.x_);
		double h = Arith.absd(p1.y_ - p2.y_);
		init(x, y, w, h, fill);
	}
	
	private void init(double x, double y, double w, double h, boolean fill)
	{
		//if (w <= 0 || h <= 0)
		//	throw new IllegalArgumentException("Width ("+w+") and height ("+h+") cannot be 0");
		if (w <= 0)
			w = 1;
		
		if (h <= 0)
			h = 1;
		
		width_ = w;
		height_ = h;
		p1_ = new Pointd(x,y);
		p2_ = new Pointd(x + width_, y + height_);
		p3_ = new Pointd(x + width_, y);
		p4_ = new Pointd(x, y + height_);

		points_ = new Pointd [] { p1_, p2_, p3_, p4_ };
		
		segments_ = new Segment[]
				{ new Segment(p1_, p3_),
				  new Segment(p3_, p2_),
				  new Segment(p2_, p4_),
				  new Segment(p4_, p1_)};
		fill_ = fill;
	}
	
	public Pointd getP1()
	{
		return p1_;
	}
	@Override
	public Pointd getPointd()
	{
		return p1_;
	}
	public Pointd getP2()
	{
		return p2_;
	}
	public Pointd getP3()
	{
		return p3_;
	}
	public Pointd getP4()
	{
		return p4_;
	}
	
	public double getWidth()
	{
		return width_;
	}
	
	public double getHeight()
	{
		return height_;
	}

	@Override
	public Pointd intersect(Segment other)
	{
		Pointd p, p1 = other.getP1(), p2 = other.getP2();
		// if entirely inside, return a point
		if (isPointInside(p1) && isPointInside(p2))
			return p1;

		// or any side intersecting?
		for (int i=0; i<segments_.length; ++i)
		{
			p = segments_[i].intersect(other);
			if (p != null)
				return p;
		}
		return null;
	}
	
	@Override
	public Pointd intersect(Line other)
	{
		// any side intersecting?
		for (int i=0; i<segments_.length; ++i)
		{
			Pointd p = segments_[i].intersect(other);
			if (p != null)
				return p;
		}
		return null;
	}

	@Override
	public Pointd intersect(Circle other)
	{
		Pointd p;
		// any line intersecting?
		for (int i=0; i<segments_.length; ++i)
		{
			p = other.intersect(segments_[i]);
			if (p != null)
				return p;
		}
		// circle inside?
		p = other.getPoint();
		if (isPointInside(p))
			return p;
		
		return null;
	}

	@Override
	public Pointd intersect(Rectangle other)
	{
		Pointd p, p2;
	
		// inside?
		p = other.p1_;
		p2 = other.p2_; // corner opposite to p
		if (isPointInside(p) && isPointInside(p2))
			return p;
		
		// any line intersecting?
		for (int i=0; i<segments_.length; ++i)
		{
			p = other.intersect(segments_[i]);
			if (p != null)
				return p;
		}
		
		return null;
	}
	

	@Override
	public void draw(Graphics2D g)
	{
		if (fill_)
			g.fillRect((int)p1_.x_, (int)p1_.y_, (int)width_, (int)height_);
		else
			g.drawRect((int)p1_.x_, (int)p1_.y_, (int)width_, (int)height_);
	}

	@Override
	public boolean isPointInside(Pointd p)
	{
		return p.x_ >= p1_.x_
				&& p.x_ <= p2_.x_
				&& p.y_ >= p1_.y_
				&& p.y_ <= p2_.y_;
	}

	@Override
	public double minDistanceToPoint(Pointd p)
	{
		double min = Double.MAX_VALUE;
		for (Segment s : segments_)
		{
			double d = s.minDistanceToPoint(p);
			if (d == 0)
				return 0;
			if (d < min)
				min = d;
		}
		return min;
	}

	@Override
	public GeometricObject translate(Vector2D dv)
	{
		Pointd p = dv.add(p1_);
		return new Rectangle(p.x_, p.y_, width_, height_, fill_);
	}
	
	public boolean isFilled()
	{
		return fill_;
	}

	@Override
	public GeometricObject clone()
	{
		return new Rectangle(p1_.clone(), p2_.clone(), fill_);
	}
	
	@Override
	public String toString()
	{
		return "Rect("+p1_+"->"+p2_+")";
	}

}
