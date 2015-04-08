package org.paulpell.miam.logic;


import static java.awt.event.KeyEvent.*;

public class Constants
{
	
	public static double EPSILON = .005;
	
	
	
	// reserved keys
	private static final int[] RESERVED_KEYS = {
							VK_S/*settings*/,
							VK_P/*pause*/,
							VK_N, /* new game */
							VK_SPACE, /* new game */
							VK_ESCAPE};
	
	public static boolean isKeyReserved(int key)
	{
		for (int i=0; i<RESERVED_KEYS.length; ++i)
			if (RESERVED_KEYS[i] == key)
				return true;
		return false;
	}
	
	// screen size
	public final static int DEFAULT_IMAGE_WIDTH 	= 600;
	public final static int DEFAULT_IMAGE_HEIGHT 	= 600;
	
	// directions
	public final static int DIR_LEFT 	= 0;
	public final static int DIR_UP 		= 1;
	public final static int DIR_RIGHT 	= 2;
	public final static int DIR_DOWN 	= 3; // negative y_, not down on screen
	
	
	// SNAKE
	public final static int INIT_SNAKE_SPEED = 3; 
	public final static int INIT_SNAKE_LENGTH = 60;

    public final static int SNAKE_ACTION_TURN_LEFT 	= 0;
    public final static int SNAKE_ACTION_TURN_RIGHT = 1;
    public final static int SNAKE_ACTION_SPEEDUP 	= 2;
    public final static int SNAKE_ACTION_SPECIAL 	= 3;


	public final static int MAX_NUMBER_OF_SNAKES = 4;
	
	// ITEMS
	public final static int ITEM_HIGH_PROB_WEIGHT 	= 5;
	public final static int ITEM_MID_PROB_WEIGHT 	= 3;
	public final static int ITEM_LOW_PROB_WEIGHT 	= 1;
	public final static int ITEM_NULL_PROB_WEIGHT 	= 0;
	

	
	public static int BANANA_MIN_DURATION = 15; // steps
	public static int BANANA_EXTRA_DURATION = 30; // steps
	
	public static long DEFAULT_TIME_BETWEEN_ITEMS_MAX = 4000; // ms
	public static long DEFAULT_TIME_BETWEEN_ITEMS_MIN = 2000; // ms
	
}
