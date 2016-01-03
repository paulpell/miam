package org.paulpell.miam.logic;

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
		advanceSnakes();
	}

}
