package geom;

import java.awt.Graphics;

public abstract class GeometricObject {
	
	public abstract Pointd getPointd();
	
	public abstract boolean isPointInside(Pointd p);
	
	public abstract Pointd intersect(Line other);
	public abstract Pointd intersect(Circle other);
	public abstract Pointd intersect(Rectangle other);
	
	public abstract void draw(Graphics g);

}
