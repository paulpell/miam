package org.paulpell.miam.logic.draw.items;

public class ProbabilizedItem {
	Item item; double prob;
	public ProbabilizedItem(Item it, double proba) {
		item = it;
		prob = proba;
	}
	void normalize(double factor) {
		prob /= factor;
	}
}
