package test;

import static org.junit.Assert.*;
import geom.Line;
import geom.Pointd;

public class LineIntersectTest {

	@org.junit.Test
	public void testLines() {
		
		Pointd p1 = new Pointd(0,0),
				p2 = new Pointd(0,1),
				p3 = new Pointd(1,0),
				p4 = new Pointd(1,1),
				p5 = new Pointd(1.2,0),
				p6 = new Pointd(1.1,0);
		
		Line l12 = new Line(p1, p2),
				l14 = new Line(p1, p4),
				l13 = new Line(p1,p3),
				l23 = new Line(p2, p3),
				l26 = new Line(p2, p6),
				l34 = new Line(p3,p4),
				l35 = new Line(p3, p5),
				l56 = new Line(p5, p6), 
				l16 = new Line(p1,p6);
		
		// p1 is shared, should intersect
		Pointd p = l12.intersect(l13);
		assertTrue(p != null);
		p = l13.intersect(l12);
		assertTrue(p != null);
		
		// two parallel lines
		p = l12.intersect(l34);
		assertTrue(p == null);
		p = l34.intersect(l12);
		assertTrue(p == null);
		
		// should not intersect
		p = l12.intersect(l35);
		assertTrue(p == null);
		p = l35.intersect(l12);
		assertTrue(p == null);
		
		// both segments on same line, but not touching
		p = l12.intersect(l56);
		assertTrue(p == null);
		p = l56.intersect(l12);
		assertTrue(p == null);
		
		// intersect
		p = l16.intersect(l34);
		assertTrue(p != null);
		p = l34.intersect(l16);
		assertTrue(p != null);
		
		// intersect
		p = l14.intersect(l23);
		assertTrue(p != null);
		p = l23.intersect(l14);
		assertTrue(p != null);
		
		// intersect
		p = l26.intersect(l14);
		assertTrue(p != null);
		p = l14.intersect(l26);
		assertTrue(p != null);
		
	}
	
}
