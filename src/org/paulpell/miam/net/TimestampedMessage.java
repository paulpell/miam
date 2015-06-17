package org.paulpell.miam.net;

import java.util.HashMap;



public class TimestampedMessage
{
	
	public static final int CONTROL_MSG_MASK 	= 0x40;
	public static final int GAME_MSG_MASK 		= 0x80;
	
	
	
	public enum MsgTypes
	{
		// control messages
		CLIENT_LEAVES(		"CLIENT_LEAVES", 		CONTROL_MSG_MASK | 0),
		SERVER_STOPS(		"SERVER_STOPS", 		CONTROL_MSG_MASK | 1),
		CLIENT_REJECTED(	"CLIENT_REJECTED",		CONTROL_MSG_MASK | 2),
		CHAT_MESSAGE(		"CHAT_MESSAGE",			CONTROL_MSG_MASK | 3),
		ERROR(				"ERROR",				CONTROL_MSG_MASK | 4),
		SET_ID(				"SET_ID", 				CONTROL_MSG_MASK | 5),
		CLIENT_LIST(		"CLIENT_LIST",			CONTROL_MSG_MASK | 6),
		
		// game messages
		ACCEPT_ITEM(		"ACCEPT_ITEM", 			GAME_MSG_MASK | 0),
		ACTION_TAKEN(		"ACTION_TAKEN", 		GAME_MSG_MASK | 1),
		ITEM_SPAWN(			"ITEM_SPAWN", 			GAME_MSG_MASK | 2),
		GAME_OVER(			"GAME_OVER", 			GAME_MSG_MASK | 3),
		SNAKE_DIED(			"SNAKE_DIED", 			GAME_MSG_MASK | 4),
		GAME_LEVEL(			"GAME_LEVEL",			GAME_MSG_MASK | 5),
		GAME_START(			"GAME_START", 			GAME_MSG_MASK | 6),
		GAME_STEP(			"GAME_STEP", 			GAME_MSG_MASK | 7),
		GAME_VICTORY(		"GAME_VICTORY", 		GAME_MSG_MASK | 8),
		;
		
		
		public final String name_;
		public final int msgType_;
		
		private MsgTypes(String name, int type)
		{
			name_ = name;
			msgType_ = type;
		}
		
	}
	
	public final static HashMap<Integer, MsgTypes> int2type_ = new HashMap<Integer, MsgTypes>();
	public final static HashMap<MsgTypes, Integer> type2int_ = new HashMap<MsgTypes, Integer>();
	static
	{
		MsgTypes[] values = MsgTypes.values();
		for (int i=0; i<values.length; ++i)
		{
			type2int_.put(values[i], values[i].msgType_);
			int2type_.put(values[i].msgType_, values[i]);
		}
	}
	
	public final int timestamp_;
	public final MsgTypes type_;
	public final int from_;
	public final int length_;
	public final byte[] payload_;
	
	public TimestampedMessage(int timestamp, int from, MsgTypes type, byte[] message)
	{
		this.timestamp_ = timestamp;
		this.from_ = from;
		this.type_ = type;
		
		if (null == message)
			this.length_ = 0;
		else
			this.length_ = message.length;
		
		this.payload_ = message;
	}
	
	public String toString()
	{
		String msg = (length_ > 0 ? ": " + new String(payload_) : "");
		return "TimestampedMsg[t="+timestamp_ + ", cat=" + type_.name_ + " from " + (int)from_ + msg +"]";
	}
	
}
