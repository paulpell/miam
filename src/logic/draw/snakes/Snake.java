package logic.draw.snakes;


import java.util.Iterator;
import java.util.LinkedList;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import javax.swing.ImageIcon;

import logic.Arith;
import logic.Constants;
import logic.Globals;
import logic.draw.Drawable;
import logic.draw.items.Item;
import logic.draw.items.SpecialItem;
import geom.GeometricObject;
import geom.Pointd;
import geom.Line;
import geom.Vector2D;


/**
 * Is actually updated in advance().
 * Keyboard events are asynchronous; a flag is raised when the snake has to turn.
 * 
 * @author paul
 *
 */

public class Snake extends Drawable {
	
	/* Static constants/functions ***************************/
	
	// internal counter, reset at new Game()
	//private static int globalId = 0;
	
	// some default colors
	final private static Color[] colors = { new Color(160, 20, 50),
									new Color(50, 140, 20),
									new Color(30, 30, 190),
									new Color(10, 150, 60),
									new Color(130, 100, 10)};
	final private static Color deadColor = new Color(200,200,0);
	
	// for a new game, globalId should be 0
	/*public static void resetIds() {
		globalId = 0;
	}*/
	
	
	
	
	/* Individual properties *********************/
	//int id = Snake.globalId++;
	int id;
	
	int score = 0;
	boolean isAlive = true;
	// physics/geometry
	int speed = Globals.SNAKE_NORMAL_SPEED;
	int extraspeed = Globals.SNAKE_SPEEDUP_EXTRA;
	boolean speedup = false;
	int direction; // either 0-3 (in classic mode) or an angle based on 360 degrees (std trigo)
	double dx, dy; // cached values for how to advance, use computeDs() to update
	int angleDiff = speed * Globals.SNAKE_ANGLE_SPEED_FACTOR;
	double length = 1;//Constants.INIT_SNAKE_LENGTH;
	double toGrow = Constants.INIT_SNAKE_LENGTH;//length;
	double extraThickness = Globals.SNAKE_DEFAULT_EXTRA_THICKNESS;
	Color color;
	
	// to make sure we don't add several points between two advance() calls
	boolean turnLeft = false, turnRight = false;
	
	// the first point will be the head!
	LinkedList<Pointd> points = new LinkedList<Pointd>();
	Pointd previousHead;
	
	// is updated by computeHull(), used in draw() and for TODO: collision tests
	LinkedList<Pointd> hull_points = new LinkedList<Pointd>();
	Polygon hull;
	
	/* ****** Item stuff **********/
	int randomTurn = 0; // how many bananas are in effect
	
	//  the special item is the one the player can use
	// for now, let's accept only one SpecialItem per snake
	SpecialItem specialItem;
	boolean wantUseSpecial = false; // set when player uses 'special' key, item activated in advance()
	
	// we also have a list of items having effect on the snake
	LinkedList<Item> items = new LinkedList<Item>(); 
	boolean itemsChanged = false;// set to true during one step (after removing/adding an item)
	boolean specialItemChanged = false;
	
	
	/* Constructor ***************/
	public Snake(int x0, int y0, int dir) {
		id = Globals.control.getNextSnakeIndex();
		color = colors[id];
		direction = dir;
		computeDs();
		previousHead = new Pointd(x0, y0); 
		points.add(previousHead);
		double x1 = x0 + dx;
		double y1 = y0 + dy;
		points.addFirst(new Pointd(x1,y1));
		length = Math.sqrt((x0-x1)*(x0-x1) + (y0-y1)*(y0-y1));
		computeHull();
	}
	
	public int getId() {
		return id;
	}
	
	public Color getColor() {
		return color;
	}
	
