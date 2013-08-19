package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import logic.draw.snakes.Snake;

@SuppressWarnings("serial")
public class InfoPanel extends JPanel {
	
	Snake snake;
	JLabel scoreLabel;
	JPanel specialItemPanel, itemsPanel;
	
	int testCount = 0;
	
	public InfoPanel(Snake s) {
		snake = s;
		BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		setLayout(layout);
		//setBorder(new CompoundBorder(null, new EmptyBorder(5,0,5,5)));
		setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, s.getColor()));
		setPreferredSize(new Dimension(100,80));
		

		add(new JLabel("Player " + s.getId()));
		scoreLabel = new JLabel("Score: " + s.getScore());
		add(scoreLabel);
		

		specialItemPanel = new JPanel();
		specialItemPanel.setPreferredSize(new Dimension(15,10));
		add(specialItemPanel);
		
		
		itemsPanel = new JPanel();
		itemsPanel.setLayout(new FlowLayout());
		add(itemsPanel);
		
	}
	
	public void update() { // updates the score and repaints the snake's items
		scoreLabel.setText("Score: " + snake.getScore());
		
		if (snake.specialItemChanged()) {
			specialItemPanel.removeAll();
			specialItemPanel.add(new JLabel(snake.getSpecialItemImageIcon()));
			specialItemPanel.doLayout(); // needed to draw the image!!
		}

		if (snake.itemsChanged()) {
			ImageIcon[] ims = snake.getItemsImageIcons();
			itemsPanel.removeAll();
			for (int i=0; i<ims.length; ++i) {
				if (ims[i] != null) {
					itemsPanel.add(new JLabel(ims[i]));
				}
			}
			itemsPanel.doLayout();
		}
		
	}
	
	public void paint(Graphics g) {
		doLayout();
		super.paint(g);
		g.setColor(snake.getColor());
		g.drawRect(0, 0, getWidth()-1, getHeight()-1);
	}
}
