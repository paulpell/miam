package org.paulpell.miam.logic;

import org.paulpell.miam.logic.draw.snakes.Snake;
import org.paulpell.miam.logic.levels.Level;




public class RemoteGame extends Game
{
	
	public RemoteGame(Control gc, Level level)
	{
		super(gc, level);
	}

	@Override
	public void update()
	{
		// only advance the snakes
		for (Snake s : snakes_)
			s.advance();
	}

}