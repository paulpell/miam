package org.paulpell.miam.logic.draw;

import java.awt.Graphics2D;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.draw.snakes.Snake;



public abstract class Drawable
{
	public abstract void draw(Graphics2D g);
	//public abstract Pointd getPointd();
	//public abstract boolean isPointInside(Pointd p);
	public abstract Pointd isSnakeColliding(Snake s);
}
