package org.paulpell.miam.logic.levels;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.GameSettings;
import org.paulpell.miam.logic.draw.items.ScoreItem;
import org.paulpell.miam.logic.draw.walls.DefaultWall;

public class DefaultLevel extends Level
{

	public DefaultLevel(GameSettings settings)
	{
		super(settings, Constants.DEFAULT_LEVEL_NAME);
		wall_ = new DefaultWall();
		//setActualGameMode(GameModesEnum.MODERN);
			
		maxNumSnakes_ = Constants.MAX_NUMBER_OF_SNAKES;
		minNumSnakes_ = 1;
		
		// snake start positions and angles
		snakeStartPositions_ =
				new Pointd[]
				{
					new Pointd(50, 300),
					new Pointd(550, 300),
					new Pointd(300, 50),
					new Pointd(300, 550)
				};
		snakeStartAngles_ =
				new int[]
				{
					0,
					180,
					90,
					270
				};
		
		// initial items 
		for (double i=0; i<2. * Math.PI; i += Math.PI / 10.)
		{
			double a = i;
			double x0 = 300 + 130 * Math.cos(a);
			double y0 = 300 + 130 * Math.sin(a);
			addInitialItem(new ScoreItem(x0, y0));
		}
		
		victoryConditions_.add(new ScoreVictoryCondition(150));
		//victoryConditions_.add(new ScoreVictoryCondition(1));
	}

}