package org.paulpell.miam.net;


import java.util.Vector;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.GameSettings;
import org.paulpell.miam.logic.draw.items.Item;
import org.paulpell.miam.logic.draw.walls.Wall;
import org.paulpell.miam.logic.levels.Level;
import org.paulpell.miam.logic.levels.VictoryCondition;


public class LevelEncoder
{

	
	// items are sent at creation time
	public static byte[] encodeLevel(Level level)
			throws Exception
	{
		
		// first compute total length in bytes.. not optimal..
		int byteslen = 0;

		
		// first, general settings: snake speed, classic mode?
		
		GameSettings settings = level.getGameSettings();
		byte[] settingsbs = encodeSettings(settings);
		
		
		byteslen += settingsbs.length;
		
		// then snake start positions
		for (int i=0; i<settings.numberOfSnakes_; ++i)
		{
			Pointd p = level.getSnakeStartPosition(i);
			byte[] pbs = NetMethods.point2bytes(p);
			byteslen += 5 + pbs.length; // 5: 1 for total length, 4 for start angle
		}

		// wall

		byte[] wallBytes = (WallEncoder.encodeWall(level.getWall())).getBytes();
		int wallLen = wallBytes.length;
		byteslen += wallLen + 4; // 4 to encode the wall length itself
		
		// items
		
		Vector <Item> items = level.getInitialItems();
		int itemsLen = 0;
		byte[] tempItemBuffer = new byte[512 * items.size()];
		for (Item i : items)
		{
			byte[] enc = ItemEncoder.encodeItem(i);
			assert enc.length <= (0xFF & Byte.MAX_VALUE) : "Too big item";
			tempItemBuffer[itemsLen] = (byte)enc.length;
			NetMethods.setSubBytes(enc, tempItemBuffer, itemsLen + 1, itemsLen + 1 + enc.length);
			itemsLen += 1 + enc.length;
		}
		
		byteslen += itemsLen + 1;
		
		
		// victory conditions
		Vector <VictoryCondition> vcs = level.getVictoryConditions();
		int vcsLen = 0;
		byte[] tempVictoryCondBuffer = new byte[512 * vcs.size()];
		for (VictoryCondition vc : vcs)
		{
			byte[] enc = VictoryConditionEncoder.encodeVictoryCondition(vc);
			assert enc.length <= (0xFF & Byte.MAX_VALUE) : "Too big item";
			tempVictoryCondBuffer[vcsLen] = (byte)enc.length;
			NetMethods.setSubBytes(enc, tempVictoryCondBuffer, vcsLen + 1, vcsLen + 1 + enc.length);
			vcsLen += 1 + enc.length;
		}
		
		byteslen += vcsLen + 1;
		
		// now create the actual buffer
		
		byte[] encoded = new byte[byteslen];
		int index = 0; // where to write
		
		// fill settings
		NetMethods.setSubBytes(settingsbs, encoded, 0, settingsbs.length);
		
		// fill snake positions
		index += settingsbs.length;
		
		for (int i=0; i<settings.numberOfSnakes_; ++i)
		{
			Pointd p = level.getSnakeStartPosition(i);
			byte[] pbs = NetMethods.point2bytes(p);
			byte[] abs = NetMethods.int2bytes(level.getSnakeStartAngle(i));
			int len = pbs.length + abs.length;
			encoded[index] = (byte)len;
			NetMethods.setSubBytes(abs, encoded, index + 1, index + 5);
			NetMethods.setSubBytes(pbs, encoded, index + 5, index + 1 + len);
			index += len + 1;
		}
		
		// fill wall
		NetMethods.setSubBytes(NetMethods.int2bytes(wallLen), encoded, index, index + 4);
		NetMethods.setSubBytes(wallBytes, encoded, index + 4, index + 4 + wallLen);
		
		// fill items
		index += 4 + wallLen;
		assert items.size() <= (0xFF & Byte.MAX_VALUE) : "Too many items!";
		encoded[index] = (byte)items.size();
		NetMethods.setSubBytes(tempItemBuffer, encoded, index + 1, index + 1 + itemsLen);
		
		// fill victory conditions
		index += 1 + itemsLen;
		assert vcs.size() <= (0xFF & Byte.MAX_VALUE) : "Too many victory conditions!";
		encoded[index] = (byte)vcs.size();
		NetMethods.setSubBytes(tempVictoryCondBuffer, encoded, index + 1, index + 1 + vcsLen);
		
		assert (index + 1 + vcsLen) == byteslen : "Bad programmer could not do good job";

		return encoded;
	}
	
