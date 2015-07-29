package org.paulpell.miam.logic;


import static java.awt.event.KeyEvent.*;

import java.awt.Color;


public class Constants
{
	
	public final static double EPSILON = .005;
	
	
	public final static String LEVEL_FOLDER = "saves";
	
	public final static String DEFAULT_LEVEL_NAME = "o1";
	
	
	// reserved keys
	private static final int[] RESERVED_KEYS =
		{
			VK_S,      //settings
			VK_P,      // pause
			VK_N,      // new game 
			VK_SPACE,  // new game
			VK_ESCAPE  // general: quit, end game, etc..
		};
	
	public static boolean isKeyReserved(int key)
	{
		for (int i=0; i<RESERVED_KEYS.length; ++i)
			if (RESERVED_KEYS[i] == key)
				return true;
		return false;
	}
	
	// screen size
	public final static int DEFAULT_IMAGE_WIDTH 	= 600; // pixels
	public final static int DEFAULT_IMAGE_HEIGHT 	= 600; // pixels
	
	// directions
	public final static int DIR_LEFT 	= 0;
	public final static int DIR_UP 		= 1;
	public final static int DIR_RIGHT 	= 2;
	public final static int DIR_DOWN 	= 3; // negative y, actually the opposite of "down on screen"
	
	
	// SNAKE
	public final static int INIT_SNAKE_SPEED = 3; 
	public final static int INIT_SNAKE_LENGTH = 60;

    public final static int SNAKE_ACTION_TURN_LEFT 	= 0;
    public final static int SNAKE_ACTION_TURN_RIGHT = 1;
    public final static int SNAKE_ACTION_SPEEDUP 	= 2;
    public final static int SNAKE_ACTION_SPECIAL 	= 3;
    
    public final static int SNAKE_END_ACTION		= 16;

	public final static int ENCODED_ACTION_SIZE = 5;


	public final static int MAX_NUMBER_OF_SNAKES 	= 4;
	
	// ITEMS
	
	// how many times we try to move an item to avoid collisions
	public final static int MAX_ITEM_MOVE_TRIAL     = 50;
	
	
	public final static int ITEM_HIGH_PROB_WEIGHT 	= 5;
	public final static int ITEM_MID_PROB_WEIGHT 	= 3;
	public final static int ITEM_LOW_PROB_WEIGHT 	= 1;
	public final static int ITEM_NULL_PROB_WEIGHT 	= 0;
	

	
	public final static int BANANA_MIN_DURATION 	= 30; // steps
	public final static int BANANA_EXTRA_DURATION	= 15; // steps
	public final static int BANANA_SCORE			= 5;
	
	public final static long DEFAULT_TIME_BETWEEN_ITEMS_MAX = 3000; // ms
	public final static long DEFAULT_TIME_BETWEEN_ITEMS_MIN = 1500; // ms
	
	
	// GUI
	public final static Color WELCOME_COLOR			= new Color(120, 20, 80);
	public final static Color DRAGGER_COLOR			= new Color(110, 17, 74);
	public final static int TOP_PANEL_HEIGHT 		= 40;
	
	// OTHER
	public final static char START_GREEK_ALPHABET 	= '\u03b1';
	
	public final static int THREAD_PRIORITY_GUI 	= Thread.MAX_PRIORITY;
	
	
}
