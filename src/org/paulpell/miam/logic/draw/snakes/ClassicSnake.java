package org.paulpell.miam.logic.draw.snakes;


import java.awt.Polygon;
import java.util.Iterator;
import java.util.LinkedList;

import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.geom.Vector2D;
import org.paulpell.miam.logic.Arith;
import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.GameSettings;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.Log;


/*
 * Direction:
 *   0 = left
 *   1 = up
 *   2 = right
 *   3 = down
 */
public class ClassicSnake extends Snake
{

	// dir has a different encoding
	public ClassicSnake(int id,  GameSettings settings, int x0, int y0, int dir)
	{
		super(id, settings, x0, y0, dir);
	}
	public ClassicSnake(String fromNetwork, GameSettings settings)
	{
		super(fromNetwork, settings);
	}

	long lastTurnTime_ = 0; // we don't want to be able to turn too much
	int lastDir_ = -1;
	
	// checks if the last key hit was not too late, updates lastTurnTime if ok
	private boolean checkLastTurnTime(int dir)
	{
		long now = System.currentTimeMillis();
		
		if (dir == lastDir_)
		{
			long diff = now - lastTurnTime_;
			if (diff >= Globals.SNAKE_TIME_BETW_TURNS_MILLIS)
			{
				lastTurnTime_ = now;
				return true;
			}
			return false;
		}
		else
		{
			lastDir_ = dir;
			lastTurnTime_ = now;
			return true;
		}
	}
	
	@Override
	protected void turnLeft()
	{
		Log.logErr("ClassicSnake turn left");
		boolean canTurn = checkLastTurnTime(Constants.DIR_LEFT);
		if (canTurn)
		{
			direction_ = this.direction_ + 1;
			if (direction_ == 4)
				direction_ = 0;
			cloneHead();
			computeDs();
		}
	}
	
	@Override
	protected void turnRight()
	{
		Log.logErr("ClassicSnake turn right");
		boolean canTurn = checkLastTurnTime(Constants.DIR_RIGHT);
		if (canTurn)
		{
			direction_= this.direction_ + 3;
			if (direction_ >= 4)
				direction_ -= 4;
			cloneHead();
			computeDs();
		}
	}

	protected void computeDs()
	{
		dx_ = ((direction_ + 1) % 2) * (direction_ - 1) * speed_;
		dy_ = (direction_ % 2) * (2 - direction_) * speed_;
	}
	
	@Override
	protected void advanceTail(double s)
	{
		// remove as many points at the end and shorten as needed
		Iterator<Pointd> it = points_.descendingIterator();
		Pointd p1 = it.next();
		Pointd p2 = it.next();
		
		int toRem = 0; // how many points to remove? (impossible while traversing)
		do
		{
			int d = Arith.dirClassic(p1, p2);
			int l = (int)((p1.x_ - p2.x_) + (p1.y_ - p2.y_));
			l = l < 0 ? -l : l;
			if (l >= s) // anything remains in the last segment?
			{
				p1.x_ += ((d + 1) % 2) * (d - 1) * s;
				p1.y_ += (d % 2) * (2 - d) * s;
				s = 0; // to exit the loop
			}
			else // no => remove last segment (increase toRem)
			{
				s -= l;
				p1 = p2;
				p2 = it.next();
				++toRem;
			}
		} while (s > 0);
		
		for (int i=0; i<toRem; i++)
			points_.removeLast();
	}

	private static Vector2D getUnitVectorFromDir(int dir)
	{
		switch (dir)
		{
		case Constants.DIR_DOWN:
			return new Vector2D(+0, -1);
		case Constants.DIR_LEFT:
			return new Vector2D(-1, +0);
		case Constants.DIR_RIGHT:
			return new Vector2D(+1, +0);
		case Constants.DIR_UP:
			return new Vector2D(+0, +1);
		default:
			throw new IllegalArgumentException("Direction does not exist: " + dir);
		}
	}
	

	protected double width(double x)
	{
		return 2;
	}
	
