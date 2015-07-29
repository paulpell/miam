package org.paulpell.miam.logic.levels;

import org.paulpell.miam.logic.Log;
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
	
	public String toString()
	{
		return "Score >= " + goodScore_;
	}

	@Override
	public String getExtraParams()
	{
		return "" + goodScore_;
	}
	public void applyExtraParams(String params)
	{
		try
		{
			goodScore_ = Integer.parseInt(params);
		}
		catch (Exception e)
		{
			Log.logErr("Could not apply extra param to ScoreVictoryCondition, received: " + params);
			Log.logException(e);
		}
	}

}
