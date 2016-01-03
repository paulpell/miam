package org.paulpell.miam.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.paulpell.gamezguitk.Button;
import org.paulpell.miam.logic.Constants;

@SuppressWarnings("serial")
public class TopPanel extends JPanel
	implements MouseMotionListener, MouseListener
{

	int clickedX_;
	int clickedY_;
	boolean isValidFrameDragging_ = false;
	
	final int height_ = Constants.TOP_PANEL_HEIGHT;
	final int dragWidth_ = height_;
	final int borderThickness_ = 3;
	
	MainFrame mainFrame_;
	
	JButton gamePanelButton_;
	
	public TopPanel(MainFrame mainFrame)
	{
		mainFrame_ = mainFrame;
		
		gamePanelButton_ = new JButton("Main menu");
		gamePanelButton_.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				mainFrame_.control_.showWelcomePanel();
			}
		});
		add(gamePanelButton_);
		

		
		////////////// TEST
		
		Button b = new Button("exit", null);
		b.setBackgroundColor(new Color(170, 23, 99, 58));
		add (b);
		AbstractAction a = new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				System.exit(0);
			}
		};
		b.addAction(a);
				
				
				

		doLayout();
		
		
		setBorder(BorderFactory.createMatteBorder(0, 0, borderThickness_, 0, new Color(10, 10, 10)));
		
		setBackground(Constants.WELCOME_COLOR);
		
		addMouseMotionListener(this);
		addMouseListener(this);
		
		setPreferredSize(new Dimension(Constants.DEFAULT_IMAGE_WIDTH, height_));
		
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		
		g.setColor(Constants.DRAGGER_COLOR);
		g.fillRect(0, 0, dragWidth_, height_ - borderThickness_);
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (isValidFrameDragging_)
		{
			int translationX = e.getX() - clickedX_;
			int translationY = e.getY() - clickedY_;
			
			Point p = mainFrame_.getLocation();
			mainFrame_.setLocation(p.x + translationX, p.y + translationY);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{}

	@Override
	public void mouseClicked(MouseEvent e)
	{}

	@Override
	public void mouseEntered(MouseEvent e)
	{}

	@Override
	public void mouseExited(MouseEvent e)
	{}

	@Override
	public void mousePressed(MouseEvent e)
	{
		clickedX_ = e.getX();
		clickedY_ = e.getY();
		isValidFrameDragging_ = clickedX_ < dragWidth_;
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{}

}
