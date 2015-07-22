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
	
	public void addMessage(String msg)
	{
		messages_.add(new GameMessage(msg));
	}
	
	public void paintMessages(Graphics2D g)
	{
		long delay = System.currentTimeMillis() - lastMessageTime_;
		int msgy = ybase_;
		int msgx = xbase_;
		g.setColor(new Color(230,230,10));
		g.setFont(Fonts.normalFont_);
		for (GameMessage m : messages_)
		{
			if (m.ttl_ <= 0)
				messages_.remove(m);
			else
			{
				m.ttl_ -= delay;
				g.drawString(m.msg_, msgx, msgy);
				msgy += yinc_;
			}
		}

		lastMessageTime_ = System.currentTimeMillis();
	}

}
