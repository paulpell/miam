package net;


import logic.actions.EndSpeedupAction;
import logic.actions.EndTurnLeftAction;
import logic.actions.EndTurnRightAction;
import logic.actions.SnakeAction;
import logic.actions.SpecialAction;
import logic.actions.StartAction;
import logic.actions.StartSpeedupAction;
import logic.actions.StartTurnLeftAction;
import logic.actions.StartTurnRightAction;


// TODO whole class
public class ActionEncoder {

	
	public static byte[] encodeAction(SnakeAction a) {
		int snakeIndex = a.getSnakeIndex();
		byte[] bytes = new byte[6];
		bytes[0] = (byte)(snakeIndex >> 24);
		bytes[1] = (byte)(snakeIndex >> 16);
		bytes[2] = (byte)(snakeIndex >> 8);
		bytes[3] = (byte)(snakeIndex);
		bytes[4] = StartAction.class.isAssignableFrom(a.getClass()) ? (byte)1 : (byte)0;
		byte action = '?';
		if (a instanceof StartSpeedupAction || a instanceof EndSpeedupAction) {
			action = 's';
		}
		else if (a instanceof StartTurnLeftAction || a instanceof EndTurnLeftAction){
			action = 'l';
		}
		else if (a instanceof StartTurnRightAction || a instanceof EndTurnRightAction) {
			action = 'r';
		}
		else if (a instanceof SpecialAction) {
			action = '*'; // TODO encode special action details???
		}
		bytes[5] = action;
		return bytes;
	}
	
	
	public static SnakeAction decodeAction(byte[] bytes) {
		// TODO decode action
		int snakeIndex = (0xFF & bytes[0]) << 24;
		snakeIndex |= (0xFF & bytes[1]) << 16;
		snakeIndex |= (0xFF & bytes[2]) << 8;
		snakeIndex |= 0xFF & bytes[3];
		boolean start = bytes[4] != 0;
		char action = (char)bytes[5];
		switch (action) {
		case 's': return start ? new StartSpeedupAction(snakeIndex) : new EndSpeedupAction(snakeIndex);
		case 'l': return start ? new StartTurnLeftAction(snakeIndex): new EndTurnLeftAction(snakeIndex);
		case 'r': return start ? new StartTurnRightAction(snakeIndex): new EndTurnRightAction(snakeIndex);
		case '*': return new SpecialAction(snakeIndex);
		default: return null;
		}
	}
}
