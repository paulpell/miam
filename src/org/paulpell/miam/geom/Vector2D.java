package org.paulpell.miam.geom;

import org.paulpell.miam.logic.Arith;

/**
 * A 2D double vector
 *
 */

public class Vector2D
{
	private double x_;
	private double y_;
	
	public Vector2D(double x, double y)
	{
		this.x_ = x;
		this.y_ = y;
	}
	
	/**
	 * Creates a vector from p1 to p2, in that direction.
	 * @param p1 the initial point
	 * @param p2 the end point
	 */
	public Vector2D(Pointd p1, Pointd p2)
	{
		this.x_ = p2.x_ - p1.x_;
		this.y_ = p2.y_ - p1.y_;
	}
	
	public double getX()
	{
		return x_;
	}
	public double getY()
	{
		return y_;
	}
	public void setX(double x)
	{
		x_ = x;
	}
	public void setY(double y)
	{
		y_ = y;
	}
	public double length()
	{
		return Math.sqrt(x_*x_ + y_*y_);
	}
	
	public Pointd add(Pointd p)
	{
		return new Pointd(p.x_ + x_, p.y_ + y_);
	}
	
	public void multiplyChanging(double d)
	{
		x_ *= d; y_ *= d;
	}
	public Vector2D multiply(double d)
	{
		return new Vector2D(x_*d, y_*d);
	}
	
	/**
	 * Return a vector tangent to this: (x_,y_) will give (-y_,x_).
	 * @return a vector tangent to this
	 */
	public Vector2D normal()
	{
		return new Vector2D(-y_ ,x_);
	}
	public void normalize()
	{
		double l = length();
		x_ /= l;
		y_ /= l;
	}

	public Vector2D normalized()
	{
		Vector2D v = clone();
		v.normalize();
		return v;
	}

	public Vector2D add(Vector2D v)
	{
		return new Vector2D(x_ + v.x_, y_ + v.y_);
	}
	
	// angle in radians
	public Vector2D rotate(double angle)
	{
		double a = Arith.dirModern(new Pointd(0,0), new Pointd(x_, y_)) * Math.PI / 180.;
		double angle2 = a + angle;
		double l = length();
		double x = l * Math.cos(angle2);
		double y = l * Math.sin(angle2);
		return new Vector2D(x, y);
	}
	
	@Override
	public String toString()
	{
		return "V2D(" + x_ + "," + y_ + ")";
		
	}
	
	public Vector2D clone()
	{
		return new Vector2D(x_, y_);
	}
}
