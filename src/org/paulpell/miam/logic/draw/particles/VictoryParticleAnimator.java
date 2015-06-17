package org.paulpell.miam.logic.draw.particles;

import java.awt.Component;
import java.awt.Graphics;
import java.util.Vector;

public class VictoryParticleAnimator extends Thread
{

	
	private static final long thread_sleep = 35; // ms
	

	final Component toPaint_;

	boolean displayed_;
	
	Vector <VictoryParticleAnimation> particleAnims_;

	public VictoryParticleAnimator(Component toPaint)
	{
		super("particles");
		displayed_ = false;
		toPaint_ = toPaint;
		particleAnims_ = new Vector <VictoryParticleAnimation> ();
	}
	
	public void addVictoryParticleAnimation(VictoryParticleAnimation anim)
	{
		particleAnims_.add(anim);
	}
	
	public void resetVictoryParticleAnimations()
	{
		particleAnims_ = new Vector <VictoryParticleAnimation> ();
	}

	public void stopAnimation()
	{
		displayed_ = false;
	}
	
	public void drawParticles(Graphics g)
	{
		for (VictoryParticleAnimation p: particleAnims_)
			p.draw(g);
	}

	

	public void run()
	{
		displayed_ = true;
		while (displayed_)
		{
			if (!toPaint_.isShowing())
				displayed_ = false;
			else
			{
				for (VictoryParticleAnimation p: particleAnims_)
					p.update();
				
				toPaint_.repaint();
				try
				{
					sleep(thread_sleep);
				} catch (InterruptedException e)
				{}
			}
		}
	}

}
