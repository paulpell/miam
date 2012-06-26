package geom;

public class Line {
	private Pointd p1, p2;
	
	public Line(Pointd p1, Pointd p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public Pointd getP1() {
		return p1;
	}
	public Pointd getP2() {
		return p2;
	}
	
	public boolean isPointInside(Pointd p) {
		if (p.equals(p1) || p.equals(p2)) {
			return true;
		}
		double  dx1 = p1.x - p.x,
				dx2 = p1.x - p2.x,
				t;
		if (dx2 == 0) {
			double dy1 = p1.y - p.y,
					dy2 = p1.y - p2.y;
			if (dy2 == 0) {
				throw new UnsupportedOperationException("Line: p1 equals p2!!");
			}
			t = dy1 / dy2;
			if (getPoint(t).x == p.x) {
				return true;
			}
		}
		else {
			t = dx1 / dx2;
			if (getPoint(t).y == p.y) {
				return true;
			}
		}
		return false;
	}
	
	// t=0 returns p1 and t=1, p2
	public Pointd getPoint(double t) {
		double x = t * (p2.x - p1.x) + p1.x,
				y = t * (p2.y - p1.y) + p1.y;
		return new Pointd(x,y);
	}
	
	
	public Pointd intersect(Line other) {
		Pointd p3 = other.getP1(), p4 = other.getP2();
		//System.out.println("Line.intersect, p1=" + p1 + ", p2="+p2 + "p3="+ p3 + "p4" + p4);
		
		// some simple tests to avoid calculations
		if (p1.x < p3.x && p1.x < p4.x && p2.x < p3.x && p2.x < p4.x) return null;
		if (p1.x > p3.x && p1.x > p4.x && p2.x > p3.x && p2.x > p4.x) return null;
		if (p1.y < p3.y && p1.y < p4.y && p2.y < p3.y && p2.y < p4.y) return null;
		if (p1.y > p3.y && p1.y > p4.y && p2.y > p3.y && p2.y > p4.y) return null;
		
		double dx = (p2.x - p1.x),
				dy = (p2.y - p1.y);
		if (dx == 0) {
			return intersectVertical(other);
		}
		if (dy == 0) {
			return intersectHorizontal(other);
		}
		System.out.println("intersect normal");
		double d1 = (p3.x - p1.x) / dx - (p3.y - p1.y) / dy;
		double d2 = (p4.y - p3.y)/dy - (p4.x - p3.x)/dx;
		double t2 = d1 / d2;
		System.out.printf("t2=%.5f dx=%.5f dy=%.5f d1=%.5f d2=%.5f (p3x-p1x)=%.5f (p4x=p3x)=%.5f (p3y-p1y)=%.5f (p4y-p3y)=%.5f\n",t2,dx,dy,d1,d2,p3.x-p1.x,p4.x-p3.x,p3.y-p1.y,p4.y-p3.y);
		if (t2 < 0 || t2 > 1) {
			return null;
		}
		return other.getPoint(t2);
	}
	
	private Pointd intersectVertical(Line other) {
		System.out.println("intersect ver");
		Pointd p3 = other.getP1(), p4 = other.getP2();
		double dx = p4.x - p3.x;
		if (dx == 0) { // other also vertical
			/*if(p1.y == p3.y) {
				if ((p3.x <= p1.x && p3.x >= p2.x) || (p3.x >= p1.x && p3.x <= p2.x)) {
					return p3;
				}
				if ((p4.x <= p1.x && p4.x >= p2.x) || (p4.x >= p1.x && p4.x <= p2.x)) {
					return p4;
				}
				if ((p4.x <= p1.x && p3.x >= p2.x) || (p4.x >= p1.x && p3.x <= p2.x)) {
					return p1;
				}
				return null;
			}*/
			if (p1.x == p3.x) { // must be on same x to intersect
				if ((p3.y <= p1.y && p3.y >= p2.y) || (p3.y >= p1.y && p3.y <= p2.y)) {
					return p3;
				}
				if ((p4.y <= p1.y && p4.y >= p2.y) || (p4.y >= p1.y && p4.y <= p2.y)) {
					return p4;
				}
				if ((p3.y <= p1.y && p4.y >= p2.y) || (p3.y >= p1.y && p4.y <= p2.y)) {
					return p1;
				}
			}
			return null;
		}
		double t = (p4.x - p1.x) / dx;
		Pointd p = other.getPoint(t);
		if (t >= 0 && t <= 1 && ((p.y <= p1.y && p.y >= p2.y) || (p.y >= p1.y && p.y <= p2.y))) {
			return p;
		}
		return null;
		
	}
	
	private Pointd intersectHorizontal(Line other) {
		System.out.println("intersect hor");
		Pointd p3 = other.getP1(), p4 = other.getP2();
		double dy = p4.y - p3.y;
		if (dy == 0) { // other also horizontal
			/*if(p1.x == p3.x) {
				if ((p3.y <= p1.y && p3.y >= p2.y) || (p3.y >= p1.y && p3.y <= p2.y)) {
					return p3;
				}
				if ((p4.y <= p1.y && p4.y >= p2.y) || (p4.y >= p1.y && p4.y <= p2.y)) {
					return p4;
				}
				if ((p4.y <= p1.y && p3.y >= p2.y) || (p4.y >= p1.y && p3.y <= p2.y)) {
					return p1;
				}
				return null;
			}*/
			if (p1.y == p3.y) {
				if ((p3.x <= p1.x && p3.x >= p2.x) || (p3.x >= p1.x && p3.x <= p2.x)) {
					return p3;
				}
				if ((p4.x <= p1.x && p4.x >= p2.x) || (p4.x >= p1.x && p4.x <= p2.x)) {
					return p4;
				}
				if ((p4.x <= p1.x && p3.x >= p2.x) || (p4.x >= p1.x && p3.x <= p2.x)) {
					return p1;
				}
			}
			return null;	
		}
		double t = (p4.y - p1.y) / dy;
		Pointd p = other.getPoint(t);
		System.out.printf("intersect hor, other not hor, t=%.5f\n",t);
		if (t >= 0 && t <= 1 && ((p.x <= p1.x && p.x >= p2.x) || (p.x >= p1.x && p.x <= p2.x))) {
			System.out.println("option 6, dy="+dy+", p1.y="+p1.y+", t="+t);
			return p;
		}
		return null;
	}
	
}
