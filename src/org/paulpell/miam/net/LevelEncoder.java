package org.paulpell.miam.net;


import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.GameSettings;
import org.paulpell.miam.logic.draw.walls.Wall;
import org.paulpell.miam.logic.levels.Level;


public class LevelEncoder
{

	
	// items are sent at creation time
	public static byte[] encodeLevel(Level level)
			throws Exception
	{
		
		// first compute total length in bytes.. not optimal..
		int byteslen = 0;
		//encoded += new String(NetMethods.int2bytes(wallLen)) + wallStr;
		
				
		
		//String encoded = "";
		
		GameSettings settings = level.getGameSettings();
		
		
		// first, general settings: snake speed, classic mode?
		/*encoded += settings.classicMode_ ? '1' : '0';
		encoded += new String(NetMethods.int2bytes(settings.snakeSpeed_));
		encoded += settings.useWideSnakes_ ? '1' : '0';
		encoded += new String(NetMethods.int2bytes(settings.numberOfSnakes_));
		encoded += new String(NetMethods.int2bytes(settings.snakeExtraSpeedup_));
		encoded += new String(NetMethods.int2bytes(settings.snakeAngleSpeedFactor_));
		*/
		byteslen += 18; // 18 bytes for gameSettings
		
		// then snake start positions
		for (int i=0; i<settings.numberOfSnakes_; ++i)
		{
			Pointd p = level.getSnakeStartPosition(i);
			byte[] pbs = NetMethods.point2bytes(p);
			byteslen += 5 + pbs.length; // 5: 1 for total length, 4 for start angle
			//int a = level.getSnakeStartAngle(i);
			//String e = new String(NetMethods.int2bytes(a));
			//e += NetMethods.point2str(p);
			//encoded += (char)e.length() + e;
		}

		// and wall

		byte[] wallBytes = (WallEncoder.encodeWall(level.getWall())).getBytes();
		int wallLen = wallBytes.length;
		byteslen += wallLen + 4; // 4 to encode the wall length itself
		
		byte[] encoded = new byte[byteslen];
		
		encoded[0] = (byte) (settings.classicMode_ ? 1 : 0);
		//encoded += new String(NetMethods.int2bytes(settings.snakeSpeed_));
		byte[] gen = NetMethods.int2bytes(settings.snakeSpeed_);
		NetMethods.setSubBytes(gen, encoded, 1, 5);
		encoded[5] = (byte) (settings.useWideSnakes_ ? 1 : 0);
//		encoded += new String(NetMethods.int2bytes(settings.numberOfSnakes_));
		gen = NetMethods.int2bytes(settings.numberOfSnakes_);
		NetMethods.setSubBytes(gen, encoded, 6, 10);
//		encoded += new String(NetMethods.int2bytes(settings.snakeExtraSpeedup_));
		gen = NetMethods.int2bytes(settings.snakeExtraSpeedup_);
		NetMethods.setSubBytes(gen, encoded, 10, 14);
//		encoded += new String(NetMethods.int2bytes(settings.snakeAngleSpeedFactor_));
		gen = NetMethods.int2bytes(settings.snakeAngleSpeedFactor_);
		NetMethods.setSubBytes(gen, encoded, 14, 18);
		

		int index = 18;
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
		
		if (index + 4 + wallLen != encoded.length)
			throw new IllegalArgumentException("BAD BAD");
		
		NetMethods.setSubBytes(NetMethods.int2bytes(wallLen), encoded, index, index + 4);
		NetMethods.setSubBytes(wallBytes, encoded, index + 4, index + 4 + wallLen);
		
		
		return encoded;
	}
	
	//public static RemoteGame decodeSettings(Control control, Client client, String encoded)
	public static Level decodeLevel(byte[] encoded)
			throws Exception
	{
		// read settings
		GameSettings settings = GameSettings.getCurrentSettings();
		settings.classicMode_ = encoded[0] == (byte)1;
		//settings.snakeSpeed_ = NetMethods.bytes2int(encoded.substring(1, 5).getBytes());
		settings.snakeSpeed_ = NetMethods.bytes2int(NetMethods.getSubBytes(encoded, 1, 5));
		settings.useWideSnakes_ = encoded[5] == (byte)1;
		//settings.numberOfSnakes_ = NetMethods.bytes2int(encoded.substring(6, 10).getBytes());
		//settings.snakeExtraSpeedup_ = NetMethods.bytes2int(encoded.substring(10,14).getBytes());
		//settings.snakeAngleSpeedFactor_ = NetMethods.bytes2int(encoded.substring(14,18).getBytes());
		settings.numberOfSnakes_ = NetMethods.bytes2int(NetMethods.getSubBytes(encoded, 6, 10));
		settings.snakeExtraSpeedup_ = NetMethods.bytes2int(NetMethods.getSubBytes(encoded, 10, 14));
		settings.snakeAngleSpeedFactor_ = NetMethods.bytes2int(NetMethods.getSubBytes(encoded, 14, 18));
		
		Level level = new Level(settings);

		// read start positions
		//encoded = encoded.substring(18);
		int index = 18;
		for (int i=0; i<settings.numberOfSnakes_; ++i)
		{
		//	int l = 0xFF & encoded.charAt(0);
			int l = 0xFF & encoded[index];
			//int a = NetMethods.bytes2int(encoded.substring(1,5).getBytes());
			int a = NetMethods.bytes2int(NetMethods.getSubBytes(encoded, index + 1, index + 5));
			//Pointd p = NetMethods.str2point(encoded.substring(5, 1 + l));
			Pointd p = NetMethods.bytes2point(NetMethods.getSubBytes(encoded, index + 5, index + 1 + l));
			level.setSnakeStartAngle(i, a);
			level.setSnakeStartPosition(i, p);
			//encoded = encoded.substring(1 + l);
			index += 1 + l;
		}
		
		//int wallLen = NetMethods.bytes2int(encoded.substring(0,4).getBytes());
		//Wall wall = WallEncoder.decodeWall(encoded.substring(4, wallLen + 4));
		int wallLen = NetMethods.bytes2int(NetMethods.getSubBytes(encoded, index, index + 4));
		String wallStr = new String(NetMethods.getSubBytes(encoded, index + 4, index + 4 + wallLen));
		Wall wall = WallEncoder.decodeWall(wallStr);
		level.setWall(wall);
		
		
		//RemoteGame game = new RemoteGame(control, level);
		
		return level;
		
	}
}
