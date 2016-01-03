package org.paulpell.miam.logic.draw.items;

import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.geom.Rectangle;
import org.paulpell.miam.logic.Game;
import org.paulpell.miam.logic.draw.snakes.Snake;

public class ResurrectAll extends GlobalEffectItem
{

	private static ImageIcon s_image;
	static
	{
		s_image = new ImageIcon("images/Resurrect.png");
	}
	private static int s_width = s_image.getIconWidth();
	private static int s_height = s_image.getIconHeight();
	
	
	public ResurrectAll(double x0, double y0)
	{
		shape_ = new Rectangle(x0, y0, s_width, s_height);
	}

	@Override
	public ImageIcon getImageIcon()
	{
		return s_image;
	}

	@Override
	public boolean shouldDisplayInPanelInfo()
	{
		return false;
	}

	@Override
	public Item newItem(double x, double y)
	{
		return new ResurrectAll(x, y);
	}

	@Override
	public boolean effectStep(Snake s)
	{
		return true; // immediately finished
	}
	
	@Override
	public void globalEffect (Game game)
	{
		game.resurrectDeadSnakes();
	}

	@Override
	public void startEffect(Snake s)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("TODO: Resurrect all; Also add in settings panel");
	}

	@Override
	public String getExtraParamsDescription()
	{
		return "";
	}

	@Override
	public void applyExtraParamsDescription(String params)
	{
		// ok
	}

	@Override
	public String getTextDescription()
	{
		return "Resurrect all";
	}

	@Override
	public void draw(Graphics2D g)
	{
		Pointd pos = shape_.getP1();
		g.drawImage(s_image.getImage(), (int)pos.x_, (int)pos.y_, null);
	}


}
