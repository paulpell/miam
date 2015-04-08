package org.paulpell.miam.gui;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import org.paulpell.miam.logic.Log;

public class MainKeyDispatcher implements KeyEventDispatcher {

	MainFrame mainFrame_;
	
	public MainKeyDispatcher(MainFrame f)
	{
		mainFrame_ = f;
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent e)
	{
		if (mainFrame_.shouldGetKeyEvents())
		{
			KeyStroke ks = KeyStroke.getKeyStrokeForEvent(e);
			switch (ks.getKeyEventType())
			{
			case KeyEvent.KEY_PRESSED:
				mainFrame_.keyPressed(e);
				break;
				
			case KeyEvent.KEY_RELEASED:
				mainFrame_.keyReleased(e);
				break;
				
			case KeyEvent.KEY_TYPED:
				mainFrame_.keyTyped(e);
				break;
			
			default:
				Log.logErr("Unhandled KeyEvent: " + e.toString());
			}
			return true;// we take care of dispatching
		}
		return false;
	}

}
