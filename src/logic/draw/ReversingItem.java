package logic.draw;

import geom.Pointd;

import java.awt.Graphics;

import javax.swing.ImageIcon;

public class ReversingItem extends Item {
	
	private static ImageIcon image;
	static {
		image = new ImageIcon("images/Reverse.png");
	}
	private int width = image.getIconWidth(), height = image.getIconHeight();
	
	
	public ReversingItem(double x0, double y0) {
		super(x0, y0);
		if (image == null) {
			throw new UnsupportedOperationException("Image could not be loaded!");
		}
	}

	public int getScore() {
		return 0;
	}
	public int getGrowth() {
		// TODO Auto-generated method stub
		return 0;
	}
	public double getThickness() {
		return 0;
	}
	public boolean isPersistent() {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean isReversing() {
		// TODO Auto-generated method stub
		return true;
	}
	public void draw(Graphics g) {
		g.drawImage(image.getImage(), (int)position.x, (int)position.y, width, height, null);
	}
	public Pointd getPointd() {
		return position;
	}
	public boolean isPointInside(Pointd p) {
		return p.x >= position.x && p.x <= position.x + width
				&& p.y >= position.y && p.y <= position.y + height;
	}
	public boolean isSnakeColliding(Snake s) {
		//Pointd p = s.getHead();
		return isPointInside(s.getHead());//p.x >= position.x && p.x <= position.x + width
			//	&& p.y >= position.y && p.y <= position.y + height;
	}

	public String toString() {
		return "ReversingItem at " + position;
	}
}
