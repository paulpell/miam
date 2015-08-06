package org.paulpell.miam.logic;

import java.util.Random;

public class Utils
{


	// random
	public static final Random rand = new Random(System.currentTimeMillis());
	
	public static void threadSleep ( long millis )
	{
		try
		{
			Thread.sleep(millis);
		} catch (InterruptedException e)
		{
			Log.logErr("Poor timerTask, cannot sleep");
			Log.logException(e);
		}
	}
}
