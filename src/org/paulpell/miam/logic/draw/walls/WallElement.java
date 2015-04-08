package org.paulpell.miam.logic.draw.walls;

import java.awt.Color;
import java.awt.Graphics;

import org.paulpell.miam.geom.GeometricObject;
import org.paulpell.miam.geom.Line;
import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.draw.snakes.Snake;



public class WallElement {

	GeometricObject geomObject;
	Color color;
	
	public WallElement(GeometricObject obj, Color c) {
		geomObject = obj;
		color = c;
	}
	
	public Pointd isSnakeColliding(Snake s) {
		Line l = new Line(s.getHead(), s.getPreviousHead());
		return geomObject.intersect(l);
	}
	
	public void draw(Graphics g) {
		g.setColor(color);
		geomObject.draw(g);
	}

}
