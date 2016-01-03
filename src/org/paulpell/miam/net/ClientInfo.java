package org.paulpell.miam.net;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.Log;

public class ClientInfo
{
	
	final private int clientId_;
	final private char letter_; // greek letter ;)
	final private String name_;
	
	//final private ArrayList <PlayerInfo> playerInfos_;

	public ClientInfo(int clientId, String canonicalName)
	{
		clientId_ = clientId;
		letter_ = (char)(Constants.START_GREEK_ALPHABET + clientId);
		name_ = canonicalName;
		
		assert name_.getBytes().length <= Byte.MAX_VALUE : "too long name";
		
	//	playerInfos_ = new ArrayList <PlayerInfo> ();
	}
	
	public int getClientId()
	{
		return clientId_;
	}
	
	public char getLetter()
	{
		return letter_;
	}
	
	public String getName()
	{
		return name_;
	}
	
	@Override
	public String toString()
	{
		return letter_ + " - " + name_;
	}
	
	private byte[] makeNetBytes()
	{
		byte[] id = NetMethods.int2bytes(clientId_);
		byte[] namebs = name_.getBytes();
		int namebslen = namebs.length;

		byte[] bs = new byte[5 + namebslen];
		NetMethods.setSubBytes(id, bs, 0, 4);
		bs[4] = (byte)namebslen;
		NetMethods.setSubBytes(namebs, bs, 5, 5 + namebslen);
		return bs;
	}
	
	public static HashMap <Integer, ClientInfo> makeNetClientList (byte[] payload)
	{
		HashMap <Integer, ClientInfo> clients = new HashMap <Integer, ClientInfo>(); 
		int index = 0;
		while (index < payload.length)
		{
			if ( index + 5 > payload.length )
			{
				Log.logErr("Bad input bytes for client list.");
				break;
			}
			byte[] idbs = NetMethods.getSubBytes(payload, index, index + 4);
			int id = NetMethods.bytes2int(idbs);
			int namelen = 0xFF & payload[index + 4];
				
			if ( index + 5 + namelen > payload.length )
			{
				Log.logErr("Bad input bytes for client name.");
				break;
			}
			byte[] namebs = NetMethods.getSubBytes(payload, index + 5, index + 5 + namelen);
			String name = new String (namebs);
			ClientInfo ci = new ClientInfo(id, name);
			clients.put(id, ci);
			index += 5 + namelen;
		}
		
		return clients;
	}
	
	public static byte[] makeClientListNetBytes(Collection<ClientInfo> clients)
	{
		LinkedList<byte[]> clientBytes = new LinkedList<byte[]> ();
		int totallen = 0;
		for ( ClientInfo ci : clients )
		{
			byte[] bs = ci.makeNetBytes();
			totallen += bs.length;
			clientBytes.add(bs);
		}
		
		byte[] allbs = new byte[totallen];
		int index = 0;
		for ( byte[] bs : clientBytes )
		{
			NetMethods.setSubBytes(bs, allbs, index, index + bs.length);
			index += bs.length;
		}
		
		return allbs;
	}

	/*public ArrayList <PlayerInfo> getPlayerInfos()
	{
		return playerInfos_;
	}
	
	public void addPlayerInfo(PlayerInfo pi)
	{
		playerInfos_.add(pi);
	}*/
}
