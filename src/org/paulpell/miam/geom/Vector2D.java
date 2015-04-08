package org.paulpell.miam.geom;

/**
 * A 2D double vector
 *
 */

public class Vector2D {
	private double x, y;
	
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Creates a vector from p1 to p2, in that direction.
	 * @param p1 the initial point
	 * @param p2 the end point
	 */
	public Vector2D(Pointd p1, Pointd p2) {
		this.x = p2.x_ - p1.x_;
		this.y = p2.y_ - p1.y_;
	}
	
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public double length() {
		return Math.sqrt(x*x + y*y);
	}
	
	public Pointd add(Pointd p) {
		return new Pointd(p.x_ + x, p.y_ + y);
	}
	
	public void multiplyChanging(double d) {
		x *= d; y *= d;
	}
	public Vector2D multiply(double d) {
		return new Vector2D(x*d, y*d);
	}
	
	/**
	 * Return a vector tangent to this: (x_,y_) will give (-y_,x_).
	 * @return a vector tangent to this
	 */
	public Vector2D normal() {
		//return multiply(1 / length());
		return new Vector2D(-y ,x);
	}
	public void normalize() {
		double l = length();
		x /= l;
		y /= l;
	}

	public Vector2D add(Vector2D v) {
		return new Vector2D(x + v.x, y + v.y);
	}
	
	@Override
	public String toString()
	{
		return "V2D(" + x + "," + y + ")";
		
	}
}
