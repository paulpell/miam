package org.paulpell.miam.logic;

/*
 * An ugly class containing many globals. We make sure there is only one writer for
 * each of these variables, to ensure a minimum of security. We love 'public'!
 */

public class Globals {
	

	public static boolean ACTION_DEBUG = false;
	public static boolean NETWORK_DEBUG = true;
	public static boolean CLASSIC_DEBUG = false;

	public static int FPS = 25;
	public static int FPS_MIN = 5;
	public static int FPS_MAX = 30;
	
	
	
	//****************************************************************
	//************************* SNAKES
	
	public static int SNAKE_NORMAL_SPEED = Constants.INIT_SNAKE_SPEED; // [1,10]
	public static int SNAKE_EXTRA_SPEEDUP = 4;
	public static int SNAKE_ANGLE_SPEED_FACTOR = 4;
	public static long SNAKE_TIME_BETW_TURNS_MILLIS = 100; // in classic mode
	
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
	public static long TIME_BETWEEN_EXTRA_ITEMS_MAX = 5000; // ms
	public static long TIME_BETWEEN_EXTRA_ITEMS_MIN = 3000; // ms
	

	
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//
	// NETWORK
	
	public final static int ONLINE_PORT = 13913;
	// number of players and of clients is different...
	public static int ONLINE_DEFAULT_PLAYER_MAX_NUMBER = 2;
	public static int ONLINE_DEFAULT_CLIENT_MAX_NUMBER = 5;
	
}
