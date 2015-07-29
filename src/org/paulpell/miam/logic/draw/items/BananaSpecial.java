package org.paulpell.miam.logic.draw.items;


import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.geom.Rectangle;
import org.paulpell.miam.logic.Game;
import org.paulpell.miam.logic.draw.snakes.Snake;


public class BananaSpecial extends SpecialItem
{
	
	
	private static ImageIcon s_image = new ImageIcon("images/BananaSpecial.png");
	private static int s_width = s_image.getIconWidth();
	private static int s_height = s_image.getIconHeight();
	
	//Game game_;
	
	// for network, as nothing can be random:
	// it is initialized on master, and sent as extra param, so the bananas on all
	// clients have the same `random' moves
	private String bananaExtra_;
	
	public BananaSpecial(double x0, double y0)
	{
		if (s_image == null) {
			throw new UnsupportedOperationException("Image BananaSpecial.png could not be loaded!");
		}
		shape_ = new Rectangle(x0, y0, s_width, s_height);
		
		bananaExtra_ = new Banana(0,0).getExtraParamsDescription();
	}
	
	
	@Override
	public boolean activate(Snake s, Game g)
	{
		Pointd pos = s.getTail();
		Banana b = new Banana(pos.x_, pos.y_);
		b.applyExtraParamsDescription(bananaExtra_);
		g.addItem(b);
		return true; // the object is usable only once
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		Pointd pos = shape_.getP1();
		g.drawImage(s_image.getImage(), (int)pos.x_, (int)pos.y_, (int)s_width, (int)s_height, null);
	}

	@Override
	public ImageIcon getImageIcon()
	{
		return s_image;
	}
	
	public String getTextDescription()
	{
		return "Banana to deposit";
	}


	@Override
	public Item newItem(double x, double y)
	{
		return new BananaSpecial(x,y);
	}


	@Override
	public String getExtraParamsDescription()
	{
		return bananaExtra_;
	}


	@Override
	public void applyExtraParamsDescription(String params)
	{
		bananaExtra_ = params;
	}


	@Override
	public boolean shouldDisplayInPanelInfo()
	{
		return true;
	}
}
