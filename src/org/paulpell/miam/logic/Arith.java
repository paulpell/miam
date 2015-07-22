package org.paulpell.miam.logic;

import org.paulpell.miam.geom.Pointd;

/**
 * Class containing the magic formulas.
 */
public class Arith
{
	
	public static double mind(double d1, double d2)
	{
		return d1 < d2 ? d1 : d2;
	}
	
	public static double maxd(double d1, double d2)
	{
		return d1 > d2 ? d2 : d1;
	}
	
	public static int min(int i1, int i2)
	{
		return i1 < i2 ? i1 : i2;
	}
	
	public static int max(int i1, int i2)
	{
		return i1 > i2 ? i1 : i2;
	}
	
	public static double absd(double d)
	{
		return d >= 0 ? d : -d;
	}
	
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
	
	public static int classicDirFromDegrees(int angle)
	{
		// TODO check whether up and down are correct!
		int a = (angle + 360) % 360;
		if (a <= 45 || a > 315)
			return Constants.DIR_RIGHT;
		if (a > 45 && a <= 135)
			return Constants.DIR_DOWN;
		if (a > 135 && a <= 225)
			return Constants.DIR_LEFT;
		return Constants.DIR_UP;
	}
	
	public static double deg2rad(int d)
	{
		return d * Math.PI / 180.;
	}
	
	public static int rad2deg(double r)
	{
		return (int)(180. * r / Math.PI);
	}
	
}