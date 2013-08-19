package logic.draw.items;

import geom.Pointd;
import geom.Rectangle;

import java.awt.Graphics;

import javax.swing.ImageIcon;

import logic.Constants;
import logic.Game;
import logic.draw.snakes.Snake;

public class Banana extends Item {
	
	public  static ImageIcon image = new ImageIcon("images/Banana.png");
	
	private static int width = image.getIconWidth(), height = image.getIconHeight();
	
	public Banana(double x0, double y0) {
		if (image == null) {
			throw new UnsupportedOperationException("Image Banana.png could not be loaded!");
		}
		shape = new Rectangle(x0, y0, width, height);
		position = new Pointd(x0, y0);
		duration = Constants.rand.nextInt(20) + 15;
	}
	
	@Override
	public void draw(Graphics g) {
		g.drawImage(image.getImage(), (int)position.x, (int)position.y, (int)width, (int)height, null);
	}
	
	@Override
	public Object clone(Game g) {
		double 	x 	= Math.random() * Constants.IMAGE_WIDTH,
				y 	= Math.random() * Constants.IMAGE_HEIGHT;
		return new Banana(x, y);
	}

	@Override
	public boolean effectStep(Snake s) {
		if (duration == 0) {
			s.setRandomTurning(false);
			return true;
		}
		--duration;
		return false;
	}

	@Override
	public void startEffect(Snake s) {
		s.setRandomTurning(true);
	}
	
	@Override
	public ImageIcon getImageIcon() {
		return image;
	}
	
	public String getTextDescription() {
		return "Banana";
	}

}
