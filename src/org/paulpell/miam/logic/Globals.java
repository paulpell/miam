package org.paulpell.miam.logic;

import java.util.HashMap;

import java.awt.event.KeyEvent;

import org.paulpell.miam.logic.actions.EndSpeedupAction;
import org.paulpell.miam.logic.actions.EndTurnLeftAction;
import org.paulpell.miam.logic.actions.EndTurnRightAction;
import org.paulpell.miam.logic.actions.SnakeAction;
import org.paulpell.miam.logic.actions.StartSpeedupAction;
import org.paulpell.miam.logic.actions.StartTurnLeftAction;
import org.paulpell.miam.logic.actions.StartTurnRightAction;
import org.paulpell.miam.logic.actions.UseSpecialAction;
import org.paulpell.miam.logic.draw.items.ProbabilizedItem;
import org.paulpell.miam.net.Client;



import static java.awt.event.KeyEvent.*;


/*
 * An ugly class containing many globals. We make sure there is only one writer for
 * each of these variables, to ensure a minimum of security. We love 'public'!
 */

public class Globals {
	
	
	public static boolean NETWORK_DEBUG = true;
	public static boolean CLASSIC_DEBUG = true;

	public static int FPS = 25;
	
	public static Game currentGame;
	public static Control control;
	


	
	public static int GAME_MESSAGE_TLL = 10000; // [ms], how long a msg is displayed
	
	
	
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
	public static long TIME_BETWEEN_ITEMS_MAX = 5000; // ms
	public static long TIME_BETWEEN_ITEMS_MIN = 3000; // ms
	

	
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//
	// NETWORK
	
	public final static int ONLINE_PORT = 13913;
	public static int ONLINE_DEFAULT_PLAYER_NUMBER = 2;
}
