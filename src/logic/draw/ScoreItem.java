package logic.draw;
import geom.Pointd;

import java.awt.Graphics;
import java.awt.Color;



public class ScoreItem extends Item {
	// geography
	//int size = 10;
	int width = 10, height = 10;
	
	// snake-related
	int score, growth;
	
	public ScoreItem(int x0, int y0) {
		super(x0, y0);
		score = 1;
		growth = 20;
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
		// here we use integer rounding
		g.fillRect((int)position.x, (int)position.y, width, height);
	}
	public boolean isPointInside(Pointd p) {
		return p.x >= position.x && p.x <= position.x + width
				&& p.y >= position.y && p.y <= position.y + height;
	}
	
	public boolean isSnakeColliding(Snake s) {
		//Pointd p = s.getHead();
		//double x = position.x, y = position.y;
		//return p.x >= x && p.x <= x + size && p.y >= y && p.y <= y + size;
		return isPointInside(s.getHead());
	}
	
	public Pointd getPointd() {
		return position;
	}

}
