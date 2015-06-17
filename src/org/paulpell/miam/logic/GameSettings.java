package org.paulpell.miam.logic;

public class GameSettings
{
	public int numberOfSnakes_;
	public boolean classicMode_;
	public boolean useWideSnakes_;
	
	public boolean createExtraItems_;

	public int snakeSpeed_;
	public int snakeExtraSpeedup_;
	public int snakeAngleSpeedFactor_;
	
	private GameSettings()
	{
		numberOfSnakes_ = Globals.NUMBER_OF_SNAKES;
		classicMode_ = Globals.USE_CLASSIC_SNAKE;
		useWideSnakes_ = Globals.SNAKE_USE_WIDTH;
		snakeSpeed_ = Globals.SNAKE_NORMAL_SPEED;
		snakeExtraSpeedup_ = Globals.SNAKE_EXTRA_SPEEDUP;
		snakeAngleSpeedFactor_ = Globals.SNAKE_ANGLE_SPEED_FACTOR;
		createExtraItems_ = Globals.CREATE_EXTRA_ITEMS;
	}
	
	public static GameSettings getCurrentSettings()
	{
		return new GameSettings();
	}
}
