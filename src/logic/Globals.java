package logic;

import java.util.HashMap;

import java.awt.event.KeyEvent;

import net.GameClient;

import logic.actions.EndSpeedupAction;
import logic.actions.EndTurnLeftAction;
import logic.actions.EndTurnRightAction;
import logic.actions.SnakeAction;
import logic.actions.SpecialAction;
import logic.actions.StartSpeedupAction;
import logic.actions.StartTurnLeftAction;
import logic.actions.StartTurnRightAction;
import logic.draw.items.ProbabilizedItem;

import static java.awt.event.KeyEvent.*;


/*
 * An ugly class containing many globals. We make sure there is only one writer for
 * each of these variables, to ensure a minimum of security. We love 'public'!
 */

public class Globals {

	public static int FPS = 25;
	
	public static Game currentGame;
	public static Control control;
	
	public static boolean online = false;
	public static GameClient gameClient;
	
	//****************************************************************
	//************************* SNAKES
	
	public static int SNAKE_NORMAL_SPEED = Constants.INIT_SNAKE_SPEED; // [1,10]
	public static int SNAKE_SPEEDUP_EXTRA = 4;
	public static int SNAKE_ANGLE_SPEED_FACTOR = 4;
	public static long SNAKE_TIME_BETW_TURNS_MILLIS = 80; // in classic mode
	
	public static boolean SNAKE_USE_WIDTH				 = true;
	public static double SNAKE_DEFAULT_EXTRA_THICKNESS = 0; // 0 means the snake is one pixel thick

	public static int NUMBER_OF_SNAKES = 1;
	public static boolean USE_CLASSIC_SNAKE = false;
	
	//**********************************************************************
	//************************* ITEMS
	
	
	public static double SCORE_ITEM_PROBABILITY = 0.7; // in the interval [0,1]
	public static boolean SCORE_ITEMS_ONLY = false;
	public static long TIME_BETWEEN_ITEMS_MAX = 5000; // ms
	public static long TIME_BETWEEN_ITEMS_MIN = 3000; // ms
	
	public static HashMap<String, ProbabilizedItem> descr2Items = new HashMap<String, ProbabilizedItem>();
	
	

	//****************************************()*************************************
	//** the following is for control keys.. let's assume max number of snakes is 4
	
	// returns string representation for a (snake,action) pair
	public static String getSnakeActionKeyRepr(int snakeIndex, int action) {
		return KeyEvent.getKeyText(keys[snakeIndex][action]);
	}
	public static SnakeAction getPressedAction(int key) {
		return pressedKeyActions.get(key);
	}
	public static SnakeAction getReleasedAction(int key) {
		return releasedKeyActions.get(key);
	}
	public static boolean setSnakeActionKey(int snakeIndex, int action, int key) {
		// check whether the key is used already
		if (Constants.isKeyReserved(key)) {
			return false;
		}
		for (int i=0; i<keys.length; ++i) {
			for (int j=0; j<4; ++j) {
				if (!(i == snakeIndex && j == action)) {
					if (key == keys[i][j]) {
						return false;
					}
				}
			}
		}
		// update hash table
		SnakeAction pressedAction = pressedKeyActions.remove(keys[snakeIndex][action]);
		SnakeAction releasedAction = releasedKeyActions.remove(keys[snakeIndex][action]); 
		keys[snakeIndex][action] = key;
		pressedKeyActions.put(key, pressedAction);
		if (action != Constants.SNAKE_ACTION_SPECIAL) {
			releasedKeyActions.put(key, releasedAction);
		}
		return true;
	}
	public static int[][] keys = {
					{VK_LEFT, VK_RIGHT, VK_UP, VK_DOWN},
					{VK_A, VK_D, VK_W, VK_S},
					{VK_J, VK_L, VK_I, VK_K},
					{VK_C, VK_B, VK_F, VK_V}};

	// insert the snake actions with the default key 
	private static HashMap<Integer, SnakeAction> pressedKeyActions = new HashMap<Integer, SnakeAction>();
	private static HashMap<Integer, SnakeAction> releasedKeyActions = new HashMap<Integer, SnakeAction>();
	
	static {
		// SpecialAction only when pressed
		for (int i=0; i<keys.length; ++i) {
			pressedKeyActions.put(keys[i][0], new StartTurnLeftAction(i));
			pressedKeyActions.put(keys[i][1], new StartTurnRightAction(i));
			pressedKeyActions.put(keys[i][2], new StartSpeedupAction(i));
			pressedKeyActions.put(keys[i][3], new SpecialAction(i));
			releasedKeyActions.put(keys[i][0], new EndTurnLeftAction(i));
			releasedKeyActions.put(keys[i][1], new EndTurnRightAction(i));
			releasedKeyActions.put(keys[i][2], new EndSpeedupAction(i));
		}
	}
	
}
