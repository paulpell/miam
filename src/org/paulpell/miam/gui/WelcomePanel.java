package org.paulpell.miam.gui;

import static java.awt.event.KeyEvent.VK_E;
import static java.awt.event.KeyEvent.VK_N;
import static java.awt.event.KeyEvent.VK_O;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_SPACE;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.TimerTask;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.Control;
import org.paulpell.miam.logic.Fonts;
import org.paulpell.miam.logic.GameSettings;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.Utils;
import org.paulpell.miam.logic.draw.particles.VictoryParticleAnimation;
import org.paulpell.miam.logic.draw.particles.VictoryParticleAnimator;
import org.paulpell.miam.logic.draw.snakes.auto.AutoAction;
import org.paulpell.miam.logic.draw.snakes.auto.AutoActionType;
import org.paulpell.miam.logic.draw.snakes.auto.AutoSnake;


public class WelcomePanel extends AbstractDisplayPanel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4778643161712458771L;

	private static final Color s_textColor = new Color(10, 220, 10);
	//private static final BufferedImage s_image;

	private static final int s_width = Constants.DEFAULT_IMAGE_WIDTH;
	private static final int s_height = Constants.DEFAULT_IMAGE_HEIGHT;
	private static final int s_x = 80;
	private static final int s_y = 170;
	private static final int s_delta = 50;
	Control control_;
	MainFrame mainFrame_;
	
	VictoryParticleAnimator particleAnimator_;
	AutoSnake autoSnake_;
	
	WelcomePanel(String title, Control control, MainFrame parent)
	{
		super(title, parent);
		control_ = control;
		mainFrame_ = parent;
		setPreferredSize(new Dimension(Constants.DEFAULT_IMAGE_WIDTH, Constants.DEFAULT_IMAGE_HEIGHT));
	}
	
	private void drawBackground(Graphics2D imGr)
	{
		imGr.setColor(Constants.WELCOME_COLOR);
		imGr.fillRect(0, 0, s_width, s_height);
		
		imGr.setColor(s_textColor);
		imGr.setFont(Fonts.medFont_);
		
		imGr.drawString("Enjoy!", s_x, s_y);
		imGr.drawString("ESC to leave", s_x, s_y + s_delta);
		imGr.drawString("space for a new game", s_x, s_y + 2*s_delta);
		imGr.drawString("s to access the settings", s_x, s_y + 3*s_delta);
		imGr.drawString("o for network settings", s_x, s_y + 4*s_delta);
	}
	
	private void createParticles()
	{
		assert Globals.USE_PARTICLE_ANIMATIONS : "Should not create particles";
		particleAnimator_ = new VictoryParticleAnimator(this);
		
		for (int i=0; i<8; ++i)
		{
			int r = 220 + (int)(70 * (0.5 - Utils.rand.nextDouble()));
			int g = 40 + (int)(80 * (0.5 - Utils.rand.nextDouble()));
			int b = 40 + (int)(70 * (0.5 - Utils.rand.nextDouble()));
			Color c = new Color(r, g, b);
			VictoryParticleAnimation anim = new VictoryParticleAnimation(this, new Pointd(100 + 27 * i, 100), c);
			particleAnimator_.addVictoryParticleAnimation(anim);
		}
	}
	
	public void startAnimating()
	{
		if ( ! Globals.USE_ANIMATIONS )
			return;
		
		if ( Globals.USE_PARTICLE_ANIMATIONS )
		{
			createParticles();
			particleAnimator_.start();
		}
		
		autoSnake_ = new AutoSnake(0, GameSettings.getCurrentSettings(), s_x+9, s_y + 7*s_delta, 0);
		autoSnake_.addAction(new AutoAction(AutoActionType.GO_STRAIGHT, 2));
		autoSnake_.addAction(new AutoAction(AutoActionType.TURN_RIGHT, 2));
		GlobalAnimationTimer.scheduleRepeatedTask(
				autoSnake_.makeTimerTask(null, mainFrame_), 0, 30);
	}

	public void paint(Graphics g)
	{
		Fonts.setupFonts(g);
		
		BufferedImage bufImg = new BufferedImage(s_width, s_height, BufferedImage.TYPE_INT_RGB);
		Graphics2D imGr = bufImg.createGraphics();
		drawBackground(imGr);
		if ( null != particleAnimator_)
			particleAnimator_.drawParticles(imGr);
		if ( null != autoSnake_ )
			autoSnake_.draw(imGr);
		g.drawImage(bufImg, 0, 0, null);
	}

	@Override
	public void displayMessage(String message, boolean immediately)
	{
		assert false;
	}

	@Override
	public boolean canRemovePanel()
	{
		return true;
	}

	@Override
	public void panelRemoved()
	{
		if ( null != autoSnake_ )
		{
			TimerTask tt = autoSnake_.getTimerTask();
			if ( null != tt ) tt.cancel();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode())
		{
		case KeyEvent.VK_ESCAPE:
			control_.quit();
			break;
			
		case VK_S:
			control_.showSettingsPanel();
			break;
			
		case VK_SPACE:
		case VK_N:
			control_.newPressed();
			break;
			
		case VK_E:
			control_.showLevelEditor();
			break;
			
		case VK_O:
			control_.showNetworkPanel(null);
			break;
			
		default:
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public KeyListener getCurrentKeyListener(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
		case KeyEvent.VK_ESCAPE:
		case VK_S:
		case VK_SPACE:
		case VK_N:
		case VK_E:
		case VK_O:
			return this;
		}
			
		return null;
	}


}
