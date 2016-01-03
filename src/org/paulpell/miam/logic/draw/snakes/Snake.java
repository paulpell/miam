package org.paulpell.miam.logic.draw.snakes;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

import javax.swing.ImageIcon;

import org.paulpell.miam.geom.GeometricObject;
import org.paulpell.miam.geom.Segment;
import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.geom.Vector2D;
import org.paulpell.miam.gui.GlobalColorTable;
import org.paulpell.miam.logic.Arith;
import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.logic.Game;
import org.paulpell.miam.logic.GameSettings;
import org.paulpell.miam.logic.Globals;
import org.paulpell.miam.logic.Log;
import org.paulpell.miam.logic.draw.Drawable;
import org.paulpell.miam.logic.draw.items.Item;
import org.paulpell.miam.logic.draw.items.StockItem;



/**
 * Is actually updated in advance().
 * Keyboard events are asynchronous; a flag is raised when the snake has to turn
 * or to take a (special) action.
 * 
 * There is a hull, computed at each step, which is then drawn.
 * 
 * @author paul
 *
 */

public class Snake extends Drawable
{
	
	/* Static constants/functions ***************************/
	
	
	
	private int x0_, y0_; // only used for initial position
	
	
	
	/* Individual properties *********************/
	int id_;
	
	GameSettings settings_;
	
	int score_ = 0;
	boolean isAlive_ = true;
	// physics/geometry
	int speed_;
	int angleDiff_;
	boolean hasSpeedup_ = false;
	int direction_; // either 0-3 (in classic mode) or an angle based on 360 degrees (std trigo)
	double dx_, dy_; // cached values for how to advance, use computeDs() to update
	double length_ = 1;
	// TODO: negative growth
	double toGrow_ = Constants.INIT_SNAKE_LENGTH;
	// TODO: double extraThickness = Globals.SNAKE_DEFAULT_EXTRA_THICKNESS;
	Color color_;
	

	boolean turnLeft_ = false;
	boolean turnRight_ = false;
	
	// the first point will be the head!
	LinkedList<Pointd> points_ = new LinkedList<Pointd>();
	Pointd previousHead_;
	
	// is updated by computeHull(), used in draw() and for TODO: collision tests
	LinkedList<Pointd> hull_points_ = new LinkedList<Pointd>();
	Polygon hull_;
	
	/* ****** Item stuff **********/
	int randomTurn_ = -1; // in which direction bananas are telling us to go
	
	//  the special item is the one the player can use
	// for now, let's accept only one SpecialItem per snake
	StockItem specialItem_;
	boolean wantUseSpecial_ = false; // set when player uses 'special' key, item activated in advance()
	
	// we also have a list of items having effect on the snake
	LinkedList<Item> items_ = new LinkedList<Item>(); 
	boolean itemsChanged_ = false;// set to true during one step if removing/adding an item
	boolean specialItemChanged_ = false;
	
	
	
	/* Constructor ***************/
	public Snake(int id, GameSettings settings, int x0, int y0, int dir)
	{
		id_ = id;
		direction_ = dir;
		settings_ = settings;
		
		this.x0_ = x0;
		this.y0_ = y0;
		init();
	}
	
	public Snake(String fromNetwork, GameSettings settings)
	{
		// inspired from getNetworkRepresentation()
		String[] nrs = fromNetwork.split(",");
		id_ = Integer.parseInt(nrs[0]);
		x0_ = Integer.parseInt(nrs[1]);
		y0_ = Integer.parseInt(nrs[2]);
		direction_ = Integer.parseInt(nrs[3]);
		settings_ = settings;
		init();
	}
	
	// needed for both constructors
	private void init()
	{
		speed_ = settings_.snakeSpeed_;
		angleDiff_ = speed_ * settings_.snakeAngleSpeedFactor_;
		color_ = GlobalColorTable.getSnakeColor(id_);
		computeDs();
		previousHead_ = new Pointd(x0_, y0_); 
		points_.add(previousHead_);
		double x1 = x0_ + dx_;
		double y1 = y0_ + dy_;
		points_.addFirst(new Pointd(x1,y1));
		length_ = Math.sqrt((x0_-x1)*(x0_-x1) + (y0_-y1)*(y0_-y1));
		computeHull();
	}
	
	public int getId() {
		return id_;
	}
	
	public Color getColor() {
		return color_;
	}
	
