package org.paulpell.miam.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;

import org.paulpell.miam.logic.Fonts;







public class MessagePainter
{
	class GameMessage
	{
		final String msg_;
		long ttl_; // [ms], how long it is displayed
		
		protected  GameMessage(String message, long ttl)
		{
			msg_ = message;
			ttl_ = ttl;
		}
		
		// 5 seconds ttl constructor
		protected GameMessage(String message)
		{
			this (message, 5000);
		}

		
		@Override
		public String toString()
		{
			String str = "Msg[ttl=" + ttl_ + ";";
			if (msg_.length() > 10)
				str += msg_.substring(0, 10);
			else
				str += msg_;
			return str + "]";
		}
	}
	

	
	LinkedList <GameMessage> messages_;
	long lastMessageTime_;
	
	int ybase_;
	int xbase_;
	int yinc_;

	public MessagePainter(int xbase, int ybase, int yinc)
	{
		xbase_ = xbase;
		ybase_ = ybase;
		yinc_ = yinc;
		messages_ = new LinkedList <GameMessage> ();
		lastMessageTime_= System.currentTimeMillis();
	}
	
	public boolean hasMessages()
	{
		synchronized (messages_) {
			return ! messages_.isEmpty();
		}
	}
	
	public void addMessage(String msg)
	{
		synchronized (messages_) {
			messages_.add(new GameMessage(msg));
		}
	}
	
	public void clearMessages()
	{
		synchronized (messages_) {
			messages_.clear();
		}
	}
	
	public void paintMessages(Graphics2D g)
	{
		//int width = g.getFontMetrics().stringWidth(text);
		if (hasMessages()) {
			long delay = System.currentTimeMillis() - lastMessageTime_;
			int msgy = ybase_;
			int msgx = xbase_;
			g.setColor(new Color(230,230,10));
			g.setFont(Fonts.normalFont_);
			LinkedList <GameMessage> msgs_copy = copyOverMessages();
			int nMsgs = msgs_copy.size();
			for (int i=0; i<nMsgs; ++i)
			{
				GameMessage m = msgs_copy.get(i);
				m.ttl_ -= delay;
				g.drawString(m.msg_, msgx, msgy);
				msgy += yinc_;
			}
			// those with ttl > 0: Put back in same order
			synchronized (messages_) {
				messages_.addAll(msgs_copy);
			}
		}
		lastMessageTime_ = System.currentTimeMillis();
	}
	
	// copies all items from messages_ to the returned list
	// without those whose ttl is <= 0
	private LinkedList <GameMessage> copyOverMessages()
	{
		LinkedList <GameMessage> msgs_copy = new LinkedList <GameMessage> ();
		synchronized (messages_) {
			for (GameMessage m : messages_)
				if (m.ttl_ > 0)
					msgs_copy.add(m);
			messages_.clear();
		}
		return msgs_copy;
	}
}
