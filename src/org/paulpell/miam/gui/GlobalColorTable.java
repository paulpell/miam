package org.paulpell.miam.gui;

import java.awt.Color;

public class GlobalColorTable
{
	// no constructor!
	private GlobalColorTable()
	{}
	

	
	// some default s_snakesColors
	final private static Color[] s_snakesColors =
		{
			new Color(160, 20, 50),
			new Color(50, 140, 20),
			new Color(30, 30, 190),
			new Color(185, 75, 210),
			new Color(130, 100, 10)
		};
	final private static Color s_deadSnakeColor = new Color(200,200,9);

	
	public static Color getDeadSnakeColor()
	{
		return s_deadSnakeColor;
	}
	
	public static int getMaxSnakeColor()
	{
		return s_snakesColors.length - 1;
	}
	
	public static Color getSnakeColor(int index)
	{
		if (index < 0 || index >= s_snakesColors.length)
			return Color.WHITE;
		return s_snakesColors[index];
	}

}
