package org.paulpell.miam.gui;

import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_B;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_D;
import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_F;
import static java.awt.event.KeyEvent.VK_I;
import static java.awt.event.KeyEvent.VK_J;
import static java.awt.event.KeyEvent.VK_K;
import static java.awt.event.KeyEvent.VK_L;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_UP;
import static java.awt.event.KeyEvent.VK_V;
import static java.awt.event.KeyEvent.VK_W;

import java.awt.event.KeyEvent;
import java.util.HashMap;

import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.actions.EndSpeedupAction;
import org.paulpell.miam.logic.actions.EndTurnLeftAction;
import org.paulpell.miam.logic.actions.EndTurnRightAction;
import org.paulpell.miam.logic.actions.SnakeAction;
import org.paulpell.miam.logic.actions.StartSpeedupAction;
import org.paulpell.miam.logic.actions.StartTurnLeftAction;
import org.paulpell.miam.logic.actions.StartTurnRightAction;
import org.paulpell.miam.logic.actions.UseSpecialAction;

public class KeyMapping {

	
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
	public static boolean setSnakeActionKey(int snakeIndex, int action, int key)
	{
		// check whether the key is used already
		if (Constants.isKeyReserved(key))
			return false;

		for (int i=0; i<keys.length; ++i)
			for (int j=0; j<4; ++j)
				if (i != snakeIndex || j != action)
					if (key == keys[i][j])
						return false;
		
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
	public static int[][] keys =
	{
		{VK_LEFT, VK_RIGHT, VK_UP, VK_DOWN},
		{VK_A, VK_D, VK_W, VK_S},
		{VK_J, VK_L, VK_I, VK_K},
		{VK_C, VK_B, VK_F, VK_V}
	};

	// insert the snake actions with the default key 
	private static HashMap<Integer, SnakeAction> pressedKeyActions = new HashMap<Integer, SnakeAction>();
	private static HashMap<Integer, SnakeAction> releasedKeyActions = new HashMap<Integer, SnakeAction>();

	
	// initialize the hashmap with the default keys
	static
	{
		// SpecialAction only when pressed
		for (int i=0; i<keys.length; ++i)
		{
			pressedKeyActions.put(keys[i][0], new StartTurnLeftAction(i));
			pressedKeyActions.put(keys[i][1], new StartTurnRightAction(i));
			pressedKeyActions.put(keys[i][2], new StartSpeedupAction(i));
			pressedKeyActions.put(keys[i][3], new UseSpecialAction(i));
			releasedKeyActions.put(keys[i][0], new EndTurnLeftAction(i));
			releasedKeyActions.put(keys[i][1], new EndTurnRightAction(i));
			releasedKeyActions.put(keys[i][2], new EndSpeedupAction(i));
		}
	}
	
}
