package org.paulpell.miam.gui;

import javax.swing.JPanel;

public abstract class AbstractDisplayPanel extends JPanel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5375212544047422268L;

	final String titleText_;
	
	
	public AbstractDisplayPanel(String title)
	{
		titleText_ = title;
	}
	
	final String getTitleText()
	{
		return titleText_;
	}
	
	abstract public void displayMessage(String message);
}
