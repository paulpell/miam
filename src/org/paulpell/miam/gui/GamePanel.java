package org.paulpell.miam.gui;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.Control;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.draw.Drawable;
import org.paulpell.miam.logic.draw.snakes.Snake;

class GameMessage
{
	public final String msg_;
	public int ttl_ = Globals.GAME_MESSAGE_TLL; // ms
	
	
	public GameMessage(String msg)
	{
		this.msg_ = msg;
	}
}


@SuppressWarnings("serial")
public class GamePanel extends AbstractDisplayPanel
{
	
	Control control_;
	
	JPanel infoPanel_;
	JPanel snakeInfoPanels_;
	DrawPanel drawPanel_;

	// two flags for drawing stuff
	boolean pause_ = false;
	boolean gameover_ = false;
	
	Vector<GameMessage> messages_ = new Vector<GameMessage>();
	long lastDrawTime_;
	
	

	public GamePanel(Control control)
	{
		super("Snakesss");
		
		control_ = control;

		
		setLayout(new BorderLayout());
		
		drawPanel_ = new DrawPanel();
		add(drawPanel_, BorderLayout.WEST);
		doLayout();
	}
	
	// display info for each player/snake
	public void resetForNewGame(Vector<Snake> snakes, Dimension gamePanelSize)
	{
		if (infoPanel_ != null)
			remove(infoPanel_);
		
		infoPanel_ = new JPanel();
		snakeInfoPanels_ = new JPanel();
		snakeInfoPanels_.setLayout(new GridBagLayout());
		for (Iterator<Snake> it = snakes.iterator(); it.hasNext();)
		{
			GridBagConstraints c = new GridBagConstraints();
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.insets = new Insets(0, 0, 5, 0);
			snakeInfoPanels_.add(new InfoPanel(it.next()), c);
		}
		
		infoPanel_.add(snakeInfoPanels_, BorderLayout.NORTH);

		Color borderColor = new Color(20, 15, 160);
		infoPanel_.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, borderColor));
		
		
		add(infoPanel_, BorderLayout.EAST);
		
		drawPanel_.setPreferredSize(gamePanelSize);
	}
	
	public void setPause(boolean pause)
	{
		this.pause_ = pause;
		repaint();
	}
	
	public void setGameover(boolean b)
	{
		gameover_ = b;
		repaint();
	}
	/* Drawing methods ******************************************************
	 * We will paint on the BufferedImage image_, which will be displayed on drawPanel_
	 */
	
	// paints the list of objects on a black background
	private void draw_game()
	{
		// draw the Drawables
		int width = getWidth();
		int height = getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics imGr = image.createGraphics();
		imGr.setColor(new Color(0,0,0));
		imGr.fillRect(0, 0, width, height);
		
		for (Iterator<Drawable> e = control_.getDrawablesIterator(); e.hasNext();)
			e.next().draw(imGr);
		
		if (pause_)
		{
			Font font = imGr.getFont();
			font.deriveFont(font.getSize2D() * 2);
			imGr.setFont(font);
			for (int i=0; i < 5; i++)
			{
				imGr.setColor(new Color(100 + 10 * i, 20 + 20 * i, 30 + 30 * i));
				imGr.drawString("PAUSE", (int)(3 * width / 8) + i, height / 2 - 100 + i);
			}
		}

		if (gameover_)
		{
			imGr.setColor(new Color(255,20,30));
			imGr.drawString("GAME OVER", width / 2 - 75, height / 2 - 100);
			imGr.drawString("n / space for new", width / 2 - 75, height / 2 - 70);
			imGr.drawString("ESC for the main menu", width / 2 - 75, height / 2 - 40);
		}
		
		long delay = System.currentTimeMillis() - lastDrawTime_;
		int msgy = getHeight() - 10;
		for (Iterator<GameMessage> it = messages_.iterator(); it.hasNext(); )
		{
			GameMessage gmsg = it.next();
			if (gmsg.ttl_ < 0)
				it.remove();
			else
			{
				gmsg.ttl_ -= delay;
				imGr.drawString(gmsg.msg_, 10, msgy);
				msgy -= 15;
			}
		}
		
		drawPanel_.setImage(image);
		drawPanel_.repaint();
		

		lastDrawTime_ = System.currentTimeMillis();
	}
	
	public void paint(Graphics g)
	{
		draw_game();
		
		if (infoPanel_ != null)
		{
			Component[] comps = snakeInfoPanels_.getComponents();
			for (int i=0; i<comps.length; ++i)
				((InfoPanel)comps[i]).update();
			infoPanel_.repaint();
		}
	}


	@Override
	public void displayMessage(String message)
	{
		// TODO Auto-generated method stub: GamePanel.displayMessage
		
	}
	



}
