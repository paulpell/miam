package org.paulpell.miam.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class DrawPanel extends JPanel
{
	
	Image i;
	
	public void setImage(Image i)
	{
		this.i = i;
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		if (g instanceof Graphics2D)
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(i, 0, 0, null);
	}
}
