package org.paulpell.miam.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.paulpell.miam.logic.draw.snakes.Snake;


@SuppressWarnings("serial")
public class SnakeInfoPanel
	extends JPanel
{
	
	Snake snake_;
	JLabel scoreLabel_;
	JPanel specialItemPanel_;
	JPanel itemsPanel_;
	
	public SnakeInfoPanel(Snake s)
	{
		snake_ = s;
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constr;
		
		setLayout(layout);
		int borderWidth = 3;
		setBorder(BorderFactory.createMatteBorder(borderWidth, borderWidth, borderWidth, borderWidth, s.getColor()));
		setPreferredSize(new Dimension(100,80));
		

		constr = new GridBagConstraints();
		constr.gridy = 0;
		constr.weighty = 0;
		constr.fill = GridBagConstraints.NONE;
		constr.anchor = GridBagConstraints.WEST;
		add(new JLabel("Player " + s.getId()), constr);	

		
		scoreLabel_ = new JLabel("Score: " + s.getScore());
		constr = new GridBagConstraints();
		constr.gridy = 1;
		constr.weighty = 0;
		constr.fill = GridBagConstraints.NONE;
		constr.anchor = GridBagConstraints.WEST;
		add(scoreLabel_, constr);		

		specialItemPanel_ = new JPanel();
		constr = new GridBagConstraints();
		constr.gridy = 2;
		constr.weighty = 1;
		constr.fill = GridBagConstraints.NONE;
		constr.anchor = GridBagConstraints.CENTER;
		add(specialItemPanel_, constr);
		
		
		itemsPanel_ = new JPanel();
		itemsPanel_.setLayout(new FlowLayout());
		constr = new GridBagConstraints();
		constr.gridy = 3;
		constr.weighty = 1;
		constr.fill = GridBagConstraints.NONE;
		constr.anchor = GridBagConstraints.CENTER;
		add(itemsPanel_, constr);
		
	}
	
	public void update() // updates the score and repaints the snake's items
	{
		scoreLabel_.setText("Score: " + snake_.getScore());
		
		if (snake_.specialItemChanged())
		{
			specialItemPanel_.removeAll();
			specialItemPanel_.add(new JLabel(snake_.getSpecialItemImageIcon()));
		}

		if (snake_.itemsChanged())
		{
			ImageIcon[] ims = snake_.getItemsImageIcons();
			itemsPanel_.removeAll();
			for (int i=0; i<ims.length; ++i)
				if (ims[i] != null)
					itemsPanel_.add(new JLabel(ims[i]));
		}
		
		validate();
	}
	
}
