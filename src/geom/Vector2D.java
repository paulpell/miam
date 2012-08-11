package geom;

public class Vector2D {
	private double x, y;
	
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
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
	
	public Vector2D normal() {
		return multiply(1 / length());
	}
	public void normalizeChanging() {
		double l = length();
		x /= l;
		y /= l;
	}
}
