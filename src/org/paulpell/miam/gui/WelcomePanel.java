package org.paulpell.miam.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.Utils;
import org.paulpell.miam.logic.draw.particles.VictoryParticleAnimation;
import org.paulpell.miam.logic.draw.particles.VictoryParticleAnimator;


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
	
	VictoryParticleAnimator particleAnimator_;
	
	WelcomePanel(String title, JFrame parent)
	{
		super(title, parent);
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
		createParticles();
		particleAnimator_.start();
	}
	
	public void paint(Graphics imGr)
	{
		imGr.drawImage(s_image, 0, 0, null);
		particleAnimator_.drawParticles(imGr);
	}

	@Override
	public void displayMessage(String message)
	{
		// do nothing
	}


}
