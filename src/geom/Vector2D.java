package geom;

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
		this.x = p2.x - p1.x;
		this.y = p2.y - p1.y;
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
		return new Pointd(p.x + x, p.y + y);
	}
	
	public void multiplyChanging(double d) {
		x *= d; y *= d;
	}
	public Vector2D multiply(double d) {
		return new Vector2D(x*d, y*d);
	}
	
	/**
	 * Return a vector tangent to this: (x,y) will give (-y,x).
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
}
