package logic;

import geom.Pointd;

/**
 * Class containing the magic formulas.
 */
public class Arith {
	
	
	// find out the direction given two points (order matters).
	// two methods, one for angles and the other for integer encoding are provided
	public static int dir(Pointd p1, Pointd p2) {
		if (Globals.USE_CLASSIC_SNAKE) {
			return p1.x == p2.x ? (p1.y > p2.y ? Constants.DIR_UP : Constants.DIR_DOWN)
					:
					(p1.x > p2.x ? Constants.DIR_RIGHT : Constants.DIR_LEFT);
		}
		else { // using angles 0-359
			if (p1.x == p2.x) { // atan would return NaN..
				if (p1.y < p2.y) {
					return 90;
				}
				return 270;
			}
			
			int d = (int)(180. / Math.PI * Math.atan((p2.y - p1.y) / (p2.x - p1.x)));
			// since d is between 0 and 180, we have to correct
			if (p1.x > p2.x) {
				d += 180;
			}
			return (360 + d) % 360;
		}
	}
	
}