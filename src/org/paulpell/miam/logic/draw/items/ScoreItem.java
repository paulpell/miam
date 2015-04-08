package org.paulpell.miam.logic.draw.items;

import java.awt.Graphics;
import java.awt.Color;

import javax.swing.ImageIcon;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.geom.Rectangle;
import org.paulpell.miam.logic.Game;
import org.paulpell.miam.logic.draw.snakes.Snake;




public class ScoreItem extends Item
{
	
	// snake-related
	private int score_;
	private int growth_;
	
	public static final int s_width = 10;
	public static final int s_height = 10;
	
	public ScoreItem(double x0, double y0)
	{
		score_ = 1;
		growth_ = 20;
		shape_ = new Rectangle(x0, y0, s_width, s_height);
		position_ = new Pointd(x0, y0);
	}

	
	public void draw(Graphics g)
	{
		g.setColor(new Color(10, 180, 50));
		shape_.draw(g);
	}

	@Override
	public boolean effectStep(Snake s)
	{
		return true; // immediately finished
	}
	public void startEffect(Snake s)
	{
		s.addScore(score_);
		s.growBy(growth_);
	}
	
	@Override
	public Object clone(Game g)
	{
		return new ScoreItem(position_.x_, position_.y_);
	}
	@Override
	public ImageIcon getImageIcon()
	{
		return null;
	}
	
	public String getTextDescription()
	{
		return "Score";
	}


	@Override
	public Item newItem(double x, double y, Game game) 
	{
		return new ScoreItem(x,  y);
	}


	@Override
	public String getExtraParamsDescription()
	{
		return "";
	}


	@Override
	public void applyExtraParamsDescription(String params) {
		// nothing
	}
	
}