	public void computeHull()
	{
		if (!settings_.useWideSnakes_)
		{
			computeNoWidthHull();
			return;
		}
		

		if (Globals.CLASSIC_DEBUG)
			Log.logMsg("compute hull");
		
		
		// create segments of length seg_length
		double seg_length = Globals.SNAKE_DIST_BETWEEN_SEGMENTS;
		
		boolean second_traversal = false;
		
		
		LinkedList<Pointd> newHull = new LinkedList<Pointd>();

		// do a first forward traversal, then a backward traversal of points
		Iterator<Pointd> it_pts = points_.iterator();
		for (int i=0; i<2; ++i)
		{
			double w; // use this as half width
			double traversed_len = 0;
			double len12;
			double len23;
			int dir12;
			int dir23;

			Pointd p1 = it_pts.next();
			Pointd p2 = it_pts.next();
			Pointd p3;
			
			Pointd hp; // the generated points
			
			Vector2D va;
			Vector2D vb;
			
			dir12 = Arith.dirClassic(p1, p2);
			
			// first add the head point
			if (second_traversal)
				w = width(length_);
			else
				w = width(0);
			
			// dir12 + 1 specifies the `right' side of traversal
			va = getUnitVectorFromDir((dir12 + 1)%4).multiply(w);
			// point is in front of the snake
			vb = getUnitVectorFromDir(dir12).multiply(w);

			hp = va.add(vb).add(p1);
			newHull.add(hp);
			

			len12 = Arith.dist(p1, p2);
			
			// add points b/w p1 and p2
			if (len12 > seg_length)
			{
				for (double l = seg_length; l < len12; l += seg_length)
				{
					if (second_traversal)
						w = width(length_ - l);
					else
						w = width(l);
					
					va = getUnitVectorFromDir((dir12 + 1)%4).multiply(w);
					vb = getUnitVectorFromDir(dir12).multiply(l);
					hp = va.add(vb).add(p1);
					newHull.add(hp);
				}
			}
			

			traversed_len += len12;
			
			if (Globals.CLASSIC_DEBUG)
				Log.logMsg("head, p1 = " + p1 + ", p2 = " + p2 + "dir12 = " + dir12 + ", va = " + va + ", vb = " + vb + ", hp="+ hp);
			
			// now handle all the intermediate segments
			while (it_pts.hasNext())
			{
				p3 = it_pts.next();
				
				len23 = Arith.dist(p2, p3);
				dir12 = Arith.dirClassic(p1, p2);
				dir23 = Arith.dirClassic(p2, p3);
				

				// the point at p2
				if (second_traversal)
					w = width(length_ - traversed_len);
				else
					w = width(traversed_len);
				
				va = getUnitVectorFromDir((dir12 + 1)%4).multiply(w);
				vb = getUnitVectorFromDir((dir23 + 1)%4).multiply(w);
				hp = va.add(vb).add(p2);
				newHull.add(hp);
				
				// add points b/w p2 and p3 for intermediate segments
				if (len23 > seg_length)
				{
					for (double l = traversed_len + seg_length; l < traversed_len + len23; l += seg_length)
					{
						if (second_traversal)
							w = width(length_ - l);
						else
							w = width(l);
						
						va = getUnitVectorFromDir((dir23 + 1)%4).multiply(w);
						vb = getUnitVectorFromDir(dir23).multiply(l - traversed_len);
						hp = va.add(vb).add(p2);
						newHull.add(hp);
					}
				}
			
				if (Globals.CLASSIC_DEBUG)
					Log.logMsg("middle, p1 = " + p1 + ", p2 = " + p2 + ", p3 = " + p3 + "dir12 = " + dir12 + ", dir23 = " + dir23 + ", va = " + va + ", vb = " + vb + ", hp="+ hp);
				

				traversed_len += len23;
				
				p1 = p2;
				p2 = p3;
			}

			
			// add points b/w p1 and p2 (last and forelast) only if more than 2 segments (would be there already)
			len12 = Arith.dist(p1, p2);
			if (points_.size() > 2 && len12 > seg_length)
			{
				for (double l = traversed_len + seg_length; l < length_; l += seg_length)
				{
					if (second_traversal)
						w = width(length_ - l);
					else
						w = width(l);
					
					va = getUnitVectorFromDir((dir12 + 1)%4).multiply(w);
					vb = getUnitVectorFromDir(dir12).multiply(l - traversed_len);
					hp = va.add(vb).add(p1);
					newHull.add(hp);
				}
			}
			
			// and final point
			if (second_traversal)
				w = width(0);
			else
				w = width(length_);
			dir12 = Arith.dirClassic(p1, p2);
			va = getUnitVectorFromDir((dir12 + 1)%4).multiply(w);
			vb = getUnitVectorFromDir(dir12).multiply(-w);
			hp = va.add(vb).add(p2);
			newHull.add(hp);

			if (Globals.CLASSIC_DEBUG)
				Log.logMsg("tail, p1 = " + p1 + ", p2 = " + p2 + "dir12 = " + dir12 + ", va = " + va + ", vb = " + vb + ", hp="+ hp);
			
			it_pts = points_.descendingIterator(); // for the 2nd traversal
			second_traversal = true;
		}
		
		// now actually update the variables
		hull_points_ = newHull;
		hull_ = new Polygon();
		it_pts = hull_points_.iterator();
		String msg = "hull: ";
		while (it_pts.hasNext())
		{
			Pointd p = it_pts.next();
			hull_.addPoint((int)p.x_, (int)p.y_);
			msg += p;
		}
		
		if (Globals.CLASSIC_DEBUG)
			Log.logMsg(msg);
	}
	
	@Override
	public Pointd isSnakeColliding(Snake other)
	{
		Pointd p1other = other.getHead();
		Pointd p2other = other.getPreviousHead();
		boolean otherHorizontal = Arith.equalsd(p1other.y_, p2other.y_);
		
		Iterator<Pointd> it = points_.iterator();
		
		
		// if it's the same Snake, it can't collide with its first 3 segments
		if (other == this)
		{
			if (points_.size() < 5)
				return null; // impossible collision
			it.next(); it.next(); it.next(); // skip first points
		}
		Pointd p1 = it.next(), p2; // p1-p2 will represent the segments of this Snake

		do
		{
			p2 = it.next();
			
			// both horizontal or both vertical means an impossible collision
			if (otherHorizontal && Arith.equalsd(p1.x_,p2.x_))
			{
				if ((p1other.y_ >= p1.y_ && p1other.y_ <= p2.y_)
						|| (p1other.y_ <= p1.y_ && p1other.y_ >= p2.y_))
				{
					if ((p1.x_ - p1other.x_) * (p1.x_ - p2other.x_) <= 0)
						return new Pointd(p1.x_, p1other.y_);
				}
			}
			else if (!otherHorizontal && Arith.equalsd(p1.y_, p2.y_))
			{
				if ((p1other.x_ >= p1.x_ && p1other.x_ <= p2.x_)
						|| (p1other.x_ >= p2.x_ && p1other.x_ <= p1.x_))
				{
					if ((p1.y_ - p1other.y_) * (p1.y_ - p2other.y_) <= 0)
						return new Pointd(p1other.x_,p1.y_);
				}
			}
			p1 = p2;
		} while (it.hasNext());
		
		return null;
	}
	
}
