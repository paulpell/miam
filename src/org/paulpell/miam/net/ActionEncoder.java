package org.paulpell.miam.net;


import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.Log;
import org.paulpell.miam.logic.actions.EndSpeedupAction;
import org.paulpell.miam.logic.actions.EndTurnLeftAction;
import org.paulpell.miam.logic.actions.EndTurnRightAction;
import org.paulpell.miam.logic.actions.SnakeAction;
import org.paulpell.miam.logic.actions.StartAction;
import org.paulpell.miam.logic.actions.StartSpeedupAction;
import org.paulpell.miam.logic.actions.StartTurnLeftAction;
import org.paulpell.miam.logic.actions.StartTurnRightAction;
import org.paulpell.miam.logic.actions.UseSpecialAction;

public class ActionEncoder
{

	
	public static byte[] encodeAction(SnakeAction a)
	{
		int snakeIndex = a.getSnakeIndex();
		byte[] indexBytes = NetMethods.int2bytes(snakeIndex);
		byte[] bytes = new byte[6];
		System.arraycopy(indexBytes, 0, bytes, 0, 4);
		
		bytes[4] = StartAction.class.isAssignableFrom(a.getClass()) ? (byte)1 : (byte)0;
		
		byte action = '?';
		if (a instanceof StartSpeedupAction || a instanceof EndSpeedupAction)
			action = 's';
		else if (a instanceof StartTurnLeftAction || a instanceof EndTurnLeftAction)
			action = 'l';
		else if (a instanceof StartTurnRightAction || a instanceof EndTurnRightAction)
			action = 'r';
		else if (a instanceof UseSpecialAction)
			action = '*';
		
		bytes[5] = action;
		return bytes;
	}
	
	
	public static SnakeAction decodeAction(byte[] bytes)
	{
		if (bytes == null || bytes.length != 6)
			return null;

		int snakeIndex = NetMethods.bytes2int(bytes);
		boolean start = bytes[4] != 0;
		char actionChar = (char)bytes[5];
		SnakeAction ret = null;
		switch (actionChar)
		{
		case 's':
			ret = start ? new StartSpeedupAction(snakeIndex) : new EndSpeedupAction(snakeIndex); break;
		case 'l':
			ret = start ? new StartTurnLeftAction(snakeIndex): new EndTurnLeftAction(snakeIndex); break;
		case 'r':
			ret = start ? new StartTurnRightAction(snakeIndex): new EndTurnRightAction(snakeIndex); break;
		case '*':
			ret = new UseSpecialAction(snakeIndex); break;
		default: ;
		}
		if (Globals.NETWORK_DEBUG)
			Log.logMsg("Decoded action = " + ret);
		return ret;
	}
}
