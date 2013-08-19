package logic.draw.items;

import geom.Pointd;
import geom.Rectangle;

import java.awt.Graphics;

import javax.swing.ImageIcon;

import logic.Constants;
import logic.Game;
import logic.Globals;
import logic.draw.snakes.Snake;

public class Lightning extends Item {
	
	private static ImageIcon image = new ImageIcon("images/Lightning.png");
	private static int width = image.getIconWidth(), height = image.getIconHeight();
	
	private double extraSpeed;
	
	public Lightning(double x0, double y0) {
		if (image == null) {
			throw new UnsupportedOperationException("Image Lightning.png could not be loaded!");
		}
		shape = new Rectangle(x0, y0, width, height);
		position = new Pointd(x0, y0);
		duration = 0;
		extraSpeed = Constants.rand.nextDouble() * Globals.SNAKE_SPEEDUP_EXTRA;
	}
	
	
	@Override
	public boolean effectStep(Snake s) {
		if (duration == -1) {
			s.addSpeedupSpecial(-extraSpeed);
			return true; // true when cancelled
		}
		return false;
	}

	@Override
	public void startEffect(Snake s) {
		s.addSpeedupSpecial(extraSpeed);
	}
	

	public Object clone(Game g) {
		double 	x 	= Math.random() * Constants.IMAGE_WIDTH,
		y 	= Math.random() * Constants.IMAGE_HEIGHT;
		return new Lightning(x, y);
	}
	
	@Override
	public void draw(Graphics g) {
		g.drawImage(image.getImage(), (int)position.x, (int)position.y, null);
	}
	
	public ImageIcon getImageIcon() {
		return image;
	}
	
	public String getTextDescription() {
		return "Lightning";
	}
}
