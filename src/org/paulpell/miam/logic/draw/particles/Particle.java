package org.paulpell.miam.logic.draw.particles;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.geom.Vector2D;
import org.paulpell.miam.logic.Arith;
import org.paulpell.miam.logic.Utils;

public class Particle
{
	
	static final double max_v_x = 40;
	static final double max_v_y = 150;
	
	// positive acceleration since screen coords are inverted
	static final double acc_y = 11;
	
	int mass_;
	Pointd position_;
	Vector2D v_; // velocity
	
	boolean isShiny_;
	
	final double prob_shiny = 0.1;
	
	final double prob_divide = 0.5;
	final int num_div = 2;

	private final double update_dt = 0.18;

	public Particle(int mass, Pointd pos, Vector2D v)
	{
		position_ = pos;
		v_ = v;
		mass_ = mass;
		isShiny_ = mass_ > 1 && Utils.rand.nextDouble() < prob_shiny;
	}

	public Particle(Pointd pos, Vector2D v)
	{
		this(50 + Utils.rand.nextInt(150), pos, v);
	}

	
	// divide into a few particles or return null if already too small
	public Vector <Particle> divide()
	{
		if (mass_ <= 1)
			return null;
		
		Vector <Particle> ps = new Vector <Particle> ();
		if (Utils.rand.nextDouble() < prob_divide)
		{
			ps.add(this);
			return ps;
		}
		
		double vx = v_.getX();
		double vy = v_.getY();
		int sum_m = 0;
		double sum_dvx = 0;
		double sum_dvy = 0;
		int min = mass_ / num_div + 1; // absolute min: 1
		while (sum_m < mass_ - min)
		{
			int m = Arith.max(1, min - 5 + Utils.rand.nextInt(5));
			sum_m += m;
			double dvx = (max_v_x - Arith.absd(vx)) * 1.6 * (0.5 - Utils.rand.nextDouble());
			double dvy = 5 + (max_v_y / 3 - Arith.absd(vy)) * 2 * (0.5 - Utils.rand.nextDouble());
			sum_dvx += dvx;
			sum_dvy += dvy;
			Vector2D v = new Vector2D(v_.getX() + dvx, v_.getY() + dvy);
			Pointd p = new Pointd(position_.x_, position_.y_);
			ps.add(new Particle(m, p, v));
		}
		if (mass_ - sum_m <= 0)
			throw new UnsupportedOperationException("Bad programming =)");
		
		double dvx = v_.getX() - sum_dvx;
		double dvy = (v_.getY() - sum_dvy) / 1.1;
		Vector2D lastv = new Vector2D(dvx, dvy);
		Pointd p = new Pointd(position_.x_, position_.y_);
		ps.add(new Particle(mass_ - sum_m, p, lastv));
		
		return ps;
	}
	
	// return wether still visible 
	public boolean update()
	{
		double vy = v_.getY() + update_dt * acc_y;
		if (vy > max_v_y)
			vy = max_v_y;
		if (vy < -max_v_y)
			vy = -max_v_y;
		v_.setY(vy);
		
		position_.x_ += update_dt * v_.getX();
		position_.y_ += update_dt * v_.getY();
		
		boolean visible = position_.x_ < 600 && position_.x_ > 0
				&& position_.y_ < 600 && position_.y_ > -100;
		return visible;
	}
	
	public void draw(Graphics g)
	{
		int size = 4 * Arith.max(1, (int)(Math.log(mass_) / Math.log(6)));
		if (isShiny_)
		{
			Color c = g.getColor();
			g.setColor(Color.WHITE);
			g.drawOval((int)position_.x_ - 1, (int)position_.y_ - 1, 1 + size, 1 + size);
			g.setColor(c);
		}
		g.fillOval((int)position_.x_, (int)position_.y_, size, size);
	}
	
	public String toString()
	{
		return "particle(m="+mass_ +",p="+position_+", v="+v_+")";
	}
}
