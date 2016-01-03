package org.paulpell.miam.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.TimerTask;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.Constants;
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
	
	private static Font font_;
	//public static final Color s_bgColor = new Color(120, 20, 80);
	private static final Color s_textColor = new Color(10, 220, 10);
	private static final BufferedImage s_image;

	private static final int s_width = Constants.DEFAULT_IMAGE_WIDTH;
	private static final int s_height = Constants.DEFAULT_IMAGE_HEIGHT;
	private static final int s_x = 170;
	private static final int s_y = 170;
	private static final int s_delta = 40;
	
	static
	{
		s_image = new BufferedImage(s_width, s_height, BufferedImage.TYPE_INT_RGB);
		Graphics imGr = s_image.createGraphics();
		font_ = imGr.getFont();
		font_ = font_.deriveFont(font_.getSize2D() * 2);

		imGr.setColor(Constants.WELCOME_COLOR);
		imGr.fillRect(0, 0, s_width, s_height);
		
		imGr.setColor(s_textColor);
		imGr.setFont(font_);
		
		imGr.drawString("Enjoy!", s_x, s_y);
		imGr.drawString("ESC to leave", s_x, s_y + s_delta);
		imGr.drawString("space for a new game", s_x, s_y + 2*s_delta);
		imGr.drawString("s to access the settings", s_x, s_y + 3*s_delta);
		imGr.drawString("o for network settings", s_x, s_y + 4*s_delta);
		
	}
	
	MainFrame mainFrame_;
	
	VictoryParticleAnimator particleAnimator_;
	AutoSnake autoSnake_;
	
	WelcomePanel(String title, MainFrame parent)
	{
		super(title, parent);
		mainFrame_ = parent;
		setPreferredSize(new Dimension(Constants.DEFAULT_IMAGE_WIDTH, Constants.DEFAULT_IMAGE_HEIGHT));
	}
	
	private void createParticles()
	{
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
		
		autoSnake_ = new AutoSnake(0, GameSettings.getCurrentSettings(), s_x-34, s_y + 7*s_delta, 0);
		autoSnake_.addAction(new AutoAction(AutoActionType.GO_STRAIGHT, 2));
		autoSnake_.addAction(new AutoAction(AutoActionType.TURN_RIGHT, 2));
		GlobalAnimationTimer.scheduleRepeatedTask(
				autoSnake_.makeTimerTask(null, mainFrame_), 0, 30);
	}
	
	public void paint(Graphics imGr)
	{
		imGr.drawImage(s_image, 0, 0, null);

		if ( null != particleAnimator_)
			particleAnimator_.drawParticles(imGr);
		
		if ( null != autoSnake_ )
			autoSnake_.draw((Graphics2D)imGr);
		
		
		
		
		
	}

	@Override
	public void displayMessage(String message)
	{
		// do nothing
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


}