	/* Properties of the snake **********************************************/
	public boolean isAlive() {
		return isAlive;
	}
	// we give a point where we want to draw the head
	public void kill(Pointd newHead) {
		points.removeFirst();
		points.addFirst(newHead); // haha trick
		isAlive = false;
		computeHull();
	}
	public void resurrect() {
		if (isAlive) {
			return;
		}
		reverse();
		isAlive = true;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeedup(boolean b) {
		if (b && !speedup) {
			speedup = true;
			speed += extraspeed;
		}
		else if (!b && speedup) {
			speedup = false;
			speed -= extraspeed;
		}
		computeDs();
	}
	public void addSpeedupSpecial(double extra) {
		speed += extra;
	}
	
	public void addScore (int s) {
		score += s;
	}
	public int getScore() {
		return score;
	}
	
	public void setRandomTurning(boolean b) {
		randomTurn += (b ? 1 : -1);
	}
	
	public void special() {
		wantUseSpecial = true; // we'll use it at the beginning of advance()
	}
	
	public void acceptItem(Item it) {
		if (it instanceof SpecialItem) {
			specialItemChanged = true;
			specialItem = (SpecialItem)it;
		}
		else { // normal item
			itemsChanged = true;
			it.startEffect(this);
			items.add(it);
		}
	}
	public boolean itemsChanged() { // used in InfoPanel.update()
		return itemsChanged;
	}
	public boolean specialItemChanged() {
		return specialItemChanged;
	}
	
	public void reverse() {
		LinkedList<Pointd> ps = new LinkedList<Pointd>();
		Iterator<Pointd> it = points.iterator();
		while(it.hasNext()) {
			ps.addFirst(it.next());
		}
		points = ps;
		direction = Arith.dir(ps.get(1), ps.get(0));
		computeDs();
	}
	
	public double getExtraThickness() {
		return extraThickness;
	}

	
	/* **********************************************************************/
	/* *** Movement methods ************************************************/
	
	public void growBy(int l) {
		toGrow += l;
	}
	

	public void setTurnLeft(boolean val) {
		turnLeft  = val;
	}

	public void setTurnRight(boolean val) {
		turnRight  = val;
	}
	
	/* ********** computes dx and dy, the distances the snake will cover on the x and y axis */
	private void computeDs() {
		if (Globals.USE_CLASSIC_SNAKE) {
			dx = ((direction + 1) % 2) * (direction - 1) * speed;
			dy = (direction % 2) * (direction - 2) * speed;
		}
		else {
			dx = Math.cos(direction / 180. * Math.PI) * speed;
			dy = Math.sin(direction / 180. * Math.PI) * speed;
		}
	}
	
	/* ********** turn *********/
	private void turnLeft() {
		direction = (direction - angleDiff + 360) % 360;
		computeDs();
	}
	private void turnRight() {
		direction = (direction + angleDiff + 360) % 360;
		computeDs();
	}
	
	private void updateTurn() {
		// to handle the uncontrolled turn (banana)
		int nextDir = -1;
		if (randomTurn > 0) {
			nextDir = Constants.rand.nextInt(3);
		}
		
		if (nextDir == -1) {
			if (turnLeft && !turnRight) {
				turnLeft();
			}
			if (turnRight && !turnLeft) {
				turnRight();
			}
			
		}
		// if 0, go straight
		else if (nextDir == 1) {
			turnLeft();
		}
		else if (nextDir == 2) {
			turnRight();
		}
		
		if (((turnLeft ^ turnRight) && nextDir == -1) // if manual turn
				|| nextDir == 1 || nextDir == 2) { // or banana
			Pointd newHead = (Pointd) points.getFirst().clone();
			points.addFirst(newHead);
		}
	}
	
	/**
	 * O(n) algorithm to re-compute the hull for the snake, it actually sets hull_points (a list of Pointd).
	 */
	private void computeHull() {
		
		LinkedList<Pointd> newHull = new LinkedList<Pointd>();
		
		// if the snake is a simple line, simply use that line, by adding the points once forward, then once backward.
		if (!Globals.SNAKE_USE_WIDTH) {
			Iterator<Pointd> it_pts;
			for (it_pts = points.iterator(); it_pts.hasNext();)
				newHull.add(it_pts.next());
			for (it_pts = points.descendingIterator(); it_pts.hasNext();)
				newHull.add(it_pts.next());
			hull_points = newHull;
			//return;
		}
		
		
		
		
		// When not in classic mode, we create segments of length seg_length
		double seg_length = 10;
		
		// Draw a polygon around the segments
		if (!Globals.USE_CLASSIC_SNAKE) {		

			boolean snd_traversal = false;
			double w;
			
			
			// now create a polygon, and add the points in the following manner:
			// for every segment:
			//   let v be a unit vector in the direction of the segment.
			//   create a vector tangent to v, of length given by width(), and create a point at
			//   its extremity.
			// do that twice, so that we can ``circle'' the snake!
			Iterator<Pointd> it_pts = points.iterator();
			while (it_pts.hasNext()) { // this loops 2 times: once forward, the other backward in points
				double diff_pts;// length of a segment
				double traversed_len = 0;// cumulated traversed length 
				
				
				// p the next point, p_p the previous one.
				Pointd p = it_pts.next(), p_p = p; // the 2nd time, it's a descending iterator
				do {
					p = it_pts.next();
	
					Vector2D v = new Vector2D(p_p, p);
					diff_pts = v.length();
					
					v.normalize(); // unit-vector
					Vector2D v_norm_unit = v.normal(); // normal vector
					
					
					// 1 + (as many times as there is 10 in the segment)
					for (double l = traversed_len; l < traversed_len + diff_pts; l += seg_length) {
						Pointd orig = v.multiply(l - traversed_len).add(p_p); // where v_norm is applied
						
						// width at that point
						if (snd_traversal) 	w = width(length - l);
						else				w = width(l);
						// vector of length w
						Vector2D v_norm = v_norm_unit.multiply(w);
						//int x = (int)(orig.x + v_norm.getX());
						//int y = (int)(orig.y + v_norm.getY());
						double x = orig.x + v_norm.getX();
						double y = orig.y + v_norm.getY();
						//poly.addPoint(x,y);
						newHull.add(new Pointd(x,y));
						//System.out.printf("(%d,%d) ",x,y);
					}
					
					// add a point at the end of the segments anyway
					double l = traversed_len + diff_pts;
					Pointd orig = p; // where v_norm is applied
					
					if (snd_traversal) 	w = width(length - l);
					else				w = width(l);
					Vector2D v_norm = v_norm_unit.multiply(w);
					double x = orig.x + v_norm.getX();
					double y = orig.y + v_norm.getY();
					newHull.add(new Pointd(x,y));
					//poly.addPoint(x,y);

					p_p = p;
					traversed_len += diff_pts;
				} while (it_pts.hasNext());
				
				if (p != getHead()) {// then, it was the forward traversal, we go to the backward one.
					it_pts = points.descendingIterator();
					snd_traversal = true;
				}
			}
			hull_points = newHull;
			//return;
		}
	
		hull = new Polygon();
		Iterator<Pointd> it_pts = hull_points.iterator();
		while(it_pts.hasNext()) {
			Pointd p = it_pts.next();
			hull.addPoint((int)p.x, (int)p.y);
			if (p.x <= 10 && p.y <= 10) {
				System.out.println("P in creating hull is near 0!!:"+p);
			}
		}
	}
	

	/* ************ one step for the snake ***************/
	// the head shall advance, as well as the tail
	public void advance()
	{
		// Handle the items; works since Snake receives items after advancing 

		specialItemChanged = false;
		if (wantUseSpecial) {
			wantUseSpecial = false;
			if (specialItem != null) {
				if (specialItem.activate(this)) {
					specialItem = null;
					specialItemChanged = true; // since we removed it
				}
			}
		}
		
		itemsChanged = false;
		for (Iterator<Item> itIter = items.iterator(); itIter.hasNext();) {
			Item it = itIter.next();
			boolean removeItem = it.effectStep(this);
			if (removeItem) {
				itIter.remove();
				itemsChanged = true;
			}
		}
		
		
		
		// Now the snake can move!
		
		// dead or immobile snakes don't move!!!
		if (!isAlive || speed == 0) {
			return;
		}
		
		// set the variables for turning
		updateTurn();
		
		// advance the head
		{
		Pointd head = points.getFirst();
		previousHead = (Pointd)head.clone();
		head.x += dx;
		head.y += dy;
		}

		// advance the tail
		double s = speed; // we have to remove s units
		if (toGrow >= speed) { // don't move the last point while growing
			toGrow -= speed;
			length += speed;
		}
		else {
			if (toGrow> 0) {
				s = speed - toGrow;
				length += s;
				toGrow = 0;
			}
			
			// remove as many points at the end and shorten as needed
			Iterator<Pointd> it = points.descendingIterator();
			Pointd p1 = it.next();
			Pointd p2 = it.next();
			int toRem = 0; // tracks how many points to remove. (impossible to remove while traversing)
			do {
				if (p1.x == 0 && p1.y == 0) {
					System.out.println("Point 1 is zero!!");
				}
				if (p2.x == 0 && p2.y == 0) {
					System.out.println("Point2  is zero!!");
				}
				double l = Math.sqrt((p1.x - p2.x)*(p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y));
				if (l > s) { // anything remains in the last segment?
					double dx = (p2.x - p1.x),
							dy = (p2.y - p1.y);
					p1.x += dx * s / l;
					p1.y += dy * s / l;
					s = 0; // to exit the loop
				}
				else { // no => remove last segment (increase toRem)
					s -= l;
					p1 = p2;
					p2 = it.next();
					++toRem;
				}
			} while (s > 0);
			
			for (int i=0; i<toRem; i++) {
				points.removeLast();
			}
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
	 * @param x the length from the head at which the width is requested, x in [0,len]
	 * @return width of the snake at the specified length from the head
	 */
	private double width(double x) {
		double alpha = 0.5; // sets where the largest part of the snake is. 0.5 is the middle (wrt length)
		double l = length;
		
		// TODO global ???? at least snake variable
		double min = 2, max = 4;
		
		double div = (1 - alpha) * (1 - alpha) * alpha * l;
		
		double w3 = (min - max) * ( 2 * alpha - 1) / div / alpha / l / l;
		
		double w2 = (max - min) * (3 * alpha * alpha - 1) / div / alpha / l;
		
		double w1 = (max - min) * (2 - 3 * alpha) / div;
		
		double ret =  x*x*x*w3 + x*x*w2 + x*w1 + min;
		return ret;
	}
	
	
	// return the hull containing this snake, be it classic and/or wide or not
	/*private Polygon getSnakeHull() {

		Polygon poly = new Polygon();
		
		
		// if the snake is a simple line, simply return that line, by adding the points once forward, then once backward.
		if (!Globals.SNAKE_USE_WIDTH) {
			
			Iterator<Pointd> it_pts;
			
			for (it_pts = points.iterator(); it_pts.hasNext();) {
				Point p = it_pts.next().toAWTpt();
				poly.addPoint(p.x, p.y);
			}
			for (it_pts = points.descendingIterator(); it_pts.hasNext();) {
				Point p = it_pts.next().toAWTpt();
				poly.addPoint(p.x, p.y);
			}
			
			return poly;
		}
		
		
		
		
		// When not in classic mode, we create segments of length seg_length
		double seg_length = 10;
		
		// Draw a polygon around the segments
		if (!Globals.USE_CLASSIC_SNAKE) {
			

			boolean snd_traversal = false;
			double w;
			
			
			// now create a polygon, and add the points in the following manner:
			// for every segment:
			//   let v be a unit vector in the direction of the segment.
			//   create a vector tangent to v, of length given by width(), and create a point at
			//   its extremity.
			// do that twice, so that we can ``circle'' the snake!
			Iterator<Pointd> it_pts = points.iterator();
			while (it_pts.hasNext()) { // this loops 2 times: once forward, the other backward in points
				double diff_pts;// length of a segment
				double total_len = 0;// cumulated traversed length 
				
				
				// p the next point, p_p the previous one.
				Pointd p = it_pts.next(), p_p = p; // the 2nd time, it's a descending iterator
				do {
					p = it_pts.next();
	
					Vector2D v = new Vector2D(p_p, p);
					diff_pts = v.length();
					
					v.normalize(); // unit-vector
					Vector2D v_norm_unit = v.normal(); // normal vector
					
					
					// 1 + (as many times as there is 10 in the segment)
					for (double l = total_len; l < total_len + diff_pts; l += seg_length) {
						Pointd orig = v.multiply(l - total_len).add(p_p); // where v_norm is applied
						
						// width at that point
						if (snd_traversal) 	w = width(length - l);
						else				w = width(l);
						// vector of length w
						Vector2D v_norm = v_norm_unit.multiply(w);
						int x = (int)(orig.x + v_norm.getX());
						int y = (int)(orig.y + v_norm.getY());
						poly.addPoint(x,y);
						System.out.printf("(%d,%d) ",x,y);
					}
					
					// add a point at the end of the segments anyway
					double l = total_len + diff_pts;
					Pointd orig = p; // where v_norm is applied
					
					if (snd_traversal) 	w = width(length - l);
					else				w = width(l);
					Vector2D v_norm = v_norm_unit.multiply(w);
					int x = (int)(orig.x + v_norm.getX());
					int y = (int)(orig.y + v_norm.getY());
					poly.addPoint(x,y);

					p_p = p;
					total_len += diff_pts;
				} while (it_pts.hasNext());
				
				if (p != getHead()) {// then, it was the forward traversal, we go to the backward one.
					it_pts = points.descendingIterator();
					snd_traversal = true;
				}
			}
		}
		return poly;
	}*/
	
	/**
	 * Draws the snake on the given Graphics.
	 * @param g an instance of Graphics where one wants to draw the snake
	 */
	public void draw(Graphics g) {
		if (isAlive) {
			g.setColor(color);
		}
		else {
			g.setColor(deadColor);
		}
		
		if (Globals.SNAKE_USE_WIDTH) {
			double w;
			
			// draw the head and the tail: 
			w = width(0) / 2.;
			g.fillOval((int)(getHead().x - w), (int)(getHead().y - w), 2*(int)w, 2*(int)w);
			w = width(length);
			g.fillOval((int)(getTail().x - w), (int)(getTail().y - w), 2*(int)w, 2*(int)w);
		
			g.fillPolygon(hull);
			/*Polygon poly = new Polygon();
			Iterator<Pointd> it_hull = hull_points.iterator();
			for(; it_hull.hasNext();) {
				Pointd p = it_hull.next();
				poly.addPoint((int)p.x, (int)p.y);
			}
			g.fillPolygon(poly);*/	
		}	
		
		else { // ie. the snake is not wide.
			Iterator<Pointd> it_pts = points.iterator();
			Pointd p1 = it_pts.next(), p2;
			while (it_pts.hasNext()) {
				p2 = it_pts.next();
				g.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
				p1 = p2;
			}
		}
		/*
		

		// Draw a polygon around the segments
		if (Globals.SNAKE_USE_WIDTH) {
			

			boolean snd_traversal = false;
			double w;
			
			// draw the head and the tail: 
			w = width(0);
			g.fillOval((int)(getHead().x - w), (int)(getHead().y - w), 2*(int)w, 2*(int)w);
			w = width(length);
			g.fillOval((int)(getTail().x - w), (int)(getTail().y - w), 2*(int)w, 2*(int)w);
			
			// now create a polygon, and add the points in the following manner:
			// for every segment:
			//   let v be a unit vector in the direction of the segment.
			//   create a vector tangent to v, of length given by width(), and create a point at
			//   its extremity.
			// do that twice, so that we can ``circle'' the snake!
			Polygon poly = new Polygon();
			Iterator<Pointd> it_pts = points.iterator();
			while (it_pts.hasNext()) { // this loops 2 times: once forward, the other backward in points
				double seg_len;// length of a segment
				double total_len = 0;// cumulated traversed length 
				
				
				// p the next point, p_p the previous one.
				Pointd p = it_pts.next(), p_p = p; // the 2nd time, it's a descending iterator
				do {
					p = it_pts.next();
					//l += Math.sqrt((p.x - p_p.x)*(p.x - p_p.x) + (p.y -p_p.y)*(p.y-p_p.y));
					//seg_len = Math.sqrt((p.x - p_p.x)*(p.x - p_p.x) + (p.y -p_p.y)*(p.y-p_p.y));
					//total_len += seg_len;
	
					Vector2D v = new Vector2D(p_p, p);
					seg_len = v.length();
					
					v.normalize(); // unit-vector
					Vector2D v_norm_unit = v.normal(); // normal vector
					
					
					// 1 + (as many times as there is 10 in the segment)
					for (double l = total_len; l < total_len + seg_len; l += 10) {
						Pointd orig = v.multiply(l - total_len).add(p_p); // where v_norm is applied
						
						// width at that point
						if (snd_traversal) 	w = width(length - l);
						else				w = width(l);
						// vector of length w
						Vector2D v_norm = v_norm_unit.multiply(w);
						int x = (int)(orig.x + v_norm.getX());
						int y = (int)(orig.y + v_norm.getY());
						poly.addPoint(x,y);
						System.out.printf("(%d,%d) ",x,y);
						//g.drawString(""+(++i), x, y);
					}
					
					// add a point at the end of the segments anyway
					double l = total_len + seg_len;
					Pointd orig = p; // where v_norm is applied
					
					if (snd_traversal) 	w = width(length - l);
					else				w = width(l);
					Vector2D v_norm = v_norm_unit.multiply(w);
					int x = (int)(orig.x + v_norm.getX());
					int y = (int)(orig.y + v_norm.getY());
					poly.addPoint(x,y);
					//g.drawString("s"+(++i), x, y);
					//System.out.printf("(%d,%d) ",x,y);
					

					p_p = p;
					total_len += seg_len;
				} while (it_pts.hasNext());
				
				if (p != getHead()) {// then, it was the forward traversal, we go to the backward one.
					it_pts = points.descendingIterator();
					snd_traversal = true;
				}
			}
			Polygon poly = getHull();
			g.fillPolygon(poly); 
			System.out.println("Poly");
			if (points.size() > 2) {
				System.out.println("special");
			}
			
		}

		// OR we can draw a simple line, from point to point
		else{ // if not Globals.SNAKE_USE_WIDTH
			Iterator<Pointd> e = points.iterator();
			Pointd p1 = e.next(), p2;
			do {
				p2 = e.next();
				g.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
	
				p1 = p2;
			} while (e.hasNext());
			g.setColor(new Color(255,255,255));
			p1 = getHead();
			g.fillRect((int)p1.x, (int)p1.y, 1, 1);
		}*/
	}
	
	/**
	 * Returns the ImageIcons for the items affecting the snake (can be empty)
	 * @return an ImageIcon[] of the items affecting the snake
	 */
	public ImageIcon[] getItemsImageIcons() {
		ImageIcon[] ims = new ImageIcon[items.size()];
		Iterator<Item> it = items.iterator();
		for (int i = 0; i < items.size(); ++i) {
			ims[i] = it.next().getImageIcon();
		}
		return ims;
	}
	/**
	 * Returns the ImageIcon of the special item the snake holds (can be null).
	 * @return the ImageIcon of the special item the snake holds
	 */
	public ImageIcon getSpecialItemImageIcon() {
		if (specialItem == null) {
			return null;
		}
		return specialItem.getImageIcon();
	}

	
	public boolean isPointInside(Pointd p) {
		if (Globals.SNAKE_USE_WIDTH) {
			return hull.contains(p.toAWTpt());
		}
		else {
			Iterator<Pointd> it = points.iterator();
			Pointd p1  = it.next(), p2;
			do {
				p2 = it.next();
				if (new Line(p1, p2).isPointInside(p)) {
					return true;
				}
				p1 = p2;
			} while (it.hasNext()); 
			return false;
		}
	}
	
	// the list is used by threads for creating items/collision tests so we clone it
	public boolean isShapeInside(GeometricObject shape) {
		Iterator<Pointd> it_pts = hull_points.iterator();
		Pointd p1 = it_pts.next(), p2;
		for (; it_pts.hasNext();) {
			p2 = p1;
			p1 = it_pts.next();
			
			if (shape.intersect(new Line(p1, p2)) != null) return true;
		}
		return false;
		
		
		/*if (Globals.SNAKE_USE_WIDTH) {
			throw new UnsupportedOperationException("yeah");
		}
		else {
		*	LinkedList<Pointd> copyPts = (LinkedList<Pointd>)(points.clone());
			Iterator<Pointd> iter = copyPts.iterator();
			Pointd p1 = iter.next(), p2;
			for (;iter.hasNext();) {
				p2 = iter.next();
				if (null != shape.intersect(new Line(p1,p2))) {
					return true;
				}
			}
			return false;
		//}
		 *
		 */
	}
	
	public Pointd isSnakeColliding(Snake other) {
		Pointd p1other = other.getHead(),
				p2other = other.getPreviousHead();
		Line lineOther = new Line(p1other, p2other);
		boolean otherHorizontal = Arith.equalsd(p1other.y, p2other.y);
		
		Iterator<Pointd> it = points.iterator();
		
		
		// if it's the same Snake, it can't collide with its first 3 segments
		if (other == this) {
			if (points.size() < 5) {
				return null; // impossible collision
			}
			it.next(); it.next(); it.next(); // skip first points
		}
		Pointd p1 = it.next(), p2; // p1-p2 will represent the segments of this Snake
		do {
			p2 = it.next(); // it's sure next() exists at the first step
			
			if (Globals.USE_CLASSIC_SNAKE) {
				// both horizontal or both vertical means an impossible collision
				if (otherHorizontal && Arith.equalsd(p1.x,p2.x)) {
					if ((p1other.y >= p1.y && p1other.y <= p2.y)
							|| (p1other.y <= p1.y && p1other.y >= p2.y)) {
						if ((p1.x - p1other.x) * (p1.x - p2other.x) <= 0) {
							return new Pointd(p1.x, p1other.y);
						}
					}
				}
				else if (!otherHorizontal && Arith.equalsd(p1.y, p2.y)) {
					if ((p1other.x >= p1.x && p1other.x <= p2.x)
							|| (p1other.x <= p1.x && p1other.x >= p2.x)) {
						if ((p1.y - p1other.y) * (p1.y - p2other.y) <= 0) {
							return new Pointd(p1other.x,p1.y);
						}
					}
				}
			}
			else { // !Globals.USE_CLASSIC_SNAKE
				
				Line l = new Line(p1, p2);
				Pointd intersection = l.intersect(lineOther);
				if (intersection != null) {
					return intersection;
				}
				
			}
			
			
			p1 = p2;
		} while (it.hasNext());
		return null;
	}

	public Pointd getHead() {
		return points.getFirst();
	}
	public Pointd getPreviousHead() {
		return previousHead;
	}
	public Pointd getTail() {
		return points.getLast();
	}
	
	public Pointd getPointd() {
		return getHead();
	}

}
