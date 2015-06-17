package org.paulpell.miam.net;


import java.awt.Color;
import java.util.Vector;

import org.paulpell.miam.geom.Circle;
import org.paulpell.miam.geom.GeometricObject;
import org.paulpell.miam.geom.Segment;
import org.paulpell.miam.geom.Pointd;
import org.paulpell.miam.geom.Rectangle;
import org.paulpell.miam.logic.draw.walls.Wall;
import org.paulpell.miam.logic.draw.walls.WallElement;

public class WallEncoder
{

	public static String encodeWall(Wall w)
			throws Exception
	{
		String ret = w.getWidth() + "," + w.getHeight() + "\n";
		
		Vector<WallElement> elements = w.getElements();
		
		for (WallElement e: elements)
		{
			ret += e.getColor().getRGB() +
					";" + 
					encodeWallElement(e.getGeometricObject()) + 
					"\n";
		}
		return ret;
	}
	
	public static Wall decodeWall(String s)
		throws Exception
	{
		int endline = s.indexOf('\n');
		// find dimensions
		String line1 = s.substring(0, endline);
		if (null == line1)
			throw new Exception("Expected wall dimensions, found EOF");
		
		String[] ssdims = line1.split(",");
		if (ssdims.length != 2)
			throw new Exception("Could not find dimension separator!");
		int width = Integer.parseInt(ssdims[0]);
		int height = Integer.parseInt(ssdims[1]);
		Wall w = new Wall(width, height);
		
		s = s.substring(endline + 1);
		String[] lines = s.split("\n");
		
		// find wall elements
		for (String line : lines)
		{
			w.pushElement(decodeWallElement(line));
		}
		
		return w;
	}
	
	private static WallElement decodeWallElement(String s)
	{
		String[] ss = s.split(";");
		Color color = new Color(Integer.parseInt(ss[0]));
		
		char type = ss[1].charAt(0);
		s = ss[1].substring(1); // remove type char
		switch (type)
		{
		case 'l':
			return decodeLine(color, s);
		case 'r':
			return decodeRectangle(color, s);
		case 'c':
			return decodeCircle(color, s);
		}
		
		throw new UnsupportedOperationException("unimplemented element in decodeWallElement");
	}
	
	private static WallElement decodeLine(Color color, String l)
	{
		String[] ss = l.split(":");
		Pointd p1 = NetMethods.bytes2point(ss[0].getBytes());
		Pointd p2 = NetMethods.bytes2point(ss[1].getBytes());
		
		Segment line = new Segment(p1, p2);
		
		return new WallElement(line, color);
	}
	
	private static WallElement decodeRectangle(Color color, String l)
	{
		String[] ss = l.split(":");
		Pointd p1 = NetMethods.bytes2point(ss[0].getBytes());
		Pointd p2 = NetMethods.bytes2point(ss[1].getBytes());
		
		double w = p2.x_ - p1.x_;
		double h = p2.y_ - p1.y_;
		
		Rectangle rect = new Rectangle(p1.x_, p1.y_, w, h);
		
		return new WallElement(rect, color);
		
	}
	
	private static WallElement decodeCircle(Color color, String l)
	{
		String[] ss = l.split(":");
		double radius = NetMethods.str2double(ss[0]);
		Pointd p = NetMethods.bytes2point(ss[1].getBytes());
		
		Circle circle = new Circle(p, radius);
		
		return new WallElement(circle, color);
	}
	
	
	private static String encodeWallElement(GeometricObject go)
	{
		if (go instanceof Segment)
			return encodeWallElement((Segment)go);
		else if (go instanceof Rectangle)
			return encodeWallElement((Rectangle)go);
		else if (go instanceof Circle)
			return encodeWallElement((Circle)go);

		throw new UnsupportedOperationException("unimplemented element in encodeWallElement");
	}


	private static String encodeWallElement(Segment l)
	{
		String p1str = new String(NetMethods.point2bytes(l.getP1()));
		String p2str = new String(NetMethods.point2bytes(l.getP2()));
		return 'l' + p1str + ":" + p2str;
	}
	
	private static String encodeWallElement(Rectangle r)
	{
		String p1str = new String(NetMethods.point2bytes(r.getP1()));
		String p2str = new String(NetMethods.point2bytes(r.getP2()));
		return 'r' + p1str + ":" + p2str;
	}
	
	private static String encodeWallElement(Circle c)
	{
		String pstr = new String(NetMethods.point2bytes(c.getPoint()));
		return 'c' + NetMethods.double2str(c.getRadius()) + ":" + pstr;
	}
}
