package logic.draw.items;

import geom.Pointd;

import java.awt.Color;
import java.awt.Graphics;

import logic.draw.snakes.Snake;


public class DefaultWall extends Wall {

	Color color = new Color(190,190,190);
	int width = logic.Constants.IMAGE_WIDTH,
		height = logic.Constants.IMAGE_HEIGHT;
	
	public void draw(Graphics g) {
		g.setColor(color);
		g.drawLine(0,0, 0, width - 1);
		g.drawLine(0, 0, height, 0);
		g.drawLine(0, height - 1, width - 1, height - 1);
		g.drawLine(width - 1, 0, width - 1, height - 1);
	}

	public Pointd getPointd() {
		return new Pointd(0,0);
	}
	
	public boolean isPointInside(Pointd p) {
		return (p.x <= 0) || (p.x >= width) || (p.y <= 0) || (p.y >= height); 
	}
	
	public Pointd isSnakeColliding(Snake s) {
		Pointd p = s.getHead();
		if (p.x <= 0) {
			return new Pointd(0, p.y);
		}
		else if (p.x >= width) {
			return new Pointd(width, p.y);
		}
		else if (p.y <= 0) {
			return new Pointd(p.x, 0);
		}
		else if (p.y >= height) {
			return new Pointd(p.x, height);
		}
		return null;
	}
}