	public boolean isAlive() {
		return isAlive_;
	}
	
	
	// we give a point where we want to draw the head
	public void kill(Pointd newHead)
	{
		points_.removeFirst();
		points_.addFirst(newHead); // haha trick
		isAlive_ = false;
		computeHull();
	}
	public void resurrect()
	{
		if (isAlive_)
			return;

		reverse();
		isAlive_ = true;
	}
	public int getSpeed()
	{
		return speed_;
	}
	public void setSpeedup(boolean b)
	{
		if (b && !hasSpeedup_)
		{
			hasSpeedup_ = true;
			speed_ += settings_.snakeExtraSpeedup_;
		}
		else if (!b && hasSpeedup_)
		{
			hasSpeedup_ = false;
			speed_ -= settings_.snakeExtraSpeedup_;
		}
		computeDs();
	}
	public void addSpeedupSpecial(double extra)
	{
		speed_ += extra;
		computeDs();
	}
	
	public void addScore (int s) {
		score_ += s;
	}
	public int getScore() {
		return score_;
	}
	
	// Bananas set this
	public void setRandomTurning(int dir)
	{
		randomTurn_ = dir;
	}
	
	public void useSpecialItem() {
		wantUseSpecial_ = true; // we'll use it at the beginning of advance()
	}
	
	public void acceptItem(Item item)
	{
		switch (item.getType())
		{
		case SIMPLE:
			itemsChanged_ = true;
			item.startEffect(this);
			items_.add(item);
			break;
			
		case STOCK:
			specialItemChanged_ = true;
			specialItem_ = (StockItem)item;
			break;
			
		case GLOBAL:
			assert false : "Global items can not be handled by Snake";
			break;
		}
	}
	
	public boolean itemsChanged() { // used in SnakeInfoPanel.update()
		return itemsChanged_;
	}
	public boolean specialItemChanged() {
		return specialItemChanged_;
	}
	
	public void reverse()
	{
		LinkedList<Pointd> ps = new LinkedList<Pointd>();
		Iterator<Pointd> it = points_.iterator();
		while(it.hasNext())
			ps.addFirst(it.next());

		points_ = ps;
		if (settings_.classicMode_)
			direction_ = Arith.dirClassic(ps.get(1), ps.get(0));
		else
			direction_ = Arith.dirModern(ps.get(1), ps.get(0));
		computeDs();
	}
	
	/*TODO public double getExtraThickness() {
		return extraThickness;
	}*/

	
	/* **********************************************************************/
	/* *** Movement methods ************************************************/
	
	public void growBy(int l)
	{
		toGrow_ += l;
	}
	

	public void setTurnLeft(boolean val)
	{
		turnLeft_  = val;
	}

	public void setTurnRight(boolean val)
	{
		turnRight_  = val;
	}
	
	/* ********** computes dx and dy, the distances the snake will cover on the x_ and y_ axis */
	protected void computeDs()
	{
		double dx = Math.cos(direction_ / 180. * Math.PI) * speed_; 
		double dy = Math.sin(direction_ / 180. * Math.PI) * speed_;
		if (Globals.SNAKE_DEBUG)
		{
			Log.logErr("  computeDs(); old_dx = " +dx_ + ", new = " + dx);
			Log.logErr("  computeDs(); old_dy = " +dy_ + ", new = " + dy);
		}
		dx_ = dx;
		dy_ = dy;
	}
	
	/* ********** turn *********/
	// return whether we can actually turn
	protected void turnLeft()
	{
		int d = (direction_ - angleDiff_ + 360) % 360;

		if (Globals.SNAKE_DEBUG)
			Log.logErr("  turnLeft(); old = " +direction_ + ", new = " + d);
		direction_ = d;
		
		cloneHead();
		computeDs();
	}
	protected void turnRight()
	{
		int d = (direction_ + angleDiff_ + 360) % 360;
		if (Globals.SNAKE_DEBUG)
			Log.logErr("  turnRight(); old = " +direction_ + ", new = " + d);
		direction_ = d;
		cloneHead();
		computeDs();
	}
	
	protected void cloneHead()
	{
		Pointd newHead = points_.getFirst().clone();
		points_.addFirst(newHead);
	}
	
	protected void updateTurn()
	{
		// to handle the uncontrolled turn (banana)
		int nextDir = -1;
		if (Globals.SNAKE_DEBUG)
			Log.logErr("updateTurn(); randomTurn=" +randomTurn_);
		if (randomTurn_ > -1)
			nextDir = randomTurn_;
		

		if (nextDir == 1 // random turning left
				|| (nextDir == -1 && turnLeft_ && !turnRight_)) // or manual turn
			turnLeft();
		if (nextDir == 2 // random turning left
				|| (nextDir == -1 && !turnLeft_ && turnRight_)) // or manual turn
			turnRight();
	}
	
