package org.paulpell.miam.logic.levels;

import org.paulpell.miam.logic.draw.snakes.Snake;

public class ScoreVictoryCondition extends VictoryCondition
{

	int goodScore_;
	
	public ScoreVictoryCondition(int goodScore)
	{
		goodScore_ = goodScore;
	}

	@Override
	public boolean checkVictory(Snake snake)
	{
		return snake.getScore() >= goodScore_;
	}

}
