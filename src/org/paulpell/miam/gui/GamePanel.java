package org.paulpell.miam.gui;


import static java.awt.event.KeyEvent.VK_N;
import static java.awt.event.KeyEvent.VK_SPACE;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.paulpell.miam.fx.GaussianBlur;
import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.Arith;
import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.Control;
import org.paulpell.miam.logic.Fonts;
import org.paulpell.miam.logic.Game;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.Log;
import org.paulpell.miam.logic.Utils;
import org.paulpell.miam.logic.draw.Drawable;
import org.paulpell.miam.logic.draw.particles.VictoryParticleAnimation;
import org.paulpell.miam.logic.draw.particles.VictoryParticleAnimator;

@SuppressWarnings("serial")
public class GamePanel extends AbstractDisplayPanel
{
	
	Control control_;
	
	GameInfoPanel infoPanel_;
	JPanel snakeInfoPanels_;
	DrawImagePanel imagePanel_;
	
	MessagePainter msgPainter_;

	// flags for drawing stuff
	boolean paintPause_ = false;
	boolean pauseWasDrawn_ = false;
	boolean paintGameover_ = false;
	boolean gameoverWasDrawn_ = false;
	boolean paintVictory_ = false;
	boolean victoryWasDrawn_ = false;
	int victoryStartx_ = 120;
	int victoryStarty_ = 150;
	int victoryStartDelta_ = 150;
	
	// this stores the image from one frame to the next
	BufferedImage image_;
	
	Vector<Color> victoryColors_;
	
	VictoryParticleAnimator particleAnimator_;

	public GamePanel(Control control, JFrame parent)
	{
		super("Snakesss", parent);
		
		control_ = control;

		msgPainter_ = new MessagePainter(15, Constants.DEFAULT_IMAGE_HEIGHT - 15, -15);
	
		setLayout(new BorderLayout());
		
		imagePanel_ = new DrawImagePanel();
		add(imagePanel_, BorderLayout.WEST);
		doLayout();
	}
	
	// display info for each player/snake
	//public void resetForNewGame(Vector<Snake> snakes, Dimension gamePanelSize)
	public void resetForNewGame(Game g)
	{
 		if (infoPanel_ != null)
			remove(infoPanel_);
		infoPanel_ = new GameInfoPanel(g);
		add(infoPanel_, BorderLayout.EAST);
		
		msgPainter_.clearMessages();
		
		imagePanel_.setPreferredSize(g.getPreferredSize());
	}
	
	public void setPause(boolean pause)
	{
		paintPause_ = pause;
		pauseWasDrawn_ = false;
		repaint();
	}
	
	public void setGameover(boolean b)
	{
		paintGameover_ = b;
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
				(!paintVictory_ && !paintPause_ && !paintGameover_) // normal mode
				|| !isValid()
				|| (paintVictory_ && !victoryWasDrawn_)
				|| (paintGameover_ && !gameoverWasDrawn_)
				|| (paintPause_ && !pauseWasDrawn_)
				;
		if (shouldRedraw)
			image_ = redrawImage();

		Graphics2D imGr = (Graphics2D)image_.getGraphics();
		
		// draw appropriate text if needed
		if (paintVictory_)
			paintVictory(imGr);
		
		// and display messages
		msgPainter_.paintMessages(imGr);
		
		// good. Now we can really paint
		imagePanel_.setImage(image_);
		imagePanel_.repaint();
	}
	
	private BufferedImage redrawImage ()
	{
		int w = getWidth(), h = getHeight();
		// re-create a new buffered image
		//image_ = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);//image_;
		
		Graphics2D imGr = image.createGraphics();
		imGr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		
		// background
		imGr.setColor(new Color(0,0,0));
		imGr.fillRect(0, 0, w, h);
		
		// all the drawables
		for (Iterator<Drawable> e = control_.getDrawablesIterator(); e.hasNext();)
			e.next().draw(imGr);

		if (paintGameover_)
			paintGameOver(imGr);
		else if (paintPause_)
			paintPause(imGr);
		
		if ( Globals.USE_BLURRING)
		{
			long t1 = System.currentTimeMillis();
			image = GaussianBlur.blurGameImg(image);
			long t2 = System.currentTimeMillis();
			long dt2 = t2 - t1;
			Log.logMsg("Time for blurring: " + dt2);
		}
		
		return image;
	}
	
	private void paintGameOver(Graphics2D imGr)
	{
		boolean keyinfo = ! control_.isEditorLevelPlayed();
		imGr.setFont(Fonts.bigFont_);
		imGr.setColor(new Color(255,20,30));
		int x = 120;
		imGr.drawString("GAME OVER...", x, 200);
		if (keyinfo)
		{
			imGr.drawString("Space for new", x, 300);
			imGr.drawString("ESC to main menu", x, 400);
		}
		gameoverWasDrawn_ = true;
	}
	
	private void paintVictory(Graphics2D imGr)
	{
		imGr.setFont(Fonts.bigFont_);
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
	
		if (Globals.USE_PARTICLE_ANIMATIONS)
			particleAnimator_.drawParticles(imGr);

		victoryWasDrawn_ = true;
	}
	
	private void paintPause(Graphics2D imGr)
	{
		imGr.setFont(Fonts.bigFont_);
		int pausex = getWidth() / 4;
		int pausey = getHeight() / 2 - 100;
		for (int i=0; i < 5; i++)
		{
			imGr.setColor(new Color(100 + 10 * i, 20 + 20 * i, 30 + 30 * i));
			imGr.drawString("PAUSE", pausex + i, pausey + i);
		}
		
		pauseWasDrawn_ = true;
	}
	
	public void paint(Graphics g)
	{
		draw_game();
		infoPanel_.repaint();
	}


	@Override
	public void displayMessage(String message, boolean immediately)
	{
		msgPainter_.addMessage(message);
		if (immediately) {
			Dimension d = getSize();
			paintImmediately(0, 0, d.width, d.height);
			imagePanel_.paintImmediately();
		}
	}

	public void setVictoryColors(Vector<Color> colors)
	{
		paintVictory_ = true;
		victoryWasDrawn_ = false;
		victoryColors_ = colors;
		
		if ( ! Globals.USE_PARTICLE_ANIMATIONS )
			assert particleAnimator_ == null : "Particle animator != null";
		else
		{
			
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
	}
	
	public void stopVictoryColors()
	{
		if (paintVictory_)
			particleAnimator_.stopAnimation();
		paintVictory_ = false;
	}

	public void displayActualFPS(int fps)
	{
		infoPanel_.displayActualFPS(fps);
	}

	@Override
	public boolean canRemovePanel()
	{
		return true;
	}

	@Override
	public KeyListener getCurrentKeyListener(KeyEvent e) {
		return this;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keycode = e.getKeyCode();
		switch (keycode) {
		case KeyEvent.VK_ESCAPE:
			control_.onGamePanelEsc();
		
		case KeyEvent.VK_P:
			control_.togglePause();
			break;
			
		case VK_SPACE:
		case VK_N:
			control_.newPressed();
			break;
		
		default:
			control_.onGameKeyPressed(keycode);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keycode = e.getKeyCode();
		switch (keycode) {
		case KeyEvent.VK_P:
			// keep P private
			break;
		
		default:
			control_.onGameKeyReleased(keycode);
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	



}