	protected void computeNoWidthHull()
	{
		LinkedList<Pointd> newHull = new LinkedList<Pointd>();
		Iterator<Pointd> it_pts;
		for (it_pts = points_.iterator(); it_pts.hasNext();)
			newHull.add(it_pts.next());

		for (it_pts = points_.descendingIterator(); it_pts.hasNext();)
			newHull.add(it_pts.next());

		hull_points_ = newHull;
	}
	
	/**
	 * O(n) algorithm to re-compute the hull for the snake, it actually sets hull_points_ (a list of Pointd).
	 */
	protected void computeHull()
	{
		// if the snake is a simple line, simply use that line, by adding the points once forward, then once backward.
		if (!settings_.useWideSnakes_)
		{
			computeNoWidthHull();
			return;
		}
		

		
		LinkedList<Pointd> newHull = new LinkedList<Pointd>();
		
		// create segments of length seg_length
		double seg_length = Globals.SNAKE_DIST_BETWEEN_SEGMENTS;
		
		// Draw a polygon around the segments

		boolean second_traversal = false;
		double w;
		
		
		// now create a polygon, and add the points in the following manner:
		// for every segment:
		//   let v be a unit vector in the direction of the segment.
		//   create a vector tangent to v, of length given by width_(), and create a point at
		//   its extremity.
		// do that twice, so that we can ``circle'' the snake!
		Iterator<Pointd> it_pts = points_.iterator();
		while (it_pts.hasNext()) // this loops 2 times: once forward, the other backward in points
		{
			double diff_pts;// length of a segment
			double traversed_len = 0;// cumulated traversed length 
			
			
			// p the next point, p_p the previous one.
			Pointd p = it_pts.next(), p_p = p; // the 2nd time, it's a descending iterator
			do
			{
				p = it_pts.next();

				Vector2D v = new Vector2D(p_p, p);
				diff_pts = v.length();
				
				v.normalize(); // unit-vector
				Vector2D v_norm_unit = v.normal(); // normal vector
				
				
				// 1 + (as many times as there is seg_length in the segment)
				for (double l = traversed_len; l < traversed_len + diff_pts; l += seg_length)
				{
					Pointd orig = v.multiply(l - traversed_len).add(p_p); // where v_norm is applied
					
					// width_ at that point
					if (second_traversal)
						w = width(length_ - l);
					else
						w = width(l);
					// vector of length w
					Vector2D v_norm = v_norm_unit.multiply(w);
					double x = orig.x_ + v_norm.getX();
					double y = orig.y_ + v_norm.getY();
					newHull.add(new Pointd(x,y));
				}
				
				// add a point at the end of the segments anyway
				double l = traversed_len + diff_pts;
				Pointd orig = p; // where v_norm is applied
				
				if (second_traversal) 
					w = width(length_ - l);
				else
					w = width(l);
				
				Vector2D v_norm = v_norm_unit.multiply(w);
				double x = orig.x_ + v_norm.getX();
				double y = orig.y_ + v_norm.getY();
				newHull.add(new Pointd(x,y));

				p_p = p;
				traversed_len += diff_pts;
			} while (it_pts.hasNext());
			
			if (p != getHead()) // then, it was the forward traversal, we go to the backward one.
			{
				it_pts = points_.descendingIterator();
				second_traversal = true;
			}
		}
		hull_points_ = newHull;

	
		hull_ = new Polygon();
		it_pts = hull_points_.iterator();
		while(it_pts.hasNext())
		{
			Pointd p = it_pts.next();
			hull_.addPoint((int)p.x_, (int)p.y_);
		}
	}
	
