package org.paulpell.miam.geom;

import java.awt.Point;

import org.paulpell.miam.logic.Arith;

public class Pointd
{
	
	public double x_;
	public double y_;
	
	public Pointd(double x0, double y0)
	{
		x_ = x0;
		y_ = y0;
	}
	
	public Point toAWTpt()
	{
		return new Point((int)x_,(int)y_);
	}
	
	public Object clone()
	{
		return new Pointd(x_,y_);
	}
	
	public String toString()
	{
		return "P2D("+x_+","+y_+")";
	}
	
	public boolean equals(Pointd other)
	{
		return Arith.equalsd(other.x_, x_) && Arith.equalsd(other.y_, y_);
	}

}
