package geom;

public class Pointd {
	public double x, y;
	public Pointd(double x0, double y0) {
		x = x0;
		y = y0;
	}
	
	public Object clone() {
		return new Pointd(x,y);
	}
	
	public String toString() {
		return "Pointd("+x+","+y+")";
	}
	
	public boolean equals(Pointd other) {
		return other.x == x && other.y == y;
	}
}
