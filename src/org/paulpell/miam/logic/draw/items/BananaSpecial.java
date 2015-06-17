package org.paulpell.miam.logic.draw.items;


import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.geom.Rectangle;
import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.Game;
import org.paulpell.miam.logic.draw.snakes.Snake;


public class BananaSpecial extends SpecialItem
{
	
	
	private static ImageIcon s_image = new ImageIcon("images/BananaSpecial.png");
	private static int s_width = s_image.getIconWidth();
	private static int s_height = s_image.getIconHeight();
	
	Game game_;
	
	// for network, as nothing can be random:
	// it is initialized on master, and sent as extra param, so the bananas on all
	// clients have the same `random' moves
	private String bananaExtra_;
	
	public BananaSpecial(double x0, double y0, Game g)
	{
		if (s_image == null) {
			throw new UnsupportedOperationException("Image BananaSpecial.png could not be loaded!");
		}
		shape_ = new Rectangle(x0, y0, s_width, s_height);
		position_ = new Pointd(x0, y0);
		
		bananaExtra_ = new Banana(0,0).getExtraParamsDescription();
		
		game_ = g;
	}
	
	
	@Override
	public boolean activate(Snake s)
	{
		Pointd pos = s.getTail();
		Banana b = new Banana(pos.x_, pos.y_);
		b.applyExtraParamsDescription(bananaExtra_);
		game_.addItem(b);
		return true; // the object is usable only once
	}
	

	@Override
	public Object clone(Game g)
	{
		double 	x 	= Math.random() * (Constants.DEFAULT_IMAGE_WIDTH - s_width);
		double  y 	= Math.random() * (Constants.DEFAULT_IMAGE_HEIGHT - s_height);
		return new BananaSpecial(x, y, g);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(s_image.getImage(), (int)position_.x_, (int)position_.y_, (int)s_width, (int)s_height, null);
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
	public Item newItem(double x, double y, Game game)
	{
		return new BananaSpecial(x,y,game);
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