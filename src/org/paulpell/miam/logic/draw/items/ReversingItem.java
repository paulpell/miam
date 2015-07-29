package org.paulpell.miam.logic.draw.items;


import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.geom.Rectangle;
import org.paulpell.miam.logic.draw.snakes.Snake;


public class ReversingItem extends Item
{
	
	private static ImageIcon s_image;
	static
	{
		s_image = new ImageIcon("images/Reverse.png");
	}
	private static int s_width = s_image.getIconWidth();
	private static int s_height = s_image.getIconHeight();
	
	public ReversingItem(double x0, double y0)
	{
		if (s_image == null)
			throw new UnsupportedOperationException("Image could not be loaded!");
		
		shape_ = new Rectangle(x0, y0, s_width, s_height);
	}


	public void draw(Graphics2D g)
	{
		Pointd pos = shape_.getP1();
		g.drawImage(s_image.getImage(), (int)pos.x_, (int)pos.y_, (int)s_width, (int)s_height, null);
	}

	@Override
	public boolean effectStep(Snake s)
	{
		return true; // no lasting effect
	}
	public void startEffect(Snake s)
	{
		s.reverse();
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
	public ReversingItem newItem(double x, double y)
	{
		return new ReversingItem(x, y);
	}


	@Override
	public String getExtraParamsDescription() {
		return "";
	}


	@Override
	public void applyExtraParamsDescription(String params)
	{
		// nothing to do
	}


	@Override
	public boolean shouldDisplayInPanelInfo()
	{
		return false;
	}
}
