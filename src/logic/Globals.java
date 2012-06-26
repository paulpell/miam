package logic;

import java.util.HashMap;

import java.awt.event.KeyEvent;

import logic.actions.EndSpeedupAction;
import logic.actions.EndTurnLeftAction;
import logic.actions.EndTurnRightAction;
import logic.actions.SnakeAction;
import logic.actions.StartSpeedupAction;
import logic.actions.StartTurnLeftAction;
import logic.actions.StartTurnRightAction;

import static java.awt.event.KeyEvent.*;


public class Globals {

	public static int FPS = 25;
	
	public static int SNAKE_NORMAL_SPEED = Constants.INIT_SNAKE_SPEED; // [1,10]
	public static int SNAKE_SPEEDUP_EXTRA = 4;
	public static int SNAKE_ANGLE_DIFF = 8;
	public static long SNAKE_TIME_BETW_TURNS_MILLIS = 80; 
	
	public static double SNAKE_DEFAULT_EXTRA_THICKNESS = 0; // 0 means the snake is one pixel thick

	public static int NUMBER_OF_SNAKES = 1;
	public static boolean USE_CLASSIC_SNAKE = false;

	// the following is for control keys.. let's assume max number of snakes is 4
	
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
			for (int j=0; j<3; ++j) {
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
		releasedKeyActions.put(key, releasedAction);
		return true;
	}
	public static int[][] keys = {
					{VK_LEFT, VK_RIGHT, VK_UP},
					{VK_A, VK_D, VK_W},
					{VK_J, VK_L, VK_I},
					{VK_C, VK_B, VK_F}};

	// insert the snake actions with the default key 
	private static HashMap<Integer, SnakeAction> pressedKeyActions = new HashMap<Integer, SnakeAction>();
	private static HashMap<Integer, SnakeAction> releasedKeyActions = new HashMap<Integer, SnakeAction>();
	
	static {
		for (int i=0; i<keys.length; ++i) {
			pressedKeyActions.put(keys[i][0], new StartTurnLeftAction(i));
			pressedKeyActions.put(keys[i][1], new StartTurnRightAction(i));
			pressedKeyActions.put(keys[i][2], new StartSpeedupAction(i));
			releasedKeyActions.put(keys[i][0], new EndTurnLeftAction(i));
			releasedKeyActions.put(keys[i][1], new EndTurnRightAction(i));
			releasedKeyActions.put(keys[i][2], new EndSpeedupAction(i));
		}
	}
	
}
