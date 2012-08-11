package logic.draw.items;
import geom.Pointd;
import geom.Rectangle;

import java.awt.Graphics;
import java.awt.Color;



public class ScoreItem extends Item {
	// geography
	//private Pointd position;
	
	// snake-related
	int score, growth;
	
	public ScoreItem(double x0, double y0) {
		score = 1;
		growth = 20;
		shape = new Rectangle(x0, y0, 10, 10);
		position = new Pointd(x0, y0);
	}

	public int getGrowth() {
		return growth;
	}
	public int getScore () {
		return score;
	}
	public double getThickness() {
		return 0;
	}
	public boolean isPersistent() {
		return false;
	}
	public boolean isReversing() {
		return false;
	}
	
	public void draw(Graphics g) {
		g.setColor(new Color(10, 180, 50));
		shape.draw(g);
	}
	/*
	public Pointd getPointd() {
		return position;
	}*/

	/*public boolean isPointInside(Pointd p) {
		return shape.isPointInside(p);
	}*/

}
