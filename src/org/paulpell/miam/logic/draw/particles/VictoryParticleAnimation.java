package org.paulpell.miam.logic.draw.particles;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.Vector;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.geom.Vector2D;

public class VictoryParticleAnimation
{
	
	private Vector <Particle> particles_;
	
	private final Pointd startpos_;

	Color color_;
	
	
	
	public VictoryParticleAnimation(Component toPaint, Pointd position, Color color)
	{
		color_ = color;
		startpos_ = new Pointd(position.x_, position.y_);

		particles_ = new Vector <Particle> ();

		Pointd pos = new Pointd(position.x_, position.y_);
		Particle p1 = new Particle(pos, new Vector2D(0, -3));
		particles_.add(p1);
	}

	public void update()
	{
		if (particles_.size() == 0)
			particles_.add(new Particle(new Pointd(startpos_.x_, startpos_.y_), new Vector2D(0, 3)));
		
		Vector <Particle> newParticles = new Vector <Particle> ();
		for (Particle p : particles_)
		{
			if (p.update())
			{
				Vector <Particle> divided = p.divide();
				if (null != divided)
				{
					newParticles.addAll(divided);
				}
			}
		}
		
		particles_ = newParticles;
	}
	
	public void draw(Graphics g)
	{
		g.setColor(color_);
		for (Particle p: particles_)
		{
			p.draw(g);
		}
	}
	
	
}
