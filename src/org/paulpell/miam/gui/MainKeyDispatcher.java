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

	Control control_;
	
	
	public MainKeyDispatcher(Control control)
	{
		control_ = control;
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent e)
	{
		boolean ctrl = 0 != (e.getModifiers() & Event.CTRL_MASK);
		
		
		KeyListener kl = control_.whoShouldReceiveKeyEvents(ctrl);
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
