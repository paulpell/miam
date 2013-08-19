package logic.draw;
import geom.Pointd;

import java.awt.Graphics;

import logic.draw.snakes.Snake;

//import logic.Arith;


public abstract class Drawable {//implements Comparable<Drawable> {
	public abstract void draw(Graphics g);
	public abstract Pointd getPointd();
	public abstract boolean isPointInside(Pointd p);
	public abstract Pointd isSnakeColliding(Snake s);
}
