package org.paulpell.miam.logic;

import org.paulpell.miam.geom.Pointd;

/**
 * Class containing the magic formulas.
 */
public class Arith
{
	
	public static boolean equalsd(double d1, double d2)
	{
		double d = d1 - d2;
		return d > -Constants.EPSILON && d < Constants.EPSILON;
	}
	
	public static double dist(Pointd p1, Pointd p2)
	{
		double dx = p1.x_ - p2.x_;
		double dy = p1.y_ - p2.y_;
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	
	// find out the direction given two points (order matters).
	// two methods, one for angles and the other for integer encoding are provided
	public static int dirClassic(Pointd p1, Pointd p2)
	{
		return equalsd(p1.x_, p2.x_) ? (p1.y_ > p2.y_ ? Constants.DIR_DOWN : Constants.DIR_UP)
						:
				(p1.x_ > p2.x_ ? Constants.DIR_LEFT : Constants.DIR_RIGHT);
	}
	
	public static int dirModern(Pointd p1, Pointd p2)
	{
		if (equalsd(p1.x_, p2.x_)) // atan would return NaN..
		{ 
			if (p1.y_ < p2.y_)
				return 90;
			return 270;
		}
		
		int d = (int)(180. / Math.PI * Math.atan((p2.y_ - p1.y_) / (p2.x_ - p1.x_)));
		// since d is between 0 and 180, we have to correct
		if (p1.x_ > p2.x_)
			d += 180;
		return (360 + d) % 360;
	}
	
}