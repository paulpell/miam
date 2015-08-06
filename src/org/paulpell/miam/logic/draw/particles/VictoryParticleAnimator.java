package org.paulpell.miam.logic.draw.particles;

import java.awt.Component;
import java.awt.Graphics;
import java.util.Vector;

import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.Log;
import org.paulpell.miam.logic.Utils;

public class VictoryParticleAnimator extends Thread
{

	
	private static final long thread_sleep = 1000 / Globals.FPS; // ms
	

	final Component toPaint_;

	boolean displayed_;
	boolean stopped_;
	
	Vector <VictoryParticleAnimation> particleAnims_;

	public VictoryParticleAnimator(Component toPaint)
	{
		super("particles");
		displayed_ = false;
		stopped_ = false;
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
		if ( stopped_ )
			return;
		
		synchronized (this)
		{
			displayed_ = false;
			// we wait until the next round of animation,
			// at which point the animation thread must be quitting
			// or have quitted already
			try
			{
				wait();
			} catch (InterruptedException e)
			{
				Log.logErr("Main thread cannot sleep to stop particles");
				Log.logException(e);
			}
		}
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
				Utils.threadSleep(thread_sleep);
			}
		}
		
		synchronized (this)
		{
			notify();
		}
		
		stopped_ = true;
		assert ! displayed_ : "Particles: still displayed_, but exiting";
	}

}
