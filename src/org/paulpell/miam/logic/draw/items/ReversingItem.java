package org.paulpell.miam.logic.draw.items;


import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.geom.Rectangle;
import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.Game;
import org.paulpell.miam.logic.draw.snakes.Snake;


public class ReversingItem extends Item
{
	
	private static ImageIcon s_image;
	static {
		s_image = new ImageIcon("images/Reverse.png");
	}
	private static int s_width = s_image.getIconWidth();
	private static int s_height = s_image.getIconHeight();
	
	public ReversingItem(double x0, double y0)
	{
		if (s_image == null) {
			throw new UnsupportedOperationException("Image could not be loaded!");
		}
		shape_ = new Rectangle(x0, y0, s_width, s_height);
		position_ = new Pointd(x0, y0);
	}


	public void draw(Graphics2D g)
	{
		g.drawImage(s_image.getImage(), (int)position_.x_, (int)position_.y_, (int)s_width, (int)s_height, null);
	}

	@Override
	public boolean effectStep(Snake s)
	{
		return true; // no during effect
	}
	public void startEffect(Snake s)
	{
		s.reverse();
	}

	@Override
	public Object clone(Game g)
	{
		double 	x 	= Math.random() * (Constants.DEFAULT_IMAGE_WIDTH - s_width);
		double	y 	= Math.random() * (Constants.DEFAULT_IMAGE_HEIGHT - s_height);
		return new ReversingItem(x, y);
	}
	
	@Override
	public ImageIcon getImageIcon()
	{
		return s_image;
	}
	
	public String getTextDescription()
	{
		return "Reverse";
	}


	@Override
	public Item newItem(double x, double y, Game game)
	{
		return new ReversingItem(x, y);
	}


	@Override
	public String getExtraParamsDescription() {
		return "";
	}


	@Override
	public void applyExtraParamsDescription(String params) {
		// nothing to do
	}


	@Override
	public boolean shouldDisplayInPanelInfo()
	{
		return false;
	}
}
