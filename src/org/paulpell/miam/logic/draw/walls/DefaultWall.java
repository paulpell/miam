package org.paulpell.miam.logic.draw.walls;


import java.awt.Color;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.geom.Rectangle;
import org.paulpell.miam.geom.Circle;
import org.paulpell.miam.logic.Constants;



public class DefaultWall extends Wall
{

	Color color_ = new Color(0,255,0);
	final static int width_ = Constants.DEFAULT_IMAGE_WIDTH;
	final static int height_ = Constants.DEFAULT_IMAGE_HEIGHT;
	
	public DefaultWall()
	{
		super(width_, height_);
		
		int thick = 3;
		
		Rectangle left = new Rectangle(0, 0, thick, height_, true);
		WallElement we1 = new WallElement(left, color_);
		pushElement(we1);
		Rectangle right = new Rectangle(width_ - thick, 0, thick, height_, true);
		WallElement we2 = new WallElement(right, color_);
		pushElement(we2);
		Rectangle top = new Rectangle(0, 0, width_, thick, true);
		WallElement we3 = new WallElement(top, color_);
		pushElement(we3);
		Rectangle bottom = new Rectangle(0, height_ - thick, width_, thick, true);
		WallElement we4 = new WallElement(bottom, color_);
		pushElement(we4);
		
		Circle c;
		for (int i=0; i<13; ++i)
		{
			c = new Circle(new Pointd(300, 300), 50 - i);
			WallElement we = new WallElement(c, color_);
			pushElement(we);
		}
	}
	
}
