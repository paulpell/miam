package org.paulpell.miam.logic.draw.walls;


import java.awt.Color;
import java.awt.Graphics;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.draw.snakes.Snake;



public class DefaultWall extends Wall
{

	Color color_ = new Color(0,255,0);
	
	public DefaultWall()
	{
		super(Constants.DEFAULT_IMAGE_WIDTH, Constants.DEFAULT_IMAGE_HEIGHT);
	}
	
	public void draw(Graphics g)
	{
		g.setColor(color_);
		g.drawLine(0,0, 0, width_ - 2);
		g.drawLine(0, 0, height_ - 1, 0);
		g.drawLine(0, height_ - 1, width_ - 2, height_ - 1);
		g.drawLine(width_ - 2, 0, width_ - 2, height_ - 1);
	}

	public Pointd getPointd()
	{
		return new Pointd(0,0);
	}
	
	public boolean isPointInside(Pointd p)
	{
		return (p.x_ <= 0) || (p.x_ >= width_) || (p.y_ <= 0) || (p.y_ >= height_); 
	}
	
	public Pointd isSnakeColliding(Snake s)
	{
		Pointd p = s.getHead();
		if (p.x_ <= 0)
			return new Pointd(0, p.y_);
		else if (p.x_ >= width_)
			return new Pointd(width_, p.y_);
		else if (p.y_ <= 0)
			return new Pointd(p.x_, 0);
		else if (p.y_ >= height_)
			return new Pointd(p.x_, height_);
		return null;
	}
}
