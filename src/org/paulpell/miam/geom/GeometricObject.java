package org.paulpell.miam.geom;

import java.awt.Graphics2D;

import org.paulpell.miam.logic.Log;

public abstract class GeometricObject
{
	
	// return some point of this object
	public abstract Pointd getPointd();
	
	public abstract boolean isPointInside(Pointd p);
	
	public abstract Pointd intersect(Segment other);
	public abstract Pointd intersect(Line other);
	public abstract Pointd intersect(Circle other);
	public abstract Pointd intersect(Rectangle other);
	
	public abstract double minDistanceToPoint(Pointd p);
	
	public abstract void draw(Graphics2D g);
	
	public abstract GeometricObject translate(Vector2D dv);
	
	// pretty ugly method to intersect any type of GeomObject against any other
	public final Pointd intersectGeneric(GeometricObject g)
	{
		if (g instanceof Segment)
			return intersect((Segment)g);
		else if (g instanceof Line)
			return intersect((Line)g);
		else if (g instanceof Circle)
			return intersect((Circle)g);
		else if (g instanceof Rectangle)
			return intersect((Rectangle)g);
	
		Log.logErr("Unimplemented GeometricObject in GeometricObject.intersectGeneric");
		throw new UnsupportedOperationException("Unimplemented GeometricObject in GeometricObject.intersectGeneric");
	}

}
