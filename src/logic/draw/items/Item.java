package logic.draw.items;

import javax.swing.ImageIcon;

import logic.Game;
import logic.draw.Drawable;
import logic.draw.snakes.Snake;
import geom.GeometricObject;
import geom.Line;
import geom.Pointd;

/**
 * Extending this class forces the user to create the following methods:
 * clone()
 * effectStep() -- here we can decide when to cancel for instance
 * startEffect()
 */

public abstract class Item extends Drawable {
	
	
	protected GeometricObject shape;
	protected Pointd position;
	protected int duration; // for during effects
	
	
	public GeometricObject getShape() {
		return shape;
	}
	
	// needed to display the items in the info panel
	public abstract ImageIcon getImageIcon();
	
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
	
	public void setPosition(double x, double y) {
		position = new Pointd(x,y);
	}
	
	public abstract Object clone(Game g);
	
	// one step of duration, returns true when it's finished
	public abstract boolean effectStep(Snake s);
	public abstract void startEffect(Snake s);
	
	public abstract String getTextDescription();

}
