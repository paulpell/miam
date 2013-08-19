package geom;

import java.awt.Point;
import logic.Arith;

public class Pointd {
	
	public double x, y;
	
	public Pointd(double x0, double y0) {
		x = x0;
		y = y0;
	}
	
	public Point toAWTpt() {
		return new Point((int)x,(int)y);
	}
	
	public Object clone() {
		return new Pointd(x,y);
	}
	
	public String toString() {
		return "("+x+","+y+")";
	}
	
	public boolean equals(Pointd other) {
		return Arith.equalsd(other.x, x) && Arith.equalsd(other.y, y);
	}

}
