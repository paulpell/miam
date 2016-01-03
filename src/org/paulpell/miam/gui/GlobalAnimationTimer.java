package org.paulpell.miam.gui;

import java.util.Timer;
import java.util.TimerTask;

public class GlobalAnimationTimer
	extends Timer
{
	
	private static GlobalAnimationTimer instance_ = new GlobalAnimationTimer();

	private GlobalAnimationTimer()
	{
		super("GUI-anim");
	}
	
	public static void scheduleSingleTask (TimerTask tt, long delay)
	{
		instance_.schedule(tt, delay);
	}
	
	public static void scheduleRepeatedTask (TimerTask tt, long delay, long period)
	{
		instance_.scheduleAtFixedRate(tt, delay, period);
	}

}
