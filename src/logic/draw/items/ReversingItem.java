package logic.draw.items;

import geom.Pointd;
import geom.Rectangle;

import java.awt.Graphics;

import javax.swing.ImageIcon;

public class ReversingItem extends Item {
	
	private static ImageIcon image;
	static {
		image = new ImageIcon("images/Reverse.png");
	}
	private int width = image.getIconWidth(), height = image.getIconHeight();
	//private Pointd position;
	
	public ReversingItem(double x0, double y0) {
		if (image == null) {
			throw new UnsupportedOperationException("Image could not be loaded!");
		}
		//width = image.getIconWidth();
		//height = image.getIconHeight();
		shape = new Rectangle(x0, y0, width, height);
		position = new Pointd(x0, y0);
	}

	public int getScore() {
		return 0;
	}
	public int getGrowth() {
		return 0;
	}
	public double getThickness() {
		return 0;
	}
	public boolean isPersistent() {
		return false;
	}
	public boolean isReversing() {
		return true;
	}
	public void draw(Graphics g) {
		g.drawImage(image.getImage(), (int)position.x, (int)position.y, (int)width, (int)height, null);
	}
	/*public Pointd getPointd() {
		return position;
	}*/
	/*public boolean isPointInside(Pointd p) {
		return p.x >= position.x && p.x <= position.x + width
				&& p.y >= position.y && p.y <= position.y + height;
	}*/
	//public boolean isSnakeColliding(Snake s) {
	/*public Pointd isSnakeColliding(Snake s) {
		//return isPointInside(s.getHead());
		Pointd p = s.getHead(),
				p2 = s.getPreviousHead();
		if (p.x >= position.x && p.x <= position.x + width) {
			if (p.y >= position.y && p.y <= position.y + height) {
				if (p2.y <= p.y) {
					return new Pointd(p.x, position.y);
				}
				else {
					return new Pointd(p.x, position.y + height);
				}
			}
		}
		else if (p.y >= position.y && p.y <= position.y + height) {
			if (p.x >= position.x && p.x <= position.x + width) {
				if (p2.x <= p.x) {
					return new Pointd(position.x,p.y);
				}
				else {
					return new Pointd(position.x + width, p.y);
				}
			}
		}
		return null;
	}*/

}