	// 
	protected void advanceTail(double s)
	{

		// remove as many points at the end and shorten as needed
		Iterator<Pointd> it = points_.descendingIterator();
		Pointd p1 = it.next();
		Pointd p2 = it.next();
		
		int toRem = 0; // tracks how many points to remove. (impossible to remove while traversing)
		do
		{
			double l = Math.sqrt((p1.x_ - p2.x_)*(p1.x_ - p2.x_) + (p1.y_ - p2.y_)*(p1.y_ - p2.y_));
			if (l - Constants.EPSILON > s) 
			{ // anything remains in the last segment?
				double dx = (p2.x_ - p1.x_),
						dy = (p2.y_ - p1.y_);
				double dx_new = dx * s / l,
						dy_new = dy * s / l;
				p1.x_ += dx_new;
				p1.y_ += dy_new;
				s = 0; // to exit the loop
			}
			else
			{ // no => remove last segment (increase toRem)
				s -= l;
				p1 = p2;
				p2 = it.next();
				++toRem;
			}
		} while (s > 0);
		
		for (int i=0; i<toRem; i++)
			points_.removeLast();
	}
	
	private void updateItems (Game game)
	{
		specialItemChanged_ = false;
		if (wantUseSpecial_)
		{
			wantUseSpecial_ = false;
			if (specialItem_ != null)
			{
				if (specialItem_.activate(this, game))
				{
					specialItem_ = null;
					specialItemChanged_ = true; // since we removed it
				}
			}
		}
		
		itemsChanged_ = false;
		for (Iterator<Item> itIter = items_.iterator(); itIter.hasNext();)
		{
			Item it = itIter.next();
			boolean removeItem = it.effectStep(this);
			if (removeItem)
			{
				itIter.remove();
				itemsChanged_ = true;
			}
		}
	}
	

	/* ************ one step for the snake ***************/
	// the head shall advance, as well as the tail
	/**
	 * Advances the snake by one step, using dx_, dy_ and calls updateTurn().
	 * This function also handles the items. It finally calls computeHull().
	 * 
	 */
	public void advance(Game game)
	{
		// Handle the items; works since Snake receives items after advancing 
		 updateItems (game);
		
		// dead or immobile snakes don't move!!!
		if (!isAlive_ || speed_ == 0)
			return;

		// Now the snake can move!
		// set the variables for turning
		updateTurn();

		// advance the head
		{
			Pointd head = points_.getFirst();
			if (Globals.SNAKE_DEBUG)
				Log.logErr("  advance head; old = " + head);
			previousHead_ = head.clone();
			head.x_ += dx_;
			head.y_ += dy_;
			if (Globals.SNAKE_DEBUG)
				Log.logErr("  advance head; new = " + head);
		}


		// advance the tail
		if (toGrow_ >= speed_)
		{ // don't move the last point while growing
			toGrow_ -= speed_;
			length_ += speed_;
		}
		else
		{
			double s = speed_; // we have to remove s units
			if (toGrow_ > 0)
			{
				s = speed_ - toGrow_;
				length_ += s;
				toGrow_ = 0;
			}
			
			advanceTail(s);
		}

		if ( Globals.SNAKE_DEBUG)
		{
			String dbgPos = "snake(" + id_ + ") pos+dir: " + getHead()+ ";" + getDirection() + "|";
			Log.logMsg(dbgPos);
		}
		
		computeHull();
	}


	/* Drawable methods *******************************************/

	/**
	 * Returns the width of the snake at the given point. Cubic function, with the following conditions:
	 * width(0) = min_width
	 * width(len) = min_width
	 * width(alpha * length) = max_width
	 * dwidth/dx (alpha * length) = 0
	 * 
	 * @param x_ the length from the head at which the width_ is requested, x_ in [0,len]
	 * @return width_ of the snake at the specified length from the head
	 */
	protected double width(double x)
	{
		double alpha = 0.5; // sets where the largest part of the snake is. 0.5 is the middle (wrt length)
		double l = length_;
		
		// TODO global ???? at least snake variable
		double min = 2;
		double max = 4;
		
		double div = (1 - alpha) * (1 - alpha) * alpha * l;
		
		double w3 = (min - max) * ( 2 * alpha - 1) / div / alpha / l / l;
		
		double w2 = (max - min) * (3 * alpha * alpha - 1) / div / alpha / l;
		
		double w1 = (max - min) * (2 - 3 * alpha) / div;
		
		double ret =  x*x*x*w3 + x*x*w2 + x*w1 + min;
		return ret;
	}
	
	/**
	 * Draws the snake on the given Graphics.
	 * @param g an instance of Graphics where one wants to draw the snake
	 */
	public void draw(Graphics2D g)
	{
		if (isAlive_)
			g.setColor(color_);
		else
			g.setColor(GlobalColorTable.getDeadSnakeColor());
		
		if (settings_.useWideSnakes_)
			g.fillPolygon(hull_);	
		else
		{
			Iterator<Pointd> it_pts = points_.iterator();
			Pointd p1 = it_pts.next(), p2;
			while (it_pts.hasNext())
			{
				p2 = it_pts.next();
				g.drawLine((int)p1.x_, (int)p1.y_, (int)p2.x_, (int)p2.y_);
				p1 = p2;
			}
		}
	}
	
