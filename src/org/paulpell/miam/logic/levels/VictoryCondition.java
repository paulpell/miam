package org.paulpell.miam.logic.levels;

import org.paulpell.miam.logic.draw.snakes.Snake;

public abstract class VictoryCondition
{	
	public abstract boolean checkVictory(Snake snake);
	
	@Override
	public abstract String toString();
	
	public abstract String getExtraParams();
	public abstract void applyExtraParams(String params);

}
