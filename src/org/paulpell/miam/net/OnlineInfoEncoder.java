package org.paulpell.miam.net;

import org.paulpell.miam.logic.players.PlayerInfo;

public class OnlineInfoEncoder
{


	protected static byte[] encodePlayerInfo (PlayerInfo pi)
	{

		byte[] namebs = pi.getName().getBytes();
		int len = 9 + namebs.length;
		byte[] buf = new byte[ len ];
		NetMethods.setSubBytes(NetMethods.int2bytes(pi.getClientId()), buf, 0, 4);
		NetMethods.setSubBytes(NetMethods.int2bytes(pi.getSnakeId()), buf, 4, 8);
		buf[8] = (byte)pi.getClientLetter();
		NetMethods.setSubBytes(namebs, buf, 9, len);
		
		return buf;
	}
	
	protected static PlayerInfo decodePlayerInfo (byte[] buf)
	{
		byte[] sidbs = NetMethods.getSubBytes(buf, 0, 4);
		byte[] cidbs = NetMethods.getSubBytes(buf, 4, 8);
		char letter = (char)(0xFF & buf[8]);
		byte[] namebs = NetMethods.getSubBytes(buf, 9, buf.length);
		int sid = NetMethods.bytes2int(sidbs);
		int cid = NetMethods.bytes2int(cidbs);
		return new PlayerInfo(new String(namebs), sid, cid, letter);
	}
	
}