	/**
	 * Returns the ImageIcons for the items affecting the snake (can be empty)
	 * @return an ImageIcon[] of the items affecting the snake
	 */
	public ImageIcon[] getItemsImageIcons()
	{
		ArrayList <ImageIcon> ims = new ArrayList <ImageIcon> ();
		Iterator<Item> it = items_.iterator();
		for (; it.hasNext(); )
		{
			Item i = it.next();
			if (i.shouldDisplayInPanelInfo())
				ims.add(i.getImageIcon());
		}
		
		ImageIcon[] ret = new ImageIcon[ims.size()];
		ret = ims.toArray(ret);
		return ret;
	}
	/**
	 * Returns the ImageIcon of the special item the snake holds (can be null).
	 * @return the ImageIcon of the special item the snake holds
	 */
	public ImageIcon getSpecialItemImageIcon()
	{
		if (specialItem_ == null)
			return null;
		
		return specialItem_.getImageIcon();
	}

	/**
	 * Returns whether the given point lies inside this snake, or on it if not using width.
	 * @param p the point to be tested
	 * @return whether the given point lies inside the snake
	 */
	public boolean isPointInside(Pointd p)
	{
		if (settings_.useWideSnakes_)
			return hull_.contains(p.toAWTpt());
	
		Iterator<Pointd> it = points_.iterator();
		Pointd p1  = it.next(), p2;
		do
		{
			p2 = it.next();
			if (new Segment(p1, p2).isPointInside(p))
				return true;

			p1 = p2;
		} while (it.hasNext()); 
		
		return false;
	}
	
	/**
	 * Returns whether the given shape is intersecting with this snake.
	 * @param shape A geometric object to be tested
	 * @return whether the shape is intersecting with this snake
	 */
	public boolean isShapeInside(GeometricObject shape)
	{
		Iterator<Pointd> it_pts;
		if (settings_.useWideSnakes_)
			it_pts = hull_points_.iterator();
		else
			it_pts = points_.iterator();

		Pointd p1  = it_pts.next();
		Pointd p2;
		do
		{
			p2 = it_pts.next();
			if (null != new Segment(p1, p2).intersectGeneric(shape))
				return true;

			p1 = p2;
		} while (it_pts.hasNext()); 
		return false;
	}
	
	/**
	 * Tells whether the other snake is colliding into this one.
	 * Returns null if there is no collision.
	 * @return the point at which the snake is colliding into this one, or null
	 */
	public Pointd isSnakeColliding(Snake other)
	{
		Pointd p1other = other.getHead();
		Pointd p2other = other.getPreviousHead();
		Segment lineOther = new Segment(p1other, p2other);
		
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
			p2 = it.next(); // it's sure next() exists at the first step
			
			Segment l = new Segment(p1, p2);
			Pointd intersection = l.intersect(lineOther);
			if (intersection != null)
				return intersection;
			
			p1 = p2;
		} while (it.hasNext());
		
		return null;
	}

	/**
	 * Return the point at which the head is located
	 * @return the point at which the head is located
	 */
	public Pointd getHead()
	{
		return points_.getFirst();
	}

	/**
	 * Return the point at which the head was located at the previous step
	 * @return the point at which the head was located at the previous step
	 */
	protected Pointd getPreviousHead()
	{
		return previousHead_;
	}
	
	/**
	 * Return a line to test intersection, with the head point
	 * and the previous head position.
	 * @return a line to test intersection
	 */
	public Segment getIntersectionTestLine()
	{
		return new Segment(getHead(), getPreviousHead());
	}

	/**
	 * Return the point at which the tail is located
	 * @return the point at which the tail is located
	 */
	public Pointd getTail()
	{
		return points_.getLast();
	}
	
	/**
	 * Return a unique representation of this snake
	 * @return a unique representation of this snake
	 */
	public String getNetworkRepresentation()
	{
		return id_ + "," + x0_ + "," + y0_ + "," + direction_;
	}
	
	/**
	 * Return the direction towards which the snake is moving
	 * @return the direction towards which the snake is moving
	 */
	public int getDirection()
	{
		return direction_;
	}


}
