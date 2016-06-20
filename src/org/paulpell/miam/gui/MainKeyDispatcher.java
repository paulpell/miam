package org.paulpell.miam.gui;

import java.awt.Event;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.KeyStroke;

import org.paulpell.miam.logic.Log;

public class MainKeyDispatcher implements KeyEventDispatcher
{

	MainFrame mainFrame_;
	
	
	public MainKeyDispatcher(MainFrame mainFrame)
	{
		mainFrame_ = mainFrame;
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent e)
	{
		int mod = e.getModifiers();
		
		// if alt or ctrl, let Java do the job =)
		if (0 != (mod & Event.CTRL_MASK))
			return false;
		if (0 != (mod & Event.ALT_MASK))
			return false;
		
		KeyListener kl = mainFrame_.getCurrentKeyListener(e);
		if (null != kl)
		{
			KeyStroke ks = KeyStroke.getKeyStrokeForEvent(e);
			switch (ks.getKeyEventType())
			{
			case KeyEvent.KEY_PRESSED:
				kl.keyPressed(e);
				break;
				
			case KeyEvent.KEY_RELEASED:
				kl.keyReleased(e);
				break;
				
			case KeyEvent.KEY_TYPED:
				kl.keyTyped(e);
				break;
			
			default:
				Log.logErr("Unhandled KeyEvent: " + e.toString());
			}
			return true;// we take care of dispatching
		}
		return false;
	}

}
