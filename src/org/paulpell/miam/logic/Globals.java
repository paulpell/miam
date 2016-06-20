package org.paulpell.miam.logic;

/*
 * An ugly class containing many globals. 
 */

public class Globals
{

	//****************************************************************
	//************************* DEBUG FLAGS
	public static boolean DEBUG = true;

	public static boolean ACTION_DEBUG = DEBUG && true;
	public static boolean NETWORK_DEBUG = DEBUG && false;
	public static boolean CLASSIC_DEBUG = DEBUG && false;
	public static boolean SNAKE_DEBUG = DEBUG && true;

	

	//****************************************************************
	//************************* CPU / GRAPHICS
	public static int FPS = 25;
	public static int FPS_MIN = 5;
	public static int FPS_MAX = 30;
	
	public static boolean USE_ANIMATIONS = false;
	public static boolean USE_PARTICLE_ANIMATIONS = true;
	public static boolean USE_BLURRING = false;
	
	
	//****************************************************************
	//************************* SNAKES
	
	public static int SNAKE_NORMAL_SPEED = Constants.INIT_SNAKE_SPEED; // [1,10]
	public static int SNAKE_EXTRA_SPEEDUP = 4;
	public static int SNAKE_ANGLE_SPEED_FACTOR = 4;
	public static long SNAKE_TIME_BETW_TURNS_MILLIS = 350; // in classic mode
	
	public static boolean SNAKE_USE_WIDTH				 = true;
	public static double SNAKE_DIST_BETWEEN_SEGMENTS = 10; // for nicer drawing
	public static double SNAKE_DEFAULT_EXTRA_THICKNESS = 0; // 0 means the snake is one pixel thick

	public static int NUMBER_OF_SNAKES = 1;
	public static boolean USE_CLASSIC_SNAKE = false;
	
	//**********************************************************************
	//************************* ITEMS
	

	public static int ITEM_START_NUMBER = 10;	// how many items when the game starts
	
	public static double SCORE_ITEM_PROBABILITY = 0.7; // in the interval [0,1]
	public static boolean SCORE_ITEMS_ONLY = false;
	public static boolean CREATE_EXTRA_ITEMS = true;
	

	

	
	//****************************************************************
	//************************* SNAKES
	public static int GUI_MOVE_WINDOW_STEP_PIXELS = 5;
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//
	// NETWORK
	
	public final static int ONLINE_PORT = 13913;
	// number of players and of clients is different...
	public static int ONLINE_DEFAULT_MAX_PLAYER_NUMBER = 2;
	public static int ONLINE_DEFAULT_CLIENT_MAX_NUMBER = 5;
	
}
