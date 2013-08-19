package logic.draw.items;

import geom.Pointd;
import geom.Rectangle;

import java.awt.Graphics;

import javax.swing.ImageIcon;

import logic.Constants;
import logic.Game;
import logic.draw.snakes.Snake;

public class ReversingItem extends Item {
	
	private static ImageIcon image;
	static {
		image = new ImageIcon("images/Reverse.png");
	}
	private int width = image.getIconWidth(), height = image.getIconHeight();
	
	public ReversingItem(double x0, double y0) {
		if (image == null) {
			throw new UnsupportedOperationException("Image could not be loaded!");
		}
		shape = new Rectangle(x0, y0, width, height);
		position = new Pointd(x0, y0);
	}


	public void draw(Graphics g) {
		g.drawImage(image.getImage(), (int)position.x, (int)position.y, (int)width, (int)height, null);
	}

	@Override
	public boolean effectStep(Snake s) {
		return true; // no during effect
	}
	public void startEffect(Snake s) {
		s.reverse();
	}

	@Override
	public Object clone(Game g) {
		double 	x 	= Math.random() * Constants.IMAGE_WIDTH,
				y 	= Math.random() * Constants.IMAGE_HEIGHT;
		return new ReversingItem(x, y);
	}
	
	@Override
	public ImageIcon getImageIcon() {
		return image;
	}
	
	public String getTextDescription() {
		return "Reverse";
	}
}
