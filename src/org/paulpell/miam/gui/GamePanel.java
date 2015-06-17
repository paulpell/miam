package org.paulpell.miam.gui;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JPanel;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.Arith;
import org.paulpell.miam.logic.Control;
import org.paulpell.miam.logic.Utils;
import org.paulpell.miam.logic.draw.Drawable;
import org.paulpell.miam.logic.draw.particles.VictoryParticleAnimation;
import org.paulpell.miam.logic.draw.particles.VictoryParticleAnimator;
import org.paulpell.miam.logic.draw.snakes.Snake;


class GameMessage
{
	final String msg_;
	long ttl_; // [ms], how long it is displayed
	
	protected  GameMessage(String message, long ttl)
	{
		msg_ = message;
		ttl_ = ttl;
	}
	
	// 5 seconds ttl constructor
	protected GameMessage(String message)
	{
		this (message, 5000);
	}
}


@SuppressWarnings("serial")
public class GamePanel extends AbstractDisplayPanel
{
	
	Control control_;
	
	//JPanel infoPanel_;
	GameInfoPanel infoPanel_;
	JPanel snakeInfoPanels_;
	DrawImagePanel imagePanel_;
	static Font bigFont_;
	static Font normalFont_;

	// flags for drawing stuff
	boolean pause_ = false;
	boolean pauseWasDrawn_ = false;
	boolean gameover_ = false;
	boolean gameoverWasDrawn_ = false;
	boolean victory_ = false;
	boolean victoryWasDrawn_ = false;
	int victoryStartx_ = 120;
	int victoryStarty_ = 150;
	int victoryStartDelta_ = 150;
	
	//BufferedImage staticVictoryImage_ = null;
	
	BufferedImage image_;
	
	Vector<Color> victoryColors_;
	
	LinkedList <GameMessage> messages_;
	long lastMessageTime_;
	
	

	//Vector <VictoryParticleAnimation> particleAnims_;
	VictoryParticleAnimator particleAnimator_;

	public GamePanel(Control control)
	{
		super("Snakesss");
		
		control_ = control;

		messages_ = new LinkedList <GameMessage> ();
		lastMessageTime_ = System.currentTimeMillis();

		
		setLayout(new BorderLayout());
		
		imagePanel_ = new DrawImagePanel();
		add(imagePanel_, BorderLayout.WEST);
		doLayout();
	}
	
	// display info for each player/snake
	public void resetForNewGame(Vector<Snake> snakes, Dimension gamePanelSize)
	{
		if (infoPanel_ != null)
			remove(infoPanel_);
		
		infoPanel_ = new GameInfoPanel(snakes);
		
		add(infoPanel_, BorderLayout.EAST);
		
		imagePanel_.setPreferredSize(gamePanelSize);
	}
	
	public void setPause(boolean pause)
	{
		pause_ = pause;
		pauseWasDrawn_ = false;
		repaint();
	}
	
	public void setGameover(boolean b)
	{
		gameover_ = b;
		gameoverWasDrawn_ = false;
		repaint();
	}
	/* Drawing methods ******************************************************
	 * We will paint on the BufferedImage image_, which will be displayed on imagePanel_
	 */
	
