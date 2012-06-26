package logic.draw;

import geom.Pointd;

public abstract class Item extends Drawable {
	protected Pointd position;
	
	public Item(double x0, double y0) {
		position = new Pointd(x0, y0);
	}
	
	public abstract int getScore();
	public abstract int getGrowth();
	public abstract double getThickness();
	public abstract boolean isPersistent(); 
	public abstract boolean isReversing();
}
