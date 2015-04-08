package org.paulpell.miam.geom;

import java.awt.Graphics;

import org.paulpell.miam.logic.Arith;


public class Line extends GeometricObject
{
	private Pointd p1_;
	private Pointd p2_;
	
	public Line(Pointd p1, Pointd p2)
	{
		this.p1_ = p1;
		this.p2_ = p2;
	}
	
	public Pointd getP1() {
		return p1_;
	}
	public Pointd getP2() {
		return p2_;
	}
	
	public Vector2D getNormal()
	{
		double dx = p1_.x_ - p2_.x_;
		double dy = p1_.y_ - p2_.y_;
		return new Vector2D(dy, -dx);
	}
	
	public Vector2D getTangent()
	{
		double dx = p1_.x_ - p2_.x_;
		double dy = p1_.y_ - p2_.y_;
		return new Vector2D(dx, dy);
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
				throw new UnsupportedOperationException("Line: p1_ equals p2_!!");

			t = dy1 / dy2;
			if (Arith.equalsd(getPointOn(t).x_, p.x_))
				return true;

		}
		else
		{
			t = dx1 / dx2;
			if (Arith.equalsd(getPointOn(t).y_, p.y_))
				return true;
		}
		return false;
	}
	
	// linear, t=0 returns p1_ and t=1, p2_
	public Pointd getPointOn(double t)
	{
		double x = t * (p2_.x_ - p1_.x_) + p1_.x_;
		double y = t * (p2_.y_ - p1_.y_) + p1_.y_;
		return new Pointd(x,y);
	}
	
	
	public Pointd intersect(Line other)
	{
		Pointd p3 = other.getP1();
		Pointd p4 = other.getP2();
		
		// some simple tests to avoid calculations
		if (p1_.x_ < p3.x_ && p1_.x_ < p4.x_ && p2_.x_ < p3.x_ && p2_.x_ < p4.x_) return null;
		if (p1_.x_ > p3.x_ && p1_.x_ > p4.x_ && p2_.x_ > p3.x_ && p2_.x_ > p4.x_) return null;
		if (p1_.y_ < p3.y_ && p1_.y_ < p4.y_ && p2_.y_ < p3.y_ && p2_.y_ < p4.y_) return null;
		if (p1_.y_ > p3.y_ && p1_.y_ > p4.y_ && p2_.y_ > p3.y_ && p2_.y_ > p4.y_) return null;
		
		double dx = (p2_.x_ - p1_.x_);
		double dy = (p2_.y_ - p1_.y_);
		if (Arith.equalsd(0, dx))
			return intersectVertical(other);

		if (Arith.equalsd(0, dy))
			return intersectHorizontal(other);

		double d1 = (p3.x_ - p1_.x_) / dx - (p3.y_ - p1_.y_) / dy;
		double d2 = (p4.y_ - p3.y_)/dy - (p4.x_ - p3.x_)/dx;
		double t2 = d1 / d2;
		if (t2 < 0 || t2 > 1)
			return null;

		return other.getPointOn(t2);
	}
	
	private Pointd intersectVertical(Line other)
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
		double t = (p4.x_ - p1_.x_) / dx;
		Pointd p = other.getPointOn(t);
		if (t >= 0 && t <= 1 &&
				((p.y_ <= p1_.y_ && p.y_ >= p2_.y_)
						|| (p.y_ >= p1_.y_ && p.y_ <= p2_.y_)))
			return p;
		
		return null;
		
	}
	
	private Pointd intersectHorizontal(Line other)
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
		double t = (p4.y_ - p1_.y_) / dy;
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
	public void draw(Graphics g)
	{
		g.drawLine((int)p1_.x_, (int)p1_.y_, (int)p2_.x_, (int)p2_.y_);
	}
	
}
