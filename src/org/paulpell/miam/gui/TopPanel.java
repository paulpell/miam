package org.paulpell.miam.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.paulpell.miam.logic.Constants;

@SuppressWarnings("serial")
public class TopPanel extends JPanel
	implements MouseMotionListener, MouseListener, ComponentListener
{

	int clickedX_ = -1; // -1 for a debug flag
	int clickedY_;
	boolean frameMoved_ = true; // trick to start moving frame the first time
	
	final int height_ = Constants.TOP_PANEL_HEIGHT;
	final int dragWidth_ = height_;
	final int borderThickness_ = 3;
	
	MainFrame mainFrame_;
	
	JButton gamePanelButton_;
	
	Point newMainFramePos_;
	
	public TopPanel(MainFrame mainFrame)
	{
		mainFrame_ = mainFrame;
		
		FlowLayout layout = new FlowLayout();
		setLayout(layout);
		
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
		
		doLayout();
		
		setBorder(BorderFactory.createMatteBorder(0, 0, borderThickness_, 0, new Color(10, 10, 10)));
		
		setBackground(Constants.WELCOME_COLOR);
		
		addMouseMotionListener(this);
		addMouseListener(this);
		mainFrame_.addComponentListener(this);
		
		setPreferredSize(new Dimension(Constants.DEFAULT_IMAGE_WIDTH, height_));

		newMainFramePos_ = mainFrame_.getLocation();
		
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

		int ex = e.getX();
		int ey = e.getY();
		int dx = ex - clickedX_;
		int dy = ey - clickedY_;
		if ( frameMoved_ ) {
			newMainFramePos_.x += dx;
			newMainFramePos_.y += dy;
			mainFrame_.setLocation(newMainFramePos_);
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
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	@Override
	public void componentHidden(ComponentEvent e) 
	{}

	@Override
	public void componentMoved(ComponentEvent e)
	{
		frameMoved_ = true;
		newMainFramePos_ = mainFrame_.getLocation();
	}

	@Override
	public void componentResized(ComponentEvent e)
	{}

	@Override
	public void componentShown(ComponentEvent e)
	{}

}