	// paints the list of objects on a black background
	private void draw_game()
	{
		
		boolean shouldRedraw =
				(!victory_ && !pause_ && !gameover_) // normal mode
				|| !isValid()
				|| (victory_ && !victoryWasDrawn_)
				|| (gameover_ && !gameoverWasDrawn_)
				|| (pause_ && !pauseWasDrawn_)
				;
		
		BufferedImage image;
		Graphics2D imGr;
		int width = getWidth();
		int height = getHeight();
		if (width == 0 && height == 0)
			System.out.println("all 0");
		if (shouldRedraw)
		{
			// re-create a new buffered image
			image_ = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			image = image_;
			
			imGr = image.createGraphics();
			imGr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	
			if (bigFont_ == null)
			{
				Font font = imGr.getFont();
				normalFont_ = font;
				bigFont_ = font.deriveFont(font.getSize2D() * 4);
			}
			
			// background
			imGr.setColor(new Color(0,0,0));
			imGr.fillRect(0, 0, width, height);
			
			// all the drawables
			for (Iterator<Drawable> e = control_.getDrawablesIterator(); e.hasNext();)
				e.next().draw(imGr);


			if (gameover_)
			{
				imGr.setFont(bigFont_);
				imGr.setColor(new Color(255,20,30));
				int x = 120;
				imGr.drawString("GAME OVER...", x, 200);
				imGr.drawString("Space for new", x, 300);
				imGr.drawString("ESC to main menu", x, 400);
				gameoverWasDrawn_ = true;
			}
			else if (pause_)
			{
				imGr.setFont(bigFont_);
				int pausex = width / 4;
				int pausey = height / 2 - 100;
				for (int i=0; i < 5; i++)
				{
					imGr.setColor(new Color(100 + 10 * i, 20 + 20 * i, 30 + 30 * i));
					imGr.drawString("PAUSE", pausex + i, pausey + i);
				}
				pauseWasDrawn_ = true;
			}
			
			
		}
		else // ! shouldRedraw_
		{
			// copy image_
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			image_.copyData(image.getRaster());
			imGr = image.createGraphics();
		}
		
		
		// draw appropriate text if needed
		if (victory_)
		{
			victoryWasDrawn_ = true;

			imGr.setFont(bigFont_);
			int y = victoryStarty_;
			for (Color c : victoryColors_)
			{
				String vs = "Victory!";
				imGr.setColor(Color.WHITE);
				imGr.drawString(vs, victoryStartx_ - 1, y - 1);
				imGr.drawString(vs, victoryStartx_ + 1, y + 1);
				imGr.drawString(vs, victoryStartx_ - 1, y + 1);
				imGr.drawString(vs, victoryStartx_ + 1, y - 1);
				imGr.setColor(c);
				imGr.drawString(vs, victoryStartx_, y);
				y += victoryStartDelta_;
			}
		
			particleAnimator_.drawParticles(imGr);
		}
		/*else if (gameover_)
		{
			imGr.setFont(bigFont_);
			imGr.setColor(new Color(255,20,30));
			int x = 120;
			imGr.drawString("GAME OVER...", x, 200);
			imGr.drawString("Space for new", x, 300);
			imGr.drawString("ESC to main menu", x, 400);
		}
		/*else if (pause_)
		{
			imGr.setFont(bigFont_);
			int pausex = width / 4;
			int pausey = height / 2 - 100;
			for (int i=0; i < 5; i++)
			{
				imGr.setColor(new Color(100 + 10 * i, 20 + 20 * i, 30 + 30 * i));
				imGr.drawString("PAUSE", pausex + i, pausey + i);
			}
		}
		*/
		
		// and display messages
		long delay = System.currentTimeMillis() - lastMessageTime_;
		int msgy = height - 100;
		int msgx = 15;
		imGr.setColor(new Color(200,200,0));
		imGr.setFont(normalFont_);
		for (GameMessage m : messages_)
		{
			if (m.ttl_ <= 0)
				messages_.remove(m);
			else
			{
				m.ttl_ -= delay;
				imGr.drawString(m.msg_, msgx, msgy);
				msgy -= 15;
			}
		}

		lastMessageTime_ = System.currentTimeMillis();
		

		
		// good. Now we can really paint
		imagePanel_.setImage(image);
		
		imagePanel_.repaint();
		
	}
	
	public void paint(Graphics g)
	{
		draw_game();
		infoPanel_.repaint();
	}


	@Override
	public void displayMessage(String message)
	{
		messages_.add(new GameMessage(message));
	}

	public void setVictoryColors(Vector<Color> colors)
	{
		victory_ = true;
		victoryWasDrawn_ = false;
		victoryColors_ = colors;
		
		if (null != particleAnimator_)
			particleAnimator_.stopAnimation();
		
		particleAnimator_ = new VictoryParticleAnimator(this);

		int ybase = victoryStarty_;
		int x = victoryStartx_;
		for (Color c : victoryColors_)
		{
			for (int i=0; i<13; ++i)
			{
				int r = c.getRed() + (int)(70 * (0.5 - Utils.rand.nextDouble()));
				r = Arith.max(0, Arith.min(255, r));
				int g = c.getGreen() + (int)(80 * (0.5 - Utils.rand.nextDouble()));
				g = Arith.max(0, Arith.min(255, g));
				int b = c.getBlue()+ (int)(70 * (0.5 - Utils.rand.nextDouble()));
				b = Arith.max(0, Arith.min(255, b));
				Color c2 = new Color(r, g, b);
				x += 10 + Utils.rand.nextInt(23);
				int y = ybase + 20 - Utils.rand.nextInt(40);
				VictoryParticleAnimation anim = new VictoryParticleAnimation(this, new Pointd(x,y), c2);
				particleAnimator_.addVictoryParticleAnimation(anim);
			}
			ybase += victoryStartDelta_;
		}
		particleAnimator_.start();
	}
	
	public void stopVictoryColors()
	{
		if (victory_)
			particleAnimator_.stopAnimation();
		victory_ = false;
	}

	public void displayActualFPS(int fps)
	{
		infoPanel_.displayActualFPS(fps);
	}
	



}
