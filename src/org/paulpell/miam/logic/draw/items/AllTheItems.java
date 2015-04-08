package org.paulpell.miam.logic.draw.items;

import org.paulpell.miam.logic.Game;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.Log;


/**
 * Contains all the possible items, but the score_ items.
 */


public class AllTheItems
{

	private static Item[] items =
		{
		new Banana(0,0),
		new BananaSpecial(0,0, null),
		new Lightning(0,0),
		new ReversingItem(0,0),
	};
	
	// weight set for each item. At the beginning, uniform distribution
	private static int[] weights;
	private static double[] probs;
	static
	{
		double uniform = 1. / items.length;
		probs = new double[items.length];
		weights = new int[items.length];
		for (int i=0; i<items.length; ++i)
		{
			weights[i] = 1;
			probs[i] = uniform;
		}
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
		if (weights.length != items.length)
			throw new Error("Weights specified for probs are the wrong size");
		weights = ws;
		double sum = 0;
		for (int i=0; i<weights.length; ++i)
			sum += weights[i];
		for (int i=0; i<weights.length; ++i)
			probs[i]= weights[i] / sum;
	}
	
	public static int getWeightIndex(Item item)
	{
		for (int i=0; i<items.length; ++i)
			if (item.getClass() == items[i].getClass())
				return weights[i];

		return 0;
	}
}
