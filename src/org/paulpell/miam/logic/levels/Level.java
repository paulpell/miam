package org.paulpell.miam.logic.levels;

import java.util.Vector;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.Game;
import org.paulpell.miam.logic.GameSettings;
import org.paulpell.miam.logic.draw.items.Item;
import org.paulpell.miam.logic.draw.walls.Wall;

public class Level
{
	
	public enum GameModesEnum
	{
		CLASSIC,
		MODERN,
		BOTH
	};
	
	protected GameSettings gameSettings_;
	
	protected GameModesEnum possibleGameMode_ = GameModesEnum.BOTH;
	
	protected int maxNumSnakes_ = Constants.MAX_NUMBER_OF_SNAKES;
	protected int minNumSnakes_ = 1;
	
	
	protected Vector <Item> initialItems_;
	
	protected Wall wall_;
	
	protected Pointd[] 	snakeStartPositions_;
	protected int[] 	snakeStartAngles_;
	
	
	protected Vector <VictoryCondition> victoryConditions_;

	public Level()
	{
		this(GameSettings.getCurrentSettings());
	}
	
	public Level(GameSettings settings)
	{
		checkSettings(settings);
		gameSettings_ = settings;
		
		// default snake positions:
		snakeStartPositions_ 	= new Pointd[maxNumSnakes_];
		snakeStartAngles_		= new int[maxNumSnakes_];

		for (int i=0; i<snakeStartPositions_.length; ++i)
		{
			snakeStartPositions_[i] = new Pointd(150, 200 - 40 * i);
			snakeStartAngles_[i] = 0;
		}
		
		initialItems_ = new Vector <Item> ();
		victoryConditions_ = new Vector <VictoryCondition> ();
	}
	
	
	public Wall getWall()
	{
		return wall_;
	}
	
	public void setWall(Wall w)
	{
		wall_ = w;
	}
	
	public Vector <Item> getInitialItems()
	{
		return initialItems_;
	}
	
	public void addInitialItem(Item it)
	{
		initialItems_.add(it);
	}
	
	public void setInitialItems(Vector <Item> items)
	{
		initialItems_ = items;
	}
	
	public Vector <VictoryCondition> getVictoryConditions()
	{
		return victoryConditions_;
	}
	
	public void addVictoryCondition(VictoryCondition vc)
	{
		victoryConditions_.add(vc);
	}
	
	public void setVictoryConditions(Vector <VictoryCondition> vcs)
	{
		victoryConditions_ = vcs;
	}
	
	
	public GameModesEnum getPossibleGameMode()
	{
		return possibleGameMode_;
	}
	
	public void setPossibleGameMode(GameModesEnum mode)
	{
		possibleGameMode_ = mode;
	}
	
	public GameModesEnum getActualGameMode()
	{
		return gameSettings_.classicMode_ ?
				GameModesEnum.CLASSIC :
				GameModesEnum.MODERN;
	}
	
	public void setActualGameMode(GameModesEnum mode)
	{
		switch (mode)
		{
		case BOTH:
			throw new IllegalArgumentException("Actual mode cannot be BOTH!!");
		case CLASSIC:
			if (possibleGameMode_ == GameModesEnum.MODERN)
				throw new IllegalArgumentException("Classic mode snake disallowed in this level!");
			gameSettings_.classicMode_ = true;
			break;
		case MODERN:
			if (possibleGameMode_ == GameModesEnum.CLASSIC)
				throw new IllegalArgumentException("Modern mode snake disallowed in this level!");
			gameSettings_.classicMode_ = false;
			break;
		}
	}
	
	public void setSnakeStartPosition(int i, Pointd p)
	{
		validateIndex(i);
		snakeStartPositions_[i] = p;
	}
	
	public Pointd getSnakeStartPosition(int i)
	{
		validateIndex(i);
		return snakeStartPositions_[i];
	}
	
	public int getSnakeStartAngle(int i)
	{
		validateIndex(i);
		return snakeStartAngles_[i];
	}
	
	public void setSnakeStartAngle(int i, int a)
	{
		validateIndex(i);
		snakeStartAngles_[i] = a;
	}
	
	public int getMaxNumberSnakes()
	{
		return maxNumSnakes_;
	}
	
	public void setMaxNumberSnakes(int n)
	{
		validateBound(n);
		maxNumSnakes_ = n;
	}

	public int getMinNumberSnakes()
	{
		return minNumSnakes_;
	}
	
	public void setMinNumberSnakes(int n)
	{
		validateBound(n);
		minNumSnakes_ = n;
	}
	
	public int getNumberSnakes()
	{
		return gameSettings_.numberOfSnakes_;
	}
	
	public void setNumberSnakes(int n)
	{
		if (n < minNumSnakes_
				|| n > maxNumSnakes_)
			throw new IllegalArgumentException(
					"Number of snakes must be less than MAX ("
					+ Constants.MAX_NUMBER_OF_SNAKES);
		
		gameSettings_.numberOfSnakes_ = n;	
	}
	
	private void validateBound(int n)
	{
		if (n < 1)
			throw new IllegalArgumentException(
					"Snakes number bound must be more than 0");	
		if (n > Constants.MAX_NUMBER_OF_SNAKES)
			throw new IllegalArgumentException(
					"Snakes number bound must be less than MAX ("
					+ Constants.MAX_NUMBER_OF_SNAKES + ")");
	}
	
	private void validateIndex(int i)
	{
		if (i < 0 || i >= maxNumSnakes_)
			throw new IllegalArgumentException("Invalid snake index: " + i);
	}

	public void setGameSettings(GameSettings settings)
	{
		checkSettings(settings);
		gameSettings_ = settings;
	}
	
	public GameSettings getGameSettings()
	{
		return gameSettings_;
	}
	
	private void checkSettings(GameSettings settings)
	{
		if (settings.numberOfSnakes_ < minNumSnakes_)
			throw new IllegalArgumentException("Too few snakes specified");
		
		if (settings.numberOfSnakes_ > maxNumSnakes_)
			throw new IllegalArgumentException("Too many snakes specified");
		
		if (settings.classicMode_ && possibleGameMode_ == GameModesEnum.MODERN)
			throw new IllegalArgumentException("Classic mode fobidden for this level");
		
		if (!settings.classicMode_ && possibleGameMode_ == GameModesEnum.CLASSIC)
			throw new IllegalArgumentException("Modern mode fobidden for this level");
		
	}

}
