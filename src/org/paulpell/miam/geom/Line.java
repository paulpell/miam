package org.paulpell.miam.geom;


import java.awt.Graphics2D;

import org.paulpell.miam.logic.Arith;

public class Line extends GeometricObject
{
	
	final Pointd p1_;
	final Pointd p2_;
	final Vector2D v_;
	
	final boolean isHorizontal_;
	final boolean isVertical_;
	
	// for the line representation y = ax + b. We use it only
	// if not vertical or horizontal, so it's good enough =)
	private final double a_;
	private final double b_;

	public Line(Pointd p, Vector2D v)
	{
		p1_ = p;
		v_ = v;
		p2_ = v_.add(p1_);
		isHorizontal_ = Arith.equalsd(p1_.y_, p2_.y_);
		isVertical_ = Arith.equalsd(p1_.x_, p2_.x_);
		
		if (isHorizontal_ || isVertical_)
		{
			a_ = b_ = 0; // don't care
		}
		else
		{
			// a = dy/dx, use p1 to find b
			a_ = (p1_.y_ - p2_.y_) / (p1_.x_ - p2_.x_);
			b_ = p1_.y_ - a_ * p1_.x_;
		}
	}
	

	public Line(Pointd p1, Pointd p2)
	{
		this(p1, new Vector2D(p1, p2));
	}
	
	public Pointd getP1()
	{
		return p1_;
	}
	public Pointd getP2()
	{
		return p2_;
	}

	// linear, t=0 returns pSel1_ and t=1, pSel2_
	public Pointd getPointOn(double t)
	{
		double x = t * (p2_.x_ - p1_.x_) + p1_.x_;
		double y = t * (p2_.y_ - p1_.y_) + p1_.y_;
		return new Pointd(x,y);
	}
	
	@Override
	public Pointd getPointd()
	{
		return p1_;
	}

	@Override
	public boolean isPointInside(Pointd p)
	{
		return isPointColinear(p);
	}

	@Override
	public Pointd intersect(Line other)
	{
		if (other.isHorizontal_ && isHorizontal_)
		{
			if (Arith.equalsd(p1_.y_, other.p1_.y_))
				return p1_;
			return null;
		}
		
		if (other.isVertical_ && isVertical_)
		{
			if (Arith.equalsd(p1_.x_, other.p1_.x_))
				return p1_;
			return null;
		}
		
		if (other.isVertical_ && isHorizontal_)
			return new Pointd(other.p1_.x_, p1_.y_);
		
		if (other.isHorizontal_ && isVertical_)
			return new Pointd(p1_.x_, other.p1_.y_);
		
		// parallel lines
		if (Arith.equalsd(other.a_, a_))
		{
			if (Arith.equalsd(other.b_, b_))
				return p1_;
			return null;
		}
		
		// otherwise...
		double x = (b_ - other.b_) / (other.a_ - a_);
		double y = a_ * x + b_;
		return new Pointd(x, y);
	}

	@Override
	public Pointd intersect(Segment other)
	{
		return other.intersect(this);
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
	public void draw(Graphics2D g)
	{
		/*java.awt.Rectangle clipRect = g.getClipBounds();
		if (isHorizontal_)
		{
			int y = (int)pSel1_.y_;
			g.drawLine(0, y, clipRect.width, y);
		}
		else if (isVertical_)
		{
			int y = (int)pSel1_.y_;
			g.drawLine(y, 0, y, clipRect.width);
		}
		else
		{
			
		}*/
		throw new UnsupportedOperationException("Cannot draw Line!");
	}
	
	public boolean isPointColinear(Pointd p)
	{
		Pointd p2 = v_.add(p1_);
		if (p.equals(p1_) || p.equals(p2))
			return true;
		
		double d1 = (p.x_ - p1_.x_) / (p2.x_ - p1_.x_);
		double d2 = (p.y_ - p1_.y_) / (p2.y_ - p1_.y_);
		return Arith.equalsd(d1, d2);
	}

	
	public Vector2D getNormal()
	{
		return v_.normal();
	}
	
	public Vector2D getTangent()
	{
		return v_.clone();
	}


	@Override
	public double minDistanceToPoint(Pointd p)
	{
		if (isPointColinear(p))
			return 0;
		
		Line other = new Line(p, getNormal());
		Pointd i = intersect(other);
		return Arith.dist(p, i);
	}


	@Override
	public GeometricObject translate(Vector2D dv)
	{
		return new Line(dv.add(p1_), v_);
	}


	@Override
	public GeometricObject clone()
	{
		return new Line(p1_.clone(), p2_.clone());
	}
	
	@Override
	public String toString()
	{
		return "Line("+p1_+"->"+p2_+")";
	}

}
