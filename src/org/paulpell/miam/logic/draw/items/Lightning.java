package org.paulpell.miam.logic.draw.items;


import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.geom.Rectangle;
import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.Game;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.Utils;
import org.paulpell.miam.logic.draw.snakes.Snake;



public class Lightning extends Item
{
	
	private static ImageIcon s_image = new ImageIcon("images/Lightning.png");
	private static int s_width = s_image.getIconWidth();
	private static int s_height = s_image.getIconHeight();
	
	private double extraSpeed_;
	
	public Lightning(double x0, double y0)
	{
		if (s_image == null)
			throw new UnsupportedOperationException("Image Lightning.png could not be loaded!");

		shape_ = new Rectangle(x0, y0, s_width, s_height);
		position_ = new Pointd(x0, y0);
		effectDuration_ = 0;
		extraSpeed_ = Utils.rand.nextDouble() * Globals.SNAKE_EXTRA_SPEEDUP;
	}
	
	
	@Override
	public boolean effectStep(Snake s)
	{
		if (effectDuration_ == -1)
		{
			s.addSpeedupSpecial(-extraSpeed_);
			return true; // true when cancelled
		}
		return false;
	}

	@Override
	public void startEffect(Snake s)
	{
		s.addSpeedupSpecial(extraSpeed_);
	}
	

	public Object clone(Game g)
	{
		double x = Math.random() * (Constants.DEFAULT_IMAGE_WIDTH - s_width);
		double y = Math.random() * (Constants.DEFAULT_IMAGE_HEIGHT - s_height);
		return new Lightning(x, y);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(s_image.getImage(), (int)position_.x_, (int)position_.y_, null);
	}
	
	public ImageIcon getImageIcon()
	{
		return s_image;
	}
	
	public String getTextDescription()
	{
		return "Lightning";
	}


	@Override
	public Item newItem(double x, double y,Game game)
	{
		return new Lightning(x, y);
	}


	@Override
	public String getExtraParamsDescription() {
		return "" + Double.doubleToLongBits(extraSpeed_);
	}


	@Override
	public void applyExtraParamsDescription(String params) {
		extraSpeed_ = Double.longBitsToDouble(Long.parseLong(params));
	}


	@Override
	public boolean shouldDisplayInPanelInfo()
	{
		return true;
	}
}
