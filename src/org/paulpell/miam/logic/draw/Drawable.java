package org.paulpell.miam.logic.draw;

import java.awt.Graphics;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.draw.snakes.Snake;


//import org.paulpell.miam.logic.Arith;


public abstract class Drawable {//implements Comparable<Drawable> {
	public abstract void draw(Graphics g);
	public abstract Pointd getPointd();
	public abstract boolean isPointInside(Pointd p);
	public abstract Pointd isSnakeColliding(Snake s);
}
