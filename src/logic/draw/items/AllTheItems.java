package logic.draw.items;

import logic.Game;


/**
 * Contains all the possible items, but the score items.
 */


public class AllTheItems {

	static private double uniform;

	private static Item[] items = {
		new Banana(0,0), new BananaSpecial(0,0, null), new Lightning(0,0),
		new ReversingItem(0,0)//, new ScoreItem(0,0)
	};
	
	// weight set for each item. At the beginning, uniform distribution
	private static int[] weights;
	private static double[] probs;
	static {
		uniform = 1. / items.length;
		probs = new double[items.length];
		weights = new int[items.length];
		for (int i=0; i<items.length; ++i) {
			weights[i] = 1;
			probs[i] = uniform;
		}
	}
	
	
	
	public static Item[] getItems() {
		Item[] is = items.clone();
		return is;
	}
	
	public static Item getRandomItem(Game game) {
		double p = Math.random();
		double p_low = 0, p_high;
		for (int i=0; i<items.length; ++i) {
			p_high = p_low + probs[i];
			
			if (p >= p_low && p < p_high) {
				// ugly class trick, why not using scala..?
				if (items[i] instanceof SpecialItem) {
					return (Item)((SpecialItem)items[i]).clone(game);
				}
				return (Item)items[i].clone(game);
			}
			p_low = p_high;
		}
		throw new Error("bug in probabilities computation");
	}
	
	public static void setProbabilities(int[] ws) {
		if (weights.length != items.length)
			throw new Error("Weights specified for probs are the wrong size");
		weights = ws;
		double sum = 0;
		for (int i=0; i<weights.length; ++i) sum += weights[i];
		for (int i=0; i<weights.length; ++i) probs[i]= weights[i] / sum;
	}
	
	public static int getWeightIndex(Item item) {
		for (int i=0; i<items.length; ++i) {
			if (item.getClass() == items[i].getClass()) {
				return weights[i];
			}
		}
		return 0;
	}
}
