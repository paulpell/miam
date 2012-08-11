package logic.draw.items;

import logic.draw.Drawable;
import logic.draw.snakes.Snake;
import geom.GeometricObject;
import geom.Line;
import geom.Pointd;

public abstract class Item extends Drawable {
	
	protected GeometricObject shape;
	protected Pointd position;
	
	public abstract int getScore();
	public abstract int getGrowth();
	public abstract double getThickness();
	public abstract boolean isPersistent(); 
	public abstract boolean isReversing();
	
	public GeometricObject getShape() {
		return shape;
	}
	
	public Pointd isSnakeColliding(Snake s) {
		Line l = new Line(s.getHead(), s.getPreviousHead());
		return shape.intersect(l);
	}
	public boolean isPointInside(Pointd p) {
		return shape.isPointInside(p);
	}
	public Pointd getPointd() {
		return position;
	}
}
