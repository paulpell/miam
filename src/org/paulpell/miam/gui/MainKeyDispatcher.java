package org.paulpell.miam.gui;

import java.awt.Event;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.KeyStroke;

import org.paulpell.miam.logic.Control;
import org.paulpell.miam.logic.Log;

public class MainKeyDispatcher implements KeyEventDispatcher
{

//	Control control_;
	MainFrame mainFrame_;
	
	
	public MainKeyDispatcher(MainFrame mainFrame)//Control control)
	{
		mainFrame_ = mainFrame;
		//control_ = control;
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent e)
	{
		int modctrl = Event.CTRL_MASK;
		int altctrl = Event.ALT_MASK;
		int mod = e.getModifiers();
		Log.logErr("Keyev: mod=" + mod + ", ctrl=" + modctrl + ", alt=" + altctrl);
		
		// if alt or ctrl, let Java do the job =)
		if (0 != (mod & Event.CTRL_MASK))
			return false;
		if (0 != (mod & Event.ALT_MASK))
			return false;
		
		KeyListener kl = mainFrame_.getCurrentKeyListener(e);
		//KeyListener kl = control_.dispatchKeyEvent(e);
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