	public static Level decodeLevel(byte[] encoded)
			throws Exception
	{
		
		GameSettings settings = readSettings(encoded, 0);
		
		Level level = new Level(settings, "RemoveLevel");

		// read start positions
		int index = 18;
		for (int i=0; i<settings.numberOfSnakes_; ++i)
		{
			int l = 0xFF & encoded[index];
			int a = NetMethods.bytes2int(NetMethods.getSubBytes(encoded, index + 1, index + 5));
			Pointd p = NetMethods.bytes2point(NetMethods.getSubBytes(encoded, index + 5, index + 1 + l));
			level.setSnakeStartAngle(i, a);
			level.setSnakeStartPosition(i, p);
			index += 1 + l;
		}
		
		int wallLen = NetMethods.bytes2int(NetMethods.getSubBytes(encoded, index, index + 4));
		String wallStr = new String(NetMethods.getSubBytes(encoded, index + 4, index + 4 + wallLen));
		Wall wall = WallEncoder.decodeWall(wallStr);
		level.setWall(wall);
		
		index += 4 + wallLen;
		
		if (index < encoded.length)
		{
			int itemsNum = 0xFF & encoded[index];
			++index;
			
			Vector <Item> items = new Vector <Item> ();
			for (int i=0; i<itemsNum; ++i)
			{
				int itemLen = 0xFF & encoded[index];
				byte[] itembs = NetMethods.getSubBytes(encoded, index + 1, index + 1 + itemLen);
				items.add(ItemEncoder.decodeItem(itembs));
				index += 1 + itemLen;
			}
			
			level.setInitialItems(items);
		}
		
		if (index < encoded.length)
		{
			int vcsNum = 0xFF & encoded[index];
			++index;
			
			for (int i=0; i<vcsNum; ++i)
			{
				int vcLen = encoded[index];
				byte[] vcbs = NetMethods.getSubBytes(encoded, index + 1, index + 1 + vcLen);
				VictoryCondition  vc = VictoryConditionEncoder.decodeVictoryCondition(vcbs);
				level.addVictoryCondition(vc);
			}
		}
		
		return level;
		
	}
	
	
	private static GameSettings readSettings(byte[] encoded, int offset)
	{
		GameSettings settings = GameSettings.getCurrentSettings();
		
		settings.classicMode_ =
				encoded[offset] == (byte)1;
		settings.snakeSpeed_ =
				NetMethods.bytes2int(NetMethods.getSubBytes(encoded, offset + 1, offset + 5));
		settings.useWideSnakes_ =
				encoded[offset + 5] == (byte)1;
		settings.numberOfSnakes_ =
				NetMethods.bytes2int(NetMethods.getSubBytes(encoded, offset + 6, offset + 10));
		settings.snakeExtraSpeedup_ =
				NetMethods.bytes2int(NetMethods.getSubBytes(encoded, offset + 10, offset + 14));
		settings.snakeAngleSpeedFactor_ =
				NetMethods.bytes2int(NetMethods.getSubBytes(encoded, offset + 14, offset + 18));
		
		return settings;
	}
	
	private static byte[] encodeSettings(GameSettings settings)
	{
		byte[] encoded = new byte[18];
		encoded[0] =
				(byte) (settings.classicMode_ ? 1 : 0);
		byte[] gen = NetMethods.int2bytes(settings.snakeSpeed_);
		NetMethods.setSubBytes(gen, encoded, 1, 5);
		
		encoded[5] =
				(byte) (settings.useWideSnakes_ ? 1 : 0);
		
		gen = NetMethods.int2bytes(settings.numberOfSnakes_);
		NetMethods.setSubBytes(gen, encoded, 6, 10);
		
		gen = NetMethods.int2bytes(settings.snakeExtraSpeedup_);
		NetMethods.setSubBytes(gen, encoded, 10, 14);
		
		gen = NetMethods.int2bytes(settings.snakeAngleSpeedFactor_);
		NetMethods.setSubBytes(gen, encoded, 14, 18);
		
		return encoded;
	}
}
