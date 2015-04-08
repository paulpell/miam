package org.paulpell.miam.logic.draw.items;


import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.ImageIcon;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.geom.Rectangle;
import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.Game;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.Log;
import org.paulpell.miam.logic.Utils;
import org.paulpell.miam.logic.draw.snakes.Snake;


public class Banana extends Item
{
	
	private  static ImageIcon s_image = new ImageIcon("images/Banana.png");
	
	private static int s_width = s_image.getIconWidth();
	private static int s_height = s_image.getIconHeight();
	
	// contains values in (0,1,2), to tell snake: (forward, left, right)
	private LinkedList <Integer> randomMoves_;
	
	public Banana(double x0, double y0)
	{
		this(x0, y0, Constants.BANANA_MIN_DURATION + Utils.rand.nextInt(Constants.BANANA_EXTRA_DURATION));
	}
	
	public Banana(double x0, double y0,int duration)
	{
		if (s_image == null)
			throw new UnsupportedOperationException("Image Banana.png could not be loaded!");

		shape_ = new Rectangle(x0, y0, s_width, s_height);
		position_ = new Pointd(x0, y0);
		this.duration_ = duration;
		randomMoves_ = new LinkedList <Integer> ();
		for (int i=0; i<duration; ++i)
			randomMoves_.add(Utils.rand.nextInt(3));
	}
	
	
	
	@Override
	public void draw(Graphics g)
	{
		g.drawImage(s_image.getImage(), (int)position_.x_, (int)position_.y_, (int)s_width, (int)s_height, null);
	}
	
	@Override
	public Object clone(Game g)
	{
		double 	x 	= Math.random() * (Constants.DEFAULT_IMAGE_WIDTH - s_width);
		double	y 	= Math.random() * (Constants.DEFAULT_IMAGE_HEIGHT - s_height);
		return new Banana(x, y);
	}

	public Item newItem(double x, double y, int duration)
	{
		return new Banana(x,y,duration);
	}

	@Override
	public boolean effectStep(Snake s)
	{
		if (randomMoves_.isEmpty())
		{
			s.setRandomTurning(-1);
			if (Globals.NETWORK_DEBUG)
				Log.logMsg("Random turning off");
			return true;
		}
		int i = randomMoves_.removeFirst();
		s.setRandomTurning(i);
		if (Globals.NETWORK_DEBUG)
			Log.logMsg("Random turning: " + i);
		return false;
	}

	@Override
	public void startEffect(Snake s)
	{
		if (Globals.NETWORK_DEBUG)
			Log.logMsg("Random on: " + movesToString());
		// nothing to do
	}
	
	@Override
	public ImageIcon getImageIcon()
	{
		return s_image;
	}
	
	public String getTextDescription()
	{
		return "Banana";
	}

	@Override
	public Item newItem(double x, double y, Game game)
	{
		return new Banana(x, y);
	}
	

	@Override
	public String getExtraParamsDescription()
	{
		String ret = "" + (char)randomMoves_.size();
		String print = "";
		for (Iterator<Integer> it = randomMoves_.iterator(); it.hasNext(); )
		{
			int i = (int)it.next();
			ret += (char)i;
			print += i;
		}
		if (Globals.NETWORK_DEBUG)
			Log.logMsg("Banana> Generated extra : " + print);
		return ret;
	}
	@Override
	public void applyExtraParamsDescription(String params)
	{
		randomMoves_ = new LinkedList <Integer> ();
		char size = params.charAt(0);
		
		String print = "";
		for (int i = 1; i <= size; ++i)
		{
			int m = (int)params.charAt(i);
			randomMoves_.addLast(m);
			print += m;
		}
		if (Globals.NETWORK_DEBUG)
			Log.logMsg("Banana> Applied extra : " + print);
	}
	
	private String movesToString()
	{
		String print = "";
		for (int i = 0; i < randomMoves_.size(); ++i)
		{
			print += randomMoves_.get(i);
		}
		return print;
	}

	@Override
	public String toString()
	{
		return "Banana[" + position_ + ", moves: " + movesToString() + "]";
	}
}
