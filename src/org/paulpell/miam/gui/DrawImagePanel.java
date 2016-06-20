package org.paulpell.miam.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class DrawImagePanel extends JPanel
{
	
	Image i;
	
	public void setImage(Image i)
	{
		this.i = i;
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		g.drawImage(i, 0, 0, null);
	}
	
	public void paintImmediately()
	{
		Dimension d = getSize();
		paintImmediately(0, 0, d.width, d.height);
	}
}
