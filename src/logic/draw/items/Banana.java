package logic.draw.items;

import geom.Pointd;
import geom.Rectangle;

import java.awt.Graphics;

import javax.swing.ImageIcon;

public class Banana extends Item {
	
	private static ImageIcon image;
	static {
		image = new ImageIcon("images/Banana.png");
	}
	private int width = image.getIconWidth(), height = image.getIconHeight();
	
	public Banana(double x0, double y0) {
		if (image == null) {
			throw new UnsupportedOperationException("Image Banana.png could not be loaded!");
		}
		System.out.printf("Banana: width = %d, height = %d\n",width, height);
		shape = new Rectangle(x0, y0, width, height);
		position = new Pointd(x0, y0);
	}
	
	@Override
	public int getScore() {return 0;}

	@Override
	public int getGrowth() {return 0;}

	@Override
	public double getThickness() {return 0;}

	@Override
	public boolean isPersistent() {return false;}

	@Override
	public boolean isReversing() {return false;}

	@Override
	public void draw(Graphics g) {
		g.drawImage(image.getImage(), (int)position.x, (int)position.y, (int)width, (int)height, null);
	}

}
