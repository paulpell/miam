package org.paulpell.miam.logic.draw.items;


import javax.swing.ImageIcon;

import org.paulpell.miam.logic.Game;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.Log;


/**
 * Contains all the possible items, but the score_ items.
 */


public class AllTheItems
{

	public final static Item[] items =
		{
		new Banana(0,0),
		new BananaSpecial(0,0), //, null),
		new Lightning(0,0),
		new ScoreItem(0, 0),
		new ReversingItem(0,0),
	};
	
	public final static int INDEX_BANANA = 0;
	public final static int INDEX_BANANA_SPECIAL = 1;
	public final static int INDEX_LIGHTNING = 2;
	public final static int INDEX_SCORE = 3;
	public final static int INDEX_REVERSO = 4;
	public final static int INDEX_LAST = INDEX_REVERSO; // !!!!!! update this
	
	
	// weight set for each item. At the beginning, uniform distribution
	private static int[] weights_;
	private static double[] probs;
	static
	{
		double uniform = 1. / items.length;
		probs = new double[items.length];
		weights_ = new int[items.length];
		for (int i=0; i<items.length; ++i)
		{
			weights_[i] = 1;
			probs[i] = uniform;
		}
	}
	
	public static ImageIcon getImageIcon(int i)
	{
		if (i < 0 || i > INDEX_LAST)
			return null;
		return items[i].getImageIcon();
	}
	
	
	
	public static Item[] getItems()
	{
		Item[] is = items.clone();
		return is;
	}
	
	// first choose p randomly in [0,1],
	// then find which item corresponds to that p, depending on the distribution
	public static Item getRandomItem(Game game)
	{
		double p = Math.random();
		double p_low = 0, p_high;
		for (int i=0; i<items.length; ++i)
		{
			p_high = p_low + probs[i];
			
			// if p is inside the prob interval for this item, return it
			if (p >= p_low && p < p_high)
			{
				if (items[i] instanceof Banana && Globals.NETWORK_DEBUG)
				{
					Item i2 = (Item)items[i].clone(game);
					Log.logMsg("Cloned banana, old = " + items[i] + ", new = " + i2);
					return i2;
				}
				return (Item)items[i].clone(game);
			}

			p_low = p_high;
		}
		throw new Error("bug in probabilities computation");
	}
	
	public static void setProbabilities(int[] ws)
	{
		if (weights_.length != items.length)
			throw new Error("Weights specified for probs are the wrong size");
		weights_ = ws;
		double sum = 0;
		for (int i=0; i<weights_.length; ++i)
			sum += weights_[i];
		for (int i=0; i<weights_.length; ++i)
			probs[i]= weights_[i] / sum;
	}
	
	public static int getWeightIndex(Item item)
	{
		for (int i=0; i<items.length; ++i)
			if (item.getClass() == items[i].getClass())
				return weights_[i];

		return 0;
	}
}
