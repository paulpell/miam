package org.paulpell.miam.logic.draw.items;


import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.geom.Rectangle;
import org.paulpell.miam.logic.draw.snakes.Snake;




public class ScoreItem extends Item
{
	
	// snake-related
	private int score_;
	private int growth_;
	
	private  static ImageIcon s_image = new ImageIcon("images/Score.png");
	
	public static final int s_width = s_image.getIconWidth();
	public static final int s_height = s_image.getIconHeight();
	
	
	public ScoreItem(double x0, double y0)
	{
		score_ = 1;
		growth_ = 20;
		shape_ = new Rectangle(x0, y0, s_width, s_height);
	}

	
	public void draw(Graphics2D g)
	{
		Pointd pos = shape_.getP1();
		g.drawImage(s_image.getImage(), (int)pos.x_, (int)pos.y_, null);
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
	public ImageIcon getImageIcon()
	{
		return s_image;
	}
	
	public String getTextDescription()
	{
		return "Score";
	}


	@Override
	public ScoreItem newItem(double x, double y) 
	{
		return new ScoreItem(x,  y);
	}


	@Override
	public String getExtraParamsDescription()
	{
		return "";
	}


	@Override
	public void applyExtraParamsDescription(String params)
	{
		// nothing
	}


	@Override
	public boolean shouldDisplayInPanelInfo()
	{
		return false;
	}
	
}
