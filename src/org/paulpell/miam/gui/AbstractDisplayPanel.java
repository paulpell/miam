package org.paulpell.miam.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public abstract class AbstractDisplayPanel
	extends JPanel
	implements KeyListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5375212544047422268L;

	JFrame parentFrame_;
	final String titleText_;
	
	
	public AbstractDisplayPanel(String title, JFrame parentFrame)
	{
		titleText_ = title;
		parentFrame_ = parentFrame;
	}
	
	final String getTitleText()
	{
		return titleText_;
	}
	
	abstract public void displayMessage(String message, boolean immediately);
	
	// return false if we can't close
	abstract public boolean canRemovePanel();
	public void panelRemoved()
	{}
	
	abstract public KeyListener getCurrentKeyListener(KeyEvent e);
}
