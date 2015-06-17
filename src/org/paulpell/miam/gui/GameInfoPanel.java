package org.paulpell.miam.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.paulpell.miam.logic.draw.snakes.Snake;

@SuppressWarnings("serial")
public class GameInfoPanel extends JPanel
{

	JLabel FPSLabel_;
	JPanel snakeInfoPanels_;
	

	public GameInfoPanel(Vector<Snake> snakes)
	{
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		
		snakeInfoPanels_ = new JPanel();
		snakeInfoPanels_.setLayout(new GridBagLayout());
		for (Iterator<Snake> it = snakes.iterator(); it.hasNext();)
		{
			GridBagConstraints c = new GridBagConstraints();
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.insets = new Insets(0, 0, 5, 0);
			snakeInfoPanels_.add(new SnakeInfoPanel(it.next()), c);
		}
		
		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 1;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(3, 3, 3, 3);
		
		add(snakeInfoPanels_, c);
		
		FPSLabel_ = new JLabel("");
		c = new GridBagConstraints();
		c.weighty = 0;
		c.gridy = 1;
		add(FPSLabel_, c);
	
		
		Color borderColor = new Color(20, 15, 160);
		setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, borderColor));
	}
	

	public void displayActualFPS(int fps)
	{
		FPSLabel_.setText("FPS: " + fps);
	}
	
	@Override
	public void paint(Graphics g)
	{
		Component[] comps = snakeInfoPanels_.getComponents();
		for (int i=0; i<comps.length; ++i)
			((SnakeInfoPanel)comps[i]).update();

		super.paint(g);
	}

}
