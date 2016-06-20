package org.paulpell.miam.logic.draw.snakes.auto;

import java.awt.Component;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TimerTask;

import org.paulpell.miam.logic.Game;
import org.paulpell.miam.logic.GameSettings;
import org.paulpell.miam.logic.draw.snakes.Snake;


public class AutoSnake extends Snake
{	
	LinkedList <AutoAction> actions_;
	Iterator <AutoAction> actionIter_;
	AutoAction currentAction_;
	TimerTask timerTask_;

	public AutoSnake(int id, GameSettings settings, int x0, int y0, int dir)
	{
		super(id, settings, x0, y0, dir);
		init();
	}

	public AutoSnake(String fromNetwork, GameSettings settings)
	{
		super(fromNetwork, settings);
		init();
	}
	
	private void init()
	{
		actions_ = new LinkedList <AutoAction> ();
	}
	
	public void addAction (AutoAction a)
	{
		actions_.add(a);
	}
	
	@Override
	public void advance(Game game)
	{
		// check validity
		if ( null == actions_ || 0 == actions_.size() )
			return; // nothing to do
		
		if ( null == currentAction_ || 0 <= currentAction_.repeats_ )
		{
			if ( null == actionIter_ || ! actionIter_.hasNext() )
				actionIter_ = actions_.iterator(); // restart
			if ( ! actionIter_.hasNext() )
				return; // no more hope
		
			currentAction_ = (AutoAction)actionIter_.next().clone();
		}
		
		// now use current action
		--currentAction_.repeats_;
		
		switch (currentAction_.type_)
		{
		case TURN_LEFT:
			setTurnLeft(true);
			break;
		case TURN_RIGHT:
			setTurnRight(true);
			break;
		default:
			setTurnLeft(false);
			setTurnRight(false);
			break;	
		}
		
		// and update state
		super.advance (game);
	}
	
	public TimerTask getTimerTask ()
	{
		return timerTask_;
	}
	public TimerTask makeTimerTask (final Game game, final Component cont)
	{
		if (null == timerTask_)
			timerTask_ =
				new TimerTask()
				{
					@Override
					public void run()
					{
						AutoSnake.this.advance(game);
						cont.repaint();
					}
				};
		return timerTask_;
	}
	

}
